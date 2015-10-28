# react-native-fabric-digits

## Introduction

**Fabric Digits wrapper to use it with React-Native.**

Special thanks to [jpdriver](https://github.com/jpdriver) for his tutorial on [how to integrate Digits in a React-Native project](https://medium.com/p/getting-started-with-digits-and-react-native-f79b22439416).

## Install

1. `npm install react-native-fabric-digits --save`

### iOS procedure 
2. In the XCode's "Project navigator", right click on your project's Libraries folder ➜ `Add Files to <...>`
3. Go to `node_modules` ➜ `react-native-fabric-digits` ➜ `ios` -> select the `RNFabricDigits.xcodeproj`
4. Go to `Build Phases` tab of your project, select a target, open `Link Binary With Libraries`, click on `+` and add `libRNFabricDigits.a`

## Example

You can try the test project in the `example` directory. Don't forget to run `npm install` in the `example` directory to be able to run the project!

## Usage

This package provide three classes: `DigitsAuthenticateMaanger`, `DigitsLoginButton` (optional) and `DigitsLogoutButton` (optional).

You can simply use the login and logout buttons provided to trigger the `authentication` or `logout` flows as bellow: 

```javascript
var Digits = require('react-native-fabric-digits');
var { DigitsLoginButton, DigitsLogoutButton } = Digits;

// ...

completion: function(error, response) {
  // Your code here.
},

// ...

<DigitsLoginButton
  options={{
    title: "Connect with your phone",
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
  text="Use my phone number"
  buttonStyle={styles.DigitsAuthenticateButton}
  textStyle={styles.DigitsAuthenticateButtonText}
/>

...

<DigitsLogoutButton
  completion={this.completion}
  text="Logout"
  buttonStyle={styles.DigitsAuthenticateButton}
  textStyle={styles.DigitsAuthenticateButtonText}
/>
```

Or you can directly use methods from the `DigitsAuthenticateManager` class:

```javascript
var Digits = require('react-native-fabric-digits');
var { DigitsAuthenticateManager } = Digits;

// ...

DigitsAuthenticateManager.authenticateDigitsWithOptions(options, completion);

// ...

DigitsAuthenticateManager.logout();

```

## Roadmap

- [ ] Android support.

## Licence

**MIT**
