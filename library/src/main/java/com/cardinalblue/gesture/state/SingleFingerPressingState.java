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

package com.cardinalblue.gesture.state;

import android.os.Message;
import android.view.MotionEvent;

import com.cardinalblue.gesture.IGestureStateOwner;
import com.cardinalblue.gesture.MyMotionEvent;

import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_DRAG;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE;
import static com.cardinalblue.gesture.IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING;

public class SingleFingerPressingState extends BaseGestureState {

    private static final int MSG_TAP = 0xA1;
    private static final int MSG_LONG_PRESS = 0xA2;

    // Given...
    private final int mTapSlopSquare;
    private final int mTouchSlopSquare;
    private final int mTapTimeout;
    private final int mLongPressTimeout;

    // Configurations.
    private boolean mIsTapEnabled = true;
    private boolean mIsLongPressEnabled = true;

    private boolean mHadLongPress;

    private int mTapCount;

    private float mStartFocusX;
    private float mStartFocusY;

    private MotionEvent mPreviousDownEvent;
    private MotionEvent mCurrentDownEvent;
    private MotionEvent mCurrentUpEvent;

    private Object mTouchingObject;
    private Object mTouchingContext;

    public SingleFingerPressingState(IGestureStateOwner owner,
                                     int tapSlopSquare,
                                     int touchSlopSquare,
                                     int tapTimeout,
                                     int longPressTimeout) {
        super(owner);

        mTapSlopSquare = tapSlopSquare;
        mTouchSlopSquare = touchSlopSquare;
        mTapTimeout = tapTimeout;
        mLongPressTimeout = longPressTimeout;
    }

    @Override
    public void onEnter(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        mTapCount = 0;
        mHadLongPress = false;

        // Hold touching things.
        mTouchingObject = touchingObject;
        mTouchingContext = touchingContext;

        onDoing(event, touchingObject, touchingContext);
    }

    @Override
    public void onDoing(MotionEvent event,
                        Object touchingObject,
                        Object touchingContext) {
        final int action = event.getActionMasked();
        final boolean pointerUp = (action == MotionEvent.ACTION_POINTER_UP);
        final int skipIndex = pointerUp ? event.getActionIndex() : -1;

        // Determine focal point.
        float sumX = 0, sumY = 0;
        final int count = event.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += event.getX(i);
            sumY += event.getY(i);
        }
        final int pointerDownCount = pointerUp ? count - 1 : count;
        final float focusX = sumX / pointerDownCount;
        final float focusY = sumY / pointerDownCount;

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                mOwner.issueStateTransition(
                    STATE_MULTIPLE_FINGERS_PRESSING,
                    event, touchingObject, touchingContext);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                mStartFocusX = focusX;
                mStartFocusY = focusY;
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                // Hold events.
                if (mPreviousDownEvent != null) {
                    mPreviousDownEvent.recycle();
                    mPreviousDownEvent = null;
                }
                mPreviousDownEvent = mCurrentDownEvent;
                mCurrentDownEvent = MotionEvent.obtain(event);

                final boolean isSingleFinger = pointerDownCount == 1;
                if (isSingleFinger) {
                    // Handle TAP (determine if it is a TAP gesture by waiting
                    // for a short period).
                    boolean hadTapMessage = mOwner.getHandler().hasMessages(MSG_TAP);
                    if (hadTapMessage) {
                        // Remove unhandled tap.
                        mOwner.getHandler().removeMessages(MSG_TAP);
                    }

                    // Handle long-press.
                    if (mIsLongPressEnabled) {
                        // Remove unhandled long-press.
                        mOwner.getHandler().removeMessages(MSG_LONG_PRESS);
                        mOwner.getHandler().sendMessageAtTime(
                            obtainMessageWithPayload(
                                MSG_LONG_PRESS, event, touchingObject, touchingContext),
                            event.getDownTime() + mTapTimeout + mLongPressTimeout);
                    }

                    // Hold start x and y.
                    mStartFocusX = focusX;
                    mStartFocusY = focusY;
                } else {
                    // Transit to new state.
                    mOwner.issueStateTransition(
                        STATE_MULTIPLE_FINGERS_PRESSING,
                        event, touchingObject, touchingContext);
                }

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int deltaX = (int) (focusX - mStartFocusX);
                final int deltaY = (int) (focusY - mStartFocusY);
                final int distance = (deltaX * deltaX) + (deltaY * deltaY);

