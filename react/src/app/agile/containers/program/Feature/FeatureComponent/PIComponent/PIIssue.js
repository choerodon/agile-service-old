import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Tooltip } from 'choerodon-ui';
import classnames from 'classnames';
import _ from 'lodash';
import TypeTag from '../../../../../components/TypeTag';
import UserHead from '../../../../../components/UserHead';
import StatusTag from '../../../../../components/StatusTag';
import PriorityTag from '../../../../../components/PriorityTag';
import './PIIssue.scss';

@observer
class PIItem extends Component {
  shouldComponentUpdate(nextProps) {
    if (JSON.stringify(nextProps) === JSON.stringify(this.props)) {
      return false;
    }
    return true;
  }

  render() {
    const { item } = this.props;
    return (
      <div className={classnames('c7n-feature-IssueCard')}>
        <div
          label="PIItem"
          className={classnames('c7n-feature-IssueCard-left')}
        >
          <TypeTag
            data={{
              ...item.issueTypeDTO,
              colour: item.featureType === 'business' ? '#29B6F6' : '#FFCA28',
            }}
          />
          <div className="c7n-feature-IssueCard-left-summaryContainer">
            <div className="c7n-feature-IssueCard-left-issueNum" style={{ textDecoration: item.statusMapDTO && item.statusMapDTO.code === 'complete' ? 'line-through' : 'none' }}>
              {`${item.issueNum}`}
            </div>
            <Tooltip title={item.summary} placement="topLeft">
              <div className="c7n-feature-IssueCard-left-issueSummary">{item.summary}</div>
            </Tooltip>
          </div>
        </div>
        <div
          className={classnames('c7n-feature-IssueCard-right')}
        >
          <div className={classnames('line-two-left')}>
            {!_.isNull(item.epicName) && item.epicName ? (
              <Tooltip title={`史诗: ${item.epicName}`}>
                <span
                  label="PIItem"
                  className="c7n-feature-IssueCard-right-epic container"
                  style={{
                    color: item.color || item.epicColor,
                    border: `1px solid ${item.color || item.epicColor}`,
                  }}
                >
                  {item.epicName}
                </span>
              </Tooltip>
            ) : ''}
            {item.assigneeId && (
              <UserHead
                user={{
                  id: item.assigneeId,
                  loginName: '',
                  realName: item.assigneeName,
                  avatar: item.imageUrl,
                }}
              />
            )}
          </div>
          <div className={classnames('line-two-right')}>
            <Tooltip title={`状态: ${item.statusMapDTO ? item.statusMapDTO.name : ''}`}>
              <div className="c7n-feature-IssueCard-right-status">
                <StatusTag
                  data={item.statusMapDTO}
                />
              </div>
            </Tooltip>
            <Tooltip title={`故事点: ${item.storyPoints}`}>
              <div
                label="PIItem"
                className={classnames('c7n-feature-IssueCard-right-storyPoint', {
                  visible: item.storyPoints,
                })}
              >
                {item.storyPoints}
              </div>
            </Tooltip>
          </div>
        </div>
      </div>
    );
  }
}

export default PIItem;
