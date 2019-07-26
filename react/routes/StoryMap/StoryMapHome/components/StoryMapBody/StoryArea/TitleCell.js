import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import './TitleCell.scss';

@observer
class TitleCell extends Component {
  handleCreateVersionClick = () => {
    StoryMapStore.setCreateModalVisible(true);
  }

  renderTitle = (storyCollapse) => {
    const { swimLine, isFullScreen } = StoryMapStore;
    const { version } = this.props;
    const { storyNum } = version;
    switch (swimLine) {
      case 'none': {
        return null;
      }
      case 'version': {
        return (
          <Fragment>
            <Icon
              style={{ marginRight: 15 }}
              type={storyCollapse ? 'expand_less' : 'expand_more'}
              onClick={(e) => {
                StoryMapStore.collapseStory(version.versionId);
              }}
            />
            {version.name}
            {` (${storyNum || 0})`}
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
          </Fragment>
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
      collapse ? null : (
        <Cell style={{        
          // borderBottom: storyCollapse ? '1px solid #D8D8D8' : 'none',
          padding: '10px 0',
          boxShadow: storyCollapse ? 'inset 0 -1px 0 #D8D8D8,inset 1px 0 0 #D8D8D8' : 'inset 1px 0 0 #D8D8D8',
          ...lastCollapse || epicIndex === 0 ? { boxShadow: storyCollapse ? 'inset 0 -1px 0 #D8D8D8' : 'none' } : { },       
          ...showTitle ? {
            position: 'sticky',
            zIndex: 5,
            left: 0,         
            // background: 'white',
          } : {}, 
        }}
        > 
          <div style={{ display: 'flex' }} className="c7nagile-StoryMap-TitleCell">        
            {showTitle && this.renderTitle(storyCollapse)}       
          </div>   
        </Cell>
      )
    );
  }
}

TitleCell.propTypes = {

};

export default TitleCell;
