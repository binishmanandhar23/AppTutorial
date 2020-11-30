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

import android.graphics.Rect
import android.util.Log

/**
 * Class responsible for calculating where the Showcase should position itself
 */
class ShowcaseAreaCalculator {
    val showcaseRect = Rect()

    /**
     * Creates a [Rect] which represents the area the showcase covers. Used
     * to calculate where best to place the text
     *
     * @return true if voidedArea has changed, false otherwise.
     */
    fun calculateShowcaseRect(x: Float, y: Float, showcaseDrawer: ShowcaseDrawer): Boolean {
        val cx = x.toInt()
        val cy = y.toInt()
        val dw = showcaseDrawer.showcaseWidth
        val dh = showcaseDrawer.showcaseHeight
        if (showcaseRect.left == cx - dw / 2 && showcaseRect.top == cy - dh / 2) {
            return false
        }
        Log.d("ShowcaseView", "Recalculated")
        showcaseRect.left = cx - dw / 2
        showcaseRect.top = cy - dh / 2
        showcaseRect.right = cx + dw / 2
        showcaseRect.bottom = cy + dh / 2
        return true
    }
}