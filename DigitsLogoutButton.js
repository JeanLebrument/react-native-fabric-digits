var React = require('react-native');
var { TouchableHighlight, Text, PropTypes } = React;
var DigitsManager = require("react-native").NativeModules.DigitsManager;

var DigitsLogoutButton = React.createClass({
  propTypes: {
    completion: PropTypes.func.isRequired,
    text: PropTypes.string.isRequired,
    buttonStyle: TouchableHighlight.propTypes.style,
    textStyle: Text.propTypes.style,
  },

  buttonPressed() {
    DigitsManager.logout();
    this.props.completion(null, {});
  },

  render() {
    return (
      <TouchableHighlight style={this.props.buttonStyle} onPress={this.buttonPressed} >
        <Text style={this.props.textStyle}>{this.props.text}</Text>
      </TouchableHighlight>
    );
  }
});

module.exports = DigitsLogoutButton;