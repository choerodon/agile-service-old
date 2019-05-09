import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Modal, Select, Tooltip, Icon,
} from 'choerodon-ui';
import { Content, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import FeatureStore from '../../../../../stores/program/Feature/FeatureStore';

const { Sidebar } = Modal;
const { AppState } = stores;
const { Option } = Select;

@observer
class ClosePI extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectChose: '0',
      targetStatus: null,
    };
  }

  handleTargetStatusChange=(value) => {
    this.setState({
      targetStatus: value,
    });
  };

  /**
   *完成PI事件
   *
   * @memberof ClosePI
   */
  handleClosePI() {
    const {
      store, data: propData, onCancel, refresh,
    } = this.props;
    const { selectChose, targetStatus } = this.state;
    const todoStatusList = FeatureStore.getTodoStatusList;
    const completeMessage = JSON.stringify(store.getPICompleteMessage) === '{}' ? null : store.getPICompleteMessage;
    const updateStatusId = targetStatus || todoStatusList[0].id;

    const data = {
      programId: AppState.currentMenuType.id,
      id: propData.id,
      artId: propData.artId,
      targetPiId: selectChose,
      objectVersionNumber: propData.objectVersionNumber,
      updateStatusId,
      statusCategoryCode: updateStatusId && 'todo',
    };
    store.closePI(data).then(() => {
      onCancel();
      refresh();
    }).catch(() => {
    });
  }


  render() {
    const {
      data, store, visible, onCancel,
    } = this.props;
    const { selectChose } = this.state;
    const todoStatusList = FeatureStore.getTodoStatusList;
    const completeMessage = JSON.stringify(store.getPICompleteMessage) === '{}' ? null : store.getPICompleteMessage;
    return visible
      ? (
        <Sidebar
          title="完成PI"
          visible={visible}
          okText="结束"
          cancelText="取消"
          onCancel={onCancel.bind(this)}
          onOk={this.handleClosePI.bind(this)}
        >
          <Content
            style={{
              padding: 0,
              paddingBottom: 20,
            }}
            title={`完成PI“${data.name}”`}
            description="请在下面选择未完成特性的去向，以完成一个PI计划。注意：完成当前PI后会自动为您开启下一个PI。"
          >
            <p className="c7n-closeSprint-message">
              <span>{!_.isNull(completeMessage) ? completeMessage.completedCount : ''}</span>
              {' 个特性 已经完成'}
            </p>
            <p style={{ marginTop: 24 }} className="c7n-closeSprint-message">
              <span>{!_.isNull(completeMessage) ? completeMessage.unCompletedCount : ''}</span>
              {' 个特性 未完成'}
            </p>
            <p style={{ fontSize: 14, marginTop: 36 }}>选择该PI未完成的问题：</p>
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
                completeMessage.piTodoDTOList.map(item => (
                  <Option value={item.id}>{`${item.code}-${item.name}`}</Option>
                ))
              ) : ''}
              <Option value="0">特性列表</Option>
            </Select>
            <br />
            {selectChose !== '0'
              ? (
                <React.Fragment>
                  <Select
                    style={{ marginTop: 24, width: 512 }}
                    label="目标状态"
                    onChange={this.handleTargetStatusChange}
                    defaultValue={todoStatusList[0].id}
                  >
                    {todoStatusList.map(status => <Option value={status.id}>{status.name}</Option>)}
                  </Select>
                  <Tooltip title="自动开启下一个PI时，处于准备阶段状态的问题将会转换到该状态。" placement="top">
                    <Icon
                      type="help"
                      style={{
                        fontSize: 16, color: '#bdbdbd', height: 20, lineHeight: 1.25, marginLeft: 2,
                      }}
                    />
                  </Tooltip>
                </React.Fragment>
              ) : ''
            }
          </Content>
        </Sidebar>
      ) : null;
  }
}

export default ClosePI;
