import {
  observable, computed, action, runInAction,
} from 'mobx';
import { axios, store } from '@choerodon/boot';
import querystring from 'query-string';

const { height } = window.screen;

@store('IssueTypeScreenSchemesStore')
class IssueTypeScreenSchemesStore {
  @observable isLoading = false;

  @observable schemesList = [];

  @observable scheme = false;

  @observable issueTypeList = [];

  @observable screenList = [];

  @observable createShow = false;

  @observable pageInfo = {
    current: 1, total: 0, pageSize: height <= 900 ? 10 : 15,
  };

  @action setPageInfo(page) {
    this.pageInfo.current = page.pageNum;
    this.pageInfo.total = page.total;
    this.pageInfo.pageSize = page.pageSize;
  }

  @computed get getPageInfo() {
    return this.pageInfo;
  }

  @computed get getIsLoading() {
    return this.isLoading;
  }

  @action setIsLoading(loading) {
    this.isLoading = loading;
  }

  @action setSchemeList(data) {
    this.schemesList = data;
  }

  @computed get getSchemeList() {
    return this.schemesList.slice();
  }

  @action setScheme(data) {
    this.scheme = data;
  }

  @computed get getScheme() {
    return this.scheme;
  }

  @action setIssueTypeList(data) {
    this.issueTypeList = data;
  }

  @computed get getIssueTypeList() {
    return this.issueTypeList.slice();
  }

  @action setScreenList(data) {
    this.screenList = data;
  }

  @computed get getScreenList() {
    return this.screenList.slice();
  }

  @computed get getCreateShow() {
    return this.createShow;
  }

  @action setCreateShow(data) {
    this.createShow = data;
  }

  loadSchemeList = (orgId, page = this.pageInfo.current, pageSize = this.pageInfo.pageSize, sort = { field: 'id', order: 'desc' }, map = {
    param: '',
  }) => {
    this.setIsLoading(true);
    return axios.get(`/agile/v1/organizations/${orgId}/page_issue?${querystring.stringify(map)}&page=${page}&size=${pageSize}&sort=${sort.field},${sort.order}`).then((data) => {
      const res = this.handleProptError(data);
      if (res) {
        this.setSchemeList(data.list);        
        this.setPageInfo(data);
      }
      this.setIsLoading(false);
    });
  };

  loadSchemeById = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/page_issue/${id}`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setScheme(res);
    }
    return res;
  });

  createScheme = (orgId, scheme) => axios.post(`/agile/v1/organizations/${orgId}/page_issue`, JSON.stringify(scheme))
    .then(data => this.handleProptError(data));

  updateScheme = (orgId, id, scheme) => axios.put(`/agile/v1/organizations/${orgId}/page_issue/${id}`, JSON.stringify(scheme))
    .then(data => this.handleProptError(data));

  checkDelete = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/page_issue/check_delete/${id}`)
    .then(data => this.handleProptError(data));

  checkName = (orgId, name) => axios.get(`/agile/v1/organizations/${orgId}/page_issue/check_name?name=${name}`)
    .then(data => this.handleProptError(data));

  deleteScheme = (orgId, id) => axios.delete(`/agile/v1/organizations/${orgId}/page_issue/${id}`)
    .then(data => this.handleProptError(data));

  loadIssueTypes = orgId => axios.get(`/agile/v1/organizations/${orgId}/issue_type/types`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setIssueTypeList(res);
    }
    return res;
  });

  loadScreens = orgId => axios.get(`/agile/v1/organizations/${orgId}/page_scheme/query_all`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setScreenList(res);
    }
    return res;
  });

  handleProptError = (error) => {
    if (error && error.failed) {
      // Choerodon.prompt(error.message);
      return false;
    } else {
      return error;
    }
  }
}

const issueTypeScreenSchemesStore = new IssueTypeScreenSchemesStore();

export default issueTypeScreenSchemesStore;
