import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer, inject } from 'mobx-react';
import { axios, stores } from 'choerodon-front-boot';
import Calendar from 'choerodon-ui/lib/rc-components/calendar/';
import _ from 'lodash';
import moment from 'moment';
import 'moment/locale/zh-cn';
import zhCN from 'choerodon-ui/lib/rc-components/calendar/locale/zh_CN';
import './rc-calendar.scss';
import './WorkCalender.scss';

const format = 'YYYY-MM-DD';

@observer
class WorkCalendar extends Component {
  static propTypes = {
    saturdayWork: PropTypes.bool,
    sundayWork: PropTypes.bool,
    useHoliday: PropTypes.bool,
    selectDays: PropTypes.arrayOf(PropTypes.object),
    holidayRefs: PropTypes.arrayOf(PropTypes.object),
    startDate: PropTypes.string,
    workDates: PropTypes.arrayOf(PropTypes.object),
  };

  constructor(props) {
    super(props);
    this.state = {
      workDatesInLocal: [],
    };
  }

  /**
   * 自定义渲染日期格式
   * @param current
   * @param now
   * @returns {*}
   */
  dateRender = (current, now) => {
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      selectDays,
      holidayRefs,
      startDate,
      workDates = [],
      selectedDateDisabled,
    } = this.props;
    const { workDatesInLocal } = this.state;
    // 渲染当前月，当前迭代可见数据
    if (current.format('MM') !== now.format('MM')
      || !startDate
      || moment(current.format(format)).isBefore(moment(moment(startDate).format(format)))) {
      return (
        <div className="rc-calendar-date not-current-month">
          {current.date()}
        </div>
      );
    }
    const date = current.format(format);
    const weekdays = [
      saturdayWork ? null : '六',
      sundayWork ? null : '日',
    ];
    let dateStyle;

    const workDayStyle = {
      color: '#000', background: '#EFEFEF',
    };
    const holiadyWorkDayStyle = {
      color: '#000', background: '#EFEFEF',
    };
    const notWorkDayStyle = {
      color: '#EF2A26', background: '#FFE7E7',
    };
    let sprintDayStyle = {
      color: '#FFF', background: '#3F51B5',
    };
    const localData = moment.localeData();
    // 通过日期缩写判断是否为周六日
    const isWeekDay = weekdays.includes(localData.weekdaysMin(current));
    // 判断是否为法定假期
    let holidayInfo = [];
    if (useHoliday && holidayRefs.length) {
      holidayInfo = holidayRefs.filter(d => d.holiday === date);
    }
    // 冲刺自定义设置
    const workDate = workDates.length
      ? workDates.filter(d => d.workDay === date)
      : workDatesInLocal.filter(d => d.workDay === date);
    // 组织自定义设置
    const selectDay = selectDays.filter(d => d.workDay === date);

    let holidayTag = null;

    const startDateCopy = moment(startDate).format(format);

    if (workDate.length) {
      dateStyle = workDate[0].status === 1 ? workDayStyle : notWorkDayStyle;
    } else if (selectDay.length) {
      dateStyle = selectDay[0].status === 1 ? workDayStyle : notWorkDayStyle;
    } else if (isWeekDay) {
      dateStyle = notWorkDayStyle;
    } else {
      dateStyle = workDayStyle;
    }

