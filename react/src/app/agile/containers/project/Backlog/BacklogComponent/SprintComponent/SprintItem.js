import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Droppable } from 'react-beautiful-dnd';
import {
  Input, Button, Select, Icon, Tooltip, Modal, Avatar, Dropdown, Menu,
} from 'choerodon-ui';
import './Sprint.scss';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import SprintContainer from './SprintItemComponent/SprintContainer';
import NoneSprint from './SprintItemComponent/NoneSprint';

const { confirm } = Modal;

@observer
class SprintItem extends Component {
  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  /**
   *删除冲刺事件
   *
   * @param {*} e
   * @memberof SprintItem
   */
  handleDeleteSprint = (item, e) => {
    const that = this;
    const { store, refresh } = this.props;
    if (e.key === '0') {
      if (item.issueSearchDTOList && item.issueSearchDTOList.length > 0) {
        confirm({
          width: 560,
          wrapClassName: 'deleteConfirm',
          title: `删除冲刺${item.sprintName}`,
          content: (
            <div>
              <p style={{ marginBottom: 10 }}>请确认您要删除这个冲刺。</p>
              <p style={{ marginBottom: 10 }}>这个冲刺将会被彻底删除，冲刺中的任务将会被移动到待办事项中。</p>
            </div>
          ),
          onOk() {
            return that.props.store.axiosDeleteSprint(item.sprintId).then((res) => {
              that.props.refresh();
            }).catch((error) => {
            });
          },
          onCancel() {},
          okText: '删除',
          okType: 'danger',
        });
      } else {
        store.axiosDeleteSprint(item.sprintId).then((res) => {
          refresh();
        }).catch((error) => {
        });
      }
    }
  };

  render() {
    const { refresh, display, isInProgram } = this.props;
    const arr = BacklogStore.getSprintData;
    // 冲刺按id正序，活跃冲刺排在最上面
    let displayList = arr.length ? arr.sort((a, b) => a.sprintId - b.sprintId) : [];
    displayList = [
      ...displayList.filter(item => item.statusCode === 'started'),
      ...displayList.filter(item => item.statusCode !== 'started'),
    ];
    if (isInProgram && !display && arr.length) {
      const start = arr.filter(s => s.statusCode === 'started');
      if (start.length === 0) {
        displayList = [displayList[0]];
      } else {
        displayList = start;
      }
    }

    return (
      <div
        role="none"
        style={{
          width: '100%',
        }}
        onClick={() => {
          if (!BacklogStore.isDragging) {
            BacklogStore.onBlurClick();
          }
        }}
      >
        {
          displayList.length
            ? displayList.map(sprintItem => (
              <SprintContainer
                isCreated={sprintItem.isCreated}
                refresh={refresh}
                key={sprintItem.sprintId}
                data={sprintItem}
                type="sprint"
              />
            )) : <NoneSprint />
        }
        <SprintContainer
          key="0"
          data={BacklogStore.getBacklogData}
          type="backlog"
        />
      </div>
    );
  }
}

export default SprintItem;
