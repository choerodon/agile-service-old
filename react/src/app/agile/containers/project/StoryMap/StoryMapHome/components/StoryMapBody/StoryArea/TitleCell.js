import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import Cell from '../Cell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

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
          <div className="c7nagile-StoryMap-StoryCell-title">
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
                <Button
                  className="c7nagile-StoryMap-StoryCell-title-createBtn"
                  type="primary"
                  icon="playlist_add"
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
      otherData, showTitle, storyCollapse, isLastRow,
    } = this.props;
    const { collapse } = otherData || {};

    return (
      <Cell style={{
        ...collapse ? { borderBottom: isLastRow ? '1px solid #D8D8D8' : 'none', borderTop: 'none' } : {},
        ...showTitle ? {
          position: 'sticky',
          zIndex: 5,
          left: 0,
          borderRight: 'none',
        } : {},
      }}
      >
        {collapse ? null : (
          <div style={{
            height: '100%', display: 'flex', flexDirection: 'column',
          }}
          >
            <div style={{ textAlign: 'left', marginLeft: -20, height: 30 }}>
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
