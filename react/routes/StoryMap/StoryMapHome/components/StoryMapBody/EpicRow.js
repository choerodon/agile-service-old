import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../stores/project/StoryMap/StoryMapStore';
import EpicCell from './EpicCell';

@observer
class EpicRow extends Component {
  render() {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;

    return (
      <tr style={{ height: 60 }}>
        {epicList.map((epic, index) => (
          <EpicCell 
            lastCollapse={index > 0 ? storyData[epicList[index - 1].issueId] && storyData[epicList[index - 1].issueId].collapse : false}
            isLastEpic={index === epicList.length - 1}
            index={index}
            epic={epic}
            otherData={storyData[epic.issueId]}
          />
        ))}
      </tr>
    );
  }
}

EpicRow.propTypes = {

};

export default EpicRow;
