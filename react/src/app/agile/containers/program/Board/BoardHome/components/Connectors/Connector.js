import React, { Component } from 'react';
import PropTypes from 'prop-types';
import './Connector.scss';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';

/**
     * @desc 三阶贝塞尔
     * @param {number} t 当前百分比
     * @param {Array} p1 起点坐标
     * @param {Array} p2 终点坐标
     * @param {Array} cp1 控制点1
     * @param {Array} cp2 控制点2
     */
function threeBezier(t, p1, cp1, cp2, p2) {
  const [x1, y1] = p1;
  const [x2, y2] = p2;
  const [cx1, cy1] = cp1;
  const [cx2, cy2] = cp2;
  const x = x1 * (1 - t) * (1 - t) * (1 - t)
    + 3 * cx1 * t * (1 - t) * (1 - t)
    + 3 * cx2 * t * t * (1 - t)
    + x2 * t * t * t;
  const y = y1 * (1 - t) * (1 - t) * (1 - t)
    + 3 * cy1 * t * (1 - t) * (1 - t)
    + 3 * cy2 * t * t * (1 - t)
    + y2 * t * t * t;
  return [x, y];
}
class Connector extends Component {
  state = {
    click: false,
  }

  handleClick = (e) => {
    e.preventDefault();
    this.setState(({ click }) => ({
      click: !click,
    }));
  }

  handleDeleteClick = () => {
    const { connection } = this.props;
    BoardStore.deleteConnection(connection.id);
  }

  render() {
    const { from, to, isWarn } = this.props;
    const { click } = this.state;
    const ax = (from.x + to.x) / 2 + Math.min(Math.abs(from.x - to.x) / 50, 5);
    const ay = (from.y + to.y) / 2 + Math.min(Math.abs(from.y - to.y) / 50, 9);
    // 获取中点
    const [cx, cy] = threeBezier(0.3, [from.x, from.y], [to.x, to.y], [ax, ay], [ax, ay]);
    return (
      [<path
        onClick={this.handleClick}
        className={`c7nagile-Connector-helperLine ${isWarn ? 'warn' : ''}`}
        d={`
        M${from.x},${from.y} 
        C${ax},${ay} ${ax},${ay} ${to.x},${to.y}`}
      />, <path
        className={`c7nagile-Connector-line ${isWarn ? 'warn' : ''}`}
        d={`
        M${from.x},${from.y} 
        C${ax},${ay} ${ax},${ay} ${to.x},${to.y}`}
        markerStart={`url(#${isWarn ? 'StartMarkerWarn' : 'StartMarker'})`}
        markerEnd={`url(#${isWarn ? 'arrowheadWarn' : 'arrowhead'})`}
      />, click 
        ? [<circle className={`c7nagile-Connector-delete-circle ${isWarn ? 'warn' : ''}`} style={{ cursor: 'pointer' }} stroke="none" onClick={this.handleDeleteClick} cx={cx} cy={cy} r="9" fill="#3F51B5" />,
          <text className={`c7nagile-Connector-delete-icon icon ${isWarn ? 'warn' : ''}`} fill="white" stroke="none" style={{ fontSize: '16px' }} x={cx - 8} y={cy + 7}>&#xE5C3;</text>,
        ] : null]
    );
  }
}

Connector.propTypes = {

};

export default Connector;
