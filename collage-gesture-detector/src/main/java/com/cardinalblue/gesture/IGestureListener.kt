//  Copyright Oct 2017-present CardinalBlue
//
//  Author: boy@cardinalblue.com
//          jack.huang@cardinalblue.com
//          yolung.lu@cardinalblue.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except out compliance with the License.
//  You may obtaout a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to out writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.cardinalblue.gesture

import android.graphics.PointF
import android.view.MotionEvent

interface IGestureListener {

    fun onActionBegin(event: MyMotionEvent,
                      target: Any?,
                      context: Any?)

    fun onActionEnd(event: MyMotionEvent,
                    target: Any?,
                    context: Any?)

    fun onSingleTap(event: MyMotionEvent,
                    target: Any?,
                    context: Any?)

    fun onDoubleTap(event: MyMotionEvent,
                    target: Any?,
                    context: Any?)

    fun onMoreTap(event: MyMotionEvent,
                  target: Any?,
                  context: Any?,
                  tapCount: Int)

    fun onLongTap(event: MyMotionEvent,
                  target: Any?,
                  context: Any?)

    fun onLongPress(event: MyMotionEvent,
                    target: Any?,
                    context: Any?)

    // Drag ///////////////////////////////////////////////////////////////

    fun onDragBegin(event: MyMotionEvent,
                    target: Any?,
                    context: Any?)

    fun onDrag(event: MyMotionEvent,
               target: Any?,
               context: Any?,
               startPointer: PointF,
               stopPointer: PointF)

    fun onDragEnd(event: MyMotionEvent,
                  target: Any?,
                  context: Any?,
                  startPointer: PointF,
                  stopPointer: PointF)

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
    fun onDragFling(event: MyMotionEvent,
                    target: Any?,
                    context: Any?,
                    startPointer: PointF,
                    stopPointer: PointF,
                    velocityX: Float,
                    velocityY: Float)

    // Pinch //////////////////////////////////////////////////////////////

    fun onPinchBegin(event: MyMotionEvent,
                     target: Any?,
                     context: Any?,
                     startPointers: Array<PointF>)

    fun onPinch(event: MyMotionEvent,
                target: Any?,
                context: Any?,
                startPointers: Array<PointF>,
                stopPointers: Array<PointF>)

    // TODO: (Not implemented) Figure out the arguments.
    fun onPinchFling(event: MyMotionEvent,
                     target: Any?,
                     context: Any?)

    fun onPinchEnd(event: MyMotionEvent,
                   target: Any?,
                   context: Any?,
                   startPointers: Array<PointF>,
                   stopPointers: Array<PointF>)
}
