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

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.cardinalblue.gesture.state.BaseGestureState;
import com.cardinalblue.gesture.state.DragState;
import com.cardinalblue.gesture.state.IdleState;
import com.cardinalblue.gesture.state.MultipleFingersPressingState;
import com.cardinalblue.gesture.state.PinchState;
import com.cardinalblue.gesture.state.SingleFingerPressingState;

public class GestureDetector implements Handler.Callback,
                                        IGestureStateOwner {

    private int mTouchSlopSquare;
    private int mTapSlopSquare;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;

    /**
     * Defines the default duration in milliseconds before a press turns into
     * a long press.
     */
    private static final int LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    /**
     * The duration in milliseconds we will wait to see if a touch event is a
     * tap or a scroll. If the user does not move within this interval, it is
     * considered to be a tap.
     */
    private static final int TAP_TIMEOUT = Math.max(150, ViewConfiguration.getTapTimeout());

    private final Handler mHandler;
    private final IGestureListener mListener;

    private BaseGestureState mState;
    private final IdleState mIdleState;
    private final SingleFingerPressingState mSingleFingerPressingState;
    private final MultipleFingersPressingState mMultipleFingersPressingState;
    private final DragState mDragState;
    private final PinchState mPinchState;

    /**
     * Creates a GestureDetector with the supplied listener.
     * You may only use this constructor from a {@link android.os.Looper} thread.
     *
     * @param context the application's context
     * @param touchSlop
     * @param tapSlop
     * @param minFlingVec
     * @param maxFlingVec @throws NullPointerException if {@code listener} is null.
     * @see android.os.Handler#Handler()
     */
    public GestureDetector(Context context,
                           IGestureListener listener,
                           float touchSlop,
                           float tapSlop,
                           float minFlingVec,
                           float maxFlingVec) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalThreadStateException(
                "The detector should be always initialized on the main thread.");
        }

        mHandler = new GestureHandler(this);
        mListener = listener;

        // Init properties like touch slop square, tap slop square, ..., etc.
        init(context, touchSlop, tapSlop, minFlingVec, maxFlingVec);

        // TODO: Configure the policy of factory of the factor.

        // Internal states.
        mIdleState = new IdleState(this);
        mSingleFingerPressingState = new SingleFingerPressingState(
            this,
            mTapSlopSquare, mTouchSlopSquare,
            TAP_TIMEOUT, LONG_PRESS_TIMEOUT);
        mMultipleFingersPressingState = new MultipleFingersPressingState(this, mTouchSlopSquare);
        mDragState = new DragState(this, mMinFlingVelocity, mMaxFlingVelocity);
        mPinchState = new PinchState(this);

        // Init IDLE state.
        mState = mIdleState;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public IGestureListener getListener() {
        return mListener;
    }

    @Override
    public void issueStateTransition(IGestureStateOwner.State newState,
                                     MotionEvent event,
                                     Object touchingObject,
                                     Object touchingContext) {
        BaseGestureState oldState = mState;
        if (oldState != null) {
            oldState.onExit(event, touchingObject, touchingContext);
        }

        // TODO: Use a factor to produce reusable internal states.
        switch (newState) {
            case STATE_IDLE:
                mState = mIdleState;
                break;
            case STATE_SINGLE_FINGER_PRESSING:
                mState = mSingleFingerPressingState;
                break;
            case STATE_DRAG:
                mState = mDragState;
                break;
            case STATE_MULTIPLE_FINGERS_PRESSING:
                mState = mMultipleFingersPressingState;
                break;
            case STATE_PINCH:
                mState = mPinchState;
                break;
            default:
                mState = mIdleState;
        }

        // Enter new state.
        mState.onEnter(event, touchingObject, touchingContext);
    }

    public void setIsTapEnabled(boolean enabled) {
        mSingleFingerPressingState.setIsTapEnabled(enabled);
    }

    /**
     * Set whether long-press is enabled, if this is enabled when a user
     * presses and holds down you get a long-press event and nothing further.
     * If it's disabled the user can press and hold down and then later
     * moved their finger and you will get scroll events. By default
     * long-press is enabled.
     *
     * @param enabled whether long-press should be enabled.
     */
    public void setIsLongPressEnabled(boolean enabled) {
        mSingleFingerPressingState.setIsLongPressEnabled(enabled);
    }

    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the {@link IGestureListener}
     * supplied.
     *
     * @param event The current motion event.
     * @return true if the {@link IGestureListener} consumed
     * the event, else false.
     */
    public boolean onTouchEvent(MotionEvent event,
                                Object touchingObject,
                                Object touchingContext) {
        mState.onDoing(event, touchingObject, touchingContext);

        return mSingleFingerPressingState.getIsTapEnabled() |
               mSingleFingerPressingState.getIsLongPressEnabled();
    }

    @Override
    public boolean handleMessage(Message msg) {
        // Delegate to state.
        return mState.onHandleMessage(msg);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private void init(Context context,
                      float touchSlop,
                      float tapSlop,
                      float minFlingVec,
                      float maxFlingVec) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);

        touchSlop = Math.min(touchSlop, configuration.getScaledTouchSlop());
        tapSlop = Math.min(tapSlop, configuration.getScaledDoubleTapSlop());

        mTouchSlopSquare = (int) (touchSlop * touchSlop);
        mTapSlopSquare = (int) (tapSlop * tapSlop);

        mMinFlingVelocity = (int) Math.max(minFlingVec,
                                           configuration.getScaledMinimumFlingVelocity());
        mMaxFlingVelocity = (int) Math.max(maxFlingVec,
                                           configuration.getScaledMaximumFlingVelocity());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    private static class GestureHandler extends Handler {

        GestureHandler(Callback callback) {
            super(callback);
        }
    }
}

