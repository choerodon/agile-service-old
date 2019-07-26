import React, { Component } from 'react';
import { Form } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Content, Header, Page, Permission, stores } from '@choerodon/boot';
import {
  mxEditor,
  mxGraphHandler,
  mxOutline,
  mxEdgeHandler,
  mxParallelEdgeLayout,
  mxConstants,
  mxEdgeStyle,
  mxLayoutManager,
  mxCodec,
  mxClient,
  mxConnectionHandler,
  mxConstraintHandler,
  mxUtils,
  mxEvent,
  mxImage,
  mxDefaultKeyHandler,
  mxRubberband,
  mxRectangleShape,
  mxConnectionConstraint,
  mxPoint,
  mxRectangle,
  mxCellState,
  mxCell,
  mxGeometry,
  mxGraph,
  mxGraphModel,
  mxEventObject,
} from 'mxgraph-js';
import { getByteLen, getStageMap } from '../../common/utils';

import './Graph.less';
import Pointer from '../../assets/image/point.gif';

const edgeStyle = 'edgeStyle=orthogonalEdgeStyle;jettySize=20;rounded=0;html=1;strokeColor=#868585;labelBackgroundColor=#fff;strokeWidth=1;';
const focusStyle = 'edgeStyle=orthogonalEdgeStyle;jettySize=20;rounded=0;html=1;strokeColor=#000;strokeWidth=2;labelBackgroundColor=#fff;';

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

class Graph extends Component {
  constructor(props) {
    super(props);
    this.state = {
      changed: false,
      statusVisible: false,
      transitionVisible: false,
      statusEditVisible: false,
      transitionEditVisible: false,
      cardVisible: false,
      selectCell: {},
      maxId: 0,
      noLabel: false,
      source: false,
      target: false,
      initial: true,
    };
    this.editor = null;
  }

  componentDidMount() {
    const { setRef } = this.props;
    if (setRef) {
      setRef(this);
    }
    this.editor = new mxEditor();
    this.loadGraph();
  }

  componentWillReceiveProps(nextProps) {
    const { renderChanged } = nextProps;
    if (renderChanged) {
      const { graph } = this.editor;
      const cells = _.filter(graph.model && graph.model.cells, item => item.vertex || item.edge);
      // _.find(cells, item => item.vertex);
      graph.removeCells(cells);
      const parent = graph.getDefaultParent();
      this.readFromData(graph, parent, nextProps);
    }
  }

  /**
   * 定义状态节点和连线的样式
   * @param graph
   */
  setStyle = (graph) => {
    const { noLabel } = this.state;

    // 获取连线样式对象
    const styleEdge = graph.getStylesheet().getDefaultEdgeStyle();
    // 连线样式-转角处是不是圆角
    styleEdge[mxConstants.STYLE_ROUNDED] = true;
    // 连线的计算函数
    styleEdge[mxConstants.STYLE_EDGE] = mxEdgeStyle.OrthConnector;
    // 线宽
    styleEdge.strokeWidth = 2;
    // 是否在连线上显示label
    styleEdge[mxConstants.STYLE_NOLABEL] = noLabel;
    styleEdge[mxConstants.STYLE_FONTCOLOR] = '#000000';

    // 功能暂时不明确
    graph.alternateEdgeStyle = 'vertical';

    // 获取状态节点样式对象
    const styleNode = graph.getStylesheet().getDefaultVertexStyle();
    // 连线距节点的空隙
    // styleNode[mxConstants.STYLE_PERIMETER_SPACING] = 2;
    // 圆角
    styleNode[mxConstants.STYLE_ROUNDED] = true;
    // 节点字号
    styleNode[mxConstants.STYLE_FONTSIZE] = '13';
    // 节点字号
    styleNode[mxConstants.STYLE_FONTSTYLE] = 0;
    // 颜色
    styleNode[mxConstants.STYLE_FONTCOLOR] = '#FFF';
    // 填充色
    styleNode[mxConstants.STYLE_FILLCOLOR] = '#E3E3E3';
    // 边框颜色
    styleNode[mxConstants.STYLE_STROKECOLOR] = '#FFF';
    // 边框宽度
    // styleNode[mxConstants.STYLE_STROKEWIDTH] = 2;
    // 选中后
    mxConstants.VERTEX_SELECTION_STROKEWIDTH = 2;
    mxConstants.VERTEX_SELECTION_COLOR = '#1295FF';

    mxConstants.EDGE_SELECTION_COLOR = '#1295FF';
    mxConstants.EDGE_SELECTION_STROKEWIDTH = 2;
  };

