import { Tooltip } from 'choerodon-ui';
import React, { Component } from 'react';
import UserHead from '../../../../../../../components/UserHead/UserHead';

/**
 * 任务经办人呈现
 * @returns React 函数式组件
 * @param assigneeName
 * @param assigneeId
 * @param imageUrl
 */
export default class Assignee extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { assigneeName, assigneeId, imageUrl } = this.props;
    return nextProps.assigneeName !== assigneeName || nextProps.assigneeId !== assigneeId || nextProps.imageUrl !== imageUrl;
  }

  render() {
    const { assigneeName, assigneeId, imageUrl } = this.props;
    return (
      <Tooltip title={assigneeName ? `经办人: ${assigneeName}` : ''}>
        {
          assigneeName ? (
            <UserHead
              hiddenText
              size={32}
              style={{ marginLeft: 8 }}
              user={{
                id: assigneeId,
                loginName: assigneeName,
                realName: assigneeName,
                avatar: imageUrl,
              }}
            />
          ) : (
            <div style={{
              width: 32,
              height: 32,
              flexShrink: 0,
              marginLeft: 8,
              marginBottom: 4,
            }}
            />
          )
        }
      </Tooltip>
    );
  }
}
