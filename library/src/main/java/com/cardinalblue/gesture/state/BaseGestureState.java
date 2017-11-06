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

package com.cardinalblue.gesture.state;

import android.os.Message;
import android.view.MotionEvent;

import com.cardinalblue.gesture.IGestureStateOwner;
import com.cardinalblue.gesture.MyMotionEvent;

public abstract class BaseGestureState {

    protected final IGestureStateOwner mOwner;

    public BaseGestureState(IGestureStateOwner owner) {
        mOwner = owner;
    }

    public abstract void onEnter(MotionEvent event,
                                 Object touchingObject,
                                 Object touchingContext);

    public abstract void onDoing(MotionEvent event,
                                 Object touchingObject,
                                 Object touchingContext);

    public abstract void onExit(MotionEvent event,
                                Object touchingObject,
                                Object touchingContext);

    public abstract boolean onHandleMessage(Message msg);

    ///////////////////////////////////////////////////////////////////////////
    // Protected / Private Methods ////////////////////////////////////////////

    protected MyMotionEvent obtainMyMotionEvent(MotionEvent event) {
        // TODO: Remember to pass x and y array.
        return new MyMotionEvent(event.getActionMasked(), null, null);
    }

    protected MyMessagePayload obtainMessagePayload(MotionEvent event,
                                                    Object touchingObject,
                                                    Object touchingContext) {
        // TODO: Remember to pass x and y array.
        MyMotionEvent eventClone = new MyMotionEvent(event.getActionMasked(), null, null);

        return new MyMessagePayload(eventClone,
                                    touchingObject,
                                    touchingContext);
    }

    protected Message obtainMessageWithPayload(int what,
                                               MotionEvent event,
                                               Object touchingObject,
                                               Object touchingContext) {
        final Message msg = mOwner.getHandler().obtainMessage(what);
        msg.obj = obtainMessagePayload(event,
                                       touchingObject,
                                       touchingContext);

        return msg;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Clazz //////////////////////////////////////////////////////////////////

    protected static class MyMessagePayload {

        final MyMotionEvent event;
        final Object touchingTarget;
        final Object touchingContext;

        MyMessagePayload(MyMotionEvent event,
                         Object touchingTarget,
                         Object touchingContext) {
            this.event = event;
            this.touchingTarget = touchingTarget;
            this.touchingContext = touchingContext;
        }
    }
}
