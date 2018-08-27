// Copyright Feb 2017-present CardinalBlue
//
// Author: boy@cardinalblue.com
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

package com.cardinalblue.gesture.state

import android.os.Message
import android.view.MotionEvent

import com.cardinalblue.gesture.IGestureStateOwner
import com.cardinalblue.gesture.ShadowMotionEvent

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

    protected fun obtainMyMotionEvent(event: MotionEvent): ShadowMotionEvent {
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
            ShadowMotionEvent(event.actionMasked,
                              downXs, downYs)

        } else {
            ShadowMotionEvent(event.actionMasked,
                              downXs, downYs)
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

    internal class MyMessagePayload(val event: ShadowMotionEvent,
                                    val target: Any?,
                                    val context: Any?)
}
