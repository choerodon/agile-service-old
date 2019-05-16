import React, { Component } from 'react';
import SprintDetails from './SprintDetails';
import Card from '../Card';

class SprintDetailsWrap extends Component {
  constructor(props) {
    super(props);
  }

  render() {
    const { sprintId, link } = this.props;
    return (
      <Card
        title="冲刺详情"
        link={link}
        sprintId={sprintId}
      >
        <SprintDetails 
          sprintId={sprintId}
        />
      </Card>
    );
  }
}
export default SprintDetailsWrap;
