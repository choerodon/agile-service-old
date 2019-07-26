import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import FeatureCell from './FeatureCell';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class FeatureRow extends Component {
  render() {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;
    return (
      <tr style={{ height: 82 }}>
        {epicList.map((epic, index) => (
          <FeatureCell
            lastCollapse={index > 0 ? storyData[epicList[index - 1].issueId] && storyData[epicList[index - 1].issueId].collapse : false}
            isLastColumn={index === epicList.length - 1} 
            epicIndex={index}
            epic={epic}
            otherData={storyData[epic.issueId]}
          />
        )) }
      </tr>
    );
  }
}

FeatureRow.propTypes = {

};

export default FeatureRow;
