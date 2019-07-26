/**
 * 列状态
 */
import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { DragDropContext } from 'react-beautiful-dnd';
import './SwimLane.scss';
import RenderSwimLaneContext from './index';
import ColumnProvider from './ColumnProvider';
import StatusProvider from './StatusProvider';
import CardProvider from './CardProvider';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import EpicRenderHeader from './EpicRenderHeader';

@inject('AppState')
@observer
class SwimLane extends Component {
  renderEpicLane = mode => (
    <EpicRenderHeader
      parentIssueArr={ScrumBoardStore.getInterconnectedData}
      otherIssueWithoutParent={ScrumBoardStore.getOtherQuestion}
      mode={mode}
      fromEpic
    >
      {(parentIssue, epicPrefix) => this.renderParentWithSub('parent_child', true, parentIssue, epicPrefix)}
    </EpicRenderHeader>
  );

  /**
   * 渲染被分配的任务列
   * @returns {Array}
   */
  renderParentWithSub = (mode, fromEpic = null, parentIssue = null, epicPrefix = null) => (
    <RenderSwimLaneContext
      parentIssueArr={fromEpic ? parentIssue.interConnectedDataMap : ScrumBoardStore.getInterconnectedData}
      otherIssueWithoutParent={fromEpic ? parentIssue.unInterConnectedDataMap : ScrumBoardStore.getOtherQuestion}
      fromEpic={fromEpic}
      epicPrefix={epicPrefix}
      mode={mode}
    >
      {key => this.renderSwimLane(key)}
    </RenderSwimLaneContext>
  );

  renderSwimLane = (key) => {
    const { mapStructure, onDragEnd, onDragStart } = this.props;
    return (
      <DragDropContext
        onDragEnd={(start) => {
          onDragEnd(start);
        }}
        onDragStart={(start) => {
          onDragStart(start);
        }}
      >
        <ColumnProvider
          keyId={key}
          {...mapStructure}
        >
          {(statusArr, columnId) => (
            <StatusProvider
              statusData={statusArr}
              columnId={columnId}
              keyId={key}
            >
              {(keyId, id, completed, statusName, categoryCode) => <CardProvider keyId={keyId} id={id} completed={completed} statusName={statusName} categoryCode={categoryCode} />}
            </StatusProvider>
          )}
        </ColumnProvider>
      </DragDropContext>
    );
  };

  renderContext = (mode) => {
    switch (mode) {
      case 'assignee':
      case 'parent_child':
        return this.renderParentWithSub(mode);
      case 'swimlane_epic':
        return this.renderEpicLane(mode);
      case 'swimlane_none':
        return this.renderParentWithSub(mode);
      default:
        return null;
    }
  };

  render() {
    const { mode } = this.props;
    return (
      this.renderContext(mode)
    );
  }
}

export default SwimLane;
