import React, { Component } from 'react';
import PropTypes from 'prop-types';

class PiWorkCard extends Component {
  render() {
    const { leap } = this.props;
    return (
      <div style={{ flex: leap }} />
    );
  }
}

PiWorkCard.propTypes = {

};

export default PiWorkCard;
