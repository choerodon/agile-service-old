import React, { Component } from 'react';
import { Circle } from 'rc-progress';
import 'rc-progress/assets/index.css';
import './index.scss';

class Progress extends Component {
  // shouldComponentUpdate(nextProps, nextState) {
  //   if (nextProps.typeCode === this.props.typeCode) {
  //     return false;
  //   }
  //   return true;
  // }

  render() {
    const {
      percent, title, unit
    } = this.props;
    return (
      <div className="c7n-agile-dashboard-progress">
        <div className="circle">
          <Circle
            percent={percent}
            strokeWidth="6"
            trailWidth="6"
            strokeLinecap="square"
            strokeColor="#ffb100"
            trailColor="rgba(255, 177, 0, 0.3)"
          />
        </div>
        <div className="tip">
          <div>
            <span className="count">{title}</span>
            <span className="unit">{unit}</span>
          </div>
        </div>
      </div>
    );
  }
}
export default Progress;