  hideSidebar = () => {
    this.setState({
      showSidebar: false,
    });
  };

  // componentWillRecieveProps(nextProps) {
  //   const { initial } = this.state;
  //   const { graph } = this.editor;
  //   const parent = graph.getDefaultParent();
  //   if (initial) {
  //     this.readFromData(graph, parent);
  //   }
  // }

  /**
   * 创建转换按钮
   */
  handleTransitionVisible = (visible) => {
    this.setState({ transitionVisible: visible });
  };

  /**
   * 取消按钮
   */
  handleCancel = () => {
    this.props.form.resetFields();
    this.setState({
      statusVisible: false,
      transitionVisible: false,
      statusEditVisible: false,
      transitionEditVisible: false,
    });
  };

  /**
   * 检查新增状态是否和已有状态重叠，目前只校验纵向重叠
   * @param height
   * @returns {boolean}
   */
  checkIsOverlap = (height) => {
    const { graph } = this.editor;
    const cells = _.filter(graph.model && graph.model.cells, item => item.vertex || item.edge);
    let tag = false;
    _.forEach(cells, (cell) => {
      if (cell.geometry
        && cell.geometry.x === 150
        && cell.geometry.y >= (height - 26)
        && cell.geometry.y <= (height + 26)
      ) {
        tag = true;
      }
    });
    return tag;
  };

  /**
   * 创建新状态
   */
  createStatus = (values) => {
    const { graph } = this.editor;
    const { maxId } = this.state;
    const parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    let cell;
    try {
      const textWidth = (values.statusVO
        && values.statusVO.name
        && getByteLen(values.statusVO.name)) || 0;
      const statusWidth = textWidth > 62 ? textWidth : 62;
      let height = 0;
      while (this.checkIsOverlap(height)) {
        height += 30;
      }
      cell = graph.insertVertex(
        parent,
        `n${values.id}`,
        values.statusVO && values.statusVO.name,
        150, height, statusWidth, 26,
        `strokeColor=red;fillColor=${statusColor[values.statusVO.type].colour
          ? `${statusColor[values.statusVO.type].colour};`
          : '#E3E3E3;'}`,
      );
      cell.statusId = values.statusId;
      cell.nodeId = values.id;
      cell.status = values.type;
      cell.des = values.statusVO && values.statusVO.description;
    } catch (e) {
      window.console.log(e);
    } finally {
      graph.refresh();
      graph.getModel().endUpdate();
      this.handleCancel();
    }
    return Promise.resolve(cell);
  };

  /**
   * 创建新转换
   */
  createTransition = (values, sourceId, targetId) => {
    const { graph } = this.editor;
    let { source, target } = this.state;
    const { style, changedEdges } = this.state;
    const graphModel = graph.getModel();
    if (sourceId) {
      source = graphModel.getCell(`n${sourceId}`);
    }
    if (targetId) {
      target = graphModel.getCell(`n${targetId}`);
    }
    const { maxId } = this.state;
    const parent = graph.getDefaultParent();
    let cell;
    graph.getModel().beginUpdate();
    try {
      if (values.type === 'transform_all') {
        source = graph.insertVertex(
          parent, `all${targetId}`,
          '全部',
          target.geometry.x + target.geometry.width + 50,
          target.geometry.y + target.geometry.height / 2 - 10, 40, 20,
          'fillColor=#4A4A4A;fontColor=#fff;',
        );
        source.status = 'node_all';
        source.setConnectable(false);
        const allEdgeStyle = 'entryX=1;entryY=0.5;entryPerimeter=1;exitX=0;exitY=0.5;exitPerimeter=1;';
        cell = graph.insertEdge(parent, `t${values.id}`, '', source, target, `${focusStyle}${allEdgeStyle}`);
        cell.pStyle = allEdgeStyle;
      } else {
        cell = graph.insertEdge(parent, `t${values.id}`, values.name, source, target, `${focusStyle}${style || ''}`);
        cell.pStyle = style;
      }
      this.setState({ maxId: maxId + 1 });
      cell.name = values.name;
      cell.des = values.description;
      cell.status = values.type;
      cell.transferId = values.id;
      cell.allStatusTransformId = values.allStatusTransformId;
      if (source.nodeId !== target.nodeId && target) {
        const currentStyle = target.getStyle();
        target.setStyle(`${currentStyle}strokeColor=#FFF;`);
      }
      changedEdges.push(cell);
      this.setState({
        changedEdges,
      });
    } catch (e) {
      window.console.log(e);
    } finally {
      graph.refresh();
      graph.getModel().endUpdate();
      this.handleCancel();
    }
    return Promise.resolve(cell);
  };

