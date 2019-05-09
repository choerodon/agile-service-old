import React, { Component } from 'react';
import { observer } from 'mobx-react';

@observer
class SideBorder extends Component {
  shouldComponentUpdate(nextProps) {
    if (JSON.stringify(nextProps) === JSON.stringify(this.props)) {
      return false;
    }
    return true;
  }

  render() {
    const { item, clickIssue } = this.props;
    return (
      <div
        className="c7n-feature-issueSideBorder"
        style={{
          display: clickIssue === item.issueId ? 'block' : 'none',
        }}
      />
    );
  }
}

export default SideBorder;
