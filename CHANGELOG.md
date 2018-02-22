Revisions
===

2.3.0
---

- Pending policy is consumed only when ACTION_DOWN is happening.

2.2.0
---

- Introduce flexible state transition management by GesturePolicy. (1cb631ce81aee9e612fb9bb7b96d09ec14a9136c)
- Switching the focus dragging finger will kill and respawn the dragging state under the DRAG_ONLY policy.
- Care focus pointer movement to determine whether transit to DRAG state under DRAG_ONLY policy.

2.1.0
---

- Break down original GOD gesture listener to *TAP*, *DRAG*, and *PINCH* gesture listeners.

2.0.0
---

- Convert all Java code to Kotlin. (67bbe189117ff318b95dbd596b86cc3452afbefe)
- Can register listener anytime rather than in the constructor. (2774b7b3d65e007ded505a56c1df679d88fec176)

1.1.0
---

- Configurable gesture recognizition. (8b1522b4b9550af0ca5ec4007a20cf2c0c3b464d)

1.0.0
---

- Support *TAP*, *DRAG*, and *PINCH* gestures, see [link](https://github.com/cardinalblue/collage-gesture-detector-android/blob/v1.0.0/collage-gesture-detector/src/main/java/com/cardinalblue/gesture/IGestureListener.java)