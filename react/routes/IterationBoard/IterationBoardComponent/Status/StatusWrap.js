import React, { Component } from 'react';
import Card from '../Card';
import Status from './Status';

class StatusWrap extends Component {
  render() {
    const { sprintId } = this.props;

    return (
      <Card
        title="状态分布"
        sprintId={sprintId}
      >
        <Status
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default StatusWrap;
