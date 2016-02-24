# react-native-fabric-digits

## Introduction

**Fabric Digits wrapper for React-Native.**

Special thanks to the following people/projects:
- [jpdriver](https://github.com/jpdriver) for his tutorial on [how to integrate Digits in a React-Native project](https://medium.com/p/getting-started-with-digits-and-react-native-f79b22439416).
- [react-native-fabric-digits](https://github.com/JeanLebrument/react-native-fabric-digits) for providing the basis of this readme, project structure and iOS implementation
- [react-native-digits](https://github.com/fixt/react-native-digits) for helping me figure out how to package the Android version.
- [Corentin S.](http://stackoverflow.com/a/33563461) for providing the basis of the Android implementation in a Stack Overflow answer

## Install

1. `npm install proximaio/react-native-fabric-digits --save`

### iOS procedure 
1. Follow the usual Digits installation procedure on your project, including adding the frameworks and modifying any files
2. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
3. Go to `node_modules` ➜ `react-native-fabric-digits` ➜ `ios` -> select the `RCTDigitsManager.xcodeproj`
4. Go to `Build Phases` tab of your project, select a target, open `Link Binary With Libraries`, click on `+` and add `libRCTDigitsManager.a`

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
  compile project(':react-native-digits')           <--- ADD THIS
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
  android:name="io.fabric.ApiKey"
  android:value="YOUR_API_KEY" />
<meta-data
  android:name="io.fabric.ApiSecret"
  android:value="YOUR_API_SECRET" />
```


## Usage

This package provide three classes: `DigitsLoginButton`, and `DigitsLogoutButton`.

You can simply use the login and logout buttons provided to trigger the `authentication` or `logout` flows as bellow: 

```javascript
/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var React = require('react-native');

var Digits = require('react-native-fabric-digits');
var { DigitsLoginButton, DigitsLogoutButton } = Digits;

var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} = React;

var Test = React.createClass({
  getInitialState: function() {
    return { logged: false, error: false, response: {} };
  },

  completion: function(error, response) {
    if (error && error.code !== 1) {
      this.setState({ logged: false, error: true, response: {} });
    } else if (response) {
      var logged = JSON.stringify(response) === '{}' ? false : true;
      this.setState({ logged: logged, error: false, response: response });
    }
  },

  render: function() {
    var error = this.state.error ? <Text>An error occured.</Text> : null;
    var content = this.state.logged ? 
      (<View>
        <Text>
          Auth Token: {this.state.response.authToken}{'\n'}
          Auth Token Secret: {this.state.response.authTokenSecret}{'\n\n'}
        </Text>
        <DigitsLogoutButton
          completion={this.completion}
          text="Logout"
          buttonStyle={styles.DigitsAuthenticateButton}
          textStyle={styles.DigitsAuthenticateButtonText}/>
      </View>) : (<DigitsLoginButton
        options={{
          title: "Logging in is great",
          phoneNumber: "+61",
          appearance: {
            backgroundColor: {
              hex: "#ffffff",
              alpha: 1.0
            },
            accentColor: {
              hex: "#43a16f",
              alpha: 0.7
            },
            headerFont: {
              name: "Arial",
              size: 16
            },
            labelFont: {
              name: "Helvetica",
              size: 18
            },
            bodyFont: {
              name: "Helvetica",
              size: 16
            }
          }
        }}
        completion={this.completion}
        text="Login (Do it)"
        buttonStyle={styles.DigitsAuthenticateButton}
        textStyle={styles.DigitsAuthenticateButtonText}/>);
    return (
      <View style={styles.container}>
        {error}
        {content}
      </View>
    );
  }
});
var styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  DigitsAuthenticateButton: {
    height: 50,
    width: 230,
    backgroundColor: '#13988A',
    justifyContent: 'center',
    borderRadius: 5
  },
  DigitsAuthenticateButtonText: {
    fontSize: 16,
    color: '#fff',
    alignSelf: 'center',
    fontWeight: 'bold'
  }
});

AppRegistry.registerComponent('Test', () => Test);
```


## Licence

**MIT**