    if (startDateCopy === date) {
      if (now.format('DD') === moment(date).format('DD')) {
        sprintDayStyle = {
          color: '#FFF', background: '#3F51B5', boxShadow: 'none',
        };
      }
      dateStyle = sprintDayStyle;
      if (useHoliday && holidayInfo.length) {
        holidayTag = (
          <React.Fragment>
            {
              workDate.length && (workDate[0].status === 0 || holidayInfo[0].status === 0)
                ? (<span className="tag tag-notwork">休</span>)
                : (<span className="tag tag-work" style={{ background: 'none' }}>班</span>)
            }
            <span className="des">{holidayInfo[0].name}</span>
          </React.Fragment>
        );
      } else if (workDate.length) {
        holidayTag = workDate[0].status === 0 ? <span className="tag tag-work" style={{ background: 'none' }}>休</span> : <span className="tag tag-work" style={{ background: 'none' }}>班</span>;
      } else if (selectDay.length) {
        holidayTag = selectDay[0].status === 0 ? <span className="tag tag-work" style={{ background: 'none' }}>休</span> : <span className="tag tag-work" style={{ background: 'none' }}>班</span>;
      } else if (isWeekDay) {
        holidayTag = <span className="tag tag-work" style={{ background: 'none' }}>休</span>;
      } else {
        holidayTag = <span className="tag tag-work" style={{ background: 'none' }}>班</span>;
      }
    } else if (useHoliday && holidayInfo.length) {
      if (workDate.length) {
        dateStyle = workDate[0].status === 0 ? notWorkDayStyle : workDayStyle;
      } else {
        dateStyle = holidayInfo[0].status === 1 ? holiadyWorkDayStyle : notWorkDayStyle;
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

    dateStyle = {
      ...dateStyle,
      cursor: selectedDateDisabled ? 'auto' : 'pointer',
    };

    return (
      <div className="rc-calendar-date" style={dateStyle}>
        {holidayTag}
        {current.date()}
      </div>
    );
  };

  /**
   * 选中日期时触发的函数
   * @param date
   * @param source
   */

  onSelectDate = (date, source) => {
    const { selectedDateDisabled } = this.props;
    if (!selectedDateDisabled) {
      if (source && source.source === 'todayButton') {
        return;
      }
      const {
        saturdayWork,
        sundayWork,
        useHoliday,
        selectDays,
        holidayRefs,
        startDate,
        onWorkDateChange,
        workDates = [],
      } = this.props;
      const weekdays = [
        saturdayWork ? null : '六',
        sundayWork ? null : '日',
      ];
      // 如果当前时间之前，不处理
      if (!date || !startDate
      || moment(date.format(format)).isBefore(moment(moment(startDate).format(format)))) {
        return;
      }
      if (date) {
        this.dealWorkDay(
          date, weekdays, useHoliday, selectDays, holidayRefs, onWorkDateChange, workDates,
        );
      }
    }
  };

  dealWorkDay = (
    date, weekdays, useHoliday, selectDays, holidayRefs, onWorkDateChange, workDates,
  ) => {
    const selectDate = date.format(format);
    const workDate = workDates.filter(d => d.workDay === selectDate);
    if (workDate.length) {
      onWorkDateChange(workDate[0]);
    } else if (selectDays.length && selectDays.map(d => d.workDay).indexOf(selectDate) !== -1) {
      const selectDay = selectDays.filter(d => d.workDay === selectDate);
      onWorkDateChange({
        status: selectDay[0].status ? 0 : 1,
        workDay: selectDay[0].workDay,
      });
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
      onWorkDateChange({
        workDay: selectDate,
        status: isWorkDay ? 0 : 1,
      });
    }
  };

  /**
   * footer 图示
   * @param title -- 图示标题
   * @param color -- icon颜色
   * @param fontColor -- 字体颜色
   * @param text -- icon内部文字
   * @returns ReactNodes
   */
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

  /**
   * 渲染日历 footer
   * @returns ReactNodes
   */

  renderFooter = () => (
    <div>
      <div style={{
        display: 'flex', padding: '0 16px', flexWrap: 'wrap',
      }}
      >
        {this.renderTag('起始日/结束日', '#3F51B5', '#FFF', 'N')}
        {this.renderTag('工作日', '#EFEFEF', '#000', 'N')}
        {this.renderTag('休息日', '#FEF3F2', '#EF2A26', 'N')}
        {this.renderTag('法定节假日补班', '#000', '#FFF', '班')}
        {this.renderTag('法定节假日', '#EF2A26', '#FFF', '休')}
      </div>
    </div>
  );

  render() {
    const { startDate } = this.props;
    return (
      <div className="c7n-workCalendar">
        <Calendar
          defaultValue={moment(startDate)}
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
