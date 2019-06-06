import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Column from '../Column';
import StoryCard from './StoryCard';
import CreateStory from './CreateStory';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryColumn extends Component {
  handleCreateStory=(newStory) => {
    const { epicIndex, featureIndex } = this.props;
    StoryMapStore.afterCreateStory(newStory);
  }

  render() {
    const {
      storys, width, epic, feature, version,
    } = this.props;
    // console.log(storys);
    return (
      <Column width={width}>
        <div>
          {storys && storys.map(story => <StoryCard story={story} />)}
          <CreateStory onCreate={this.handleCreateStory} epic={epic} feature={feature} version={version} />
        </div>
      </Column>
    );
  }
}

StoryColumn.propTypes = {

};

export default StoryColumn;
