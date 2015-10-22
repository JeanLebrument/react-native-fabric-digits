/**
 *
 * @providesModule DigitAuthenticateButton
 * @flow
 *
 */
'use strict';

var React = require('React');
var StyleSheet = require('StyleSheet');
var requireNativeComponent = require('requireNativeComponent');

var DigitsAuthenticateButton = React.createClass({
  render: function() {
    return (
      <DGTAuthenticateButtonView style={styles.DigitsAuthenticateButton}/>
    );
  }
});

var styles = StyleSheet.create({
  DigitsAuthenticateButton: {
    height: 50,
    width: 200,
    backgroundColor: '#13988A'
  },
});

var DGTAuthenticateButtonView = requireNativeComponent('DGTAuthenticateButtonView', DGTAuthenticateButtonView);

module.exports = DigitsAuthenticateButton;
