import { observable, action, computed, toJS } from 'mobx';
import { store, stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';

const { AppState } = stores;
const UNIT_STATUS = {
  issue_count: {
    committed: undefined,
    completed: undefined,
  },
  story_point: {
    committed: 'allStoryPoints',
    completed: 'completedStoryPoints',
  },
  remain_time: {
    committed: 'allRemainTimes',
    completed: 'completedRemainTimes',
  },
};
const UNIT2NAME = {
  story_point: '故事点',
  issue_count: '问题计数',
  remain_time: '剩余时间',
};

@store('VersionReportStore')
class VersionReportStore {
  @observable tableLoading = false;
  @observable tableData = [];
  @observable chartLoading = false;
  @observable chartData = [];
  @observable beforeCurrentUnit = 'story_point';
  @observable currentUnit = 'story_point';
  @observable versions = [];
  @observable versionFinishLoading = false;
  @observable currentVersionId = undefined;
  @observable reload = false;

  loadEpicAndChartAndTableData() {
    this.loadVersions()
      .then(() => {
        if (this.versions.length) {
          this.loadChartData();
          this.loadTableData();
        }
      });
  }

  loadVersions() {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/names`, ['version_planning', 'released'])
      .then((res) => {
        this.setVersionFinishLoading(true);
        this.setVersions(res);
        this.setCurrentVersion(res.length ? res[0].versionId : undefined);
      });
  }

  loadChartData(versionId = this.currentVersionId, unit = this.currentUnit) {
    this.setChartLoading(true);
    this.setReload(true);
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/version_chart?versionId=${versionId}&type=${unit}`)
      .then((res) => {
        this.setBeforeCurrentUnit(unit);
        this.setChartData(res);
        this.setChartLoading(false);
        this.setReload(false);
      });
  }

  loadTableData(versionId = this.currentVersionId) {
    this.setTableLoading(true);
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/version_issue_list?organizationId=${orgId}&versionId=${versionId}`)
      .then((res) => {
        this.setTableData(res);
        this.setTableLoading(false);
      });
  }

  @action setTableLoading(data) {
    this.tableLoading = data;
  }

  @action setTableData(data) {
    this.tableData = data;
  }

  @action setChartLoading(data) {
    this.chartLoading = data;
  }

  @action setChartData(data) {
    this.chartData = data;
  }

  @action setBeforeCurrentUnit(data) {
    this.beforeCurrentUnit = data;
  }

  @action setCurrentUnit(data) {
    this.currentUnit = data;
  }

  @action setVersions(data) {
    this.versions = data;
  }

  @action setVersionFinishLoading(data) {
    this.versionFinishLoading = data;
  }

  @action setCurrentVersion(data) {
    this.currentVersionId = data;
  }

  @action setReload(data) {
    this.reload = data;
  }

  @computed get getChartDataX() {
    const groupDays = _.map(this.chartData, 'groupDay');
    return groupDays;
  }

  // 处理后端返回值为null或小数精度问题
  dealNullValue = (list = []) => _.map(list, (item) => {
    if (item) {
      if (item % 1 > 0) {
        return item.toFixed(1);
      }
      return item || 0;
    } else {
      return 0;
    }
  });

  @computed get getChartDataYAll() {
    const prop = UNIT_STATUS[this.beforeCurrentUnit].committed;
    if (!prop) {
      return [];
    }
    const all = _.map(this.chartData, prop);
    return this.dealNullValue(all);
  }

  @computed get getChartDataYCompleted() {
    const prop = UNIT_STATUS[this.beforeCurrentUnit].completed;
    if (!prop) {
      return [];
    }
    const completed = _.map(this.chartData, prop);
    return this.dealNullValue(completed);
  }

  @computed get getChartDataYIssueCountAll() {
    if (this.beforeCurrentUnit !== 'issue_count') {
      return [];
    }
    const all = _.map(this.chartData, 'issueCount');
    return all;
  }

  @computed get getChartDataYIssueCountCompleted() {
    if (this.beforeCurrentUnit === 'issue_count') {
      const all = _.map(this.chartData, 'issueCompletedCount');
      return all;
    }
    return [];
  }

  @computed get getChartDataYIssueCountUnEstimate() {
    if (this.beforeCurrentUnit === 'issue_count') {
      return [];
    }
    const all = _.map(this.chartData, v => (v.unEstimateIssueCount <= 0 ? 0 : (v.unEstimateIssueCount / v.issueCount).toFixed(2) * 100).toFixed(0));
    return all;
  }

  @computed get getChartYAxisName() {
    const name = UNIT2NAME[this.beforeCurrentUnit];
    return name;
  }

  @computed get getLatest() {
    const chartData = this.chartData.slice();
    if (chartData && chartData.length) {
      return chartData[chartData.length - 1];
    }
    return {};
  }

  @computed get getCurrentVersion() {
    const currentVersion = this.versions.find(x => x.versionId === this.currentVersionId);
    return currentVersion || {};
  }
}

const versionReportStore = new VersionReportStore();
export default versionReportStore;
