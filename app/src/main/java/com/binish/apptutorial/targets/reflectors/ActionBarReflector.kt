package com.binish.apptutorial.targets.reflectors

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewParent


/**
 * Reflector which finds action items in the standard API 11 ActionBar implementation
 */
class ActionBarReflector(private val mActivity: Activity) : Reflector {
    override val actionBarView: ViewParent
        get() = homeButton.parent.parent
    override val homeButton: View
        get() = mActivity.findViewById(R.id.home)
            ?: throw RuntimeException(
                "insertShowcaseViewWithType cannot be used when the theme " +
                        "has no ActionBar"
            )

    override fun showcaseActionItem(itemId: Int) {}
}
