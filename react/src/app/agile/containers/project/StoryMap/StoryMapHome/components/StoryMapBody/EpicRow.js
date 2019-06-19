import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import EpicCell from './EpicCell';

@observer
class EpicRow extends Component {
  render() {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;

    return (
      <tr style={{ height: 60 }}>
        {epicList.map((epicData, index) => <EpicCell index={index} epicData={epicData} otherData={storyData[epicData.issueId]} />)}
      </tr>
    );
  }
}

EpicRow.propTypes = {

};

export default EpicRow;