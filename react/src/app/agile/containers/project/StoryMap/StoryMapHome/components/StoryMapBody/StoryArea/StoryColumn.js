import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { DropTarget } from 'react-dnd';
import Column from '../Column';
import StoryCard from './StoryCard';
import CreateStory from './CreateStory';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryColumn extends Component {
  handleCreateStory = (newStory) => {
    const { epicIndex, featureIndex } = this.props;
    StoryMapStore.afterCreateStory(newStory);
  }

  render() {
    const {
      storys, width, epic, feature, version, connectDropTarget, isOver,
    } = this.props;
    // console.log(storys);
    return (
      <Column width={width} saveRef={connectDropTarget} style={{ background: isOver ? 'rgb(240,240,240)' : 'white' }}>
        <div style={{ display: 'flex', flexWrap: 'wrap' }}>
          {storys && storys.map(story => <StoryCard story={story} version={version} />)}
          <CreateStory onCreate={this.handleCreateStory} epic={epic} feature={feature} version={version} />
        </div>
      </Column>
    );
  }
}

StoryColumn.propTypes = {

};

export default DropTarget(
  'story',
  {
    drop: props => ({ epic: props.epic, feature: props.feature, version: props.version }),
  },
  (connect, monitor) => ({
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver(),
    canDrop: monitor.canDrop(),
  }),
)(StoryColumn);
