//  Copyright Oct 2017-present CardinalBlue
//
//  Author: boy@cardinalblue.com
//          jack.huang@cardinalblue.com
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

package com.cardinalblue.gesture;

public class MyMotionEvent {

    public final int maskedAction;

    public final int downPointerCount;
    public final float[] downXs;
    public final float[] downYs;

    public final boolean isUp;
    public final float upX;
    public final float upY;

    public MyMotionEvent(int maskedAction,
                         float[] downXs,
                         float[] downYs,
                         boolean isUp,
                         float upX,
                         float upY) {
        if (downXs == null || downYs == null) {
            throw new IllegalArgumentException("Invalid down x and y array.");
        } else if (downXs.length != downYs.length) {
            throw new IllegalArgumentException("Amount of down x is not consistent to y.");
        }

        this.maskedAction = maskedAction;

        this.downXs = downXs;
        this.downYs = downYs;
        this.downPointerCount = downXs.length;

        this.isUp = isUp;
        this.upX = upX;
        this.upY = upY;
    }
}
