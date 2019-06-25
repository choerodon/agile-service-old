import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';
import './TitleCell.scss';

@observer
class TitleCell extends Component {
  handleCreateVersionClick = () => {
    StoryMapStore.setCreateModalVisible(true);
  }

  renderTitle = (storyCollapse) => {
    const { swimLine, isFullScreen } = StoryMapStore;
    const { version } = this.props;

    switch (swimLine) {
      case 'none': {
        return null;
      }
      case 'version': {
        return (
          <div>
            <Icon
              style={{ marginRight: 15 }}
              type={storyCollapse ? 'expand_less' : 'expand_more'}
              onClick={(e) => {
                StoryMapStore.collapseStory(version.versionId);
              }}
            />
            {version.name}
            {version.versionId === 'none' && !isFullScreen && (
              <Tooltip title="创建版本">
                <Icon
                  className="c7nagile-StoryMap-TitleCell-createBtn"
                  type="playlist_add"                 
                  onClick={this.handleCreateVersionClick}
                  shape="circle"
                />
              </Tooltip>
            )}
          </div>
        );
      }
      default: return null;
    }
  }


  render() {
    const {
      otherData, showTitle, storyCollapse, epicIndex, isLastRow, lastCollapse,
    } = this.props;
    const { collapse } = otherData || {};

    return (
      <Cell style={{
        borderRight: 'none',
        borderBottom: storyCollapse ? '1px solid #D8D8D8' : 'none',
        padding: '10px 0',
        boxShadow: 'none',
        borderLeft: 'solid 1px #D8D8D8',
        ...lastCollapse ? { borderLeft: 'none' } : { },
        // ...collapse ? { borderBottom: isLastRow && storyCollapse ? '1px solid #D8D8D8' : 'none', borderTop: 'none' } : {},
        ...showTitle ? {
          position: 'sticky',
          zIndex: 5,
          left: 0,         
          // background: 'white',
        } : {},        
       
      }}
      >
        {collapse ? null : (
          <div style={{ display: 'flex' }} className="c7nagile-StoryMap-TitleCell">
            <div style={{ textAlign: 'left' }}>
              {showTitle && this.renderTitle(storyCollapse)}
            </div>
          </div>
        )}
      </Cell>
    );
  }
}

TitleCell.propTypes = {

};

export default TitleCell;
