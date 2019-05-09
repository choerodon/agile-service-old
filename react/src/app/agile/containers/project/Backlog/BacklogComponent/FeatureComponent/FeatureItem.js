import React, { Component } from 'react';
import { stores, axios, store } from 'choerodon-front-boot';
import { observer, inject } from 'mobx-react';
import { Droppable, Draggable, DragDropContext } from 'react-beautiful-dnd';
import {
  Dropdown, Menu, Input, Icon, message,
} from 'choerodon-ui';
import _ from 'lodash';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import DraggableFeature from './DraggableFeature';

const { AppState } = stores;
// @inject('AppState')
@observer
class FeatureItem extends Component {
  /**
   *点击featureItem的事件
   *
   * @param {*} type
   * @memberof 
   */
  handleClickFeature = (type) => {
    const { clickFeature } = this.props;
    clickFeature(type);
  };

  render() {
    const { issueRefresh, refresh } = this.props;
    return (
      BacklogStore.getFeatureData.map((item, index) => (
        <div
          role="none"
          // onMouseEnter={(e) => {
          //   if (BacklogStore.isDragging) {
          //     BacklogStore.toggleIssueDrag(true);
          //     e.currentTarget.style.border = '2px dashed green';
          //   }
          // }}
          // onMouseLeave={(e) => {
          //   if (BacklogStore.isDragging) {
          //     BacklogStore.toggleIssueDrag(false);
          //     e.currentTarget.style.border = 'none';
          //   }
          // }}
          // onMouseUp={(e) => {
          //   if (BacklogStore.getIsDragging) {
          //     BacklogStore.toggleIssueDrag(false);
          //     e.currentTarget.style.border = 'none';
          //     BacklogStore.axiosUpdateIssuesToFeature(
          //       item.issueId, BacklogStore.getIssueWithFeatureOrVersion,
          //     ).then((res) => {
          //       issueRefresh();
          //       refresh();
          //     }).catch((error) => {
          //       issueRefresh();
          //       refresh();
          //     });
          //   }
          // }}
          onClick={(e) => {
            this.handleClickFeature(item.issueId);
          }}
        >
          <Draggable isDragDisabled draggableId={`epicItem-${index}`} key={item.issueId} index={index}>
            {(draggableProvided, draggableSnapshot) => (
              <DraggableFeature
                item={item}
                refresh={refresh}
                draggableProvided={draggableProvided}
                draggableSnapshot={draggableSnapshot}
                handleClickFeature={this.handleClickFeature}
              />
            )}
          </Draggable>
        </div>
      ))
    );
  }
}

export default FeatureItem;
