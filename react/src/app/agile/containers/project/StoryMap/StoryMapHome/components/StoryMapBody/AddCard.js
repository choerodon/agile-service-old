import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';

class AddCard extends Component {
  render() {
    const { style, ...otherProps } = this.props;
    return (
      <div
        style={{
          width: 20,
          height: '100%',
          display: 'flex',
          alignItems: 'center', 
          justifyContent: 'center',
          background: 'rgba(0,0,0,0.04)',
          borderRadius: 2, 
          cursor: 'pointer',
          marginLeft: 4,
          ...style,
        }}
        {...otherProps}
      >
        <Icon
          type="control_point"
          style={{
            height: 14, width: 14, fontSize: '14px', color: 'rgba(0,0,0,0.3)', 
          }}
        />
      </div>
    );
  }
}

AddCard.propTypes = {

};

export default AddCard;
