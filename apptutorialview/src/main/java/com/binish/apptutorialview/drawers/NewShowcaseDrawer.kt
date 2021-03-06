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
package com.binish.apptutorialview.drawers

import android.content.res.Resources
import android.content.res.Resources.Theme
import android.graphics.Bitmap
import android.graphics.Canvas
import com.binish.apptutorialview.R

class NewShowcaseDrawer(resources: Resources, theme: Theme?) : StandardShowcaseDrawer(resources, theme) {
    private val outerRadius: Float = resources.getDimension(R.dimen.showcase_radius_outer)
    private val innerRadius: Float = resources.getDimension(R.dimen.showcase_radius_inner)
    override fun setShowcaseColour(color: Int) {
        eraserPaint.color = color
    }

    override fun drawShowcase(buffer: Bitmap?, x: Float, y: Float, scaleMultiplier: Float) {
        val bufferCanvas = Canvas(buffer!!)
        eraserPaint.alpha = ALPHA_60_PERCENT
        bufferCanvas.drawCircle(x, y, outerRadius, eraserPaint)
        eraserPaint.alpha = 0
        bufferCanvas.drawCircle(x, y, innerRadius, eraserPaint)
    }

    override val showcaseWidth: Int
        get() = (outerRadius * 2).toInt()

    override val showcaseHeight: Int
        get() = (outerRadius * 2).toInt()


    override val blockedRadius: Float
        get() = innerRadius

    companion object {
        private const val ALPHA_60_PERCENT = 153
    }

}