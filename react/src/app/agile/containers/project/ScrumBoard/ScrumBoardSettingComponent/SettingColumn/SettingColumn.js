import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import { stores, Permission } from 'choerodon-front-boot';
import {
  Input, message, Icon, Modal,
} from 'choerodon-ui';
import StatusCard from '../StatusCard/StatusCard';
import './SettingColumn.scss';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import EasyEdit from '../../../../../components/EasyEdit/EasyEdit';

const { AppState } = stores;

@observer
class SettingColumn extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
    };
  }

  handleDeleteColumn() {
    this.setState({
      visible: true,
    });
    // ScrumBoardStore.axiosDeleteColumn(this.props.data.columnId).then((data) => {
    //   this.props.refresh();
    // }).catch((err) => {
    // });
  }

  updateColumnMaxMin(type, value) {
    let totalIssues = 0;
    const { data: propData, refresh } = this.props;
    for (let index = 0, len = propData.subStatuses.length; index < len; index += 1) {
      for (let index2 = 0, len2 = propData.subStatuses[index].issues.length; index2 < len2; index2 += 1) {
        if (ScrumBoardStore.getCurrentConstraint === 'issue') {
          totalIssues += 1;
        } else if (propData.subStatuses[index].issues[index2].typeCode !== 'sub_task') {
          totalIssues += 1;
        }
      }
    }
    const maxminObj = {};
    if (type === 'maxNum') {
      // if (this.props.data.minNum) {
      //   if (parseInt(value, 10) < parseInt(this.props.data.minNum, 10)) {
      //     Choerodon.prompt('最大值不能小于最小值');
      //     return;
      //   }
      // }
      // if (parseInt(value, 10) < totalIssues) {
      //   Choerodon.prompt('最大值不能小于当前已有issue数');
      //   return;
      // }
      maxminObj.maxNum = value;
      maxminObj.minNum = propData.minNum;
    }
    if (type === 'minNum') {
      // if (this.props.data.maxNum) {
      //   if (parseInt(value, 10) > parseInt(this.props.data.maxNum, 10)) {
      //     Choerodon.prompt('最小值不能大于最大值');
      //     return;
      //   }
      // }
      // if (parseInt(value, 10) > totalIssues) {
      //   Choerodon.prompt('最小值不能大于当前已有issue数');
      //   return;
      // }
      maxminObj.minNum = value;
      maxminObj.maxNum = propData.maxNum;
    }
    const data = {
      boardId: ScrumBoardStore.getSelectedBoard,
      columnId: propData.columnId,
      objectVersionNumber: propData.objectVersionNumber,
      projectId: AppState.currentMenuType.id,
      ...maxminObj,
    };
    ScrumBoardStore.axiosUpdateMaxMinNum(
      propData.columnId, data,
    ).then(() => {
      Choerodon.prompt('设置成功');
      refresh();
    }).catch((error) => {
    });
  }

  handleSaveColumnName(name) {
    const { data: propData, index } = this.props;
    const data = {
      columnId: propData.columnId,
      objectVersionNumber: propData.objectVersionNumber,
      name,
      projectId: AppState.currentMenuType.id,
      boardId: ScrumBoardStore.getSelectedBoard,
    };
    ScrumBoardStore.axiosUpdateColumn(
      propData.columnId, data, ScrumBoardStore.getSelectedBoard,
    ).then((res) => {
      const originData = ScrumBoardStore.getBoardData;
      originData[index].objectVersionNumber = res.objectVersionNumber;
      originData[index].name = res.name;
      ScrumBoardStore.setBoardData(originData);
    }).catch((error) => {
    });
  }

  renderStatus() {
    const { data, draggabled, refresh } = this.props;
    const list = data.subStatuses;
    const result = [];
    for (let index = 0, len = list.length; index < len; index += 1) {
      result.push(
        <StatusCard
          draggabled={draggabled}
          key={`${data.columnId}-${index}`}
          columnId={data.columnId}
          data={list[index]}
          index={index}
          refresh={refresh.bind(this)}
          // setLoading={this.props.setLoading}
        />,
      );
    }
    return result;
  }

  render() {
    const menu = AppState.currentMenuType;
    const {
      data, disabled, refresh, objectVersionNumber, columnId, index, draggabled,
    } = this.props;
    const { visible } = this.state;
    const { type, id: projectId, organizationId: orgId } = menu;

    if (disabled) {
      return (
        <div
          className="c7n-scrumsetting-column"
          style={{
            flex: 1,
            height: '100%',
          }}
        >
          <div
            className="c7n-scrumsetting-columnContent"
            style={{
              background: 'white',
            }}
          >
            <div className="c7n-scrumsetting-columnTop">
              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.board.deleteScrumBoard']}>
                <div
                  className="c7n-scrumsetting-icons"
                  style={{
                    visibility: data.columnId === 'unset' ? 'hidden' : 'visible',
                  }}
                >
                  <Icon
                    type="open_with"
                    style={{
                      cursor: 'pointer',
                    }}
                  />
                  <Icon
                    type="delete"
                    style={{
                      cursor: 'pointer',
                    }}
                    role="none"
                    onClick={this.handleDeleteColumn.bind(this)}
                  />
                  <Modal
                    title="删除列"
                    visible={visible || false}
                    onOk={() => {
                      this.setState({
                        visible: false,
                      });
                      ScrumBoardStore.axiosDeleteColumn(data.columnId).then(() => {
                        refresh();
                      }).catch((err) => {
                      });
                    }}
                    onCancel={() => {
                      this.setState({
                        visible: false,
                      });
                    }}
                    okText="确定"
                    cancelText="取消"
                  >
                    {'确定要删除该列？'}
                  </Modal>
                </div>
              </Permission>

              <div className="c7n-scrumsetting-columnStatus">
                {data.name}
              </div>
              <div style={{ borderBottom: '3px solid rgba(0,0,0,0.26)' }} className="c7n-scrumsetting-columnBottom">
                {
                  data.columnId === 'unset' ? (
                    <div>
                      <span>无该问题的状态</span>
                    </div>
                  ) : (
                    <div>
                      <span style={{ cursor: 'pointer' }}>
                        {'最大值：'}
                        {data.maxNum}
                      </span>
                      <span style={{ cursor: 'pointer' }}>
                        {'最小值：'}
                        {data.maxNum}
                      </span>
                    </div>
                  )
                }
              </div>
            </div>

            <div className="c7n-scrumsetting-columnDrop">
              <Droppable
                type="status"
                droppableId={`${data.categoryCode},${data.columnId}`}
              >
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    style={{
                      background: snapshot.isDraggingOver ? 'rgba(26,177,111,0.08)' : 'unset',
                      height: '100%',
                      minHeight: '84px',
                    }}
                  >
                    {this.renderStatus()}
                    {/* {provided.placeholder} */}
                  </div>
                )}
              </Droppable>
            </div>
          </div>
        </div>
      );
    } else {
      return (
        <Draggable
          isDragDisabled={draggabled}
          key={data.columnId}
          index={index}
          draggableId={JSON.stringify({
            columnId: data.columnId,
            objectVersionNumber: data.objectVersionNumber,
          })}
          type="columndrop"
        >
          {(provided1, snapshot1) => (
            <div
              className="c7n-scrumsetting-column"
              ref={provided1.innerRef}
              {...provided1.draggableProps}
              style={{
                flex: 1,
                // width: this.props.styleValue,
                ...provided1.draggableProps.style,
              }}
            >
              <div className="c7n-scrumsetting-columnContent">
                <div className="c7n-scrumsetting-columnTop">
                  <div
                    style={{
                      visibility: data.columnId === 'unset' ? 'hidden' : 'visible',
                    }}
                  >
                    <div
                      className="c7n-scrumsetting-icons"
                    >
                      <Icon
                        type="open_with"
                        style={{
                          cursor: 'move',
                          display: draggabled && 'none',
                        }}
                        {...provided1.dragHandleProps}
                      />
                      <Icon
                        type="delete"
                        style={{
                          cursor: 'pointer',
                          display: draggabled && 'none',
                        }}
                        role="none"
                        onClick={this.handleDeleteColumn.bind(this)}
                      />
                    </div>
                    <Modal
                      title="删除列"
                      visible={visible || false}
                      onOk={() => {
                        this.setState({
                          visible: false,
                        });
                        ScrumBoardStore.axiosDeleteColumn(data.columnId).then(() => {
                          refresh();
                        }).catch((err) => {
                        });
                      }}
                      onCancel={() => {
                        this.setState({
                          visible: false,
                        });
                      }}
                      okText="确定"
                      cancelText="取消"
                    >
                      {'确定要删除该列？'}
                    </Modal>
                  </div>
                  <div className="c7n-scrumsetting-columnStatus">
                    <Permission
                      type={type}
                      projectId={projectId}
                      organizationId={orgId}
                      service={['agile-service.board.deleteScrumBoard']}
                      noAccessChildren={(
                        data.name
                    )}
                    >
                      <EasyEdit
                        type="input"
                        defaultValue={data.name}
                        enterOrBlur={this.handleSaveColumnName.bind(this)}
                      >
                        {data.name}
                      </EasyEdit>
                    </Permission>
                  </div>
                  <div
                    className="c7n-scrumsetting-columnBottom"
                    style={{
                      borderBottom: data.color ? `3px solid ${data.color}` : '3px solid rgba(0,0,0,0.26)',
                    }}
                  >
                    {
                      data.columnId === 'unset' ? (
                        <div>
                          <span>无该问题的状态</span>
                        </div>
                      ) : (
                        <div
                          style={{
                            visibility: ScrumBoardStore.getCurrentConstraint === 'constraint_none' ? 'hidden' : 'visible',
                            display: 'flex',
                            justifyContent: 'space-between',
                            flexWrap: 'wrap',
                          }}
                        >
                          <Permission
                            type={type}
                            projectId={projectId}
                            organizationId={orgId}
                            service={['agile-service.project-info.updateProjectInfo']}
                            noAccessChildren={(
                              <span
                                style={{ minWidth: '110px' }}
                              >
                                {'最大值：'}
                                {typeof data.maxNum === 'number' ? data.maxNum : '没有最大'}
                              </span>
                            )}
                          >
                            <EasyEdit
                              className="editSpan"
                              type="input"
                              defaultValue={data.maxNum
                                ? data.maxNum : null}
                              enterOrBlur={(value) => {
                                this.updateColumnMaxMin('maxNum', value);
                              }}
                            >
                              <span
                                style={{ cursor: 'pointer', minWidth: '110px' }}
                              >
                                {'最大值：'}
                                {typeof data.maxNum === 'number' ? data.maxNum : '没有最大'}
                              </span>
                            </EasyEdit>
                          </Permission>
                          <Permission
                            type={type}
                            projectId={projectId}
                            organizationId={orgId}
                            service={['agile-service.project-info.updateProjectInfo']}
                            noAccessChildren={(
                              <span
                                style={{ minWidth: '110px' }}
                              >
                                {'最小值：'}
                                {typeof data.minNum === 'number' ? data.minNum : '没有最小'}
                              </span>
                            )}
                          >
                            <EasyEdit
                              className="editSpan"
                              type="input"
                              defaultValue={data.minNum
                                ? data.minNum : null}
                              enterOrBlur={(value) => {
                                this.updateColumnMaxMin('minNum', value);
                              }}
                            >
                              <span
                                style={{ cursor: 'pointer', minWidth: '110px' }}
                              >
                                {'最小值：'}
                                {typeof data.minNum === 'number' ? data.minNum : '没有最小'}
                              </span>
                            </EasyEdit>
                          </Permission>
                        </div>
                      )
                    }
                  </div>
                </div>
                <div className="c7n-scrumsetting-columnDrop">
                  <Droppable
                    type="status"
                    droppableId={`${data.categoryCode},${data.columnId},${data.minNum},${data.maxNum}`}
                  >
                    {(provided, snapshot) => (
                      <div
                        ref={provided.innerRef}
                        style={{
                          background: snapshot.isDraggingOver
                            ? 'rgba(26,177,111,0.08)' : 'unset',
                          height: '100%',
                        }}
                      >
                        {this.renderStatus()}
                        {provided.placeholder}
                      </div>
                    )}
                  </Droppable>
                </div>
              </div>
              {provided1.placeholder}
            </div>
          )}
        </Draggable>
      );
    }
  }
}

export default SettingColumn;
