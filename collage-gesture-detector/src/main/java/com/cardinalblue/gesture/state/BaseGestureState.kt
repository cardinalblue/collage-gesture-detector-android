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
import com.cardinalblue.gesture.MyMotionEvent

abstract class BaseGestureState internal constructor(
    private val mOwner: IGestureStateOwner) {

    protected val owner: IGestureStateOwner get() = mOwner

    abstract fun onEnter(event: MotionEvent,
                         target: Any?,
                         context: Any?)

    abstract fun onDoing(event: MotionEvent,
                         target: Any?,
                         context: Any?)

    abstract fun onExit(event: MotionEvent,
                        target: Any?,
                        context: Any?)

    abstract fun onHandleMessage(msg: Message): Boolean

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    protected fun obtainMyMotionEvent(event: MotionEvent): MyMotionEvent {
        val action = event.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val upIndex = if (pointerUp) event.actionIndex else -1
        val downPointerCount = event.pointerCount - if (pointerUp) 1 else 0

        // Prepare the down x and y;
        val downXs = FloatArray(downPointerCount)
        val downYs = FloatArray(downPointerCount)
        var i = 0
        var j = 0
        while (i < event.pointerCount) {
            if (i == upIndex) {
                ++i
                continue
            }

            downXs[j] = event.getX(i)
            downYs[j] = event.getY(i)

            ++j
            ++i
        }

        return if (pointerUp) {
            MyMotionEvent(event.actionMasked,
                          downXs, downYs,
                          true,
                          event.getX(upIndex),
                          event.getY(upIndex))

        } else {
            MyMotionEvent(event.actionMasked,
                          downXs, downYs,
                          false, 0f, 0f)
        }
    }

    private fun obtainMessagePayload(event: MotionEvent,
                                     target: Any?,
                                     context: Any?): MyMessagePayload {
        val eventClone = obtainMyMotionEvent(event)

        return MyMessagePayload(eventClone,
                                target,
                                context)
    }

    internal fun obtainMessageWithPayload(what: Int,
                                          event: MotionEvent,
                                          target: Any?,
                                          context: Any?): Message {
        val msg = mOwner.handler.obtainMessage(what)
        msg.obj = obtainMessagePayload(event,
                                       target,
                                       context)

        return msg
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    internal class MyMessagePayload(val event: MyMotionEvent,
                                    val target: Any?,
                                    val context: Any?)
}
