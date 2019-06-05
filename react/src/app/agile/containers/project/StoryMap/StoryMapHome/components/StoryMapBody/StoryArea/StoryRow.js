import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryCell from './StoryCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryRow extends Component {
  getFirstNotCollapseIndex = () => {
    const { storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    for (let i = 0; i < epicWithFeature.length; i += 1) {
      if (!storyData[epicWithFeature[i].issueId].collapse) {
        return i;
      }
    }
    return 0;
  }

  render() {
    const { collapseEpics, storyMapData, storyData } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    const firstNotCollapseIndex = this.getFirstNotCollapseIndex();
    const { storyCollapse } = this.props;
    return (
      <tr style={{ ...storyCollapse ? { height: 0 } : {} }}>
        {epicWithFeature.map((epic, index) => <StoryCell showTitle={firstNotCollapseIndex === index} epic={epic} otherData={storyData[epic.issueId]} {...this.props} />)}
      </tr>
    );
  }
}

StoryRow.propTypes = {

};

export default StoryRow;
