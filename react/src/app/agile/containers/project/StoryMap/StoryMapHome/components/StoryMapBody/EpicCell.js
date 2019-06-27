/* eslint-disable no-nested-ternary */
import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { DropTarget } from 'react-dnd';
import { getEmptyImage } from 'react-dnd-html5-backend';
import Column from './Column';
import EpicCard from './EpicCard';
import Cell from './Cell';
import AddCard from './AddCard';
import CreateEpic from './CreateEpic';
import EpicDrag from './EpicDrag';
import { ColumnWidth, CellPadding } from '../../Constants';
import AutoScroll from '../../../../../../common/AutoScroll';
import EpicDragCollapse from './EpicDragCollapse';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import IsInProgramStore from '../../../../../../stores/common/program/IsInProgramStore';

@observer
class EpicCell extends Component {
  state = {
    resizing: false,
  }

  componentDidMount() {
    this.AutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('minimap-container-scroll')[0],
      onMouseMove: this.handleMouseMove,
      onMouseUp: this.handleMouseUp,
    });
    this.DragAutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('minimap-container-scroll')[0],
      pos: {
        left: 200,
        top: 0,
        bottom: 150,
        right: 150,
      },
      type: 'drag',
    });
  }

  handleCollapse = () => {
    const { epic, collapse } = this.props;
    StoryMapStore.collapse(epic.issueId);
  }

  handleAddEpicClick = () => {
    const { epic } = this.props;
    StoryMapStore.addEpic(epic);
  }

  handleCreateEpic = (newEpic) => {
    const { index } = this.props;
    StoryMapStore.afterCreateEpic(index, newEpic);
  }

  /**
   * item改变大小
   * @parameter mode 模式 left或right
   * @parameter multiple 变几个 => 1
   */
  handleItemResize = (mode, multiple) => {
    const { epic } = this.props;
    let width = this.initWidth;
    switch (mode) {
      case 'right': {
        width += multiple;
        break;
      }
      default: break;
    }
    // 最小为一
    if (width > 0) {
      StoryMapStore.setFeatureWidth({ epicId: epic.issueId, featureId: 'none', width });
    }
  }

  handleMouseDown = (mode, e) => {
    // e.stopPropagation();
    // e.preventDefault();
    this.AutoScroll.prepare(e);
    const { otherData: { feature: { none: { width } } } } = this.props;
    this.initWidth = width;
    this.initScrollPosition = {
      x: e.clientX,
    };
    this.setState({
      resizing: true,
    });
  }

  handleMouseMove = (e, { left: scrollPos }) => {
    this.fireResize(e, scrollPos);
  }

  // 触发item的宽度变化
  fireResize = ({ clientX }, scrollPos) => {
    const { isLast } = this.props;
    const posX = clientX - this.initScrollPosition.x + scrollPos;
    if (isLast && posX > 15) {
      this.handleItemResize('right', 1);
    }
    // 一个所占宽度
    if (Math.abs(posX) > (ColumnWidth / 2)) {
      // 变化的倍数 当达到宽度1/2的倍数的时候触发变化        
      const multiple = Math.round(Math.abs(posX) / (ColumnWidth / 2));
      // console.log(multiple);
      // 奇数和偶数的不同处理 5=>2  4=>2
      if (multiple % 2 === 0) {
        this.handleItemResize('right', multiple * (posX > 0 ? 1 : -1) / 2);
      } else {
        this.handleItemResize('right', (multiple - 1) / 2 * (posX > 0 ? 1 : -1));
      }
    }
  }

  /**
   * 鼠标up将数据初始化
   * 
   * 
   */
  handleMouseUp = (e) => {
    this.setState({
      resizing: false,
    });
    const { epic, otherData: { feature: { none: { width } } } } = this.props;

    // 只在数据变化时才请求
    if (this.initWidth !== width) {
      const { issueId } = epic;
      const type = 'epic';
      StoryMapStore.changeWidth({
        width,
        issueId,
        type,
      }, {
        epicId: issueId,
        initWidth: this.initWidth,
      });
    }
  }

  handleMouseDown = (e) => {
    this.DragAutoScroll.prepare(e);
  }

  render() {
    const {
      epic, otherData, lastCollapse, index, connectDropTarget, isOver,
    } = this.props;
    const { resizing } = this.state;
    const { collapse, storys, feature } = otherData || {};
    const { isInProgram } = IsInProgramStore;
    const {
      // featureCommonDOList,
      issueId,
      // issueNum,
      // summary,
      // typeCode,
      adding,
    } = epic;
    let subIssueNum = 0;
    if (storys && feature) {
      subIssueNum = Math.max(storys.length + Object.keys(feature).length - 1, 0);// 减去none
    }

    return (
      <Cell
        saveRef={connectDropTarget}
        epicIndex={index}
        lastCollapse={lastCollapse}
        collapse={collapse}
        rowSpan={collapse ? '0' : '1'}
        style={{
          padding: CellPadding,
          position: 'sticky',
          top: 0,
          zIndex: 6,
          background: isOver ? 'pink' : 'white',
          ...collapse ? {
            borderLeft: lastCollapse ? 'none' : 'solid 1px #D8D8D8',
            borderRight: 'solid 1px #D8D8D8',
            boxShadow: 'rgb(216, 216, 216) 0px -1px 0px inset',
          } : {},
        }}
      >

        {!adding && (
          <span
            style={{
              ...collapse ? {
                position: 'sticky',
                marginLeft: -50,   
                top: 28,
                zIndex: 10,
              } : {
                position: 'absolute',
                left: 4,
                top: 28,
              },
            }}          
          >
            <Icon              
              type={collapse ? 'navigate_next' : 'navigate_before'}
              onClick={this.handleCollapse}
            />
          </span>
          
        )}
        {collapse && <div style={{ width: 40 }} />}
        {collapse
          ? (
            <Fragment>
              <div style={{
                width: 26,
                overflow: 'hidden',
                wordBreak: 'break-all',
                whiteSpace: 'pre-wrap',
                position: 'sticky',
                top: 20,
                marginLeft: 10,
                ...collapse ? { marginTop: -10 } : {},
              }}
              >
                {`${epic.epicName || '无史诗'} (${subIssueNum})`}
              </div>
            </Fragment>
          ) : (
            <Fragment>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Column style={{ minHeight: 'unset' }}>
                  {adding
                    ? <CreateEpic onCreate={this.handleCreateEpic} />
                    : <EpicCard epic={epic} subIssueNum={subIssueNum} index={index} onMouseDown={this.handleMouseDown} />}
                </Column>
                {issueId && !StoryMapStore.isFullScreen ? (!adding && !isInProgram && <AddCard style={{ height: 64 }} onClick={this.handleAddEpicClick} />) : null}
                {resizing && (
                <div style={{
                  position: 'fixed',
                  top: 0,
                  left: 0,
                  bottom: 0,
                  right: 0,
                  zIndex: 9999,
                  cursor: 'col-resize',
                }}
                />
                )}
                {!isInProgram && issueId ? (
                  <div
                    className="c7nagile-StoryMap-FeatureColumn-Resize"
                    style={{
                      top: 0,
                      height: '100%',
                      width: 20,
                      position: 'absolute',
                      zIndex: 2,
                      cursor: 'col-resize',
                      right: -5,
                    }}
                    onMouseDown={this.handleMouseDown.bind(this, 'right')}
                    role="none"
                  >
                    <div className={`c7nagile-StoryMap-FeatureColumn-Resize-highlight ${resizing ? 'active' : ''}`} />
                  </div>
                ) : null}
              </div>              
            </Fragment>
          )}

        {collapse && <EpicDragCollapse epic={epic} index={index} subIssueNum={subIssueNum} onMouseDown={this.handleMouseDown} />}
      </Cell>
    );
  }
}

EpicCell.propTypes = {

};

export default DropTarget(
  'epic',
  {
    drop: props => ({ epic: props.epic, index: props.index }),
  },
  (connect, monitor) => ({
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver(),
    // canDrop: monitor.canDrop(), //去掉可以优化性能
  }),
)(EpicCell);
