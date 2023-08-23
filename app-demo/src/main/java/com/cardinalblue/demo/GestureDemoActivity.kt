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

package com.cardinalblue.demo

import android.graphics.PointF
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.cardinalblue.demo.view.DemoView
import com.cardinalblue.gesture.*
import com.cardinalblue.gesture.PointerUtils.DELTA_RADIANS
import com.cardinalblue.gesture.PointerUtils.DELTA_SCALE_X
import com.cardinalblue.gesture.PointerUtils.DELTA_X
import com.cardinalblue.gesture.PointerUtils.DELTA_Y
import com.cardinalblue.gesture.rx.*
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class GestureDemoActivity : AppCompatActivity() {

    private val mLog: MutableList<String> = mutableListOf()

    // View.
    private val mCanvasView by lazy { findViewById<DemoView>(R.id.canvas) }
    private val mTxtLog by lazy { findViewById<TextView>(R.id.txt_gesture_test) }
    private val mBtnClearLog by lazy { findViewById<ImageView>(R.id.btn_clear) }
    private val mBtnEnableTap by lazy { findViewById<SwitchCompat>(R.id.toggle_tap) }
    private val mBtnEnableDrag by lazy { findViewById<SwitchCompat>(R.id.toggle_drag) }
    private val mBtnEnablePinch by lazy { findViewById<SwitchCompat>(R.id.toggle_pinch) }
    private val mBtnPolicyAll by lazy { findViewById<RadioButton>(R.id.opt_all) }
    private val mBtnPolicyDragOnly by lazy { findViewById<RadioButton>(R.id.opt_drag_only) }

    // Collage gesture detector
    private val mGestureDetector: GestureDetector by lazy {
        GestureDetector(Looper.getMainLooper(),
                        ViewConfiguration.get(this@GestureDemoActivity),
                        resources.getDimension(R.dimen.touch_slop),
                        resources.getDimension(R.dimen.tap_slop),
                        resources.getDimension(R.dimen.fling_min_vec),
                        resources.getDimension(R.dimen.fling_max_vec))
    }

    // Disposables.
    private val mDisposablesOnCreate = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_gesture_editor)

        // Bind view.
        mDisposablesOnCreate.add(
            RxView.clicks(mBtnClearLog)
                .subscribe { _ ->
                    clearLog()
                })
        mDisposablesOnCreate.add(
            RxView.touches(mCanvasView)
                .subscribe { event ->
                    mGestureDetector.onTouchEvent(event, mCanvasView, null)
                })
        mDisposablesOnCreate.add(
            GestureDetectorObservable(gestureDetector = mGestureDetector,
                                      threadVerifier = ThreadVerifierAndroidImpl())
                .compose(dispatchGestureEvent)
                .subscribe())
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnableTap)
                .startWith(mBtnEnableTap.isChecked)
                .subscribe { checked ->
                    mGestureDetector.tapGestureEnabled = checked
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnableDrag)
                .startWith(mBtnEnableDrag.isChecked)
                .subscribe { checked ->
                    mGestureDetector.dragGestureEnabled = checked
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnablePinch)
                .startWith(mBtnEnablePinch.isChecked)
                .subscribe { checked ->
                    mGestureDetector.pinchGestureEnabled = checked
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnPolicyAll)
                .startWith(mBtnPolicyAll.isChecked)
                .subscribe { checked ->
                    if (!checked) return@subscribe
                    mGestureDetector.setPolicy(GesturePolicy.ALL)
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnPolicyDragOnly)
                .startWith(mBtnPolicyDragOnly.isChecked)
                .subscribe { checked ->
                    if (!checked) return@subscribe
                    mGestureDetector.setPolicy(GesturePolicy.DRAG_ONLY)
                })
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unbind view.
        mDisposablesOnCreate.clear()
    }

    private val dispatchGestureEvent = ObservableTransformer<GestureObservable, Any> { upstream ->
        upstream.flatMap { eventSrc ->
            when (eventSrc) {
                is GestureLifecycleObservable -> eventSrc.compose(handleGestureLifecycle)
                is TapGestureObservable -> eventSrc.compose(handleTapGesture)
                is DragGestureObservable -> eventSrc.compose(handleDragGesture)
                is PinchGestureObservable -> eventSrc.compose(handlePinchGesture)
            }
        }
    }

    private val handleGestureLifecycle = ObservableTransformer<GestureEvent, Any> { upstream ->
        upstream.flatMap { event ->
            event as GestureLifecycleEvent
            when (event) {
                is TouchBeginEvent -> {
                    printLog("--------------")
                    printLog("⬇onTouchBegin")
                }
                is TouchEndEvent -> {
                    printLog("⬆onTouchEnd")

                    mCanvasView.resetDemo()
                }
                else -> {}
            }

            Observable.empty<Any>()
        }
    }

    private val handleTapGesture = ObservableTransformer<GestureEvent, Any> { upstream ->
        upstream.flatMap { event ->
            event as SingleFingerEvent

            when (event) {
                is TapEvent -> {
                    when {
                        event.taps == 1 -> printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onSingleTap", 1))
                        event.taps == 2 -> printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onDoubleTap", 2))
                        else -> printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onMoreTap", event.taps))
                    }
                }
                is LongTapEvent -> {
                    printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onLongTap", 1))
                }
                is LongPressEvent -> {
                    printLog("\uD83D\uDD50 onLongPress")
                }
                else -> {}
            }

            Observable.empty<Any>()
        }
    }

    private val handleDragGesture = ObservableTransformer<GestureEvent, Any> { upstream ->
        upstream.flatMap { event ->
            event as SingleFingerEvent

            when (event) {
                is DragBeginEvent -> {
                    printLog("✍️ onDragBegin")

                    mCanvasView.startDragDemo()
                }
                is DragDoingEvent -> {
                    printLog("✍️ onDrag")

                    mCanvasView.dragDemo(PointF(event.startPointer.first,
                                                event.startPointer.second),
                                         PointF(event.stopPointer.first,
                                                event.stopPointer.second))
                }
                is DragFlingEvent -> {
                    printLog("✍ \uD83C\uDFBC onDragFling vx=%.3f, vy=%.3f".format(event.velocityX, event.velocityY))
                }
                is DragEndEvent -> {
                    printLog("✍️ onDragEnd")

                    mCanvasView.stopDragDemo()
                }
                else -> {}
            }

            Observable.empty<Any>()
        }
    }

    private val handlePinchGesture = ObservableTransformer<GestureEvent, Any> { upstream ->
        upstream.flatMap { event ->
            event as TwoFingersEvent

            when (event) {
                is PinchBeginEvent -> {
                    printLog("\uD83D\uDD0D onPinchBegin")

                    mCanvasView.startPinchDemo()
                }
                is PinchDoingEvent -> {
                    val startPointers = Array(event.startPointers.size) { i ->
                        PointF(event.startPointers[i].first,
                               event.startPointers[i].second)
                    }
                    val stopPointers = Array(event.startPointers.size) { i ->
                        PointF(event.stopPointers[i].first,
                               event.stopPointers[i].second)
                    }
                    val transform = PointerUtils.getTransformFromPointers(startPointers, stopPointers)

                    printLog(String.format(Locale.ENGLISH,
                                           "\uD83D\uDD0D onPinch: " +
                                           "dx=%.1f, dy=%.1f, " +
                                           "ds=%.2f, " +
                                           "dr=%.2f",
                                           transform[DELTA_X], transform[DELTA_Y],
                                           transform[DELTA_SCALE_X],
                                           transform[DELTA_RADIANS]))

                    mCanvasView.pinchDemo(startPointers, stopPointers)
                }
                is PinchFlingEvent -> {
                    printLog("\uD83D\uDD0D onPinchFling")
                }
                is PinchEndEvent -> {
                    printLog("\uD83D\uDD0D onPinchEnd")

                    mCanvasView.stopPinchDemo()
                }
                else -> {}
            }

            Observable.empty<Any>()
        }
    }

    private fun ensureUiThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalThreadStateException(
                "Callback should be triggered in the UI thread")
        }
    }

    private fun printLog(msg: String) {
        mLog.add(msg)
        while (mLog.size > 32) {
            mLog.removeAt(0)
        }

        val builder = StringBuilder()
        mLog.forEach { line ->
            builder.append(line)
            builder.append("\n")
        }

        mTxtLog.text = builder.toString()
    }

    private fun clearLog() {
        mLog.clear()
        mTxtLog.text = getString(R.string.tap_anywhere_to_start)
    }
}
