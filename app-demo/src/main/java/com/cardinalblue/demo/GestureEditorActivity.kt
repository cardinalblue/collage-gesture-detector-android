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
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import com.cardinalblue.gesture.*
import com.cardinalblue.gesture.PointerUtils.DELTA_RADIANS
import com.cardinalblue.gesture.PointerUtils.DELTA_SCALE_X
import com.cardinalblue.gesture.PointerUtils.DELTA_X
import com.cardinalblue.gesture.PointerUtils.DELTA_Y
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class GestureEditorActivity : AppCompatActivity(),
                              IAllGesturesListener {

    private val mLog: MutableList<String> = mutableListOf()

    // View.
    private val mTxtLog by lazy { findViewById<TextView>(R.id.text_gesture_test) }
    private val mBtnClearLog by lazy { findViewById<ImageView>(R.id.btn_clear) }
    private val mBtnEnableTap by lazy { findViewById<SwitchCompat>(R.id.toggle_tap) }
    private val mBtnEnableDrag by lazy { findViewById<SwitchCompat>(R.id.toggle_drag) }
    private val mBtnEnablePinch by lazy { findViewById<SwitchCompat>(R.id.toggle_pinch) }
    private val mBtnEnableMultipleFingers by lazy { findViewById<SwitchCompat>(R.id.toggle_multiple_fingers) }

    // Disposables.
    private val mDisposablesOnCreate = CompositeDisposable()

    private val mGestureDetector: GestureDetector by lazy {
        GestureDetector(this@GestureEditorActivity,
                        resources.getDimension(R.dimen.touch_slop),
                        resources.getDimension(R.dimen.tap_slop),
                        resources.getDimension(R.dimen.fling_min_vec),
                        resources.getDimension(R.dimen.fling_max_vec))
    }

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
            RxCompoundButton
                .checkedChanges(mBtnEnableTap)
                .startWith(mBtnEnableTap.isChecked)
                .subscribe { checked ->
                    mGestureDetector.tapGestureListener = if (checked)
                        this@GestureEditorActivity else null
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnableDrag)
                .startWith(mBtnEnableDrag.isChecked)
                .subscribe { checked ->
                    mGestureDetector.dragGestureListener = if (checked)
                        this@GestureEditorActivity else null
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnablePinch)
                .startWith(mBtnEnablePinch.isChecked)
                .subscribe { checked ->
                    mGestureDetector.pinchGestureListener = if (checked)
                        this@GestureEditorActivity else null
                })
        mDisposablesOnCreate.add(
            RxCompoundButton
                .checkedChanges(mBtnEnableMultipleFingers)
                .startWith(mBtnEnableMultipleFingers.isChecked)
                .subscribe { checked ->
                    mGestureDetector.setPolicy(if (checked) GesturePolicy.ALL else GesturePolicy.DRAG_ONLY)
                })
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unbind view.
        mDisposablesOnCreate.clear()

        // Gesture listener.
        mGestureDetector.tapGestureListener = null
        mGestureDetector.dragGestureListener = null
        mGestureDetector.pinchGestureListener = null
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            mGestureDetector.onTouchEvent(event, null, null)
        } ?: false
    }

    // GestureListener ----------------------------------------------------->

    override fun onActionBegin(event: MyMotionEvent,
                               target: Any?,
                               context: Any?) {
        printLog("--------------")
        printLog("⬇onActionBegin")
    }

    override fun onActionEnd(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("⬆onActionEnd")
    }

    override fun onSingleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onSingleTap", 1))
    }

    override fun onDoubleTap(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onDoubleTap", 2))
    }

    override fun onMoreTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           tapCount: Int) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onMoreTap", tapCount))
    }

    override fun onLongTap(event: MyMotionEvent,
                           target: Any?,
                           context: Any?) {
        printLog(String.format(Locale.ENGLISH, "\uD83D\uDD95 x%d onLongTap", 1))
    }

    override fun onLongPress(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("\uD83D\uDD50 onLongPress")
    }

    override fun onDragBegin(event: MyMotionEvent,
                             target: Any?,
                             context: Any?) {
        printLog("✍️ onDragBegin")
    }

    override fun onDrag(event: MyMotionEvent,
                        target: Any?,
                        context: Any?,
                        startPointer: PointF,
                        stopPointer: PointF) {
        // DO NOTHING.
        printLog("✍️ onDrag")
    }

    override fun onDragEnd(event: MyMotionEvent,
                           target: Any?,
                           context: Any?,
                           startPointer: PointF,
                           stopPointer: PointF) {
        printLog("✍️ onDragEnd")
    }

    override fun onDragFling(event: MyMotionEvent,
                             target: Any?,
                             context: Any?,
                             startPointer: PointF,
                             stopPointer: PointF,
                             velocityX: Float,
                             velocityY: Float) {
        printLog("✍ \uD83C\uDFBC onDragFling")
    }

    override fun onPinchBegin(event: MyMotionEvent,
                              target: Any?,
                              context: Any?,
                              startPointers: Array<PointF>) {
        printLog("\uD83D\uDD0D onPinchBegin")
    }

    override fun onPinch(event: MyMotionEvent,
                         target: Any?,
                         context: Any?,
                         startPointers: Array<PointF>,
                         stopPointers: Array<PointF>) {
        val transform = PointerUtils.getTransformFromPointers(startPointers,
                                                              stopPointers)

        printLog(String.format(Locale.ENGLISH,
                               "\uD83D\uDD0D onPinch: " +
                               "dx=%.1f, dy=%.1f, " +
                               "ds=%.2f, " +
                               "dr=%.2f",
                               transform[DELTA_X], transform[DELTA_Y],
                               transform[DELTA_SCALE_X],
                               transform[DELTA_RADIANS]))
    }

    override fun onPinchFling(event: MyMotionEvent,
                              target: Any?,
                              context: Any?) {
        printLog("\uD83D\uDD0D onPinchFling")
    }

    override fun onPinchEnd(event: MyMotionEvent,
                            target: Any?,
                            context: Any?,
                            startPointers: Array<PointF>,
                            stopPointers: Array<PointF>) {
        printLog("\uD83D\uDD0D onPinchEnd")
    }

    // GestureListener <- end -----------------------------------------------

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
