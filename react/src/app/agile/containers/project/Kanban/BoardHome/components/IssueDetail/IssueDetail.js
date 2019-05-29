import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditFeature from '../../../../../../components/FeatureDetailShow';
import KanbanStore from '../../../../../../stores/project/Kanban/KanbanStore';
import IsInProgramStore from '../../../../../../stores/common/program/IsInProgramStore';

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
    const { refresh, HeaderStore, programId } = this.props;
    return KanbanStore.getClickedIssue ? (
      <EditFeature
        disabled={IsInProgramStore.isInProgram}
        programId={programId}
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
