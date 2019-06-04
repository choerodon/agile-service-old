import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CardWidth, CardHeight, CardMargin } from '../../Constants';

class Card extends Component {
  render() {
    const { style } = this.props;
    return (
      <div 
        {...this.props}
        style={{
          width: CardWidth, 
          height: CardHeight,
          margin: CardMargin,
          textAlign: 'left',
          ...style, 
        }}
      />
    );
  }
}

Card.propTypes = {

};

export default Card;
