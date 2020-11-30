package com.binish.apptutorialview.targets.wrapper

import android.util.Log
import android.view.View
import android.view.ViewParent
import java.lang.reflect.Field
import java.util.*


/**
 * Class which wraps round the many implementations of ActionBarView and allows finding of Action
 * items
 */
class ActionBarViewWrapper(actionBarView: ViewParent) {
    private val mActionBarView: ViewParent
    private val mActionBarViewClass: Class<*>
    private val mAbsActionBarViewClass: Class<*>?

    /**
     * Return the view which represents the spinner on the ActionBar, or null if there isn't one
     */
    val spinnerView: View?
        get() {
            try {
                val spinnerField = mActionBarViewClass.getDeclaredField("mSpinner")
                spinnerField.isAccessible = true
                return spinnerField[mActionBarView] as View
            } catch (e: NoSuchFieldException) {
                Log.e("TAG", "Failed to find actionbar spinner", e)
            } catch (e: IllegalAccessException) {
                Log.e("TAG", "Failed to access actionbar spinner", e)
            }
            return null
        }

    /**
     * Return the view which represents the title on the ActionBar, or null if there isn't one
     */
    val titleView: View?
        get() {
            try {
                val mTitleViewField = mActionBarViewClass.getDeclaredField("mTitleView")
                mTitleViewField.isAccessible = true
                return mTitleViewField[mActionBarView] as View
            } catch (e: NoSuchFieldException) {
                Log.e("TAG", "Failed to find actionbar title", e)
            } catch (e: IllegalAccessException) {
                Log.e("TAG", "Failed to access actionbar title", e)
            }
            return null
        }

    /**
     * Return the view which represents the overflow action item on the ActionBar, or null if there isn't one
     */
    val overflowView: View?
        get() {
            try {
                val actionMenuPresenterField =
                    mAbsActionBarViewClass!!.getDeclaredField("mActionMenuPresenter")
                actionMenuPresenterField.isAccessible = true
                val actionMenuPresenter = actionMenuPresenterField[mActionBarView]
                val overflowButtonField =
                    actionMenuPresenter.javaClass.getDeclaredField("mOverflowButton")
                overflowButtonField.isAccessible = true
                return overflowButtonField[actionMenuPresenter] as View
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
            return null
        }

    /**
     * Return the view which represents the MediaRouterButton action item on the ActionBar, or null
     * if there isn't one
     */
    val mediaRouterButtonView: View?
        get() {
            try {
                val actionMenuPresenterField = mActionBarViewClass.getDeclaredField("mOptionsMenu")
                actionMenuPresenterField.isAccessible = true
                val optionsMenu = actionMenuPresenterField[mActionBarView]
                val actionItemsField = optionsMenu.javaClass.getDeclaredField("mActionItems")
                actionItemsField.isAccessible = true
                val actionItems: List<*> = actionItemsField[optionsMenu] as ArrayList<*>
                if (null != actionItems) {
                    for (obj in actionItems) {
                        println(obj)
                        val view = getMediaRouteButton(obj!!)
                        if (null != view) {
                            return view as View?
                        }
                    }
                }
                return null
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
            return null
        }

    private fun getMediaRouteButton(obj: Any): Any? {
        try {
            val f = obj.javaClass.getDeclaredField("mActionView")
            f.isAccessible = true
            val view = f[obj]
            if ("android.support.v7.app.MediaRouteButton" == view.javaClass.name) {
                return view
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    fun getActionItem(actionItemId: Int): View? {
        try {
            val actionMenuPresenterField =
                mAbsActionBarViewClass!!.getDeclaredField("mActionMenuPresenter")
            actionMenuPresenterField.isAccessible = true
            val actionMenuPresenter = actionMenuPresenterField[mActionBarView]
            val menuViewField = actionMenuPresenter.javaClass.superclass!!
                .getDeclaredField("mMenuView")
            menuViewField.isAccessible = true
            val menuView = menuViewField[actionMenuPresenter]
            val mChField: Field
            mChField = when {
                menuView.javaClass.toString().contains("com.actionbarsherlock") -> {
                    // There are thousands of superclasses to traverse up
                    // Have to get superclasses because mChildren is private
                    menuView.javaClass.superclass!!.superclass
                        ?.superclass!!.superclass!!.getDeclaredField("mChildren")
                }
                menuView.javaClass.toString().contains("android.support.v7") -> {
                    menuView.javaClass.superclass!!.superclass
                        ?.superclass!!.getDeclaredField("mChildren")
                }
                else -> {
                    menuView.javaClass.superclass!!.superclass!!.getDeclaredField("mChildren")
                }
            }
            mChField.isAccessible = true
            val mChs = mChField[menuView] as Array<Any>
            for (mCh in mChs) {
                if (mCh != null) {
                    val v = mCh as View
                    if (v.id == actionItemId) {
                        return v
                    }
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        return null
    }

    init {
        var actionBarView = actionBarView
        if (!actionBarView.javaClass.name.contains("ActionBarView")) {
            val previousP = actionBarView.javaClass.name
            actionBarView = actionBarView.parent
            val throwP = actionBarView.javaClass.name
            check(actionBarView.javaClass.name.contains("ActionBarView")) {
                "Cannot find ActionBarView for " +
                        "Activity, instead found " + previousP + " and " + throwP
            }
        }
        mActionBarView = actionBarView
        mActionBarViewClass = actionBarView.javaClass
        mAbsActionBarViewClass = actionBarView.javaClass.superclass
    }
}
