package com.binish.apptutorial.targets

import android.graphics.Point


/**
 * Showcase a specific x/y co-ordinate on the screen.
 */
class PointTarget : Target {
    override val point: Point

    constructor(point: Point) {
        this.point = point
    }

    constructor(xValue: Int, yValue: Int) {
        point = Point(xValue, yValue)
    }
}