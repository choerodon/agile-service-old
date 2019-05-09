import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content, stores, Permission } from 'choerodon-front-boot';
import { Button, Icon } from 'choerodon-ui';
import { groupBy } from 'lodash';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import SettingColumn from '../SettingColumn/SettingColumn';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';
import SideBarContent from '../SideBarContent/SideBarContent';

const { AppState } = stores;

@observer
class ColumnPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addStatus: false,
      addColumn: false,
    };
  }


  // draggableId: "551,1"
  // mode: "FLUID"
  // source: {droppableId: "todo,2537,null,null", index: 0}
  // type: "status"
  handleDragStart = (draging) => {
    setTimeout(() => {
      KanbanStore.setCurrentDrag(draging);
    });
  }

  handleAddStatus() {
    this.setState({
      addStatus: true,
    });
    if (JSON.stringify(KanbanStore.getStatusCategory) === '{}') {
      KanbanStore.axiosGetStatusCategory().then((data) => {
        KanbanStore.setStatusCategory(data);
      });
    }
  }

  // judgeCanDrag=(result) => {
  //   const groupedColumns = groupBy(KanbanStore.getBoardData, 'categoryCode');
  //   const { destination: { droppableId }, draggableId } = result;
  //   const statusCode = draggableId.split(',')[2];
  //   const sameStatusColumn = groupedColumns[statusCode];
  //   const isOnlyOne = groupedColumns[statusCode].length === 1;
  //   if (isOnlyOne) {
  //     return '只剩一个状态';
  //   }
  //   return null;
  // }

  handleDragEnd(result) {
    KanbanStore.setCurrentDrag(null);
    if (!result.destination) {
      return;
    }
    const { refresh } = this.props;
    // 移动列
    if (result.destination.droppableId === 'columndrop') {
      const originState2 = JSON.parse(JSON.stringify(KanbanStore.getBoardData));
      const newState2 = JSON.parse(JSON.stringify(KanbanStore.getBoardData));

      const [draggableData2] = newState2.splice(result.source.index, 1);
      newState2.splice(result.destination.index, 0, draggableData2);
      KanbanStore.setBoardData(newState2);
      const data = {
        boardId: KanbanStore.getSelectedBoard,
        columnId: JSON.parse(result.draggableId).columnId,
        projectId: parseInt(AppState.currentMenuType.id, 10),
        sequence: result.destination.index,
        objectVersionNumber: JSON.parse(result.draggableId).objectVersionNumber,
      };
      KanbanStore.axiosUpdateColumnSequence(
        KanbanStore.getSelectedBoard, data,
      ).then(() => {
        refresh();
      }).catch(() => {
        KanbanStore.setBoardData(originState2);
      });
    } else {
      // const message = this.judgeCanDrag(result);
      // if (message) {
      //   Choerodon.prompt(message);
      //   return;
      // }
      // 移动状态
      const originState = JSON.parse(JSON.stringify(KanbanStore.getBoardData));
      const newState = JSON.parse(JSON.stringify(KanbanStore.getBoardData));


      let draggableData = {};
      let columnIndex;
      for (let index = 0, len = newState.length; index < len; index += 1) {
        if (String(newState[index].columnId) === String(result.source.droppableId.split(',')[1])) {
          columnIndex = index;
          [draggableData] = newState[index].subStatuses.splice(result.source.index, 1);
        }
      }
      if (result.destination.droppableId.split(',')[1] === 'unset') {
        const code = result.draggableId.split(',')[0];
        const columnId = result.source.droppableId.split(',')[1];
        const minNum = result.source.droppableId.split(',')[2];
        let totalNum = 0;
        if (KanbanStore.getCurrentConstraint !== 'constraint_none') {
          for (
            let index = 0, len = newState[columnIndex].subStatuses.length;
            index < len;
            index += 1) {
            for (
              let index2 = 0, len2 = newState[columnIndex].subStatuses[index].issues.length;
              index2 < len2;
              index2 += 1) {
              if (KanbanStore.getCurrentConstraint === 'issue') {
                totalNum += 1;
              } else if (newState[columnIndex].subStatuses[index].issues[index2].issueTypeDTO.typeCode !== 'sub_task') {
                totalNum += 1;
              }
            }
          }/* eslint-disable */
          // if (!isNaN(minNum)) {
          //   /* eslint-enable */
          //   if (parseInt(totalNum, 10) < parseInt(minNum, 10)) {
          //     Choerodon.prompt('剩余状态issue数小于列的最小issue数，无法移动状态');
          //     return;
          //   }
          // }
        }
        for (let index = 0, len = newState.length; index < len; index += 1) {
          if (String(newState[index].columnId) === String(result.destination.droppableId.split(',')[1])) {
            newState[index].subStatuses.splice(result.destination.index, 0, draggableData);
          }
        }
        KanbanStore.setBoardData(newState);
        KanbanStore.moveStatusToUnset(code, {
          columnId,
        }).then((data) => {
          const newData = data;
          newData.issues = draggableData.issues;
          for (let index = 0, len = newState.length; index < len; index += 1) {
            if (String(newState[index].columnId) === String(result.destination.droppableId.split(',')[1])) {
              newState[index].subStatuses.splice(result.destination.index, 1, newData);
            }
          }
          KanbanStore.setBoardData(newState);
          refresh();
        }).catch((error) => {
          KanbanStore.setBoardData(originState);
        });
      } else {
        const code = result.draggableId.split(',')[0];
        const categorycode = result.destination.droppableId.split(',')[0];
        const columnId = result.destination.droppableId.split(',')[1];
        const position = result.destination.index;
        const statusObjectVersionNumber = result.draggableId.split(',')[1];
        const originColumnId = result.source.droppableId.split(',')[1] === 'unset' ? 0 : result.source.droppableId.split(',')[1];
        const minNum = result.source.droppableId.split(',')[2];
        const maxNum = result.destination.droppableId.split(',')[3];
        let totalNum = 0;
        if (KanbanStore.getCurrentConstraint !== 'constraint_none') {
          for (
            let index = 0, len = newState[columnIndex].subStatuses.length;
            index < len;
            index += 1) {
            for (
              let index2 = 0, len2 = newState[columnIndex].subStatuses[index].issues.length;
              index2 < len2;
              index2 += 1) {
              if (KanbanStore.getCurrentConstraint === 'issue') {
                totalNum += 1;
              } else if (newState[columnIndex].subStatuses[index].issues[index2].issueTypeDTO.typeCode !== 'sub_task') {
                totalNum += 1;
              }
            }
          }
          /* eslint-disable */
          // if (!isNaN(minNum)) {
          //   /* eslint-enable */
          //   if (parseInt(totalNum, 10) < parseInt(minNum, 10)) {
          //     Choerodon.prompt('剩余状态issue数小于列的最小issue数，无法移动状态');
          //     return;
          //   }
          // }
          let destinationTotal = 0;
          for (let index = 0, len = newState.length; index < len; index += 1) {
            if (parseInt(newState[index].columnId, 10) === parseInt(columnId, 10)) {
              for (
                let index2 = 0, len2 = newState[index].subStatuses.length;
                index2 < len2;
                index2 += 1) {
                for (
                  let index3 = 0, len3 = newState[index].subStatuses[index2].issues.length;
                  index3 < len3;
                  index3 += 1) {
                  if (KanbanStore.getCurrentConstraint === 'issue') {
                    destinationTotal += 1;
                  } else if (newState[index].subStatuses[index2].issues[index3].issueTypeDTO.typeCode !== 'sub_task') {
                    destinationTotal += 1;
                  }
                }
              }
            }
          }
          let draggableTotal = 0;
          for (let index = 0, len = draggableData.issues.length; index < len; index += 1) {
            if (KanbanStore.getCurrentConstraint === 'issue') {
              draggableTotal += 1;
            } else if (draggableData.issues[index].issueTypeDTO.typeCode !== 'sub_task') {
              draggableTotal += 1;
            }
          }
          // if ((destinationTotal + draggableTotal) > parseInt(maxNum, 10)) {
          //   Choerodon.prompt('移动至目标列后的issue数大于目标列的最大issue数，无法移动状态');
          //   return;
          // }
        }
        for (let index = 0, len = newState.length; index < len; index += 1) {
          if (String(newState[index].columnId) === String(result.destination.droppableId.split(',')[1])) {
            newState[index].subStatuses.splice(result.destination.index, 0, draggableData);
          }
        }
        KanbanStore.setBoardData(newState);
        KanbanStore.moveStatusToColumn(code, {
          // categorycode,
          columnId,
          position,
          statusObjectVersionNumber,
          originColumnId,
        }).then((data) => {
          const newData = data;
          newData.issues = draggableData.issues;
          for (let index = 0, len = newState.length; index < len; index += 1) {
            if (String(newState[index].columnId) === String(result.destination.droppableId.split(',')[1])) {
              newState[index].subStatuses.splice(result.destination.index, 1, newData);
            }
          }
          KanbanStore.setBoardData(newState);
          refresh();
        }).catch((error) => {
          KanbanStore.setBoardData(originState);
        });
      }
    }
  }

  handleAddColumn() {
    this.setState({
      addColumn: true,
    });
    if (JSON.stringify(KanbanStore.getStatusCategory) === '{}') {
      KanbanStore.axiosGetStatusCategory().then((data) => {
        KanbanStore.setStatusCategory(data);
      }).catch((error) => {
      });
    }
  }

  renderColumns(data, draging) {
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const result = [];
    const { refresh } = this.props;
    const groupedColumns = groupBy(data, 'categoryCode');
    const { draggableId, type: dragType } = draging || {};
    const statusCategoryCode = draggableId && draggableId.split(',')[2]
    for (let index = 0, len = data.length; index < len; index += 1) {
      const column = data[index];
      const { categoryCode } = column;
      const sameStatusColumn = groupedColumns[categoryCode];
      const isOnlyOne = groupedColumns[categoryCode].length === 1;
      // 状态只能拖动到和状态相同的列
      const statusDropDisabled = dragType === 'status' && statusCategoryCode !== categoryCode;      
      result.push(
        <Permission
          type={type}
          projectId={projectId}
          organizationId={orgId}
          service={['agile-service.board.deleteScrumBoard']}
          noAccessChildren={(
            <SettingColumn
              noPermission
              statusDropDisabled={statusDropDisabled}
              isOnlyOne={isOnlyOne}
              sameStatusColumn={sameStatusColumn}
              data={column}
              refresh={refresh.bind(this)}
              // setLoading={this.props.setLoading.bind}
              index={index}
              styleValue={`${parseFloat(parseFloat(1 / data.length) * 100)}%`}
            />
          )}
        >
          <SettingColumn
            statusDropDisabled={statusDropDisabled}
            isOnlyOne={isOnlyOne}
            sameStatusColumn={sameStatusColumn}
            data={column}
            refresh={refresh.bind(this)}
            // setLoading={this.props.setLoading.bind}
            index={index}
            styleValue={`${parseFloat(parseFloat(1 / data.length) * 100)}%`}
          />
        </Permission>,
      );
    }
    return result;
  }

  renderUnsetColumn() {
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const BoardData = KanbanStore.getBoardData;
    const { refresh } = this.props;
    if (BoardData.length > 0) {
      if (BoardData[BoardData.length - 1].columnId === 'unset') {
        return (
          <Permission
            type={type}
            projectId={projectId}
            organizationId={orgId}
            service={['agile-service.board.deleteScrumBoard']}
            noAccessChildren={(
              <SettingColumn
                noPermission
                data={BoardData[BoardData.length - 1]}
                refresh={refresh.bind(this)}
                index={BoardData.length - 1}
                disabled
              />
            )}
          >
            <SettingColumn
              data={BoardData[BoardData.length - 1]}
              refresh={refresh.bind(this)}
              index={BoardData.length - 1}
              disabled
            />
          </Permission>
        );
      }
    }
    return '';
  }

  render() {
    const BoardData = JSON.parse(JSON.stringify(KanbanStore.getBoardData));
    const draging = KanbanStore.getCurrentDrag;
    const { refresh } = this.props;
    const { addStatus, addColumn } = this.state;
    if (BoardData.length > 0) {
      if (BoardData[BoardData.length - 1].columnId === 'unset') {
        BoardData.splice(BoardData.length - 1, 1);
      }
    }
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    return (

      <Content
        description="分栏可以添加、删除、重新排序和重命名。列是基于全局状态和可移动的列与列之间。注意：项目群中状态只能拖放到对应类别的列中。"
        style={{
          padding: 0,
          overflow: 'unset',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <div>
          {
            KanbanStore.getCanAddStatus ? (
              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.issue-status.createStatus']}>
                <Button
                  funcType="flat"
                  type="primary"
                  onClick={this.handleAddStatus.bind(this)}
                >
                  <Icon type="playlist_add" />
                  <span>添加状态</span>
                </Button>
              </Permission>
            ) : ''
          }
          <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.board-column.createBoardColumn']}>
            <Button
              funcType="flat"
              type="primary"
              onClick={this.handleAddColumn.bind(this)}
            >
              <Icon type="playlist_add" />
              <span>添加列</span>
            </Button>
          </Permission>
        </div>
        <div
          className="c7n-scrumsetting"
          style={{
            marginTop: 32,
            flexGrow: 1,
            height: '100%',
          }}
        >
          <DragDropContext
            onDragEnd={
              this.handleDragEnd.bind(this)
            }
            onDragStart={this.handleDragStart}
          >
            <Droppable droppableId="columndrop" direction="horizontal" type="columndrop">
              {(provided, snapshot) => (
                <div
                  ref={provided.innerRef}
                  style={{
                    display: 'flex',
                    flex: BoardData.length,
                  }}
                  {...provided.droppableProps}
                >
                  {this.renderColumns(BoardData, draging)}
                  {provided.placeholder}
                </div>
              )}
            </Droppable>
            {this.renderUnsetColumn()}
          </DragDropContext>
        </div>
        {
          addStatus ? (
            <SideBarContent
              visible={addStatus}
              type="Status"
              onChangeVisible={(data) => {
                this.setState({
                  addStatus: data,
                });
              }}
              refresh={refresh.bind(this)}
              store={KanbanStore}
            />
          ) : ''
        }
        {
          addColumn ? (
            <SideBarContent
              visible={addColumn}
              type="Column"
              onChangeVisible={(data) => {
                this.setState({
                  addColumn: data,
                });
              }}
              refresh={refresh.bind(this)}
              store={KanbanStore}
            />
          ) : ''
        }
      </Content>
    );
  }
}

export default ColumnPage;
