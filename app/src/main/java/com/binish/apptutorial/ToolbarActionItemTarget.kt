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

import android.graphics.Point
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import com.binish.apptutorial.targets.Target
import com.binish.apptutorial.targets.ViewTarget

/**
 * Represents an Action item to showcase (e.g., one of the buttons on an ActionBar).
 * To showcase specific action views such as the home button, use [ToolbarActionItemTarget]
 *
 * @see ToolbarActionItemTarget
 */
class ToolbarActionItemTarget(
    private val toolbar: Toolbar,
    @param:IdRes private val menuItemId: Int
) : Target {
    override val point: Point
        get() = ViewTarget(toolbar.findViewById(menuItemId)).point
}