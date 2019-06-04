import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryCell from './StoryCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryRow extends Component {
  render() {
    const { collapseEpics, storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    return (
      <tr>
        {epicWithFeature.map(epic => <StoryCell epic={epic} otherData={storyData[epic.issueId]} />)}
      </tr>
    );
  }
}

StoryRow.propTypes = {

};

export default StoryRow;
