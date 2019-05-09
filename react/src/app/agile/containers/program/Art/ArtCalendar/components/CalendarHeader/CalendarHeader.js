import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import Moment from 'moment';
import { extendMoment } from 'moment-range';
import './CalendarHeader.scss';

const moment = extendMoment(Moment);
class CalendarHeader extends PureComponent {
  caculateYearFlex = (years) => {
    if (years.length === 0) {
      return 1;
    } else {
      const { startDate, endDate } = this.props;
      return years.map((year, i) => {
        if (i === 0) {
          return moment.range(moment(startDate), moment(year).endOf('year')).diff('days') + 1;
        } else if (i === years.length - 1) {
          return moment.range(moment(year).startOf('year'), moment(endDate)).diff('days');
        } else {
          return moment.range(moment(year).startOf('year'), moment(year).endOf('year')).diff('days') + 1;
        }
      });
    }

    // moment().dayOfYear(Number);
  }

  calculateLastWeek = (week) => {
    const { startDate, endDate } = this.props;
    return moment.range(moment(week).startOf('day'), endDate).diff('days');
  }

  render() {
    const { startDate, endDate } = this.props;
    const range = moment.range(startDate, endDate);
    // const totalDays = range.diff('days');

    const years = Array.from(range.by('years'));
    const weeks = Array.from(range.by('weeks', { excludeEnd: true }));
    const YearFlexs = this.caculateYearFlex(years);
    // console.log(YearFlexs);
    return (
      <div className="c7nagile-CalendarHeader">
        {
          <div className="c7nagile-CalendarHeader-years">
            {
              years.map((year, i) => (
                <div style={{ flex: YearFlexs[i] }} className="c7nagile-CalendarHeader-year">
                  <span style={{ paddingLeft: 8 }}>{year.format('YYYY')}</span>
                </div>
              ))
            }
          </div>
        }
        <div className="c7nagile-CalendarHeader-weeks">
          {
            weeks.map((week, i) => (
              <div className="c7nagile-CalendarHeader-week" style={{ flex: i === weeks.length - 1 ? this.calculateLastWeek(week) : 7, flexShrink: 0 }}>
                <div className="c7nagile-CalendarHeader-week-num">
                  {`${i + 1}周`}
                </div>
                <div className="c7nagile-CalendarHeader-week-during">
                  {`${week.format('MM月DD日')} ~ ${week.add(i === weeks.length - 1 ? this.calculateLastWeek(week) - 1 : 6, 'days').format('MM月DD日')}`}
                </div>
              </div>
            ))
          }
        </div>
      </div>
    );
  }
}

CalendarHeader.propTypes = {

};

export default CalendarHeader;
