Release Process
===

Automation with CI
---

CI server will look into the git comment, and smartly deploy the library if it's a release comment.

```
git commit --allow-empty -m "bump x.x.x" && git push origin master
```

Manually
---

Deploy to Bintray and JCenter by running the following command:

```
# Gesture detctor
./gradlew clean build collage-gesture-detector:bintrayUpload -PversionName="x.x.x"
# Gesture detector with RxJava support
./gradlew clean build collage-gesture-detector-rx:bintrayUpload -PversionName="x.x.x"
```

Tag and submit the tag to GitHub, for example:

```
git tag vX.X.X
git push origin master --tag

```
