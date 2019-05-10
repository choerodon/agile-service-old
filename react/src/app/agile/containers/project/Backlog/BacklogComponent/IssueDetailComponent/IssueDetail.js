import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditIssue from '../../../../../components/EditIssueNarrow';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState')
@observer
class IssueDetail extends Component {
  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  /**
   *detail有更新回调待办事项更新
   *
   * @memberof IssueDetail
   */
  handleIssueUpdate() {
    const chosenEpic = BacklogStore.getChosenEpic;
    BacklogStore.axiosGetSprint(BacklogStore.getSprintFilter()).then((res) => {
      BacklogStore.setSprintData(res);
    }).catch((error) => {
    });
  }

  /**
   * 刷新issue详情的数据
   */
  refreshIssueDetail() {
    if (this.editIssue) {
      this.editIssue.loadIssueDetail();
    }
  }

  render() {
    // const { paramOpenIssueId } = this.state;
    const { cancelCallback, refresh } = this.props;
    const visible = Object.keys(BacklogStore.getClickIssueDetail).length > 0;
    return (

      visible ? (
        <EditIssue
          store={BacklogStore}
          onRef={(ref) => {
            this.editIssue = ref;
          }}
          issueId={BacklogStore.getClickIssueId}
          onCancel={() => {
            BacklogStore.setClickIssueDetail({});
            BacklogStore.setIsLeaveSprint(false);
            BacklogStore.clearMultiSelected();
            cancelCallback();
          }}
          onDeleteIssue={() => {
            BacklogStore.setClickIssueDetail({});
            BacklogStore.setIsLeaveSprint(false);
            refresh();
          }}
          onCreateVersion={() => {
            BacklogStore.axiosGetVersion().then((data2) => {
              const newVersion = [...data2];
              for (let index = 0, len = newVersion.length; index < len; index += 1) {
                newVersion[index].expand = false;
              }
              BacklogStore.setVersionData(newVersion);
            }).catch((error) => {
            });
          }}
          onUpdate={refresh}
        />
      ) : ''
    );
  }
}

export default IssueDetail;
