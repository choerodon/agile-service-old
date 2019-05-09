import axios from 'axios';
import {
  observable, action, computed, toJS, 
} from 'mobx';
import { store, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import moment from 'moment';

const { AppState } = stores;

@store('AccumulationStore')
class AccumulationStore {
  @observable filterList = [];

  @observable columnData = [];

  @observable timeData = [{
    id: 1,
    name: '上周',
    check: false,   
  }, {
    id: 2,
    name: '上两周',
    check: false,
  }, {
    id: 3,
    name: '上个月',
    check: false,
  }, {
    id: 4,
    name: '前3月',
    check: false,
  }, {
    id: 5,
    name: '前6月',
    check: false,
  }, {
    id: 6,
    name: '所有时间',
    check: true,
  }, {
    id: 7,
    name: '自定义',
    check: false,
  }];

  @observable startDate = '';

  @observable endDate = '';

  @observable projectCreationDate = '';

  @observable accumulationData = {};

  @observable boardList = [];

  @observable projectInfo = {};

  @action changeFilterData(id, bool) {
    for (let index = 0, len = this.filterList.length; index < len; index += 1) {
      if (this.filterList[index].filterId === id) {
        this.filterList[index].check = bool;
      }
    }
  }

  @action changeColumnData(id, bool) {
    for (let index = 0, len = this.columnData.length; index < len; index += 1) {
      if (this.columnData[index].columnId === id) {
        this.columnData[index].check = bool;
      }
    }
  }

  @computed get getProjectInfo() {
    return toJS(this.projectInfo);
  }

  @action setProjectInfo(data) {
    this.projectInfo = data;
  }

  axiosGetProjectInfo() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/project_info`);
  }

  @computed get getBoardList() {
    return toJS(this.boardList);
  }

  @action setBoardList(data) {
    this.boardList = data;
  }

  @computed get getAccumulationData() {
    return toJS(this.accumulationData);
  }

  @action setAccumulationData(data) {
    this.accumulationData = data;
  }

  axiosGetAccumulationData(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/cumulative_flow_diagram`, data);
  }

  @computed get getStartDate() {
    return this.startDate;
  }

  @action setStartDate(data) {
    this.startDate = data;
  }

  @computed get getEndDate() {
    return this.endDate;
  }

  @action setEndDate(data) {
    this.endDate = data;
  }

  @computed get getTimeData() {
    return toJS(this.timeData);
  }

  @action setTimeData(data) {
    this.timeData = data;
  }

  @computed get getColumnData() {
    return toJS(this.columnData);
  }

  @action setColumnData(data) {
    this.columnData = data;
  }

  @computed get getFilterList() {
    return toJS(this.filterList);
  }

  @action setFilterList(data) {
    this.filterList = data;
  }

  axiosGetFilterList() {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
      contents: [],
      filterName: '',
    });
  }
}

const accumulationStore = new AccumulationStore();
export default accumulationStore;
