import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryRow from './StoryRow';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryArea extends Component {
  renderWithSwimVersion=() => {
    const { versionList, swimLine } = StoryMapStore;   
    return versionList.map((version, index) => <StoryRow isLastRow={index === versionList.length - 1} version={version} storyCollapse={version.collapse} />);
  }

  renderWithNone=() => <StoryRow isLastRow />

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
