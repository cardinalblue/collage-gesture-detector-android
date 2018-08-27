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

package com.cardinalblue.gesture

data class ShadowMotionEvent(val maskedAction: Int,
                             val downXs: FloatArray,
                             val downYs: FloatArray,
                             val isUp: Boolean,
                             val upX: Float,
                             val upY: Float,
                             val downPointerCount: Int = Math.min(downXs.size, downYs.size),
                             val downFocusX: Float = kotlin.run {
                                 var sum = 0f
                                 for (i in 0..downPointerCount - 1) {
                                     sum += downXs[i]
                                 }
                                 sum / downPointerCount
                             },
                             val downFocusY: Float = kotlin.run {
                                 var sum = 0f
                                 for (i in 0..downPointerCount - 1) {
                                     sum += downYs[i]
                                 }
                                 sum / downPointerCount
                             })
