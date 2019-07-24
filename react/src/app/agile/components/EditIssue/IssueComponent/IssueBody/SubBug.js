import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, Icon, Tooltip } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import CreateSubBug from '../../../CreateIssue/CreateSubBug';
import IssueList from '../../Component/IssueList';

@inject('AppState')
@observer class SubBug extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
  }

  /**
   * IssueList
   * @param {*} issue
   * @param {*} i
   */
  renderIssueList = (issue, i) => {
    const { reloadIssue, store, onDeleteSubIssue } = this.props;
    const { issueId: id } = store.getIssue;
    return (
      <IssueList
        showAssignee
        key={issue.issueId}
        issue={{
          ...issue,
          typeCode: issue.typeCode || 'sub_task',
        }}
        i={i}
        onOpen={() => {
          if (reloadIssue) {
            reloadIssue(issue.issueId);
          }
        }}
        onRefresh={() => {
          if (reloadIssue) {
            reloadIssue(id);
          }
          if (onDeleteSubIssue) {
            onDeleteSubIssue();
          }
        }}
      />
    );
  };

  renderSubIssues = () => {
    const { store } = this.props;
    const { subBugVOList = [] } = store.getIssue;
    return (
      <div className="c7n-tasks">
        {
          subBugVOList.map((subIssue, i) => this.renderIssueList(subIssue, i))
        }
      </div>
    );
  };

  handleCreateSubIssue = () => {
    const { onUpdate, reloadIssue, store } = this.props;
    store.setCreateSubBugShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

  render() {
    const { store, disabled } = this.props;
    const { issueId, summary } = store.getIssue;
    const { getCreateSubBugShow: createSubBugShow } = store;
    return (
      <div id="bug">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="bug_report c7n-icon-title" />
            <span>缺陷</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="创建缺陷" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => store.setCreateSubBugShow(true)}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
          )}
        </div>
        {this.renderSubIssues()}
        {
          createSubBugShow ? (
            <CreateSubBug
              relateIssueId={issueId}
              parentSummary={summary}
              visible={createSubBugShow}
              onCancel={() => store.setCreateSubBugShow(false)}
              onOk={this.handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(SubBug));
