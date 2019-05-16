import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import SprintName from './PIHeaderComponent/PIName';
import SprintVisibleIssue from './PIHeaderComponent/SprintVisibleIssue';
import '../PI.scss';

@inject('AppState', 'HeaderStore')
@observer class BacklogHeader extends Component {
  render() {
    const {
      expand, toggleSprint, issueCount,
    } = this.props;

    return (
      <div className="c7n-feature-sprintTop">
        <div className="c7n-feature-springTitle">
          <div className="c7n-feature-sprintTitleSide">
            <SprintName
              type="backlog"
              expand={expand}
              piName="特性列表"
              toggleSprint={toggleSprint}
            />
            <SprintVisibleIssue
              issueCount={issueCount}
            />
          </div>
        </div>
      </div>
    );
  }
}

export default BacklogHeader;
