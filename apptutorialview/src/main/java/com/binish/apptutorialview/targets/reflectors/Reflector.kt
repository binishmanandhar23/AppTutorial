package com.binish.apptutorial.targets.reflectors

import android.view.View
import android.view.ViewParent


interface Reflector {
    val homeButton: View?

    fun showcaseActionItem(itemId: Int)
    val actionBarView: ViewParent?

    enum class ActionBarType {
        STANDARD, APP_COMPAT, ACTIONBAR_SHERLOCK
    }
}
