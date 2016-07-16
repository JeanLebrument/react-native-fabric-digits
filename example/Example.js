/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Alert,
  AppRegistry,
  StyleSheet,
  Text,
  View,
} from 'react-native';

const Digits = require('react-native-fabric-digits');
const { DigitsLoginButton, DigitsLogoutButton } = Digits;

class Example extends Component {
  constructor(props) {
    super(props);
    this.state = {
      logged: false,
      error: false,
      response: {}
    };
    this.completion = this.completion.bind(this);
    this.getSessionDetails = this.getSessionDetails.bind(this);
  }

  completion(error, response) {
    if (error && error.code !== 1) {
      this.setState({ logged: false, error: true, response: {} });
    } else if (response) {
      const logged = JSON.stringify(response) === '{}' ? false : true;
      this.setState({ logged: logged, error: false, response: response }, this.getSessionDetails);
    }
  }

  getSessionDetails() {
    if (this.state.logged) {
      this.refs.DigitsLogoutButton.getSessionDetails(function(sessionDetails) {
        Alert.alert('Success!', sessionDetails.phoneNumber);
      });
    }
  }

  render() {
    const error = this.state.error ? <Text>An error occured.</Text> : null;
    const content = this.state.logged ?
      (<View style={styles.container}>
        <Text>
          OAuth Token: {this.state.response['X-Verify-Credentials-Authorization']}
        </Text>
        <DigitsLogoutButton
          ref="DigitsLogoutButton"
          completion={this.completion}
          text="Logout"
          buttonStyle={styles.DigitsAuthenticateButton}
          textStyle={styles.DigitsAuthenticateButtonText}/>
      </View>) : (<DigitsLoginButton
        ref="DigitsLoginButton"
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
}

const styles = StyleSheet.create({
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
    borderRadius: 5,
  },
  DigitsAuthenticateButtonText: {
    fontSize: 16,
    color: '#fff',
    alignSelf: 'center',
    fontWeight: 'bold',
  },
});

AppRegistry.registerComponent('Example', () => Example);
