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
        final int action = event.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int upIndex = pointerUp ? event.getActionIndex() : -1;
        final int downPointerCount = event.getPointerCount() - (pointerUp ? 1 : 0);

        // Prepare the down x and y;
        final float[] downXs = new float[downPointerCount];
        final float[] downYs = new float[downPointerCount];
        for (int i = 0, j = 0; i < event.getPointerCount(); ++i) {
            if (i == upIndex) continue;

            downXs[j] = event.getX(i);
            downYs[j] = event.getY(i);

            ++j;
        }

        if (pointerUp) {
            return new MyMotionEvent(event.getActionMasked(),
                                     downXs, downYs,
                                     true,
                                     event.getX(upIndex),
                                     event.getY(upIndex));

        } else {
            return new MyMotionEvent(event.getActionMasked(),
                                     downXs, downYs,
                                     false, 0, 0);
        }
    }

    protected MyMessagePayload obtainMessagePayload(MotionEvent event,
                                                    Object touchingObject,
                                                    Object touchingContext) {
        MyMotionEvent eventClone = obtainMyMotionEvent(event);

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
