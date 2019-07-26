import { observable, action, computed } from 'mobx';
import { axios, store } from '@choerodon/boot';

const { height } = window.screen;
@store('IssueTypeStore')
class IssueTypeStore {
  @observable isLoading = false;

  @observable createTypeShow = false;

  @observable issueTypes = [];

  @observable issueType = false;

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

  @computed get getCreateTypeShow() {
    return this.createTypeShow;
  }

  @action setCreateTypeShow(data) {
    this.createTypeShow = data;
  }

  @action setIssueTypes(data) {
    this.issueTypes = data;
  }

  @computed get getIssueTypes() {
    return this.issueTypes.slice();
  }

  @action setIssueType(data) {
    this.issueType = data;
  }

  @computed get getIssueType() {
    return this.issueType;
  }

  loadIssueType = (
    orgId,
    page = this.pageInfo.current,
    pageSize = this.pageInfo.pageSize,
    sort = { field: 'id', order: 'desc' },
    param = {},
  ) => {
    this.setIsLoading(true);
    return axios.post(
      `/agile/v1/organizations/${orgId}/issue_type/list?page=${page}&size=${pageSize}&sort=${sort.field},${sort.order}`,
      JSON.stringify(param),
    ).then((data) => {
      const res = this.handleProptError(data);
      if (res) {
        this.setIssueTypes(data.list);
        this.setPageInfo(data);
      }
      this.setIsLoading(false);
    });
  };

  loadIssueTypeById = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/issue_type/${id}`).then((data) => {
    const res = this.handleProptError(data);
    if (res) {
      this.setIssueType(res);
    }
    return res;
  });

  createIssueType = (orgId, issueType) => axios.post(`/agile/v1/organizations/${orgId}/issue_type`, JSON.stringify(issueType))
    .then(data => this.handleProptError(data))


  updateIssueType = (orgId, id, issueType) => axios.put(`/agile/v1/organizations/${orgId}/issue_type/${id}`, JSON.stringify(issueType))
    .then(data => this.handleProptError(data));

  checkDelete = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/issue_type/check_delete/${id}`)
    .then(data => this.handleProptError(data));

  checkName = (orgId, name, id) => axios.get(`/agile/v1/organizations/${orgId}/issue_type/check_name?name=${name}${id ? `&id=${id}` : ''}`)
    .then(data => this.handleProptError(data));

  deleteIssueType = (orgId, id) => axios.delete(`/agile/v1/organizations/${orgId}/issue_type/${id}`)
    .then(data => this.handleProptError(data));

  handleProptError = (error) => {
    if (error && error.failed) {
      // Choerodon.prompt(error.message);
      return false;
    } else {
      return error;
    }
  }
}

const issueTypeStore = new IssueTypeStore();
export default issueTypeStore;
