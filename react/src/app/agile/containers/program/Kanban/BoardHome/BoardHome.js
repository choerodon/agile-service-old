/* eslint-disable no-restricted-globals */
import React, { Component } from 'react';
import { toJS } from 'mobx';
import { observer, inject } from 'mobx-react';
import {
  Page, Header, stores,
} from 'choerodon-front-boot';
import { Button, Spin } from 'choerodon-ui';
import { find } from 'lodash';
import {
  StatusColumn, NoneSprint, CreateFeatureContainer, IssueDetail,
} from './components';
import { ProgramBoardSettingLink } from '../../../../common/utils';
import SwimLane from './components/RenderSwimLaneContext/SwimLane';
import BoardDataController from './BoardDataController';
import QuickSearch from '../../../../components/QuickSearch';
import CSSBlackMagic from '../../../../components/CSSBlackMagic/CSSBlackMagic';
import KanbanStore from '../../../../stores/program/Kanban/KanbanStore';
import './BoardHome.scss';

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
  }

  componentDidMount() {
    this.getBoard();
  }

  componentWillUnmount() {
    this.dataConverter = null;
    KanbanStore.resetDataBeforeUnmount();
  }

  async getBoard() {
    const boardListData = await KanbanStore.axiosGetBoardList();
    const defaultBoard = boardListData.find(item => item.userDefault) || boardListData[0];
    if (defaultBoard && defaultBoard.boardId) {
      this.refresh(defaultBoard, null, boardListData);
    }
  }

  handleCreateFeatureClick = () => {
    KanbanStore.setCreateFeatureVisible(true);
  }

  handleSettingClick = () => {
    const { history } = this.props;
    history.push(ProgramBoardSettingLink());
  }

  onDragStart = (result) => {
    const { headerStyle } = this.props;
    const { draggableId } = result;
    const [SwimLaneId, issueId, columnCategoryCode] = draggableId.split(['/']);
    // 没有活跃pi，只能在当前列拖动
    if (!KanbanStore.getActivePi) {
      // headerStyle.changeStyle(canDropWhenNoPi(SwimLaneId));
      headerStyle.changeStyle(canDropWhenNoPi(SwimLaneId));
    } else {
      headerStyle.changeStyle(style(SwimLaneId));
    }
    KanbanStore.setIsDragging(true);
  };

  onDragEnd = (result) => {
    const { headerStyle } = this.props;
    const { destination, source, draggableId } = result;
    const [SwimLaneId, issueId] = draggableId.split(['/']);
    const allDataMap = KanbanStore.getAllDataMap;
    KanbanStore.resetCanDragOn();
    KanbanStore.setIsDragging(true);
    headerStyle.unMountStyle();
    if (!destination) {
      return;
    }

    if (destination.droppableId === source.droppableId && destination.index === source.index) {
      return;
    }

    const [startStatus, startColumn, startStatusCode] = source.droppableId.split(['/']).map(id => (isNaN(id) ? id : parseInt(id, 10)));
    const startStatusIndex = source.index;

    const [destinationStatus, destinationColumn, destinationStatusCode] = destination.droppableId.split(['/']).map(id => (isNaN(id) ? id : parseInt(id, 10)));
    const destinationStatusIndex = destination.index;
    // 同一列且同一个状态拖动，不处理
    if (startColumn === destinationColumn && startStatus === destinationStatus) {
      return;
    }
    const issue = {
      ...allDataMap.get(+issueId),
      stayDay: 0,
    };
    // KanbanStore.getSwimLaneData
    const destinationColumnData = find(toJS(KanbanStore.getMapStructure.columnStructure), { columnId: destinationColumn });
    const { categoryCode: destinationColumnStatusCode } = destinationColumnData;
    const destinationSwimLineData = toJS(KanbanStore.getSwimLaneData[SwimLaneId][destinationStatus]);
    const activePi = toJS(KanbanStore.activePi);
    const rank = destinationColumnStatusCode !== 'prepare';
    let piId;

    if (destinationColumnStatusCode === 'prepare' && destinationColumnStatusCode === 'prepare') {
      piId = undefined;
    } else if (destinationSwimLineData.length > 0) {
      // eslint-disable-next-line prefer-destructuring
      piId = destinationSwimLineData[0].piId;
    } else {
      piId = activePi ? activePi.id : undefined;
    }

    const [type, parentId] = SwimLaneId.split('-');
    const piChange = piId && piId !== issue.piId;
    if (piChange && activePi) {
      // eslint-disable-next-line prefer-destructuring
      piId = activePi.id;
    }
    // debugger;
    KanbanStore.updateIssue(issue, startStatus, startStatusIndex, destinationStatus, destinationStatusIndex, SwimLaneId, piId, rank, piChange).then((data) => {
      if (data.failed) {
        Choerodon.prompt(data.message);
        KanbanStore.setSwimLaneData(SwimLaneId, startStatus, startStatusIndex, SwimLaneId, destinationStatus, destinationStatusIndex, issue, true);
      } else {
        // if (KanbanStore.getSwimLaneCode === 'parent_child' && parentId !== 'other') {
        //   KanbanStore.judgeMoveParentToDone(destinationStatus, SwimLaneId, +parentId, KanbanStore.getStatusMap.get(destinationStatus).categoryCode === 'done');
        // }
        if (data.issueId === KanbanStore.getCurrentClickId) {
          KanbanStore.getEditRef.loadIssueDetail();
        }
        if (startColumn !== destinationColumn) {
          KanbanStore.resetHeaderData(startColumn, destinationColumn, issue.issueTypeDTO.typeCode);
        }
        KanbanStore.rewriteObjNumber(data, issueId, issue);
      }
    });
    KanbanStore.setSwimLaneData(SwimLaneId, startStatus, startStatusIndex, SwimLaneId, destinationStatus, destinationStatusIndex, issue, false);
  };

  handleCreate = () => {
    KanbanStore.setCreateFeatureVisible(false);
    this.refresh(KanbanStore.getBoardList.get(KanbanStore.getSelectedBoard));
  }

  refresh(defaultBoard, url, boardListData) {
    KanbanStore.setSpinIf(true);
    Promise.all([KanbanStore.axiosGetIssueTypes(), KanbanStore.axiosGetStateMachine(), KanbanStore.axiosGetBoardData(defaultBoard.boardId), KanbanStore.axiosGetAllEpicData()]).then(([issueTypes, stateMachineMap, defaultBoardData, epicData]) => {
      this.dataConverter.setSourceData(epicData, defaultBoardData);
      const renderDataMap = new Map([
        ['parent_child', this.dataConverter.getParentWithSubData],
        ['swimlane_epic', this.dataConverter.getEpicData],
        ['assignee', this.dataConverter.getAssigneeData],
        ['feature', this.dataConverter.getFeatureData],
        ['swimlane_none', this.dataConverter.getAllData],
      ]);
      const renderData = renderDataMap.get(defaultBoard.userDefaultBoard)();
      const canDragOn = this.dataConverter.getCanDragOn();
      const statusColumnMap = this.dataConverter.getStatusColumnMap();
      const statusMap = this.dataConverter.getStatusMap();
      const mapStructure = this.dataConverter.getMapStructure();
      const allDataMap = this.dataConverter.getAllDataMap(defaultBoard.userDefaultBoard);
      const headerData = this.dataConverter.getHeaderData();
      KanbanStore.scrumBoardInit(AppState, url, boardListData, defaultBoard, defaultBoardData, null, issueTypes, stateMachineMap, canDragOn, statusColumnMap, allDataMap, mapStructure, statusMap, renderData, headerData);
    });
  }


  render() {
    const { HeaderStore } = this.props;
    return (
      <Page
        className="c7nagile-board-page"
      >
        <Header title="项目群看板">
          <Button
            funcType="flat"
            icon="playlist_add"
            onClick={this.handleCreateFeatureClick}
          >
            创建特性
          </Button>
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
            <div />
            <div
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
            </div>
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
              />
            </div>

          </Spin>
        </div>
        <CreateFeatureContainer onCreate={this.handleCreate} />
      </Page>
    );
  }
}

BoardHome.propTypes = {

};

export default BoardHome;
