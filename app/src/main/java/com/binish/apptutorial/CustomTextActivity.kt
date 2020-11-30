package com.binish.apptutorial

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Layout
import android.text.TextPaint
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView

class CustomTextActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_text)
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = resources.getDimension(R.dimen.abc_text_size_body_1_material)
        paint.isStrikeThruText = true
        paint.color = Color.RED
        paint.typeface = Typeface.createFromAsset(assets, "RobotoSlab-Regular.ttf")
        val title = TextPaint(Paint.ANTI_ALIAS_FLAG)
        title.textSize = resources.getDimension(R.dimen.abc_text_size_headline_material)
        title.isUnderlineText = true
        title.color = Color.YELLOW
        title.typeface = Typeface.createFromAsset(assets, "RobotoSlab-Regular.ttf")
        val showcaseView = ShowcaseView.Builder(this)
            .withNewStyleShowcase()
            .setTarget(ViewTarget(R.id.imageView, this))
            .setContentTextPaint(paint)
            .setContentTitle(R.string.custom_text_painting_title)
            .setContentText(R.string.custom_text_painting_text)
            .setContentTitlePaint(title)
            .build()
        showcaseView.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER)
        showcaseView.setTitleTextAlignment(Layout.Alignment.ALIGN_CENTER)
        showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE)
    }
}