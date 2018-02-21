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

package com.cardinalblue.gesture

import android.os.Handler
import android.view.MotionEvent

interface IGestureStateOwner {

    val handler: Handler

    var listener: IGestureListener?

    // All recognized states.
    enum class State {
        STATE_IDLE,

        STATE_SINGLE_FINGER_PRESSING,
        STATE_DRAG,

        STATE_MULTIPLE_FINGERS_PRESSING,
        STATE_PINCH
    }

    fun issueStateTransition(newState: State,
                             event: MotionEvent,
                             target: Any?,
                             context: Any?)
}
