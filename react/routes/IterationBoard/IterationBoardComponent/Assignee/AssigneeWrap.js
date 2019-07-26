import React, { Component } from 'react';
import Card from '../Card';
import Assignee from './Assignee';

class AssigneeWrap extends Component {
  render() {
    const { sprintId, link } = this.props;

    return (
      <Card
        title="经办人分布"
        link={link}
        sprintId={sprintId}
      >
        <Assignee
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default AssigneeWrap;
