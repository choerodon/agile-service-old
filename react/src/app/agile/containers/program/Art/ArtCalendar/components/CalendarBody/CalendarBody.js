import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
import PiItem from '../PiItem';
import Events from '../Events';
import './CalendarBody.scss';

const moment = extendMoment(Moment);


class CalendarBody extends PureComponent {
  renderPIItem = () => {
    const { data } = this.props;
    const itemArr = [];
    data.forEach((pi, i, arr) => {
      const range = arr[i + 1] && moment.range(moment(pi.endDate), moment(arr[i + 1].startDate));
      const diff = range && range.diff('days');
      if (diff) {
        itemArr.push(<PiItem pi={pi} diff />);
        itemArr.push(<div style={{ flex: `${diff}` }} />);
      } else {
        itemArr.push(<PiItem pi={pi} />);
      }
    });
    return itemArr;
  };

  render() {
    const { startDate, endDate, data } = this.props;
    const range = moment.range(startDate, endDate);
    const days = range.diff('days');
    const todayPos = moment.range(moment(startDate), moment()).diff('days');
    return (
      <div className="c7nagile-CalendarBody">
        <div className="c7nagile-CalendarBody-days">
          {
            Array(days).fill(0).map((a, i) => (
              <div className={i === todayPos ? 'c7nagile-CalendarBody-day today' : 'c7nagile-CalendarBody-day'} />
            ))
          }
        </div>
        <div className="c7nagile-CalendarBody-content">
          <div className="c7nagile-CalendarBody-pis">
            {
              this.renderPIItem()
            }
          </div>
          {/* <Events /> */}
        </div>
      </div>
    );
  }
}

CalendarBody.propTypes = {

};

export default CalendarBody;
