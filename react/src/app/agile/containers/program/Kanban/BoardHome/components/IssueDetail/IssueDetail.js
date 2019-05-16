import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditFeature from '../../../../Feature/FeatureComponent/FeatureDetail/EditFeature';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';

@inject('HeaderStore')
@observer
class IssueDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  onRef = (ref) => {
    KanbanStore.setEditRef(ref);
  };

  render() {
    const { refresh, HeaderStore } = this.props;
    return KanbanStore.getClickedIssue ? (      
      <EditFeature
        key={KanbanStore.getClickIssueDetail.issueId}
        store={KanbanStore}
        onRef={this.onRef}
        backUrl="board"
        style={{
          height: HeaderStore.announcementClosed ? 'calc(100vh - 156px)' : 'calc(100vh - 208px)',         
        }}
        issueId={KanbanStore.getClickIssueDetail.issueId}
        onCancel={() => {
          KanbanStore.resetClickedIssue();
        }}
        onDeleteIssue={() => {
          KanbanStore.resetClickedIssue();
          refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
        }}
        onUpdate={() => {
          refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
        }}
        resetIssue={(parentIssueId) => {
          KanbanStore.resetCurrentClick(parentIssueId);
        }}
      />
    ) : null;
  }
}

export default IssueDetail;
