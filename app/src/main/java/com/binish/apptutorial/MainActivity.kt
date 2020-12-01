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
package com.binish.apptutorial

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.binish.apptutorial.animations.AnimationSampleActivity
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.OnShowcaseEventListener
import com.binish.apptutorialview.ShowcaseView

class MainActivity : AppCompatActivity(), View.OnClickListener, OnShowcaseEventListener,
    OnItemClickListener {
    var sv: ShowcaseView? = null
    var buttonBlocked: Button? = null
    var listView: ListView? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.main)
//        setSupportActionBar(findViewById(R.id.toolbar))
        val adapter = HardcodedListAdapter(this)
        listView = findViewById<View>(R.id.listView) as ListView
        listView!!.adapter = adapter
        listView!!.onItemClickListener = this
        buttonBlocked = findViewById<View>(R.id.buttonBlocked) as Button
        buttonBlocked!!.setOnClickListener(this)
        val lps = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        val margin = ((resources.displayMetrics.density * 12) as Number).toInt()
        lps.setMargins(margin, margin, margin, margin)
        val target = ViewTarget(R.id.buttonBlocked, this)
        sv = ShowcaseView.Builder(this)
            .withMaterialShowcase()
            .setTarget(target)
            .setContentTitle(R.string.showcase_main_title)
            .setContentText(R.string.showcase_main_message)
            .setStyle(R.style.CustomShowcaseTheme2)
            .setShowcaseEventListener(this)
            .replaceEndButton(R.layout.view_custom_button)
            .build()
        sv!!.setButtonPosition(lps)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun dimView(view: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view!!.alpha = ALPHA_DIM_VALUE
        }
    }

    override fun onClick(view: View) {
        val viewId = view.id
        when (viewId) {
            R.id.buttonBlocked -> if (sv!!.isShown) {
                sv!!.setStyle(R.style.CustomShowcaseTheme)
            } else {
                sv!!.show()
            }
        }
    }

    override fun onShowcaseViewHide(showcaseView: ShowcaseView?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            listView!!.alpha = 1f
        }
        buttonBlocked!!.setText(R.string.button_show)
        //buttonBlocked.setEnabled(false);
    }

    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {}
    override fun onShowcaseViewShow(showcaseView: ShowcaseView?) {
        dimView(listView)
        buttonBlocked!!.setText(R.string.button_hide)
        //buttonBlocked.setEnabled(true);
    }

    override fun onShowcaseViewTouchBlocked(motionEvent: MotionEvent?) {}
    override fun onItemClick(adapterView: AdapterView<*>?, view: View, position: Int, l: Long) {
        startActivity(Intent(this, DemoOption.values()[position].activityClass))
    }

    private class HardcodedListAdapter(context: Context?) : ArrayAdapter<Any?>(
        context!!, R.layout.item_next_thing
    ) {
        override fun getCount(): Int {
            return DemoOption.values().size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView =
                    LayoutInflater.from(context).inflate(R.layout.item_next_thing, parent, false)
            }
            val currentOption = DemoOption.values()[position]
            (convertView!!.findViewById<View>(R.id.textView) as TextView).setText(currentOption.titleRes)
            (convertView.findViewById<View>(R.id.textView2) as TextView).setText(currentOption.summaryRes)
            return convertView
        }
    }

    private enum class DemoOption(
        val titleRes: Int,
        val summaryRes: Int,
        val activityClass: Class<out Activity?>
    ) {
        ACTION_ITEMS(
            R.string.title_action_items,
            R.string.sum_action_items,
            ActionItemsSampleActivity::class.java
        ),
        FRAGMENTS(
            R.string.title_fragments,
            R.string.sum_fragments,
            FragmentDemoActivity::class.java
        ),
        EVENTS(
            R.string.title_events,
            R.string.sum_event,
            EventsActivity::class.java
        ),
        SINGLE_SHOT(
            R.string.title_single_shot,
            R.string.sum_single_shot,
            SingleShotActivity::class.java
        ),
        ANIMATIONS(
            R.string.title_animations,
            R.string.sum_animations,
            AnimationSampleActivity::class.java
        ),
        CUSTOM_TEXT(
            R.string.custom_text,
            R.string.custom_text_summary,
            CustomTextActivity::class.java
        ),
        CUSTOM_SHOWCASE(
            R.string.custom_showcase_title,
            R.string.custom_showcase_summary,
            CustomShowcaseActivity::class.java
        ),
        MEMORY(R.string.title_memory, R.string.sum_memory, MemoryManagementTesting::class.java);
    }

    companion object {
        private const val ALPHA_DIM_VALUE = 0.1f
    }
}