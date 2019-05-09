import {
  observable, action, computed, toJS, 
} from 'mobx';
import { store, stores, axios } from 'choerodon-front-boot';
import Item from 'choerodon-ui/lib/list/Item';
import _ from 'lodash';

const { AppState } = stores;

@store('VersionReportStore')
class VersionReportStore {
    @observable versionList = [];

    @observable issues = {
      done: {
        data: [],
        pagination: {
          current: 1,
          total: 0,
          pageSize: 10,
        },
      },
      unfinished: {
        data: [],
        pagination: {
          current: 1,
          total: 0,
          pageSize: 10,
        },
      },
      unfinishedUnestimated: {
        data: [],
        pagination: {
          current: 1,
          total: 0,
          pageSize: 10,
        },
      },
    }

    @observable pieData = [];

    @observable sourceData=[];

    @observable reportData = {};

    @observable colors = [];

    @observable pieLoading = false;

  @action changePieLoading(flag) {
      this.pieLoading = flag;
    }

  @action setPieData(data) {
    this.pieData = data;
  }
  
  @action setSourceData(data) {
    this.sourceData = data;
  } 

  @action setColors(data) {
    this.colors = data;
  }

  @computed get getPieData() {
    return this.pieData;
  }

  @computed get getSourceData() {
    return this.sourceData;
  }

  @computed get getColors() {
    return this.colors;
  }

  @computed get getReportData() {
    return toJS(this.reportData);
  }

  @action setReportData(data) {
    this.reportData = data;
  }

  axiosGetReportData(versionId, type) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/${versionId}?type=${type}`);
  }

  @computed get getIssues() {
    return toJS(this.issues);
  }

  @action setIssues(type, type2, data) {
    this.issues[type][type2] = data;
  }

  axiosGetIssues(versionId, data, util) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/${versionId}/issues?status=${data.status}&type=${util}&page=${data.page}&size=${data.size}`);
  }

  @computed get getVersionList() {
    return toJS(this.versionList);
  }

  @action setVersionList(data) {
    this.versionList = data;
  }

  axiosGetVersionList() {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/names`, ['version_planning', 'released']);
  }

  getPieDatas = (projectId, type, sprintId, versionId, startDate, endDate) => {
    const orgId = AppState.currentMenuType.organizationId;
    this.changePieLoading(true);
    axios.get(`/agile/v1/projects/${projectId}/reports/pie_chart?organizationId=${orgId}&fieldName=${type}${sprintId ? `&sprintId=${sprintId}` : ''}${versionId ? `&versionId=${versionId}` : ''}${startDate ? `&startDate=${startDate}` : ''}${endDate ? `&endDate=${endDate}` : ''}`)
      .then((data) => {
        const len = data.length;
        if (len) {
          const colors = ['#9665E2', '#F0657D', '#FAD352', '#FF9915', '#45A3FC', '#3F51B5', '#47CBCA', '#59CB79', '#F953BA', '#D3D3D3'];
          if (len > 10) {
            for (let i = 10; i < len; i += 1) {
              colors.push(`#${(`00000${((Math.random() * 16777215 + 0.5) >> 0).toString(16)}`).slice(-6)}`);
            }
          }
          this.setColors(colors);
          this.setSourceData(data);
          const bigData = data.filter(item => item.percent >= 2);
          const otherData = {
            name: '其它', typeName: null, value: _.reduce(_.filter(data, item => item.percent < 2), (sum, item) => sum += item.value, 0), percent: _.reduce(_.filter(data, item => item.percent < 2), (sum, item) => sum += item.percent, 0).toFixed(2),
          };
          if (otherData.value > 0) {
            bigData.push(otherData);
          }
          this.setPieData(bigData);
        } else {
          this.setPieData([]);
        }
        this.changePieLoading(false);
      })
      .catch((error) => {
        this.changePieLoading(false);
      });
  }
}

const versionReportStore = new VersionReportStore();
export default versionReportStore;
