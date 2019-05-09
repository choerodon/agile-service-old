import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Content, stores } from 'choerodon-front-boot';
import {
  Select,
} from 'choerodon-ui';
import moment from 'moment';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import WorkCalendar from '../../../../../components/WorkCalendar';

const { AppState } = stores;
const { Option } = Select;

@observer
class WorkCalendarPage extends Component {
  onWorkDateChange = (data) => {
    const { sprintId } = ScrumBoardStore.getWorkDate;
    const year = moment().year();
    if (data.calendarId) {
      ScrumBoardStore.axiosDeleteCalendarData(data.calendarId).then(() => {
        ScrumBoardStore.axiosGetCalendarData(year);
      });
    } else {
      ScrumBoardStore.axiosCreateCalendarData(sprintId, data).then(() => {
        ScrumBoardStore.axiosGetCalendarData(year);
      });
    }
  };

  render() {
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      timeZoneWorkCalendarDTOS: selectDays,
      workHolidayCalendarDTOS: holidayRefs,
    } = ScrumBoardStore.getWorkSetting;
    const {
      workCalendarRefDTOS: workDates,
      startDate,
      endDate,
    } = ScrumBoardStore.getWorkDate;
    const { selectedDateDisabled } = this.props;
    return (
      <Content
        description="工作日历是用于配置当前冲刺的实际工作时间，比如：在开启冲刺后，原定的法定节假日需要加班，这时可针对该冲刺进行日历修改，此工作日历的修改会导致当前冲刺报表数据的变动。"
        link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/#工作日历"
        style={{
          padding: 0,
          height: '100%',
        }}
      >
        <WorkCalendar
          startDate={startDate}
          endDate={endDate}
          mode="ScrumBoard"
          saturdayWork={saturdayWork}
          sundayWork={sundayWork}
          useHoliday={useHoliday}
          selectDays={selectDays}
          holidayRefs={holidayRefs}
          workDates={workDates}
          onWorkDateChange={this.onWorkDateChange}
          selectedDateDisabled={selectedDateDisabled}
        />
      </Content>
    );
  }
}

export default WorkCalendarPage;
