/* eslint-disable no-restricted-globals */
import React, { Component } from 'react';
import { toJS } from 'mobx';
import { observer, inject } from 'mobx-react';
import {
  Page, Header, stores,
} from '@choerodon/boot';
import { Button, Spin, Select } from 'choerodon-ui';
import { find } from 'lodash';
import {
  StatusColumn, NoneSprint, IssueDetail,
} from './components';
import { ProgramBoardSettingLink } from '../../../../common/utils';
import SwimLane from './components/RenderSwimLaneContext/SwimLane';
import BoardDataController from './BoardDataController';
import QuickSearch from '../../../../components/QuickSearch';
import CSSBlackMagic from '../../../../components/CSSBlackMagic/CSSBlackMagic';
import KanbanStore from '../../../../stores/project/Kanban/KanbanStore';
import './BoardHome.scss';

const { Option, OptGroup } = Select;
const { AppState } = stores;
const style = swimLaneId => `
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn {
    background-color: rgba(140, 158, 255, 0.12) !important;
  }
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus >  .c7n-swimlaneContext-itemBodyStatus-container {
    border-width: 2px;
    border-style: dashed;
    border-color: #26348b;
  }
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus > .c7n-swimlaneContext-itemBodyStatus-container > .c7n-swimlaneContext-itemBodyStatus-container-statusName {
      visibility: visible !important;
  } 
`;
const canDropWhenNoPi = swimLaneId => `
  .${swimLaneId}.prepare.c7n-swimlaneContext-itemBodyColumn {
    background-color: rgba(140, 158, 255, 0.12) !important;
  }
  .${swimLaneId}.prepare.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus >  .c7n-swimlaneContext-itemBodyStatus-container {
    border-width: 2px;
    border-style: dashed;
    border-color: #26348b;
  }
  .${swimLaneId}.prepare.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus > .c7n-swimlaneContext-itemBodyStatus-container > .c7n-swimlaneContext-itemBodyStatus-container-statusName {
      visibility: visible !important;
  } 
  .${swimLaneId}.todo.c7n-swimlaneContext-itemBodyColumn {
    background-color: rgba(140, 158, 255, 0.12) !important;
  }
  .${swimLaneId}.todo.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus >  .c7n-swimlaneContext-itemBodyStatus-container {
    border-width: 2px;
    border-style: dashed;
    border-color: #26348b;
  }
  .${swimLaneId}.todo.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus > .c7n-swimlaneContext-itemBodyStatus-container > .c7n-swimlaneContext-itemBodyStatus-container-statusName {
      visibility: visible !important;
  }
`;
@CSSBlackMagic
@inject('AppState', 'HeaderStore')
@observer
class BoardHome extends Component {
  constructor() {
    super();
    this.dataConverter = new BoardDataController();
    this.quickFilters = [{
      id: 'business',
      name: '特性',
    }, {
      id: 'enabler',
      name: '使能',
    }, {
      id: '1',
      name: '已完成',
    }, {
      id: '0',
      name: '未完成',
    }];
  }

  componentDidMount() {
    this.getBoard();
  }

  componentWillUnmount() {
    this.dataConverter = null;
    KanbanStore.resetDataBeforeUnmount();
  }

  async getBoard() {
    const { programId } = this.props;
    const boardListData = await KanbanStore.axiosGetBoardList(programId);
    const defaultBoard = boardListData.find(item => item.userDefault) || boardListData[0];
    if (defaultBoard && defaultBoard.boardId) {
      this.refresh(defaultBoard, null, boardListData);
    }
  }

  renderPlaceHolder = (type, props, ommittedValues) => {
    const values = [];
    for (const value of ommittedValues) {
      const target = find(this[type], { [props[0]]: value })[props[1]];
      if (target) {
        values.push(target);
      }
    }
    return values.join(', ');
  };

  handleCreateFeatureClick = () => {
    KanbanStore.setCreateFeatureVisible(true);
  };

  handleQuickSearchChange = (filters) => {
    const featureFilters = filters.filter(filter => ['business', 'enabler'].includes(filter));
    const completeFilters = filters.filter(filter => ['0', '1'].includes(filter));
    KanbanStore.addQuickSearchFilter({ featureFilters, completeFilters });
    this.refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
  };

  handleSettingClick = () => {
    const { history } = this.props;
    history.push(ProgramBoardSettingLink());
  };

  handleCreate = () => {
    KanbanStore.setCreateFeatureVisible(false);
    this.refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
  }

