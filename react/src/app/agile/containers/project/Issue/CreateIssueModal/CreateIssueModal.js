import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { stores } from 'choerodon-front-boot';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import CreateIssue from '../../../../components/CreateIssueNew';
import IssueFilterControler from '../IssueFilterControler';

// const { AppState } = stores;

@observer
class CreateIssueModal extends Component {
  // 点击创建时触发
  handleCreateIssue = (issueObj) => {
    IssueStore.createQuestion(false);
    this.filterControler = new IssueFilterControler();
    this.filterControler.resetCacheMap();
    IssueStore.setLoading(true);
    if (issueObj) {
      IssueStore.setClickedRow({
        selectedIssue: issueObj,
        expand: true,
      });
    }
    this.filterControler.refresh('refresh').then((res) => {
      IssueStore.refreshTrigger(res);
      // 创建窗口自动关闭需要接收一个 Promise.resolve()
      return Promise.resolve(res);
    });
  };

  render() {
    return IssueStore.getCreateQuestion ? (
      <CreateIssue
        visible={IssueStore.getCreateQuestion}
        onCancel={() => { IssueStore.createQuestion(false); }}
        onOk={this.handleCreateIssue.bind(this)}
      />
    ) : null;
  }
}

export default CreateIssueModal;
