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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.graphics.Point
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.binish.apptutorialview.AnimationFactory.AnimationEndListener
import com.binish.apptutorialview.AnimationFactory.AnimationStartListener

@TargetApi(VERSION_CODES.HONEYCOMB)
class AnimatorAnimationFactory : AnimationFactory {
    private val interpolator: AccelerateDecelerateInterpolator = AccelerateDecelerateInterpolator()
    override fun fadeInView(target: View?, duration: Long, listener: AnimationStartListener?) {
        val oa = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE, VISIBLE)
        oa.setDuration(duration).addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animator: Animator) {
                listener!!.onAnimationStart()
            }
        })
        oa.start()
    }

    override fun fadeOutView(target: View?, duration: Long, listener: AnimationEndListener?) {
        val oa = ObjectAnimator.ofFloat(target, ALPHA, INVISIBLE)
        oa.setDuration(duration).addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                listener!!.onAnimationEnd()
            }
        })
        oa.start()
    }

    override fun animateTargetToPoint(showcaseView: ShowcaseView?, point: Point?) {
        val set = AnimatorSet()
        val xAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseX", point!!.x)
        val yAnimator = ObjectAnimator.ofInt(showcaseView, "showcaseY", point.y)
        set.playTogether(xAnimator, yAnimator)
        set.interpolator = interpolator
        set.start()
    }

    companion object {
        private const val ALPHA = "alpha"
        private const val INVISIBLE = 0f
        private const val VISIBLE = 1f
    }

}