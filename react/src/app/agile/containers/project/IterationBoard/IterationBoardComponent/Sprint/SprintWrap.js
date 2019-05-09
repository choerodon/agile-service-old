import React, { Component } from 'react';
import Card from '../Card';
import Sprint from './Sprint';

class SprintWrap extends Component {
  render() {
    const { sprintId, sprintName } = this.props;

    return (
      <Card
        title={sprintName}
        sprintId={sprintId}
      >
        <Sprint
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default SprintWrap;
