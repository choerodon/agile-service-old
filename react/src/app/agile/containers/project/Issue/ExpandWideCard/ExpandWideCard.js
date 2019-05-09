import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import EditIssue from '../../../../components/EditIssueWide';
import { loadIssue } from '../../../../api/NewIssueApi';
import IssueFilterControler from '../IssueFilterControler';

@inject('AppState', 'HeaderStore')
@observer
class ExpandWideCard extends Component {
  // 更新 Issue 时
  handleIssueUpdate = (issueId) => {
    const { issueRefresh } = this.props;
    issueRefresh();
  };

  render() {
    const { HeaderStore, onHideIssue } = this.props;
    return IssueStore.getExpand ? (
      <EditIssue
        {...this.props}
        store={IssueStore}
        issueId={IssueStore.getSelectedIssue && IssueStore.getSelectedIssue.issueId}
        onCancel={() => {
          onHideIssue();
          IssueStore.setClickedRow({
            expand: false,
            selectedIssue: {},
            checkCreateIssue: false,
          });
        }}
        onDeleteIssue={() => {
          IssueStore.setClickedRow({
            expand: false,
            selectedIssue: {},
          });
          const filterControler = new IssueFilterControler();
          filterControler.refresh('refresh').then((res) => {
            IssueStore.refreshTrigger(res);
            Promise.resolve();
          });
        }}
        onUpdate={this.handleIssueUpdate.bind(this)}
        onCopyAndTransformToSubIssue={() => {
          const filterControler = new IssueFilterControler();
          filterControler.refresh('refresh').then((res) => {
            IssueStore.refreshTrigger(res);
            Promise.resolve();
          });
        }}
      /> 
    ) : null;
  }
}

export default ExpandWideCard;
