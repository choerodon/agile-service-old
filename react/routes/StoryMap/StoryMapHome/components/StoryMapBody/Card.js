import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CardWidth, CardHeight, CardMargin } from '../../Constants';
import './Card.scss';

class Card extends Component {
  render() {
    const {
      style, className, saveRef, ...otherProps 
    } = this.props;
    return (
      <div 
        ref={saveRef}
        style={{
          width: CardWidth, 
          height: CardHeight,
          margin: CardMargin,
          textAlign: 'left',
          ...style, 
        }}
        className={`c7nagile-StoryMap-Card ${className || ''}`}
        {...otherProps}
      />
    );
  }
}

Card.propTypes = {

};

export default Card;
