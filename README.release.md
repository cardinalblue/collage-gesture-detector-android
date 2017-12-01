Release Process
===

### Step 1

Update the `library_version` configuration in the `collage-gesture-detector/deploy.gradle` file.

### Step 2
Run the command

```
./gradlew clean build collage-gesture-detector:bintrayUpload
```