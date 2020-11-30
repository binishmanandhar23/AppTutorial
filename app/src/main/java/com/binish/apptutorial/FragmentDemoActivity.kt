package com.binish.apptutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView
import com.binish.apptutorialview.SimpleShowcaseEventListener

class FragmentDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_demo)
    }

    fun onHiddenFirstShowcase() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_host_two, SecondDemoFragment())
            .commit()
    }

    class FirstDemoFragment : Fragment() {
        private var button: Button? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_layout, container, false)
            button = view.findViewById<View>(R.id.fragment_demo_button) as Button
            return view
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            ShowcaseView.Builder(activity!!)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme)
                .setTarget(ViewTarget(button!!))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_fragment_title)
                .setContentText(R.string.showcase_fragment_message)
                .setShowcaseEventListener(object : SimpleShowcaseEventListener() {
                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
                        (activity as FragmentDemoActivity?)!!.onHiddenFirstShowcase()
                    }
                })
                .build()
        }
    }

    class SecondDemoFragment : Fragment() {
        private var button: Button? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_layout, container, false)
            button = view.findViewById<View>(R.id.fragment_demo_button) as Button
            return view
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            ShowcaseView.Builder(activity!!)
                .withMaterialShowcase()
                .setStyle(R.style.CustomShowcaseTheme2)
                .setTarget(ViewTarget(button!!))
                .hideOnTouchOutside()
                .setContentTitle(R.string.showcase_fragment_title_2)
                .setContentText(R.string.showcase_fragment_message_2)
                .build()
        }
    }
}