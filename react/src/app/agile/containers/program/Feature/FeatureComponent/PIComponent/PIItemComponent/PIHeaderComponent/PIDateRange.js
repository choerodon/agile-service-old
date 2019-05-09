import React, { Component } from 'react';
import moment from 'moment';
import { observer, inject } from 'mobx-react';

@inject('AppState', 'HeaderStore')
@observer class PIDateRange extends Component {
  render() {
    const { startDate, endDate } = this.props;
    return (
      <div
        className="c7n-feature-sprintData"
        style={{
          display: 'flex',
          flexWrap: 'wrap',
        }}
      >
        {startDate ? moment(startDate, 'YYYY-MM-DD').format('YYYY年MM月DD日') : '无'}
        {' ~ '}
        {endDate ? moment(endDate, 'YYYY-MM-DD').format('YYYY年MM月DD日') : '无'}
      </div>
    );
  }
}

export default PIDateRange;
