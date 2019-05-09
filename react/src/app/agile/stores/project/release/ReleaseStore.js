import {
  observable, action, computed, toJS,
} from 'mobx';
import axios from 'axios';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('RleaseStore')
class ReleaseStore {
  @observable versionList = [];

  @observable originIssue = [];

  @observable versionDetail = {};

  @observable versionStatusIssues = [];

  @observable publicVersionDetail = {};

  @observable issueTypes = [];

  @observable issueStatus = [];

  @observable issuePriority = [];

  @observable issueCountDetail = {
    todoCount: 0,
    todoStatusCount: 0,
    doingStatusCount: 0,
    doneStatusCount: 0,
    doingCount: 0,
    doingStatus: {},
    doneCount: 0,
    doneStatus: {},
    count: 0,
  };

  @observable filters = {
    advancedSearchArgs: {},
    searchArgs: {},
    content: '',
  };

  @observable filterMap = new Map([
    ['todo', {}],
    ['doing', {}],
    ['done', {}],
    ['0', {}],
  ]);

  @observable deleteReleaseVisible = false;

  @action setSearchContent(data) {
    if (data) {
      this.filters.contents = data;
    }
  }

  @computed get getIssueCountDetail() {
    return this.issueCountDetail;
  }

  @action setIssueCountDetail(data) {
    this.issueCountDetail = data;
  }

  @action setAdvArg(data) {
    if (data) {
      Object.assign(this.filters.advancedSearchArgs, data);
    }
  }

  @action setArg(data) {
    if (data) {
      Object.assign(this.filters.searchArgs, data);
    }
  }

  @computed get getFilter() {
    return toJS(this.filters);
  }

  @action setFilterMap(key) {
    this.filterMap.set(key, toJS(this.filters));
    this.clearArg();
  }

  @computed get getFilterMap() {
    return toJS(this.filterMap);
  }

  @action clearArg() {
    this.filters = {
      advancedSearchArgs: {},
      searchArgs: {},
      content: '',
    };
  }

  axiosFileVersion(id) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${id}/archived`);
  }

  axiosUnFileVersion(id) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${id}/revoke_archived`);
  }

  axiosMergeVersion(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/merge`, data);
  }

  axiosPublicRelease(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/release`, data);
  }

  axiosUnPublicRelease(versionId) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${versionId}/revoke_release`);
  }

  @computed get getPublicVersionDetail() {
    return toJS(this.publicVersionDetail);
  }

  @action setPublicVersionDetail(data) {
    this.publicVersionDetail = data;
  }

  axiosVersionIssueStatistics(id) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${id}/names`);
  }

  axiosGetPublicVersionDetail(versionId) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${versionId}/plan_names`);
  }

  axiosUpdateVersion(versionId, data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/update/${versionId}`, data);
  }

  axiosGetVersionStatusIssues(versionId, data = {}, statusCode) {
    const orgId = AppState.currentMenuType.organizationId;
    if (statusCode && statusCode !== '0') {
      return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${versionId}/issues?organizationId=${orgId}&statusCode=${statusCode}`, data);
    } else {
      return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${versionId}/issues?organizationId=${orgId}`, data);
    }
  }

  handleDataDrag = (projectId, data) => axios.put(`/agile/v1/projects/${projectId}/product_version/drag`, JSON.stringify(data));

  @computed get getVersionStatusIssues() {
    return toJS(this.versionStatusIssues);
  }

  @action setVersionStatusIssues(data) {
    this.versionStatusIssues = data;
  }

  @computed get getOriginIssue() {
    return toJS(this.originIssue);
  }

  @action setOriginIssue(data) {
    this.originIssue = data;
  }

  axiosGetVersionDetail(versionId) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/${versionId}`);
  }

  @computed get getVersionDetail() {
    return toJS(this.versionDetail);
  }

  @action setVersionDetail(data) {
    this.versionDetail = data;
  }

  @computed get getVersionList() {
    return toJS(this.versionList);
  }

  @action setVersionList(data) {
    this.versionList = data;
  }

  @action setFilters(data) {
    this.filters = data;
  }

  @action setIssueTypes(data) {
    this.issueTypes = data;
  }

  @computed get getIssueTypes() {
    return toJS(this.issueTypes);
  }

  @action setIssuePriority(data) {
    this.issuePriority = data;
  }

  @computed get getIssuePriority() {
    return toJS(this.issuePriority);
  }

  @action setIssueStatus(data) {
    this.issueStatus = data;
  }

  @computed get getIssueStatus() {
    return toJS(this.issueStatus);
  }

  @action setDeleteReleaseVisible(data) {
    this.deleteReleaseVisible = data;
  }

  @computed get getDeleteReleaseVisible() {
    return this.deleteReleaseVisible;
  }

  axiosAddRelease(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version`, data);
  }

  axiosGetVersionList(pageRequest) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/versions?page=${pageRequest.page}&size=${pageRequest.size}`, this.filters);
  }

  axiosDeleteVersion(data) {
    let stringData = '';
    // if (data.fixTargetVersionId) {
    //   stringData += `fixTargetVersionId=${data.fixTargetVersionId}&`;
    // }
    // if (data.influenceTargetVersionId) {
    //   stringData += `influenceTargetVersionId=${data.influenceTargetVersionId}`;
    // }
    if (data.targetVersionId) {
      stringData += `targetVersionId=${data.targetVersionId}`;
    }
    return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/delete/${data.versionId}?${stringData}`);
  }

  axiosGetVersionListWithoutPage() {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/names`, ['version_planning']);
  }

  axiosCheckName(proId, name) {
    return axios.get(`/agile/v1/projects/${proId}/product_version/check?name=${name}`);
  }

  async getSettings() {
    const type = await this.loadType();
    this.setIssueTypes(type);

    const status = await this.loadStatus();
    this.setIssueStatus(status);

    const priorities = await this.loadPriorities();
    this.setIssuePriority(priorities);
  }

  loadType = () => axios.get(`/issue/v1/projects/${AppState.currentMenuType.id}/schemes/query_issue_types?apply_type=agile`);

  loadStatus = () => axios.get(`/issue/v1/projects/${AppState.currentMenuType.id}/schemes/query_status_by_project_id?apply_type=agile`);

  loadPriorities = () => axios.get(`/issue/v1/projects/${AppState.currentMenuType.id}/priority/list_by_org`);
}

const releaseStore = new ReleaseStore();
export default releaseStore;
