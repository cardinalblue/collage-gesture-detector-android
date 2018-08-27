// Copyright Aug 2018-present CardinalBlue
//
// Author: boyw165@gmail.com
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

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

sealed class GestureObservable(private val default: GestureEvent)
    : Observable<GestureEvent>() {

    private var observer: Observer<in GestureEvent>? = null

    internal fun offer(item: GestureEvent) {
        this.observer?.onNext(item)
    }

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        observer.onSubscribe(ActionDisposable {
            // Clear reference
            this.observer = null
        })
        observer.onNext(default)

        // Cache observer for offering something later
        this.observer = observer
    }

    internal class ActionDisposable(val lambda: () -> Unit) : Disposable {

        private val disposed = AtomicBoolean(false)

        override fun isDisposed(): Boolean {
            return disposed.get()
        }

        override fun dispose() {
            lambda()
            disposed.set(true)
        }
    }
}

class GestureLifecycleObservable(default: GestureLifecycleEvent)
    : GestureObservable(default) {

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        println("${LibraryConst.TAG}: GestureLifecycleObservable is subscribed")
        super.subscribeActual(observer)
    }
}

class TapGestureObservable(default: SingleFingerEvent)
    : GestureObservable(default) {

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        println("${LibraryConst.TAG}: TapGestureObservable is subscribed")
        super.subscribeActual(observer)
    }
}

class DragGestureObservable(default: SingleFingerEvent)
    : GestureObservable(default) {

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        println("${LibraryConst.TAG}: DragGestureObservable is subscribed")
        super.subscribeActual(observer)
    }
}

class PinchGestureObservable(default: TwoFingersEvent)
    : GestureObservable(default) {

    override fun subscribeActual(observer: Observer<in GestureEvent>) {
        println("${LibraryConst.TAG}: PinchGestureObservable is subscribed")
        super.subscribeActual(observer)
    }
}