  getCell = (id) => {
    const { graph } = this.editor;
    const graphModel = graph.getModel();
    return graphModel.getCell(id);
  }

  /**
   * 控制连线上的label显示
   * @param e
   */
  handleCheckChange = (e) => {
    const { graph } = this.editor;
    const styleEdge = graph.getStylesheet().getDefaultEdgeStyle();
    styleEdge[mxConstants.STYLE_NOLABEL] = !e.target.checked;
    graph.refresh();
  };

  /**
   * 从JSON中加载
   * @param graph
   * @param parent
   */
  readFromData = (graph, parent, nextProps) => {
    // const layout = new mxParallelEdgeLayout(graph);
    // const layoutMgr = new mxLayoutManager(graph);
    // layoutMgr.getLayout = (cell) => {
    //   if (cell.getChildCount() > 0) {
    //     return layout;
    //   }
    //   return null;
    // };
    graph.getModel().beginUpdate();
    const maxId = 0;
    const vertexes = [];
    const highlight = {};
    try {
      const { data, enable = true } = nextProps;
      if (data) {
        if (data.vertex && data.vertex.length) {
          data.vertex.forEach((item) => {
            const {
              positionX: x,
              positionY: y,
              width,
              height,
              statusId,
              id,
              type: status,
              statusVO,
            } = item;
            // 根据状态名称计算节点宽度
            const textWidth = (item.statusVO
              && item.statusVO.name
              && getByteLen(item.statusVO.name)) || 0;
            const statusWidth = textWidth > width ? textWidth : width;
            const vet = graph.insertVertex(
              parent, `n${id}`,
              (item.statusVO && item.statusVO.name) || '',
              x, y, statusWidth, height,
              status === 'node_start'
                ? 'shape=ellipse;fillColor=#FFB100;strokeColor=#FFF;'
                : `shape=rectangle;fillColor=${statusVO && statusVO.type && statusColor[statusVO.type].colour
                  ? `${statusColor[statusVO.type].colour};`
                  : '#E3E3E3'};strokeColor=red;`,
            );

            if (status === 'node_start') {
              vet.setConnectable(false);
            } else if (!enable) {
              const styleEdge = graph.getStylesheet().getDefaultEdgeStyle();
              styleEdge[mxConstants.STYLE_NOLABEL] = false;
              vet.setConnectable(false);
            }
            vertexes[id] = vet;
            vet.statusId = statusId;
            vet.originWide = width;
            vet.nodeId = id;
            vet.status = status;
            vet.allStatusTransformId = item.allStatusTransformId;
            vet.des = item.statusVO && item.statusVO.description;
            this.setState({
              vertexes,
            });
          });
        }
        if (data.edge && data.edge.length) {
          // const { seq } = parentNode;
          data.edge.forEach((item) => {
            const {
              startNodeId,
              endNodeId,
              id,
              name,
              style,
              type,
              description,
            } = item;
            const sourceElement = vertexes[startNodeId];
            const targetElement = vertexes[endNodeId];

            const doc2 = mxUtils.createXmlDocument();
            let ed;
            if (type === 'transform_all') {
              const endNode = vertexes[endNodeId];
              const all = graph.insertVertex(
                parent, `all${endNodeId}`,
                '全部',
                endNode.geometry.x + endNode.geometry.width + 50,
                endNode.geometry.y + endNode.geometry.height / 2 - 10, 40, 20,
                'fillColor=#4A4A4A;fontColor=#fff;',
              );
              all.status = 'node_all';
              // all.setEnabled(false);
              all.setConnectable(false);
              const allEdgeStyle = 'entryX=1;entryY=0.5;entryPerimeter=1;exitX=0;exitY=0.5;exitPerimeter=1;';
              ed = graph.insertEdge(parent, `t${id}`, '', all, targetElement, `${edgeStyle}${allEdgeStyle}`);
              ed.pStyle = allEdgeStyle;
            } else {
              ed = graph.insertEdge(parent, `t${id}`, name || 'open', sourceElement, targetElement, `${edgeStyle}${style || ''}`);
              ed.pStyle = style;
            }
            ed.name = name;
            ed.des = description;
            ed.status = type;
            ed.transferId = id;
            if (startNodeId !== endNodeId && targetElement) {
              const currentStyle = targetElement.getStyle();
              targetElement.setStyle(`${currentStyle}strokeColor=#FFF;`);
            }
          });
        }
      } else {
        const bigin = graph.insertVertex(parent, 0, '', 25, 0, 50, 50, 'shape=ellipse;');
        const firstEle = graph.insertVertex(parent, -2, 'open', 0, 120, 100, 50, '');
        const doc2 = mxUtils.createXmlDocument();
        const edge2 = doc2.createElement('Sequence');
        graph.insertEdge(parent, -3, edge2, bigin, firstEle, edgeStyle);
      }
    } catch (e) {
      window.console.log(e);
    } finally {
      graph.refresh();
      graph.getModel().endUpdate();
      // Need to move othervise the dragging canvas is broken
      graph.moveCells(graph.getChildCells(null, true, true), 1, 0);
      graph.moveCells(graph.getChildCells(null, true, true), -1, 0);
      graph.center(true, true, 0.4, 0.2);
      this.setState({
        changed: false,
        maxId,
      });
    }
  };

