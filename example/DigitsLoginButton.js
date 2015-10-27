var React = require('react-native');
var { TouchableHighlight, Text, PropTypes } = React;
var DigitsAuthenticateManager = require('./DigitsAuthenticateManager');

var DigitsLoginButton = React.createClass({
  propTypes: {
    options: PropTypes.object.isRequired,
    completion: PropTypes.func.isRequired,
    text: PropTypes.string.isRequired,
    buttonStyle: TouchableHighlight.propTypes.style,
    textStyle: Text.propTypes.style,
  },

  buttonPressed() {
    DigitsAuthenticateManager.authenticateDigitsWithOptions(this.props.options, this.props.completion)
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