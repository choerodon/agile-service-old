import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  sumBy, findIndex, max, sum,
} from 'lodash';
import { observer } from 'mobx-react';
import Connector from './Connector';
import {
  CardHeight, CardWidth, CardMargin, ColumnMinHeight, ColumnWidth, basePosX, basePosY,
} from '../Constants';
import './Connectors.scss';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';


@observer
class Connectors extends Component {
  getLeapWidth = ({
    sprintIndex,
    columnIndex,
    rowIndex,
  }) => {
    const { sprints } = BoardStore;
    const preSprintsWidth = sumBy(sprints.slice(0, sprintIndex), 'columnWidth');
    return preSprintsWidth * ColumnWidth + columnIndex * ColumnWidth + sprintIndex * 3;
  }

  getLeapHeight = ({
    projectIndex,
    sprintIndex,
    columnIndex,
    rowIndex,
  }) => {
    const { projects } = BoardStore;
    const projectHeights = BoardStore.getProjectsHeight;
    const preProjectsHeight = sum(projectHeights.slice(0, projectIndex));
    return preProjectsHeight * ColumnMinHeight + rowIndex * ColumnMinHeight;
  }

  calulatePoint = (connection) => {
    const { from, to } = connection;
    
    const [fromXAxis, fromYAxis] = this.getAxis(from);
    const [toXAxis, toYAxis] = this.getAxis(to);
    console.log([fromXAxis, fromYAxis], [toXAxis, toYAxis]);
    const [fromPosition, toPosition] = this.getPositionByAxis({
      fromXAxis, fromYAxis, toXAxis, toYAxis,
    });
    console.log([fromPosition, toPosition]);
    console.log([this.getPoint(from, fromPosition), this.getPoint(to, toPosition)]);

    return {
      from: this.getPoint(from, fromPosition),
      to: this.getPoint(to, toPosition),
    };
  }

  getPositionByAxis=({
    fromXAxis, fromYAxis, toXAxis, toYAxis,
  }) => {
    if (fromXAxis === toXAxis) {
      if (fromYAxis === toYAxis) {
        return ['bottom', 'top'];
      } else if (fromYAxis > toYAxis) {
        return ['top', 'bottom'];
      } else {
        return ['bottom', 'top'];
      }
    } else if (fromXAxis > toXAxis) {
      if (fromYAxis === toYAxis) {
        return ['left', 'right'];
      } else if (fromYAxis > toYAxis) {
        return ['left', 'right'];
      } else {
        return ['right', 'right'];
      }
    } else if (fromXAxis < toXAxis) {
      if (fromYAxis === toYAxis) {
        return ['right', 'left'];
      } else if (fromYAxis > toYAxis) {
        return ['left', 'right'];
      } else {
        return ['right', 'left'];
      }
    }
    return ['bottom', 'top'];
  }

  // 获取横纵坐标
  getAxis=({
    projectIndex,
    sprintIndex,
    columnIndex,
    rowIndex,
  }) => {
    const { sprints } = BoardStore;
    const projectHeights = BoardStore.getProjectsHeight;
    const xAxis = sumBy(sprints.slice(0, sprintIndex), 'columnWidth') + columnIndex;
    const yAxis = sum(projectHeights.slice(0, projectIndex)) + rowIndex;
    return [xAxis, yAxis];
  }

  getLeftPoint = left => ({
    x: basePosX + this.getLeapWidth(left) + ColumnWidth - CardMargin,
    y: basePosY + this.getLeapHeight(left) + CardHeight / 2,
  })

  getRightPoint = right => ({
    x: basePosX + this.getLeapWidth(right) + 2 + CardMargin,
    y: basePosY + this.getLeapHeight(right) + CardHeight / 2,
  })


  getPoint = (point, position) => {
    const middleX = basePosX + this.getLeapWidth(point) + ColumnWidth / 2;
    const middleY = basePosY + this.getLeapHeight(point) + CardHeight / 2;

    switch (position) {
      case 'top':
        return ({
          x: middleX,
          y: middleY - CardHeight / 2,
        });
      case 'bottom':
        return ({
          x: middleX,
          y: middleY + CardHeight / 2,
        });
      case 'left':
        return ({
          x: middleX - CardWidth / 2,
          y: middleY,
        });
      case 'right':
        return ({
          x: middleX + CardWidth / 2,
          y: middleY,
        });
        
      default:
        return {
          x: 0,
          y: 0,
        };
    }
  }

  getIndex = (info) => {
    const { teamProjectId, sprintId, id } = info;
    const { sprints, projects } = BoardStore;

    const projectIndex = findIndex(projects, { projectId: teamProjectId });
    const sprintIndex = findIndex(sprints, { sprintId });
    const issueIndex = findIndex(projects[projectIndex].teamSprints[sprintIndex].boardFeatures, { id });
    const { columnWidth } = sprints[sprintIndex];
    const columnIndex = issueIndex % columnWidth;
    const rowIndex = Math.ceil((issueIndex + 1) / columnWidth) - 1;
    console.log({
      projectIndex,
      sprintIndex,
      columnIndex,
      rowIndex,
    });
    return {
      projectIndex,
      sprintIndex,
      columnIndex,
      rowIndex,
    };
  }

  render() {
    const { connections } = this.props;
    return (
      <svg
        className="c7nagile-Connectors"
        xmlns="http://www.w3.org/2000/svg"
        // viewBox="0 0 800 600"
        preserveAspectRatio="xMidYMid meet"
      >
        {/* <path     
        className="line"            
        onClick={this.handleClick}
        d="
        M120, 40
        L200, 120"
        // markerStart="url(#arrowhead)" 
        // markerMid="url(#arrowhead)" 
        markerEnd="url(#arrowhead)"
      /> */}
        <g fill="none" stroke="#BEC4E5" strokeWidth="1.5">
          {
            connections.map((connection) => {
              const fromP = this.getIndex(connection.boardFeature);
              const toP = this.getIndex(connection.dependBoardFeature);
              const { from, to, isToLeft } = this.calulatePoint({ from: fromP, to: toP });
              return <Connector from={from} to={to} />;
            })
          }
          {/* <Connector
            from={{
              x: 120,
              y: 40,
            }}
            to={{
              x: 196,
              y: 120,
            }}
          /> */}
        </g>
        <defs>
          <marker
            id="arrowhead"
            viewBox="0 0 10 10"
            refX="8"
            refY="5"
            markerWidth="6"
            markerHeight="6"
            orient="auto"
          >
            <path fill="#3f51b5" d="M 0 0 L 10 5 L 0 10 z" />
          </marker>
          <marker
            id="StartMarker"
            viewBox="0 0 12 12"
            refX="5"
            refY="6"
            markerWidth="8"
            markerHeight="8"
            fill="white"
            stroke="#BEC4E5"
            orient="auto"
          >
            <circle cx="6" cy="6" r="3" />
          </marker>
        </defs>

      </svg>
    );
  }
}

Connectors.propTypes = {

};

export default Connectors;
