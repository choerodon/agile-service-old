import React, { Component } from 'react';
import Card from '../Card';
import Prio from './Priority';

class Priority extends Component {
  render() {
    const { sprintId, link } = this.props;

    return (
      <Card
        title="优先级分布"
        link={link}
        sprintId={sprintId}
      >
        <Prio
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default Priority;
