Release Process
===

### Step 1

Tag and submit the tag to GitHub, for example:

```
git tag v0.0.0
git push origin master --tag

```

Once the new release is updated to the GitHub, go to the release page and update the change log of the new release manually.

### Step 2

Update the `library_version` configuration in the `collage-gesture-detector/deploy.gradle` file.

### Step 3

Deploy to Bintray and JCenter by running the following command:

```
./gradlew clean build collage-gesture-detector:bintrayUpload
```
