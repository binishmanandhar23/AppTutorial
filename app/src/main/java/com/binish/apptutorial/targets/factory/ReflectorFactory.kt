package com.binish.apptutorial.targets.factory

import android.app.Activity
import com.binish.apptutorial.targets.reflectors.Reflector
import com.binish.apptutorial.targets.reflectors.ActionBarReflector
import com.binish.apptutorial.targets.reflectors.AppCompatReflector
import com.binish.apptutorial.targets.reflectors.SherlockReflector


/**
 * Base class which uses reflection to determine how to showcase Action Items and Action Views.
 */
internal object ReflectorFactory {
    fun getReflectorForActivity(activity: Activity): Reflector? {
        return when (searchForActivitySuperClass(activity)) {
            Reflector.ActionBarType.STANDARD -> ActionBarReflector(activity)
            Reflector.ActionBarType.APP_COMPAT -> AppCompatReflector(activity)
            Reflector.ActionBarType.ACTIONBAR_SHERLOCK -> SherlockReflector(activity)
        }
    }

    private fun searchForActivitySuperClass(activity: Activity): Reflector.ActionBarType {
        var currentLevel: Class<*>? = activity.javaClass
        while (currentLevel != Activity::class.java) {
            if (currentLevel!!.simpleName == "SherlockActivity" || currentLevel.simpleName == "SherlockFragmentActivity") {
                return Reflector.ActionBarType.ACTIONBAR_SHERLOCK
            }
            if (currentLevel.simpleName == "ActionBarActivity") {
                return Reflector.ActionBarType.APP_COMPAT
            }
            currentLevel = currentLevel.superclass
        }
        return Reflector.ActionBarType.STANDARD
    }
}
