import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Table, Button, Modal, Form, Select, Input, Tooltip, Tabs, Radio, Card, Popconfirm, Spin } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import { Content, Header, Page, Permission, stores } from '@choerodon/boot';
import _ from 'lodash';
import './EditConfig.scss';

const prefixCls = 'issue-state-machine-config';
const { AppState } = stores;

const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const { TabPane } = Tabs;
const RadioGroup = Radio.Group;

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

@observer
class EditConfig extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);
    const { id, machineId, stateId } = this.props.match.params;

    this.state = {
      page: 0,
      pageSize: 10,
      id,
      machineId,
      stateId,
      organizationId: menu.organizationId,
      show: false,
      submitting: false,
      stateMachineData: {},
      stateList: [],
      source: false,
      targetData: {},
      enable: true,
    };
    this.graph = null;
    this.canvas = null;
  }

  componentDidMount() {
    this.loadTarget();
    this.loadStateMachine();
  }

  getColumn = () => {
    const { transferData, nodeData } = this.state;
    return (
      [{
        title: <FormattedMessage id="stateMachine.state" />,
        dataIndex: 'statusVO',
        key: 'statusVO',
        width: 300,
        render: text => text && (
          <div className={`${prefixCls}-text-node`}>{text.name}</div>
        ),
      }, {
        title: <FormattedMessage id="stateMachine.transfer" />,
        dataIndex: 'id',
        key: 'id',
        render: (id) => {
          const { nodeTransfer = {} } = this.state;
          return (
            <React.Fragment>
              {transferData && transferData.map(item => item.startNodeId === id && (
                <div className={`${prefixCls}-text-transfer-item`} key={item.id}>
                  {`${item.name}  >>>`} {
                    nodeData && nodeData.map(node => node.id === item.endNodeId && (
                      <div className={`${prefixCls}-text-node`} key={`${item.id}-${node.id}`}>
                        {node.statusVO && node.statusVO.name}
                      </div>
                    ))
                  }
                </div>
              ))}
            </React.Fragment>
          );
        },
      }, {
        align: 'right',
        width: 104,
        key: 'action',
        render: (test, record) => (
          <div>
            <Tooltip placement="top" title={<FormattedMessage id="stateMachine.transfer.add" />}>
              <Button shape="circle" size={'small'} onClick={this.textTransferAdd.bind(this, record.id)}>
                <span className="icon icon-add" />
              </Button>
            </Tooltip>
            <Tooltip placement="top" title={<FormattedMessage id="delete" />}>
              <Button shape="circle" size={'small'} onClick={this.textTransferDel.bind(this, record.id)}>
                <span className="icon icon-delete" />
              </Button>
            </Tooltip>
            <Permission service={[]} >
              <Tooltip placement="bottom" title={<div>{!record.synchro ? <FormattedMessage id="app.synch" /> : <React.Fragment>{record.active ? <FormattedMessage id="edit" /> : <FormattedMessage id="app.start" />}</React.Fragment>}</div>}>
                <span />
              </Tooltip>
            </Permission>
          </div>
        ),
      }]
    );
  }

  getTransferById = (onLoad = false) => {
    const { organizationId, id } = this.state;
    const { StateMachineStore } = this.props;
    if (!onLoad) {
      this.setState({
        loading: true,
      });
    }
    StateMachineStore.getTransferById(organizationId, id).then((data) => {
      this.setState({
        loading: false,
        isLoading: false,
      });
      if (data) {
        this.setState({
          targetData: data,
        });
        this.renderTransfer(data);
      }
    });
  };

  renderTransfer = (data) => {
    const figure = [];
    const { canvas } = this;
    const { width, height } = canvas.getBoundingClientRect();
    const { startNodeVO, name, endNodeVO } = data;
    const { devicePixelRatio } = window;
    canvas.setAttribute('width', width * devicePixelRatio);
    canvas.setAttribute('height', height * devicePixelRatio);
    const recWidth = 140;
    const recHeight = 70;
    let lineWidth = 400;
    let recX = 50;
    const recY = 70;
    const range = {
      x: recX,
      y: recY,
      h: recHeight,
    };
    if (width > 1000) {
      recX = (width * devicePixelRatio - recWidth * 4 - lineWidth * 2) / 2;
    } else {
      lineWidth = 200;
      recX = (width * devicePixelRatio - recWidth * 4 - lineWidth * 2) / 2;
    }

    const ctx = canvas.getContext('2d');
    if (startNodeVO) {
      ctx.beginPath();
      ctx.fillStyle = '#FFB100';
      ctx.fillRect(recX, recY, recWidth, recHeight);
      ctx.fillStyle = '#ffffff';
      ctx.font = '25px Georgia';
      ctx.textAlign = 'center';
      ctx.fillText(startNodeVO.statusVO && startNodeVO.statusVO.name ? startNodeVO.statusVO.name : '', recX + recWidth / 2, recY + recHeight / 2 + 5);
      ctx.stroke();
      figure.push({
        x: recX,
        y: recY,
        w: recWidth,
        h: recHeight,
        id: startNodeVO.id,
      });
      range.x = recX;
      range.y = recY;
      range.h = recHeight;
    }

    const newX = startNodeVO ? recWidth + lineWidth : 0;
    ctx.beginPath();
    ctx.fillStyle = '#D8D8D8';
    ctx.fillRect(recX + newX, recY + recHeight / 4, recWidth * 2, recHeight / 2);
    ctx.fillStyle = '#000000a6';
    ctx.font = '25px Georgia';
    ctx.textAlign = 'center';
    ctx.fillText(data.name || 'sss', recX + newX + recWidth, recY + recHeight / 2 + 8);
    ctx.stroke();

    if (startNodeVO) {
      ctx.beginPath();
      ctx.strokeStyle = '#ccc';
      ctx.moveTo(recX + recWidth, recY + recHeight - recHeight / 2);
      ctx.lineTo(recX + recWidth + lineWidth, recY + recHeight - recHeight / 2);

      ctx.moveTo(recX + recWidth + lineWidth - 10, recY + recHeight - recHeight / 2 - 5);
      ctx.lineTo(recX + recWidth + lineWidth, recY + recHeight - recHeight / 2);

      ctx.moveTo(recX + recWidth + lineWidth - 10, recY + recHeight - recHeight / 2 + 5);
      ctx.lineTo(recX + recWidth + lineWidth, recY + recHeight - recHeight / 2);
      ctx.stroke();
    }

    if (endNodeVO) {
      ctx.beginPath();
      ctx.fillStyle = '#4D90FE';
      ctx.fillRect(recX + newX + recWidth * 2 + lineWidth, recY, recWidth, recHeight);
      ctx.fillStyle = '#ffffff';
      ctx.font = '25px Georgia';
      ctx.textAlign = 'center';
      ctx.fillText(endNodeVO.statusVO && endNodeVO.statusVO.name ? endNodeVO.statusVO.name : '', recX + newX + recWidth * 2 + lineWidth + recWidth / 2, recY + recHeight / 2 + 5);
      ctx.stroke();
      figure.push({
        x: recX + newX + recWidth * 2 + lineWidth,
        y: recY,
        w: recWidth,
        h: recHeight,
        id: endNodeVO.id,
      });
      range.w = recX + newX + recWidth * 2 + lineWidth + recWidth;

      ctx.beginPath();
      ctx.strokeStyle = '#ccc';
      ctx.moveTo(recX + newX + recWidth * 2, recY + recHeight - recHeight / 2);
      ctx.lineTo(recX + newX + recWidth * 2 + lineWidth, recY + recHeight - recHeight / 2);

      ctx.moveTo(recX + newX + recWidth * 2 + lineWidth - 10, recY + recHeight - recHeight / 2 - 5);
      ctx.lineTo(recX + newX + recWidth * 2 + lineWidth, recY + recHeight - recHeight / 2);

      ctx.moveTo(recX + newX + recWidth * 2 + lineWidth - 10, recY + recHeight - recHeight / 2 + 5);
      ctx.lineTo(recX + newX + recWidth * 2 + lineWidth, recY + recHeight - recHeight / 2);
      ctx.stroke();
    }
    this.setState({
      canvasStatus: {
        figure,
        range,
      },
    });
  };

  getStateById = (onLoad = false) => {
    const { organizationId, stateId } = this.state;
    const { StateMachineStore } = this.props;
    if (!onLoad) {
      this.setState({
        loading: true,
      });
    }
    StateMachineStore.getStateById(organizationId, stateId).then((data) => {
      this.setState({
        loading: false,
        isLoading: false,
      });
      if (data) {
        this.setState({
          targetData: data,
        });
        this.renderState(data);
      }
    });
  };

  renderState = (data) => {
    const figure = [];
    const { canvas } = this;
    const { width, height } = canvas.getBoundingClientRect();
    const { intoTransform, statusVO, outTransform } = data;
    const { devicePixelRatio } = window;
    // 计算出物理像素
    canvas.setAttribute('width', width * devicePixelRatio);

    const recWidth = 140;
    const recHeight = 70;
    let lineWidth = 400;
    let recX = 50;
    const recY = 70;
    if (width > 1000) {
      // 计算起始位置，
      recX = (width * devicePixelRatio - recWidth * 4 - lineWidth * 2) / 2;
    } else {
      lineWidth = 200;
      recX = (width * devicePixelRatio - recWidth * 4 - lineWidth * 2) / 2;
    }
    const range = {
      x: recX,
      y: recY,
      w: 0,
      h: 0,
    };
    const ctx = canvas.getContext('2d');
    const intoFigure = [];
    let y = recY + recHeight / 4;
    if (intoTransform.length !== 1) {
      y = recY;
    }
    let canvasH = intoTransform && intoTransform.length ? intoTransform.length : 0;
    if (outTransform && outTransform.length > canvasH) {
      canvasH = outTransform.length;
    }
    canvasH = (canvasH * (recHeight / 2 + 10) + recY * 2) / devicePixelRatio;
    if (canvasH > 120) {
      canvas.style.height = `${canvasH}px`;
      canvas.setAttribute('height', canvasH * devicePixelRatio);
    } else {
      canvas.setAttribute('height', height * devicePixelRatio);
    }
    if (intoTransform && intoTransform.length) {
      intoTransform.forEach((item, index) => {
        ctx.beginPath();
        ctx.fillStyle = '#D8D8D8';
        ctx.fillRect(recX, y + index * (recHeight / 2 + 20), recWidth * 2, recHeight / 2);
        ctx.fillStyle = '#000000a6';
        ctx.font = '25px Georgia';
        ctx.textAlign = 'center';
        ctx.fillText(item.name || '', recX + recWidth, y + index * (recHeight / 2 + 20) + recHeight / 4 + 8);
        ctx.stroke();
        intoFigure.push({
          x: recX,
          y: y + index * (recHeight / 2 + 20),
          w: recWidth * 2,
          h: recHeight / 2,
          id: item.id,
        });
      });
      range.x = recX;
      range.y = y;
      range.h = intoFigure[intoFigure.length - 1].y + recHeight / 2;
      range.w = recWidth * 2;
    }

    let stateY = recY;
    if (intoFigure.length && intoFigure.length !== 1) {
      stateY = recY + (intoFigure[intoFigure.length - 1].y - intoFigure[0].y - recHeight / 2) / 2;
    }
    const newX = intoTransform && intoTransform.length ? recX + range.w + lineWidth : recX;
    // 状态节点渲染
    ctx.beginPath();
    ctx.fillStyle = '#FFB100';
    ctx.fillRect(newX, stateY, recWidth, recHeight);
    ctx.fillStyle = '#ffffff';
    ctx.font = '25px Georgia';
    ctx.textAlign = 'center';
    ctx.fillText(data.statusVO && data.statusVO.name ? data.statusVO.name : 'sss', newX + recWidth / 2, stateY + recHeight / 2 + 5);
    ctx.stroke();


    if (intoTransform && intoTransform.length) {
      ctx.beginPath();
      ctx.strokeStyle = '#ccc';
      let equal = false;
      intoFigure.forEach((item) => {
        const x = item.x + item.w;
        const y = item.y + item.h / 2;

        ctx.moveTo(x, y);
        if (y === stateY + recHeight / 2) {
          equal = true;
          ctx.lineTo(recX + recWidth * 2 + lineWidth, y);

          ctx.moveTo(recX + recWidth * 2 + lineWidth - 10, y - 5);
          ctx.lineTo(recX + recWidth * 2 + lineWidth, y);
          ctx.moveTo(recX + recWidth * 2 + lineWidth - 10, y + 5);
          ctx.lineTo(recX + recWidth * 2 + lineWidth, y);
        } else {
          ctx.lineTo(x + lineWidth / 2, y);

          // 垂直连接线
          ctx.moveTo(x + lineWidth / 2, y);
          ctx.lineTo(x + lineWidth / 2, stateY + recHeight / 2);
        }
      });
      if (!equal) {
        ctx.moveTo(recX + range.w + lineWidth - lineWidth / 2, stateY + recHeight / 2);
        ctx.lineTo(recX + range.w + lineWidth, stateY + recHeight / 2);

        // 箭头
        ctx.moveTo(recX + range.w + lineWidth - 10, stateY + recHeight / 2 - 5);
        ctx.lineTo(recX + range.w + lineWidth, stateY + recHeight / 2);
        ctx.moveTo(recX + range.w + lineWidth - 10, stateY + recHeight / 2 + 5);
        ctx.lineTo(recX + range.w + lineWidth, stateY + recHeight / 2);
      }
      ctx.stroke();
    }


    range.w = intoTransform && intoTransform.length ? range.w + lineWidth + recWidth : recWidth;

    const outFigure = [];
    if (outTransform && outTransform.length) {
      const x = recX + range.w + lineWidth;
      y = stateY + recHeight / 4;
      if (outTransform.length !== 1) {
        y = recY;
      }

      outTransform.forEach((item, index) => {
        ctx.beginPath();
        ctx.fillStyle = '#D8D8D8';
        ctx.fillRect(x, y + index * (recHeight / 2 + 20), recWidth * 2, recHeight / 2);
        ctx.fillStyle = '#000000a6';
        ctx.font = '25px Georgia';
        ctx.textAlign = 'center';
        ctx.fillText(item.name || 'dddd', x + recWidth, y + index * (recHeight / 2 + 20) + recHeight / 4 + 8);
        ctx.stroke();
        outFigure.push({
          x,
          y: y + index * (recHeight / 2 + 20),
          w: recWidth * 2,
          h: recHeight / 2,
          id: item.id,
        });
      });

      ctx.beginPath();
      ctx.strokeStyle = '#ccc';
      const lineY = stateY + recHeight / 2;
      ctx.moveTo(recX + range.w, lineY);
      ctx.lineTo(recX + range.w + lineWidth / 2, lineY);

      // equal = false;
      outFigure.forEach((item) => {
        if (item.y + item.h / 2 === lineY) {
          ctx.moveTo(recX + range.w + lineWidth / 2, lineY);
          ctx.lineTo(item.x, lineY);

          ctx.moveTo(item.x - 10, lineY - 5);
          ctx.lineTo(item.x, lineY);
          ctx.moveTo(item.x - 10, lineY + 5);
          ctx.lineTo(item.x, lineY);
        } else {
          // 垂直连接线
          ctx.moveTo(recX + range.w + lineWidth / 2, lineY);
          ctx.lineTo(recX + range.w + lineWidth / 2, item.y + item.h / 2);

          ctx.moveTo(recX + range.w + lineWidth / 2, item.y + item.h / 2);
          ctx.lineTo(item.x, item.y + item.h / 2);

          // 箭头
          ctx.moveTo(item.x - 10, item.y + item.h / 2 - 5);
          ctx.lineTo(item.x, item.y + item.h / 2);
          ctx.moveTo(item.x - 10, item.y + item.h / 2 + 5);
          ctx.lineTo(item.x, item.y + item.h / 2);
        }
      });

      ctx.stroke();
      range.w = range.w + lineWidth + recWidth * 2;
      if (range.h < outFigure[outFigure.length - 1].y + recHeight / 2) {
        range.h = outFigure[outFigure.length - 1].y + recHeight / 2;
      }
    }
    figure.push(...intoFigure);
    figure.push(...outFigure);
    this.setState({
      canvasStatus: {
        figure,
        range,
      },
    });
  };

  loadStateMachine = () => {
    const { organizationId, id, machineId } = this.state;
    const { StateMachineStore } = this.props;

    StateMachineStore.loadStateMachineDraftById(organizationId, machineId).then((data) => {
      this.setState({
        loading: false,
      });
      if (data) {
        this.setState({
          stateMachineData: data,
        });
      }
    });
  };

  loadTarget = (onLoad = false) => {
    const { stateId } = this.state;
    if (!onLoad) {
      this.setState({
        isLoading: true,
      });
    }
    if (stateId) {
      this.getStateById(onLoad);
    } else {
      this.getTransferById(onLoad);
    }
  };

  refresh = () => {
    this.loadTarget();
  };

  getCanvas = (node) => {
    this.canvas = node;
  };

  // DISPLAY TRANSFER NAME or NO
  handleCheckChange = (e) => {
    this.graph.handleCheckChange(e);
  };


  handleSubmit = () => {
    const { StateMachineStore } = this.props;
    const {
      state,
      type,
      editState,
      page,
      pageSize,
      sorter,
      tableParam,
      organizationId,
      selectedCell,
    } = this.state;

    this.props.form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        if (type === 'state') {
          const { name } = data.state.label.props;
          if (state === 'add') {
            this.addStateMachineNode(data);
          } else {
            const { nodeData } = this.state;
            if (nodeData && nodeData.length) {
              // GET NODE DATA
              const node = _.find(
                nodeData, item => item.id.toString() === selectedCell.id.toString(),
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
                      selectedCell.setValue(name);
                      selectedCell.stateId = data.state.key;
                      this.graph.refresh();
                      this.setState({
                        selectedCell,
                        show: false,
                        nodeData: nodes,
                        isLoading: false,
                      });
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
            };
            this.updateStateMachineTransfer(param)
              .then((item) => {
                if (item && item.failed) {
                  Choerodon.prompt(item.message);
                } else {
                  selectedCell.setValue(item.name);
                  selectedCell.des = item.description;
                  this.graph.refresh();
                  transferData[index] = item;
                  this.setState({
                    show: false,
                    isLoading: false,
                    transferData,
                  });
                }
              });
          }
        }
      }
    });
  };

  handleCancel = () => {
    this.setState({
      deleteList: null,
      deleteVisible: false,
      deleteId: null,
    });
  };

  handleEdit = (state) => {
    const { StateMachineStore, intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { machineId, id: configId } = this.state;
    history.push(`/agile/state-machines/${machineId}/editconfig/select/${state}/${configId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  isPointIn = (x, y, range) => {
    const { devicePixelRatio } = window;
    if (range && x * devicePixelRatio >= range.x && x * devicePixelRatio <= range.x + range.w
      && y * devicePixelRatio >= range.y && y * devicePixelRatio <= range.y + range.h) {
      return true;
    }
    return false;
  };

  handleStateClick = (stateId) => {
    const { intl, history } = this.props;
    const { name, id, organizationId } = AppState.currentMenuType;
    const { machineId, id: transferId, stateId: sId } = this.state;
    if (sId) {
      history.push(`/agile/state-machines/${machineId}/editconfig/${stateId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
    } else {
      history.push(`/agile/state-machines/${machineId}/editconfig/${transferId}/state/${stateId}?type=organization&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
    }
  };

  onDelete = (id) => {
    const { StateMachineStore } = this.props;
    const { organizationId } = this.state;
    this.setState({
      loading: true,
    });
    StateMachineStore.deleteConfig(organizationId, id).then((data) => {
      if (data) {
        this.loadTarget(true);
      } else {
        this.setState({
          loading: false,
        });
      }
    });
  };

  handleMousOover = (e) => {
    const { canvas } = this;
    const { canvasStatus } = this.state;
    const { figure, range } = canvasStatus;
    const { x, y } = canvas.getBoundingClientRect();
    const { clientX, clientY } = e;
    const canvasX = clientX - x;
    const canvasY = clientY - y;
    let isPointIn = false;
    const ctx = canvas.getContext('2d');
    if (this.isPointIn(canvasX, canvasY, range)) {
      if (figure) {
        figure.forEach((item) => {
          if (this.isPointIn(canvasX, canvasY, item)) {
            isPointIn = true;
          }
        });
      }
    }
    if (isPointIn) {
      canvas.style.cursor = 'pointer';
    } else {
      canvas.style.cursor = 'auto';
    }
  };

  handleCanvasClick = (e) => {
    const { canvas } = this;
    const { canvasStatus } = this.state;
    const { figure, range } = canvasStatus;
    const { x, y } = canvas.getBoundingClientRect();
    const { clientX, clientY } = e;
    const canvasX = clientX - x;
    const canvasY = clientY - y;
    const ctx = canvas.getContext('2d');
    if (this.isPointIn(canvasX, canvasY, range)) {
      if (figure) {
        figure.forEach((item) => {
          if (this.isPointIn(canvasX, canvasY, item)) {
            this.handleStateClick(item.id);
          }
        });
      }
    }
  };

  changeTab = (key) => {
    const { StateMachineStore } = this.props;
    StateMachineStore.setConfigType(key);
  };

  handleConditoinChange = (e) => {
    const { StateMachineStore } = this.props;
    const { organizationId, id } = this.state;
    StateMachineStore.updateCondition(organizationId, id, e.target.value);
  };

  render() {
    const { StateMachineStore, intl } = this.props;
    const {
      targetData,
      stateId,
      stateMachineData,
      machineId,
      id: transferId,
      loading,
      isLoading,
    } = this.state;
    const menu = AppState.currentMenuType;
    const {
      type,
      id: projectId,
      organizationId: orgId,
      name,
    } = menu;
    const rowSelection = {
      getCheckboxProps: record => ({
        defaultChecked: record.id === 119,
      }),
    };
    return (
      <Page>
        <Header
          title={<FormattedMessage id="stateMachine.config" />}
          backPath={stateId ? `/agile/state-machines/${machineId}/editconfig/${transferId}?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}` : `/agile/state-machines/edit/${machineId}/${stateMachineData.status}?type=${type}&id=${projectId}&name=${encodeURIComponent(name)}&organizationId=${orgId}`}
        >
          <Button
            onClick={this.refresh}
            funcType="flat"
          >
            <i className="icon-refresh icon" />
            <FormattedMessage id="refresh" />
          </Button>
        </Header>
        <Spin spinning={isLoading}>
          <Content>
            <div className={`${prefixCls}-header`}>
              {stateMachineData && stateMachineData.status === '2' && (
                <div className={`${prefixCls}-header-tip`}>
                  <span className="icon icon-warning" />
                  <div className={`${prefixCls}-header-tip-text`}>
                    注意：此状态机正在被使用。你正在编辑 状态机草稿 ，如果修改后的草稿需要生效，请点击 发布 。删除草稿 后草稿备份为现在正在使用的状态机。
                </div>
                  <div className={`${prefixCls}-header-tip-action`}>
                    <Button type="primary" funcType="raised" >发布</Button>
                    <Button funcType="raised" className="delete" >删除状态</Button>
                  </div>
                </div>
              )}
              <div className={`${prefixCls}-header-name`}>
                {stateId ? <FormattedMessage id="stateMachine.state" /> : <FormattedMessage id="stateMachine.transfer" />}
                :
              {stateId ? targetData.statusVO && targetData.statusVO.name : targetData.name}
                {stateMachineData && stateMachineData.status === '2' && <span className={`${prefixCls}-header-name-state`}>草稿</span>}
              </div>
              <div className={`${prefixCls}-header-stateMachine`}>
                <span><FormattedMessage id="stateMachine.title" /></span>
                :
              {stateMachineData.name}
              </div>
              <div className={`${prefixCls}-header-page`}>
                <span><FormattedMessage id="stateMachine.transfer.page" /></span>
                :
              {stateMachineData.page}
              </div>
              <div className={`${prefixCls}-header-des`}>{stateMachineData.description}</div>
            </div>
            <div>
              <canvas
                onMouseMove={this.handleMousOover}
                onClick={this.handleCanvasClick}
                ref={this.getCanvas}
                style={{ border: '1px solid #d3d3d3', width: '100%', height: '120px' }}
              >
                Your browser does not support the HTML5 canvas tag.
              </canvas>
            </div>
            {
              !stateId && (
                <Tabs
                  onChange={this.changeTab}
                  defaultActiveKey={StateMachineStore.getConfigType}
                  activeKey={StateMachineStore.getConfigType}
                >
                  <TabPane tab={<FormattedMessage id="stateMachine.condition" />} key="config_condition">
                    <div className={`${prefixCls}-condition-des`}>
                      <FormattedMessage id="stateMachine.condition.des" />
                    </div>
                    <div className={`${prefixCls}-condition-guide`}>
                      <FormattedMessage id="stateMachine.condition.guide" />
                      <a><FormattedMessage id="stateMachine.condition.link" /></a>
                    </div>
                    {
                      !stateId && targetData.conditions && targetData.conditions.length ? (
                        <div>
                          <RadioGroup onChange={this.handleConditoinChange} name="radiogroup" defaultValue={targetData.conditionStrategy}>
                            <Radio value="condition_all">满足下列所有条件</Radio>
                            <Radio value="condition_one">满足下列条件之一</Radio>
                          </RadioGroup>
                          <Spin spinning={loading}>
                            <Card
                              style={{ marginTop: 16 }}
                              type="inner"
                              title={(
                                <Button onClick={() => this.handleEdit('config_condition')} funcType="flat" icon="add">
                                  <FormattedMessage id="stateMachine.config_condition.add" />
                                </Button>
                              )}
                            >
                              {
                                targetData.conditions.map(item => (
                                  <div key={item.id} className={`${prefixCls}-tab-list`}>
                                    {item.codeDescription || ''}
                                    <Popconfirm title={<FormattedMessage id="stateMachine.transfer.deleteConfirm" />} onConfirm={() => this.onDelete(item.id)}>
                                      <Button className="action" shape="circle" size={'small'}>
                                        <span className="icon icon-delete" />
                                      </Button>
                                    </Popconfirm>
                                  </div>
                                ))
                              }
                            </Card>
                          </Spin>
                        </div>
                      )
                        : (
                          <Button onClick={() => this.handleEdit('config_condition')} type="primary" funcType="raised">
                            <FormattedMessage id="stateMachine.config_condition.add" />
                          </Button>
                        )
                    }

                  </TabPane>
                  <TabPane tab={<FormattedMessage id="stateMachine.verification" />} key="config_validator">
                    <div className={`${prefixCls}-condition-des`}>
                      <FormattedMessage id="stateMachine.verification.des" />
                    </div>
                    <div className={`${prefixCls}-condition-guide`}>
                      <FormattedMessage id="stateMachine.condition.guide" />
                      <a><FormattedMessage id="stateMachine.condition.link" /></a>
                    </div>
                    {
                      !stateId && targetData.validators && targetData.validators.length ? (
                        <Spin spinning={loading}>
                          <Card
                            style={{ marginTop: 16 }}
                            type="inner"
                            title={(
                              <Button onClick={() => this.handleEdit('config_validator')} funcType="flat" icon="add">
                                <FormattedMessage id="stateMachine.config_validator.add" />
                              </Button>
                            )}
                          >
                            {
                              targetData.validators.map(item => (
                                <div key={item.id} className={`${prefixCls}-tab-list`}>
                                  {item.codeDescription || ''}
                                  <Popconfirm title={<FormattedMessage id="stateMachine.transfer.deleteConfirm" />} onConfirm={() => this.onDelete(item.id)}>
                                    <Button className="action" shape="circle" size={'small'}>
                                      <span className="icon icon-delete" />
                                    </Button>
                                  </Popconfirm>
                                </div>
                              ))
                            }
                          </Card>
                        </Spin>
                      )
                        : (
                          <Button onClick={() => this.handleEdit('config_validator')} type="primary" funcType="raised">
                            <FormattedMessage id="stateMachine.config_validator.add" />
                          </Button>
                        )
                    }

                  </TabPane>
                  <TabPane tab={<FormattedMessage id="stateMachine.processor" />} key="processor">
                    <div className={`${prefixCls}-condition-des`}>
                      <FormattedMessage id="stateMachine.processor.des" />
                    </div>
                    <div className={`${prefixCls}-condition-guide`}>
                      <FormattedMessage id="stateMachine.condition.guide" />
                      <a><FormattedMessage id="stateMachine.condition.link" /></a>
                    </div>
                    {
                      !stateId && targetData.postpositions && targetData.postpositions.length ? (
                        <Spin spinning={loading}>
                          <Card
                            style={{ marginTop: 16 }}
                            type="inner"

                            title={(
                              <Button onClick={() => this.handleEdit('config_postposition')} funcType="flat" icon="add">
                                <FormattedMessage id="stateMachine.config_postposition.add" />
                              </Button>
                            )}
                          >
                            {
                              targetData.postpositions.map(item => (
                                <div key={item.id} className={`${prefixCls}-tab-list`}>
                                  {item.codeDescription || ''}
                                  <Popconfirm title={<FormattedMessage id="stateMachine.transfer.deleteConfirm" />} onConfirm={() => this.onDelete(item.id)}>
                                    <Button className="action" shape="circle" size={'small'}>
                                      <span className="icon icon-delete" />
                                    </Button>
                                  </Popconfirm>
                                </div>
                              ))
                            }
                          </Card>
                        </Spin>
                      )
                        : (
                          <Button onClick={() => this.handleEdit('config_postposition')} type="primary" funcType="raised">
                            <FormattedMessage id="stateMachine.config_postposition.add" />
                          </Button>
                        )
                    }

                  </TabPane>
                </Tabs>
              )
            }
          </Content>
        </Spin>
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(EditConfig)));
