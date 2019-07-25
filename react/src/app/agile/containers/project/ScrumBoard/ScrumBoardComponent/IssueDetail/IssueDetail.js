import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditIssue from '../../../../../components/EditIssue';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

@inject('AppState')
@observer
class IssueDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {};
    this.EditIssue = React.createRef();  
    ScrumBoardStore.setEditRef(this.EditIssue);
  }

  render() {
    const { refresh } = this.props;
    return ScrumBoardStore.getClickedIssue ? (
      <EditIssue
        store={ScrumBoardStore}
        forwardedRef={this.EditIssue}
        backUrl="scrumboard"        
        issueId={ScrumBoardStore.getCurrentClickId}
        onCancel={() => {
          ScrumBoardStore.resetClickedIssue();
        }}
        onDeleteIssue={() => {
          ScrumBoardStore.resetClickedIssue();
          refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
        }}
        onUpdate={() => {
          refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
        }}
        resetIssue={(parentIssueId) => {
          ScrumBoardStore.resetCurrentClick(parentIssueId);
        }}
      />
    ) : null;
  }
}

export default IssueDetail;
