import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import StoryColumn from './StoryColumn';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import { ColumnMinHeight } from '../../../Constants';
import './StoryCell.scss';

@observer
class StoryCell extends Component {
  handleCreateVersionClick = () => {
    StoryMapStore.setCreateModalVisible(true);
  }

  getStorys = (targetFeature) => {
    const { swimLine } = StoryMapStore;
    const { version } = this.props;
    try {
      switch (swimLine) {
        case 'none': {
          return targetFeature.storys;
        }
        case 'version': {
          return targetFeature.version[version.versionId];
        }
        default: return [];
      }
    } catch (error) {
      return [];
    }
  }

  render() {
    const {
      epic, otherData, storyCollapse, isLastRow, isLastColumn, epicIndex, lastCollapse,
    } = this.props;
    const { storyData, swimLine } = StoryMapStore;
    const { issueId: epicId, featureCommonDOList, adding } = epic;
    const targetEpic = storyData[epicId];
    const { collapse } = otherData || {};
    // let epicStorys = [];
    // if (targetEpic && targetEpic.feature && targetEpic.feature.none) {
    //   epicStorys = targetEpic.feature.none.storys;
    // }
    // const featureList = epicStorys.length > 0 ? featureCommonDOList.concat([{ issueId: 'none' }]) : featureCommonDOList;
    // 没有史诗不显示直接关联史诗的列
    const featureList = epicId === 0 ? featureCommonDOList : featureCommonDOList.concat([{ issueId: 'none' }]);

    return (
      !storyCollapse && !collapse && (
        <Cell epicIndex={epicIndex} lastCollapse={lastCollapse} collapse={collapse}>
          {collapse ? null : (
            <div style={{
              minHeight: ColumnMinHeight, height: '100%', display: 'flex', flexDirection: 'column',
            }}
            >
              <div style={{ display: 'flex', flex: 1 }}>
                {
                  adding ? null : (
                    <Fragment>
                      {featureList.filter(feature => !feature.adding).map((feature, index) => {
                        const targetFeature = targetEpic.feature[feature.issueId] || {};
                        if (targetFeature) {
                          const storys = this.getStorys(targetFeature);
                          if (feature.issueId === 'none' && storys.length === 0) {
                            return null;
                          }
                          return (
                            <StoryColumn 
                              feature={feature}
                              featureIndex={index}
                              isLast={isLastColumn && index === featureList.length - 1}
                              storys={storys}
                              width={targetFeature.width}
                              {...this.props}
                            />
                          );
                        } else {
                          return null;
                        }              
                      })}
                    </Fragment>
                  )
                }
              </div>
            </div>
          )}
        </Cell>
      )
    );
  }
}

StoryCell.propTypes = {

};

export default StoryCell;
