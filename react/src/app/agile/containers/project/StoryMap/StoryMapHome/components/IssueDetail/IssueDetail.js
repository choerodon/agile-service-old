import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditIssue from '../../../../../../components/EditIssue';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import ProgramIssueShow from '../../../../../../components/ProgramIssueShow';

@inject('AppState')
@observer
class IssueDetail extends Component {
  // componentDidMount() {
  //   const { onRef } = this.props;
  //   onRef(this);
  // }


  /**
   * 刷新issue详情的数据
   */
  refreshIssueDetail() {
    if (this.editIssue) {
      this.editIssue.loadIssueDetail();
    }
  }

  handleCancel=() => {
    StoryMapStore.setClickIssue(null);
  }

  handleDeleteIssue=() => {
    
  }

  render() {
    const { refresh } = this.props;
    const { clickIssue } = StoryMapStore;
    const visible = clickIssue;
    const { programId } = clickIssue || {};
    const TargetComponent = programId ? ProgramIssueShow : EditIssue;
    return (
      visible ? (
        <TargetComponent         
          programId={programId}        
          onRef={(ref) => {
            this.editIssue = ref;
          }}
          issueId={clickIssue.issueId}
          onCancel={this.handleCancel}
          onDeleteIssue={this.handleDeleteIssue}
          onUpdate={refresh}
        />
      ) : null
    );
  }
}

export default IssueDetail;
