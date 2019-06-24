import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { DropTarget } from 'react-dnd';
import Column from '../Column';
import StoryCard from './StoryCard';
import CreateStory from './CreateStory';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import './StoryColumn.scss';

@observer
class StoryColumn extends Component {
  handleCreateStory = (newStory) => {
    StoryMapStore.afterCreateStory(newStory);
  }


  render() {
    // console.log('render');
    const {
      storys, width, epic, feature, version, connectDropTarget, isOver, rowIndex,
    } = this.props;    
    return (
      <Column
        width={width}
        saveRef={connectDropTarget} 
        style={{ background: isOver ? 'rgb(240,240,240)' : 'white', position: 'relative' }}
      >
        <div style={{ display: 'flex', flexWrap: 'wrap' }}>
          {storys && storys.map((story, index) => <StoryCard index={index} rowIndex={rowIndex} story={story} version={version} />)}
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
    // canDrop: monitor.canDrop(), //去掉可以优化性能
  }),
)(StoryColumn);
