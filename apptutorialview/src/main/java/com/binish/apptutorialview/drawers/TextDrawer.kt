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

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.text.*
import android.text.style.MetricAffectingSpan
import android.text.style.TextAppearanceSpan
import com.binish.apptutorialview.ShowcaseView.TextPosition

/**
 * Draws the text as required by the ShowcaseView
 */
internal class TextDrawer(resources: Resources, context: Context) {
    private val titlePaint: TextPaint = TextPaint()
    private val textPaint: TextPaint
    private val context: Context = context
    private val padding: Float = resources.getDimension(R.dimen.text_padding)
    private val actionBarOffset: Float = resources.getDimension(R.dimen.action_bar_offset)
    private var textAlignment = Layout.Alignment.ALIGN_NORMAL
    private var textString: SpannableString? = null
    private var textLayout: DynamicLayout? = null
    private var textSpan: MetricAffectingSpan? = null
    private var titleAlignment = Layout.Alignment.ALIGN_NORMAL
    private var titleString: SpannableString? = null
    private var titleLayout: DynamicLayout? = null
    private var titleSpan: MetricAffectingSpan? = null
    private val bestTextPosition = FloatArray(3)
    private var hasRecalculated = false

    @TextPosition
    private var forcedTextPosition = ShowcaseView.UNDEFINED
    fun draw(canvas: Canvas) {
        if (shouldDrawText()) {
            val textPosition = bestTextPosition
            val width = Math.max(
                0, bestTextPosition[INDEX_TEXT_WIDTH]
                    .toInt()
            )
            if (!TextUtils.isEmpty(titleString)) {
                canvas.save()
                if (hasRecalculated) {
                    titleLayout = DynamicLayout(
                        titleString!!, titlePaint,
                        width, titleAlignment, 1.0f, 1.0f, true
                    )
                }
                if (titleLayout != null) {
                    canvas.translate(
                        textPosition[INDEX_TEXT_START_X],
                        textPosition[INDEX_TEXT_START_Y]
                    )
                    titleLayout!!.draw(canvas)
                    canvas.restore()
                }
            }
            if (!TextUtils.isEmpty(textString)) {
                canvas.save()
                if (hasRecalculated) {
                    textLayout = DynamicLayout(
                        textString!!, textPaint,
                        width, textAlignment, 1.2f, 1.0f, true
                    )
                }
                val offsetForTitle = if (titleLayout != null) titleLayout!!.height
                    .toFloat() else 0.toFloat()
                if (textLayout != null) {
                    canvas.translate(
                        textPosition[INDEX_TEXT_START_X],
                        textPosition[INDEX_TEXT_START_Y] + offsetForTitle
                    )
                    textLayout!!.draw(canvas)
                    canvas.restore()
                }
            }
        }
        hasRecalculated = false
    }

    fun setContentText(details: CharSequence?) {
        if (details != null) {
            val ssbDetail = SpannableString(details)
            ssbDetail.setSpan(textSpan, 0, ssbDetail.length, 0)
            textString = ssbDetail
            hasRecalculated = true
        }
    }

    fun setContentTitle(title: CharSequence?) {
        if (title != null) {
            val ssbTitle = SpannableString(title)
            ssbTitle.setSpan(titleSpan, 0, ssbTitle.length, 0)
            titleString = ssbTitle
            hasRecalculated = true
        }
    }

