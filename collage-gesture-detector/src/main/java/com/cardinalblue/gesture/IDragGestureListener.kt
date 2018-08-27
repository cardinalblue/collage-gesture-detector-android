// Copyright Feb 2018-present CardinalBlue
//
// Author: boy@cardinalblue.com
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

import android.view.MotionEvent

interface IDragGestureListener {

    // Drag ///////////////////////////////////////////////////////////////

    fun onDragBegin(event: ShadowMotionEvent,
                    target: Any?,
                    context: Any?)

    fun onDrag(event: ShadowMotionEvent,
               target: Any?,
               context: Any?,
               startPointer: Pair<Float, Float>,
               stopPointer: Pair<Float, Float>)

    fun onDragEnd(event: ShadowMotionEvent,
                  target: Any?,
                  context: Any?,
                  startPointer: Pair<Float, Float>,
                  stopPointer: Pair<Float, Float>)

    // Fling //////////////////////////////////////////////////////////////

    /**
     * Notified of a fling event when it occurs with the initial on down
     * [MotionEvent] and the matching up [MotionEvent]. The
     * calculated velocity is supplied along the x and y axis out pixels per
     * second.
     * @param event                The MotionEvent alternative.
     * @param startPointer The first down pointer that started the
     * fling.
     * @param stopPointer  The move pointer that triggered the
     * current onDragFling.
     * @param velocityX            The velocity of this fling measured in
     * pixels per second along the x axis.
     * @param velocityY            The velocity of this fling measured in
     */
    fun onDragFling(event: ShadowMotionEvent,
                    target: Any?,
                    context: Any?,
                    startPointer: Pair<Float, Float>,
                    stopPointer: Pair<Float, Float>,
                    velocityX: Float,
                    velocityY: Float)
}
