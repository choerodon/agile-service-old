import React, { Component } from 'react';
import './PriorityTag.scss';

const PRIORITY_MAP = {
  medium: {
    color: '#3575df',
    bgColor: 'rgba(77, 144, 254, 0.2)',
    name: '中',
  },
  high: {
    color: '#f44336',
    bgColor: 'rgba(244, 67, 54, 0.2)',
    name: '高',
  },
  low: {
    color: 'rgba(0, 0, 0, 0.36)',
    bgColor: 'rgba(0, 0, 0, 0.08)',
    name: '低',
  },
  default: {
    color: 'transparent',
    bgColor: 'transparent',
    name: '',
  },
};

class PriorityTag extends Component {
  shouldComponentUpdate(nextProps, nextState) {
    if (nextProps.priority
      && this.props.priority
      && nextProps.priority.id === this.props.priority.id) {
      return false;
    }
    return true;
  }

  render() {
    const { priority, style } = this.props;
    return (
      <div
        style={style}
        className="c7n-priorityTag-container"
      >
        <div
          className="c7n-priorityTag"
          style={{
            backgroundColor: `${priority ? priority.colour : '#FFFFFF'}1F`,
            color: priority ? priority.colour : '#FFFFFF',
          }}
        >
          {priority ? priority.name : ''}
        </div>
      </div>
    );
  }
}

export default PriorityTag;
