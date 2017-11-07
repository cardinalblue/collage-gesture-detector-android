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

import java.util.ArrayList;

import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING;

public class PinchState extends BaseGestureState {

    /**
     * The pointers at the moment this state is entered.
     */
    private final SparseArray<PointF> mStartPointers = new SparseArray<>();
    /**
     * The pointers at the moment this state is doing.
     */
    private final SparseArray<PointF> mStopPointers = new SparseArray<>();
    /**
     * This is for holding the order of fingers getting down. Because the
     * {@link SparseArray} is unordered, we introduce auxiliary space to store
     * the order.
     */
    private ArrayList<Integer> mOrderedPointerIds = new ArrayList<>();

    public PinchState(IGestureStateOwner owner) {
        super(owner);
    }

    @Override
    public void onEnter(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        final int action = event.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int upIndex = pointerUp ? event.getActionIndex() : -1;

        // Hold the all down pointers.
        mStartPointers.clear();
        mStopPointers.clear();
        mOrderedPointerIds.clear();
        for (int i = 0; i < event.getPointerCount(); ++i) {
            if (i == upIndex) continue;

            final int id = event.getPointerId(i);

            mStartPointers.put(id, new PointF(event.getX(i),
                                              event.getY(i)));
            mStopPointers.put(id, new PointF(event.getX(i),
                                             event.getY(i)));

            mOrderedPointerIds.add(id);
        }

        // Dispatch pinch-begin.
        mOwner.getListener().onPinchBegin(
            obtainMyMotionEvent(event), touchingObject, touchingContext,
            new PointF[]{mStartPointers.get(mOrderedPointerIds.get(0)),
                         mStartPointers.get(mOrderedPointerIds.get(1))});
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
                // Update stop pointers.
                final PointF pointer1 = mStopPointers.get(mOrderedPointerIds.get(0));
                pointer1.set(event.getX(0), event.getY(0));

                final PointF pointer2 = mStopPointers.get(mOrderedPointerIds.get(1));
                pointer2.set(event.getX(1), event.getY(1));

                // Dispatch callback.
                mOwner.getListener().onPinch(
                    obtainMyMotionEvent(event), touchingObject, touchingContext,
                    new PointF[]{mStartPointers.get(mOrderedPointerIds.get(0)),
                                 mStartPointers.get(mOrderedPointerIds.get(1))},
                    new PointF[]{mStopPointers.get(mOrderedPointerIds.get(0)),
                                 mStopPointers.get(mOrderedPointerIds.get(1))});

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
                // Hold new down pointer ID.
                mOrderedPointerIds.add(event.getPointerId(downIndex));
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                if (downPointerCount >= 2) {
                    final int upId = event.getPointerId(upIndex);

                    if (mOrderedPointerIds.indexOf(upId) != -1 &&
                        mOrderedPointerIds.indexOf(upId) < 2) {
                        // The anchor pointers (first two) is changed, the gesture
                        // would end and restart.
                        mOwner.getListener().onPinchEnd(
                            obtainMyMotionEvent(event), touchingObject, touchingContext,
                            new PointF[]{mStartPointers.get(mOrderedPointerIds.get(0)),
                                         mStartPointers.get(mOrderedPointerIds.get(1))},
                            new PointF[]{mStopPointers.get(mOrderedPointerIds.get(0)),
                                         mStopPointers.get(mOrderedPointerIds.get(1))});

                        // Refresh the start pointers.
                        mStartPointers.clear();
                        mStopPointers.clear();
                        mOrderedPointerIds.clear();
                        for (int i = 0; i < event.getPointerCount(); ++i) {
                            if (i == upIndex) continue;

                            mStartPointers.put(event.getPointerId(i),
                                               new PointF(event.getX(i),
                                                          event.getY(i)));
                            mStopPointers.put(event.getPointerId(i),
                                              new PointF(event.getX(i),
                                                         event.getY(i)));
                            mOrderedPointerIds.add(event.getPointerId(i));
                        }

                        // Restart the gesture.
                        mOwner.getListener().onPinchBegin(
                            obtainMyMotionEvent(event), touchingObject, touchingContext,
                            new PointF[]{mStartPointers.get(mOrderedPointerIds.get(0)),
                                         mStartPointers.get(mOrderedPointerIds.get(1))});
                    } else {
                        // Remove the hold (up) pointer.
                        mStartPointers.remove(upId);
                        mStopPointers.remove(upId);
                        int index = mOrderedPointerIds.indexOf(upId);
                        if (index != -1) {
                            mOrderedPointerIds.remove(index);
                        }
                    }
                } else {
                    // Transit to STATE_SINGLE_FINGER_PRESSING state.
                    mOwner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, touchingObject, touchingContext);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                // Transit to IDLE state.
                mOwner.issueStateTransition(STATE_IDLE,
                                            event,
                                            touchingObject,
                                            touchingContext);
                break;
            }
        }
    }

    @Override
    public void onExit(MotionEvent event,
                       Object touchingObject,
                       Object touchingContext) {
        // Dispatch pinch-end.
        mOwner.getListener().onPinchEnd(
            obtainMyMotionEvent(event), touchingObject, touchingContext,
            new PointF[]{mStartPointers.get(mStartPointers.keyAt(0)),
                         mStartPointers.get(mStartPointers.keyAt(1))},
            new PointF[]{mStopPointers.get(mStopPointers.keyAt(0)),
                         mStopPointers.get(mStopPointers.keyAt(1))});

        // Clear pointers.
        mStartPointers.clear();
        mStopPointers.clear();
        mOrderedPointerIds.clear();
    }

    @Override
    public boolean onHandleMessage(Message msg) {
        return false;
    }
}
