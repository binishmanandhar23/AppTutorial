package com.binish.apptutorial

import android.app.Activity
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView
import com.binish.apptutorialview.drawers.ShowcaseDrawer

class CustomShowcaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_showcase)
        ShowcaseView.Builder(this)
            .setTarget(ViewTarget(R.id.imageView, this))
            .setContentTitle(R.string.custom_text_painting_title)
            .setContentText(R.string.custom_text_painting_text)
            .setShowcaseDrawer(CustomShowcaseView(resources))
            .build()
    }

    private class CustomShowcaseView(resources: Resources) : ShowcaseDrawer {
        override val blockedRadius: Float
        private val height: Float
        private val eraserPaint: Paint
        private val basicPaint: Paint
        private val eraseColour: Int
        private val renderRect: RectF
        override fun setShowcaseColour(color: Int) {
            eraserPaint.color = color
        }

        override fun drawShowcase(buffer: Bitmap?, x: Float, y: Float, scaleMultiplier: Float) {
            val bufferCanvas = Canvas(buffer!!)
            renderRect.left = x - blockedRadius / 2f
            renderRect.right = x + blockedRadius / 2f
            renderRect.top = y - height / 2f
            renderRect.bottom = y + height / 2f
            bufferCanvas.drawRect(renderRect, eraserPaint)
        }

        override val showcaseWidth: Int
            get() = blockedRadius.toInt()
        override val showcaseHeight: Int
            get() = height.toInt()

        override fun setBackgroundColour(backgroundColor: Int) {
            // No-op, remove this from the API?
        }

        override fun erase(bitmapBuffer: Bitmap?) {
            bitmapBuffer!!.eraseColor(eraseColour)
        }

        override fun drawToCanvas(canvas: Canvas?, bitmapBuffer: Bitmap?) {
            canvas!!.drawBitmap(bitmapBuffer!!, 0f, 0f, basicPaint)
        }

        init {
            blockedRadius = resources.getDimension(R.dimen.custom_showcase_width)
            height = resources.getDimension(R.dimen.custom_showcase_height)
            val xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            eraserPaint = Paint()
            eraserPaint.color = 0xFFFFFF
            eraserPaint.alpha = 0
            eraserPaint.xfermode = xfermode
            eraserPaint.isAntiAlias = true
            eraseColour = resources.getColor(R.color.custom_showcase_bg)
            basicPaint = Paint()
            renderRect = RectF()
        }
    }
}