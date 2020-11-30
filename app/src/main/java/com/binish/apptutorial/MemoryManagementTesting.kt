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
import com.binish.apptutorialview.SimpleShowcaseEventListener

class MemoryManagementTesting : Activity() {
    var currentShowcase = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        showcase()
    }

    private fun showcase() {
        ShowcaseView.Builder(this)
            .withMaterialShowcase()
            .setContentText(String.format("Showing %1\$d", currentShowcase))
            .setTarget(ViewTarget(R.id.buttonBlocked, this))
            .setShowcaseEventListener(
                object : SimpleShowcaseEventListener() {
                    override fun onShowcaseViewDidHide(showcaseView: ShowcaseView?) {
                        currentShowcase++
                        showcase()
                    }
                }
            )
            .build()
    }
}