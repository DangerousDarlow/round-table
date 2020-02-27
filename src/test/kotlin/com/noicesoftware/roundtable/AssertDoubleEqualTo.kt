package com.noicesoftware.roundtable

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import kotlin.math.abs

fun Assert<Double>.isEqualTo(expected: Double, tolerance: Double) = given { actual ->
    val difference = abs(expected - actual)
    if (difference <= tolerance) return
    expected(
            message = ": value${show(expected)} with tolerance ${show(tolerance)} but actual was:${show(actual)}",
            expected = tolerance,
            actual = difference
    )
}