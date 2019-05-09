import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import SprintName from './SprintHeaderComponent/SprintName';
import SprintVisibleIssue from './SprintHeaderComponent/SprintVisibleIssue';
import '../Sprint.scss';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class BacklogHeader extends Component {
  render() {
    const {
      data, expand, toggleSprint, sprintId, issueCount,
    } = this.props;

    return (
      <div className="c7n-backlog-sprintTop">
        <div className="c7n-backlog-springTitle">
          <div className="c7n-backlog-sprintTitleSide">
            <SprintName
              type="backlog"
              expand={expand}
              sprintName="待办事项"
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
