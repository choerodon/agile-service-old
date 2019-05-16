import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';

@inject('AppState')
@observer class IssueNumber extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  handleClickParent = () => {
    const {
      parentIssueId, resetIssue, reloadIssue,
    } = this.props;
    if (reloadIssue) {
      reloadIssue(parentIssueId);
    }
    if (resetIssue) {
      resetIssue(parentIssueId);
    }
  };

  handleClickIssueNum = () => {
    const {
      history, urlParams, backUrl, issueNum, issueId,
    } = this.props;
    history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${issueId}&paramUrl=${backUrl || 'backlog'}`);
    return false;
  };

  render() {
    const {
      typeCode, parentIssueNum, issueNum, type,
    } = this.props;

    return (
      <div style={{ fontSize: 16, lineHeight: '28px', fontWeight: 500 }}>
        {
          parentIssueNum ? (
            <span>
              <span
                role="none"
                style={{ color: 'rgb(63, 81, 181)', cursor: 'pointer' }}
                onClick={this.handleClickParent}
              >
                {parentIssueNum}
              </span>
              <span style={{ paddingLeft: 10, paddingRight: 10 }}>/</span>
            </span>
          ) : null
        }
        {
          type === 'wide' || (['sub_task', 'bug'].includes(typeCode) && parentIssueNum) ? (
            <span>
              {issueNum}
            </span>
          ) : (
            <a
              role="none"
              onClick={this.handleClickIssueNum}
            >
              {issueNum}
            </a>
          )
        }
      </div>
    );
  }
}

export default withRouter(IssueNumber);
