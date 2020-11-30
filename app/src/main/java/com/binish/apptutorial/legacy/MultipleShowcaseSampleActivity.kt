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
package com.binish.apptutorial.legacy

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.binish.apptutorial.R

class MultipleShowcaseSampleActivity : Activity() {
    //ShowcaseViews mViews;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_legacy)
        findViewById<View>(R.id.buttonLike).setOnClickListener {
            Toast.makeText(
                applicationContext, R.string.like_message, Toast.LENGTH_SHORT
            ).show()
        }

        //mOptions.block = false;
//        mViews = new ShowcaseViews(this,
//                new ShowcaseViews.OnShowcaseAcknowledged() {
//            @Override
//            public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
//                Toast.makeText(MultipleShowcaseSampleActivity.this, R.string.dismissed_message, Toast.LENGTH_SHORT).show();
//            }
//        });
//        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.image,
//                R.string.showcase_image_title,
//                R.string.showcase_image_message,
//                SHOWCASE_KITTEN_SCALE));
//        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.buttonLike,
//                R.string.showcase_like_title,
//                R.string.showcase_like_message,
//                SHOWCASE_LIKE_SCALE));
//        mViews.show();
        enableUp()
    }

    private fun enableUp() {
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val SHOWCASE_KITTEN_SCALE = 1.2f
        private const val SHOWCASE_LIKE_SCALE = 0.5f
    }
}