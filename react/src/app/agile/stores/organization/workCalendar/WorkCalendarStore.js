import axios from 'axios';
import { observable, action, computed } from 'mobx';
import { store, stores } from 'choerodon-front-boot';

@store('WorkCalendarStore')
class WorkCalendarStore {
  @observable holidayRefs = [];
  @observable selectDays = [];
  @observable workDaySetting = {
    saturdayWork: false,
    sundayWork: false,
    useHoliday: false,
    timeZoneCode: 'Asia/Shanghai',
    areaCode: 'Asia',
  };

  @computed get getHolidayRefs() {
    return this.holidayRefs;
  }

  @action setHolidayRef(data) {
    this.holidayRefs = data;
  }

  @computed get getWorkDaySetting() {
    return this.workDaySetting;
  }

  @action setWorkDaySetting(data) {
    this.workDaySetting = data;
  }

  @computed get getSelectDays() {
    return this.selectDays.slice();
  }

  @action setSelectDays(data) {
    this.selectDays = data;
  }

  // 法定假日
  axiosGetHolidayData = (orgId, year) => {
    return axios.get(`/agile/v1/organizations/${orgId}/work_calendar_holiday_refs?year=${year}`).then((data) => {
      this.setHolidayRef(data);
    });
  };

  // 获取时区、周六日、是否应用法定假日
  axiosGetWorkDaySetting = (orgId) => {
    return axios.get(`/agile/v1/organizations/${orgId}/time_zone_work_calendars`).then((data) => {
      if (data) {
        this.setWorkDaySetting(data);
      }
    });
  };

  // 获取自定义设置
  axiosGetCalendarData = (orgId, timeZoneId, year) => {
    return axios.get(`/agile/v1/organizations/${orgId}/time_zone_work_calendars/ref/${timeZoneId}?year=${year}`).then((data) => {
      if (data) {
        this.setSelectDays(data);
      }
    });
  };

  axiosUpdateSetting(orgId, timeZoneId, data) {
    return axios.put(`/agile/v1/organizations/${orgId}/time_zone_work_calendars/${timeZoneId}`, data);
  }

  axiosDeleteCalendarData(orgId, calendarId) {
    return axios.delete(`/agile/v1/organizations/${orgId}/time_zone_work_calendars/ref/${calendarId}`);
  }

  axiosCreateCalendarData(orgId, timeZoneId, data) {
    return axios.post(`/agile/v1/organizations/${orgId}/time_zone_work_calendars/ref/${timeZoneId}`, data);
  }
}

const workCalendarStore = new WorkCalendarStore();
export default workCalendarStore;
