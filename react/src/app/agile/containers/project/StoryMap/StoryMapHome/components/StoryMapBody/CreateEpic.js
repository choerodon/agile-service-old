import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import Card from './Card';

class CreateEpic extends Component {
  handleBlur=(e) => {
    // console.log(e.target.value);
  }

  render() {
    return (
      <Card style={{
        boxShadow: '0 0 4px -2px rgba(0,0,0,0.50), 0 2px 4px 0 rgba(0,0,0,0.13)',
        borderRadius: 2,
        height: 42, 
        margin: '4px 4px 4px 9px',
        padding: 7,
        display: 'flex',
        justifyContent: 'center',
      }}
      >
        <Input autoFocus onBlur={this.handleBlur} placeholder="在此创建史诗" />       
      </Card>
    );
  }
}

CreateEpic.propTypes = {

};

export default CreateEpic;
