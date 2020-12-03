// Copyright Mar 2018-present CardinalBlue
//
// Author: boy@cardinalblue.com
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

package com.cardinalblue.demo.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cardinalblue.demo.R
import com.cardinalblue.gesture.PointerUtils

class DemoView : View {

    private var mDefaultColor = 0
    private var mShowDemo = false
    private val mDemoPaint = Paint()
    private val mDemoMatrix = Matrix()
    private val mDemoMatrixStart = Matrix()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {
        mDefaultColor = ContextCompat.getColor(context, R.color.black_80)

        mDemoPaint.color = mDefaultColor
        mDemoPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mShowDemo) {
            canvas.drawColor(Color.WHITE)

            val count = canvas.save()

            canvas.concat(mDemoMatrix)
            canvas.drawRect(0f, 0f,
                            width.toFloat(), height.toFloat(),
                            mDemoPaint)

            canvas.restoreToCount(count)
        } else {
            canvas.drawColor(mDefaultColor)
        }
    }

    fun resetDemo() {
        mDemoMatrix.reset()
        mDemoMatrixStart.reset()
        mShowDemo = false

        invalidate()
    }

    fun startDragDemo() {
        mShowDemo = true

        // Hold starting transform.
        mDemoMatrixStart.set(mDemoMatrix)

        invalidate()
    }

    fun dragDemo(startPointer: PointF,
                 stopPointer: PointF) {
        val dx = stopPointer.x - startPointer.x
        val dy = stopPointer.y - startPointer.y
        mDemoMatrix.set(mDemoMatrixStart)
        mDemoMatrix.postTranslate(dx, dy)

        invalidate()
    }

    fun stopDragDemo() {
        // DO NOTHING.
    }

    fun startPinchDemo() {
        mShowDemo = true

        // Hold starting transform.
        mDemoMatrixStart.set(mDemoMatrix)

        invalidate()
    }

    fun pinchDemo(startPointers: Array<PointF>,
                  stopPointers: Array<PointF>) {
        // Calculate the transformation.
        val transform = PointerUtils.getTransformFromPointers(startPointers, stopPointers)

        val dx = transform[PointerUtils.DELTA_X]
        val dy = transform[PointerUtils.DELTA_Y]
        val dScale = transform[PointerUtils.DELTA_SCALE_X]
        val dRadians = transform[PointerUtils.DELTA_RADIANS]
        val pivotX = transform[PointerUtils.PIVOT_X]
        val pivotY = transform[PointerUtils.PIVOT_Y]

        mDemoMatrix.set(mDemoMatrixStart)
        mDemoMatrix.postScale(dScale, dScale, pivotX, pivotY)
        mDemoMatrix.postRotate(Math.toDegrees(dRadians.toDouble()).toFloat(), pivotX, pivotY)
        mDemoMatrix.postTranslate(dx, dy)

        invalidate()
    }

    fun stopPinchDemo() {
        // DO NOTHING.
    }
}
