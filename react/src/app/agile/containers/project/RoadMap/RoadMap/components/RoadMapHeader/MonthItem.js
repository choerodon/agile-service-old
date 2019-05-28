import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Tooltip } from 'choerodon-ui';
import moment from 'moment';
import './MonthItem.scss';

class MonthItem extends Component {
  render() {
    const { startDate, endDate } = this.props;
    const days = endDate.diff(startDate, 'days');
    const isInner = moment(startDate).isSame(startDate.startOf('month'));    
    return (
      <div style={{ flex: days }} className="c7nagile-MonthItem">
        {isInner && (
        <Tooltip title={startDate.format('YYYY年MM月DD日')} placement="topLeft">
          <div className="c7nagile-MonthItem-content">{startDate.format('YYYY年M月D日')}</div>
        </Tooltip>
        )}
        {isInner && <div className="c7nagile-MonthItem-head" />}
        <div className="c7nagile-MonthItem-tail" />        
      </div>
    );
  }
}

MonthItem.propTypes = {

};

export default MonthItem;
