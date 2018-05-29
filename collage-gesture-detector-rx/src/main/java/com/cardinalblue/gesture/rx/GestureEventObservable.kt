// Copyright May 2018-present CardinalBlue
//
// Author: boy@cardinalblue.com,
//         prada@cardinalblue.com
//         raymond.wu@cardinalblue.com
//         shih.yao@cardinalblue.com
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

package com.cardinalblue.gesture.rx

import android.graphics.PointF
import com.cardinalblue.gesture.GestureDetector
import com.cardinalblue.gesture.IAllGesturesListener
import com.cardinalblue.gesture.MyMotionEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * RxJava layer for [GestureDetector].
 */
class GestureEventObservable(gestureDetector: GestureDetector,
                             threadVerifier: IThreadVerifier? = null)
    : Observable<GestureEvent>() {

    private val mGestureDetector = gestureDetector
    private val mThreadVerifier: IThreadVerifier? = threadVerifier

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        mThreadVerifier?.ensureMainThread()

        val d = DisposableListener(detector = mGestureDetector,
                                   observer = observer)

        observer.onSubscribe(d)

        mGestureDetector.tapGestureListener = d
        mGestureDetector.dragGestureListener = d
        mGestureDetector.pinchGestureListener = d
    }

    private class DisposableListener(val detector: GestureDetector,
                                     val observer: Observer<in GestureEvent>)
        : Disposable,
          IAllGesturesListener {

        private val disposed = AtomicBoolean(false)

        override fun isDisposed(): Boolean {
            return disposed.get()
        }

        override fun dispose() {
            detector.tapGestureListener = null
            detector.dragGestureListener = null
            detector.pinchGestureListener = null
        }

        override fun onActionBegin(event: MyMotionEvent,
                                   target: Any?,
                                   context: Any?) {
            observer.onNext(TouchBeginEvent(event = event,
                                            target = target,
                                            context = context))
        }

        override fun onActionEnd(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            observer.onNext(TouchEndEvent(event = event,
                                          target = target,
                                          context = context))
        }

        override fun onSingleTap(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            observer.onNext(TapEvent(event = event,
                                     target = target,
                                     context = context,
                                     taps = 1))
        }

        override fun onDoubleTap(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            observer.onNext(TapEvent(event = event,
                                     target = target,
                                     context = context,
                                     taps = 2))
        }

        override fun onMoreTap(event: MyMotionEvent,
                               target: Any?,
                               context: Any?,
                               tapCount: Int) {
            observer.onNext(TapEvent(event = event,
                                     target = target,
                                     context = context,
                                     taps = tapCount))
        }

        override fun onLongTap(event: MyMotionEvent,
                               target: Any?,
                               context: Any?) {
            observer.onNext(LongTapEvent(event = event,
                                         target = target,
                                         context = context))
        }

        override fun onLongPress(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            observer.onNext(LongPressEvent(event = event,
                                           target = target,
                                           context = context))
        }

        override fun onDragBegin(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            observer.onNext(DragBeginEvent(event = event,
                                           target = target,
                                           context = context))
        }

        override fun onDrag(event: MyMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointer: PointF,
                            stopPointer: PointF) {
            observer.onNext(OnDragEvent(event = event,
                                        target = target,
                                        context = context,
                                        startPointer = startPointer,
                                        stopPointer = stopPointer))
        }

        override fun onDragEnd(event: MyMotionEvent,
                               target: Any?,
                               context: Any?,
                               startPointer: PointF,
                               stopPointer: PointF) {
            observer.onNext(DragEndEvent(event = event,
                                         target = target,
                                         context = context,
                                         startPointer = startPointer,
                                         stopPointer = stopPointer))
        }

        override fun onDragFling(event: MyMotionEvent,
                                 target: Any?,
                                 context: Any?,
                                 startPointer: PointF,
                                 stopPointer: PointF,
                                 velocityX: Float,
                                 velocityY: Float) {
            observer.onNext(DragFlingEvent(event = event,
                                           target = target,
                                           context = context,
                                           startPointer = startPointer,
                                           stopPointer = stopPointer,
                                           velocityX = velocityX,
                                           velocityY = velocityY))
        }

        override fun onPinchBegin(event: MyMotionEvent,
                                  target: Any?,
                                  context: Any?,
                                  startPointers: Array<PointF>) {
            observer.onNext(PinchBeginEvent(event = event,
                                            target = target,
                                            context = context,
                                            startPointers = startPointers))
        }

        override fun onPinch(event: MyMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointers: Array<PointF>,
                             stopPointers: Array<PointF>) {
            observer.onNext(OnPinchEvent(event = event,
                                         target = target,
                                         context = context,
                                         startPointers = startPointers,
                                         stopPointers = stopPointers))
        }

        override fun onPinchFling(event: MyMotionEvent,
                                  target: Any?,
                                  context: Any?) {
            observer.onNext(PinchFlingEvent(event = event,
                                            target = target,
                                            context = context))
        }

        override fun onPinchEnd(event: MyMotionEvent,
                                target: Any?,
                                context: Any?,
                                startPointers: Array<PointF>,
                                stopPointers: Array<PointF>) {
            observer.onNext(PinchEndEvent(event = event,
                                          target = target,
                                          context = context,
                                          startPointers = startPointers,
                                          stopPointers = stopPointers))
        }
    }
}
