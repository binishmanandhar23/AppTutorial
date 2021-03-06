package com.binish.apptutorial.targets.reflectors

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewParent


/**
 * Created by Alex on 27/10/13.
 */
class AppCompatReflector(private val mActivity: Activity) : Reflector {
    override val actionBarView: ViewParent
        get() = homeButton.parent.parent
    override val homeButton: View
        get() {
            var homeButton = mActivity.findViewById<View>(R.id.home)
            if (homeButton != null) {
                return homeButton
            }
            val homeId = mActivity.resources.getIdentifier("home", "id", mActivity.packageName)
            homeButton = mActivity.findViewById(homeId)
            if (homeButton == null) {
                throw RuntimeException(
                    "insertShowcaseViewWithType cannot be used when the theme " +
                            "has no ActionBar"
                )
            }
            return homeButton
        }

    override fun showcaseActionItem(itemId: Int) {}
}
