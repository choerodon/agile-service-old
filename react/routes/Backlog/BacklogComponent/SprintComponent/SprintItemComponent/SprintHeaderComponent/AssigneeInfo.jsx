import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Tooltip, Button, Select, Icon, Modal, Avatar, Dropdown, Menu,
} from 'choerodon-ui';
import UserHead from '../../../../../../../components/UserHead';
import AssigneeModal from './AssigneeModal';
// import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class AssigneeInfo extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
    };
  }

  expandMore = () => {
    this.setState({
      expand: true,
    });
  };

  closeMore = () => {
    this.setState({
      expand: false,
    });
  };

  render() {
    const { assigneeIssues, data } = this.props;
    const { expand } = this.state;
    return (
      <div className="c7n-backlog-sprintName">
        {assigneeIssues ? assigneeIssues
          .filter(assignee => assignee.assigneeId)
          .map((existAssignee, index) => (
            <Tooltip
              key={`tooltip-${existAssignee.assigneeId}`}
              placement="bottom"
              title={(
                <div>
                  <p>{existAssignee.assigneeName}</p>
                  <p>
                    {'故事点: '}
                    {existAssignee.totalStoryPoints || 0}
                  </p>
                  <p>
                    {'剩余预估时间: '}
                    {existAssignee.totalRemainingTime ? existAssignee.totalRemainingTime : '无'}
                  </p>
                  <p>
                    {'问题: '}
                    {existAssignee.issueCount}
                  </p>
                </div>
              )}
            >
              <div style={{ display: 'none' }}>Magic</div>
              <UserHead
                tooltip={false}
                hiddenText
                size={24}
                style={{
                  marginBottom: 6,
                }}
                user={{
                  id: existAssignee.assigneeId,
                  loginName: existAssignee.assigneeName,
                  realName: existAssignee.assigneeName,
                  avatar: existAssignee.imageUrl,
                }}
              />
            </Tooltip>
          )) : null}
        <div style={{ flex: 1, display: 'flex', alignItems: 'center' }}>
          <Icon
            style={{
              // flex: 1,
              cursor: 'pointer',
              fontSize: 20,
              marginLeft: 8,
              display: assigneeIssues && assigneeIssues.length > 0 ? 'inline-block' : 'none',
            }}
            type="more_vert"
            role="none"
            onClick={this.expandMore}
          />
        </div>
        <AssigneeModal
          visible={expand}
          onCancel={this.closeMore}
          data={data}
        />
      </div>
    );
  }
}

export default AssigneeInfo;
