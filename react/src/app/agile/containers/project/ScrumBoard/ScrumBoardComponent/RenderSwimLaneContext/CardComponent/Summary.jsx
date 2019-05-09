import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';

/**
 * 任务经办人呈现
 * @returns React 函数式组件
 * @param summary
 */
export default class Summary extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { summary } = this.props;
    return nextProps.summary !== summary;
  }

  render() {
    const { summary } = this.props;
    return (
      <Tooltip title={summary} placement="topLeft">
        <p className="textDisplayTwoColumn">
          {summary}
        </p>
      </Tooltip>
    );
  }
}
