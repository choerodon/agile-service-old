import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Button, Icon } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import CreateSubTask from '../../../CreateSubTask';
import IssueList from '../../Component/IssueList';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';

@inject('AppState')
@observer class SubTask extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
    this.state = {
    };
  }

  componentDidMount() {
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
    const { subIssueDTOList = [] } = store.getIssue;
    return (
      <div className="c7n-tasks">
        {
          subIssueDTOList.map((subIssue, i) => this.renderIssueList(subIssue, i))
        }
      </div>
    );
  };

  handleCreateSubIssue = () => {
    const { onUpdate, reloadIssue } = this.props;
    VisibleStore.setCreateSubTaskShow(false);
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
    const { getCreateSubTaskShow: createSubTaskShow } = VisibleStore;
    return (
      <div id="sub_task">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="filter_none c7n-icon-title" />
            <span>子任务</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => VisibleStore.setCreateSubTaskShow(true)}>
              <Icon type="playlist_add icon" />
              <span>创建子任务</span>
            </Button>
          </div>
        </div>
        {this.renderSubIssues()}
        {
          createSubTaskShow ? (
            <CreateSubTask
              issueId={issueId}
              parentSummary={summary}
              visible={createSubTaskShow}
              onCancel={() => VisibleStore.setCreateSubTaskShow(false)}
              onOk={this.handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(SubTask));
