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
package com.binish.apptutorialview

import android.content.Context

internal class ShotStateStore(private val context: Context) {
    var shotId = INVALID_SHOT_ID.toLong()
    fun hasShot(): Boolean {
        return isSingleShot && context
            .getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
            .getBoolean("hasShot$shotId", false)
    }

    val isSingleShot: Boolean
        get() = shotId != INVALID_SHOT_ID.toLong()

    fun storeShot() {
        if (isSingleShot) {
            val internal =
                context.getSharedPreferences(PREFS_SHOWCASE_INTERNAL, Context.MODE_PRIVATE)
            internal.edit().putBoolean("hasShot$shotId", true).apply()
        }
    }

    fun setSingleShot(shotId: Long) {
        this.shotId = shotId
    }

    companion object {
        private const val PREFS_SHOWCASE_INTERNAL = "showcase_internal"
        private const val INVALID_SHOT_ID = -1
    }
}