import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Icon, Button } from 'choerodon-ui';
import StoryColumn from './StoryColumn';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import AddCard from '../AddCard';
import { CardHeight } from '../../../Constants';
import './StoryCell.scss';

@observer
class StoryCell extends Component {
  renderTitle = (storyCollapse) => {
    const { swimLine } = StoryMapStore;
    const { version } = this.props;
    
    switch (swimLine) {
      case 'none': {
        return null;
      }
      case 'version': {
        return (
          <div className="c7nagile-StoryMap-StoryCell-title">
            <Icon
              style={{ marginRight: 15 }}
              type={storyCollapse ? 'expand_less' : 'expand_more'}
              onClick={() => {
                StoryMapStore.collapseStory(version.versionId);
              }}
            />
            {version.name}
            {version.versionId === 'none' && <Button className="c7nagile-StoryMap-StoryCell-title-createBtn" type="primary" icon="playlist_add">创建版本</Button>}
          </div>
        );
      }
      default: return null;
    }
  }

  getStorys = (targetFeature) => {
    const { swimLine } = StoryMapStore;
    const { version } = this.props;
    switch (swimLine) {
      case 'none': {
        return targetFeature.storys;
      }
      case 'version': {
        return targetFeature.version[version.versionId];
      }
      default: return [];
    }
  }

  render() {
    const {
      epic, otherData, showTitle, version, storyCollapse, isLastRow,
    } = this.props;
    const { storyData, storyMapData } = StoryMapStore;
    const { issueId: epicId, featureCommonDOList, adding } = epic;
    const targetEpic = storyData[epicId];
    const { collapse } = otherData || {};
    return (
      <Cell style={{ ...collapse ? { borderBottom: isLastRow ? '1px solid #D8D8D8' : 'none', borderTop: 'none' } : {} }}>
        {collapse ? null : (
          <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            <div style={{ textAlign: 'left', marginLeft: -20, height: 30 }}>
              {showTitle && this.renderTitle(storyCollapse)}
            </div>
            <div style={{ display: 'flex', flex: 1 }}>
              {
                adding ? null : (
                  <Fragment>
                    {storyCollapse ? null : featureCommonDOList.filter(feature => !feature.adding).map((feature) => {
                      const targetFeature = targetEpic.feature[feature.issueId] || {};
                      return targetFeature && <StoryColumn storys={this.getStorys(targetFeature)} width={targetFeature.width} />;
                    })}
                    {/* <AddCard style={{ height: CardHeight, marginTop: 5 }} /> */}
                  </Fragment>
                )
              }
            </div>
          </div>
        )}
      </Cell>
    );
  }
}

StoryCell.propTypes = {

};

export default StoryCell;
