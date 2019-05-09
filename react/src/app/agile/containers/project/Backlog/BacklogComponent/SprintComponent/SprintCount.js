import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@observer
export default class SprintCount extends Component {
  render() {
    const { issueId } = this.props;
    return BacklogStore.getIsDragging === issueId && BacklogStore.getMultiSelected.size > 0 ? (
      <span
        className="c7n-backlog-sprintCount"
        label="sprintIssue"
      >
        {BacklogStore.getMultiSelected.size}
      </span>
    ) : null;
  }
}
