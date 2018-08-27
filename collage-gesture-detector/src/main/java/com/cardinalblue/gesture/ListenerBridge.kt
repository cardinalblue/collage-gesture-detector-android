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

import java.util.concurrent.CopyOnWriteArrayList

/**
 * The bridge manages the dispatch of [ITapGestureListener], [IDragGestureListener],
 * and [IPinchGestureListener]. By dispatching the callback, it manages a state
 * across the gesture lifecycle, which means you can't stop the [IDragGestureListener.onDrag]
 * from calling even if you disable the listener in the middle the gesture
 * dispatching going.
 */
internal class ListenerBridge : IAllGesturesListener {

    private val lifecycleListeners = CopyOnWriteArrayList<IGestureLifecycleListener>()

    fun addLifecycleListener(listener: IGestureLifecycleListener) {
        lifecycleListeners.add(listener)
    }

    fun removeLifecycleListener(listener: IGestureLifecycleListener) {
        lifecycleListeners.remove(listener)
    }

    fun removeAllLifecycleGestureListeners() {
        lifecycleListeners.clear()
    }

    internal var tapEnabled: Boolean = false
    internal var longPressEnabled: Boolean = false
    private val tapListeners = CopyOnWriteArrayList<ITapGestureListener>()

    fun addTapGestureListener(listener: ITapGestureListener) {
        tapListeners.add(listener)

        tapEnabled = true
        longPressEnabled = true
    }

    fun removeTapGestureListener(listener: ITapGestureListener) {
        tapListeners.remove(listener)
    }

    fun removeAllTapGestureListeners() {
        tapListeners.clear()
    }

    internal var dragEnabled: Boolean = false
    private val dragListeners = CopyOnWriteArrayList<IDragGestureListener>()

    fun addDragGestureListener(listener: IDragGestureListener) {
        dragListeners.add(listener)

        dragEnabled = true
    }

    fun removeDragGestureListener(listener: IDragGestureListener) {
        dragListeners.remove(listener)
    }

    fun removeAllDragGestureListeners() {
        dragListeners.clear()
    }

    internal var pinchEnabled: Boolean = false
    private val pinchListeners = CopyOnWriteArrayList<IPinchGestureListener>()

    fun addPinchGestureListener(listener: IPinchGestureListener) {
        pinchListeners.add(listener)

        pinchEnabled = true
    }

    fun removePinchGestureListener(listener: IPinchGestureListener) {
        pinchListeners.remove(listener)
    }

    fun removeAllPinchGestureListeners() {
        pinchListeners.clear()
    }

    override fun onTouchBegin(event: ShadowMotionEvent,
                              target: Any?,
                              context: Any?) {
        lifecycleListeners.forEach { it.onTouchBegin(event, target, context) }
    }

    override fun onTouchEnd(event: ShadowMotionEvent,
                            target: Any?,
                            context: Any?) {
        lifecycleListeners.forEach { it.onTouchEnd(event, target, context) }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Tap ////////////////////////////////////////////////////////////////////

    override fun onSingleTap(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?) {
        if (!tapEnabled) return
        tapListeners.forEach { it.onSingleTap(event, target, context) }
    }

    override fun onDoubleTap(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?) {
        if (!tapEnabled) return
        tapListeners.forEach { it.onDoubleTap(event, target, context) }
    }

    override fun onMoreTap(event: ShadowMotionEvent,
                           target: Any?,
                           context: Any?,
                           tapCount: Int) {
        if (!tapEnabled) return
        tapListeners.forEach { it.onMoreTap(event, target, context, tapCount) }
    }

    override fun onLongTap(event: ShadowMotionEvent,
                           target: Any?,
                           context: Any?) {
        if (!longPressEnabled || !tapEnabled) return
        tapListeners.forEach { it.onLongTap(event, target, context) }
    }

    override fun onLongPress(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?) {
        if (!longPressEnabled) return
        tapListeners.forEach { it.onLongPress(event, target, context) }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Drag ///////////////////////////////////////////////////////////////////

    private var ifHandleDrag = false

    override fun onDragBegin(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?) {
        // Remember the setting for the DRAG session.
        ifHandleDrag = dragEnabled
        if (!ifHandleDrag) return

        dragListeners.forEach { it.onDragBegin(event, target, context) }
    }

    override fun onDrag(event: ShadowMotionEvent,
                        target: Any?,
                        context: Any?,
                        startPointer: Pair<Float, Float>,
                        stopPointer: Pair<Float, Float>) {
        if (!ifHandleDrag) return

        dragListeners.forEach { it.onDrag(event, target, context,
                                          startPointer, stopPointer) }
    }

    override fun onDragFling(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointer: Pair<Float, Float>,
                             stopPointer: Pair<Float, Float>,
                             velocityX: Float,
                             velocityY: Float) {
        if (!ifHandleDrag) return

        dragListeners.forEach { it.onDragFling(event, target, context,
                                               startPointer, stopPointer,
                                               velocityX, velocityY) }
    }

    override fun onDragEnd(event: ShadowMotionEvent,
                           target: Any?,
                           context: Any?,
                           startPointer: Pair<Float, Float>,
                           stopPointer: Pair<Float, Float>) {
        if (!ifHandleDrag) return

        dragListeners.forEach { it.onDragEnd(event, target, context,
                                             startPointer, stopPointer) }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pinch //////////////////////////////////////////////////////////////////

    private var ifHandlePinch = false

    override fun onPinchBegin(event: ShadowMotionEvent,
                              target: Any?,
                              context: Any?,
                              startPointers: Array<Pair<Float, Float>>) {
        // Remember the setting for the PINCH session.
        ifHandlePinch = pinchEnabled
        if (!ifHandlePinch) return

        pinchListeners.forEach { it.onPinchBegin(event, target, context,
                                                 startPointers) }
    }

    override fun onPinch(event: ShadowMotionEvent,
                         target: Any?,
                         context: Any?,
                         startPointers: Array<Pair<Float, Float>>,
                         stopPointers: Array<Pair<Float, Float>>) {
        if (!ifHandlePinch) return

        pinchListeners.forEach { it.onPinch(event, target, context,
                                            startPointers, stopPointers) }
    }

    override fun onPinchFling(event: ShadowMotionEvent,
                              target: Any?,
                              context: Any?) {
        if (!ifHandlePinch) return

        pinchListeners.forEach { it.onPinchFling(event, target, context) }
    }

    override fun onPinchEnd(event: ShadowMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointers: Array<Pair<Float, Float>>,
                            stopPointers: Array<Pair<Float, Float>>) {
        if (!ifHandlePinch) return

        pinchListeners.forEach { it.onPinchEnd(event, target, context,
                                               startPointers, stopPointers) }
    }
}
