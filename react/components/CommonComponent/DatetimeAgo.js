import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';
import TimeAgo from 'timeago-react';

class DatetimeAgo extends Component {
  render() {
    const { date } = this.props;
    return (
      <Tooltip placement="top" title={date || ''}>
        <TimeAgo
          datetime={date || ''}
          locale={Choerodon.getMessage('zh_CN', 'en')}
        />
      </Tooltip>
    );
  }
}

export default DatetimeAgo;
