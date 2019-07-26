/* eslint-disable */
import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Table, Button, Modal, Form, Select, Input, Tooltip, Tabs, Checkbox, Popconfirm, Spin, message,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';
import _ from 'lodash';
import Graph from '../../../../components/Graph';
import StateStore from '../../../../stores/organization/state';
import ReadAndEdit from '../../../../components/ReadAndEdit';
import { getByteLen, getStageMap, getStageList } from '../../../../common/utils';

import './EditStateMachine.scss';

const prefixCls = 'issue-state-machine';
const { AppState } = stores;

const { Sidebar, info, confirm } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const { TabPane } = Tabs;

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 26 },
  },
};

const statusColor = getStageMap();
const stageList = getStageList();

@observer
class EditStateMachine extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);
    const { id, status } = this.props.match.params;
    this.state = {
      page: 0,
      pageSize: 10,
      id,
      status,
      projectId: menu.id,
      organizationId: menu.organizationId,
      openRemove: false,
      show: false,
      submitting: false,
      stateMachineData: {},
      stateList: [],
      source: false,
      target: false,
      enable: status !== 'state_machine_active',
      loading: false,
      publishLoading: false,
      currentRae: undefined,
      name: '',
      description: '',
      deleteLoading: false,
      statusId: false,
      stateName: false,
      error: false,
      isEdit: false,
      displayHeader: true,
      isLoading: false,
      selectedDeleteId: false,
      isSelect: true,
    };
    this.graph = null;
    this.update = false;
  }

  componentDidMount() {
    this.loadStateMachine();
  }

  getColumn = () => {
    const { transferData, nodeData, enable } = this.state;
    const column = [{
      title: <FormattedMessage id="stateMachine.state" />,
      dataIndex: 'statusVO',
      key: 'statusVO',
      width: 300,
      render: (text, record) => text && (
        <div
          className={`${prefixCls}-text-node`}
          style={{
            backgroundColor: record.statusVO
            && record.statusVO.type && statusColor[record.statusVO.type].colour,
          }}
        >{text.name}</div>
      ),
    }, {
      title: <FormattedMessage id="stateMachine.transfer" />,
      dataIndex: 'id',
      key: 'id',
      render: (id) => {
        const { nodeTransfer = {} } = this.state;
        return (
          <React.Fragment>
            {transferData && transferData.map(item => (item.startNodeId === id || item.type === 'transform_all') && (
              <div className={`${prefixCls}-text-transfer-item`} key={item.id}>
                {`${item.name}  >>>`} {
                  nodeData && nodeData.map(node => node.id === item.endNodeId && (
                    <div
                      className={`${prefixCls}-text-node`}
                      key={`${item.id}-${node.id}`}
                      style={{
                        backgroundColor: node.statusVO
                        && node.statusVO.type && statusColor[node.statusVO.type].colour,
                      }}
                    >
                      {node.statusVO && node.statusVO.name}
                    </div>
                  ))
                }
              </div>
            ))}
          </React.Fragment>
        );
      },
    }];
    if (enable) {
      column.push({
        align: 'right',
        width: 104,
        key: 'action',
        render: (test, record) => {
          const transfes = _.filter(transferData, item => item.startNodeId === record.id);
          return (
            <div>
              <Tooltip placement="top" title={<FormattedMessage id="stateMachine.transfer.add" />}>
                <Button shape="circle" size={'small'} onClick={this.textTransferAdd.bind(this, record.id)}>
                  <span className="icon icon-add" />
                </Button>
              </Tooltip>
              {transfes && transfes.length
                ? <Tooltip placement="top" title={<FormattedMessage id="delete" />}>
                  <Button shape="circle" size={'small'} onClick={this.textTransferDel.bind(this, record.id)}>
                    <span className="icon icon-delete" />
                  </Button>
                </Tooltip> : <span style={{ display: 'inline-block', width: 24 }} />
              }
              <Permission>
                <Tooltip
                  placement="bottom"
                  title={
                    <div>
                      {!record.synchro
                        ? <FormattedMessage id="app.synch" />
                        : <React.Fragment>
                          {record.active
                            ? <FormattedMessage id="edit" />
                            : <FormattedMessage id="app.start" />
                          }
                          </React.Fragment>
                      }
                      </div>
                  }
                >
                  <span />
                </Tooltip>
              </Permission>
            </div>
          );
        },
      });
    }
    return column;
  };

  getFormContent = () => {
    const {
      type,
      stateList,
      nodeData,
      source,
      target,
      selectedCell,
      statusId,
    } = this.state;
    const { form, intl } = this.props;
    const { getFieldDecorator } = form;
    if (type === 'state') {
      return (
        <div className="issue-region">
          <Form layout="vertical" className="issue-sidebar-form c7nagile-form">
            <FormItem
              {...formItemLayout}
            >
              {getFieldDecorator('state', {
                rules: [{
                  required: true,
                  message: intl.formatMessage({ id: 'required' }),
                }],
                initialValue: statusId ? { key: statusId } : '',
              })(
                <Select
                  style={{ width: 520 }}
                  label={<FormattedMessage id="state.title" />}
                  dropdownMatchSelectWidth
                  size="default"
                  labelInValue
                >
                  {stateList && stateList.length > 0 && stateList.map(s => (
                    <Option
                      value={s.id}
                      key={String(s.id)}
                    >
                      <div className="issue-state-machine-block" style={{ backgroundColor: s.type ? statusColor[s.type].colour : '#FFF' }} />
                      <span id={s.id} name={s.name} style={{ display: 'inline-block', width: '100%', verticalAlign: 'middle' }}>{s.name}</span>
                    </Option>
                  ))}
                </Select>,

              )}
            </FormItem>
            <span onClick={this.handleCreateState} role="none" className={`${prefixCls}-state-create`}>添加新状态</span>
          </Form>
        </div>
      );
    } else {
      return (
        <div className="issue-region">
          <Form layout="vertical" className="issue-sidebar-form c7nagile-form">
            <React.Fragment>
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('startNodeId', {
                  rules: [{
                    required: true,
                    message: intl.formatMessage({ id: 'required' }),
                  }],
                  initialValue: source ? source.statusId || source.statusId === 0 ? source.statusId.toString() : 'all' : '',
                })(
                  <Select
                    style={{ width: 520 }}
                    label={<FormattedMessage id="stateMachine.transfer.source" />}
                    dropdownMatchSelectWidth
                    size="default"
                    disabled={source !== false}
                  >
                    {
                      source.statusId === 0 && (
                        <Option
                          value="0"
                          key="0"
                        >
                          <span style={{ display: 'inline-block', width: '100%' }}><FormattedMessage id="stateMachine.node.name.start" /></span>
                        </Option>
                      )

                    }
                    {
                      source && !source.statusId && (
                        <Option
                          value="all"
                          key="all"
                        >
                          <span style={{ display: 'inline-block', width: '100%' }}><FormattedMessage id="stateMachine.node.name.all" /></span>
                        </Option>
                      )
                    }
                    {nodeData
                      && nodeData.length > 0
                      && nodeData.map(VO => VO.statusVO && (
                        <Option
                          value={VO.statusVO.id.toString()}
                          key={VO.statusVO.toString()}
                        >
                          <div className="issue-state-machine-block" style={{ backgroundColor: VO.statusVO.type ? statusColor[VO.statusVO.type].colour : '#FFF' }} />
                          <span id={VO.id} name={VO.statusVO.name} style={{ display: 'inline-block', width: '100%' }}>{VO.statusVO.name}</span>
                        </Option>
                      ))}
                  </Select>,

                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
              >
                {getFieldDecorator('endNodeId', {
                  rules: [{
                    required: true,
                    message: intl.formatMessage({ id: 'required' }),
                  }],
                  initialValue: target ? target.statusId.toString() : '',
                })(
                  <Select
                    style={{ width: 520 }}
                    label={<FormattedMessage id="stateMachine.transfer.target" />}
                    dropdownMatchSelectWidth
                    size="default"
                    disabled={target !== false}
                  >
                    {nodeData
                      && nodeData.length > 0
                      && nodeData.map(VO => VO.statusVO && (
                        <Option
                          value={VO.statusVO.id.toString()}
                          key={VO.statusVO.toString()}
                        >
                          <div className="issue-state-machine-block" style={{ backgroundColor: VO.statusVO.type ? statusColor[VO.statusVO.type].colour : '#FFF' }} />
                          <span id={VO.statusVO.id} name={VO.statusVO.name} style={{ display: 'inline-block', width: '100%' }}>{VO.statusVO.name}</span>
                        </Option>
                      ))}
                  </Select>,

                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                className="issue-sidebar-form"
              >
                {getFieldDecorator('name', {
                  rules: [{
                    required: true,
                    message: intl.formatMessage({ id: 'required' }),
                  }, {
                    validator: this.checkTransferName,
                  }],
                  initialValue: selectedCell && selectedCell.edge ? selectedCell.name : '',
                })(
                  <Input
                    maxLength={15}
                    style={{ width: 520 }}
                    label={<FormattedMessage id="stateMachine.transfer.name" />}
                  />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                className="issue-sidebar-form"
              >
                {getFieldDecorator('description', {
                  initialValue: selectedCell && selectedCell.edge ? selectedCell.des : '',
                })(
                  <TextArea
                    maxLength={45}
                    style={{ width: 520 }}
                    label={<FormattedMessage id="stateMachine.transfer.des" />}
                  />,
                )}
              </FormItem>
              {/* <FormItem */}
                {/* {...formItemLayout} */}
              {/* > */}
                {/* {getFieldDecorator('page', { */}
                  {/* initialValue: stateData ? { key: stateData.id } : [], */}
                {/* })( */}
                  {/* <Select */}
                    {/* style={{ width: 520 }} */}
                    {/* label={<FormattedMessage id="stateMachine.transfer.page" />} */}
                    {/* dropdownMatchSelectWidth */}
                    {/* size="default" */}
                    {/* labelInValue */}
                  {/* > */}
                    {/* {nodeData && */}
                      {/* nodeData.length > 0 && */}
                      {/* nodeData.map((VO) => { */}
                        {/* if (VO.statusVO) { */}
                          {/* return ( */}
                            {/* <Option */}
                              {/* value={VO.statusVO.id.toString()} */}
                              {/* key={VO.statusVO.toString()} */}
                            {/* > */}
                              {/* <span */}
                                {/* id={VO.statusVO.id} */}
                                {/* name={VO.statusVO.name} */}
                                {/* style={{ display: 'inline-block', width: '100%' }} */}
                              {/* > */}
                                {/* {VO.statusVO.name} */}
                              {/* </span> */}
                            {/* </Option> */}
                          {/* ); */}
                        {/* } */}
                        {/* return null; */}
                      {/* })} */}
                  {/* </Select>, */}

                {/* )} */}
              {/* </FormItem> */}
            </React.Fragment>
          </Form>
        </div>
      );
    }
  };

  checkName = async (rule, value, callback) => {
    const { StateMachineStore, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    if (value && value.trim()) {
      const res = await StateMachineStore.checkStateName(orgId, value);
      if (res && res.statusExist) {
        callback(intl.formatMessage({ id: 'priority.create.name.error' }));
      } else {
        callback();
      }
    } else {
      callback();
    }
  };

  checkTransferName = async (rule, value, callback) => {
    const { id, nodeData, selectedCell } = this.state;
    const { StateMachineStore, intl, form } = this.props;
    const startNodeId = form.getFieldValue('startNodeId');
    const endNodeId = form.getFieldValue('endNodeId');
    const orgId = AppState.currentMenuType.organizationId;
    if (startNodeId && startNodeId !== 'all' && endNodeId && value && value.trim()) {
      if (selectedCell && value === selectedCell.name) {
        callback();
      } else {
        const source = _.find(nodeData, item => item.statusId.toString() === startNodeId);
        const target = _.find(nodeData, item => item.statusId.toString() === endNodeId);
        const res = await StateMachineStore.checkTransferName(
          orgId,
          source.id,
          target.id,
          id,
          value,
        );
        if (res) {
          callback();
        } else {
          callback(intl.formatMessage({ id: 'priority.create.name.error' }));
        }
      }
    } else {
      callback();
    }
  };

  getCreateForm = () => {
    const { form, intl } = this.props;
    const { getFieldDecorator } = form;
    return (<div className="issue-region">
      <Form layout="vertical" className="issue-sidebar-form c7nagile-form">
        <FormItem
          {...formItemLayout}
        >
          {getFieldDecorator('name', {
            rules: [{
              required: true,
              whitespace: true,
              max: 47,
              message: intl.formatMessage({ id: 'state.name.required' }),
            }, {
              validator: this.checkName,
            }],
          })(
            <Input
              style={{ width: 520 }}
              autoFocus
              label={<FormattedMessage id="state.name" />}
              size="default"
              maxLength={15}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
          className="issue-sidebar-form"
        >
          {getFieldDecorator('description')(
            <TextArea
              style={{ width: 520 }}
              label={<FormattedMessage id="state.des" />}
              maxLength={45}
            />,
          )}
        </FormItem>
        <FormItem
          {...formItemLayout}
        >
          {getFieldDecorator('type', {
            rules: [{
              required: true,
              whitespace: true,
              message: intl.formatMessage({ id: 'required' }),
            }],
            initialValue: 'todo',
          })(
            <Select
              style={{ width: 520 }}
              label={<FormattedMessage id="state.stage" />}
              dropdownMatchSelectWidth
              size="default"
            >
              {stageList
                .map(stage => (
                  <Option
                    value={stage.code}
                    key={stage.code}
                  >
                    <div style={{ display: 'inline-block' }}>
                      <div className="issue-state-machine-block" style={{ backgroundColor: stage.colour }} />
                      <span style={{ verticalAlign: 'middle', width: '100%' }}>{stage.name}</span>
                    </div>
                  </Option>
                ))}
            </Select>,

          )}
        </FormItem>
        <div className="issue-state-tips-wrapper">
          <div className="issue-state-tips">帮助识别问题所处的生命周期的某个阶段</div>
          <div className="issue-state-tips">开始处理问题时，从 <span>待处理</span> 到 <span>处理中</span> ，随后，当完成所有工作时，进入到 <span>完成</span> 阶段。</div>
        </div>
      </Form>
    </div>);
  };

  setGraphRef = (graph) => {
    this.graph = graph;
  };

  handleCreateState = () => {
    this.setState({
      createShow: true,
      show: false,
    });
  };

  loadStateMachine = () => {
    const { StateMachineStore } = this.props;
    const { organizationId, id, status } = this.state;
    if (status !== 'state_machine_active') {
      StateMachineStore.loadStateMachineDraftById(organizationId, id)
        .then((data) => {
          if (data && data.failed) {
            Choerodon.prompt(data.message);
          } else {
            const nodeData = data.nodeVOS;
            const transferData = data.transformVOS;
            const { name, description, objectVersionNumber } = data;
            this.setState({
              stateMachineData: data,
              transferData,
              nodeData,
              name,
              description,
              objectVersionNumber,
              loading: false,
            }, () => {
              this.update = false;
            });
          }
        });
    } else {
      StateMachineStore.loadStateMachineDeployById(organizationId, id)
        .then((data) => {
          if (data && data.failed) {
            Choerodon.prompt(data.message);
          } else {
            const nodeData = data.nodeVOS;
            const transferData = data.transformVOS;
            const { name, description, objectVersionNumber } = data;
            this.setState({
              stateMachineData: data,
              transferData,
              nodeData,
              name,
              description,
              objectVersionNumber,
              loading: false,
            }, () => {
              this.update = false;
            });
          }
        });
    }
  };

  refresh = () => {
    this.setState({
      loading: true,
    });
    this.update = true;
    this.loadStateMachine();
  };

  loadStateList = () => {
    const { organizationId, nodeData } = this.state;
    StateStore.loadAllState(organizationId).then((data) => {
      if (data && data.failed) {
        Choerodon.prompt(data.message);
      } else {
        const stateList = _.differenceWith(data,
          nodeData,
          (dataItem, nodeItem) => nodeItem.statusVO && dataItem.id === nodeItem.statusVO.id);
        this.setState({
          stateList,
        });
      }
    });
  };

  addStateMachineNode = (data) => {
    const { organizationId, stateMachineData } = this.state;
    const { StateMachineStore } = this.props;
    const node = {
      positionX: 150,
      positionY: 0,
      statusId: data.state.key,
      stateMachineId: stateMachineData.id,
    };
    this.setState({
      isLoading: true,
    });
    StateMachineStore.addStateMachineNode(organizationId, stateMachineData.id, node)
      .then((nodeData) => {
        if (nodeData && nodeData.failed) {
          Choerodon.prompt(nodeData.message);
        } else if (nodeData && nodeData.length) {
          const cell = _.find(nodeData, item => item.statusId === data.state.key);
          if (cell) {
            this.graph.createStatus(cell);
          }
        }
        this.setState({
          show: false,
          isLoading: false,
          nodeData,
          statusId: false,
          stateName: false,
          isEdit: false,
        });
      });
  };

  updateStateMachineNode = (data) => {
    const { organizationId, stateMachineData } = this.state;
    const { StateMachineStore } = this.props;
    this.setState({
      isLoading: true,
    });
    return StateMachineStore
      .updateStateMachineNode(organizationId, data.id, stateMachineData.id, data);
  };

  addStateMachineTransfer = (data) => {
    const { organizationId, stateMachineData, transferData } = this.state;
    const { StateMachineStore } = this.props;
    const node = {
      ...data,
      // startNodeId: source.id,
      // endNodeId: target.id,
      stateMachineId: stateMachineData.id,
      page: null,
    };
    this.setState({
      isLoading: true,
    });
    StateMachineStore.addStateMachineTransfer(organizationId, stateMachineData.id, node)
      .then((item) => {
        if (item && item.failed) {
          Choerodon.prompt(item.message);
        } else {
          this.graph.createTransition(item, item.startNodeId, item.endNodeId);
          transferData.push(item);
          this.setState({
            show: false,
            transferData,
            isLoading: false,
            isEdit: false,
          });
        }
      });
  };

  updateStateMachineTransfer = (data) => {
    const {
      organizationId,
      stateMachineData,
    } = this.state;
    const { StateMachineStore } = this.props;
    this.setState({
      isLoading: true,
    });
    return StateMachineStore
      .updateStateMachineTransfer(organizationId, data.id, stateMachineData.id, data);
  };

  // DOUBLE CLICK NODE or TRANSFER
  handleDbClick = (cell, type) => {
    const { stateMachineData } = this.state;
    this.setState({
      selectedCell: cell,
      isEdit: true,
    });
    if (type === 'transfer') {
      this.setState({
        source: cell.source,
        target: cell.target,
      });
      this.showSideBar(type, 'edit');
    } else if (type === 'state' && stateMachineData.status === 'state_machine_create') {
      this.showSideBar(type, 'edit');
    }
  };

  // ON DRAG NODE, UPDATE NODE'S POSITION
  handleOnMove = (cell) => {
    const { nodeData } = this.state;
    if (nodeData && nodeData.length) {
      const node = _.find(nodeData, item => (item.id && item.id.toString())
        === (cell.nodeId && cell.nodeId.toString()));
      if (node) {
        const data = {
          ...node,
          positionX: cell.geometry.x,
          positionY: cell.geometry.y,
        };
        this.updateStateMachineNode(data)
          .then((nodes) => {
            if (nodes && nodes.failed) {
              Choerodon.prompt(nodes.message);
            } else {
              this.setState({
                show: false,
                nodeData: nodes,
                isLoading: false,
                isEdit: false,
              });
            }
          });
      }
    }
  };

  handleReLink = (edge, style) => {
    const { transferId } = edge;
    const { transferData } = this.state;
    // GET INDEX OF SELECTED TRANSFER IN TRANSFER DATA
    const index = _.findIndex(transferData,
      item => item.id.toString() === (edge.transferId && edge.transferId.toString()));
    if (index > -1) {
      const param = {
        ...transferData[index],
        startNodeId: edge.source.id,
        endNodeId: edge.target.id,
        page: null,
        style,
      };
      this.updateStateMachineTransfer(param)
        .then((item) => {
          // edge.setValue(item.name);
          // selectedCell.des = item.description;
          // this.graph.refresh();
          if (item && item.failed) {
            Choerodon.prompt(item.message);
          } else {
            transferData[index] = item;
            this.setState({
              show: false,
              isLoading: false,
              transferData,
              isEdit: false,
            });
          }
        });
    }
  };

  handleCellClick = (cell) => {
    if ((cell && cell.statusId !== 0) || !cell) {
      this.setState({
        selectedCell: cell,
        allChecked: cell && cell.allStatusTransformId,
      });
    }
  };

  // DISPLAY TRANSFER NAME or NO
  handleCheckChange = (e) => {
    this.graph.handleCheckChange(e);
  };


  handleSubmit = () => {
    const {
      state,
      type,
      selectedCell,
      edgeStyle,
      stateName,
      stateType,
    } = this.state;
    const { form } = this.props;
    form.validateFields(['name'], { force: true });
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        if (type === 'state' && data.state && data.state.key) {
          const { name } = data.state.label && data.state.label.length
            ? data.state.label[1].props : { name: stateName };
          if (state === 'add') {
            this.addStateMachineNode(data);
          } else {
            const { nodeData } = this.state;
            if (nodeData && nodeData.length) {
              // GET NODE DATA
              const node = _.find(
                nodeData, item => item.id.toString() === selectedCell.nodeId.toString(),
              );
              if (node) {
                // UPDATE STATEID OF NODE DATA
                const param = {
                  ...node,
                  statusId: data.state.key,
                };
                this.updateStateMachineNode(param)
                  .then((nodes) => {
                    if (nodes && nodes.failed) {
                      Choerodon.prompt(nodes.message);
                    } else {
                      // 更新当前状态
                      selectedCell.setValue(name);
                      selectedCell.statusId = data.state.key;
                      // originWide为默认宽度
                      const newWide = getByteLen(name) > selectedCell.originWide
                        ? getByteLen(name) : selectedCell.originWide;
                      selectedCell.geometry.width = newWide;
                      let fillColor = stateType && statusColor[stateType].colour;
                      if (data.state.label) {
                        fillColor = data.state.label
                          && data.state.label.length
                          && data.state.label[0]
                          && data.state.label[0].props.style.backgroundColor;
                      }
                      const currentStyle = selectedCell.getStyle();
                      selectedCell.setStyle(`${currentStyle}fillColor=${fillColor};`);
                      // 更新全部标签位置
                      if (selectedCell.edges && selectedCell.edges.length) {
                        const edges = selectedCell.edges.filter(edge => edge.status === 'transform_all');
                        edges.forEach((edge) => {
                          edge.source.geometry.x = selectedCell.geometry.x + newWide + 50;
                        });
                      }
                      this.graph.refresh();
                      this.setState({
                        selectedCell: false,
                        show: false,
                        nodeData: nodes,
                        isLoading: false,
                        isEdit: false,
                        statusId: false,
                        stateName: false,
                        stateType: false,
                      });
                      // 将新增加的状态改为另一个，会导致状态消失
                      // 先全局刷新
                      this.refresh();
                    }
                  });
              }
            }
          }
        } else if (type === 'transfer') {
          if (state === 'add') {
            const { nodeData } = this.state;
            const source = _.find(nodeData, item => item.statusId.toString() === data.startNodeId);
            const target = _.find(nodeData, item => item.statusId.toString() === data.endNodeId);
            const param = {
              ...data,
              startNodeId: source.id,
              endNodeId: target.id,
              style: edgeStyle,
            };
            this.addStateMachineTransfer(param);
          } else {
            const { transferData } = this.state;
            // GET INDEX OF SELECTED TRANSFER IN TRANSFER DATA
            const index = _.findIndex(transferData,
              item => item.id.toString() === selectedCell.transferId.toString());
            const param = {
              ...transferData[index],
              name: data.name,
              description: data.description,
              page: null,
              style: edgeStyle,
            };
            this.updateStateMachineTransfer(param)
              .then((item) => {
                if (item && item.failed) {
                  Choerodon.prompt(item.message);
                } else {
                  // 全部转换不展示标签
                  selectedCell.setValue(selectedCell.status === 'transform_all' ? '' : item.name);
                  selectedCell.name = item.name;
                  selectedCell.des = item.description;
                  this.graph.refresh();
                  transferData[index] = item;
                  this.setState({
                    show: false,
                    isLoading: false,
                    transferData,
                    isEdit: false,
                  });
                }
              });
          }
        }
      }
    });
  };

  // CREATE NEW TRANSFER BY LINKING
  handleOnTransfer = (source, target, style) => {
    this.setState({
      source,
      target,
      edgeStyle: style,
    });
    this.showSideBar('transfer', 'add');
  };

  changeStyle = (cell, transferData, node) => {
    let change = true;
    if (cell && transferData.length) {
      transferData.forEach((item) => {
        if (item.endNodeId === cell.nodeId) {
          if (!node || (node.nodeId !== item.startNodeId)) {
            change = false;
          }
        }
      });
    }
    return change;
  };

  checkDelete = (cell) => {
    const { StateMachineStore, intl } = this.props;
    const {
      stateMachineData, organizationId,
    } = this.state;
    const that = this;
    StateMachineStore.checkDeleteNode(
      organizationId, cell.statusId, stateMachineData.id,
    ).then((data) => {
      if (data && !data.failed) {
        if (data.canDelete) {
          confirm({
            title: intl.formatMessage({ id: 'stateMachine.node.deleteTip' }),
            content: (
              <p>
                {intl.formatMessage(
                  {
                    id: 'stateMachine.delete.confirm',
                  }, {
                    des: cell.des,
                  },
                )}
              </p>
            ),
            onOk() {
              that.removeCell(cell);
            },
          });
        } else {
          info({
            title: intl.formatMessage({ id: 'stateMachine.node.deleteInfo' }),
            content: (
              <p>
                {intl.formatMessage(
                  {
                    id: 'stateMachine.node.deleteDes',
                  }, {
                    count: data.count,
                  },
                )}
              </p>
            ),
            onOk() {},
            okText: intl.formatMessage({ id: 'confirm' }),
          });
        }
      }
    });
  };

  removeCell = (cell) => {
    const { StateMachineStore } = this.props;
    const {
      stateMachineData, organizationId, transferData, nodeData,
    } = this.state;
    this.setState({
      loading: true,
    });
    if (cell.vertex) {
      const targetLink = [];
      transferData.forEach((item) => {
        if (item.startNodeId === cell.nodeId) {
          targetLink.push(item);
        }
      });
      const targetData = [];
      if (targetLink.length) {
        nodeData.forEach((item) => {
          targetLink.forEach((link) => {
            if (link.endNodeId === item.id) {
              targetData.push(item);
            }
          });
        });
      }

      StateMachineStore.deleteStateMachineNode(
        organizationId, cell.nodeId, stateMachineData.id,
      ).then((data) => {
        this.setState({
          loading: false,
        });
        if (data && !data.failed) {
          if (cell.allStatusTransformId) {
            const cells = [];
            // 移除全部转换和标签
            if (cell.edges && cell.edges.length) {
              cell.edges.forEach((item) => {
                if (item.status === 'transform_all') {
                  cells.push(item.source);
                }
              });
            }
            // cells.push(this.graph.getCell(`all${cell.allStatusTransformId}`));
            this.graph.removeCells(cells);
            _.remove(transferData, item => item.id === cell.allStatusTransformId);
          }
          if (targetData.length) {
            const targetNode = [];
            targetData.forEach((item) => {
              const node = this.graph.getCell(`n${item.id}`);
              const change = this.changeStyle(node, transferData);
              if (this.changeStyle(node, transferData, cell)) {
                const currentStyle = node.getStyle();
                node.setStyle(`${currentStyle}strokeColor=red`);
                this.graph.refresh();
              }
            });
          }
          this.graph.removeCells();
          this.setState({
            nodeData: data,
            selectedCell: null,
            transferData,
          });
        }
      });
    } else {
      const targetNode = cell.target;
      StateMachineStore
        .deleteStateMachineTransfer(organizationId, cell.transferId, stateMachineData.id)
        .then((data) => {
          this.setState({
            loading: false,
          });
          if (data && !data.failed) {
            if (cell) {
              this.graph.removeCells([cell], true);
            } else {
              this.graph.removeCells();
            }
            if (this.changeStyle(targetNode, transferData, cell.source)) {
              const currentStyle = targetNode.getStyle();
              targetNode.setStyle(`${currentStyle}strokeColor=red;`);
              this.graph.refresh();
            }
            _.remove(transferData,
              item => item.id.toString() === cell.transferId.toString());
            this.handleCancel();
            this.setState({
              selectedCell: null,
              transferData,
            });
          }
        });
    }
  };

  textTransferDel = (id) => {
    const { transferData } = this.state;
    const transfes = _.filter(transferData, item => item.startNodeId === id && item.status !== '2');
    this.setState({
      deleteList: transfes,
      deleteVisible: true,
      deleteId: id,
    });
  };

  textTransferAdd = (id) => {
    const { nodeData } = this.state;
    const source = _.find(nodeData, item => item.id === id);

    if (source) {
      this.setState({
        source: this.graph.getCell(`n${source.id}`),
        target: false,
      }, () => {
        this.showSideBar('transfer', 'add');
      });
    }
  };

  handleCancel = () => {
    this.setState({
      deleteList: null,
      deleteVisible: false,
      deleteId: null,
      deleteDraftVisible: false,
      selectedDeleteId: false,
      isSelect: true,
    });
  };

  handleDeleteTransfer = () => {
    const { selectedDeleteId } = this.state;
    if (selectedDeleteId) {
      const cell = this.graph.getCell(`t${selectedDeleteId}`);
      this.setState({
        selectedCell: cell,
      }, () => {
        this.removeCell(cell);
      });
    } else {
      this.setState({
        isSelect: false,
      });
    }
  };

  toolbarAdd = (type) => {
    this.setState({
      selectedCell: null,
      source: false,
      target: false,
    }, () => {
      this.showSideBar(type, 'add');
    });
  };

  handleCardEdit = (cell) => {
    if (cell) {
      const type = cell.edge ? 'transfer' : 'state';
      this.handleDbClick(cell, type);
    }
  };

  showSideBar = (type, state) => {
    this.setState({
      show: true,
      type,
      state,
    });
    if (type === 'state') {
      this.loadStateList();
    }
  };

  hideSidebar = () => {
    this.setState({
      show: false,
      statusId: false,
      stateName: false,
      stateType: false,
      isEdit: false,
      isLoading: false,
    });
  };

  hideCreateSidebar = () => {
    this.setState({
      createShow: false,
      show: true,
    });
  };

  handleConfig = (cell) => {
    const { intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { stateMachineData } = this.state;
    const configId = cell.transferId;
    history.push(`/agile/state-machines/${stateMachineData.id}/editconfig/${configId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  handleDeploy = () => {
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { id: stateMachineId } = this.state;
    history.push(`/agile/state-machines/edit/${stateMachineId}/state_machine_draft?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  showDeleteDraft = () => {
    this.setState({
      deleteDraftVisible: true,
    });
  };

  handleDeleteDraft = () => {
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { id: stateMachineId } = this.state;
    this.setState({
      deleteLoading: true,
    });
    StateMachineStore.deleteDraft(organizationId, stateMachineId)
      .then((data) => {
        this.setState({
          deleteLoading: true,
        });
        if (data) {
          history.push(`/agile/state-machines/edit/${stateMachineId}/state_machine_active?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
        }
      }).catch(() => {
        this.setState({
          deleteLoading: true,
        });
      });
  };

  handlePublish = () => {
    const { stateMachineData } = this.state;
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    this.setState({
      publishLoading: true,
    });
    StateMachineStore.publishStateMachine(organizationId, stateMachineData.id).then((data) => {
      this.setState({
        publishLoading: false,
      });
      if (data) {
        Choerodon.prompt(intl.formatMessage({ id: 'stateMachine.publish.success' }), 'success');
        history.push(`/agile/state-machines/edit/${stateMachineData.id}/state_machine_active?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
      } else {
        info({
          title: intl.formatMessage({ id: 'stateMachine.publish.info' }),
          content: (
            <p>
              {intl.formatMessage({ id: 'stateMachine.publish.des' })}
            </p>
          ),
          onOk() {},
          okText: intl.formatMessage({ id: 'confirm' }),
        });
      }
    });
  };

  handleAllChange = (e) => {
    const { StateMachineStore } = this.props;
    const {
      organizationId, selectedCell, stateMachineData, transferData,
    } = this.state;
    this.setState({
      allChecked: e.target.checked,
    });
    if (e.target.checked) {
      StateMachineStore.linkAllToNode(organizationId, selectedCell.nodeId, stateMachineData.id)
        .then((item) => {
          transferData.push(item);
          selectedCell.allStatusTransformId = item.id;
          this.graph.createTransition(item, item.startNodeId, item.endNodeId);
          this.setState({
            transferData,
          });
        });
    } else {
      StateMachineStore.deleteAllToNode(organizationId, selectedCell.allStatusTransformId)
        .then(() => {
          const cells = [];
          cells.push(this.graph.getCell(`all${selectedCell.nodeId}`));
          cells.push(this.graph.getCell(`t${selectedCell.allStatusTransformId}`));
          _.remove(transferData, item => item.id === selectedCell.allStatusTransformId);
          selectedCell.allStatusTransformId = null;
          this.setState({
            transferData,
          });
          this.graph.removeCells(cells);
          if (this.changeStyle(selectedCell, transferData, selectedCell.source)) {
            const currentStyle = selectedCell.getStyle();
            selectedCell.setStyle(`${currentStyle}strokeColor=red;`);
            this.graph.refresh();
          }
        });
    }
  };

  handlecreateSubmit = () => {
    const { organizationId } = this.state;
    const { form } = this.props;
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        const postData = data;
        postData.organizationId = organizationId;
        this.setState({
          submitting: true,
        });
        StateStore.createState(organizationId, postData)
          .then((res) => {
            if (res && res.failed) {
              Choerodon.prompt(res.message);
            } else {
              this.loadStateList();
              this.setState({
                createShow: false,
                show: true,
                statusId: res.id,
                stateName: res.name,
                stateType: res.type,
              });
            }
            this.setState({
              submitting: false,
            });
          }).catch((error) => {
            Choerodon.prompt(error.response.data.message);
            this.setState({
              submitting: false,
            });
          });
      }
    });
  };

  changeRae = (currentRae) => {
    this.setState({
      currentRae,
    });
  };

  updateScheme = (code) => {
    const { StateMachineStore } = this.props;
    const {
      objectVersionNumber,
      [code]: newValue,
      id,
      error,
    } = this.state;
    if (!error) {
      if (code === 'name' && !newValue) {
        const {
          name, description,
        } = StateMachineStore.getStateMachine;
        this.setState({
          currentRae: undefined,
          name,
          description,
        });
      } else {
        const menu = AppState.currentMenuType;
        const {
          organizationId: orgId,
        } = menu;
        const data = {
          [code]: this.state[code],
          objectVersionNumber,
        };
        StateMachineStore.updateStateMachine(orgId, id, data).then(() => {
          this.refresh();
          this.setState({
            currentRae: undefined,
          });
        });
      }
    }
  };

  resetScheme = (origin, code) => {
    this.setState({
      [code]: origin,
      error: false,
    });
  };

  handleChange = async (e, code) => {
    const { StateMachineStore, intl } = this.props;
    const menu = AppState.currentMenuType;
    const {
      organizationId: orgId,
    } = menu;
    const { name } = StateMachineStore.getStateMachine;
    const newName = e.target.value;
    let error = '';
    this.setState({
      [code]: newName,
    });
    if (name !== newName && code === 'name') {
      const res = await StateMachineStore.checkName(orgId, newName);
      if (res) {
        error = intl.formatMessage({ id: 'priority.create.name.error' });
      }
    }
    this.setState({
      error,
    });
  };

  changeDisplayHeader = () => {
    this.setState({
      displayHeader: !this.state.displayHeader,
    });
  };

  render() {
    const { intl } = this.props;
    const {
      stateMachineData,
      selectedCell,
      nodeData,
      loading,
      transferData,
      status,
      publishLoading,
      currentRae,
      name: schemeName,
      description,
      deleteLoading,
      error,
      displayHeader,
      isSelect,
    } = this.state;
    const dataSource = nodeData && nodeData.slice();
    _.remove(dataSource, item => item.statusId === 0);
    const menu = AppState.currentMenuType;
    const {
      type, id: projectId, organizationId: orgId, name,
    } = menu;
    const graphHeader = (
      <React.Fragment>
        <Button onClick={() => this.toolbarAdd('state')} className="graph-toolbar-button" icon="add"><FormattedMessage id="stateMachine.state.add" /></Button>
        <Button onClick={() => this.toolbarAdd('transfer')} className="graph-toolbar-button" icon="add"><FormattedMessage id="stateMachine.transfer.add" /></Button>
        <Checkbox defaultChecked onClick={this.handleCheckChange} className="graph-toolbar-checkbox"><FormattedMessage id="stateMachine.transfer.display" /></Checkbox>
      </React.Fragment>
    );
    const graphExtra = (
      <div className="graph-card">
        <div className="graph-card-title">{selectedCell && selectedCell.value}
        </div>
        <div className="graph-card-des"><FormattedMessage id="stateMachine.des" />: {selectedCell && selectedCell.des}</div>
        {selectedCell && selectedCell.edge ? (
          <React.Fragment>
            <div className="graph-card-toolbar">
              <Button
                className="graph-card-btn"
                funcType="raised"
                onClick={() => this.handleCardEdit(this.state.selectedCell)}
              ><FormattedMessage id="edit" /></Button>
              <Button
                disabled
                className="graph-card-btn"
                funcType="raised"
                type="primary"
                onClick={() => this.handleConfig(this.state.selectedCell)}
              ><FormattedMessage id="stateMachine.config" /></Button>
              <Popconfirm title={<FormattedMessage id="stateMachine.transfer.deleteConfirm" />} onConfirm={() => this.removeCell(this.state.selectedCell)}>
                <Button
                  disabled={selectedCell && selectedCell.status !== 'transform_custom'}
                  className="graph-card-btn"
                  funcType="raised"
                >
                  <FormattedMessage id={`stateMachine.${selectedCell && selectedCell.vertex ? 'state' : 'transfer'}.delete`} />
                </Button>
              </Popconfirm>
            </div>
          </React.Fragment>
        )
          : (
            <React.Fragment>
              <div className="graph-card-all">
                <Checkbox checked={this.state.allChecked} onChange={this.handleAllChange}><FormattedMessage id="stateMachine.node.all" /></Checkbox>
              </div>
              <Button
                disabled={selectedCell && selectedCell.status !== 'node_custom'}
                className="graph-card-btn"
                funcType="raised"
                onClick={() => this.checkDelete(this.state.selectedCell)}
              >
                <FormattedMessage id={`stateMachine.${selectedCell && selectedCell.vertex ? 'state' : 'transfer'}.delete`} />
              </Button>
            </React.Fragment>
          )}
      </div>);

    const operations = <Button
      className={`${prefixCls}-button`}
      icon={displayHeader ? 'vertical_align_top' : 'vertical_align_bottom'}
      onClick={this.changeDisplayHeader}
    />;

    // 状态机流程图高度
    let graphHigh = 'calc(100vh - 395px)';
    if (!displayHeader) {
      graphHigh = 'calc(100vh - 255px)';
    } else if (status === 'state_machine_create') {
      graphHigh = 'calc(100vh - 330px)';
    } else if (status === 'state_machine_active') {
      graphHigh = 'calc(100vh - 370px)';
    }

    return (
      <Page>
        <Header
          title={<FormattedMessage id={status === 'state_machine_active' ? 'stateMachine.edit.avtive' : 'stateMachine.edit'} />}
          backPath={`/agile/${status === 'state_machine_draft' ? `state-machines/edit/${stateMachineData.id}/state_machine_active` : 'state-machines'}?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}`}
        >
          <Button
            onClick={this.refresh}
            funcType="flat"
          >
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Content>
          <div className={`${prefixCls}-header`} style={{ display: displayHeader ? 'block' : 'none' }}>
            {status && status === 'state_machine_active' && (
              <div className={`${prefixCls}-header-tip`}>
                <span className="icon icon-warning" />
                <div className={`${prefixCls}-header-tip-text`}>
                  <FormattedMessage id="stateMachine.edit.deploy.tip" />
                </div>
                <div className={`${prefixCls}-header-tip-action`}>
                  <Button onClick={() => this.handleDeploy()} type="primary" funcType="raised"><FormattedMessage id="edit" /></Button>
                </div>
              </div>
            )}
            {status && status === 'state_machine_draft' && (
              <div className={`${prefixCls}-header-tip`}>
                <span className="icon icon-warning" />
                <div className={`${prefixCls}-header-tip-text`}>
                  <FormattedMessage id="stateMachine.edit.draft.tip" />
                </div>
                <div className={`${prefixCls}-header-tip-action`}>
                  <Button
                    onClick={this.handlePublish}
                    type="primary"
                    funcType="raised"
                    loading={publishLoading}
                  >
                    <FormattedMessage id="stateMachine.publish" />
                  </Button>
                  {
                    status === 'state_machine_draft' && <Button onClick={this.showDeleteDraft} funcType="raised" className="delete">
                      <FormattedMessage id="stateMachine.draft.delete" />
                    </Button>
                  }
                </div>
              </div>
            )}
            <div style={{ width: 440 }}>
              <ReadAndEdit
                callback={this.changeRae}
                thisType="name"
                current={currentRae}
                origin={schemeName}
                onOk={() => this.updateScheme('name')}
                onCancel={origin => this.resetScheme(origin, 'name')}
                readModeContent={(
                  <div className={`${prefixCls}-header-name`}>
                    {schemeName}
                  </div>
                )}
                style={{ marginBottom: 10 }}
                error={error}
              >
                <Input
                  size="small"
                  maxLength={20}
                  value={schemeName}
                  onChange={e => this.handleChange(e, 'name')}
                  onPressEnter={() => this.updateScheme('name')}
                  autoize={{ minRows: 1, maxRows: 1 }}
                />
              </ReadAndEdit>
            </div>
          </div>
          <Tabs defaultActiveKey="graph" tabBarExtraContent={operations}>
            <TabPane tab={<FormattedMessage id="stateMachine.tab.graph" />} key="graph">
              {nodeData && nodeData.length && (
                <Spin spinning={loading}>
                  <Graph
                    renderChanged={this.update}
                    setRef={this.setGraphRef}
                    onLink={this.handleOnTransfer}
                    header={status !== 'state_machine_active' && graphHeader}
                    cellDblClick={this.handleDbClick}
                    extra={selectedCell && selectedCell.status !== 'node_start' && selectedCell.status !== 'node_all' && status !== 'state_machine_active' && graphExtra}
                    cellClick={this.handleCellClick}
                    data={nodeData && { vertex: nodeData, edge: transferData }}
                    onMove={this.handleOnMove}
                    onReLink={this.handleReLink}
                    enable={this.state.enable}
                    height={graphHigh}
                  />
                </Spin>
              )}
            </TabPane>
            <TabPane tab={<FormattedMessage id="stateMachine.tab.text" />} key="text">
              {status !== 'state_machine_active' && (
                <div className={`${prefixCls}-tab-action-btn`}>
                  <Button onClick={() => this.toolbarAdd('state')} funcType="flat" icon="add">
                    <FormattedMessage id="stateMachine.state.add" />
                  </Button>
                </div>)
              }
              <Table
                dataSource={dataSource}
                columns={this.getColumn()}
                filterBarPlaceholder={intl.formatMessage({ id: 'filter' })}
                rowKey={record => record.id}
                loading={StateStore.getIsLoading}
                onChange={this.tableChange}
                filterBar={false}
                className="issue-table"
              />
            </TabPane>
          </Tabs>
        </Content>
        {this.state.show && <Sidebar
          title={<FormattedMessage id={this.state.type === 'state' ? `stateMachine.state.${this.state.isEdit ? 'edit' : 'add'}` : `stateMachine.transfer.${this.state.isEdit ? 'edit' : 'add'}`} />}
          visible={this.state.show}
          onOk={this.handleSubmit}
          okText={<FormattedMessage id={this.state.isEdit ? 'save' : 'add'} />}
          cancelText={<FormattedMessage id="cancel" />}
          confirmLoading={this.state.isLoading}
          onCancel={this.hideSidebar}
        >
          {this.getFormContent()}
        </Sidebar>}
        {this.state.createShow && <Sidebar
          title={<FormattedMessage id="state.create" />}
          visible={this.state.createShow}
          onOk={this.handlecreateSubmit}
          okText={<FormattedMessage id={this.state.type === 'create' ? 'create' : 'save'} />}
          cancelText={<FormattedMessage id="cancel" />}
          confirmLoading={this.state.submitting}
          onCancel={this.hideCreateSidebar}
        >
          {this.getCreateForm()}
        </Sidebar>}
        {this.state.deleteVisible && (
          <Modal
            title={<FormattedMessage id="stateMachine.transfer.delete" />}
            visible={this.state.deleteVisible}
            onOk={this.handleDeleteTransfer}
            onCancel={this.handleCancel}
          >
            <div><FormattedMessage id="stateMachine.transfer.deleteTip" /></div>
            <Select
              label={<FormattedMessage id="stateMachine.transfer.delete.tip" />}
              style={{ width: '100%' }}
              dropdownMatchSelectWidth
              size="default"
              onChange={value => this.setState({ selectedDeleteId: value, isSelect: true })}
            >
              {this.state.deleteList
              && this.state.deleteList.length
              && this.state.deleteList
                .map(item => <Option key={item.id} value={item.id}>{item.name}</Option>)}
            </Select>
            <div
              style={{
                display: isSelect ? 'none' : 'block',
                color: '#d50000',
                fontSize: 13,
              }}
            >
              <FormattedMessage id="stateMachine.transfer.deleteTip" />
            </div>
          </Modal>
        )}
        {this.state.deleteDraftVisible && (
          <Modal
            title={<FormattedMessage id="stateMachine.draft.delete" />}
            visible={this.state.deleteDraftVisible}
            onOk={this.handleDeleteDraft}
            onCancel={this.handleCancel}
            confirmLoading={deleteLoading}
          >
            <p className={`${prefixCls}-del-content`}>
              <FormattedMessage id="stateMachine.draft.delete" />
              <span>:</span>
              <span className={`${prefixCls}-del-content-name`}>{stateMachineData.name}</span>
            </p>
            <p className={`${prefixCls}-del-tip`}>
              <FormattedMessage id="state.delete.tip" />
            </p>
          </Modal>
        )}
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(EditStateMachine)));
