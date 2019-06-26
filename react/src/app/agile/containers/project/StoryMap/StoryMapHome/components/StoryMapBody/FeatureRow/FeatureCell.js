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
    const { epic, otherData } = this.props;
    const { epicId } = epic;
    StoryMapStore.addFeature(epic);
  }

  handleCreateFeature=(newFeature) => {
    const { epicIndex } = this.props;
    StoryMapStore.afterCreateFeature(epicIndex, newFeature);
  }

  render() {
    const {
      epic, otherData, isLastColumn, lastCollapse, epicIndex,
    } = this.props;
    const { featureCommonDOList, adding } = epic;
    const { collapse } = otherData || {};
    const hasAddingFeature = find(featureCommonDOList, { adding: true });
    const { isInProgram } = IsInProgramStore;
    return (
      collapse ? null : (
        <Cell
          epicIndex={epicIndex}
          lastCollapse={lastCollapse}
          collapse={collapse}
          style={{
            position: 'sticky',
            top: 60,
            zIndex: 6,
            background: 'white',
            ...collapse ? { zIndex: 'unset' } : {}, 
          }}
        >
          { (
            <div style={{ display: 'flex' }}>        
              {adding ? null : (
                <Fragment>
                  {featureCommonDOList.filter(feature => !feature.adding).map(feature => <FeatureColumn epic={epic} feature={feature} otherData={otherData.feature[feature.issueId]} />)}             
                  {/* 没有关联feature，但是关联了史诗的故事 */}
                  {otherData.feature.none && otherData.feature.none.storys.length > 0 ? <FeatureColumn isLast={isLastColumn} epic={epic} feature={{ issueId: 'none' }} otherData={otherData.feature.none} /> : null}                  
                </Fragment>
              )}
            </div>
        )}
        </Cell>
      )
    );
  }
}

FeatureCell.propTypes = {

};

export default FeatureCell;
