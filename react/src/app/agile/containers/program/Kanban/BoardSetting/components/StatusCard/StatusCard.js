import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Draggable } from 'react-beautiful-dnd';
import {
  Radio, Icon, Tooltip, Modal,
} from 'choerodon-ui';
import { stores, Permission } from 'choerodon-front-boot';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';
import EditStatus from '../EditStatus/EditStatus';
import './StatusCard.scss';

const { AppState } = stores;
const { confirm, warning } = Modal;

@observer
class StatusCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      disabled: false,
    };
  }

  getStatusNumber() {
    const data = KanbanStore.getBoardData;
    let length = 0;
    for (let index = 0, len = data.length; index < len; index += 1) {
      length += data[index].subStatuses.length;
    }
    return length;
  }

  handleDeleteClick = async () => {
    const { data } = this.props;
    const deleteCode = data.statusId;
    const canBeDeleted = await KanbanStore.axiosStatusCanBeDelete(deleteCode);
    const that = this;
    if (canBeDeleted) {
      confirm({
        title: '移除状态',
        content: `确定要移除状态 ${data.name}？`,
        onOk() {
          that.handleDeleteStatus();
        },
        onCancel() {},
      });
    } else {
      warning({
        title: '移除状态',
        content: `无法移除初始状态 ${data.name}，如要移除请联系组织管理员。`,
        onOk() {},
        onCancel() {},
      });
    }
  };

  async handleDeleteStatus() {
    const { data: propData, refresh } = this.props;
    const originData = JSON.parse(JSON.stringify(KanbanStore.getBoardData));
    const data = JSON.parse(JSON.stringify(KanbanStore.getBoardData));
    const deleteCode = propData.statusId;
    let deleteIndex = '';
    for (let index = 0, len = data[data.length - 1].subStatuses.length; index < len; index += 1) {
      if (String(data[data.length - 1].subStatuses[index].id) === String(deleteCode)) {
        deleteIndex = index;
      }
    }
    data[data.length - 1].subStatuses.splice(deleteIndex, 1);
    KanbanStore.setBoardData(data);
    try {
      await KanbanStore.axiosDeleteStatus(deleteCode);
    } catch (err) {
      KanbanStore.setBoardData(originData);
    }
    refresh();
  }

  renderCloseDisplay() {
    const { columnId, data } = this.props;
    if (columnId === 'unset') {
      if (data.issues.length === 0) {
        if (this.getStatusNumber() > 1) {
          return 'block';
        }
      }
    }
    return 'none';
  }

  renderBackground() {
    const { data: { categoryCode } } = this.props;
    switch (categoryCode) {
      case 'todo':
        return 'rgb(255, 177, 0)';
      case 'doing':
        return 'rgb(77, 144, 254)';
      case 'done':
        return 'rgb(0, 191, 165)';
      case 'prepare':
        return '#F67F5A';
      default:
        return 'gray';
    }    
  }

  render() {
    this.getStatusNumber();
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const {
      data, index, refresh, noPermission, ...otherProps
    } = this.props;
    const { visible, disabled } = this.state;
    return (
      <Draggable
        isDragDisabled={noPermission}
        key={data.code}
        draggableId={`${data.statusId},${data.objectVersionNumber},${data.categoryCode}`}
        index={index}
        type="status"
        {...otherProps}
      >
        {(provided, snapshot) => (
          <div>
            <div
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              style={{
                userSelect: 'none',
                ...provided.draggableProps.style,
              }}
              className="c7n-scrumsetting-card"
            >
              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.issue-status.updateStatus']}>
                <Icon
                  style={{
                    position: 'absolute',
                    right: this.renderCloseDisplay() === 'block' ? 32 : 12,
                    top: '15px',
                    cursor: 'pointer',
                    fontSize: '14px',
                    visibility: 'hidden',
                  }}
                  type="settings"
                  role="none"
                  onClick={() => {
                    if (JSON.stringify(KanbanStore.getStatusCategory) === '{}') {
                      KanbanStore.axiosGetStatusCategory().then((backData) => {
                        KanbanStore.setStatusCategory(backData);
                        this.setState({
                          visible: true,
                        });
                      }).catch((error) => {
                      });
                    } else {
                      this.setState({
                        visible: true,
                      });
                    }
                  }}
                />
              </Permission>
              {KanbanStore.getCanAddStatus ? (
                <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.issue-status.deleteStatus']}>
                  <Icon
                    style={{
                      position: 'absolute',
                      top: 15,
                      right: 12,
                      display: this.renderCloseDisplay(),
                      cursor: 'pointer',
                      fontSize: '14px',
                      // visibility: 'hidden',
                    }}
                    role="none"
                    onClick={this.handleDeleteClick}
                    type="delete"
                  />
                </Permission>
              ) : ''}
              <EditStatus
                visible={visible}
                onChangeVisible={(item) => {
                  this.setState({
                    visible: item,
                  });
                }}
                data={data}
                refresh={refresh.bind(this)}
              />
              <span
                className="c7n-scrumsetting-cardStatus"
                style={{
                  background: data.categoryCode ? this.renderBackground() : '',
                  color: 'white',
                }}
              >
                {data.status ? data.status : data.name}
              </span>
              <div style={{
                display: 'flex', justifyContent: 'space-between', marginTop: 10, flexWrap: 'wrap',
              }}
              >
                <p className="textDisplayOneColumn">
                  {data.issues ? `${data.issues.length} issues` : ''}
                </p>
                <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.issue-status.updateStatus']}>
                  <Radio
                    disabled={disabled}
                    style={{ marginRight: 0 }}
                    checked={data.completed ? data.completed : false}
                    onClick={() => {
                      const clickData = {
                        id: data.id,
                        statusId: data.statusId,
                        objectVersionNumber: data.objectVersionNumber,
                        completed: !data.completed,
                        projectId: AppState.currentMenuType.id,
                      };
                      this.setState({
                        disabled: true,
                      });
                      KanbanStore.axiosUpdateIssueStatus(
                        data.id, clickData,
                      ).then((res) => {
                        refresh();
                      }).then((res) => {
                        this.setState({
                          disabled: false,
                        });
                      })
                        .catch((error) => {
                        });
                    }}
                  >
                    {'设置已完成'}
                    <Tooltip title="勾选后，卡片处于此状态的编号会显示为：#̶0̶0̶1̶，卡片状态视为已完成。" placement="topRight">
                      <Icon
                        type="help"
                        style={{
                          fontSize: 14, color: '#bdbdbd', height: 20, lineHeight: 1.25, marginLeft: 2,
                        }}
                      />
                    </Tooltip>
                  </Radio>
                </Permission>
              </div>
            </div>
            {provided.placeholder}
          </div>
        )
        }
      </Draggable>
    );
  }
}

export default StatusCard;
