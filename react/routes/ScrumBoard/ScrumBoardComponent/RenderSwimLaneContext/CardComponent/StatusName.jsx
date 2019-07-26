import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';

/**
 * 任务状态呈现
 * @returns React 函数式组件
 * @param statusName
 * @param categoryCode
 */
export default class StatusName extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { statusName, categoryCode } = this.props;
    return nextProps.statusName !== statusName || nextProps.categoryCode !== categoryCode;
  }

  render() {
    const { statusName, categoryCode } = this.props;
    const renderStatusBackground = (parameters) => {
      if (parameters === 'todo') {
        return 'rgb(255, 177, 0)';
      } else if (parameters === 'doing') {
        return 'rgb(77, 144, 254)';
      } else if (parameters === 'done') {
        return 'rgb(0, 191, 165)';
      } else {
        return 'gray';
      }
    };
    return (
      <Tooltip title={`状态: ${statusName}`}>
        <p
          style={{
            borderRadius: 2,
            paddingLeft: 4,
            paddingRight: 4,
            background: renderStatusBackground(categoryCode),
            color: 'white',
            maxWidth: 50,
            minWidth: 20,
            textAlign: 'center',
            height: 20,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {statusName}
        </p>
      </Tooltip>
    );
  }
}
