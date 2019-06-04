import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import EpicCell from './EpicCell';

@observer
class EpicRow extends Component {
  render() {
    const { collapseEpics, storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};

    return (
      <tr>
        {epicWithFeature ? epicWithFeature.map(epicData => <EpicCell epicData={epicData} otherData={storyData[epicData.issueId]} />) : null}
      </tr>
    );
  }
}

EpicRow.propTypes = {

};

export default EpicRow;
