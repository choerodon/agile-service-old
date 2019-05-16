import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, Icon } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import CreateSubBug from '../../../CreateSubBug';
import IssueList from '../../Component/IssueList';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';

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
    const { reloadIssue, store } = this.props;
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
        }}
      />
    );
  };

  renderSubIssues = () => {
    const { store } = this.props;
    const { subBugDTOList = [] } = store.getIssue;
    return (
      <div className="c7n-tasks">
        {
          subBugDTOList.map((subIssue, i) => this.renderIssueList(subIssue, i))
        }
      </div>
    );
  };

  handleCreateSubIssue = () => {
    const { onUpdate, reloadIssue } = this.props;
    VisibleStore.setCreateSubBugShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

  render() {
    const { store } = this.props;
    const { issueId, summary } = store.getIssue;
    const { getCreateSubBugShow: createSubBugShow } = VisibleStore;
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
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => VisibleStore.setCreateSubBugShow(true)}>
              <Icon type="playlist_add icon" />
              <span>创建缺陷</span>
            </Button>
          </div>
        </div>
        {this.renderSubIssues()}
        {
          createSubBugShow ? (
            <CreateSubBug
              issueId={issueId}
              parentSummary={summary}
              visible={createSubBugShow}
              onCancel={() => VisibleStore.setCreateSubBugShow(false)}
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
