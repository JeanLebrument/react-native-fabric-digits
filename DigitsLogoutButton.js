var React = require('react-native');
var { TouchableHighlight, Text, PropTypes } = React;
var DigitsAuthenticateManager = require('./DigitsAuthenticateManager');

var DigitsLogoutButton = React.createClass({
  propTypes: {
    completion: PropTypes.func.isRequired,
    text: PropTypes.string.isRequired,
    buttonStyle: TouchableHighlight.propTypes.style,
    textStyle: Text.propTypes.style,
  },

  buttonPressed() {
    DigitsAuthenticateManager.logout()
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