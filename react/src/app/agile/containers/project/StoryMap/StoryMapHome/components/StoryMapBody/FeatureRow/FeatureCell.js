import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { find } from 'lodash';
import FeatureColumn from './FeatureColumn';
import Cell from '../Cell';
import AddCard from '../AddCard';
import CreateFeature from './CreateFeature';
import { CardHeight } from '../../../Constants';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import IsInProgramStore from '../../../../../../../stores/common/program/IsInProgramStore';

@observer
class FeatureCell extends Component {
  handleAddFeatureClick=() => {
    const { epicData, otherData } = this.props;
    const { epicId } = epicData;
    StoryMapStore.addFeature(epicData);
  }

  handleCreateFeature=(newFeature) => {
    const { EpicIndex } = this.props;
    StoryMapStore.afterCreateFeature(EpicIndex, newFeature);
  }

  render() {
    const { epicData, otherData } = this.props;
    const { featureCommonDOList, adding } = epicData;
    const { collapse } = otherData || {};
    const hasAddingFeature = find(featureCommonDOList, { adding: true });
    const { isInProgram } = IsInProgramStore;
    return (
      <Cell style={{ ...collapse ? { borderBottom: 'none', borderTop: 'none' } : {} }}>
        {collapse ? null : (
          <div style={{ display: 'flex' }}>        
            {adding ? null : (
              <Fragment>
                {featureCommonDOList.filter(feature => !feature.adding).map(feature => <FeatureColumn feature={feature} otherData={otherData.feature[feature.issueId]} />)}
                {hasAddingFeature 
                  ? <CreateFeature onCreate={this.handleCreateFeature} epicId={epicData.issueId} /> 
                  : !isInProgram && <AddCard style={{ height: CardHeight, marginTop: 5 }} onClick={this.handleAddFeatureClick} />}
                {/* 没有关联feature，但是关联了史诗的故事 */}
                {otherData.feature.none && otherData.feature.none.storys.length > 0 && <FeatureColumn feature={{ issueId: 'none' }} otherData={otherData.feature.none} />}
              </Fragment>
            )}
          </div>
        )}
      </Cell>
    );
  }
}

FeatureCell.propTypes = {

};

export default FeatureCell;
