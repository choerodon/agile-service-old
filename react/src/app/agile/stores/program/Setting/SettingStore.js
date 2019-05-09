import axios from 'axios';
import {
  observable, action, computed, toJS, 
} from 'mobx';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('SettingStore')
class SettingStore {
  // 项目群信息
  @observable program = {};

  @computed get getProgram() {
    return toJS(this.program);
  }

  @action setProgram(data) {
    this.program = data;
  }

  // 项目群下项目
  @observable teams = [];

  @computed get getTeams() {
    return toJS(this.teams);
  }

  @action setTeams(data) {
    this.teams = data;
  }

  // 组织层工作日历设置
  @observable orgSetting = {
    saturdayWork: false,
    sundayWork: false,
    useHoliday: false,
    timeZoneWorkCalendarDTOS: [],
    workHolidayCalendarDTOS: [],
  };

  @computed get getOrgSetting() {
    return toJS(this.orgSetting);
  }

  @action setOrgSetting(data) {
    this.orgSetting = data;
  }

  // 项目层工作日历设置
  @observable proSetting = [];

  @computed get getProSetting() {
    return toJS(this.proSetting);
  }

  @action setProSetting(data) {
    this.proSetting = data;
  }

  getProgramInfo = () => {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/agile/v1/projects/${proId}/project_info`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setProgram(res);
        }
      });
  };

  updateProgramInfo = (info) => {
    const proId = AppState.currentMenuType.id;
    return axios.put(`/agile/v1/projects/${proId}/project_info`, info)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setProgram(res);
          Choerodon.prompt('修改成功');
        }
      })
      .catch(() => {
        Choerodon.prompt('修改失败');
      });
  };

  getProgramTeams = () => {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/agile/v1/projects/${proId}/program_info/team`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setTeams(res);
        }
      });
  };

  axiosOrgSetting = (year) => {
    const proId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    return axios.get(`/agile/v1/projects/${proId}/sprint/time_zone_detail/${orgId}?year=${year}`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setOrgSetting(res);
        }
      });
  };

  axiosGetProSetting = (year) => {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/agile/v1/projects/${proId}/work_calendar_ref/project?year=${year}`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          this.setProSetting(res);
        }
      });
  };

  axiosDeleteCalendarData = (id) => {
    const proId = AppState.currentMenuType.id;
    return axios.delete(`/agile/v1/projects/${proId}/work_calendar_ref/${id}`)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          Choerodon.prompt('修改成功');
        }
      })
      .catch(() => {
        Choerodon.prompt('修改失败');
      });
  };

  axiosCreateCalendarData = (info) => {
    const proId = AppState.currentMenuType.id;
    return axios.post(`/agile/v1/projects/${proId}/work_calendar_ref/project`, info)
      .then((res) => {
        if (res.failed) {
          Choerodon.prompt(res.message);
        } else {
          Choerodon.prompt('修改成功');
        }
      })
      .catch(() => {
        Choerodon.prompt('修改失败');
      });
  };
}

const settingStore = new SettingStore();

export default settingStore;