                if (distance > mTouchSlopSquare) {
                    // Transit to new state.
                    mOwner.issueStateTransition(
                        STATE_DRAG,
                        event, touchingObject, touchingContext);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                // Hold up event.
                if (mCurrentUpEvent != null) {
                    mCurrentUpEvent.recycle();
                }
                mCurrentUpEvent = MotionEvent.obtain(event);

                if (mHadLongPress) {
                    // Transit to IDLE state.
                    mOwner.issueStateTransition(
                        STATE_IDLE, event, touchingObject, touchingContext);
                } else {
                    // Two continuous taps far apart doesn't count as a double tap.
                    if (isConsideredCloseTap(mPreviousDownEvent, mCurrentDownEvent)) {
                        ++mTapCount;
                    }

                    // Because it is a TAP, cancel the upcoming long-press delay.
                    cancelLongPress();

                    // Defer the transition to IDLE state.
                    mOwner.getHandler().sendMessageDelayed(
                        obtainMessageWithPayload(MSG_TAP,
                                                 event,
                                                 touchingObject,
                                                 touchingContext),
                        mTapTimeout);
                }
                break;
            }

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
        final int action = event.getActionMasked();

        // Cancel tap.
        mOwner.getHandler().removeMessages(MSG_TAP);
        // Cancel long-press.
        if (mIsLongPressEnabled) {
            mOwner.getHandler().removeMessages(MSG_LONG_PRESS);
        }

        // Dispatch tap callback.
        if (action == MotionEvent.ACTION_UP) {
            final MyMotionEvent clone = obtainMyMotionEvent(mCurrentUpEvent);

            if (mIsLongPressEnabled && mHadLongPress) {
                mOwner.getListener().onLongTap(
                    clone, mTouchingObject, mTouchingContext);
            } else if (mIsTapEnabled && mTapCount > 0) {
                if (mTapCount == 1) {
                    mOwner.getListener().onSingleTap(
                        clone, mTouchingObject, mTouchingContext);
                } else if (mTapCount == 2) {
                    mOwner.getListener().onDoubleTap(
                        clone, mTouchingObject, mTouchingContext);
                } else {
                    mOwner.getListener().onMoreTap(
                        clone, mTouchingObject, mTouchingContext, mTapCount);
                }
            }
        }

        // Recycle hold events.
        if (mPreviousDownEvent != null) {
            mPreviousDownEvent.recycle();
            mPreviousDownEvent = null;
        }
        if (mCurrentDownEvent != null) {
            mCurrentDownEvent.recycle();
            mCurrentDownEvent = null;
        }
        if (mCurrentUpEvent != null) {
            mCurrentUpEvent.recycle();
            mCurrentUpEvent = null;
        }
    }

    @Override
    public boolean onHandleMessage(Message msg) {
        switch (msg.what) {
            case MSG_LONG_PRESS: {
                final MyMessagePayload payload = (MyMessagePayload) msg.obj;

                // Notify the detector that it's NOT a tap anymore.
                cancelTaps();
                mHadLongPress = true;

                if (mIsLongPressEnabled && mOwner.getListener() != null) {
                    mOwner.getListener().onLongPress(
                        payload.event,
                        payload.touchingTarget,
                        payload.touchingContext);
                }
                return true;
            }

            case MSG_TAP: {
                // Transit to IDLE state.
                // Note: the IDLE state would received a recycled hold-down-event.
                mOwner.issueStateTransition(
                    STATE_IDLE, mCurrentUpEvent,
                    mTouchingObject, mTouchingContext);
                return true;
            }

            default:
                return false;
        }
    }

    public boolean getIsTapEnabled() {
        return mIsTapEnabled;
    }

    public void setIsTapEnabled(boolean enabled) {
        mIsTapEnabled = enabled;
    }

    public boolean getIsLongPressEnabled() {
        return mIsLongPressEnabled;
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
        mIsLongPressEnabled = enabled;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private boolean isConsideredCloseTap(MotionEvent previousDown,
                                         MotionEvent currentDown) {
        if (currentDown == null) {
            // The current down event might be null because the state is entered
            // from ACTION_POINTER_UP action.
            return false;
        } else if (previousDown == null) {
            // The previous down event is null only when the state is entered from
            // ACTION_DOWN action.
            return true;
        }

//            final long deltaTime = currentUp.getEventTime() - previousUp.getEventTime();
//            if (deltaTime > TAP_TIMEOUT) {
//                // The previous-up is too long ago, it is definitely a new tap!
//                return false;
//            }

        final int deltaX = (int) currentDown.getX() - (int) previousDown.getX();
        final int deltaY = (int) currentDown.getY() - (int) previousDown.getY();
        return (deltaX * deltaX + deltaY * deltaY < mTapSlopSquare);
    }

    private void cancelLongPress() {
        mOwner.getHandler().removeMessages(MSG_LONG_PRESS);

        mHadLongPress = false;
    }

    private void cancelTaps() {
        mOwner.getHandler().removeMessages(MSG_TAP);
    }
}
