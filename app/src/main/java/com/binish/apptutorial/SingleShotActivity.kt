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

import android.app.Activity
import android.os.Bundle
import com.binish.apptutorial.targets.ViewTarget
import com.binish.apptutorialview.ShowcaseView

class SingleShotActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_shot)
        val viewTarget = ViewTarget(R.id.button, this)
        ShowcaseView.Builder(this)
            .setTarget(viewTarget)
            .setContentTitle(R.string.title_single_shot)
            .setContentText(R.string.R_string_desc_single_shot)
            .singleShot(42)
            .build()
    }
}