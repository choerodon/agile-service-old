import React, { Component } from 'react';
import Card from '../Card';
import Assignee from './Assignee';

class AssigneeWrap extends Component {
  render() {
    const { link } = this.props;

    return (
      <Card
        title="经办人分布"
        link={link}
      >
        <Assignee />
      </Card>
    );
  }
}

export default AssigneeWrap;
