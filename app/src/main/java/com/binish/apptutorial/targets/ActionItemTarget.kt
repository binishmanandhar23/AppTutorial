package com.binish.apptutorial.targets

import android.app.Activity
import android.graphics.Point
import android.view.ViewParent
import com.binish.apptutorial.targets.factory.ReflectorFactory
import com.binish.apptutorial.targets.reflectors.Reflector
import com.binish.apptutorial.targets.wrapper.ActionBarViewWrapper


/**
 * Represents an Action item to showcase (e.g., one of the buttons on an ActionBar).
 * To showcase specific action views such as the home button, use [com.github.amlcurran.showcaseview.targets.ActionItemTarget]
 *
 * @see com.github.amlcurran.showcaseview.targets.ActionItemTarget
 */
class ActionItemTarget(private val mActivity: Activity, private val mItemId: Int) : Target {
    var mActionBarWrapper: ActionBarViewWrapper? = null
    override val point: Point
        get() {
            setUp()
            return ViewTarget(mActionBarWrapper!!.getActionItem(mItemId)!!).point
        }

    protected fun setUp() {
        val reflector: Reflector? = ReflectorFactory.getReflectorForActivity(mActivity)
        val p: ViewParent = reflector?.actionBarView!! //ActionBarView
        mActionBarWrapper = ActionBarViewWrapper(p)
    }
}
