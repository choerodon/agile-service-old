import React, { Component } from 'react';
import Card from '../Card';
import IssueType from './IssueType';

class IssueTypeWrap extends Component {
  render() {
    const { link } = this.props;
    return (
      <Card 
        title="问题类型分布"
        link={link}
      >
        <IssueType />
      </Card>
    );
  }
}

export default IssueTypeWrap;
