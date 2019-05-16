import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import moment from 'moment';
import '../../PI.scss';

@inject('AppState', 'HeaderStore')
@observer class PILastDays extends Component {
  getLastDays = () => {
    const { startDate, endDate } = this.props;
    const start = moment(startDate);
    const end = moment(endDate);
    const today = moment(moment().format('YYYY-MM-DD 00:00:00'));
    if (today.isBefore(start)) {
      return moment.duration(end.diff(start)).asDays() + 1;
    } else if (today.isBefore(end)) {
      return moment.duration(end.diff(today)).asDays() + 1;
    } else {
      return 0;
    }
  };

  render() {
    return (
      <p className="c7n-feature-sprintQuestion">
        {
          `剩余${this.getLastDays()}天`
        }
      </p>
    );
  }
}

export default PILastDays;
