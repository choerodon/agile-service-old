import React, { Component } from 'react';
import {
  Button, 
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import './SwimLaneHeader.scss';
import TypeTag from '../../../../../../components/TypeTag';
import StatusTag from '../../../../../../components/StatusTag';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';
import UserHead from '../../../../../../components/UserHead';

@observer
export default class SwimLaneHeader extends Component {
  renderByMode(mode, subIssueDataLength) {
    const { parentIssue } = this.props;
    const switchMap = new Map([
      ['parent_child', this.renderStoryComponent],
      ['assignee', this.renderAssigneeComponent],
      ['feature', this.renderFeatureComponent],
      ['swimlane_epic', this.renderEpicComponent],
    ]);
    const strMap = new Map([
      ['parent_child', '子任务'],
      ['assignee', '任务'],
      ['feature', '问题'],
      ['swimlane_epic', '子任务'],
      ['swimlane_none', '子任务'],
    ]);
    return (
      <div style={{ display: 'flex', alignItems: 'center' }}>
        {switchMap.get(mode)(parentIssue)}
        <span className="c7n-parentIssue-count" style={{ whiteSpace: 'nowrap' }}>{`  (${subIssueDataLength} ${strMap.get(mode)})`}</span>
      </div>
    );
  }

  renderStoryComponent = ({
    issueTypeDTO, issueNum, categoryCode, statusName, summary, assigneeId, assigneeName, imageUrl,
  }) => {
    const { parentIssue } = this.props;
    return (
      <React.Fragment>
        <TypeTag
          style={{
            marginLeft: 8,
            marginRight: 6,
          }}
          data={issueTypeDTO}
        />
        <span
          style={{ cursor: 'pointer', minWidth: 70, marginRight: 10 }}
          role="none"
          onClick={(e) => {
            e.stopPropagation();
            KanbanStore.setClickedIssue(parentIssue);
          }}
        >
          {`#${issueNum}`}
        </span>
        <StatusTag
          categoryCode={categoryCode}
          style={{ marginRight: 10 }}
          name={statusName}
        />
        <UserHead
          style={{ marginRight: 10 }}
          hiddenText
          size={24}
          user={{
            id: assigneeId,
            loginName: '',
            realName: assigneeName,
            avatar: imageUrl,
          }}
        />
        <span
          className="c7n-parentIssue-summary"
          style={JSON.stringify(KanbanStore.getClickIssueDetail) !== '{}' ? {
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textOverflow: 'ellipsis',
            maxWidth: 235,
          } : {}
          }
        >
          {summary}
        </span>
      </React.Fragment>
    );
  };

  renderAssigneeComponent = ({ assigneeName, assigneeAvatarUrl, assigneeId }) => (
    <React.Fragment>
      <UserHead
        hiddenText
        size={24}
        user={{
          id: assigneeId,
          loginName: '',
          realName: assigneeName,
          avatar: assigneeAvatarUrl,
        }}
      />
      <span>{assigneeName}</span>
    </React.Fragment>
  );

  renderFeatureComponent=({ featureType }) => (
    <React.Fragment>
      <div>
        {featureType === 'enabler' ? '使能' : '特性'}
      </div>
    </React.Fragment>
  )

  renderEpicComponent = ({ epicName }) => (
    <span>{epicName}</span>
  );

  render() {
    const {
      parentIssue, subIssueDataLength, mode, keyId, style,
    } = this.props;
    if (keyId === 'other') {
      if (mode === 'swimlane_epic') {
        return `无史诗问题（${subIssueDataLength}）`;
      } else if (mode === 'swimlane_none') {
        return (
          <div
            className="c7n-swimlaneHeader"
            role="none"
            onClick={(e) => {
              e.stopPropagation();
            }}
          >
            {`所有问题（${subIssueDataLength}）`}
          </div>
        );
      } else {
        return `其他问题(${subIssueDataLength} 任务)`;
      }
    } else {
      return (
        <div
          className="c7n-swimlaneHeader"
          style={style}
          role="none"
          onClick={(e) => {
            e.stopPropagation();
          }}
        >
          {this.renderByMode(mode, subIssueDataLength)}
          {
            mode === 'parent_child' ? (
              <Button
                type="primary"
                style={{
                  display: parentIssue.categoryCode !== 'done' && parentIssue.canMoveToComplish ? 'block' : 'none',
                }}
                onClick={(e) => {
                  e.stopPropagation();
                  KanbanStore.setUpdateParent(parentIssue);
                  KanbanStore.setTransFromData(parentIssue, parentIssue.issueId);
                }}
              >
                {'移动到done'}
              </Button>
            ) : null
          }
        </div>
      );
    }
  }
}
