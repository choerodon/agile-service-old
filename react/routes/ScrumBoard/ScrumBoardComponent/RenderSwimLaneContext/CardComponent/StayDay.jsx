import React, { Component } from 'react';
import { Tooltip, Icon, Rate } from 'choerodon-ui';

export default class StayDay extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { stayDay, completed } = this.props;
    return nextProps.stayDay !== stayDay || nextProps.completed !== completed;
  }

  render() {
    const convertedDay = (parameters) => {
      if (parameters >= 0 && parameters <= 6) {
        return 1;
      } else if (parameters >= 7 && parameters <= 10) {
        return 2;
      } else if (parameters >= 11 && parameters <= 15) {
        return 3;
      } else {
        return 4;
      }
    };
    const { stayDay, completed } = this.props;
    return stayDay >= 3 && !completed ? (
      <Tooltip title={`卡片停留 ${stayDay} 天`}>
        <div className="rate-wrapper">
          <Rate
            character={<Icon type="brightness_1" />}
            allowHalf
            disabled
            value={convertedDay(stayDay)}
            count={4}
            className={stayDay <= 3 ? 'notEmergency' : 'emergency'}
          />
        </div>
      </Tooltip>
    ) : '';
  }
}
