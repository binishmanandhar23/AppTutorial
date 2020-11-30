package com.binish.apptutorialview

import android.content.res.Resources
import android.graphics.*

class MaterialShowcaseDrawer(resources: Resources) : ShowcaseDrawer {
    private var radius: Float = 0f
    private val basicPaint: Paint
    private val eraserPaint: Paint = Paint()
    private var backgroundColor = 0

    override val showcaseHeight: Int
        get() = (radius * 2).toInt()

    override val showcaseWidth: Int
        get() = (radius * 2).toInt()

    override fun drawShowcase(buffer: Bitmap?, x: Float, y: Float, scaleMultiplier: Float) {
        val bufferCanvas = Canvas(buffer!!)
        bufferCanvas.drawCircle(x, y, radius, eraserPaint)
    }

    override val blockedRadius: Float
        get() = radius

    override fun setShowcaseColour(color: Int) {
        // no-op
    }

    override fun setBackgroundColour(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
    }

    override fun erase(bitmapBuffer: Bitmap?) {
        bitmapBuffer?.eraseColor(backgroundColor)
    }

    override fun drawToCanvas(canvas: Canvas?, bitmapBuffer: Bitmap?) {
        canvas?.drawBitmap(bitmapBuffer!!, 0f, 0f, basicPaint)
    }

    init {
        radius = resources.getDimension(R.dimen.showcase_radius_material)
        eraserPaint.color = 0xFFFFFF
        eraserPaint.alpha = 0
        eraserPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        eraserPaint.isAntiAlias = true
        basicPaint = Paint()
    }
}