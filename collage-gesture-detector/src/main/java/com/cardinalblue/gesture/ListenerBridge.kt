// Copyright Feb 2018-present CardinalBlue
//
// Authors: boy@cardinalblue.com
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

import android.graphics.PointF

internal class ListenerBridge : IAllGesturesListener {

//    internal var lifecycleListener: IGestureLifecycleListener? = null
    internal var tapListener: ITapGestureListener? = null
    internal var dragListener: IDragGestureListener? = null
    internal var pinchListener: IPinchGestureListener? = null

    override fun onActionBegin(event: MyMotionEvent,
                               target: Any?,
                               context: Any?) {
        when {
            tapListener != null -> tapListener?.onActionBegin(event, target, context)
            dragListener != null -> dragListener?.onActionBegin(event, target, context)
            pinchListener != null -> pinchListener?.onActionBegin(event, target, context)
        }
    }

    override fun onActionEnd(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        when {
            tapListener != null -> tapListener?.onActionEnd(event, target, context)
            dragListener != null -> dragListener?.onActionEnd(event, target, context)
            pinchListener != null -> pinchListener?.onActionEnd(event, target, context)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tap ////////////////////////////////////////////////////////////////////

    override fun onSingleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        tapListener?.onSingleTap(event, target, context)
    }

    override fun onDoubleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        tapListener?.onDoubleTap(event, target, context)
    }

    override fun onMoreTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           tapCount: Int) {
        tapListener?.onMoreTap(event, target, context, tapCount)
    }

    override fun onLongTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?) {
        tapListener?.onLongTap(event, target, context)
    }

    override fun onLongPress(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        tapListener?.onLongPress(event, target, context)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drag ///////////////////////////////////////////////////////////////////

    override fun onDragBegin(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        dragListener?.onDragBegin(event, target, context)
    }

    override fun onDrag(event: MyMotionEvent,
                        target: Any?,
                        context: Any?,
                        startPointer: PointF,
                        stopPointer: PointF) {
        dragListener?.onDrag(event, target, context,
                             startPointer, stopPointer)
    }

    override fun onDragFling(event: MyMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointer: PointF,
                             stopPointer: PointF,
                             velocityX: Float,
                             velocityY: Float) {
        dragListener?.onDragFling(event, target, context,
                                  startPointer, stopPointer,
                                  velocityX, velocityY)
    }

    override fun onDragEnd(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           startPointer: PointF,
                           stopPointer: PointF) {
        dragListener?.onDragEnd(event, target, context,
                                startPointer, stopPointer)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pinch //////////////////////////////////////////////////////////////////

    override fun onPinchBegin(event: MyMotionEvent,
                              target: Any?,
                              context: Any?,
                              startPointers: Array<PointF>) {
        pinchListener?.onPinchBegin(event, target, context,
                                    startPointers)
    }

    override fun onPinch(event: MyMotionEvent,
                         target: Any?,
                         context: Any?,
                         startPointers: Array<PointF>,
                         stopPointers: Array<PointF>) {
        pinchListener?.onPinch(event, target, context,
                               startPointers, stopPointers)
    }

    override fun onPinchFling(event: MyMotionEvent,
                              target: Any?,
                              context: Any?) {
        pinchListener?.onPinchFling(event, target, context)
    }

    override fun onPinchEnd(event: MyMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointers: Array<PointF>,
                            stopPointers: Array<PointF>) {
        pinchListener?.onPinchEnd(event, target, context,
                                  startPointers, stopPointers)
    }
}
