package com.binish.apptutorial


import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView

class ActionItemsSampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_action_items)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        try {
            val navigationButtonViewTarget: ViewTarget = ViewTargets.navigationButtonViewTarget(toolbar)
            ShowcaseView.Builder(this)
                .withMaterialShowcase()
                .setTarget(navigationButtonViewTarget)
                .setStyle(R.style.CustomShowcaseTheme2)
                .setContentText("Here's how to highlight items on a toolbar")
                .build()
                .show()
        } catch (e: ViewTargets.MissingViewException) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}