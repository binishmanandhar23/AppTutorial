package com.binish.apptutorial

import android.view.MotionEvent
import com.binish.apptutorialview.OnShowcaseEventListener
import com.binish.apptutorialview.ShowcaseView
import java.util.*
import kotlin.collections.ArrayList

class MultiEventListener(private val listeners: List<OnShowcaseEventListener>) : OnShowcaseEventListener {
    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
        for (listener in listeners) {
            listener.onShowcaseViewHide(showcaseView)
        }
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
        for (listener in listeners) {
            listener.onShowcaseViewDidHide(showcaseView)
        }
    }

    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {
        for (listener in listeners) {
            listener.onShowcaseViewShow(showcaseView)
        }
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
        for (listener in listeners) {
            listener.onShowcaseViewTouchBlocked(motionEvent)
        }
    }
}