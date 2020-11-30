package com.binish.apptutorial

import android.view.View
import androidx.appcompat.widget.Toolbar
import com.binish.apptutorial.targets.ViewTarget

/**
 * A collection of not-officially supported ViewTargets. Use them at your own risk!
 */
object ViewTargets {
    /**
     * Highlight the navigation button (the Up or Navigation drawer button) in a Toolbar
     * @param toolbar The toolbar to search for the view in
     *
     * @throws MissingViewException when the view couldn't be found. Raise an issue on Github if you get this!
     */
    @Throws(MissingViewException::class)
    fun navigationButtonViewTarget(toolbar: Toolbar?): ViewTarget {
        return try {
            val field = Toolbar::class.java.getDeclaredField("mNavButtonView")
            field.isAccessible = true
            val navigationView = field[toolbar] as View
            ViewTarget(navigationView)
        } catch (e: NoSuchFieldException) {
            throw MissingViewException(e)
        } catch (e: IllegalAccessException) {
            throw MissingViewException(e)
        }
    }

    class MissingViewException(throwable: Throwable?) : Exception(throwable)
}