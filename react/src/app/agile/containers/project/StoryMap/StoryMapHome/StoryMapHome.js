import React, { Component, Fragment } from 'react';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import PropTypes from 'prop-types';
import {
  Button, Select, Checkbox, Menu, Dropdown,
} from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import { observer } from 'mobx-react';
import Minimap, { Child } from '../../../../components/MiniMap';
import Empty from '../../../../components/Empty';
import epicPic from '../../../../assets/image/emptyStory.svg';
import Loading from '../../../../components/Loading';
import StoryMapBody from './components/StoryMapBody';
import SideIssueList from './components/SideIssueList';
import SwitchSwimLine from './components/SwitchSwimLine';
import CreateVersion from './components/CreateVersion';
import CreateEpicModal from './components/CreateEpicModal';
import CreateFeatureModal from './components/CreateFeatureModal';
import StoryMapStore from '../../../../stores/project/StoryMap/StoryMapStore';
import IsInProgramStore from '../../../../stores/common/program/IsInProgramStore';
import './StoryMapHome.scss';

function toFullScreen(dom) {
  if (dom.requestFullscreen) {
    dom.requestFullscreen();
  } else if (dom.webkitRequestFullscreen) {
    dom.webkitRequestFullscreen();
  } else if (dom.mozRequestFullScreen) {
    dom.mozRequestFullScreen();
  } else {
    dom.msRequestFullscreen();
  }
}

function exitFullScreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.msExitFullscreen) {
    document.msExitFullscreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if (document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  }
}
const HEX = {
  'c7nagile-StoryMap-EpicCard': '#D9C2FB',
  'c7nagile-StoryMap-StoryCard': '#AEE9E0',
  business: '#B2E6F4',
  enabler: '#FEA',
};
@observer
class StoryMapHome extends Component {
  componentDidMount() {
    this.handleRefresh();
    document.addEventListener('fullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('webkitfullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('mozfullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('MSFullscreenChange', this.handleChangeFullScreen);
  }

  componentWillUnmount() {
    document.removeEventListener('fullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('webkitfullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('mozfullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('MSFullscreenChange', this.handleChangeFullScreen);
  }

  handleRefresh = () => {
    StoryMapStore.getStoryMap();
  }

  handleClickIssueList = () => {
    StoryMapStore.toggleSideIssueListVisible();
  }

  handleCreateEpicClick = () => {
    StoryMapStore.setCreateEpicModalVisible(true);
  }

  handleCreateFeatureClick=() => {
    StoryMapStore.setCreateFeatureModalVisible(true);
  }

  handleCreateVersion = (version) => {
    StoryMapStore.afterCreateVersion(version);
  }

  handleCreateEpic = (newEpic) => {
    StoryMapStore.setCreateEpicModalVisible(false);
    StoryMapStore.afterCreateEpicInModal(newEpic);
  }

  handleCreateFeature = () => {
    StoryMapStore.setCreateFeatureModalVisible(false);
    StoryMapStore.getStoryMap();
  }

  handleChangeFullScreen = (e) => {
    const isFullScreen = document.webkitFullscreenElement
      || document.mozFullScreenElement
      || document.msFullscreenElement;
    StoryMapStore.setIsFullScreen(!!isFullScreen);
  }

  handleFullScreen = () => {
    const isFullScreen = document.webkitFullscreenElement
      || document.mozFullScreenElement
      || document.msFullscreenElement;
    if (!isFullScreen) {
      this.fullScreen();
    } else {
      this.exitFullScreen();
    }
  }

  fullScreen = () => {
    const target = document.querySelector('.content');
    toFullScreen(target);
  };

  exitFullScreen = () => {
    exitFullScreen();
  }

  renderChild({
    width, height, left, top, node,
  }) {
    let classNameFound = null;

    node.classList.forEach((className) => {
      if (HEX[className]) {
        classNameFound = className;
      }
    });

    return (
      <div
        style={{
          position: 'absolute',
          width,
          height,
          left,
          top,
          backgroundColor: HEX[classNameFound],
        }}
      />
    );
  }

  render() {
    const { loading, isFullScreen } = StoryMapStore;
    const isEmpty = StoryMapStore.getIsEmpty;
    return (
      <Page
        className="c7nagile-StoryMap"
        service={[
          'agile-service.pi.queryRoadMapOfProgram',
        ]}
      >
        <Header title="故事地图">
          {/* <Button
            icon="playlist_add"
            onClick={this.handleCreateEpicClick}
          >
            创建史诗
          </Button> */}
          <Button
            icon="refresh"
            onClick={this.handleRefresh}
          >
            刷新
          </Button>
          <SwitchSwimLine />
          <Button onClick={this.handleFullScreen.bind(this)} icon={isFullScreen ? 'exit_full_screen' : 'zoom_out_map'}>
            {isFullScreen ? '退出全屏' : '全屏'}
          </Button>
          <Button
            type="primary"
            funcType="raised"
            style={{ color: 'white', marginLeft: 'auto', marginRight: 30 }}
            icon="view_module"
            onClick={this.handleClickIssueList}
          >
            需求池
          </Button>
        </Header>
        <Content style={{ padding: 0, paddingBottom: 49 }}>
          <Loading loading={loading} />
          {!isEmpty ? (
            <Fragment>
              <Minimap disabledVertical width={300} height={40} showHeight={300} className="c7nagile-StoryMap-minimap" selector=".minimapCard" childComponent={this.renderChild.bind(this)}>
                <StoryMapBody />
              </Minimap>
            </Fragment>
          ) : (
            <Empty
              style={{ background: 'white', height: 'calc(100% + 120px)', marginTop: -120 }}
              pic={epicPic}
              title="欢迎使用敏捷用户故事地图"
              description={(
                <Fragment>
                    用户故事地图是以史诗或特性为基础，根据版本控制进行管理规划，点击
                  <a role="none" onClick={this.handleCreateEpicClick}>创建史诗</a>
                    或
                  <a role="none" onClick={this.handleCreateFeatureClick} disabled={IsInProgramStore.isInProgram}>创建特性</a>
                    进入用户故事地图。
                </Fragment>
                )}
            />
          )}
          <SideIssueList />
          <CreateVersion onOk={this.handleCreateVersion} />
          <CreateEpicModal onOk={this.handleCreateEpic} />
          <CreateFeatureModal onOk={this.handleCreateFeature} />
        </Content>
      </Page>
    );
  }
}

StoryMapHome.propTypes = {

};
export default ({ ...props }) => (
  <DragDropContextProvider backend={HTML5Backend}>
    <StoryMapHome {...props} />
  </DragDropContextProvider>
);
