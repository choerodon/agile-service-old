import React, { Component, Fragment, useEffect } from 'react';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import PropTypes from 'prop-types';
import { Button } from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import { observer } from 'mobx-react-lite';
import Minimap, { Child } from '../../../components/MiniMap';
import Empty from '../../../components/Empty';
import epicPic from '../../../assets/image/emptyStory.svg';
import Loading from '../../../components/Loading';
import StoryMapBody from './components/StoryMapBody';
import SideIssueList from './components/SideIssueList';
import SwitchSwimLine from './components/SwitchSwimLine';
import CreateVersion from './components/CreateVersion';
import CreateEpicModal from './components/CreateEpicModal';
import IssueDetail from './components/IssueDetail';
import Search from './components/Search';
import StoryMapStore from '../../../stores/project/StoryMap/StoryMapStore';
import IsInProgramStore from '../../../stores/common/program/IsInProgramStore';
import useFullScreen from './useFullScreen';
import './StoryMapHome.scss';

const HEX = {
  'c7nagile-StoryMap-EpicCard': '#D9C2FB',
  'c7nagile-StoryMap-StoryCard': '#AEE9E0',
  business: '#B2E6F4',
  enabler: '#FEA',
};

const StoryMapHome = observer(() => {
  const handleRefresh = () => {
    StoryMapStore.getStoryMap();
  };
  useEffect(() => {
    handleRefresh();
    return () => { StoryMapStore.clear(); };
  }, []);
  const handleClickIssueList = () => {
    StoryMapStore.toggleSideIssueListVisible();    
  };

  const handleCreateEpicClick = () => {
    StoryMapStore.setCreateEpicModalVisible(true);
  };
  const handleCreateVersion = (version) => {
    StoryMapStore.afterCreateVersion(version);
    document.getElementsByClassName('minimap-container-scroll')[0].scrollTop = 0;
  };

  const handleCreateEpic = (newEpic) => {
    StoryMapStore.setCreateEpicModalVisible(false);
    StoryMapStore.afterCreateEpicInModal(newEpic);
  };


  const onFullScreenChange = (isFullScreen) => {    
    StoryMapStore.setIsFullScreen(!!isFullScreen);
  };


  const renderChild = ({
    width, height, left, top, node,
  }) => {
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
  };

  const handleIssueRefresh = () => {
    handleRefresh();
  };


  const { loading } = StoryMapStore;
  const isEmpty = StoryMapStore.getIsEmpty;
  const [isFullScreen, toggleFullScreen] = useFullScreen(document.documentElement, onFullScreenChange);
  return (
    <Page
      className="c7nagile-StoryMap"
      service={[
        'agile-service.pi.queryRoadMapOfProgram',
      ]}
    >
      <Header title="故事地图">
        <Button
          icon="refresh"
          onClick={handleRefresh}
        >
            刷新
        </Button>
        <SwitchSwimLine />
        <Button onClick={toggleFullScreen} icon={isFullScreen ? 'fullscreen_exit' : 'zoom_out_map'}>
          {isFullScreen ? '退出全屏' : '全屏'}
        </Button>
        {!StoryMapStore.isFullScreen && (
          <Button
            type="primary"
            funcType="raised"
            style={{ color: 'white', marginLeft: 'auto', marginRight: 30 }}
            icon="view_module"
            onClick={handleClickIssueList}
          >
            需求池
          </Button>
        )}
      </Header>
      <Content style={{
        padding: 0, paddingBottom: 49, 
      }}
      >
        <Loading loading={loading} />
        {!isEmpty ? (
          <Fragment>
            <Search />
            <Minimap disabledVertical width={300} height={40} showHeight={300} className="c7nagile-StoryMap-minimap" selector=".minimapCard" childComponent={renderChild}>
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
                    用户故事地图是以史诗为基础，根据版本控制进行管理规划，点击
                <a role="none" onClick={handleCreateEpicClick} disabled={IsInProgramStore.isInProgram}>创建史诗</a>                    
              </Fragment>
                )}
          />
        )}
        <SideIssueList />
        <CreateVersion onOk={handleCreateVersion} />
        <CreateEpicModal onOk={handleCreateEpic} />
        <IssueDetail refresh={handleIssueRefresh} />
      </Content>
    </Page>
  );
});

StoryMapHome.propTypes = {

};
export default ({ ...props }) => (
  <DragDropContextProvider backend={HTML5Backend}>
    <StoryMapHome {...props} />
  </DragDropContextProvider>
);
