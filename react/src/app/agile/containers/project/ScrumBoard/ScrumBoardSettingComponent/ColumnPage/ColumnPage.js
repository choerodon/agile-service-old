import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content, stores, Permission } from 'choerodon-front-boot';
import {
  Button, Select, Icon, message,
} from 'choerodon-ui';
import _ from 'lodash';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import SettingColumn from '../SettingColumn/SettingColumn';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import SideBarContent from '../SideBarContent/SideBarContent';

const { AppState } = stores;
const { Option } = Select;

@observer
class ColumnPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addStatus: false,
      addColumn: false,
    };
  }

  handleAddStatus() {
    this.setState({
      addStatus: true,
    });
    if (JSON.stringify(ScrumBoardStore.getStatusCategory) === '{}') {
      ScrumBoardStore.axiosGetStatusCategory().then((data) => {
        ScrumBoardStore.setStatusCategory(data);
      }).catch((error) => {
      });
    }
  }

  handleDragEnd(result) {
    if (!result.destination) {
      return;
    }
    const { refresh } = this.props;
    // 移动列
    if (result.destination.droppableId === 'columndrop') {
      const originState2 = JSON.parse(JSON.stringify(ScrumBoardStore.getBoardData));
      const newState2 = JSON.parse(JSON.stringify(ScrumBoardStore.getBoardData));
      let draggableData2 = {};
      [draggableData2] = newState2.splice(result.source.index, 1);
      newState2.splice(result.destination.index, 0, draggableData2);
      ScrumBoardStore.setBoardData(newState2);
      const data = {
        boardId: ScrumBoardStore.getSelectedBoard,
        columnId: JSON.parse(result.draggableId).columnId,
        projectId: parseInt(AppState.currentMenuType.id, 10),
        sequence: result.destination.index,
        objectVersionNumber: JSON.parse(result.draggableId).objectVersionNumber,
      };
      ScrumBoardStore.axiosUpdateColumnSequence(
        ScrumBoardStore.getSelectedBoard, data,
      ).then((res) => {
        refresh();
      }).catch((error) => {
        ScrumBoardStore.setBoardData(originState2);
      });
    } else {
      // 移动状态
      const originState = JSON.parse(JSON.stringify(ScrumBoardStore.getBoardData));
      const newState = JSON.parse(JSON.stringify(ScrumBoardStore.getBoardData));
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
        if (ScrumBoardStore.getCurrentConstraint !== 'constraint_none') {
          for (
            let index = 0, len = newState[columnIndex].subStatuses.length;
            index < len;
            index += 1) {
            for (
              let index2 = 0, len2 = newState[columnIndex].subStatuses[index].issues.length;
              index2 < len2;
              index2 += 1) {
              if (ScrumBoardStore.getCurrentConstraint === 'issue') {
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
        ScrumBoardStore.setBoardData(newState);
        ScrumBoardStore.moveStatusToUnset(code, {
          columnId,
        }).then((data) => {
          const newData = data;
          newData.issues = draggableData.issues;
          for (let index = 0, len = newState.length; index < len; index += 1) {
            if (String(newState[index].columnId) === String(result.destination.droppableId.split(',')[1])) {
              newState[index].subStatuses.splice(result.destination.index, 1, newData);
            }
          }
          ScrumBoardStore.setBoardData(newState);
          refresh();
        }).catch((error) => {
          ScrumBoardStore.setBoardData(originState);
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
        if (ScrumBoardStore.getCurrentConstraint !== 'constraint_none') {
          for (
            let index = 0, len = newState[columnIndex].subStatuses.length;
            index < len;
            index += 1) {
            for (
              let index2 = 0, len2 = newState[columnIndex].subStatuses[index].issues.length;
              index2 < len2;
              index2 += 1) {
              if (ScrumBoardStore.getCurrentConstraint === 'issue') {
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
                  if (ScrumBoardStore.getCurrentConstraint === 'issue') {
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
            if (ScrumBoardStore.getCurrentConstraint === 'issue') {
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
        ScrumBoardStore.setBoardData(newState);
        ScrumBoardStore.moveStatusToColumn(code, {
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
          ScrumBoardStore.setBoardData(newState);
          refresh();
        }).catch((error) => {
          ScrumBoardStore.setBoardData(originState);
        });
      }
    }
  }

  handleAddColumn() {
    this.setState({
      addColumn: true,
    });
    if (JSON.stringify(ScrumBoardStore.getStatusCategory) === '{}') {
      ScrumBoardStore.axiosGetStatusCategory().then((data) => {
        ScrumBoardStore.setStatusCategory(data);
      }).catch((error) => {
      });
    }
  }

  renderColumn(data) {
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const result = [];
    const { refresh } = this.props;
    for (let index = 0, len = data.length; index < len; index += 1) {
      result.push(
        <Permission
          type={type}
          projectId={projectId}
          organizationId={orgId}
          service={['agile-service.board.deleteScrumBoard']}
          noAccessChildren={(
            <SettingColumn
              draggabled
              data={data[index]}
              refresh={refresh.bind(this)}
          // setLoading={this.props.setLoading.bind}
              index={index}
              styleValue={`${parseFloat(parseFloat(1 / data.length) * 100)}%`}
            />
      )}
        >
          <SettingColumn
            data={data[index]}
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
    const BoardData = ScrumBoardStore.getBoardData;
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
                draggabled
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
    const BoardData = JSON.parse(JSON.stringify(ScrumBoardStore.getBoardData));
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
        description="分栏可以添加、删除、重新排序和重命名。列是基于全局状态和可移动的列与列之间。最小和最大限制可设置为每个已映射的列中。"
        style={{
          padding: 0,
          overflow: 'unset',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
        }}
        link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/"
      >
        <div
          style={{
            marginTop: 16,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
          }}
        >
          <Permission
            type={type}
            projectId={projectId}
            organizationId={orgId}
            service={['agile-service.project-info.updateProjectInfo']}
            noAccessChildren={(
              <Select
                value={ScrumBoardStore.getCurrentConstraint}
                label="列约束"
                style={{ width: 512 }}
                disabled
              >
                {
                  ScrumBoardStore.getLookupValue.constraint ? (
                    ScrumBoardStore.getLookupValue.constraint.map(item => (
                      <Option key={item.valueCode} value={item.valueCode}>{item.name}</Option>
                    ))
                  ) : ''
                }
              </Select>
            )}
          >
            <Select
              value={ScrumBoardStore.getCurrentConstraint}
              label="列约束"
              style={{ width: 512 }}
              onChange={(value) => {
                const oldData = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
                ScrumBoardStore.axiosUpdateBoard({
                  boardId: ScrumBoardStore.getSelectedBoard,
                  columnConstraint: value,
                  projectId: AppState.currentMenuType.id,
                  objectVersionNumber: oldData.objectVersionNumber,
                }).then((res) => {
                  ScrumBoardStore.setBoardList(ScrumBoardStore.getSelectedBoard, res);
                  ScrumBoardStore.setCurrentConstraint(value);
                }).catch((error) => {
                });
              }}
            >
              {
                ScrumBoardStore.getLookupValue.constraint ? (
                  ScrumBoardStore.getLookupValue.constraint.map(item => (
                    <Option key={item.valueCode} value={item.valueCode}>{item.name}</Option>
                  ))
                ) : ''
              }
            </Select>
          </Permission>
          <div>
            {
              ScrumBoardStore.getCanAddStatus ? (
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
                  {this.renderColumn(BoardData)}
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
              store={ScrumBoardStore}
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
              store={ScrumBoardStore}
            />
          ) : ''
        }
      </Content>
    );
  }
}

export default ColumnPage;
