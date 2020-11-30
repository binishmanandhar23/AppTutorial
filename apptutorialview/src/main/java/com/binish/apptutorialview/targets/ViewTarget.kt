package com.binish.apptutorial.targets

import android.app.Activity
import android.graphics.Point
import android.view.View


/**
 * Target a view on the screen. This will centre the target on the view.
 */
class ViewTarget : Target {
    private val mView: View

    constructor(view: View) {
        mView = view
    }

    constructor(viewId: Int, activity: Activity) {
        mView = activity.findViewById(viewId)
    }

    override val point: Point
        get() {
            val location = IntArray(2)
            mView.getLocationInWindow(location)
            val x = location[0] + mView.width / 2
            val y = location[1] + mView.height / 2
            return Point(x, y)
        }
}