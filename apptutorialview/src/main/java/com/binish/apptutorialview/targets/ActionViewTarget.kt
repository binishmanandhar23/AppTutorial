package com.binish.apptutorial.targets

import android.app.Activity
import android.graphics.Point
import android.view.ViewParent
import com.binish.apptutorial.targets.factory.ReflectorFactory
import com.binish.apptutorial.targets.reflectors.Reflector
import com.binish.apptutorialview.targets.wrapper.ActionBarViewWrapper


class ActionViewTarget(private val mActivity: Activity, private val mType: Type) : Target {
    var mActionBarWrapper: ActionBarViewWrapper? = null
    var mReflector: Reflector? = null

    private fun setUp() {
        mReflector = ReflectorFactory.getReflectorForActivity(mActivity)
        val p: ViewParent = mReflector?.actionBarView!! //ActionBarView
        mActionBarWrapper = ActionBarViewWrapper(p)
    }

    override val point: Point
        get() {
            var internal: Target? = null
            setUp()
            internal = when (mType) {
                Type.SPINNER -> ViewTarget(mActionBarWrapper?.spinnerView!!)
                Type.HOME -> ViewTarget(mReflector?.homeButton!!)
                Type.OVERFLOW -> ViewTarget(mActionBarWrapper?.overflowView!!)
                Type.TITLE -> ViewTarget(mActionBarWrapper?.titleView!!)
                Type.MEDIA_ROUTE_BUTTON -> ViewTarget(mActionBarWrapper?.mediaRouterButtonView!!)
            }
            return internal.point
        }

    enum class Type {
        SPINNER, HOME, TITLE, OVERFLOW, MEDIA_ROUTE_BUTTON
    }
}
