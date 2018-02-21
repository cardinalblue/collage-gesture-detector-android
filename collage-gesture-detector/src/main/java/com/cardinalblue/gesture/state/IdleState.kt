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

package com.cardinalblue.gesture.state

import android.os.Message
import android.view.MotionEvent

import com.cardinalblue.gesture.IGestureStateOwner

import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_MULTIPLE_FINGERS_PRESSING
import com.cardinalblue.gesture.IGestureStateOwner.State.STATE_SINGLE_FINGER_PRESSING

class IdleState(owner: IGestureStateOwner)
    : BaseGestureState(owner) {

    private var mIsMultitouchEnabled = true

    override fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        owner.listener?.onActionEnd(obtainMyMotionEvent(event),
                                    target,
                                    context)
    }

    override fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?) {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val isSingleFinger = event.pointerCount == 1

                if (isSingleFinger || !mIsMultitouchEnabled) {
                    owner.issueStateTransition(
                        STATE_SINGLE_FINGER_PRESSING,
                        event, target, context)
                } else {
                    owner.issueStateTransition(
                        STATE_MULTIPLE_FINGERS_PRESSING,
                        event, target, context)
                }
            }
        }
    }

    override fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?) {
        owner.listener?.onActionBegin(obtainMyMotionEvent(event),
                                      target,
                                      context)
    }

    override fun onHandleMessage(msg: Message): Boolean {
        return true
    }

    fun setIsTransitionToMultiTouchEnabled(enabled: Boolean) {
        mIsMultitouchEnabled = enabled
    }
}
