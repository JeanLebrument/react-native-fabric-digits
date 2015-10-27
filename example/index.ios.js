/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var React = require('react-native');
var DigitsLoginButton = require('./DigitsLoginButton');
var DigitsLogoutButton = require('./DigitsLogoutButton');
var {
  AppRegistry,
  StyleSheet,
  Text,
  View,
  TouchableHighlight
} = React;

var MyDigitsDemo = React.createClass({
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
          You're logged! {'\n\n'}
          Phone number: {this.state.response.phoneNumber}{'\n'}
          Email address: {this.state.response.emailAddress}{'\n'}
          Email address verified: {this.state.response.emailAddressVerified ? "true" : "false"}{'\n'}
          User ID: {this.state.response.userID}{'\n'}
          Auth Token: {this.state.response.authToken}{'\n'}
          Auth Token Secret: {this.state.response.authTokenSecret}{'\n\n'}
        </Text>
        <DigitsLogoutButton
          completion={this.completion}
          text="Logout"
          buttonStyle={styles.DigitsAuthenticateButton}
          textStyle={styles.DigitsAuthenticateButtonText}
        />
      </View>) : (<DigitsLoginButton
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
      />);

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

AppRegistry.registerComponent('MyDigitsDemo', () => MyDigitsDemo);
