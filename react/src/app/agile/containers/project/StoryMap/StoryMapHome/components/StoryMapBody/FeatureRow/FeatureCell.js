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
    const { epicData, otherData, isLastColumn } = this.props;
    const { featureCommonDOList, adding } = epicData;
    const { collapse } = otherData || {};
    const hasAddingFeature = find(featureCommonDOList, { adding: true });
    const { isInProgram } = IsInProgramStore;
    return (
      <Cell style={{
        position: 'sticky',
        top: 60,
        zIndex: 6,
        background: 'white',
        boxShadow: 'inset 0 -1px 0 #D8D8D8,inset 1px 0 0 #D8D8D8',
        border: 'none',       
        ...collapse ? { boxShadow: 'none' } : {}, 
      }}
      >
        {collapse ? null : (
          <div style={{ display: 'flex' }}>        
            {adding ? null : (
              <Fragment>
                {featureCommonDOList.filter(feature => !feature.adding).map(feature => <FeatureColumn epic={epicData} feature={feature} otherData={otherData.feature[feature.issueId]} />)}             
                {/* 没有关联feature，但是关联了史诗的故事 */}
                {otherData.feature.none ? <FeatureColumn isLast={isLastColumn} epic={epicData} feature={{ issueId: 'none' }} otherData={otherData.feature.none} /> : null}
                {hasAddingFeature 
                  ? <CreateFeature onCreate={this.handleCreateFeature} epicId={epicData.issueId} /> 
                  : !isInProgram && <AddCard style={{ height: CardHeight, marginTop: 5 }} onClick={this.handleAddFeatureClick} />}
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
