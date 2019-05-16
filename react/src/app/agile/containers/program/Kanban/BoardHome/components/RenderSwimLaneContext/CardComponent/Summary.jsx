import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';

/**
 * 任务经办人呈现
 * @returns React 函数式组件
 * @param summary
 */
export default class Summary extends Component {
  shouldComponentUpdate(nextProps) {
    const { summary } = this.props;
    return nextProps.summary !== summary;
  }

  render() {
    const { summary } = this.props;
    return (
      <Tooltip title={summary} placement="topLeft">
        <div className="textDisplayOneColumn">
          {summary}
        </div>
      </Tooltip>
    );
  }
}
