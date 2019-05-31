import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import {
  Page, Header, stores, Content,
} from '@choerodon/boot';
import { find } from 'lodash';
import { Button, Select, Checkbox } from 'choerodon-ui';
import Empty from '../../../../components/Empty';
import Loading from '../../../../components/Loading';
import BoardStore from '../../../../stores/project/Board/BoardStore';
import BoardBody from './components/BoardBody';

import noBoard from '../../../../assets/noBoard.svg';
import { artListLink } from '../../../../common/utils';
import './BoardHome.scss';

const { Option } = Select;
@observer
class BoardHome extends Component {
  componentDidMount() {
    const { programId } = this.props;
    BoardStore.loadData(programId);
  }

  handleRefresh = () => {
    const { programId } = this.props;
    BoardStore.loadData(programId);
  }

  handleClickFeatureList = () => {
    BoardStore.setFeatureListVisible(!BoardStore.featureListVisible);
  }

  handleSprintChange=(value) => {
    BoardStore.setFilter({
      sprintId: value,
    });
    this.handleRefresh();
  }

  handleProjectChange=(value) => {
    BoardStore.setFilter({
      teamProjectId: value,
    });
    this.handleRefresh();
  }

  CheckboxChange=(type, e) => {
    const { checked } = e.target;    
    switch (type) {
      case 'onlyDependFeature': {
        BoardStore.setFilter({
          onlyDependFeature: checked,
        });
        this.handleRefresh();
        break; 
      }
      case 'onlyOtherTeamDependFeature': {
        BoardStore.setFilter({
          onlyOtherTeamDependFeature: checked,
        });
        this.handleRefresh();
        break; 
      }
      default:
        break;
    }
  }

  renderPlaceHolder=(type, props, ommittedValues) => {
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
      sprintId,
      teamProjectId,
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
            placeholder="根据冲刺筛选"
            allowClear
            style={{ width: 180 }}
            onChange={this.handleSprintChange}
            value={sprintId}
            maxTagCount={0}
            maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'filterSprintList', ['sprintId', 'sprintName'])}
          >
            {sprintOptions}
          </Select>
          <Select
            className="SelectTheme"
            placeholder="根据团队筛选" 
            allowClear
            style={{ width: 120 }}
            onChange={this.handleProjectChange}
            value={teamProjectId}
            maxTagCount={0}
            maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'filterTeamList', ['teamProjectId', 'name'])}
          >
            {projectOptions}
          </Select>
          <Checkbox
            checked={onlyDependFeature}            
            onChange={this.CheckboxChange.bind(this, 'onlyDependFeature')}
          >
           只看有依赖关系的卡片
          </Checkbox>
          {teamProjectId && (
          <Checkbox
            checked={onlyOtherTeamDependFeature}            
            onChange={this.CheckboxChange.bind(this, 'onlyOtherTeamDependFeature')}
          >
           只看和当前团队有依赖关系的卡片
          </Checkbox>
          )}
          <Button
            icon="refresh"
            onClick={this.handleRefresh}
          >
            刷新
          </Button>
          <div style={{ flex: 1, visibility: 'hidden' }} />          
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
                  <Link to={artListLink()} disabled>ART设置</Link>
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

BoardHome.propTypes = {

};

export default BoardHome;