  /**
   * 从XML加载
   * @param graph
   * @param parent
   */
  readFromXML = (graph, parent) => {
    // Automatically handle parallel edges
    const layout = new mxParallelEdgeLayout(graph);
    const layoutMgr = new mxLayoutManager(graph);
    layoutMgr.getLayout = (cell) => {
      if (cell.getChildCount() > 0) {
        return layout;
      }
      return null;
    };
    graph.getModel().beginUpdate();
    try {
      const { xml } = this.props;
      const doc = mxUtils.parseXml(xml);
      const codec = new mxCodec(doc);
      const model = codec.decode(doc.documentElement, graph.getModel());

      const cells = model.getElementsByTagName('mxCell');

      const cellArr = Array.from(cells);
      const vertexes = [];

      cellArr.forEach((cell, index) => {
        const element = cell;
        const id = element.getAttribute('id');
        const value = element.getAttribute('value');
        const style = element.getAttribute('style');
        // If element is Vertex/cell
        if (element.hasAttribute('vertex')) {
          const geometry = element.getElementsByTagName('mxGeometry');
          const x = geometry[0].getAttribute('x');
          const y = geometry[0].getAttribute('y');
          const width = geometry[0].getAttribute('width');
          const height = geometry[0].getAttribute('height');
          // add vertex
          vertexes[id] = graph.insertVertex(parent, id, value, x, y, width, height, style);
          this.setState({
            nodesLength: this.state.nodesLength + 1,
          });
        } else if (element.hasAttribute('edge')) {
          const { parentNode } = element;
          const seqIndex = parentNode.getAttribute('seq');
          const sourceElement = vertexes[element.getAttribute('source')];
          const targetElement = vertexes[element.getAttribute('target')];
          const doc2 = mxUtils.createXmlDocument();
          const edge = doc2.createElement('Sequence');
          edge.setAttribute('seq', seqIndex);
          graph.insertEdge(parent, id, edge, sourceElement, targetElement, `strokeColor=${index}`);
        }
      });
    } catch (e) {
      window.console.log(e);
    } finally {
      // Updates the display
      graph.refresh();
      graph.getModel().endUpdate();
      // Need to move othervise the dragging canvas is broken
      graph.moveCells(graph.getChildCells(null, true, true), 1, 0);
      graph.moveCells(graph.getChildCells(null, true, true), -1, 0);
      graph.center();
      this.setState({
        changed: false,
      });
    }
  };

