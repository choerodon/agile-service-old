import {
  observable, action, computed, toJS, 
} from 'mobx';
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

@store('EpicReportStore')
class EpicReportStore {
  @observable tableLoading = false;

  @observable tableData = [];

  @observable chartLoading = false;

  @observable chartData = [];

  @observable beforeCurrentUnit = 'story_point';

  @observable currentUnit = 'story_point';

  @observable epics = [];

  @observable epicFinishLoading = false;

  @observable currentEpicId = undefined;

  @observable reload = false;

  loadEpicAndChartAndTableData() {
    this.loadEpics()
      .then(() => {
        if (this.epics.length) {
          this.loadChartData();
          this.loadTableData();
        }
      });
  }

  loadEpics() {
    const orgId = AppState.currentMenuType.organizationId;
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/epics?organizationId=${orgId}`)
      .then((res) => {
        this.setEpicFinishLoading(true);
        this.setEpics(res);
        this.setCurrentEpic(res.length ? res[0].issueId : undefined);
      });
  }

  loadChartData(epicId = this.currentEpicId, unit = this.currentUnit) {
    this.setChartLoading(true);
    this.setReload(true);
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/epic_chart?epicId=${epicId}&type=${unit}`)
      .then((res) => {
        this.setBeforeCurrentUnit(unit);
        res = res.map(item => ({
          ...item,
          allRemainTimes: item.allRemainTimes || 0,
          allStoryPoints: item.allStoryPoints || 0,
          completedRemainTimes: item.completedRemainTimes || 0,
          completedStoryPoints: item.completedStoryPoints || 0,
          issueCompletedCount: item.issueCompletedCount || 0,
          issueCount: item.issueCount || 0,
          unEstimateIssueCount: item.unEstimateIssueCount || 0,
        }));
        this.setChartData(res);
        this.setChartLoading(false);
        this.setReload(false);
      });
  }

  loadTableData(epicId = this.currentEpicId) {
    this.setTableLoading(true);
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/epic_issue_list?organizationId=${orgId}&epicId=${epicId}`)
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

  @action setEpics(data) {
    this.epics = data;
  }

  @action setEpicFinishLoading(data) {
    this.epicFinishLoading = data;
  }

  @action setCurrentEpic(data) {
    this.currentEpicId = data;
  }

  @action setReload(data) {
    this.reload = data;
  }

  @computed get getChartDataX() {
    const groupDays = _.map(this.chartData, 'groupDay');
    return groupDays;
  }

  @computed get getChartDataYAll() {
    const prop = UNIT_STATUS[this.beforeCurrentUnit].committed;
    if (!prop) {
      return [];
    }
    const all = _.map(this.chartData, prop);
    return this.dealNullValue(all);
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

  @computed get getChartDataYCompleted() {
    const prop = UNIT_STATUS[this.beforeCurrentUnit].completed;
    if (!prop) {
      return [];
    }
    const completed = _.map(this.chartData, prop);
    return this.dealNullValue(completed);
  }

  @computed get getChartDataYIssueCountAll() {
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
    const all = _.map(this.chartData, 'unEstimateIssueCount');
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
}

const epicReportStore = new EpicReportStore();
export default epicReportStore;
