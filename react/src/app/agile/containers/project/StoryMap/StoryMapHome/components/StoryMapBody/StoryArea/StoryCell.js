import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryColumn from './StoryColumn';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryCell extends Component {
  render() {
    const { epic, otherData } = this.props;
    const { storyData, storyMapData } = StoryMapStore;
    const { issueId: epicId, featureCommonDOList } = epic;
    const targetEpic = storyData[epicId];
    const { collapse } = otherData;
    return (
      <Cell>
        {collapse ? null : (
          <div style={{ display: 'flex' }}>
            {featureCommonDOList.map((feature) => {
              const targetFeature = targetEpic.feature[feature.issueId];
              return targetFeature && <StoryColumn storys={targetFeature.storys} />;
            })}
          </div>
        )}
      </Cell>
    );
  }
}

StoryCell.propTypes = {

};

export default StoryCell;
