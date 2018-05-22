// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
//         yolung.lu@cardinalblue.com
//         jack.huang@cardinalblue.com
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

import com.cardinalblue.gesture.state.*

class GesturePolicy
internal constructor(stateOwner: IGestureStateOwner,
                     touchSlopSquare: Int = 0,
                     tapSlopSquare: Int = 0,
                     minFlingVelocity: Int = 0,
                     maxFlingVelocity: Int = 0) {

    // Policy mode.
    private var mPolicy: Int = ALL

    // Internal immutable states pool.
    private val mIdleStatePool: HashMap<Int, BaseGestureState> = HashMap()
    private val mSingleFingerPressingStatePool: HashMap<Int, BaseGestureState> = HashMap()
    private val mMultipleFingersPressingStatePool: HashMap<Int, BaseGestureState> = HashMap()
    private val mDragStatePool: HashMap<Int, BaseGestureState> = HashMap()
    private val mPinchStatePool: HashMap<Int, BaseGestureState> = HashMap()

    init {
        // Mode "ALL"
        mIdleStatePool[ALL] = IdleState(stateOwner)
        mSingleFingerPressingStatePool[ALL] = SingleFingerIdleState(
            stateOwner,
            tapSlopSquare.toLong(), touchSlopSquare.toLong(),
            GestureDetector.TAP_TIMEOUT.toLong(), GestureDetector.LONG_PRESS_TIMEOUT.toLong())
        mMultipleFingersPressingStatePool[ALL] = MultipleFingersIdleState(stateOwner, touchSlopSquare)
        mDragStatePool[ALL] = DragState(stateOwner, minFlingVelocity, maxFlingVelocity)
        mPinchStatePool[ALL] = PinchState(stateOwner)

        // Mode "DRAG_ONLY"
        mIdleStatePool[DRAG_ONLY] = IdleStateForDragOnly(stateOwner)
        mSingleFingerPressingStatePool[DRAG_ONLY] = SingleFingerIdleStateForDragOnly(
            stateOwner,
            touchSlopSquare.toLong())
        mDragStatePool[DRAG_ONLY] = DragStateForDragOnly(stateOwner, minFlingVelocity, maxFlingVelocity)
    }

    internal fun setPolicy(policy: Int) {
        when (policy) {
            ALL, DRAG_ONLY -> mPolicy = policy
            else -> throw IllegalArgumentException("Invalid given policy")
        }
    }

    internal fun getDefaultState(): BaseGestureState {
        return mIdleStatePool[ALL]!!
    }

    internal fun getNewState(newState: IGestureStateOwner.State): BaseGestureState {
        // TODO: Use a factor to produce reusable internal states.
        return when (newState) {
            IGestureStateOwner.State.STATE_IDLE -> getIdleState()
            IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING -> getSingleFingerPressingState()
            IGestureStateOwner.State.STATE_DRAG -> getDragState()
            IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING -> getMultipleFingersPressingState()
            IGestureStateOwner.State.STATE_PINCH -> getPinchState()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    private fun getIdleState(): BaseGestureState {
        return mIdleStatePool[mPolicy]!!
    }

    private fun getSingleFingerPressingState(): BaseGestureState {
        return mSingleFingerPressingStatePool[mPolicy]!!
    }

    private fun getDragState(): BaseGestureState {
        return mDragStatePool[mPolicy]!!
    }

    private fun getMultipleFingersPressingState(): BaseGestureState {
        return mMultipleFingersPressingStatePool[mPolicy]!!
    }

    private fun getPinchState(): BaseGestureState {
        return mPinchStatePool[mPolicy]!!
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    companion object {
        const val ALL: Int = 1
        const val DRAG_ONLY: Int = 1.shl(1)
    }
}
