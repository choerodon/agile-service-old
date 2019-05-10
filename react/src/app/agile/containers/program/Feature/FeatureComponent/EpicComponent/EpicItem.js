import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Draggable } from 'react-beautiful-dnd';
import DraggableEpic from './DraggableEpic';

@observer
class EpicItem extends Component {
  /**
   *点击epicItem的事件
   *
   * @param {*} type
   * @memberof Epic
   */
  handleClickEpic = (type) => {
    const { clickEpic } = this.props;
    clickEpic(type);
  };

  render() {
    const { issueRefresh, refresh, store } = this.props;
    return (
      store.getEpicData.map((item, index) => (
        <div
          key={item.issueId}
          role="none"
          onMouseEnter={(e) => {
            if (store.isDragging) {
              store.toggleIssueDrag(true);
              e.currentTarget.style.border = '2px dashed green';
            }
          }}
          onMouseLeave={(e) => {
            if (store.isDragging) {
              store.toggleIssueDrag(false);
              e.currentTarget.style.border = 'none';
            }
          }}
          onMouseUp={(e) => {
            if (store.getIsDragging) {
              store.toggleIssueDrag(false);
              e.currentTarget.style.border = 'none';
              store.moveIssuesToEpic(
                item.issueId, store.getIssueWithEpic,
              ).then(() => {
                issueRefresh();
                refresh();
              }).catch(() => {
                issueRefresh();
                refresh();
              });
            }
          }}
          onClick={() => {
            this.handleClickEpic(item.issueId);
          }}
        >
          <Draggable draggableId={`epicItem-${index}`} key={item.issueId} index={index}>
            {(draggableProvided, draggableSnapshot) => (
              <DraggableEpic
                store={store}
                item={item}
                refresh={refresh}
                draggableProvided={draggableProvided}
                draggableSnapshot={draggableSnapshot}
                handleClickEpic={this.handleClickEpic}
              />
            )}
          </Draggable>
        </div>
      ))
    );
  }
}

export default EpicItem;
