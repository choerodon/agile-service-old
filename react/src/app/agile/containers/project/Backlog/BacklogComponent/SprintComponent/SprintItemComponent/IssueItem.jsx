import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import { observer, inject } from 'mobx-react';
import classnames from 'classnames';
import { Tooltip } from 'choerodon-ui';
import _ from 'lodash';
import Typetag from '../../../../../../components/TypeTag';
import UserHead from '../../../../../../components/UserHead';
import { STATUS } from '../../../../../../common/Constant';
import SideBorder from '../SideBorder';
import SprintCount from '../SprintCount';
import SprintIssue from '../SprintIssue';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';

@observer
class IssueItem extends Component {
  handleClick = (e) => {
    e.stopPropagation();
    const { onClick, item } = this.props;
    onClick(e, item);
  };

  render() {
    const { item } = this.props;
    return (
      <div
        className={classnames('c7n-backlog-sprintIssueItem', {
          'issue-selected': BacklogStore.getMultiSelected.get(item.issueId),
        })}
        key={item.issueId}
        label="sprintIssue"
        onClick={this.handleClick}
        role="none"
      >
        <SprintCount
          key={`${item.issueId}-count`}
          issueId={item.issueId}
        />
        <SprintIssue
          key={item.issueId}
          ref={((e) => {
            this.ref = e;
          })}
          item={item}
          epicVisible={null}
          versionVisible={null}
          issueDisplay={0}
        />
      </div>
    );
  }
}

export default IssueItem;
