import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import FeatureCell from './FeatureCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class FeatureRow extends Component {
  render() {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;
    return (
      <tr style={{ height: 82 }}>
        {epicList.map((epicData, index) => <FeatureCell lastCollapse={index > 0 ? storyData[epicList[index - 1].issueId].collapse : false} isLastColumn={index === epicList.length - 1} EpicIndex={index} epicData={epicData} otherData={storyData[epicData.issueId]} />) }
      </tr>
    );
  }
}

FeatureRow.propTypes = {

};

export default FeatureRow;
