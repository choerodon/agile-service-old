import React, { Component, Fragment } from 'react';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import PropTypes from 'prop-types';
import { Button, Select, Checkbox } from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import { observer } from 'mobx-react';
import Empty from '../../../../components/Empty';
import noBoard from '../../../../assets/noBoard.svg';
import Loading from '../../../../components/Loading';
import StoryMapBody from './components/StoryMapBody';
import StoryMapStore from '../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryMapHome extends Component {
  componentDidMount() {
    this.handleRefresh();
  }

  handleRefresh=() => {
    StoryMapStore.getStoryMap();
  }

  render() {
    const { loading, storyMapData } = StoryMapStore;
    return (
      <Page
        className="c7ntest-Issue c7ntest-region"
        service={[
          'agile-service.pi.queryRoadMapOfProgram',
        ]}
      >
        <Header title="故事地图">
          <Button
            icon="refresh"
            onClick={this.handleRefresh}
          >
            刷新
          </Button>
        </Header>
        <Content style={{ paddingTop: 0 }}>
          <Loading loading={loading} />
          {storyMapData ? (
            <Fragment>
              <StoryMapBody />
            </Fragment>
          ) : (
            <Empty
              style={{ background: 'white', height: 'calc(100% + 120px)', marginTop: -120 }}
              pic={noBoard}
              title="没有活跃的PI"
              description={(
                <Fragment>
                    这是您的项目公告板。如果您想看到特性的依赖关系，可以先到
                  {/* <Link to={artListLink()}>ART设置</Link> */}
                    创建开启火车，再创建特性并关联到活跃的PI。
                </Fragment>
                )}
            />
          )}
        </Content>
      </Page>
    );
  }
}

StoryMapHome.propTypes = {

};

export default StoryMapHome;
