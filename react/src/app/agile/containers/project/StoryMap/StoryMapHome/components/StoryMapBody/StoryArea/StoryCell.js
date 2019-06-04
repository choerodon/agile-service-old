import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryColumn from './StoryColumn';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import AddCard from '../AddCard';
import { CardHeight } from '../../../Constants';

@observer
class StoryCell extends Component {
  render() {
    const { epic, otherData } = this.props;
    const { storyData, storyMapData } = StoryMapStore;
    const { issueId: epicId, featureCommonDOList, adding } = epic;
    const targetEpic = storyData[epicId];
    const { collapse } = otherData || {};
    return (
      <Cell>
        {collapse ? null : (
          <div style={{ display: 'flex' }}>
            {
              adding ? null : (
                <Fragment>
                  {featureCommonDOList.filter(feature => !feature.adding).map((feature) => {
                    const targetFeature = targetEpic.feature[feature.issueId] || {};
                    return targetFeature && <StoryColumn storys={targetFeature.storys} width={targetFeature.width} />;
                  })}
                  {/* <AddCard style={{ height: CardHeight, marginTop: 5 }} /> */}
                </Fragment>
              )
            }           
          </div>
        )}
      </Cell>
    );
  }
}

StoryCell.propTypes = {

};

export default StoryCell;
