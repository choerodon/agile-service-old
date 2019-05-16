import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import { observer } from 'mobx-react';
import IssueItem from './IssueItem';

@observer
class IssueList extends Component {
  myOnMouseDown = (e, item) => {
    e.stopPropagation();
    const { store, piId } = this.props;
    if (!(e.shiftKey && (e.ctrlKey || e.metaKey))) {
      if (e.shiftKey) {
        store.dealWithMultiSelect(piId, item, 'shift');
      } else if (e.ctrlKey || e.metaKey) {
        store.dealWithMultiSelect(piId, item, 'ctrl');
      } else {
        store.clickedOnce(piId, item);
      }
    }
  };

  render() {
    const { piId, store } = this.props;

    return store.getIssueMap.get(piId).map((item, index) => (
      <Draggable key={item.issueId} draggableId={item.issueId} index={index}>
        {provided => (
          <div
            key={item.issueId}
            className="c7n-feature-sprintIssue"
            ref={provided.innerRef}
            {...provided.draggableProps}
            {...provided.dragHandleProps}
          >
            <IssueItem
              key={item.issueId}
              item={{ ...item, piId }}
              onClick={this.myOnMouseDown}
              store={store}
            />
            {provided.placeholder}
          </div>
        )}
      </Draggable>
    ));
  }
}

export default IssueList;
