import React, { Component } from 'react';
import Card from '../Card';
import IterationType from './IterationType';

class IterationTypeWrap extends Component {
  render() {
    // const { link } = this.props; 
    return (
      <Card 
        title="问题类型状态分布"
        // link={link}
      >
        <IterationType />
      </Card>
    );
  }
}

export default IterationTypeWrap;
