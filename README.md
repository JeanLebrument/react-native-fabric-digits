# react-native-fabric-digits

## Introduction

**Fabric Digits wrapper for React-Native.**

Special thanks to the following people/projects:
- [jpdriver](https://github.com/jpdriver) for his tutorial on [how to integrate Digits in a React-Native project](https://medium.com/p/getting-started-with-digits-and-react-native-f79b22439416).
- [thessem](https://github.com/thessem/react-native-fabric-digits) for providing the Android support
- [react-native-digits](https://github.com/fixt/react-native-digits) for helping me figure out how to package the Android version.
- [Corentin S.](http://stackoverflow.com/a/33563461) for providing the basis of the Android implementation in a Stack Overflow answer

## Before Installing

1. Open your React Native app in Xcode / Android Studio / similar
2. Install DigitsKit (the same way you would for non-React Native projects)

For iOS apps, you can install DigitsKit either through the [Fabric OS X app](https://fabric.io/downloads/apple) or [CocoaPods](https://fabric.io/kits/ios/digits/install)

For Android apps, you can install DigitsKit either through the [Fabric IDE plugin](https://fabric.io/downloads/android) or [Gradle](https://fabric.io/kits/android/digits/install)

## Install

`npm install JeanLebrument/react-native-fabric-digits --save`

## Either Link with RNPM... (Automatic)

1. `npm install rnpm -g --save`
2. `rnpm link react-native-fabric-digits`

Follow the usual Digits installation procedure on your project, including adding the frameworks and modifying any files. Don't forget about initializing Fabric using `with` in you native code!

## ...or Link (Manual)

### iOS procedure
1. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
2. Go to `node_modules` ➜ `react-native-fabric-digits` ➜ `ios` -> select the `RCTDigitsManager.xcodeproj`
3. Go to `Build Phases` tab of your project, select a target, open `Link Binary With Libraries`, click on `+` and add `libRCTDigitsManager.a`

### Android procedure

### In `settings.gradle`

Add to bottom:

```java
include ':react-native-fabric-digits'
project(':react-native-fabric-digits').projectDir = new File(settingsDir, '../node_modules/react-native-fabric-digits/android')
```

#### In `android/build.gradle`

```java
allprojects {
  repositories {
    mavenLocal()
    jcenter()
    maven { url 'https://maven.fabric.io/public' }   <--- ADD THIS
  }
}
```

#### In `android/app/build.gradle`

```java
dependencies {
  compile fileTree(dir: 'libs', include: ['*.jar'])
  compile 'com.android.support:appcompat-v7:23.0.0'
  compile 'com.facebook.react:react-native:0.14.+'
  compile project(':react-native-fabric-digits')          <--- ADD THIS
}
```

#### In `MainActivity.java`

```java
import com.proxima.RCTDigits.DigitsPackage;         <--- ADD THIS

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
        new MainReactPackage(),
        new DigitsPackage()                         <--- ADD THIS
      );
    }
 ```

#### In `AndroidManifest.xml`

Add this inside the `application` tag.

```xml
<meta-data
  android:name="io.Digits.ApiKey"
  android:value="YOUR_API_KEY" />
<meta-data
  android:name="io.Digits.ApiSecret"
  android:value="YOUR_API_SECRET" />
```


## Usage

This package provide two classes: `DigitsLoginButton` and `DigitsLogoutButton`. You should `render` the version that corresponds to the Log In state in your app.

Start with `DigitsLoginButton`, then when a user has successfully logged in, swap this out for `DigitsLogoutButton`.

Have a look at the Example, which illustrates all the features.

## FAQ

### What's Digits?
* please see https://get.digits.com. Digits is part of Fabric -- a suite of tools for building mobile apps from Twitter.

### Does Digits officially support React Native?
* Not directly on its own. Officially, DigitsKit only supports the native languages (Objective C, Swift, and Java).
* However, React Native has the ability to expose native modules to JavaScript components, which is exactly what this library does.

### Can I see the OAuth details that the DigitsAPI returns?
* In our example, the returned OAuth `response` is put into the component state.

### Can I see the phone number the user entered?
* Yes! Once a user has logged in, you can call `getSessionDetails()` to get the sessionId and phoneNumber.

## Licence

**MIT**