    /**
     * Calculates the best place to position text
     * @param canvasW width of the screen
     * @param canvasH height of the screen
     * @param shouldCentreText
     * @param showcase
     */
    fun calculateTextPosition(
        canvasW: Int,
        canvasH: Int,
        shouldCentreText: Boolean,
        showcase: Rect
    ) {
        val areas = IntArray(4) //left, top, right, bottom
        areas[ShowcaseView.LEFT_OF_SHOWCASE] = showcase.left * canvasH
        areas[ShowcaseView.ABOVE_SHOWCASE] = showcase.top * canvasW
        areas[ShowcaseView.RIGHT_OF_SHOWCASE] = (canvasW - showcase.right) * canvasH
        areas[ShowcaseView.BELOW_SHOWCASE] = (canvasH - showcase.bottom) * canvasW
        var largest = 0
        for (i in 1 until areas.size) {
            if (areas[i] > areas[largest]) largest = i
        }
        if (forcedTextPosition != ShowcaseView.UNDEFINED) {
            largest = forcedTextPosition
        }
        when (largest) {
            ShowcaseView.LEFT_OF_SHOWCASE -> {
                bestTextPosition[INDEX_TEXT_START_X] = padding
                bestTextPosition[INDEX_TEXT_START_Y] = padding
                bestTextPosition[INDEX_TEXT_WIDTH] = showcase.left - 2 * padding
            }
            ShowcaseView.ABOVE_SHOWCASE -> {
                bestTextPosition[INDEX_TEXT_START_X] = padding
                bestTextPosition[INDEX_TEXT_START_Y] = padding + actionBarOffset
                bestTextPosition[INDEX_TEXT_WIDTH] = canvasW - 2 * padding
            }
            ShowcaseView.RIGHT_OF_SHOWCASE -> {
                bestTextPosition[INDEX_TEXT_START_X] = showcase.right + padding
                bestTextPosition[INDEX_TEXT_START_Y] = padding
                bestTextPosition[INDEX_TEXT_WIDTH] = canvasW - showcase.right - 2 * padding
            }
            ShowcaseView.BELOW_SHOWCASE -> {
                bestTextPosition[INDEX_TEXT_START_X] = padding
                bestTextPosition[INDEX_TEXT_START_Y] = showcase.bottom + padding
                bestTextPosition[INDEX_TEXT_WIDTH] = canvasW - 2 * padding
            }
        }
        if (shouldCentreText) {
            // Center text vertically or horizontally
            when (largest) {
                ShowcaseView.LEFT_OF_SHOWCASE, ShowcaseView.RIGHT_OF_SHOWCASE -> bestTextPosition[INDEX_TEXT_START_Y] =
                    bestTextPosition[INDEX_TEXT_START_Y] + (canvasH / 4)
                ShowcaseView.ABOVE_SHOWCASE, ShowcaseView.BELOW_SHOWCASE -> {
                    bestTextPosition[INDEX_TEXT_WIDTH] = bestTextPosition[INDEX_TEXT_WIDTH] / 2
                    bestTextPosition[INDEX_TEXT_START_X] =
                        bestTextPosition[INDEX_TEXT_START_X] + (canvasW / 4)
                }
            }
        } else {
            // As text is not centered add actionbar padding if the text is left or right
            when (largest) {
                ShowcaseView.LEFT_OF_SHOWCASE, ShowcaseView.RIGHT_OF_SHOWCASE -> bestTextPosition[INDEX_TEXT_START_Y] += actionBarOffset
            }
        }
        hasRecalculated = true
    }

    fun setTitleStyling(styleId: Int) {
        titleSpan = TextAppearanceSpan(context, styleId)
        setContentTitle(titleString)
    }

    fun setDetailStyling(styleId: Int) {
        textSpan = TextAppearanceSpan(context, styleId)
        setContentText(textString)
    }

    fun shouldDrawText(): Boolean {
        return !TextUtils.isEmpty(titleString) || !TextUtils.isEmpty(textString)
    }

    fun setContentPaint(contentPaint: TextPaint?) {
        textPaint.set(contentPaint)
        if (textString != null) {
            textString!!.removeSpan(textSpan)
        }
        textSpan = NoOpSpan()
        setContentText(textString)
    }

    fun setTitlePaint(textPaint: TextPaint?) {
        titlePaint.set(textPaint)
        if (titleString != null) {
            titleString!!.removeSpan(titleSpan)
        }
        titleSpan = NoOpSpan()
        setContentTitle(titleString)
    }

    fun setDetailTextAlignment(textAlignment: Layout.Alignment) {
        this.textAlignment = textAlignment
    }

    fun setTitleTextAlignment(titleTextAlignment: Layout.Alignment) {
        titleAlignment = titleTextAlignment
    }

    fun forceTextPosition(@TextPosition textPosition: Int) {
        require(!(textPosition > ShowcaseView.BELOW_SHOWCASE || textPosition < ShowcaseView.UNDEFINED)) { "ShowcaseView text was forced with an invalid position" }
        forcedTextPosition = textPosition
    }

    private class NoOpSpan : MetricAffectingSpan() {
        override fun updateDrawState(tp: TextPaint) {
            // No-op
        }

        override fun updateMeasureState(p: TextPaint) {
            // No-op
        }
    }

    companion object {
        private const val INDEX_TEXT_START_X = 0
        private const val INDEX_TEXT_START_Y = 1
        private const val INDEX_TEXT_WIDTH = 2
    }

    init {
        titlePaint.isAntiAlias = true
        textPaint = TextPaint()
        textPaint.isAntiAlias = true
    }
}