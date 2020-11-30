/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.binish.apptutorial.animations

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.binish.apptutorial.R
import com.binish.apptutorial.targets.Target.Companion.NONE
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView

/**
 * Created by Alex on 26/10/13.
 */
class AnimationSampleActivity : Activity(), View.OnClickListener {
    private var showcaseView: ShowcaseView? = null
    private var counter = 0
    private var textView1: TextView? = null
    private var textView2: TextView? = null
    private var textView3: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        textView1 = findViewById<View>(R.id.textView) as TextView
        textView2 = findViewById<View>(R.id.textView2) as TextView
        textView3 = findViewById<View>(R.id.textView3) as TextView
        showcaseView = ShowcaseView.Builder(this)
            .setTarget(ViewTarget(findViewById(R.id.textView)))
            .setOnClickListener(this)
            .build()
        showcaseView!!.setButtonText(getString(R.string.next))
    }

    private fun setAlpha(alpha: Float, vararg views: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            for (view in views) {
                view.alpha = alpha
            }
        }
    }

    override fun onClick(v: View) {
        when (counter) {
            0 -> showcaseView!!.setShowcase(ViewTarget(textView2!!), true)
            1 -> showcaseView!!.setShowcase(ViewTarget(textView3!!), true)
            2 -> {
                showcaseView!!.setTarget(NONE)
                showcaseView!!.setContentTitle("Check it out")
                showcaseView!!.setContentText("You don't always need a target to showcase")
                showcaseView!!.setButtonText(getString(R.string.close))
                setAlpha(0.4f, textView1!!, textView2!!, textView3!!)
            }
            3 -> {
                showcaseView!!.hide()
                setAlpha(1.0f, textView1!!, textView2!!, textView3!!)
            }
        }
        counter++
    }
}