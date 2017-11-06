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
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.cardinalblue.gesture.IGestureStateOwner;
import com.cardinalblue.gesture.MyMotionEvent;

import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING;

public class DragState extends BaseGestureState {

    // Given..
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    private VelocityTracker mVelocityTracker;
    private float mStartFocusX;
    private float mStartFocusY;

    public DragState(IGestureStateOwner owner,
                     int minimumFlingVelocity,
                     int maximumFlingVelocity) {
        super(owner);

        mMinFlingVelocity = minimumFlingVelocity;
        mMaxFlingVelocity = maximumFlingVelocity;
    }

    @Override
    public void onEnter(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        mStartFocusX = event.getX();
        mStartFocusY = event.getY();

        mOwner.getListener().onDragBegin(
            obtainMyMotionEvent(event),
            touchingObject, touchingContext);
    }

    @Override
    public void onDoing(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getActionMasked();
        final float focusX = event.getX();
        final float focusY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
                // Transit to multiple-fingers-pressing state.
                mOwner.issueStateTransition(
                    STATE_MULTIPLE_FINGERS_PRESSING,
                    event, touchingObject, touchingContext);
                break;

            case MotionEvent.ACTION_MOVE:
                mOwner.getListener().onDrag(
                    obtainMyMotionEvent(event), touchingObject, touchingContext,
                    new PointF(mStartFocusX, mStartFocusY),
                    new PointF(focusX, focusY));
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Transit to IDLE state.
                mOwner.issueStateTransition(STATE_IDLE, event, touchingObject, touchingContext);
                break;
        }
    }

    @Override
    public void onExit(MotionEvent event,
                       Object touchingObject,
                       Object touchingContext) {
        final MyMotionEvent clone = obtainMyMotionEvent(event);
        final float focusX = event.getX();
        final float focusY = event.getY();

        if (isConsideredFling(event)) {
            // TODO: Complete fling arguments.
            mOwner.getListener().onDragFling(clone, touchingObject, touchingContext,
                                             new PointF(), new PointF(), 0, 0);
        }

        // TODO: Complete the translation argument.
        mOwner.getListener().onDragEnd(
            clone, touchingObject, touchingContext,
            new PointF(mStartFocusX, mStartFocusY),
            new PointF(focusX, focusY));

        if (mVelocityTracker != null) {
            // This may have been cleared when we called out to the
            // application above.
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public boolean onHandleMessage(Message msg) {
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private boolean isConsideredFling(MotionEvent event) {
        final int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_UP) {
            // A fling must travel the minimum tap distance
            final int pointerId = event.getPointerId(0);
            mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);

            final float velocityY = mVelocityTracker.getYVelocity(pointerId);
            final float velocityX = mVelocityTracker.getXVelocity(pointerId);

            return (Math.abs(velocityY) > mMinFlingVelocity)
                   || (Math.abs(velocityX) > mMinFlingVelocity);
        } else {
            return false;
        }
    }
}
