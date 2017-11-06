Gesture Detector
===

The system built-in `GestureDetector` has no concept of gesture lifecycle. We enclose gesture callbacks with lifecycle so that you could something in the **onActionBegin** and do anothing in the **onActionEnd** callbacks.

The State Diagram
---

![state diagram](docs/figure-state-paradigm.jpg)

Recognized Gestures
---

```
void onActionBegin();

void onActionEnd();

void onSingleTap();

void onDoubleTap();

void onMoreTap();

void onLongTap();

void onLongPress();

// Drag ///////////////////////////////////////////////////////////////

boolean onDragBegin();

void onDrag();

void onDragEnd();

// Fling //////////////////////////////////////////////////////////////

boolean onDragFling();

// Pinch //////////////////////////////////////////////////////////////

boolean onPinchBegin();

void onPinch();

void onPinchEnd();
```

Checkout the details in the code, [interface link](library/src/main/java/com/cardinalblue/gesture/IGestureListener.java).