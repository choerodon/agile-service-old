import { observable, action, computed } from 'mobx';
import { store, stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';
import {
  loadSprints, loadSprint, loadSprintIssues, loadChartData, 
} from '../../../api/NewIssueApi';

const { AppState } = stores;

@store('ReportStore')
class ReportStore {
  @observable loading = false;

  // @observable todo = false;

  // @observable done = false;

  // @observable remove = false;

  @observable sprints = [];

  @observable currentSprint = {};

  @observable activeKey = 'done';

  @observable doneIssues = [];

  @observable todoIssues = [];

  @observable removeIssues = [];

  @observable chartData = {
    xAxis: [],
    yAxis: [],
  };

  @observable donePagination = {
    current: 0, 
    pageSize: 10, 
    total: undefined,
  };

  @observable todoPagination = {
    current: 0, 
    pageSize: 10, 
    total: undefined,
  }; 

  @observable removePagination = {
    current: 0, 
    pageSize: 10, 
    total: undefined,
  }

  init() {
    loadSprints(['started', 'closed'])
      .then((res) => {
        this.setSprints(res || []);
        if (res && res.length) {
          this.changeCurrentSprint(res[0].sprintId);
        } else {
          this.setCurrentSprint({});
        }
      })
      .catch((error) => {
        this.setSprints([]);
      });
  }

  changeCurrentSprint(sprintId) {
    if (sprintId) {
      loadSprint(sprintId)
        .then((res) => {
          this.setCurrentSprint(res || {});
          // ready to load when activeKey change
          // this.setTodo(false);
          // this.setDone(false);
          // this.setRemove(false);
          // this.getChartData();
          this.loadCurrentTab();
        })
        .catch((error) => {
        });
    } else {
      this.init();
    }
  }

  loadCurrentTab() {
    const ARRAY = {
      done: 'loadDoneIssues',
      todo: 'loadTodoIssues',
      remove: 'loadRemoveIssues',
    };
    if (!this.currentSprint.sprintId) {
      return;
    }
    this[ARRAY[this.activeKey]]();
  }

  getChartData() {
    loadChartData(this.currentSprint.sprintId, 'issueCount').then((res) => {
      const data = res;
      const newData = [];
      for (let index = 0, len = data.length; index < len; index += 1) {
        if (!_.some(newData, { date: data[index].date })) {
          newData.push({
            date: data[index].date,
            issues: [{
              issueId: data[index].issueId,
              issueNum: data[index].issueNum,
              newValue: data[index].newValue,
              oldValue: data[index].oldValue,
              statistical: data[index].statistical,
            }],
            type: data[index].type,
          });
        } else {
          let index2;
          for (let i = 0, len2 = newData.length; i < len2; i += 1) {
            if (newData[i].date === data[index].date) {
              index2 = i;
            }
          }
          newData[index2].issues = [...newData[index2].issues, {
            issueId: data[index].issueId,
            issueNum: data[index].issueNum,
            newValue: data[index].newValue,
            oldValue: data[index].oldValue,
            statistical: data[index].statistical,
          }];
        }
      }
      for (let index = 0, len = newData.length; index < len; index += 1) {
        let rest = 0;
        if (newData[index].type !== 'endSprint') {
          if (index > 0) {
            rest = newData[index - 1].rest;
          }
        }
        for (let i = 0, len2 = newData[index].issues.length; i < len2; i += 1) {
          if (newData[index].issues[i].statistical) {
            rest += newData[index].issues[i].newValue - newData[index].issues[i].oldValue;
          }
        }
        newData[index].rest = rest;
      }
      this.setChartData({
        xAxis: _.map(newData, 'date'),
        yAxis: _.map(newData, 'rest'),
      });
    }).catch((error) => {
    });
  }

  loadDoneIssues(page = 0, size = 10) {
    this.setLoading(true);
    loadSprintIssues(this.currentSprint.sprintId, 'done', page, size)
      .then((res) => {
        this.setDoneIssues(res.content);
        this.setDonePagination({
          ...this.donePagination,
          total: res.totalElements,
        });
        this.setLoading(false);
        // this.setDone(true);
      });
  }

  loadTodoIssues(page = 0, size = 10) {
    this.setLoading(true);
    loadSprintIssues(this.currentSprint.sprintId, 'unfinished', page, size)
      .then((res) => {
        this.setTodoIssues(res.content);
        this.setTodoPagination({
          ...this.todoPagination,
          total: res.totalElements,
        });
        this.setLoading(false);
        // this.setTodo(true);
      });
  }

  loadRemoveIssues(page = 0, size = 10) {
    this.setLoading(true);
    loadSprintIssues(this.currentSprint.sprintId, 'remove', page, size)
      .then((res) => {
        this.setRemoveIssues(res.content);
        this.setRemovePagination({
          ...this.removePagination,
          total: res.totalElements,
        });
        this.setLoading(false);
        // this.setRemove(true);
      });
  }

  @action setSprints(data) {
    this.sprints = data;
  }

  @action setCurrentSprint(data) {
    this.currentSprint = data;
  }

  @action setActiveKey(data) {
    this.activeKey = data;
  }

  @action setPagination(data) {
    this.pagination = data;
  }

  @action setFilter(data) {
    this.filter = data;
  }

  @action setOrder(data) {
    this.order = data;
  }

  @action setLoading(data) {
    this.loading = data;
  }

  @action setDone(data) {
    this.done = data;
  }

  @action setTodo(data) {
    this.todo = data;
  }

  @action setRemove(data) {
    this.remove = data;
  }

  @action setDoneIssues(data) {
    this.doneIssues = data;
  }

  @action setTodoIssues(data) {
    this.todoIssues = data;
  }

  @action setRemoveIssues(data) {
    this.removeIssues = data;
  }

  @action setDonePagination(data) {
    this.donePagination = data;
  }

  @action setTodoPagination(data) {
    this.todoPagination = data;
  }

  @action setRemovePagination(data) {
    this.removePagination = data;
  }

  @action setDoneFilter(data) {
    this.doneFilter = data;
  }

  @action setTodoFilter(data) {
    this.todoFilter = data;
  }

  @action setRemoveFilter(data) {
    this.removeFilter = data;
  }

  @action setDoneOrder(data) {
    this.doneOrder = data;
  }

  @action setTodoOrder(data) {
    this.todoOrder = data;
  }

  @action setRemoveOrder(data) {
    this.removeOrder = data;
  }

  @action setChartData(data) {
    this.chartData = data;
  }

  @computed get getCurrentSprintStatus() {
    const STATUS_TIP = {
      closed: {
        status: '已关闭',
        action: '结束',
      },
      started: {
        status: '进行中',
        action: '开启',
      },
    };
    if (!this.currentSprint.statusCode) {
      return ({
        status: '',
        action: '',
      });
    } else {
      return STATUS_TIP[this.currentSprint.statusCode]; 
    }
  }
}
const reportStore = new ReportStore();
export default reportStore;
