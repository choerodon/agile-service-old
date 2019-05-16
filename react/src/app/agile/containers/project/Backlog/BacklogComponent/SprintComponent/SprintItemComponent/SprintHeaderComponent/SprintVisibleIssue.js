import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import '../../Sprint.scss';

@inject('AppState', 'HeaderStore')
@observer class SprintVisibleIssue extends Component {
  render() {
    const { issueCount } = this.props;
    return (
      <p className="c7n-backlog-sprintQuestion">
        {
          `${issueCount}个问题可见`
        }
      </p>
    );
  }
}

export default SprintVisibleIssue;
