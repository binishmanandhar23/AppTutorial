package com.binish.apptutorialview

import android.view.MotionEvent

/**
 * Basic implementation of [OnShowcaseEventListener] which does nothing
 * for each event, but can be override for each one.
 */
class SimpleShowcaseEventListener : OnShowcaseEventListener {
    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
        // Override to do stuff
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
        // Override to do stuff
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {
        // Override to do stuff
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
        // Override to do stuff
    }
}