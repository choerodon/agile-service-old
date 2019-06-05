import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import FeatureCell from './FeatureCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class FeatureRow extends Component {
  render() {
    const { collapseEpics, storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    
    return (
      <tr style={{ height: 82 }}>
        {epicWithFeature ? epicWithFeature.map(epicData => <FeatureCell epicData={epicData} otherData={storyData[epicData.issueId]} />) : null}
      </tr>
    );
  }
}

FeatureRow.propTypes = {

};

export default FeatureRow;
