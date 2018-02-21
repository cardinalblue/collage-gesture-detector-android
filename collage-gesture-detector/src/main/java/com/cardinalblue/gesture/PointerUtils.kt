// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         jack.huang@cardinalblue.com
//         yolung.lu@cardinalblue.com
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the "Software"),
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
// THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.

package com.cardinalblue.gesture

import android.graphics.PointF

object PointerUtils {

    const val DELTA_X = 0
    const val DELTA_Y = 1
    const val DELTA_SCALE_X = 2
    const val DELTA_SCALE_Y = 3
    const val DELTA_RADIANS = 4

    /**
     * Get an array of [tx, ty, sx, sy, rotation] sequence representing
     * the transformation from the given event.
     */
    fun getTransformFromPointers(startPointers: Array<PointF>,
                                 stopPointers: Array<PointF>): FloatArray {
        if (startPointers.size < 2 || stopPointers.size < 2) {
            throw IllegalStateException(
                "The event must have at least two down pointers.")
        }

        val transform = floatArrayOf(0f, 0f, 1f, 1f, 0f)

        // Start pointer 1.
        val startX1 = startPointers[0].x
        val startY1 = startPointers[0].y
        // Start pointer 2.
        val startX2 = startPointers[1].x
        val startY2 = startPointers[1].y

        // Stop pointer 1.
        val stopX1 = stopPointers[0].x
        val stopY1 = stopPointers[0].y
        // Stop pointer 2.
        val stopX2 = stopPointers[1].x
        val stopY2 = stopPointers[1].y

        // Start vector.
        val startVecX = startX2 - startX1
        val startVecY = startY2 - startY1
        // Stop vector.
        val stopVecX = stopX2 - stopX1
        val stopVecY = stopY2 - stopY1

        // Start pivot.
        val startPivotX = (startX1 + startX2) / 2f
        val startPivotY = (startY1 + startY2) / 2f
        // Stop pivot.
        val stopPivotX = (stopX1 + stopX2) / 2f
        val stopPivotY = (stopY1 + stopY2) / 2f

        // Calculate the translation.
        transform[DELTA_X] = stopPivotX - startPivotX
        transform[DELTA_Y] = stopPivotY - startPivotY
        // Calculate the rotation degree.
        transform[DELTA_RADIANS] = (Math.atan2(stopVecY.toDouble(), stopVecX.toDouble()) - Math.atan2(startVecY.toDouble(), startVecX.toDouble())).toFloat()
        // Calculate the scale change.
        val dScale = (Math.hypot(stopVecX.toDouble(),
                                 stopVecY.toDouble()) / Math.hypot(startVecX.toDouble(),
                                                                   startVecY.toDouble())).toFloat()
        transform[DELTA_SCALE_X] = dScale
        transform[DELTA_SCALE_Y] = dScale
//        Log.d("xyz", String.format(Locale.ENGLISH,
//                                   "getTransform: " +
//                                   "dx=%.3f, dy=%.3f, " +
//                                   "dScale=%.3f, " +
//                                   "dRadians=%.3f",
//                                   transform[DELTA_X], transform[DELTA_Y],
//                                   transform[DELTA_SCALE_X],
//                                   transform[DELTA_RADIANS]));

        return transform
    }
}
