import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Draggable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
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
    const { issueRefresh, refresh } = this.props;
    return (
      BacklogStore.getEpicData.map((item, index) => (
        <div
          role="none"
          onMouseEnter={(e) => {
            if (BacklogStore.isDragging) {
              BacklogStore.toggleIssueDrag(true);
              e.currentTarget.style.border = '2px dashed green';
            }
          }}
          onMouseLeave={(e) => {
            if (BacklogStore.isDragging) {
              BacklogStore.toggleIssueDrag(false);
              e.currentTarget.style.border = 'none';
            }
          }}
          onMouseUp={(e) => {
            if (BacklogStore.getIsDragging) {
              BacklogStore.toggleIssueDrag(false);
              e.currentTarget.style.border = 'none';
              BacklogStore.axiosUpdateIssuesToEpic(
                item.issueId, BacklogStore.getIssueWithEpicOrVersion,
              ).then((res) => {
                issueRefresh();
                refresh();
              }).catch((error) => {
                issueRefresh();
                refresh();
              });
            }
          }}
          onClick={(e) => {
            this.handleClickEpic(item.issueId);
          }}
        >
          <Draggable draggableId={`epicItem-${index}`} key={item.issueId} index={index}>
            {(draggableProvided, draggableSnapshot) => (
              <DraggableEpic
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
