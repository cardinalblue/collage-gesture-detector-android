Release Process
===

### Step 1

Update the `library_version` configuration in the `collage-gesture-detector/deploy.gradle` file.

For example:

```
ext {
    library_version = 'x.x.x'
}
```

Then commit the change with message like, `Bump to x.x.x`

### Step 2

Tag and submit the tag to GitHub, for example:

```
git tag vX.X.X
git push origin master --tag

```

### Step 3

Deploy to Bintray and JCenter by running the following command:

```
./gradlew clean build collage-gesture-detector:bintrayUpload
```

### Step 4

Once the new release is updated to the GitHub, go to the release page and update the change log of the new release manually.