  removeCells = (cells) => {
    const { graph } = this.editor;
    graph.removeCells(cells, true);
  };

  refresh = () => {
    const { graph } = this.editor;
    graph.refresh();
  };

  /**
   * 初始化mxGraph
   */
  loadGraph = (update, datas) => {
    const {
      data,
      lineColor = '#C1C1C1',
      cellClick,
      cellDblClick,
      onLink,
      enable = true,
    } = this.props;
    const { graph } = this.editor;
    // 检查浏览器是否支持
    if (!mxClient.isBrowserSupported()) {
      // 展示错误信息
      mxUtils.error('Browser is not supported!', 200, false);
    } else {
      // 定义状态节点上的连线图标
      // mxConnectionHandler.prototype.connectImage = new mxImage(Connector, 20, 20);
      // Snaps to fixed points
      mxConstraintHandler.prototype.intersects = (icon, point, source, existingEdge) => (
        !source || existingEdge) || mxUtils.intersects(icon.bounds, point);
      mxConstraintHandler.prototype.pointImage = new mxImage(Pointer, 5, 5);

      /**
       * 连线时触发，设置离鼠标位置最近的point
       * @type {mxConnectionHandler.updateEdgeState|*}
       */
      const mxConnectionHandlerUpdateEdgeState = mxConnectionHandler.prototype.updateEdgeState;
      mxConnectionHandler.prototype.updateEdgeState = function updateEdgeState(pt, constraint) {
        // 由于连线问题，禁用
        if (!this.sourceConstraint && pt !== null && this.previous !== null) {
          const constraints = this.graph.getAllConnectionConstraints(this.previous);
          let nearestConstraint = null;
          let dist = null;

          for (let i = 0; i < constraints.length; i += 1) {
            const cp = this.graph.getConnectionPoint(this.previous, constraints[i]);
            if (cp !== null) {
              const tmp = (cp.x - pt.x) * (cp.x - pt.x) + (cp.y - pt.y) * (cp.y - pt.y);

              if (dist === null || tmp < dist) {
                nearestConstraint = constraints[i];
                dist = tmp;
              }
            }
          }

          if (nearestConstraint != null) {
            this.sourceConstraint = nearestConstraint;
          }

          // In case the edge style must be changed during the preview:
          this.edgeState.style.edgeStyle = 'orthogonalEdgeStyle';
          // And to use the new edge style in the new edge inserted into the graph,
          // update the cell style as follows:
          this.edgeState.cell.style = mxUtils.setStyle(this.edgeState.cell.style, 'edgeStyle', this.edgeState.style['edgeStyle']);
        }

        mxConnectionHandlerUpdateEdgeState.apply(this, arguments);
      };

      // Connect preview
      graph.connectionHandler.createEdgeState = function createEdgeState(me) {
        const edge = graph.createEdge(null, null, null, null, null, 'edgeStyle=orthogonalEdgeStyle');

        return new mxCellState(this.graph.view, edge, this.graph.getCellStyle(edge));
      };

      // Enables rubberband selection
      new mxRubberband(graph);

      // 为graph创建的div
      const container = this.graphContainer;

      // 背景
      if (enable) {
        container.style.background = 'url("data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAwIDEwIEwgNDAgMTAgTSAxMCAwIEwgMTAgNDAgTSAwIDIwIEwgNDAgMjAgTSAyMCAwIEwgMjAgNDAgTSAwIDMwIEwgNDAgMzAgTSAzMCAwIEwgMzAgNDAiIGZpbGw9Im5vbmUiIHN0cm9rZT0iI2UwZTBlMCIgb3BhY2l0eT0iMC4yIiBzdHJva2Utd2lkdGg9IjEiLz48cGF0aCBkPSJNIDQwIDAgTCAwIDAgMCA0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZTBlMGUwIiBzdHJva2Utd2lkdGg9IjEiLz48L3BhdHRlcm4+PC9kZWZzPjxyZWN0IHdpZHRoPSIxMDAlIiBoZWlnaHQ9IjEwMCUiIGZpbGw9InVybCgjZ3JpZCkiLz48L3N2Zz4=")';
      }

      // Disables floating connections (only use with no connect image)
      if (graph.connectionHandler.connectImage == null) {
        graph.connectionHandler.isConnectableCell = function isConnectableCell(cell) {
          return false;
        };
        mxEdgeHandler.prototype.isConnectableCell = function isConnectableCell(cell) {
          return graph.connectionHandler.isConnectableCell(cell);
        };
      }
      graph.getAllConnectionConstraints = function getAllConnectionConstraints(terminal) {
        if (terminal != null && this.model.isVertex(terminal.cell)) {
          return [new mxConnectionConstraint(new mxPoint(0, 0), true),
            new mxConnectionConstraint(new mxPoint(0.5, 0), true),
            new mxConnectionConstraint(new mxPoint(1, 0), true),
            new mxConnectionConstraint(new mxPoint(0, 0.5), true),
            new mxConnectionConstraint(new mxPoint(1, 0.5), true),
            new mxConnectionConstraint(new mxPoint(0, 1), true),
            new mxConnectionConstraint(new mxPoint(0.5, 1), true),
            new mxConnectionConstraint(new mxPoint(1, 1), true)];
        }

        return null;
      };

      /**
       * 移动鼠标，清空this.sourceConstraint,由updateEdgeState控制
       * @type {mxConnectionHandler.mouseMove|*}
       */
      const mxConnectionHandlerMouseMove = mxConnectionHandler.prototype.mouseMove;
      mxConnectionHandler.prototype.mouseMove = function mouseMove(sender, me) {
        // 由于控制point会导致连线混乱，先禁掉控制
        // this.sourceConstraint = null;
        mxConnectionHandlerMouseMove.apply(this, arguments);
      };

      // 全部标签不允许拖动，只随着状态移动
      const mxCellsMoved = mxGraph.prototype.cellsMoved;
      mxGraph.prototype.cellsMoved = function cellsMoved(
        cells, dx, dy, disconnect, constrain, extend,
      ) {
        if (cells && cells.length && cells[0].status !== 'node_all') {
          if (!disconnect) {
            const cellAll = cells[0].parent.children
              .filter(cell => cell.status === 'node_all' && cell.id === `all${cells[0].nodeId}`);
            if (cellAll && cellAll.length) {
              // 当退出编辑，再进入页面，拖动node会触发多次cellsMoved，会导致全部被移动多次，原因不明
              // 先这样限制一下
              if (cells.find(cell => cell.status === 'node_all' && cell.id === cellAll[0].id)) {
                return mxCellsMoved.apply(this, arguments);
              }
              cells.push(cellAll[0]);
            }
          }
          return mxCellsMoved.apply(this, arguments);
        }
      };

      /**
       * 获取离鼠标位置最新的point
       * @type {mxConnectionHandler.getSourcePerimeterPoint|*}
       */
      const mxConnectionHandlerGetSourcePerimeterPoint = mxConnectionHandler
        .prototype.getSourcePerimeterPoint;
      mxConnectionHandler.prototype.getSourcePerimeterPoint = function getSourcePerimeterPoint(
        state,
        pt,
        me,
      ) {
        let result = null;
        // 由于控制point会导致连线混乱，先禁掉控制
        if (!this.sourceConstraint && this.previous !== null && pt !== null) {
          const constraints = this.graph.getAllConnectionConstraints(this.previous);
          let nearestConstraint = null;
          let nearest = null;
          let dist = null;

          for (let i = 0; i < constraints.length; i += 1) {
            const cp = this.graph.getConnectionPoint(this.previous, constraints[i]);

            if (cp !== null) {
              const tmp = (cp.x - pt.x) * (cp.x - pt.x) + (cp.y - pt.y) * (cp.y - pt.y);

              if (dist === null || tmp < dist) {
                nearestConstraint = constraints[i];
                nearest = cp;
                dist = tmp;
              }
            }
          }

          if (nearestConstraint !== null) {
            this.sourceConstraint = nearestConstraint;
            result = nearest;
          }
        }

        if (result === null) {
          result = mxConnectionHandlerGetSourcePerimeterPoint.apply(this, arguments);
        }

        return result;
      };

      if (enable) {
        // 自定义连线函
        const that = this;
        mxConnectionHandler.prototype.insertEdge = function insertEdge(
          parent, id, value, source, target,
        ) {
          let sourcePoint = null;
          if (this.sourceConstraint) {
            sourcePoint = this.sourceConstraint;
          }
          let targetPoint = null;
          if (this.constraintHandler && this.constraintHandler.currentConstraint) {
            targetPoint = this.constraintHandler.currentConstraint;
          }
          const style = `exitX=${sourcePoint.point.x};exitY=${sourcePoint.point.y};exitPerimeter=1;entryX=${targetPoint.point.x};entryY=${targetPoint.point.y};entryPerimeter=1;`;
          if (onLink && (source.statusId || source.statusId === 0)) {
            // const currentStyle = target.getStyle();
            // target.setStyle(`${currentStyle}strokeColor=#FFF;`);
            onLink(source, target, style);
          }
          that.setState({
            source,
            target,
            style,
            sourcePoint,
            targetPoint,
            initial: false,
          });
        };
      } else {
        graph.setCellsLocked(true);
        graph.setCellsDisconnectable(false);
      }

      // ========= 属性配置 ==========

      // 每一步修改都进行校验
      this.editor.validation = true;

      // 为graph设置dom
      this.editor.setGraphContainer(container);

      // 禁用右键菜单
      mxEvent.disableContextMenu(container);

      // 取消高亮（功能暂不明确）
      graph.setDropEnabled(false);

      // 是否支持调整状态节点大小
      graph.setCellsResizable(false);

      // 设置状态节点样式
      this.setStyle(graph);

      mxGraph.prototype.cellConnected = function cellConnected(edge, terminal, source, constraint) {
        if (edge != null) {
          this.model.beginUpdate();
          try {
            const previous = this.model.getTerminal(edge, source);

            // Updates the constraint
            this.setConnectionConstraint(edge, terminal, source, constraint);

            // Checks if the new terminal is a port, uses the ID of the port in the
            // style and the parent of the port as the actual terminal of the edge.
            if (this.isPortsEnabled()) {
              let id = null;

              if (this.isPort(terminal)) {
                id = terminal.getId();
                terminal = this.getTerminalForPort(terminal, source);
              }

              // Sets or resets all previous information for connecting to a child port
              const key = (source) ? mxConstants.STYLE_SOURCE_PORT
                : mxConstants.STYLE_TARGET_PORT;
              this.setCellStyles(key, id, [edge]);
            }

            this.model.setTerminal(edge, terminal, source);

            if (this.resetEdgesOnConnect) {
              this.resetEdge(edge);
            }

            this.fireEvent(new mxEventObject(mxEvent.CELL_CONNECTED,
              'edge', edge, 'terminal', terminal, 'source', source,
              'previous', previous, 'point', constraint));
          } finally {
            this.model.endUpdate();
          }
        }
      };

      // 按键监听
      /*
      const keyHandler = new mxDefaultKeyHandler(this.editor);
      keyHandler.bindAction(46, 'delete');
      keyHandler.bindAction(8, 'delete');
      keyHandler.bindAction(90, 'undo', true);
      keyHandler.bindAction(89, 'redo', true);
      keyHandler.bindAction(88, 'cut', true);
      keyHandler.bindAction(67, 'copy', true);
      keyHandler.bindAction(86, 'paste', true);
      keyHandler.bindAction(107, 'zoomIn');
      keyHandler.bindAction(109, 'zoomOut'); */

      let clickFlag = null;

      // 监听节点双击
      graph.dblClick = (evt, cell) => {
        if (clickFlag) {
          clickFlag = clearTimeout(clickFlag);
        }
        if (cell && (cell.status === 'node_start' || cell.status === 'node_all')) {
          return;
        }
        if (cell && cell.edge === true) {
          // 双击连线
          if (cellDblClick && enable) {
            cellDblClick(cell, 'transfer');
          }
          this.setState({ transitionEditVisible: true, selectCell: cell });
        } else if (cell) {
          // 双击状态
          if (cellDblClick && enable) {
            cellDblClick(cell, 'state');
          }
          this.setState({ statusEditVisible: true, selectCell: cell });
        }
        // 禁用默认的双击行为
        mxEvent.consume(evt);
      };

      graph.addListener(mxEvent.CELLS_MOVED, (sender, evt) => {
        const cells = evt.getProperty('cells');
        const moveCell = cells.filter(cell => cell.status !== 'node_all');
        if (moveCell && moveCell.length === 1) {
          const { onMove } = this.props;
          if (onMove) {
            onMove(moveCell[0]);
          }
        }
      });

      graph.addListener(mxEvent.CELL_CONNECTED, (sender, evt) => {
        // 暂不支持拖拽修改已有连线
        if (evt.properties.point) {
          return;
        }
        const previous = evt.getProperty('previous');
        const edge = evt.getProperty('edge');
        const source = evt.getProperty('source');
        const constraint = evt.getProperty('point');
        const style = edge.pStyle && edge.pStyle.split('exitPerimeter=1;');
        if (previous) {
          const { onReLink } = this.props;
          let styles;
          if (constraint) {
            if (source) {
              styles = `exitX=${constraint.point.x};exitY=${constraint.point.y};exitPerimeter=1;${style[1]}`;
            } else {
              styles = `${style[0]}exitPerimeter=1;entryX=${constraint.point.x};entryY=${constraint.point.y};entryPerimeter=1;`;
            }
            edge.pStyle = styles;
            edge.setStyle(`${focusStyle}${styles}`);
          }

          if (onReLink) {
            onReLink(edge, styles);
          }
        }
      });

      graph.addListener(mxEvent.CLICK, (sender, evt) => {
        if (clickFlag) {
          clickFlag = clearTimeout(clickFlag);
        }

        clickFlag = setTimeout(() => {
          const cell = evt.getProperty('cell'); // cell may be null
          const { selectCell = {}, changedEdges = [] } = this.state;
          if (cell && cell.edges && cell.edges.length) {
            changedEdges.map(item => item.setStyle(`${edgeStyle}${item.pStyle || ''}`));
            cell.edges.map(item => item.setStyle(`${focusStyle}${item.pStyle || ''}`));
          } else {
            changedEdges.map(item => item.setStyle(`${edgeStyle}${item.pStyle || ''}`));
          }
          graph.refresh();
          if (cellClick) {
            cellClick(cell);
          }
          if (cell) {
            this.setState({ cardVisible: true, selectCell: cell, changedEdges: cell.edges || [] });
            // Do something useful with cell and consume the event
            evt.consume();
          } else {
            this.setState({ cardVisible: false, changedEdges: [] });
          }
        }, 300);
      });

      // 监听修改
      graph.getModel().addListener(mxEvent.CHANGE, (sender, evt) => {
        this.setState({
          changed: true,
        });
      });

      // 初始化图
      const parent = graph.getDefaultParent();
      // this.readFromXML(graph, parent);
      this.readFromData(graph, parent, this.props);

      // const style = graph.getStylesheet().getDefaultEdgeStyle();

      graph.setHtmlLabels(true);
      // 新连接
      graph.setConnectable(true);
      // Enables moving with right click ang drag
      graph.setPanning(true);

      graph.setTooltips(false);
      // graph.setMultigraph(false);

      // mxGraph.prototype.resetEdgesOnConnect = true;
      // mxGraph.prototype.allowAutoPanning = true;
      // mxGraph.prototype.constrainChildren = false;
      // mxGraph.prototype.constrainRelativeChildren = true;


      // Does not allow dangling edges
      graph.setAllowDanglingEdges(false);

      graph.setEdgeLabelsMovable(false);

      // Stops editing on enter or escape keypress
      // new mxRubberband(graph);

      // 启用对齐线帮助定位
      mxGraphHandler.prototype.guidesEnabled = true;

      // Disable highlight of cells when dragging from toolbar
      graph.setDropEnabled(false);

      // Enables snapping waypoints to terminals
      mxEdgeHandler.prototype.snapToTerminals = true;
    }
    new mxOutline(graph, this.outlineContainer);
  };

  render() {
    return (
      <div className="graph">
        <div className="graph-toolbar">
          {this.props.header}
        </div>
        <div
          className="graph-container"
          style={{
            height: this.props.height,
          }}
          ref={(container) => { this.graphContainer = container; }}
          id="graphContainer"
        />
        {this.props.extra}
        <div
          id="outlineContainer"
          ref={(container) => { this.outlineContainer = container; }}
          className="outline-container"
        />
      </div>
    );
  }
}

export default Form.create({})(injectIntl(Graph));