  refresh(defaultBoard, url, boardListData) {
    KanbanStore.setSpinIf(true);
    const { programId } = this.props;
    Promise.all([
      // KanbanStore.axiosGetIssueTypes(), 
      // KanbanStore.axiosGetStateMachine(), 
      KanbanStore.axiosGetBoardData(defaultBoard.boardId, programId), 
      // KanbanStore.axiosGetAllEpicData()
    ])
      .then(([defaultBoardData]) => {
        this.dataConverter.setSourceData([], defaultBoardData);
        const renderDataMap = new Map([
          ['parent_child', this.dataConverter.getParentWithSubData],
          ['swimlane_epic', this.dataConverter.getEpicData],
          ['assignee', this.dataConverter.getAssigneeData],
          ['feature', this.dataConverter.getFeatureData],
          ['swimlane_none', this.dataConverter.getAllData],
          ['undefined', this.dataConverter.getAllData],
        ]);
        const renderData = renderDataMap.get(defaultBoard.userDefaultBoard)();
        const canDragOn = this.dataConverter.getCanDragOn();
        const statusColumnMap = this.dataConverter.getStatusColumnMap();
        const statusMap = this.dataConverter.getStatusMap();
        const mapStructure = this.dataConverter.getMapStructure();
        const allDataMap = this.dataConverter.getAllDataMap(defaultBoard.userDefaultBoard);
        const headerData = this.dataConverter.getHeaderData();
        KanbanStore.scrumBoardInit(AppState, url, boardListData, defaultBoard, defaultBoardData, null, null, null, canDragOn, statusColumnMap, allDataMap, mapStructure, statusMap, renderData, headerData);
      });
  }


  render() {
    const { HeaderStore, programId } = this.props;
    const { quickSearchObj } = KanbanStore;
    return (
      <Page
        className="c7nagile-board-page"
        service={[
          'agile-service.board.queryByOptionsInProgram',
        ]}
      >
        <Header title="项目群看板">
          {/* <Button
            funcType="flat"
            icon="playlist_add"
            onClick={this.handleCreateFeatureClick}
          >
            创建特性
          </Button> */}
          <Button
            className="leftBtn2"
            funcType="flat"
            icon="refresh"
            onClick={() => {
              this.refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
            }}
          >
            刷新
          </Button>
        </Header>
        <div style={{ padding: 0, display: 'flex', flexDirection: 'column' }}>
          <div className="c7n-scrumTools">
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <span style={{ marginRight: 16, fontSize: '14px', fontWeight: 600 }}>搜索:</span>
              <Select
                className="SelectTheme primary"
                placeholder="快速搜索"
                style={{ width: 90 }}
                mode="multiple"
                maxTagCount={0}
                maxTagPlaceholder={this.renderPlaceHolder.bind(this, 'quickFilters', ['id', 'name'])}
                value={toJS(quickSearchObj.advancedSearchArgs.featureTypeList).concat(toJS(quickSearchObj.advancedSearchArgs.completeList))}
                onChange={this.handleQuickSearchChange}
              >
                {/* <OptGroup label="常用选项"> */}
                {this.quickFilters.map(filter => (
                  <Option value={filter.id}>
                    {filter.name}
                  </Option>
                ))}
                {/* </OptGroup> */}

              </Select>
            </div>
            {/* <div
              className="c7n-scrumTools-right"
              style={{ display: 'flex', alignItems: 'center', color: 'rgba(0,0,0,0.54)' }}
            >
              <Button
                funcType="flat"
                icon="settings"
                onClick={this.handleSettingClick}
              >
                配置
              </Button>
            </div> */}
          </div>
          <Spin spinning={KanbanStore.getSpinIf}>
            <div style={{ display: 'flex', width: '100%' }}>
              <div className="c7n-board" style={HeaderStore.announcementClosed ? {} : { height: 'calc(100vh - 208px)' }}>
                <div className="c7n-board-header">
                  <StatusColumn />
                </div>
                {!KanbanStore.didCurrentSprintExist || KanbanStore.allDataMap.size === 0 ? (
                  <NoneSprint />
                ) : (
                  <div
                    className="c7n-board-content"
                    style={HeaderStore.announcementClosed ? {} : { height: 'calc(100vh - 256px)' }}
                  >
                    <div className="c7n-board-container">
                      <SwimLane
                        mode={KanbanStore.getSwimLaneCode}
                        allDataMap={this.dataConverter.getAllDataMap()}
                        mapStructure={KanbanStore.getMapStructure}
                        onDragEnd={this.onDragEnd}
                        onDragStart={this.onDragStart}
                      />
                    </div>
                  </div>
                )}
              </div>
              <IssueDetail
                refresh={this.refresh.bind(this)}
                programId={programId}
              />
            </div>

          </Spin>
        </div>
        {/* <CreateFeatureContainer onOk={this.handleCreate} /> */}
      </Page>
    );
  }
}

BoardHome.propTypes = {

};

export default BoardHome;
