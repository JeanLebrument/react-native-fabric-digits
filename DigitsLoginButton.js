var React = require('react-native');
var { TouchableHighlight, Text, PropTypes } = React;
var DigitsManager = require("react-native").NativeModules.DigitsManager;

var DigitsLoginButton = React.createClass({
  propTypes: {
    options: PropTypes.object.isRequired,
    completion: PropTypes.func.isRequired,
    text: PropTypes.string.isRequired,
    buttonStyle: TouchableHighlight.propTypes.style,
    textStyle: Text.propTypes.style,
  },

  getSessionDetails(callback) {
    DigitsManager.sessionDetails((error, sessionDetails) => {
      if (error) {
        console.error(error);
      } else {
        callback(sessionDetails);
      }
    });
  },

  buttonPressed() {
    DigitsManager.launchAuthentication(this.props.options).then((responseData) => {
      console.log("[Digits] Login Successful", responseData);
      this.props.completion(null, responseData);
    }).catch((error) => {
      if(error && error.code != 1){
              console.error("[Digits] Login Error", error);
      }
      this.props.completion(error, null);
    });
  },

  render() {
    return (
      <TouchableHighlight style={this.props.buttonStyle} onPress={this.buttonPressed} >
        <Text style={this.props.textStyle}>{this.props.text}</Text>
      </TouchableHighlight>
    );
  }
});

module.exports = DigitsLoginButton;
