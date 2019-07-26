import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import { observer } from 'mobx-react';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';
import IssueItem from './IssueItem';

@observer
class IssueList extends Component {
  myOnMouseDown = (e, item) => {
    e.stopPropagation();
    const { sprintId } = this.props;
    // if (BacklogStore.getPrevClickedIssue) {
    if (!(e.shiftKey && (e.ctrlKey || e.metaKey))) {
      if (e.shiftKey) {
        BacklogStore.dealWithMultiSelect(sprintId, item, 'shift');
      } else if (e.ctrlKey || e.metaKey) {
        BacklogStore.dealWithMultiSelect(sprintId, item, 'ctrl');
      } else {
        BacklogStore.clickedOnce(sprintId, item);
      }
    }
  };

  render() {
    const { sprintId } = this.props;

    return BacklogStore.getIssueMap.get(sprintId).map((item, index) => (
      <Draggable key={item.issueId} draggableId={item.issueId} index={index}>
        {provided => (
          <div
            key={item.issueId}
            className="c7n-backlog-sprintIssue"
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
          >
            <IssueItem
              key={item.issueId}
              item={item}
              onClick={this.myOnMouseDown}
            />
            {provided.placeholder}
          </div>
        )}
      </Draggable>
    ));
  }
}

export default IssueList;
