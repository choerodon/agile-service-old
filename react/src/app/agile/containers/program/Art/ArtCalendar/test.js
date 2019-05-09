
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import 'tui-calendar/dist/tui-calendar.css';
import Calendar from '@toast-ui/react-calendar';

class test extends Component {
  render() {
    const myTheme = {
      // Theme object to extends default dark theme.
    };
    
    const MyComponent = () => (
      <Calendar
        calendars={[
          {
            id: '0',
            name: 'Private',
            bgColor: '#9e5fff',
            borderColor: '#9e5fff',
          },
          {
            id: '1',
            name: 'Company',
            bgColor: '#00a9ff',
            borderColor: '#00a9ff',
          },
        ]}
        defaultView="month"
        disableDblClick
        height="90%"
        isReadOnly={false}
        month={{
          startDayOfWeek: 0,
        }}
        schedules={[
          {
            id: '1',
            calendarId: '0',
            title: 'TOAST UI Calendar Study',
            category: 'time',
            dueDateClass: '',
            start: moment().toISOString(),
            end: moment().add('hours', 3).toISOString(),
          },
          {
            id: '2',
            calendarId: '0',
            title: 'Practice',
            category: 'milestone',
            dueDateClass: '',
            start: moment().add('days', 1).toISOString(),
            end: moment().add('days', 1).toISOString(),
            isReadOnly: true,
          },
          {
            id: '3',
            calendarId: '0',
            title: 'FE Workshop',
            category: 'allday',
            dueDateClass: '',
            start: moment().subtract('days', 2).toISOString(),
            end: moment().add('days', 1).toISOString(),
            isReadOnly: true,
          },
          {
            id: '4',
            calendarId: '0',
            title: 'Report',
            category: 'time',
            dueDateClass: '',
            start: moment().toISOString(),
            end: moment().add('hours', 1).toISOString(),
          },
        ]}

        template={{
          milestone(schedule) {
            return `<span style="color:#fff;background-color: ${schedule.bgColor};">${
              schedule.title
            }</span>`;
          },
          milestoneTitle() {
            return '里程碑';
          },
          taskTitle() {
            return '任务';
          },
          allday(schedule) {
            return `${schedule.title}<i class="fa fa-refresh"></i>`;
          },
          alldayTitle() {
            return 'All Day';
          },
        }}
        theme={myTheme}
        // timezones={[
        //   {
        //     timezoneOffset: 540,
        //     displayLabel: 'GMT+09:00',
        //     tooltip: 'Seoul',
        //   },
        //   {
        //     timezoneOffset: -420,
        //     displayLabel: 'GMT-08:00',
        //     tooltip: 'Los Angeles',
        //   },
        // ]}
        // useDetailPopup
        // useCreationPopup
        view="week"       
        taskView // e.g. true, false, or ['task', 'milestone'])
        scheduleView={['time']} // e.g. true, false, or ['allday', 'time'])
        week={{
          daynames: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
          showTimezoneCollapseButton: true,
          timezonesCollapsed: true,
        }}
      />
    );
    return (
      <MyComponent />
    );
  }
}

test.propTypes = {

};

export default test;
