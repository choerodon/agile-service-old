import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Form, Modal, Select } from 'choerodon-ui';
import { Content, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

const { Sidebar } = Modal;
const { AppState } = stores;
const { Option } = Select;

@observer
class CloseSprint extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectChose: '0',
    };
  }

  /**
   *完成冲刺事件
   *
   * @memberof CloseSprint
   */
  handleCloseSprint() {
    const { selectChose } = this.state;
    const {
      store, data: propData, onCancel, refresh,
    } = this.props;
    const data = {
      incompleteIssuesDestination: parseInt(selectChose, 10),
      projectId: parseInt(AppState.currentMenuType.id, 10),
      sprintId: propData.sprintId,
    };
    store.axiosCloseSprint(data).then((res) => {
      onCancel();
      refresh();
    }).catch((error) => {
    });
  }

  render() {
    const {
      data, store, visible, onCancel,
    } = this.props;
    const { selectChose } = this.state;
    const completeMessage = JSON.stringify(store.getSprintCompleteMessage) === '{}' ? null : store.getSprintCompleteMessage;
    return visible
      ? (
        <Sidebar
          title="完成冲刺"
          visible={visible}
          okText="结束"
          cancelText="取消"
          onCancel={onCancel.bind(this)}
          onOk={this.handleCloseSprint.bind(this)}
        >
          <Content
            style={{
              padding: 0,
              paddingBottom: 20,
            }}
            title={`完成冲刺“${data.sprintName}”`}
            description="请在下面选择未完成问题的去向，以完成一个冲刺计划。"
            link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/close-sprint/"
          >
            <p className="c7n-closeSprint-message">
              <span>{!_.isNull(completeMessage) ? completeMessage.partiallyCompleteIssues : ''}</span>
              {' '}
个问题 已经完成
            </p>
            <p style={{ marginTop: 24 }} className="c7n-closeSprint-message">
              <span>{!_.isNull(completeMessage) ? completeMessage.incompleteIssues : ''}</span>
              {' '}
个问题 未完成
            </p>
            <p style={{ marginTop: 19, color: 'rgba(0,0,0,0.65)' }}>{`其中有${completeMessage ? completeMessage.parentsDoneUnfinishedSubtasks.length : 0}个问题包含子任务，父级任务移动后与之相关的子任务也会被移动`}</p>
            <p style={{ fontSize: 14, marginTop: 36 }}>选择该冲刺未完成的问题：</p>
            <Select
              label="移动至"
              style={{ marginTop: 12, width: 512 }}
              value={selectChose}
              onChange={(value) => {
                this.setState({
                  selectChose: value,
                });
              }}
            >
              {!_.isNull(completeMessage) ? (
                completeMessage.sprintNames.map(item => (
                  <Option value={item.sprintId}>{item.sprintName}</Option>
                ))
              ) : ''}
              <Option value="0">待办事项</Option>
            </Select>
          </Content>
        </Sidebar>
      ) : null;
  }
}

export default CloseSprint;
