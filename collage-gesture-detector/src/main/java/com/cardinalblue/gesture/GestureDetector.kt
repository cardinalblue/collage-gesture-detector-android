// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         jack.huang@cardinalblue.com
//         yolung.lu@cardinalblue.com
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

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.cardinalblue.gesture.state.BaseGestureState
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Creates a GestureDetector with the supplied listener.
 * You may only use this constructor from a [android.os.Looper] thread.
 *
 * @param viewConfig the application's viewConfig
 * @param touchSlop The range where a gesture is identified if the finger
 * movement is beyond.
 * @param tapSlop The range that a TAP is identified if the finger movement is
 * still within.
 * @param minFlingVec The lower bound of the finger movement for a FLING.
 * @param maxFlingVec The upper bound of the finger movement for a FLING.
 *
 * @see android.os.Handler
 */
class GestureDetector(uiLooper: Looper,
                      viewConfig: ViewConfiguration,
                      touchSlop: Float,
                      tapSlop: Float,
                      minFlingVec: Float,
                      maxFlingVec: Float)
    : Handler.Callback,
      IGestureStateOwner {

    private var mTouchSlopSquare: Int = 0
    private var mTapSlopSquare: Int = 0
    private var mMinFlingVelocity: Int = 0
    private var mMaxFlingVelocity: Int = 0

    // State owner properties.
    override val listener: IAllGesturesListener? = ListenerBridge()
    /**
     * A handler corresponding to the UI looper and it is for the children state
     * to dispatch the callbacks, e.g. long-press.
     */
    override val handler: Handler by lazy { GestureHandler(uiLooper, this) }

    // Policy.
    private var mPendingPolicy: Int = GesturePolicy.ALL
    private val mPolicy: GesturePolicy by lazy {
        GesturePolicy(this,
                      mTouchSlopSquare,
                      mTapSlopSquare,
                      mMinFlingVelocity,
                      mMaxFlingVelocity)
    }

    // Internal immutable states.
    private var mState: BaseGestureState? = null

    init {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException(
                "The detector should be always initialized on the main thread.")
        }

        // Init properties like touch slop square, tap slop square, ..., etc.
        val newTouchSlop = Math.min(touchSlop, viewConfig.scaledTouchSlop.toFloat())
        val newTapSlop = Math.min(tapSlop, viewConfig.scaledDoubleTapSlop.toFloat())

        mTouchSlopSquare = (newTouchSlop * newTouchSlop).toInt()
        mTapSlopSquare = (newTapSlop * newTapSlop).toInt()

        mMinFlingVelocity = Math.max(minFlingVec,
                                     viewConfig.scaledMinimumFlingVelocity
                                         .toFloat())
            .toInt()
        mMaxFlingVelocity = Math.max(maxFlingVec,
                                     viewConfig.scaledMaximumFlingVelocity
                                         .toFloat())
            .toInt()

        // Init IDLE state.
        mState = mPolicy.getDefaultState()
    }

    override fun issueStateTransition(newState: IGestureStateOwner.State,
                                      event: MotionEvent,
                                      target: Any?,
                                      context: Any?) {
        val oldState = mState
        oldState?.onExit(event, target, context)

        mState = mPolicy.getNewState(newState)

        // Enter new state.
        mState!!.onEnter(event, target, context)
    }

    override fun handleMessage(msg: Message): Boolean {
        // Delegate to state.
        return mState!!.onHandleMessage(msg)
    }

    /**
     * Change the set of recognized gestures, where [GesturePolicy.ALL] is for
     * recognizing TAP, DRAG, and PINCH gestures; [GesturePolicy.DRAG_ONLY] is
     * for DRAG gesture only. By default it is [GesturePolicy.ALL].
     */
    fun setPolicy(policy: Int) {
        mPendingPolicy = policy
    }

    fun addLifecycleListener(listener: IGestureLifecycleListener) {
        getListenerBridge().addLifecycleListener(listener)
    }

    fun removeLifecycleListener(listener: IGestureLifecycleListener) {
        getListenerBridge().removeLifecycleListener(listener)
    }

    var tapGestureEnabled: Boolean
        get() = getListenerBridge().tapEnabled
        set(value) {
            getListenerBridge().tapEnabled = value
        }

    var longPressGestureEnabled: Boolean
        get() = getListenerBridge().longPressEnabled
        set(value) {
            getListenerBridge().longPressEnabled = value
        }

    fun addTapGestureListener(listener: ITapGestureListener) {
        getListenerBridge().addTapGestureListener(listener)
    }

    fun removeTapGestureListener(listener: ITapGestureListener) {
        getListenerBridge().removeTapGestureListener(listener)
    }

    @SuppressWarnings("unused")
    fun removeAllTapGestureListeners() {
        getListenerBridge().removeAllTapGestureListeners()
    }

    var dragGestureEnabled: Boolean
        get() = getListenerBridge().dragEnabled
        set(value) {
            getListenerBridge().dragEnabled = value
        }

    fun addDragGestureListener(listener: IDragGestureListener) {
        getListenerBridge().addDragGestureListener(listener)
    }

    fun removeDragGestureListener(listener: IDragGestureListener) {
        getListenerBridge().removeDragGestureListener(listener)
    }

    @SuppressWarnings("unused")
    fun removeAllDragGestureListeners() {
        getListenerBridge().removeAllDragGestureListeners()
    }

    var pinchGestureEnabled: Boolean
        get() = getListenerBridge().pinchEnabled
        set(value) {
            getListenerBridge().pinchEnabled = value
        }
    var pinchGestureListeners = CopyOnWriteArrayList<IPinchGestureListener>()

    fun addPinchGestureListener(listener: IPinchGestureListener) {
        getListenerBridge().addPinchGestureListener(listener)
    }

    fun removePinchGestureListener(listener: IPinchGestureListener) {
        getListenerBridge().removePinchGestureListener(listener)
    }

    @SuppressWarnings("unused")
    fun removeAllPinchGestureListeners() {
        getListenerBridge().removeAllPinchGestureListeners()
    }

    /**
     * Analyzes the given motion event and if applicable triggers the
     * appropriate callbacks on the [IAllGesturesListener]
     * supplied.
     *
     * @param event The current motion event.
     * @return true if the [IAllGesturesListener] consumed
     * the event, else false.
     */
    fun onTouchEvent(event: MotionEvent,
                     target: Any?,
                     context: Any?): Boolean {
        val action = event.actionMasked
        // Consume the pending policy when ACTION_DOWN.
        when (action) {
            MotionEvent.ACTION_DOWN -> { mPolicy.setPolicy(mPendingPolicy) }
        }

        mState!!.onDoing(event, target, context)

        return true
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private fun getListenerBridge(): ListenerBridge {
        return this.listener as ListenerBridge
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    private class GestureHandler
    internal constructor(uiLooper: Looper,
                         callback: Handler.Callback)
        : Handler(uiLooper, callback)

    companion object {

        /**
         * Defines the default duration in milliseconds before a press turns into
         * a long press.
         */
        internal val LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout()
        /**
         * The duration in milliseconds we will wait to see if a touch event is a
         * tap or a scroll. If the user does not move within this interval, it is
         * considered to be a tap.
         */
        internal val TAP_TIMEOUT = Math.max(150, ViewConfiguration.getTapTimeout())
    }
}

