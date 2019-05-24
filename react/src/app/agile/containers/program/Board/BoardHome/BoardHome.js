import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import {
  Page, Header, stores, Content,
} from '@choerodon/boot';
import { Button, Spin } from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import Empty from '../../../../components/Empty';
import Loading from '../../../../components/Loading';
import BoardStore from '../../../../stores/program/Board/BoardStore';
import Connectors from './components/Connectors';
import BoardBody from './components/BoardBody';
import SideFeatureList from './components/SideFeatureList';
import noBoard from '../../../../assets/noBoard.svg';
import { artListLink } from '../../../../common/utils';
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
      projects, sprints, featureListVisible, activePi, loading,
    } = BoardStore;

    return (
      <Page
        className="c7nagile-BoardHome"
        service={[
          'agile-service.board-feature.queryBoardInfo',
        ]}
      >
        <Header title="项目群公告板">
          <Button
            icon="refresh"
            onClick={this.handleRefresh}
          >
            刷新
          </Button>
          <div style={{ flex: 1, visibility: 'hidden' }} />
          {activePi.piId && (
            <Button
              type="primary"
              funcType="raised"
              style={{ color: 'white', marginRight: 30 }}
              icon="view_module"
              onClick={this.handleClickFeatureList}
            >
              特性列表
            </Button>
          )}
        </Header>
        <Content style={{ padding: 0 }}>
          <Loading loading={loading} />
          {activePi.piId ? (
            <Fragment>
              <BoardBody projects={projects} sprints={sprints} /> 
            </Fragment>
          ) : (        
            <Empty
              style={{ background: 'white', height: 'calc(100% + 120px)', marginTop: -120 }}
              pic={noBoard}
              title="没有活跃的PI"
              description={(
                <Fragment>
                      这是您的项目公告板。如果您想看到特性的依赖关系，可以先到
                  <Link to={artListLink()}>ART设置</Link>
                      创建开启火车，再创建特性并关联到活跃的PI。
                </Fragment>
                  )}
            />   
          )}           
          {featureListVisible && <SideFeatureList />}
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
