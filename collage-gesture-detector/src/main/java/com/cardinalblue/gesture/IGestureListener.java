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
import android.support.annotation.NonNull;
import android.view.MotionEvent;

public interface IGestureListener {

    void onActionBegin(@NonNull MyMotionEvent event,
                       Object object,
                       Object context);

    void onActionEnd(@NonNull MyMotionEvent event,
                     Object object,
                     Object context);

    void onSingleTap(@NonNull MyMotionEvent event,
                     Object object,
                     Object context);

    void onDoubleTap(@NonNull MyMotionEvent event,
                     Object object,
                     Object context);

    void onMoreTap(@NonNull MyMotionEvent event,
                   Object object,
                   Object context,
                   int tapCount);

    void onLongTap(@NonNull MyMotionEvent event,
                   Object object,
                   Object context);

    void onLongPress(@NonNull MyMotionEvent event,
                     Object object,
                     Object context);

    // Drag ///////////////////////////////////////////////////////////////

    void onDragBegin(@NonNull MyMotionEvent event,
                     Object object,
                     Object context);

    void onDrag(@NonNull MyMotionEvent event,
                Object object,
                Object context,
                @NonNull PointF startPointer,
                @NonNull PointF stopPointer);

    void onDragEnd(@NonNull MyMotionEvent event,
                   Object object,
                   Object context,
                   @NonNull PointF startPointer,
                   @NonNull PointF stopPointer);

    // Fling //////////////////////////////////////////////////////////////

    /**
     * Notified of a fling event when it occurs with the initial on down
     * {@link MotionEvent} and the matching up {@link MotionEvent}. The
     * calculated velocity is supplied along the x and y axis in pixels per
     * second.
     * @param event                The MotionEvent alternative.
     * @param startPointer The first down pointer that started the
     *                             fling.
     * @param stopPointer  The move pointer that triggered the
*                             current onDragFling.
     * @param velocityX            The velocity of this fling measured in
*                             pixels per second along the x axis.
     * @param velocityY            The velocity of this fling measured in
     */
    void onDragFling(@NonNull MyMotionEvent event,
                     Object object,
                     Object touchContext,
                     @NonNull PointF startPointer,
                     @NonNull PointF stopPointer,
                     float velocityX,
                     float velocityY);

    // Pinch //////////////////////////////////////////////////////////////

    void onPinchBegin(@NonNull MyMotionEvent event,
                      Object object,
                      Object touchContext,
                      @NonNull PointF[] startPointers);

    void onPinch(@NonNull MyMotionEvent event,
                 Object object,
                 Object touchContext,
                 @NonNull PointF[] startPointers,
                 @NonNull PointF[] stopPointers);

    // TODO: (Not implemented) Figure out the arguments.
    void onPinchFling(@NonNull MyMotionEvent event,
                      Object object,
                      Object touchContext);

    void onPinchEnd(@NonNull MyMotionEvent event,
                    Object object,
                    Object touchContext,
                    @NonNull PointF[] startPointers,
                    @NonNull PointF[] stopPointers);
}
