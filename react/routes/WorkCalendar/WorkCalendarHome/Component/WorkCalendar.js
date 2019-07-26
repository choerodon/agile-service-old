import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import Calendar from 'choerodon-ui/lib/rc-components/calendar/';
import _ from 'lodash';
import moment from 'moment';
import 'moment/locale/zh-cn';
import zhCN from 'choerodon-ui/lib/rc-components/calendar/locale/zh_CN';
import './rc-calendar.scss';
import './WorkCalendar.scss';

const format = 'YYYY-MM-DD';

@observer
class WorkCalendar extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  /**
   * 自定义渲染日期格式
   * @param current
   * @param now
   * @returns {*}
   */
  dateRender = (current, now) => {
    // 渲染当前页面可见月数据
    if (current.format('MM') !== now.format('MM')) {
      return (
        <div className="rc-calendar-date not-current-month">
          {current.date()}
        </div>
      );
    }
    const date = current.format(format);
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      selectDays,
      holidayRefs,
    } = this.props;
    const weekdays = [
      saturdayWork ? null : '六',
      sundayWork ? null : '日',
    ];
    const today = moment().format(format);
    const isBeforeToday = current.isBefore(today);

    // 过去的日期不可编辑
    if (isBeforeToday) {
      return (
        <div className="rc-calendar-date before-today">
          {current.date()}
        </div>
      );
    }
    let dateStyle;
    const workDayStyle = {
      color: '#000', background: '#EFEFEF',
    };
    const notWorkDayStyle = {
      color: '#EF2A26', background: '#FFE7E7',
    };
    const localData = moment.localeData();
    // 通过日期缩写判断是否为周六日
    const isWeekDay = weekdays.includes(localData.weekdaysMin(current));
    // 判断是否为法定假期
    let holidayInfo = [];
    let holidayTag = null;
    if (useHoliday && holidayRefs.length) {
      holidayInfo = holidayRefs.filter(d => d.holiday === date);
    }
    // 用户自定义设置
    const selectDay = selectDays.filter(d => d.workDay === date);
    if (selectDay.length) {
      dateStyle = selectDay[0].status === 1 ? workDayStyle : notWorkDayStyle;
    } else if (isWeekDay) {
      dateStyle = notWorkDayStyle;
    } else {
      dateStyle = workDayStyle;
    }
    if (useHoliday && holidayInfo.length) {
      if (selectDay.length) {
        dateStyle = selectDay[0].status === 1 ? workDayStyle : notWorkDayStyle;
      } else {
        dateStyle = holidayInfo[0].status === 1 ? workDayStyle : notWorkDayStyle;
        holidayTag = (
          <React.Fragment>
            {
              holidayInfo[0].status === 1
                ? <span className="tag tag-work">班</span>
                : <span className="tag tag-notwork">休</span>
            }
            <span className="des">{holidayInfo[0].name}</span>
          </React.Fragment>
        );
      }
    }
    return (
      <div className="rc-calendar-date" style={dateStyle}>
        {holidayTag}
        {current.date()}
      </div>
    );
  };

  onSelectDate = (date, source) => {
    if (source && source.source === 'todayButton') {
      return;
    }
    const now = moment();
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      selectDays,
      holidayRefs,
      updateSelete,
    } = this.props;
    const weekdays = [
      saturdayWork ? null : '六',
      sundayWork ? null : '日',
    ];
    if (date && (date.isAfter(now) || date.format(format) === now.format(format))) {
      const selectDate = date.format(format);
      let data = {};
      if (selectDays.length && selectDays.map(d => d.workDay).indexOf(selectDate) !== -1) {
        data = selectDays.filter(d => d.workDay === selectDate);
        updateSelete(data[0]);
      } else {
        const localData = moment.localeData();
        const dayOfWeek = localData.weekdaysMin(date);
        let isWorkDay = !weekdays.includes(dayOfWeek); // 是否是周末
        if (useHoliday && holidayRefs.length) {
          _.forEach(holidayRefs, (item) => {
            if (item.holiday === date.format(selectDate)) {
              isWorkDay = item.status === 1; // 是否是节假日及调休日期
            }
          });
        }
        data = {
          workDay: selectDate,
          status: isWorkDay ? '0' : '1',
        };
        updateSelete(data);
      }
    }
  };

  renderTag = (title, color = '#000', fontColor = '#FFF', text) => (
    <div style={{ marginTop: 5, display: 'flex', alignItem: 'center' }}>
      <span
        className="legend-tag"
        style={{
          backgroundColor: color,
          color: fontColor,
        }}
      >
        {text}
      </span>
      <span className="legend-text">{title}</span>
    </div>
  );

  renderFooter = () => (
    <div>
      <div style={{
        display: 'flex', padding: '0 16px', flexWrap: 'wrap',
      }}
      >
        {this.renderTag('起始日/结束日', '#3F51B5', '#FFF', 'N')}
        {this.renderTag('工作日', '#F5F5F5', '#000', 'N')}
        {this.renderTag('休息日', '#FEF3F2', '#EF2A26', 'N')}
        {this.renderTag('法定节假日补班', '#000', '#FFF', '班')}
        {this.renderTag('法定节假日', '#EF2A26', '#FFF', '休')}
      </div>
    </div>
  );

  render() {
    return (
      <div className="c7n-workCalendar">
        <Calendar
          showDateInput={false}
          showToday={false}
          locale={zhCN}
          dateRender={this.dateRender}
          onSelect={this.onSelectDate}
          renderFooter={this.renderFooter}
        />
      </div>
    );
  }
}

export default WorkCalendar;
