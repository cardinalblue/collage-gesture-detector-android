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

package com.cardinalblue.gesture.state

import android.os.Message
import android.view.MotionEvent

import com.cardinalblue.gesture.IGestureStateOwner

import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_DRAG
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_IDLE
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING

class SingleFingerPressingState(owner: IGestureStateOwner,
                                private val mTapSlopSquare: Long,
                                private val mTouchSlopSquare: Long,
                                private val mTapTimeout: Long,
                                private val mLongPressTimeout: Long)
    : BaseGestureState(owner) {

    // Configurations.
    var isTapEnabled = true
    /**
     * Set whether long-press is enabled, if this is enabled when a user
     * presses and holds down you get a long-press event and nothing further.
     * If it's disabled the user can press and hold down and then later
     * moved their finger and you will get scroll events. By default
     * long-press is enabled.
     */
    var isLongPressEnabled = true
    private var mIsMultitouchEnabled = true

    private var mHadLongPress: Boolean = false

    private var mTapCount: Int = 0

    private var mStartFocusX: Float = 0.toFloat()
    private var mStartFocusY: Float = 0.toFloat()

    private var mPreviousDownEvent: MotionEvent? = null
    private var mCurrentDownEvent: MotionEvent? = null
    private var mCurrentUpEvent: MotionEvent? = null

    private var mTouchingObject: Any? = null
    private var mTouchingContext: Any? = null

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        mTapCount = 0
        mHadLongPress = false

        // Hold touching things.
        mTouchingObject = target
        mTouchingContext = context

        onDoing(event, target, context)
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point.
        var sumX = 0f
        var sumY = 0f
        val count = event.pointerCount
        for (i in 0 until count) {
            if (upIndex == i) continue
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        val downPointerCount = if (pointerUp) count - 1 else count
        val focusX = sumX / downPointerCount
        val focusY = sumY / downPointerCount

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!mIsMultitouchEnabled)
                    return

                owner.issueStateTransition(
                    STATE_MULTIPLE_FINGERS_PRESSING,
                    event, target, context)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mStartFocusX = focusX
                mStartFocusY = focusY
            }

            MotionEvent.ACTION_DOWN -> {
                // Hold events.
                if (mPreviousDownEvent != null) {
                    mPreviousDownEvent!!.recycle()
                    mPreviousDownEvent = null
                }
                mPreviousDownEvent = mCurrentDownEvent
                mCurrentDownEvent = MotionEvent.obtain(event)

                val isSingleFinger = downPointerCount == 1
                if (isSingleFinger || !mIsMultitouchEnabled) {
                    // Handle TAP (determine if it is a TAP gesture by waiting
                    // for a short period).
                    val hadTapMessage = owner.handler.hasMessages(MSG_TAP)
                    if (hadTapMessage) {
                        // Remove unhandled tap.
                        owner.handler.removeMessages(MSG_TAP)
                    }

                    // Handle long-press.
                    if (isLongPressEnabled) {
                        // Remove unhandled long-press.
                        owner.handler.removeMessages(MSG_LONG_PRESS)
                        owner.handler.sendMessageAtTime(
                            obtainMessageWithPayload(
                                MSG_LONG_PRESS, event, target, context),
                            event.downTime + mTapTimeout + mLongPressTimeout)
                    }

                    // Hold start x and y.
                    mStartFocusX = focusX
                    mStartFocusY = focusY
                } else {
                    // Transit to new state.
                    owner.issueStateTransition(
                        STATE_MULTIPLE_FINGERS_PRESSING,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = (focusX - mStartFocusX).toInt()
                val deltaY = (focusY - mStartFocusY).toInt()
                val distance = deltaX * deltaX + deltaY * deltaY

                if (distance > mTouchSlopSquare) {
                    // Transit to new state.
                    owner.issueStateTransition(
                        STATE_DRAG,
                        event, target, context)
                }
            }

            MotionEvent.ACTION_UP -> {
                // Hold up event.
                if (mCurrentUpEvent != null) {
                    mCurrentUpEvent!!.recycle()
                }
                mCurrentUpEvent = MotionEvent.obtain(event)

                if (mHadLongPress) {
                    // Transit to IDLE state.
                    owner.issueStateTransition(
                        STATE_IDLE, event, target, context)
                } else {
                    // Two continuous taps far apart doesn't count as a double tap.
                    if (isConsideredCloseTap(mPreviousDownEvent, mCurrentDownEvent)) {
                        ++mTapCount
                    }

                    // Because it is a TAP, cancel the upcoming long-press delay.
                    cancelLongPress()

                    // Defer the transition to IDLE state.
                    owner.handler.sendMessageDelayed(
                        obtainMessageWithPayload(MSG_TAP,
                                                 event,
                                                 target,
                                                 context),
                        mTapTimeout)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                // Transit to IDLE state.
                owner.issueStateTransition(STATE_IDLE,
                                                 event,
                                                 target,
                                                 context)
            }
        }
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
        val action = event.actionMasked

        // Cancel tap.
        owner.handler.removeMessages(MSG_TAP)
        // Cancel long-press.
        if (isLongPressEnabled) {
            owner.handler.removeMessages(MSG_LONG_PRESS)
        }

        // Dispatch tap callback.
        if (action == MotionEvent.ACTION_UP) {
            val clone = obtainMyMotionEvent(mCurrentUpEvent!!)

            if (isLongPressEnabled && mHadLongPress) {
                owner.listener.onLongTap(
                    clone, mTouchingObject, mTouchingContext)
            } else if (isTapEnabled && mTapCount > 0) {
                if (mTapCount == 1) {
                    owner.listener.onSingleTap(
                        clone, mTouchingObject, mTouchingContext)
                } else if (mTapCount == 2) {
                    owner.listener.onDoubleTap(
                        clone, mTouchingObject, mTouchingContext)
                } else {
                    owner.listener.onMoreTap(
                        clone, mTouchingObject, mTouchingContext, mTapCount)
                }
            }
        }

        // Recycle hold events.
        if (mPreviousDownEvent != null) {
            mPreviousDownEvent!!.recycle()
            mPreviousDownEvent = null
        }
        if (mCurrentDownEvent != null) {
            mCurrentDownEvent!!.recycle()
            mCurrentDownEvent = null
        }
        if (mCurrentUpEvent != null) {
            mCurrentUpEvent!!.recycle()
            mCurrentUpEvent = null
        }
    }

    override fun onHandleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_LONG_PRESS -> {
                val payload = msg.obj as BaseGestureState.MyMessagePayload

                // Notify the detector that it's NOT a tap anymore.
                cancelTaps()
                mHadLongPress = true

                if (isLongPressEnabled && owner.listener != null) {
                    owner.listener.onLongPress(
                        payload.event,
                        payload.target,
                        payload.context)
                }
                return true
            }

            MSG_TAP -> {
                // Transit to IDLE state.
                // Note: the IDLE state would received a recycled hold-down-event.
                mCurrentUpEvent?.let { event ->
                    owner.issueStateTransition(
                        STATE_IDLE, event,
                        mTouchingObject, mTouchingContext)
                }
                return true
            }

            else -> return false
        }
    }

    fun setIsTransitionToMultiTouchEnabled(enabled: Boolean) {
        mIsMultitouchEnabled = enabled
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private fun isConsideredCloseTap(previousDown: MotionEvent?,
                                     currentDown: MotionEvent?): Boolean {
        if (currentDown == null) {
            // The current down event might be null because the state is entered
            // from ACTION_POINTER_UP action.
            return false
        } else if (previousDown == null) {
            // The previous down event is null only when the state is entered from
            // ACTION_DOWN action.
            return true
        }

        //            final long deltaTime = currentUp.getEventTime() - previousUp.getEventTime();
        //            if (deltaTime > TAP_TIMEOUT) {
        //                // The previous-up is too long ago, it is definitely a new tap!
        //                return false;
        //            }

        val deltaX = currentDown.x.toInt() - previousDown.x.toInt()
        val deltaY = currentDown.y.toInt() - previousDown.y.toInt()
        return deltaX * deltaX + deltaY * deltaY < mTapSlopSquare
    }

    private fun cancelLongPress() {
        owner.handler.removeMessages(MSG_LONG_PRESS)

        mHadLongPress = false
    }

    private fun cancelTaps() {
        owner.handler.removeMessages(MSG_TAP)
    }

    companion object {

        private const val MSG_TAP = 0xA1
        private const val MSG_LONG_PRESS = 0xA2
    }
}
