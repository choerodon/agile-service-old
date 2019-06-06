import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { DragSource } from 'react-dnd';
import { CardWidth, CardHeight, CardMargin } from '../../../Constants';
import { storyMove } from '../../../../../../../api/StoryMapApi';
import Card from '../Card';
import './StoryCard.scss';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

class StoryCard extends Component {
  handlRemoveStory=() => {
    const { story } = this.props;
    const { issueId } = story;
    const storyMapDragDTO = {
      // 问题id列表，移动到版本，配合versionId使用
      // versionIssueIds: [],     
      // epicId: 0, // 要关联的史诗id          
      // epicIssueIds: [issueId],
      featureId: 0, // 要关联的特性id
      // 问题id列表，移动到特性，配合featureId使用
      featureIssueIds: [issueId],
    };
    storyMove(storyMapDragDTO).then(() => {
      StoryMapStore.removeStoryFromStoryMap(story);
    });
  }

  render() {
    const { story, connectDragSource } = this.props;
    return (    
      <Card className="c7nagile-StoryMap-StoryCard" saveRef={connectDragSource}>
        <Icon type="close" className="c7nagile-StoryMap-StoryCard-delete" onClick={this.handlRemoveStory} />
        <div className="summary">
          {story.summary}
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
    beginDrag: props => ({ story: props.story }),
    endDrag(props, monitor) {
      const item = monitor.getItem();
      const dropResult = monitor.getDropResult();
      const { story } = item;
      const { epicId, featureId, storyMapVersionDOList } = item;
      const { epic, feature, version } = dropResult;
  
      if (dropResult) {
        alert(`You dropped ${item.name} into ${JSON.stringify(dropResult)}!`);
      }
    },
  },
  (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging(),
  }),
)(StoryCard);
