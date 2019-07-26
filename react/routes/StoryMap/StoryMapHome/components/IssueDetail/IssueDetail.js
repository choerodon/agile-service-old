import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditIssue from '../../../../../components/EditIssue';
import StoryMapStore from '../../../../../stores/project/StoryMap/StoryMapStore';

@inject('AppState')
@observer
class IssueDetail extends Component {
  constructor(props) {
    super(props);

    this.EditIssue = React.createRef();
  }


  /**
   * 刷新issue详情的数据
   */
  refreshIssueDetail() {
    if (this.EditIssue.current) {
      this.EditIssue.current.loadIssueDetail();
    }
  }

  handleCancel=() => {
    StoryMapStore.setClickIssue(null);
  }

  handleDeleteIssue=() => {
    StoryMapStore.setClickIssue(null);
    const { refresh } = this.props;
    refresh();
  }

  render() {
    const { refresh } = this.props;
    const { selectedIssueMap } = StoryMapStore;
    const visible = selectedIssueMap.size;
    const { programId, issueId } = selectedIssueMap.values().next().value || {};   
    return (
      visible ? (
        <EditIssue         
          programId={programId}       
          disabled={programId}
          applyType={programId ? 'program' : 'agile'}
          forwardedRef={this.EditIssue}
          issueId={issueId}
          onCancel={this.handleCancel}
          onDeleteIssue={this.handleDeleteIssue}
          onUpdate={refresh}
        />
      ) : null
    );
  }
}

export default IssueDetail;
