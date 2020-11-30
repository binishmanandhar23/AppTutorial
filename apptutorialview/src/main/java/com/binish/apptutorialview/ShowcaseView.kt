/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.binish.apptutorialview

import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.Layout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import com.binish.apptutorial.targets.Target
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A view which allows you to showcase areas of your app with an explanation.
 */
class ShowcaseView protected constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    newStyle: Boolean
) : RelativeLayout(context, attrs, defStyle), View.OnTouchListener, ShowcaseViewApi {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(*[UNDEFINED, LEFT_OF_SHOWCASE, RIGHT_OF_SHOWCASE, ABOVE_SHOWCASE, BELOW_SHOWCASE])
    annotation class TextPosition

    private var mEndButton: Button?
    private val textDrawer: TextDrawer
    private var showcaseDrawer: ShowcaseDrawer? = null
    private val showcaseAreaCalculator: ShowcaseAreaCalculator
    private var animationFactory: AnimationFactory? = null
    private val shotStateStore: ShotStateStore

    // Showcase metrics
    private var showcaseX = -1
    private var showcaseY = -1
    private var scaleMultiplier = 1f

    // Touch items
    private var hasCustomClickListener = false
    private var blockTouches = true
    private var hideOnTouch = false
    private var mEventListener = OnShowcaseEventListener.NONE
    private var hasAlteredText = false
    private var hasNoTarget = false
    private var shouldCentreText = false
    private var bitmapBuffer: Bitmap? = null

    // Animation items
    private var fadeInMillis: Long
    private var fadeOutMillis: Long
    override var isShowing = false
    private var backgroundColorShowCase = 0
    private var showcaseColor = 0
    private var blockAllTouches = false
    private val positionInWindow = IntArray(2)

    constructor(context: Context, newStyle: Boolean) : this(
        context,
        null,
        R.styleable.CustomTheme_showcaseViewStyle,
        newStyle
    )

    private fun init() {
        setOnTouchListener(this)
        if (mEndButton!!.parent == null) {
            val margin = resources.getDimension(R.dimen.button_margin) as Int
            val lps: RelativeLayout.LayoutParams =
                generateDefaultLayoutParams() as RelativeLayout.LayoutParams
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            lps.setMargins(margin, margin, margin, margin)
            mEndButton!!.layoutParams = lps
            mEndButton!!.setText(android.R.string.ok)
            if (!hasCustomClickListener) {
                mEndButton!!.setOnClickListener(hideOnClickListener)
            }
            addView(mEndButton)
        }
    }

    private fun hasShot(): Boolean {
        return shotStateStore.hasShot()
    }

    fun setShowcasePosition(point: Point) {
        setShowcasePosition(point.x, point.y)
    }

    fun setShowcasePosition(x: Int, y: Int) {
        if (shotStateStore.hasShot()) {
            return
        }
        getLocationInWindow(positionInWindow)
        showcaseX = x - positionInWindow[0]
        showcaseY = y - positionInWindow[1]
        //init();
        recalculateText()
        invalidate()
    }

    fun setTarget(target: com.binish.apptutorial.targets.Target) {
        setShowcase(target, false)
    }

    fun setShowcase(target: com.binish.apptutorial.targets.Target, animate: Boolean) {
        postDelayed(Runnable {
            if (!shotStateStore.hasShot()) {
                if (canUpdateBitmap()) {
                    updateBitmap()
                }
                val targetPoint: Point? = target.point
                if (targetPoint != null) {
                    hasNoTarget = false
                    if (animate) {
                        animationFactory!!.animateTargetToPoint(this@ShowcaseView, targetPoint)
                    } else {
                        setShowcasePosition(targetPoint)
                    }
                } else {
                    hasNoTarget = true
                    invalidate()
                }
            }
        }, 100)
    }

    private fun updateBitmap() {
        if (bitmapBuffer == null || haveBoundsChanged()) {
            if (bitmapBuffer != null) {
                bitmapBuffer?.recycle()
            }
            bitmapBuffer = Bitmap.createBitmap(
                measuredWidth,
                measuredHeight,
                Bitmap.Config.ARGB_8888
            )
        }
    }

    private fun haveBoundsChanged(): Boolean {
        return measuredWidth != bitmapBuffer?.width ||
                measuredHeight != bitmapBuffer?.height
    }

    fun hasShowcaseView(): Boolean {
        return showcaseX != 1000000 && showcaseY != 1000000 && !hasNoTarget
    }

    fun setShowcaseX(x: Int) {
        setShowcasePosition(x, getShowcaseY())
    }

    fun setShowcaseY(y: Int) {
        setShowcasePosition(getShowcaseX(), y)
    }

    fun getShowcaseX(): Int {
        getLocationInWindow(positionInWindow)
        return showcaseX + positionInWindow[0]
    }

    fun getShowcaseY(): Int {
        getLocationInWindow(positionInWindow)
        return showcaseY + positionInWindow[1]
    }

    /**
     * Override the standard button click event
     *
     * @param listener Listener to listen to on click events
     */
    fun overrideButtonClick(listener: OnClickListener?) {
        if (shotStateStore.hasShot()) {
            return
        }
        if (mEndButton != null) {
            if (listener != null) {
                mEndButton!!.setOnClickListener(listener)
            } else {
                mEndButton!!.setOnClickListener(hideOnClickListener)
            }
        }
        hasCustomClickListener = true
    }

    fun setOnShowcaseEventListener(listener: OnShowcaseEventListener?) {
        mEventListener = listener ?: OnShowcaseEventListener.NONE
    }

    fun setButtonText(text: CharSequence?) {
        if (mEndButton != null) {
            mEndButton!!.text = text
        }
    }

    private fun recalculateText() {
        val recalculatedCling = showcaseAreaCalculator.calculateShowcaseRect(
            showcaseX.toFloat(),
            showcaseY.toFloat(),
            showcaseDrawer!!
        )
        val recalculateText = recalculatedCling || hasAlteredText
        if (recalculateText) {
            val rect = if (hasShowcaseView()) showcaseAreaCalculator.showcaseRect else Rect()
            textDrawer.calculateTextPosition(
                measuredWidth,
                measuredHeight,
                shouldCentreText,
                rect
            )
        }
        hasAlteredText = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (showcaseX < 0 || showcaseY < 0 || shotStateStore.hasShot() || bitmapBuffer == null) {
            super.dispatchDraw(canvas)
            return
        }

        //Draw background color
        showcaseDrawer!!.erase(bitmapBuffer)

        // Draw the showcase drawable
        if (!hasNoTarget) {
            showcaseDrawer!!.drawShowcase(
                bitmapBuffer,
                showcaseX.toFloat(),
                showcaseY.toFloat(),
                scaleMultiplier
            )
            showcaseDrawer!!.drawToCanvas(canvas, bitmapBuffer)
        }

        // Draw the text on the screen, recalculating its position if necessary
        textDrawer.draw(canvas)
        super.dispatchDraw(canvas)
    }

    override fun hide() {
        // If the type is set to one-shot, store that it has shot
        shotStateStore.storeShot()
        mEventListener.onShowcaseViewHide(this)
        fadeOutShowcase()
    }

    private fun clearBitmap() {
        if (bitmapBuffer != null && !bitmapBuffer!!.isRecycled) {
            bitmapBuffer?.recycle()
            bitmapBuffer = null
        }
    }

    private fun fadeOutShowcase() {
        animationFactory!!.fadeOutView(
            this, fadeOutMillis, object : AnimationFactory.AnimationEndListener {
                override fun onAnimationEnd() {
                    visibility = View.GONE
                    clearBitmap()
                    isShowing = false
                    mEventListener.onShowcaseViewDidHide(this@ShowcaseView)
                }
            }
        )
    }

    override fun show() {
        isShowing = true
        if (canUpdateBitmap()) {
            updateBitmap()
        }
        mEventListener.onShowcaseViewShow(this)
        fadeInShowcase()
    }

    private fun canUpdateBitmap(): Boolean {
        return measuredHeight > 0 && measuredWidth > 0
    }

    private fun fadeInShowcase() {
        animationFactory!!.fadeInView(
            this, fadeInMillis,
            object : AnimationFactory.AnimationStartListener {
                override fun onAnimationStart() {
                    visibility = View.VISIBLE
                }
            }
        )
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (blockAllTouches) {
            mEventListener.onShowcaseViewTouchBlocked(motionEvent)
            return true
        }
        val xDelta: Float = Math.abs(motionEvent.rawX - showcaseX)
        val yDelta: Float = Math.abs(motionEvent.rawY - showcaseY)
        val distanceFromFocus =
            sqrt(xDelta.toDouble().pow(2.0) + yDelta.toDouble().pow(2.0))
        if (MotionEvent.ACTION_UP == motionEvent.action &&
            hideOnTouch && distanceFromFocus > showcaseDrawer!!.blockedRadius
        ) {
            hide()
            return true
        }
        val blocked = blockTouches && distanceFromFocus > showcaseDrawer!!.blockedRadius
        if (blocked) {
            mEventListener.onShowcaseViewTouchBlocked(motionEvent)
        }
        return blocked
    }

    private fun hideImmediate() {
        isShowing = false
        visibility = View.GONE
    }

    override fun setContentTitle(title: CharSequence?) {
        textDrawer.setContentTitle(title)
        invalidate()
    }

    override fun setContentText(text: CharSequence?) {
        textDrawer.setContentText(text)
        invalidate()
    }

    private fun setScaleMultiplier(scaleMultiplier: Float) {
        this.scaleMultiplier = scaleMultiplier
    }

    fun hideButton() {
        mEndButton!!.visibility = View.GONE
    }

    fun showButton() {
        mEndButton!!.visibility = View.VISIBLE
    }

    /**
     * Builder class which allows easier creation of [ShowcaseView]s.
     * It is recommended that you use this Builder class.
     */
    class Builder @Deprecated(
        """use {@link #withHoloShowcase()}, {@link #withNewStyleShowcase()}, or
          {@link #setShowcaseDrawer(ShowcaseDrawer)}"""
    ) constructor(activity: Activity, useNewStyle: Boolean) {
        private val showcaseView: ShowcaseView
        private val activity: Activity
        private var parent: ViewGroup
        private var parentIndex: Int

        constructor(activity: Activity) : this(activity, false)

        /**
         * Create the [com.github.amlcurran.showcaseview.ShowcaseView] and show it.
         *
         * @return the created ShowcaseView
         */
        fun build(): ShowcaseView {
            insertShowcaseView(showcaseView, parent, parentIndex)
            return showcaseView
        }

        /**
         * Draw a holo-style showcase. This is the default.<br></br>
         * <img alt="Holo showcase example" src="../../../../../../../../example2.png"></img>
         */
        fun withHoloShowcase(): Builder {
            return setShowcaseDrawer(
                StandardShowcaseDrawer(
                    activity.resources,
                    activity.theme
                )
            )
        }

        /**
         * Draw a new-style showcase.<br></br>
         * <img alt="Holo showcase example" src="../../../../../../../../example.png"></img>
         */
        fun withNewStyleShowcase(): Builder {
            return setShowcaseDrawer(
                NewShowcaseDrawer(
                    activity.resources,
                    activity.theme
                )
            )
        }

        /**
         * Draw a material style showcase.
         * <img alt="Material showcase" src="../../../../../../../../material.png"></img>
         */
        fun withMaterialShowcase(): Builder {
            return setShowcaseDrawer(MaterialShowcaseDrawer(activity.resources))
        }

        /**
         * Set a custom showcase drawer which will be responsible for measuring and drawing the showcase
         */
        fun setShowcaseDrawer(showcaseDrawer: ShowcaseDrawer): Builder {
            showcaseView.setShowcaseDrawer(showcaseDrawer)
            return this
        }

        /**
         * Set the title text shown on the ShowcaseView.
         */
        fun setContentTitle(resId: Int): Builder {
            return setContentTitle(activity.getString(resId))
        }

        /**
         * Set the title text shown on the ShowcaseView.
         */
        fun setContentTitle(title: CharSequence): Builder {
            showcaseView.setContentTitle(title)
            return this
        }

        /**
         * Set the descriptive text shown on the ShowcaseView.
         */
        fun setContentText(resId: Int): Builder {
            return setContentText(activity.getString(resId))
        }

        /**
         * Set the descriptive text shown on the ShowcaseView.
         */
        fun setContentText(text: CharSequence): Builder {
            showcaseView.setContentText(text)
            return this
        }

        /**
         * Set the target of the showcase.
         *
         * @param target a [com.github.amlcurran.showcaseview.targets.Target] representing
         * the item to showcase (e.g., a button, or action item).
         */
        fun setTarget(target: Target): Builder {
            showcaseView.setTarget(target)
            return this
        }

        /**
         * Set the style of the ShowcaseView. See the sample app for example styles.
         */
        fun setStyle(theme: Int): Builder {
            showcaseView.setStyle(theme)
            return this
        }

        /**
         * Set a listener which will override the button clicks.
         *
         *
         * Note that you will have to manually hide the ShowcaseView
         */
        fun setOnClickListener(onClickListener: OnClickListener?): Builder {
            showcaseView.overrideButtonClick(onClickListener)
            return this
        }

        /**
         * Don't make the ShowcaseView block touches on itself. This doesn't
         * block touches in the showcased area.
         *
         *
         * By default, the ShowcaseView does block touches
         */
        fun doNotBlockTouches(): Builder {
            showcaseView.setBlocksTouches(false)
            return this
        }

        /**
         * Make this ShowcaseView hide when the user touches outside the showcased area.
         * This enables [.doNotBlockTouches] as well.
         *
         *
         * By default, the ShowcaseView doesn't hide on touch.
         */
        fun hideOnTouchOutside(): Builder {
            showcaseView.setBlocksTouches(true)
            showcaseView.setHideOnTouchOutside(true)
            return this
        }

        /**
         * Set the ShowcaseView to only ever show once.
         *
         * @param shotId a unique identifier (*across the app*) to store
         * whether this ShowcaseView has been shown.
         */
        fun singleShot(shotId: Long): Builder {
            showcaseView.setSingleShot(shotId)
            return this
        }

        fun setShowcaseEventListener(showcaseEventListener: OnShowcaseEventListener?): Builder {
            showcaseView.setOnShowcaseEventListener(showcaseEventListener)
            return this
        }

        fun setParent(parent: ViewGroup, index: Int): Builder {
            this.parent = parent
            parentIndex = index
            return this
        }

        /**
         * Sets the paint that will draw the text as specified by [.setContentText]
         * or [.setContentText]. If you're using a TextAppearance (set by [.setStyle],
         * then this [TextPaint] will override that TextAppearance.
         */
        fun setContentTextPaint(textPaint: TextPaint): Builder {
            showcaseView.setContentTextPaint(textPaint)
            return this
        }

        /**
         * Sets the paint that will draw the text as specified by [.setContentTitle]
         * or [.setContentTitle]. If you're using a TextAppearance (set by [.setStyle],
         * then this [TextPaint] will override that TextAppearance.
         */
        fun setContentTitlePaint(textPaint: TextPaint): Builder {
            showcaseView.setContentTitlePaint(textPaint)
            return this
        }

        /**
         * Replace the end button with the one provided. Note that this resets any OnClickListener provided
         * by [.setOnClickListener], so call this method before that one.
         */
        fun replaceEndButton(button: Button): Builder {
            showcaseView.setEndButton(button)
            return this
        }

        /**
         * Replace the end button with the one provided. Note that this resets any OnClickListener provided
         * by [.setOnClickListener], so call this method before that one.
         */
        fun replaceEndButton(buttonResourceId: Int): Builder {
            val view: View =
                LayoutInflater.from(activity).inflate(buttonResourceId, showcaseView, false)
            require(view is Button) { "Attempted to replace showcase button with a layout which isn't a button" }
            return replaceEndButton(view)
        }

        /**
         * Block any touch made on the ShowcaseView, even inside the showcase
         */
        fun blockAllTouches(): Builder {
            showcaseView.setBlockAllTouches(true)
            return this
        }

        /**
         * Uses the android decor view to insert a showcase, this is not recommended
         * as then UI elements in showcase view can hide behind the nav bar
         */
        fun useDecorViewAsParent(): Builder {
            parent = activity.window.decorView as ViewGroup
            parentIndex = -1
            return this
        }

        /**
         * @param useNewStyle should use "new style" showcase (see [.withNewStyleShowcase]
         */
        init {
            this.activity = activity
            showcaseView = ShowcaseView(activity, useNewStyle)
            showcaseView.setTarget(com.binish.apptutorial.targets.Target.NONE)
            parent = activity.findViewById<View>(android.R.id.content) as ViewGroup
            parentIndex = parent.childCount
        }
    }

    private fun setEndButton(button: Button) {
        val copyParams: RelativeLayout.LayoutParams =
            mEndButton!!.layoutParams as RelativeLayout.LayoutParams
        mEndButton!!.setOnClickListener(null)
        removeView(mEndButton)
        mEndButton = button
        button.setOnClickListener(hideOnClickListener)
        button.layoutParams = copyParams
        addView(button)
    }

    private fun setShowcaseDrawer(showcaseDrawer: ShowcaseDrawer) {
        this.showcaseDrawer = showcaseDrawer
        this.showcaseDrawer!!.setBackgroundColour(backgroundColorShowCase)
        this.showcaseDrawer!!.setShowcaseColour(showcaseColor)
        hasAlteredText = true
        invalidate()
    }

    private fun setContentTitlePaint(textPaint: TextPaint) {
        textDrawer.setTitlePaint(textPaint)
        hasAlteredText = true
        invalidate()
    }

    private fun setContentTextPaint(paint: TextPaint) {
        textDrawer.setContentPaint(paint)
        hasAlteredText = true
        invalidate()
    }

    /**
     * Set whether the text should be centred in the screen, or left-aligned (which is the default).
     */
    fun setShouldCentreText(shouldCentreText: Boolean) {
        this.shouldCentreText = shouldCentreText
        hasAlteredText = true
        invalidate()
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder.setSingleShot
     */
    private fun setSingleShot(shotId: Long) {
        shotStateStore.setSingleShot(shotId)
    }

    /**
     * Change the position of the ShowcaseView's button from the default bottom-right position.
     *
     * @param layoutParams a [LayoutParams] representing
     * the new position of the button
     */
    override fun setButtonPosition(layoutParams: RelativeLayout.LayoutParams?) {
        mEndButton!!.layoutParams = layoutParams
    }

    /**
     * Sets the text alignment of the detail text
     */
    fun setDetailTextAlignment(textAlignment: Layout.Alignment?) {
        textDrawer.setDetailTextAlignment(textAlignment!!)
        hasAlteredText = true
        invalidate()
    }

    /**
     * Sets the text alignment of the title text
     */
    fun setTitleTextAlignment(textAlignment: Layout.Alignment?) {
        textDrawer.setTitleTextAlignment(textAlignment!!)
        hasAlteredText = true
        invalidate()
    }

    /**
     * Set the duration of the fading in and fading out of the ShowcaseView
     */
    fun setFadeDurations(fadeInMillis: Long, fadeOutMillis: Long) {
        this.fadeInMillis = fadeInMillis
        this.fadeOutMillis = fadeOutMillis
    }

    fun forceTextPosition(@TextPosition textPosition: Int) {
        textDrawer.forceTextPosition(textPosition)
        hasAlteredText = true
        invalidate()
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder.hideOnTouchOutside
     */
    override fun setHideOnTouchOutside(hideOnTouch: Boolean) {
        this.hideOnTouch = hideOnTouch
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder.doNotBlockTouches
     */
    override fun setBlocksTouches(blockTouches: Boolean) {
        this.blockTouches = blockTouches
    }

    private fun setBlockAllTouches(blockAllTouches: Boolean) {
        this.blockAllTouches = blockAllTouches
    }

    /**
     * @see com.github.amlcurran.showcaseview.ShowcaseView.Builder.setStyle
     */
    override fun setStyle(theme: Int) {
        val array: TypedArray = context.obtainStyledAttributes(theme, R.styleable.ShowcaseView)
        updateStyle(array, true)
    }

    override fun isShown(): Boolean {
        return isShowing
    }

    private fun updateStyle(styled: TypedArray, invalidate: Boolean) {
        backgroundColorShowCase = styled.getColor(
            R.styleable.ShowcaseView_sv_backgroundColor,
            Color.argb(128, 80, 80, 80)
        )
        showcaseColor = styled.getColor(R.styleable.ShowcaseView_sv_showcaseColor, HOLO_BLUE)
        var buttonText: String = styled.getString(R.styleable.ShowcaseView_sv_buttonText)!!
        if (TextUtils.isEmpty(buttonText)) {
            buttonText = resources.getString(android.R.string.ok)
        }
        val tintButton: Boolean =
            styled.getBoolean(R.styleable.ShowcaseView_sv_tintButtonColor, true)
        val titleTextAppearance: Int = styled.getResourceId(
            R.styleable.ShowcaseView_sv_titleTextAppearance,
            R.style.TextAppearance_ShowcaseView_Title
        )
        val detailTextAppearance: Int = styled.getResourceId(
            R.styleable.ShowcaseView_sv_detailTextAppearance,
            R.style.TextAppearance_ShowcaseView_Detail
        )
        styled.recycle()
        showcaseDrawer!!.setShowcaseColour(showcaseColor)
        showcaseDrawer!!.setBackgroundColour(backgroundColorShowCase)
        tintButton(showcaseColor, tintButton)
        mEndButton!!.text = buttonText
        textDrawer.setTitleStyling(titleTextAppearance)
        textDrawer.setDetailStyling(detailTextAppearance)
        hasAlteredText = true
        if (invalidate) {
            invalidate()
        }
    }

    private fun tintButton(showcaseColor: Int, tintButton: Boolean) {
        if (tintButton) {
            mEndButton!!.background.setColorFilter(showcaseColor, PorterDuff.Mode.MULTIPLY)
        } else {
            mEndButton!!.background.setColorFilter(HOLO_BLUE, PorterDuff.Mode.MULTIPLY)
        }
    }

    private val hideOnClickListener = OnClickListener { hide() }

    companion object {
        private val HOLO_BLUE = Color.parseColor("#33B5E5")
        const val UNDEFINED = -1
        const val LEFT_OF_SHOWCASE = 0
        const val RIGHT_OF_SHOWCASE = 2
        const val ABOVE_SHOWCASE = 1
        const val BELOW_SHOWCASE = 3
        private fun insertShowcaseView(
            showcaseView: ShowcaseView,
            parent: ViewGroup,
            parentIndex: Int
        ) {
            parent.addView(showcaseView, parentIndex)
            if (!showcaseView.hasShot()) {
                showcaseView.show()
            } else {
                showcaseView.hideImmediate()
            }
        }
    }

    init {
        val apiUtils = ApiUtils()
        animationFactory = if (apiUtils.isCompatWithHoneycomb) {
            AnimatorAnimationFactory()
        } else {
            NoAnimationFactory()
        }
        showcaseAreaCalculator = ShowcaseAreaCalculator()
        shotStateStore = ShotStateStore(context)

        // Get the attributes for the ShowcaseView
        val styled: TypedArray = context.theme
            .obtainStyledAttributes(
                attrs, R.styleable.ShowcaseView, R.attr.showcaseViewStyle,
                R.style.ShowcaseView
            )

        // Set the default animation times
        fadeInMillis = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        fadeOutMillis = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        mEndButton = LayoutInflater.from(context).inflate(R.layout.showcase_button, null) as Button
        showcaseDrawer = if (newStyle) {
            NewShowcaseDrawer(resources, context.theme)
        } else {
            StandardShowcaseDrawer(resources, context.theme)
        }
        textDrawer = TextDrawer(resources, getContext())
        updateStyle(styled, false)
        init()
    }
}