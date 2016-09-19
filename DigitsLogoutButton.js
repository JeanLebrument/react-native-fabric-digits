import React, { Component } from 'react';
import { NativeModules, Text, TouchableHighlight } from 'react-native';

class DigitsLogoutButton extends Component {
  constructor(props) {
    super(props);

    this.buttonPressed = this.buttonPressed.bind(this);
    this.getSessionDetails = this.getSessionDetails.bind(this);
  }

  getSessionDetails(callback) {
    NativeModules.DigitsManager.sessionDetails((error, sessionDetails) => {
      if (error) {
        console.error(error);
      } else {
        callback(sessionDetails);
      }
    });
  }

  buttonPressed() {
    NativeModules.DigitsManager.logout();
    this.props.completion(null, {});
  }

  render() {
    return (
      <TouchableHighlight style={this.props.buttonStyle} underlayColor={this.props.highlightColor} onPress={this.buttonPressed} >
        <Text style={this.props.textStyle}>{this.props.text}</Text>
      </TouchableHighlight>
    );
  }
}

DigitsLogoutButton.defaultProps = {
  highlightColor: 'black',
};

module.exports = DigitsLogoutButton;
