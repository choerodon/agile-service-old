import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryRow from './StoryRow';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryArea extends Component {
  renderWithSwimVersion=() => {
    const { versionList, swimLine } = StoryMapStore;   
    return versionList.map(version => <StoryRow version={version} storyCollapse={version.collapse} />);
  }

  renderWithNone=() => <StoryRow />

  renderStory=() => {
    const { swimLine } = StoryMapStore;
    switch (swimLine) {
      case 'none': {
        return this.renderWithNone();
      }
      case 'version': {
        return this.renderWithSwimVersion();
      }
      default: return null;
    }
  }

  render() {
    const { versionList, swimLine } = StoryMapStore;
    return this.renderStory();
  }
}

StoryArea.propTypes = {

};

export default StoryArea;
