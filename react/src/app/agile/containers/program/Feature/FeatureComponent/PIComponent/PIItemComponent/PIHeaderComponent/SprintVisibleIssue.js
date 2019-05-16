import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import '../../PI.scss';

@inject('AppState', 'HeaderStore')
@observer class SprintVisibleIssue extends Component {
  render() {
    const { issueCount } = this.props;
    return (
      <p className="c7n-feature-sprintQuestion">
        {
          `${issueCount}个特性`
        }
      </p>
    );
  }
}

export default SprintVisibleIssue;
