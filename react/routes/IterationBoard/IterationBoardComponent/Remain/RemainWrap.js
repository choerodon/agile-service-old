import React, { Component } from 'react';
import Card from '../Card';
import Remain from './Remain';

class RemainWrap extends Component {
  render() {
    const { sprintId } = this.props;
    
    return (
      <Card
        title="距离冲刺结束"
        sprintId={sprintId}
      >
        <Remain
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default RemainWrap;
