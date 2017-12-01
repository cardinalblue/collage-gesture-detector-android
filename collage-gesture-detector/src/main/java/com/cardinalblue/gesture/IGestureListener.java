//  Copyright Oct 2017-present CardinalBlue
//
//  Author: boy@cardinalblue.com
//          jack.huang@cardinalblue.com
//          yolung.lu@cardinalblue.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.cardinalblue.gesture;

import android.graphics.PointF;
import android.view.MotionEvent;

public interface IGestureListener {

    void onActionBegin(MyMotionEvent event,
                       Object touchingObject,
                       Object touchingContext);

    void onActionEnd(MyMotionEvent event,
                     Object touchingObject,
                     Object touchingContext);

    void onSingleTap(MyMotionEvent event,
                     Object touchingObject,
                     Object touchingContext);

    void onDoubleTap(MyMotionEvent event,
                     Object touchingObject,
                     Object touchingContext);

    void onMoreTap(MyMotionEvent event,
                   Object touchingObject,
                   Object touchingContext,
                   int tapCount);

    void onLongTap(MyMotionEvent event,
                   Object touchingObject,
                   Object touchingContext);

    void onLongPress(MyMotionEvent event,
                     Object touchingObject,
                     Object touchingContext);

    // Drag ///////////////////////////////////////////////////////////////

    void onDragBegin(MyMotionEvent event,
                     Object touchingObject,
                     Object touchingContext);

    void onDrag(MyMotionEvent event,
                Object touchingObject,
                Object touchingContext,
                PointF startPointerInCanvas,
                PointF stopPointerInCanvas);

    void onDragEnd(MyMotionEvent event,
                   Object touchingObject,
                   Object touchingContext,
                   PointF startPointerInCanvas,
                   PointF stopPointerInCanvas);

    // Fling //////////////////////////////////////////////////////////////

    /**
     * Notified of a fling event when it occurs with the initial on down
     * {@link MotionEvent} and the matching up {@link MotionEvent}. The
     * calculated velocity is supplied along the x and y axis in pixels per
     * second.
     * @param event                The MotionEvent alternative.
     * @param startPointerInCanvas The first down pointer that started the
     *                             fling.
     * @param stopPointerInCanvas  The move pointer that triggered the
*                             current onDragFling.
     * @param velocityX            The velocity of this fling measured in
*                             pixels per second along the x axis.
     * @param velocityY            The velocity of this fling measured in
     */
    void onDragFling(MyMotionEvent event,
                     Object touchingObject,
                     Object touchContext,
                     PointF startPointerInCanvas,
                     PointF stopPointerInCanvas,
                     float velocityX,
                     float velocityY);

    // Pinch //////////////////////////////////////////////////////////////

    void onPinchBegin(MyMotionEvent event,
                      Object touchingObject,
                      Object touchContext,
                      PointF[] startPointers);

    void onPinch(MyMotionEvent event,
                 Object touchingObject,
                 Object touchContext,
                 PointF[] startPointersInCanvas,
                 PointF[] stopPointersInCanvas);

    // TODO: (Not implemented) Figure out the arguments.
    void onPinchFling(MyMotionEvent event,
                      Object touchingObject,
                      Object touchContext);

    void onPinchEnd(MyMotionEvent event,
                    Object touchingObject,
                    Object touchContext,
                    PointF[] startPointersInCanvas,
                    PointF[] stopPointersInCanvas);
}
