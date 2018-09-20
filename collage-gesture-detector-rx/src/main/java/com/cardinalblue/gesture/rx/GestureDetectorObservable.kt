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

import com.cardinalblue.gesture.GestureDetector
import com.cardinalblue.gesture.IAllGesturesListener
import com.cardinalblue.gesture.ShadowMotionEvent
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.atomic.AtomicBoolean

/**
 * RxJava layer for [GestureDetector].
 */
class GestureDetectorObservable(private val gestureDetector: GestureDetector,
                                private val threadVerifier: IThreadVerifier? = null)
    : Observable<Observable<GestureEvent>>() {

    override fun subscribeActual(observer: Observer<in Observable<GestureEvent>>) {
        threadVerifier?.ensureMainThread()

        val d = DisposableListener(detector = gestureDetector,
                                   observer = observer)
        observer.onSubscribe(d)

        gestureDetector.addLifecycleListener(d)
        gestureDetector.addTapGestureListener(d)
        gestureDetector.addDragGestureListener(d)
        gestureDetector.addPinchGestureListener(d)
    }

    private class DisposableListener(val detector: GestureDetector,
                                     val observer: Observer<in Observable<GestureEvent>>)
        : Disposable,
          IAllGesturesListener {

        private val disposed = AtomicBoolean(false)

        @Volatile
        private var mDragSignal: BehaviorSubject<GestureEvent>? = null
        @Volatile
        private var mPinchSignal: BehaviorSubject<GestureEvent>? = null

        override fun isDisposed(): Boolean {
            return disposed.get()
        }

        override fun dispose() {
            detector.removeLifecycleListener(this)
            detector.removeTapGestureListener(this)
            detector.removeDragGestureListener(this)
            detector.removePinchGestureListener(this)
        }

        override fun onTouchBegin(event: ShadowMotionEvent,
                                  target: Any?,
                                  context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    TouchBeginEvent(rawEvent = event,
                                    target = target,
                                    context = context)))
            }
        }

        override fun onTouchEnd(event: ShadowMotionEvent,
                                target: Any?,
                                context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    TouchEndEvent(rawEvent = event,
                                  target = target,
                                  context = context)))
            }
        }

        override fun onSingleTap(event: ShadowMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    TapEvent(rawEvent = event,
                             target = target,
                             context = context,
                             downX = event.downFocusX,
                             downY = event.downFocusY,
                             taps = 1)))
            }
        }

        override fun onDoubleTap(event: ShadowMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    TapEvent(rawEvent = event,
                             target = target,
                             context = context,
                             downX = event.downFocusX,
                             downY = event.downFocusY,
                             taps = 2)))
            }
        }

        override fun onMoreTap(event: ShadowMotionEvent,
                               target: Any?,
                               context: Any?,
                               tapCount: Int) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    TapEvent(rawEvent = event,
                             target = target,
                             context = context,
                             downX = event.downFocusX,
                             downY = event.downFocusY,
                             taps = tapCount)))
            }
        }

        override fun onLongTap(event: ShadowMotionEvent,
                               target: Any?,
                               context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    LongTapEvent(rawEvent = event,
                                 target = target,
                                 context = context,
                                 downX = event.downFocusX,
                                 downY = event.downFocusY)))
            }
        }

        override fun onLongPress(event: ShadowMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            synchronized(this) {
                observer.onNext(Observable.just(
                    LongPressEvent(rawEvent = event,
                                   target = target,
                                   context = context,
                                   downX = event.downFocusX,
                                   downY = event.downFocusY)))
            }
        }

        override fun onDragBegin(event: ShadowMotionEvent,
                                 target: Any?,
                                 context: Any?) {
            synchronized(this) {
                val signal = BehaviorSubject.createDefault(
                    DragEvent(rawEvent = event,
                              target = target,
                              context = context,
                              startPointer = Pair(event.downFocusX,
                                                  event.downFocusY),
                              stopPointer = Pair(event.downFocusX,
                                                 event.downFocusY)) as GestureEvent)

                mDragSignal = signal

                observer.onNext(signal)
            }
        }

        override fun onDrag(event: ShadowMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointer: Pair<Float, Float>,
                            stopPointer: Pair<Float, Float>) {
            synchronized(this) {
                mDragSignal?.onNext(DragEvent(rawEvent = event,
                                              target = target,
                                              context = context,
                                              startPointer = startPointer,
                                              stopPointer = stopPointer))
            }
        }

        override fun onDragFling(event: ShadowMotionEvent,
                                 target: Any?,
                                 context: Any?,
                                 startPointer: Pair<Float, Float>,
                                 stopPointer: Pair<Float, Float>,
                                 velocityX: Float,
                                 velocityY: Float) {
            synchronized(this) {
                mDragSignal?.onNext(DragFlingEvent(rawEvent = event,
                                                   target = target,
                                                   context = context,
                                                   startPointer = startPointer,
                                                   stopPointer = stopPointer,
                                                   velocityX = velocityX,
                                                   velocityY = velocityY))
            }
        }

        override fun onDragEnd(event: ShadowMotionEvent,
                               target: Any?,
                               context: Any?,
                               startPointer: Pair<Float, Float>,
                               stopPointer: Pair<Float, Float>) {
            synchronized(this) {
                mDragSignal?.onNext(DragEvent(rawEvent = event,
                                              target = target,
                                              context = context,
                                              startPointer = startPointer,
                                              stopPointer = stopPointer))
                mDragSignal?.onComplete()

                // Clear reference
                mDragSignal = null
            }
        }

        override fun onPinchBegin(event: ShadowMotionEvent,
                                  target: Any?,
                                  context: Any?,
                                  startPointers: Array<Pair<Float, Float>>) {
            synchronized(this) {
                val signal = BehaviorSubject.createDefault(
                    PinchEvent(rawEvent = event,
                               target = target,
                               context = context,
                               startPointers = startPointers,
                               stopPointers = startPointers) as GestureEvent)

                mPinchSignal = signal

                observer.onNext(signal)
            }
        }

        override fun onPinch(event: ShadowMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointers: Array<Pair<Float, Float>>,
                             stopPointers: Array<Pair<Float, Float>>) {
            synchronized(this) {
                mPinchSignal?.onNext(PinchEvent(rawEvent = event,
                                                target = target,
                                                context = context,
                                                startPointers = startPointers,
                                                stopPointers = stopPointers))
            }
        }

        override fun onPinchFling(event: ShadowMotionEvent,
                                  target: Any?,
                                  context: Any?) {
            synchronized(this) {
                mPinchSignal?.onNext(PinchFlingEvent(rawEvent = event,
                                                     target = target,
                                                     context = context,
                                                     startPointers = emptyArray(),
                                                     stopPointers = emptyArray()))
            }
        }

        override fun onPinchEnd(event: ShadowMotionEvent,
                                target: Any?,
                                context: Any?,
                                startPointers: Array<Pair<Float, Float>>,
                                stopPointers: Array<Pair<Float, Float>>) {
            synchronized(this) {
                mPinchSignal?.onNext(PinchEvent(rawEvent = event,
                                                target = target,
                                                context = context,
                                                startPointers = startPointers,
                                                stopPointers = stopPointers))
                mPinchSignal?.onComplete()

                // Clear reference
                mPinchSignal = null
            }
        }
    }
}
