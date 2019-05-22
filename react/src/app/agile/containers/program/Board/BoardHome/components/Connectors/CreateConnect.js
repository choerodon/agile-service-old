import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';
import AutoScroll from '../../../../../../common/AutoScroll';


@observer
class CreateConnect extends Component {
  componentDidMount() {
    this.AutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('page-content')[0],
      pos: {
        left: 200,
        top: 150,
        bottom: 150,
        right: 150,
      },
      onMouseMove: this.fireChange,
      onMouseUp: this.handleMouseUp,
    });
  }


  saveRef = name => (ref) => {
    this[name] = ref;
  }

  handleMouseDown = (e) => {
    e.stopPropagation();
    e.preventDefault();
    BoardStore.setAddingConnection(true);
    this.AutoScroll.prepare(e);
    this.initScrollPosition = {
      x: e.clientX,
      y: e.clientY,
    };
    this.circle.style.pointerEvents = 'none';
    this.icon.style.pointerEvents = 'none';
    this.line.style.pointerEvents = 'none';
  }

  fireChange = ({ clientX, clientY }, { left, top }) => {
    this.setPoint({
      x: this.initPoint.x + clientX - this.initScrollPosition.x + left,
      y: this.initPoint.y + clientY - this.initScrollPosition.y + top,
    });
  }

  setPoint = ({ x, y } = this.initPoint) => {
    this.line.setAttribute('x2', x);
    this.line.setAttribute('y2', y);
    this.circle.setAttribute('cx', x);
    this.circle.setAttribute('cy', y);
    this.icon.setAttribute('x', x - 8);
    this.icon.setAttribute('y', y + 7);
  }

  handleMouseUp = () => {
    this.setPoint(this.initPoint);
    BoardStore.setAddingConnection(false);
    this.circle.style.pointerEvents = 'all';
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
