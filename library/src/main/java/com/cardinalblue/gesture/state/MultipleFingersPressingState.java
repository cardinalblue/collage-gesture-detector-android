//  Copyright Oct 2017-present CardinalBlue
//
//  Author: boy@cardinalblue.com
//          jack.huang@cardinalblue.com
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

package com.cardinalblue.gesture.state;

import android.graphics.PointF;
import android.os.Message;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.cardinalblue.gesture.IGestureStateOwner;

import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_PINCH;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING;

public class MultipleFingersPressingState extends BaseGestureState {

    // Given...
    private final int mTouchSlopSquare;

    // TODO: Time for insertion is O(log(n)), searching is O(1)
    // Pointers.
    private final SparseArray<PointF> mStartPointers = new SparseArray<>();
    private final SparseArray<PointF> mStopPointers = new SparseArray<>();

    public MultipleFingersPressingState(IGestureStateOwner owner,
                                        int touchSlopSquare) {
        super(owner);

        mTouchSlopSquare = touchSlopSquare;
    }

    @Override
    public void onEnter(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        final int action = event.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int upIndex = pointerUp ? event.getActionIndex() : -1;

        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_CANCEL) {
            // Transit to IDLE state.
            mOwner.issueStateTransition(
                STATE_IDLE,
                event, touchingObject, touchingContext);
        } else {
            final boolean isUp = action == MotionEvent.ACTION_POINTER_UP;
            final int pressCount = event.getPointerCount() - (isUp ? 1 : 0);
            final boolean isMultipleFingers = pressCount > 1;

            if (isMultipleFingers) {
                // Hold the all down pointers.
                mStartPointers.clear();
                mStopPointers.clear();
                for (int i = 0; i < event.getPointerCount(); ++i) {
                    if (i == upIndex) continue;

                    final int id = event.getPointerId(i);

                    mStartPointers.put(id, new PointF(event.getX(i),
                                                      event.getY(i)));
                    mStopPointers.put(id, new PointF(event.getX(i),
                                                     event.getY(i)));
                }
            } else {
                // Transit to single-finger-pressing state.
                mOwner.issueStateTransition(
                    STATE_SINGLE_FINGER_PRESSING,
                    event, touchingObject, touchingContext);
            }
        }
    }

    @Override
    public void onDoing(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        final int action = event.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int upIndex = pointerUp ? event.getActionIndex() : -1;
        final int downPointerCount = event.getPointerCount() - (pointerUp ? 1 : 0);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // Update the stop pointers.
                for (int i = 0; i < downPointerCount; ++i) {
                    final int id = event.getPointerId(i);
                    final PointF pointer = mStopPointers.get(id);

                    pointer.set(event.getX(i), event.getY(i));
                }

                // Transit to PINCH state.
                if (isConsideredPinch(mStartPointers, mStopPointers)) {
                    mOwner.issueStateTransition(
                        STATE_PINCH, event, touchingObject, touchingContext);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                // Hold undocumented pointers.
                int downIndex = event.getActionIndex();
                int downId = event.getPointerId(downIndex);
                mStartPointers.put(downId, new PointF(event.getX(downIndex),
                                                      event.getY(downIndex)));
                mStopPointers.put(downId, new PointF(event.getX(downIndex),
                                                     event.getY(downIndex)));
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int upId = event.getPointerId(upIndex);
                mStartPointers.remove(upId);
                mStopPointers.remove(upId);

                final boolean isMultipleFingers = downPointerCount > 1;
                if (!isMultipleFingers) {
                    // Transit to single-finger-pressing state.
                    mOwner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, touchingObject, touchingContext);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                // Transit to IDLE state.
                mOwner.issueStateTransition(
                    STATE_IDLE,
                    event, touchingObject, touchingContext);
                break;
            }
        }
    }

    private boolean isConsideredPinch(SparseArray<PointF> startPointers,
                                      SparseArray<PointF> stopPointers) {
        final int size = Math.min(startPointers.size(), stopPointers.size());
        for (int i = 0; i < size; ++i) {
            final PointF start = startPointers.get(startPointers.keyAt(i));
            final PointF stop = stopPointers.get(stopPointers.keyAt(i));
            final float dx = stop.x - start.x;
            final float dy = stop.y - start.y;

            if (dx * dx + dy * dy > mTouchSlopSquare) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onExit(MotionEvent event,
                       Object touchingObject,
                       Object touchingContext) {
        mStartPointers.clear();
        mStopPointers.clear();
    }

    @Override
    public boolean onHandleMessage(Message msg) {
        return false;
    }
}
