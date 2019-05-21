import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { findDOMNode } from 'react-dom';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';

const AUTOSCROLL_RATE = 7;
const isScrollable = (...values) => values.some(value => value === 'auto' || value === 'scroll');
function findScroller(n) {
  let node = n;
  while (node) {
    const style = window.getComputedStyle(node);
    if (isScrollable(style.overflow, style.overflowY, style.overflowX)) {
      return node;
    } else {
      node = node.parentNode;
    }
  }
  return null;
}
@observer
class CreateConnect extends Component {
  saveRef = name => (ref) => {
    this[name] = ref;
  }

  handleMouseDown = (e) => {
    e.stopPropagation();
    e.preventDefault();
    BoardStore.setAddingConnection(true);
    // 为自动滚动做准备
    this.prepareForScroll();
    // console.log(this[mode].getBoundingClientRect().left, e.clientX);

    const { scroller } = this.autoScroll;
    this.initScrollPosition = {
      x: e.clientX,
      y: e.clientY,
      scrollPos: 0,
      scrollLeft: scroller.scrollLeft,
      scrollTop: scroller.scrollTop,
    };
    document.addEventListener('mouseup', this.handleMouseUp);
    document.addEventListener('mousemove', this.handleMouseMove);
    this.circle.style.pointerEvents = 'none';
    this.icon.style.pointerEvents = 'none';
    this.line.style.pointerEvents = 'none';
  }

  handleMouseMove = (e) => {
    e.stopPropagation();
    e.preventDefault();
    const {
      scroller,
      scrollLeftPosition,
      scrollRightPosition,
    } = this.autoScroll;
    this.initMouseX = e.clientX;
    if (scrollLeftPosition >= e.clientX) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'left');
    } else if (scrollRightPosition <= e.clientX) {
      this.startAutoScroll({ clientX: e.clientX, clientY: e.clientY }, 'right');
    } else {
      this.stopAutoScroll(e.clientX);
    }

    this.fireChange(e);
  }

  fireChange=({ clientX, clientY }) => {
    const { scroller } = this.autoScroll;
    const { scrollLeft, scrollTop } = this.initScrollPosition;
    const scrollLeftPos = scroller.scrollLeft - scrollLeft;
    const scrollTopPos = scroller.scrollTop - scrollTop;
    this.setPoint({
      x: this.initPoint.x + clientX - this.initScrollPosition.x + scrollLeftPos,
      y: this.initPoint.y + clientY - this.initScrollPosition.y + scrollTopPos,
    });
  }

  setPoint = ({ x, y }) => {
    this.line.setAttribute('x2', x);
    this.line.setAttribute('y2', y);
    this.circle.setAttribute('cx', x);
    this.circle.setAttribute('cy', y);
    this.icon.setAttribute('x', x - 8);
    this.icon.setAttribute('y', y + 7);
  }

  handleMouseUp = (e) => {
    document.removeEventListener('mousemove', this.handleMouseMove);
    document.removeEventListener('mouseup', this.handleMouseUp);
    this.stopAutoScroll();
    this.setPoint(this.initPoint);
    BoardStore.setAddingConnection(false);
    this.circle.style.pointerEvents = 'all';
  }

  prepareForScroll = () => {
    const scroller = findScroller(findDOMNode(this));// eslint-disable-line
    const { left, width } = scroller.getBoundingClientRect();
    const scrollRightPosition = left + width;
    const scrollLeftPosition = left + 140;
    this.autoScroll = {
      scroller,
      scrollLeftPosition,
      scrollRightPosition,
    };
  }

  // 拖动时，当鼠标到边缘时，自动滚动
  startAutoScroll = ({ clientX, clientY }, mode) => {
    // console.log('start');
    const {
      scroller,
      scrollLeftPosition,
      scrollRightPosition,
    } = this.autoScroll;
    const initScrollLeft = scroller.scrollLeft;
    // 到最左或最右，停止滚动
    const shouldStop = () => (mode === 'right' && ~~(scroller.scrollLeft + scroller.offsetWidth) === scroller.scrollWidth)// eslint-disable-line
      || (mode === 'left' && scroller.scrollLeft === 0);
    if (shouldStop()) {
      cancelAnimationFrame(this.scrollTimer);
      return;
    }
    if (this.scrollTimer) {
      cancelAnimationFrame(this.scrollTimer);
    }
    const scrollFunc = () => {
      if (mode === 'right') {
        scroller.scrollLeft += AUTOSCROLL_RATE;
      } else {
        scroller.scrollLeft -= AUTOSCROLL_RATE;
      }
      const { scrollLeft, scrollTop } = this.initScrollPosition;
      this.initScrollPosition.scrollPos = scroller.scrollLeft - scrollLeft;
      
      // 因为鼠标并没有move，所以这里要手动触发，否则item的宽度不会变化
      
      this.fireChange({ clientX, clientY });
      if (shouldStop()) {
        cancelAnimationFrame(this.scrollTimer);
      } else {
        this.scrollTimer = requestAnimationFrame(scrollFunc);
      }
    };
    this.scrollTimer = requestAnimationFrame(scrollFunc);
  }

  // 停止自动滚动
  stopAutoScroll = () => {
    cancelAnimationFrame(this.scrollTimer);
  }

  render() {
    const { getIndex, getPoint } = this.props;
    const { clickIssue } = BoardStore;
    if (!clickIssue.id) {
      return null;
    }
    const index = getIndex(clickIssue);
    const { x, y } = getPoint(index, 'right');
    this.initPoint = {
      x: x + 20,
      y,
    };   
    return (
      [<line
        ref={this.saveRef('line')}
        x1={x}
        y1={y}
        x2={x + 20}
        y2={y}
        fill="none"
        stroke="#BEC4E5"
        strokeWidth="1.5"
        markerStart="url(#StartMarker)"
        markerEnd="url(#addMarker)"
      />,
        <circle ref={this.saveRef('circle')} style={{ cursor: 'pointer' }} fill="#3F51B5" onMouseDown={this.handleMouseDown} cx={x + 20} cy={y} r="9" />,
        <text ref={this.saveRef('icon')} style={{ fontSize: '16px' }} className="icon" fill="white" x={x + 12} y={y + 7}>&#xE0D7;</text>,
      ]
    );
  }
}

CreateConnect.propTypes = {

};

export default CreateConnect;
