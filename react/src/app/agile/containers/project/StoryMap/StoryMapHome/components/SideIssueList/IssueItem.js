import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Tooltip } from 'choerodon-ui';
import { find } from 'lodash';
import { DragSource, DropTarget } from 'react-dnd';
import TypeTag from '../../../../../../components/TypeTag';
import { issueLink } from '../../../../../../common/utils';
import { storyMove } from '../../../../../../api/StoryMapApi';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

import './IssueItem.scss';


const preFix = 'c7nagile-SideIssueList-IssueItem';
@observer
class IssueItem extends Component {
  render() {
    const {
      issue, connectDragSource, 
    } = this.props;
    const {
      issueTypeDTO, summary, issueNum, issueId,
    } = issue;
    // const opacity = isDragging ? 0 : 1;
    return (
      connectDragSource(  
        <div className={preFix}>
          <TypeTag data={issueTypeDTO} />
          <Link target="_blank" to={issueLink(issueId, issueTypeDTO && issueTypeDTO.typeCode, issueNum)} style={{ color: '#3F51B5', margin: '0 10px' }}>{issueNum}</Link>            
          <div className={`${preFix}-summary`}>
            <Tooltip title={summary}>
              {summary}
            </Tooltip>
          </div>
        </div>,
      )
    );
  }
}

IssueItem.propTypes = {

};

export default DragSource(
  'story',
  {
    beginDrag: props => ({
      type: 'side',
      issue: props.issue,
    }),
    endDrag(props, monitor) {
      const source = monitor.getItem();       
      const dropResult = monitor.getDropResult();
      if (dropResult) {
        const { epic: { issueId: targetEpicId }, feature: { issueId: targetFeatureId }, version } = dropResult;
        const { versionId: targetVersionId } = version || {};
        const { issue: { issueId, storyMapVersionDOList } } = source;          
        const storyMapDragDTO = {
          versionIssueIds: [],
          versionId: 0, // 要关联的版本id
          epicId: targetEpicId, // 要关联的史诗id
          versionIssueRelDTOList: [],
          // 问题id列表，移动到史诗，配合epicId使用
          epicIssueIds: [issueId],
          featureId: 0, // 要关联的特性id
          // 问题id列表，移动到特性，配合featureId使用
          featureIssueIds: [],
        };
        if (targetFeatureId && targetFeatureId !== 'none') {
          storyMapDragDTO.featureId = targetFeatureId;
          storyMapDragDTO.featureIssueIds = [issueId];
        }
        if (targetVersionId && !find(storyMapVersionDOList, { versionId: targetVersionId }) && targetVersionId !== 'none') {
          storyMapDragDTO.versionId = targetVersionId;
          storyMapDragDTO.versionIssueIds = [issueId];
        }
        // console.log(storyMapDragDTO);
        storyMove(storyMapDragDTO).then(() => {
          // StoryMapStore.removeStoryFromStoryMap(story);
          StoryMapStore.getStoryMap();
          StoryMapStore.loadIssueList();
        });
      }
    },
  },
  (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  }),
)(IssueItem);
