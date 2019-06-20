import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon, Tooltip } from 'choerodon-ui';
import { DragSource } from 'react-dnd';
import { find } from 'lodash';
import { Link } from 'react-router-dom';
import { programIssueLink, issueLink, getProjectId } from '../../../../../../../common/utils';
import { CardWidth, CardHeight, CardMargin } from '../../../Constants';
import { storyMove } from '../../../../../../../api/StoryMapApi';
import AutoScroll from '../../../../../../../common/AutoScroll';
import Card from '../Card';
import './StoryCard.scss';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

class StoryCard extends Component {
  componentDidMount() {
    this.AutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('minimap-container-scroll')[0],      
      pos: {
        left: 200,
        top: 150,
        bottom: 150,
        right: 150,
      },
      type: 'drag',
    });
  }

  setZIndex=() => {
    this.container.style.zIndex = 9999;   
  }

  resetZIndex=() => {
    this.container.style.zIndex = 'unset';   
  }

  saveRef=(ref) => {
    const { connectDragSource } = this.props;
    connectDragSource(ref);
    this.container = ref;
  }

  handleMouseDown = (e) => {
    this.AutoScroll.prepare(e);
  }

  handlRemoveStory = () => {
    const { story } = this.props;
    const { issueId } = story;
    const storyMapDragDTO = {
      // 问题id列表，移动到版本，配合versionId使用
      // versionIssueIds: [],     
      epicId: 0, // 要关联的史诗id          
      epicIssueIds: [issueId],
      featureId: 0, // 要关联的特性id
      // 问题id列表，移动到特性，配合featureId使用
      featureIssueIds: [issueId],
    };
    storyMove(storyMapDragDTO).then(() => {
      StoryMapStore.removeStoryFromStoryMap(story);
      StoryMapStore.loadIssueList();
    });
  }

  render() {   
    const {
      story, connectDragSource, index, rowIndex, 
    } = this.props;
    const { issueId, issueNum, summary } = story;
    return (
      <Card className={`c7nagile-StoryMap-StoryCard ${index === 0 && rowIndex === 0 ? 'minimapCard' : ''}`} saveRef={this.saveRef} onMouseDown={this.handleMouseDown}>
        <Icon type="close" className="c7nagile-StoryMap-StoryCard-delete" onClick={this.handlRemoveStory} />        
        <div className="summary">
          <Tooltip title={summary}>
            {issueId && issueNum ? (
              <Link to={issueLink(issueId, 'story', issueNum)} style={{ marginRight: 5 }} target="_blank">
                  #
                {issueNum}
              </Link>
            ) : null}
            {summary}
          </Tooltip>       
        </div>
      </Card>
    );
  }
}

StoryCard.propTypes = {

};

export default DragSource(
  'story',
  {
    beginDrag: (props, monitor, component) => {
      if (component && component.resetZIndex) {
        component.setZIndex();
        setTimeout(() => {
          component.resetZIndex();
        });
      }
      
      return { story: props.story, version: props.version };
    },
    endDrag(props, monitor) {
      const item = monitor.getItem();
      const dropResult = monitor.getDropResult();
      if (!dropResult) {
        return;
      }
      const { story, version: sourceVersion } = item;
      const {
        issueId, epicId, storyMapVersionDOList,
      } = story;
      const featureId = story.featureId || 'none';
      const { epic: { issueId: targetEpicId }, feature: { issueId: targetFeatureId }, version } = dropResult;
      const { versionId: targetVersionId } = version || {};
      const storyMapDragDTO = {
        versionIssueIds: [],
        versionId: 0, // 要关联的版本id
        epicId: 0, // 要关联的史诗id
        versionIssueRelDTOList: [],
        // 问题id列表，移动到史诗，配合epicId使用
        epicIssueIds: [],
        featureId: 0, // 要关联的特性id
        // 问题id列表，移动到特性，配合featureId使用
        featureIssueIds: [],
      };   
      // 史诗，特性，版本都不变时
      if (epicId === targetEpicId && featureId === targetFeatureId) {
        if (StoryMapStore.swimLine === 'version') {
          if (find(storyMapVersionDOList, { versionId: targetVersionId }) || (storyMapVersionDOList.length === 0 && targetVersionId === 'none')) {
            return;
          }
        } else if (StoryMapStore.swimLine === 'none') {
          return;
        }
      }
      if (epicId !== targetEpicId) {
        storyMapDragDTO.epicId = targetEpicId;
        storyMapDragDTO.epicIssueIds = [issueId];
      }
      if (featureId !== targetFeatureId) {
        // 移动到直接关联史诗的列，将史诗传过去，否则会将史诗清掉
        if (targetFeatureId === 'none') {
          storyMapDragDTO.epicId = targetEpicId;
          storyMapDragDTO.epicIssueIds = [issueId];
          storyMapDragDTO.featureId = 0;
        } else {
          storyMapDragDTO.featureId = targetFeatureId;
        }
        storyMapDragDTO.featureId = targetFeatureId === 'none' ? 0 : targetFeatureId;
        storyMapDragDTO.featureIssueIds = [issueId];
      }
      // 对版本进行处理
      if (StoryMapStore.swimLine === 'version') {
        // 在不同的版本移动
        if (sourceVersion.versionId !== targetVersionId) {
          // 如果原先有版本，就移除离开的版本
          if (storyMapVersionDOList.length > 0) {
            const removeVersion = find(storyMapVersionDOList, { versionId: sourceVersion.versionId });
            if (removeVersion) {
              storyMapDragDTO.versionIssueRelDTOList = [{ ...removeVersion, issueId }];
            }
          }
        }
        
        if (!find(storyMapVersionDOList, { versionId: targetVersionId })) {          
          storyMapDragDTO.versionIssueIds = [issueId];
          // 拖到未规划
          if (targetVersionId === 'none') {
            storyMapDragDTO.versionId = 0;          
          } else {
            storyMapDragDTO.versionId = targetVersionId;            
          }
        }       
      }

      // console.log(storyMapDragDTO);
      storyMove(storyMapDragDTO).then(() => {
        // StoryMapStore.removeStoryFromStoryMap(story);
        StoryMapStore.getStoryMap();
      });
    },
  },
  (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  }),
)(StoryCard);
