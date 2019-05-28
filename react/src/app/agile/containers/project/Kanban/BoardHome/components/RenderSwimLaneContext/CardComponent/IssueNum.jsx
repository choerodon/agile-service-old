import React, { Component } from 'react';

/**
 * 任务类型呈现
 * @returns React 函数式组件
 * @param issueNum
 * @param completed
 */
export default class IssueNum extends Component {
  shouldComponentUpdate(nextProps) {
    const { issueNum, completed } = this.props;
    return nextProps.issueNum !== issueNum || nextProps.completed !== completed;
  }

  render() {
    const { issueNum, completed } = this.props;
    return (
      <div
        style={{ marginLeft: 5, textDecoration: completed ? 'line-through' : '' }}
        className="textDisplayOneColumn"
      >
        {issueNum}
      </div>
    );
  }
}
