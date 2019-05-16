import React, { Component } from 'react';
import Card from '../Card';
import IssueType from './IssueType.js';

class IssueTypeWrap extends Component {
  constructor(props) {
    super(props);
  }


  render() {
    const { sprintId, link } = this.props;
    return (
      <Card
        title="迭代问题类型分布"
        link={link}
        sprintId={sprintId}
      >
        <IssueType 
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default IssueTypeWrap;
