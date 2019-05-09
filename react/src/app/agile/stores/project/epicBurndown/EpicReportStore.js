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

function transformZero2Placeholder(arr) {
  // 处理小数精度和null
  return arr.map((item) => {
    if (item) {
      if (item % 1 > 0) {
        return item.toFixed(1);
      }
      return item;
    } else {
      return '-';
    }
  });
}

function getChartDataFromServerData(data) {
  if (!data.length) {
    return [[], [], [], [], [], [], [], []];
  }
  const completed = [];
  const remaining = [];
  const added = [];
  const completedAgain = [];
  const assistant = [];
  const showZero = [];
  let showZeroBottom = [];
  let showZeroTop = [];
  data.forEach((epicData, index) => {
    const {
      start, add, done, left,
    } = epicData;
    completed.push(start >= done ? done : start);
    remaining.push(start >= done ? start - done : 0);
    added.push(start >= done ? add : add - (done - start));
    completedAgain.push(start >= done ? 0 : done - start);
  });
  assistant.push(0);
  const len = completed.length;
  completed.forEach((v, i) => {
    if (i !== len - 1) {
      assistant.push(assistant[i] + v);
    }
  });
  assistant.forEach((v, i) => {
    showZero.push(
      !completed[i]
  && !remaining[i]
  && !added[i]
  && !completedAgain[i] ? 0 : '-',
    );
  });
  
  if (showZero.every(v => v === 0)) {
    showZeroBottom = showZero;
    showZeroTop = Array.from({ length: showZero.length });
    showZeroTop = showZeroTop.map(v => '-');
  } else {
    showZero.forEach((v, i) => {
      showZeroBottom.push(v === 0 && assistant[i] === 0 ? '0.00001' : '-'); 
      showZeroTop.push(v === 0 && assistant[i] !== 0 ? '0.00001' : '-'); 
    });
  }
  return [
    assistant,
    transformZero2Placeholder(completed),
    transformZero2Placeholder(remaining),
    transformZero2Placeholder(added),
    transformZero2Placeholder(completedAgain),
    showZeroBottom,
    showZeroTop,
    showZero,
  ];
}
@store('EpicReportStore')
class EpicReportStore {
  @observable tableLoading = false;

  @observable tableData = {};

  @observable chartLoading = false;

  @observable chartDataOrigin = [];

  @observable chartData = [[], [], [], [], []];

  @observable beforeCurrentUnit = 'story_point';

  @observable currentUnit = 'story_point';

  @observable epics = [];

  @observable epicFinishLoading = false;

  @observable currentEpicId = undefined;

  @observable reload = false;

  loadEpicAndChartAndTableData() {
    return this.loadEpics()
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
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/burn_down_coordinate_type/${epicId}?type=Epic`)
      .then((res) => {
        this.setBeforeCurrentUnit(unit);
        this.setChartDataOrigin(res);
        this.setChartData(getChartDataFromServerData(res));
        this.setChartLoading(false);
        this.setReload(false);
      });
  }

  loadTableData(epicId = this.currentEpicId) {
    this.setTableLoading(true);
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/burn_down_report_type/${epicId}?organizationId=${orgId}&type=Epic`)
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

  @action setChartDataOrigin(data) {
    this.chartDataOrigin = data;
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
    return all;
  }

  @computed get getChartDataYCompleted() {
    const prop = UNIT_STATUS[this.beforeCurrentUnit].completed;
    if (!prop) {
      return [];
    }
    const completed = _.map(this.chartData, prop);
    return completed;
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

  // @computed get getPaginationShow() {

  // }
}

const epicReportStore = new EpicReportStore();
export default epicReportStore;
