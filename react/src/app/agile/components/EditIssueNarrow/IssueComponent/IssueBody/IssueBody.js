import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import IssueDetail from './IssueDetail';
import IssueDes from './IssueDes';
import IssueAttachment from './IssueAttachment';
import IssueWiki from './IssueWiki';
import IssueCommit from './IssueCommit';
import IssueWorkLog from './IssueWorkLog';
import IssueLog from './IssueLog';
import SubTask from './SubTask';
import SubBug from './SubBug';
import IssueLink from './IssueLink';
import IssueBranch from './IssueBranch';
import TestLink from './TestLink';

@inject('AppState', 'HeaderStore')
@observer class IssueBody extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  render() {
    const {
      store,
    } = this.props;
    const issue = store.getIssue;
    const { issueTypeDTO = {} } = issue;

    return (
      <div className="c7n-content-bottom" id="scroll-area" style={{ position: 'relative' }}>
        <section className="c7n-body-editIssue">
          <div className="c7n-content-editIssue">
            <IssueDetail {...this.props} />
            <IssueDes {...this.props} />
            <IssueAttachment {...this.props} />
            {issueTypeDTO.typeCode && ['sub_task', 'feature'].indexOf(issueTypeDTO.typeCode) === -1
              ? <IssueWiki {...this.props} /> : ''
             }
            <IssueCommit {...this.props} />
            {issueTypeDTO.typeCode && ['feature'].indexOf(issueTypeDTO.typeCode) === -1
              ? <IssueWorkLog {...this.props} /> : ''
             }
            <IssueLog {...this.props} />
            {issueTypeDTO.typeCode && ['sub_task', 'feature'].indexOf(issueTypeDTO.typeCode) === -1
              ? <SubTask {...this.props} /> : ''
             }
            {issueTypeDTO.typeCode && ['story', 'task'].indexOf(issueTypeDTO.typeCode) !== -1
              ? <SubBug {...this.props} /> : ''
            }
            {issueTypeDTO.typeCode && ['feature', 'sub_task'].indexOf(issueTypeDTO.typeCode) === -1
              ? <IssueLink {...this.props} /> : ''
             }
            {issueTypeDTO.typeCode && ['feature', 'sub_task'].indexOf(issueTypeDTO.typeCode) === -1
              ? <TestLink {...this.props} /> : ''
            }
            {issueTypeDTO.typeCode && ['feature'].indexOf(issueTypeDTO.typeCode) === -1
              ? <IssueBranch {...this.props} /> : ''
             }
          </div>
        </section>
      </div>
    );
  }
}

export default IssueBody;
