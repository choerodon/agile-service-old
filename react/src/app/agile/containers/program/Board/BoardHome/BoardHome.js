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
    BoardStore.loadData();
  }

  handleProjectChange = (value) => {
    BoardStore.setFilter({
      teamProjectIds: value,
    });
    BoardStore.loadData();
  }

  CheckboxChange = (type, e) => {
    const { checked } = e.target;
    switch (type) {
      case 'onlyDependFeature': {
        BoardStore.setFilter({
          onlyDependFeature: checked,
        });
        BoardStore.loadData();
        break;
      }
      case 'onlyOtherTeamDependFeature': {
        BoardStore.setFilter({
          onlyOtherTeamDependFeature: checked,
        });
        BoardStore.loadData();
        break;
      }
      default:
        break;
    }
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

  render() {
    const {
      projects, sprints, featureListVisible, activePi, loading, filter, boardData,
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
          <Checkbox
            checked={onlyDependFeature}
            onChange={this.CheckboxChange.bind(this, 'onlyDependFeature')}
          >
            仅显示依赖关系
          </Checkbox>
          {teamProjectIds && teamProjectIds.length > 0 && (
            <Checkbox
              checked={onlyOtherTeamDependFeature}
              onChange={this.CheckboxChange.bind(this, 'onlyOtherTeamDependFeature')}
            >
              显示与当前团队的依赖关系
            </Checkbox>
          )}
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
