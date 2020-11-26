package com.binish.apptutorial.targets

import android.graphics.Point


interface Target {
    val point: Point?

    companion object {
        val NONE: Target = object : Target {
            override val point: Point?
                get() = Point(1000000, 1000000)
        }
    }
}