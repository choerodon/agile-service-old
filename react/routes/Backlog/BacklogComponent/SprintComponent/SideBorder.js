import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import { observer, inject } from 'mobx-react';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@observer
class SideBorder extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    if (JSON.stringify(nextProps) === JSON.stringify(this.props)) {
      return false;
    }
    return true;
  }

  render() {
    const { item, clickIssue } = this.props;
    return (
      <div
        className="c7n-backlog-issueSideBorder"
        style={{
          display: clickIssue === item.issueId ? 'block' : 'none',
        }}
      />
    );
  }
}

export default SideBorder;
