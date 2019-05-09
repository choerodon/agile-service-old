import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
import MonthItem from './MonthItem';
import './RoadMapHeader.scss';

const moment = extendMoment(Moment);
function getMonths(startDate, endDate) {
  const range = moment.range(startDate, endDate);
  const months = Array.from(range.by('month'));
  const len = months.length;
  const firstMonth = months[0];
  const lastMonth = moment(months[len - 1]);
  const innerMonths = months.slice(1, len).map(m => m.startOf('month')); 
  const Months = [firstMonth, ...innerMonths, lastMonth];
  return Months;
}

class RoadMapHeader extends PureComponent {
  renderMonths=(Months) => {
    const MonthItems = [];
    for (let i = 0; i < Months.length - 1; i += 1) {
      MonthItems.push(<MonthItem startDate={Months[i]} endDate={Months[i + 1]} />);
    }
    return MonthItems;
  }

  render() {
    const { startDate, endDate } = this.props;
    const Months = getMonths(startDate, endDate);
    return (
      <div className="c7nagile-RoadMapHeader">        
        {this.renderMonths(Months)}
      </div>
    );
  }
}

RoadMapHeader.propTypes = {

};

export default RoadMapHeader;
