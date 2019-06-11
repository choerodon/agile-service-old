import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryCell from './StoryCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryRow extends Component {
  getFirstNotCollapseIndex = () => {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;
    for (let i = 0; i < epicList.length; i += 1) {
      if (!storyData[epicList[i].issueId].collapse) {
        return i;
      }
    }
    return 0;
  }

  render() {
    const { storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    const epicList = StoryMapStore.getEpicList;
    const firstNotCollapseIndex = this.getFirstNotCollapseIndex();
    const { storyCollapse } = this.props;
    return (
      <tr style={{ ...storyCollapse ? { height: 0 } : {} }}>
        {epicList.map((epic, index) => <StoryCell epicIndex={index} isLastColumn={index === epicWithFeature.length - 1} showTitle={firstNotCollapseIndex === index} epic={epic} otherData={storyData[epic.issueId]} {...this.props} />)}
      </tr>
    );
  }
}

StoryRow.propTypes = {

};

export default StoryRow;
