import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import {
  Page, Header, stores, Content,
} from '@choerodon/boot';
import { Button, Spin } from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import BoardStore from '../../../../stores/program/Board/BoardStore';
import Connectors from './components/Connectors';
import BoardBody from './components/BoardBody';
import SideFeatureList from './components/SideFeatureList';
import './BoardHome.scss';


@observer
class BoardHome extends Component {
  componentDidMount() {
    BoardStore.loadData();
  }

  handleRefresh = () => {
    BoardStore.loadData();
  }

  handleClickFeatureList = () => {
    BoardStore.setFeatureListVisible(!BoardStore.featureListVisible);
  }

  render() {
    const {
      projects, sprints, connections, featureListVisible, activePi,
    } = BoardStore;

    return (
      <Page
        className="c7nagile-BoardHome"
      >
        <Header title="项目群公告板">
          <Button
            icon="refresh"
            onClick={this.handleRefresh}
          >
            刷新
          </Button>
          <Button
            disabled={!activePi.piId}
            icon="refresh"
            onClick={this.handleClickFeatureList}
          >
            特性列表
          </Button>
        </Header>
        <Content style={{ padding: 0 }}>
          <BoardBody projects={projects} sprints={sprints} />
          {featureListVisible && <SideFeatureList />}
          <Connectors connections={connections} />
        </Content>
      </Page>
    );
  }
}

BoardHome.propTypes = {

};

export default ({ ...props }) => (
  <DragDropContextProvider backend={HTML5Backend}>
    <BoardHome {...props} />
  </DragDropContextProvider>
);
