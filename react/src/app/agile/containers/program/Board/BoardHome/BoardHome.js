import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { toJS } from 'mobx';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import {
  Page, Header, stores, Content,
} from '@choerodon/boot';
import { find } from 'lodash';
import { Button, Select, Checkbox } from 'choerodon-ui';
import { DragDropContextProvider } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import Empty from '../../../../components/Empty';
import Loading from '../../../../components/Loading';
import BoardStore from '../../../../stores/program/Board/BoardStore';
import BoardBody from './components/BoardBody';
import SideFeatureList from './components/SideFeatureList';
import noBoard from '../../../../assets/noBoard.svg';
import { artListLink } from '../../../../common/utils';
import './BoardHome.scss';

const { Option } = Select;
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

  handleSprintChange = (value) => {
    BoardStore.setFilter({
      sprintIds: value,
    });
    BoardStore.setSelectedFilter([]);
    BoardStore.loadData();
  }

  handleProjectChange = (value) => {
    BoardStore.setFilter({
      teamProjectIds: value,
    });
    if (value.length === 0) {
      BoardStore.setSelectedFilter([]);
    }

    BoardStore.loadData();
  }

  handleSearchChange = (value) => {
    BoardStore.setSelectedFilter(value);
    BoardStore.loadData();
  }

  renderPlaceHolder = (type, props, ommittedValues) => {
    const values = [];
    for (const value of ommittedValues) {
      const target = find(BoardStore.boardData[type], { [props[0]]: value })[props[1]];
      if (target) {
        values.push(target);
      }
    }
    return values.join(', ');
  }

  renderSearchPlaceHolder = (type, props, ommittedValues) => {
    const values = [];
    for (const value of ommittedValues) {
      const target = find(BoardStore[type], { [props[0]]: value })[props[1]];
      if (target) {
        values.push(target);
      }
    }
    return values.join(', ');
  }

  isHasFilter = () => {
    const {
      teamProjectIds, sprintIds, 
    } = BoardStore.filter;
    return teamProjectIds.length > 0 || sprintIds.length > 0 || BoardStore.selectedFilter.length > 0;
  }

  render() {
    const {
      projects, sprints, featureListVisible, activePi, loading, filter, boardData, selectedFilter,
    } = BoardStore;
    const {
      onlyDependFeature,
      sprintIds,
      teamProjectIds,
      onlyOtherTeamDependFeature,
    } = filter;
    const {
      filterSprintList = [],
      filterTeamList = [],
    } = boardData || {};
    const sprintOptions = filterSprintList.map(sprint => (
      <Option value={sprint.sprintId}>
        {sprint.sprintName}
      </Option>
    ));
    const projectOptions = filterTeamList.map(project => (
      <Option value={project.teamProjectId}>
        {project.name}
      </Option>
    ));
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
          {activePi.piId && (
            <Button
              type="primary"
              funcType="raised"
              style={{ color: 'white', marginLeft: 'auto', marginRight: 30 }}
              icon="view_module"
              onClick={this.handleClickFeatureList}
            >
              特性列表
            </Button>
          )}

        </Header>
        <Content style={{ padding: 0 }}>
          <div style={{
            display: 'flex',
            background: 'white',
            paddingLeft: 24,
            height: 48,
            alignItems: 'center',
            borderBottom: '1px solid rgba(0,0,0,.12)',
          }}
          >
            <Select
              className="SelectTheme primary"
              placeholder="快速搜索"
              mode="multiple"
              showCheckAll={false}
              style={{ maxWidth: 100 }}
              onChange={this.handleSearchChange}
              value={toJS(selectedFilter)}
              dropdownMatchSelectWidth={false}
              maxTagCount={0}
              maxTagPlaceholder={this.renderSearchPlaceHolder.bind(this, 'allFilters', ['id', 'name'])}
            >
              <Option value="onlyDependFeature" disabled={sprintIds.length > 0 || teamProjectIds.length > 0}>仅显示依赖关系</Option>
              <Option value="onlyOtherTeamDependFeature" disabled={teamProjectIds.length === 0}>显示团队相关卡片</Option>
            </Select>
            <Select
              className="SelectTheme"
              placeholder="冲刺"
              mode="multiple"
              style={{ maxWidth: 100 }}
              onChange={this.handleSprintChange}
              value={toJS(sprintIds)}
              dropdownMatchSelectWidth={false}
              maxTagCount={0}
              maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'filterSprintList', ['sprintId', 'sprintName'])}
            >
              {sprintOptions}
            </Select>
            <Select
              className="SelectTheme"
              placeholder="团队"
              mode="multiple"
              style={{ maxWidth: 100 }}
              onChange={this.handleProjectChange}
              value={toJS(teamProjectIds)}
              dropdownMatchSelectWidth={false}
              maxTagCount={0}
              maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'filterTeamList', ['teamProjectId', 'name'])}
            >
              {projectOptions}
            </Select>
            {this.isHasFilter() && (
              <Button onClick={() => {
                BoardStore.clearFilter();
                BoardStore.loadData();
              }}
              >
                清空所有筛选
              </Button>
            )}
          </div>
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
