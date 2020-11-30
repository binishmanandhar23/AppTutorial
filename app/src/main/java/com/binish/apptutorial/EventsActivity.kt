package com.binish.apptutorial

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.OnShowcaseEventListener
import com.binish.apptutorialview.ShowcaseView
import com.binish.apptutorialview.SimpleShowcaseEventListener

class EventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        val eventLog = findViewById<View>(R.id.events_log) as TextView
        val customButton = layoutInflater.inflate(R.layout.view_custom_button, null) as Button
        val multiEventListener =
            MultiEventListener(
                listOf(
                    LogToTextListener(
                        eventLog
                    ), ShakeButtonListener(customButton)
                )
            )
        ShowcaseView.Builder(this)
            .withMaterialShowcase()
            .setStyle(R.style.CustomShowcaseTheme3)
            .setTarget(ViewTarget(R.id.imageView, this))
            .setContentTitle("Events")
            .setContentText("Listening to ShowcaseView events is easy!")
            .setShowcaseEventListener(multiEventListener)
            .replaceEndButton(customButton)
            .build()
    }

    private class LogToTextListener(private val eventLog: TextView) : OnShowcaseEventListener {
        private val stringBuilder: SpannableStringBuilder = SpannableStringBuilder()
        override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
            append("Showcase hiding")
        }

        private fun append(text: String) {
            stringBuilder.append("\n").append(text)
            eventLog.text = stringBuilder
        }

        override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
            append("Showcase hidden")
        }

        override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {
            append("Showcase shown")
        }

        override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
            append("Touch blocked: x: " + motionEvent?.x + " y: " + motionEvent?.y)
        }

    }

    private inner class ShakeButtonListener(private val button: Button) :
        SimpleShowcaseEventListener() {
        override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {
            val translation = resources.getDimensionPixelOffset(R.dimen.touch_button_wobble)
            ViewCompat.animate(button)
                .translationXBy(translation.toFloat()).interpolator = WobblyInterpolator(3)
        }
    }

    private inner class WobblyInterpolator(private val cycles: Int) : Interpolator {
        private val CONVERT_TO_RADS = 2 * Math.PI
        override fun getInterpolation(proportion: Float): Float {
            val sin = Math.sin(cycles * proportion * CONVERT_TO_RADS)
            return sin.toFloat()
        }
    }
}