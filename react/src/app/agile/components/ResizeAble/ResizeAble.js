import React, { Component } from 'react';
import PropTypes from 'prop-types';
import './ResizeAble.scss';

const MODES = {
  top: {
    cursor: 'row-resize',
    top: -5,
    width: '100%',
    height: 10,
  },
  right: {
    right: -5,
    top: 0,
    width: 10,
    height: '100%',
    cursor: 'col-resize',
  },
  bottom: {
    bottom: -5,
    width: '100%',
    height: 10,
    cursor: 'row-resize',
  },
  left: {
    left: -5,
    top: 0,
    width: 10,
    height: '100%',
    cursor: 'col-resize',
  },
  topRight: {
    height: 20,
    width: 20,
    right: -10,
    top: -10,
    cursor: 'ne-resize',
  },
  bottomRight: {
    height: 20,
    width: 20,
    right: -10,
    bottom: -10,
    cursor: 'se-resize',
  },
  bottomLeft: {
    height: 20,
    width: 20,
    bottom: -10,
    left: -10,
    cursor: 'sw-resize',
  },
  topLeft: {
    height: 20,
    width: 20,
    left: -10,
    top: -10,
    cursor: 'nw-resize',
  },
};
const propTypes = {
  defaultSize: PropTypes.shape({
    width: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    height: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  }),
  size: PropTypes.shape({
    maxHeight: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    maxWidth: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    minHeight: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    minWidth: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  }),
  onResizeEnd: PropTypes.func,
};
class ResizeAble extends Component {
  constructor(props) {
    super(props);
    this.state = {
      resizing: false,
      originSize: {
        width: 0,
        height: 0,
        x: 0,
        y: 0,
      },
      mode: null,
    };
    this.mode = null;
    this.size = {
      width: props.defaultSize ? props.defaultSize.width : 'auto',
      height: props.defaultSize ? props.defaultSize.height : 'auto',
    };
  }


  saveRef = name => (ref) => {
    this[name] = ref;
  }

  handleMouseDown = (mode, e) => {
    e.stopPropagation();
    e.preventDefault();
    // 设置默认值
    this.setState({
      mode,
      resizing: true,
      originSize: {
        width: this.resizable.offsetWidth,
        height: this.resizable.offsetHeight,
        x: e.clientX,
        y: e.clientY,
      },
    });
    document.addEventListener('mouseup', this.handleMouseUp);
    document.addEventListener('mousemove', this.handleMouseMove);
  }

  handleMouseUp = (e) => {
    document.removeEventListener('mousemove', this.handleMouseMove);
    document.removeEventListener('mouseup', this.handleMouseUp);
    const { onResizeEnd } = this.props;
    if (onResizeEnd) {
      onResizeEnd(this.size);
    }
    this.setState({
      resizing: false,
    });
  }

  getResizeWidth = (vary) => {
    const { originSize } = this.state;
    const { width: initWidth, height: initHeight } = originSize;
    const { size } = this.props || {};
    const {
      height, width, minHeight, minWidth, maxHeight, maxWidth,
    } = size;
    let Width = 0;
    if (maxWidth !== undefined && initWidth + vary > maxWidth) {
      Width = maxWidth;
    } else if (minWidth !== undefined && initWidth + vary < minWidth) {
      Width = minWidth;
    } else {
      Width = initWidth + vary;
    }
    return Math.max(Width, 0);
    // this.resizable.style.width = `${initWidth + vary}px`;
  }

  getResizeHeight = (vary) => {
    const { originSize } = this.state;
    const { width: initWidth, height: initHeight } = originSize;
    const { size } = this.props || {};
    const {
      height, width, minHeight, minWidth, maxHeight, maxWidth,
    } = size;
    let Height = 0;

    if (maxHeight !== undefined && initHeight + vary > maxHeight) {
      Height = maxHeight;
    } else if (minHeight !== undefined && initHeight + vary < minHeight) {
      Height = minHeight;
    } else {
      Height = initHeight + vary;
    }
    return Math.max(Height, 0);
    // this.resizable.style.height = `${initHeight + vary}px`;
  }

  handleMouseMove = (e) => {
    e.stopPropagation();
    e.preventDefault();
    const { mode, originSize } = this.state;
    const { x, y } = originSize;
    let { width, height } = originSize;

    switch (mode) {
      case 'top': {
        height = this.getResizeHeight(y - e.clientY);
        break;
      }
      case 'topRight': {
        height = this.getResizeHeight(y - e.clientY);
        width = this.getResizeWidth(e.clientX - x);
        break;
      }
      case 'right': {
        width = this.getResizeWidth(e.clientX - x);
        break;
      }
      case 'bottomRight': {
        width = this.getResizeWidth(e.clientX - x);
        height = this.getResizeHeight(e.clientY - y);
        break;
      }
      case 'bottom': {
        height = this.getResizeHeight(e.clientY - y);
        break;
      }
      case 'bottomLeft': {
        height = this.getResizeHeight(e.clientY - y);
        width = this.getResizeWidth(x - e.clientX);
        break;
      }
      case 'left': {
        width = this.getResizeWidth(x - e.clientX);
        break;
      }
      case 'topLeft': {
        height = this.getResizeHeight(y - e.clientY);
        width = this.getResizeWidth(x - e.clientX);
        break;
      }
      default: break;
    }
    this.resizable.style.width = `${width}px`;
    this.resizable.style.height = `${height}px`;
    this.size = {
      width,
      height,
    };
    const { onResize } = this.props;
    if (onResize) {
      onResize(this.size);
    }
  }

  render() {
    const { modes, children, defaultSize } = this.props;
    const { resizing, mode } = this.state;
    return (
      <div
        className="resizable"
        ref={this.saveRef('resizable')}
        style={defaultSize}
      >
        {/* 拖动时，创建一个蒙层来显示拖动效果，防止鼠标指针闪烁 */}
        {resizing && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            zIndex: 9999,
            cursor: MODES[mode].cursor,
          }}
          />
        )}
        {children}
        {
          modes.map(position => <div role="none" key={position} style={{ position: 'absolute', ...MODES[position] }} onMouseDown={this.handleMouseDown.bind(this, position)} />)
        }
      </div>
    );
  }
}

ResizeAble.propTypes = propTypes;

export default ResizeAble;
