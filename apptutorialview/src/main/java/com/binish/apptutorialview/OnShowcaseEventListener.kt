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

import android.view.MotionEvent

/**
 * @author Alex
 */
interface OnShowcaseEventListener {
    /**
     * Called when the ShowcaseView has been told to hide. Use [.onShowcaseViewDidHide]
     * if you want to know when the ShowcaseView has been fully hidden.
     */
    fun onShowcaseViewHide(showcaseView: ShowcaseView?)

    /**
     * Called when the animation hiding the ShowcaseView has finished, and it is no longer visible on the screen.
     */
    fun onShowcaseViewDidHide(showcaseView: ShowcaseView?)

    /**
     * Called when the ShowcaseView is shown.
     */
    fun onShowcaseViewShow(showcaseView: ShowcaseView?)

    /**
     * Called when the user has touched on the ShowcaseView, but the touch was blocked
     * @param motionEvent the blocked event
     */
    fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?)

    companion object {
        /**
         * Empty implementation of OnShowcaseViewEventListener such that null
         * checks aren't needed
         */
        @JvmField
        val NONE: OnShowcaseEventListener = object : OnShowcaseEventListener {
            override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {}
            override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {}
            override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {}
            override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {}
        }
    }
}