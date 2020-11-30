package com.binish.apptutorialview

import android.graphics.Point
import android.view.View
import com.binish.apptutorialview.AnimationFactory.AnimationEndListener
import com.binish.apptutorialview.AnimationFactory.AnimationStartListener

class NoAnimationFactory : AnimationFactory {
    override fun fadeInView(target: View?, duration: Long, listener: AnimationStartListener?) {
        listener!!.onAnimationStart()
    }

    override fun fadeOutView(target: View?, duration: Long, listener: AnimationEndListener?) {
        listener!!.onAnimationEnd()
    }

    override fun animateTargetToPoint(showcaseView: ShowcaseView?, point: Point?) {
        showcaseView!!.setShowcasePosition(point!!.x, point.y)
    }
}