import React, { Component } from 'react';
import { observer } from 'mobx-react';

@observer
export default class PICount extends Component {
  render() {
    const { issueId, store } = this.props;
    return store.getIsDragging === issueId && store.getMultiSelected.size > 0 ? (
      <span
        className="c7n-feature-sprintCount"
        label="sprintIssue"
      >
        {store.getMultiSelected.size}
      </span>
    ) : null;
  }
}
