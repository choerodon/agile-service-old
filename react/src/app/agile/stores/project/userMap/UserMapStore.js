import {
  observable, action, computed, toJS,
} from 'mobx';
import axios from 'axios';
import _ from 'lodash';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('UserMapStore')
class UserMapStore {
  @observable epics = [];

  @observable showDoneEpic = false;

  @observable isApplyToEpic = false;

  @observable filters = [];

  @observable currentFilters = [];

  @observable onlyMe = false;

  @observable onlyStory = false;

  // @observable
  // currentBacklogFilters = [[], []];
  @observable sprints = [];

  @observable versions = [];

  @observable issues = [];

  @observable backlogIssues = [];

  @observable mode = 'none';

  @observable createEpic = false;

  @observable backlogExpand = [];

  @observable createVOS = false;

  @observable createVOSType = '';

  @observable selectIssueIds = [];

  @observable currentDraggableId = null;

  @observable showBackLog = false;

  @observable currentBacklogFilters = [];

  @observable left = 0;

  @observable top = 0;

  @observable offsetTops = [];

  @observable currentIndex = 0;

  @observable currentNewObj = { epicId: 0, sprintId: 0, versionId: 0 };

  @observable isLoading = false;

  @observable isFullScreen = false;

  @observable cacheIssues = [];

  @observable issueTypes = [];

  @observable defaultPriority = false;

  @action setIsFullScreen(data) {
    this.isFullScreen = data;
  }

  @action setIsLoading(flag) {
    this.isLoading = flag;
  }

  @action setCurrentNewObj(data) {
    this.currentNewObj = data;
  }

  @computed get getTitle() {
    if (this.mode === 'sprint') {
      if (this.sprints[this.currentIndex]) return this.sprints[this.currentIndex].sprintName;
      else return '未规划部分';
    }
    if (this.mode === 'version') {
      if (this.versions[this.currentIndex]) return this.versions[this.currentIndex].name;
      else return '未规划部分';
    }
    return 'issue';
  }

  @computed get getVosId() {
    if (this.mode === 'sprint') {
      if (this.sprints[this.currentIndex]) return this.sprints[this.currentIndex].sprintId;
    }
    if (this.mode === 'version') {
      if (this.versions[this.currentIndex]) return this.versions[this.currentIndex].versionId;
    }
    return 0;
  }

  @action setOffsetTops(data) {
    this.offsetTops = data;
  }

  @action setCurrentIndex(data) {
    this.currentIndex = data;
  }

  @action setLeft(data) {
    this.left = data;
  }

  @action setTop(data) {
    this.t = data;
  }

  @action
  setSelectIssueIds(data) {
    this.selectIssueIds = data;
  }

  @computed get getSelectIssueIds() {
    return this.selectIssueIds;
  }

  @action
  setCurrentDraggableId(data) {
    this.currentDraggableId = data;
  }

  @action
  setEpics(data) {
    this.epics = data;
  }

  @action changeShowBackLog() {
    this.showBackLog = !this.showBackLog;
  }

  @action clearShowBacklog() {
    this.showBackLog = false;
  }

  @action saveChangeShowBackLog() {
    this.showBackLog = false;
  }

  @computed
  get getEpics() {
    return this.epics;
  }

  @action setShowDoneEpic(data) {
    this.showDoneEpic = data;
  }

  @computed get getShowDoneEpic() {
    return this.showDoneEpic;
  }

  @action setIsApplyToEpic(data) {
    this.isApplyToEpic = data;
  }

  @computed get getIsApplyToEpic() {
    return this.isApplyToEpic;
  }

  @action
  setFilters(data) {
    this.filters = data;
  }

  @computed
  get getFilters() {
    return toJS(this.filters);
  }


  @action
  setCurrentFilter(onlyMeChecked = false, onlyStoryChecked = false, moreChecked = []) {
    this.onlyMe = onlyMeChecked;
    this.onlyStory = onlyStoryChecked;
    this.currentFilters = moreChecked;
  }

  @computed
  get getCurrentFilter() {
    return toJS(this.currentFilters);
  }

  @action
  setSprints(data) {
    this.sprints = data;
  }

  @computed
  get getSprints() {
    return this.sprints;
  }

  @action
  setVersions(data) {
    this.versions = data;
  }

  @computed
  get getVersion() {
    return this.versions;
  }

  @action
  setIssues(data) {
    this.issues = data;
  }

  @computed
  get getIssues() {
    return toJS(this.issues);
  }

  @action
  setMode(data) {
    this.mode = data;
  }

  @computed
  get getMode() {
    return this.mode;
  }

  @action
  setCreateEpic(data) {
    this.createEpic = data;
  }

  @computed
  get getCreateEpic() {
    return this.createEpic;
  }

  @action
  setCreateVOS(data) {
    this.createVOS = data;
  }

  @computed
  get getCreateVOS() {
    return this.createVOS;
  }

  @action
  setCreateVOSType(data) {
    this.createVOSType = data;
  }

  @computed
  get getCreateVOSType() {
    return this.createVOSType;
  }

  @action
  setBacklogIssues(data) {
    this.backlogIssues = data;
  }

  @action
  setBacklogExpand(data) {
    this.backlogExpand = data;
  }

  @action
  setCurrentBacklogFilters(type, data) {
    this.currentBacklogFilters[type] = data;
  }

  @action setCurrentBacklogFilter(data) {
    this.currentBacklogFilters = data;
  }

  @action setCacheIssues(data) {
    this.cacheIssues = data;
  }

  @computed get getCacheIssues() {
    return this.cacheIssues.slice();
  }

  @observable assigneeFilterIds = [];

  @computed get getAssigneeFilterIds() {
    return toJS(this.assigneeFilterIds);
  }

  @action setAssigneeFilterIds(data) {
    this.assigneeFilterIds = data;
  }

  @observable assigneeProps = [];

  @computed get getAssigneeProps() {
    return this.assigneeProps;
  }

  @action setAssigneeProps(data) {
    this.assigneeProps = data;
  }


  loadEpic = () => {
    this.setIsLoading(true);
    let url = '';
    if (this.getCurrentFilter.length) {
      const currentFilter = JSON.stringify(this.getCurrentFilter).replace(/(]|\[)/g, '');
      url += `&quickFilterIds=${currentFilter}`;
    }
    if (this.getAssigneeFilterIds.length) {
      const currentAssignee = JSON.stringify(this.getAssigneeFilterIds).replace(/(]|\[)/g, '');
      url += `&assigneeFilterIds=${currentAssignee}`;
    }
    if (this.onlyMe) {
      url += `&assigneeId=${AppState.getUserId}`;
    }
    if (this.onlyStory) {
      url += `&onlyStory=${this.onlyStory}`;
    }
    const orgId = AppState.currentMenuType.organizationId;
    const proId = AppState.currentMenuType.id;
    return axios.get(`/agile/v1/projects/${proId}/issues/storymap/epics?organizationId=${orgId}&showDoneEpic=${this.showDoneEpic}${this.isApplyToEpic ? url : ''}`)
      .then((epics) => {
        this.setEpics(epics);
        this.setIsLoading(false);
      })
      .catch((error) => {
        Choerodon.handleResponseError(error);
        this.setIsLoading(false);
      });
  };

  loadIssues = (pageType, isLoading = true) => {
    this.setIsLoading(isLoading);
    // let url = '';
    // if (this.currentFilters.includes('mine')) {
    //   url += `&assigneeId=${AppState.getUserId}`;
    // }
    // if (this.currentFilters.includes('userStory')) {
    //   url += '&onlyStory=true';
    // }
    const orgId = AppState.currentMenuType.organizationId;
    let axiosGetIssue = `/agile/v1/projects/${AppState.currentMenuType.id}/issues/storymap/issues?organizationId=${orgId}&type=${this.mode}&pageType=${pageType}`;
    if (this.getCurrentFilter.length) {
      const currentFilter = JSON.stringify(this.getCurrentFilter).replace(/(]|\[)/g, '');
      axiosGetIssue += `&quickFilterIds=${currentFilter}`;
    }
    if (this.getAssigneeFilterIds.length) {
      const currentAssignee = JSON.stringify(this.getAssigneeFilterIds).replace(/(]|\[)/g, '');
      axiosGetIssue += `&assigneeFilterIds=${currentAssignee}`;
    }
    if (this.onlyMe) {
      axiosGetIssue += `&assigneeId=${AppState.getUserId}`;
    }
    if (this.onlyStory) {
      axiosGetIssue += `&onlyStory=${this.onlyStory}`;
    }
    return axios.get(axiosGetIssue)
      .then((issues) => {
        this.setIsLoading(false);
        if (issues.failed) {
          this.setIssues([]);
        } else if (this.mode === 'version') {
          const uniqIssues = _.uniqBy(_.orderBy(issues, ['versionId'], ['desc']), 'issueId');
          const sortedUniqIssues = _.orderBy(uniqIssues, 'mapRank', 'asc');
          // this.setIssues(_.uniqBy(_.orderBy(issues, ['versionId'], ['desc']), 'issueId'));
          this.setIssues(sortedUniqIssues);
        } else {
          const sortedIssues = _.orderBy(issues, 'mapRank', 'asc');
          // this.setIssues(issues);
          this.setIssues(sortedIssues);
        }

        const arrAssignee = [];
        _.forEach(issues, (item) => {
          if (item.assigneeId && item.assigneeName) {
            arrAssignee.push({
              id: item.assigneeId,
              realName: item.assigneeName,
            });
          }
        });
        this.setAssigneeProps(_.map(_.union(_.map(arrAssignee, JSON.stringify)), JSON.parse));
      });
  }

  loadSprints = () => axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/unclosed`)
    .then((sprints) => {
      this.setSprints(sprints);
    });

  loadVersions = () => axios
    .get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version`)
    .then((versions) => {
      this.setVersions(versions);
    });

  initData = (flag, pageType = 'usermap') => {
    this.setIsLoading(flag);
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/storymap/swim_lane`)
      .then((res) => {
        this.setMode(res);
        let axiosGetIssue = `/agile/v1/projects/${AppState.currentMenuType.id}/issues/storymap/issues?organizationId=${orgId}&type=${this.mode}&pageType=${pageType}`;
        if (this.getAssigneeFilterIds.length) {
          const currentAssignee = JSON.stringify(this.getAssigneeFilterIds).replace(/(]|\[)/g, '');
          axiosGetIssue += `&assigneeFilterIds=${currentAssignee}`;
        }
        axios.all([
          axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/storymap/epics?organizationId=${orgId}&showDoneEpic=${this.showDoneEpic}`),
          axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
            contents: [],
            filterName: '',
          }),
          axios.get(axiosGetIssue),
        ])
          .then(
            axios.spread((epics, filters, issues) => {
              this.setIsLoading(false);
              this.setFilters(filters);
              this.setEpics(epics);
              // this.setIssues(issues);
              if (this.mode === 'version') {
                const uniqIssues = _.uniqBy(_.orderBy(issues, ['versionId'], ['desc']), 'issueId');
                const sortedUniqIssues = _.orderBy(uniqIssues, 'mapRank', 'asc');
                this.setIssues(sortedUniqIssues);
                if (this.cacheIssues.length === 0 || sortedUniqIssues.length > this.cacheIssues.length) {
                  this.setCacheIssues(sortedUniqIssues);
                }
              } else {
                const sortedIssues = _.orderBy(issues, 'mapRank', 'asc');
                this.setIssues(sortedIssues);
                if (this.cacheIssues.length === 0 || sortedIssues.length > this.cacheIssues.length) {
                  this.setCacheIssues(sortedIssues);
                }
              }

              this.setCurrentNewObj({ epicId: 0, [`${this.mode}Id`]: 0 });
              // 两个请求现在都执行完成

              const arrAssignee = [];
              _.forEach(issues, (item) => {
                if (item.assigneeId && item.assigneeName) {
                  arrAssignee.push({
                    id: item.assigneeId,
                    realName: item.assigneeName,
                  });
                }
              });
              this.setAssigneeProps(_.map(_.union(_.map(arrAssignee, JSON.stringify)), JSON.parse));
            }),
          )
          .catch(() => {
            this.setIsLoading(false);
          });
        if (this.showBackLog) {
          this.loadBacklogIssues();
        }
        if (this.mode === 'version') {
          this.loadVersions();
        }
        if (this.mode === 'sprint') {
          this.loadSprints();
        }
      });
  }

  getFiltersObj = (type = 'currentBacklogFilters') => {
    const filters = this[type];
    let [userId, onlyStory, filterIds] = [null, null, []];
    if (filters.includes('mine')) {
      userId = AppState.getUserId;
    }
    if (filters.includes('story')) {
      onlyStory = true;
    }
    filterIds = filters.filter(v => v !== 'mine' && v !== 'story');
    return {
      userId,
      onlyStory,
      filterIds,
    };
  };

  getQueryString = (filterObj) => {
    let query = '';
    if (filterObj.onlyStory) {
      query += '&onlyStory=true';
    }
    if (filterObj.userId) {
      query += `&assigneeId=${filterObj.userId}`;
    }
    if (Array.isArray(filterObj.filterIds) && filterObj.filterIds.length) {
      query += `&quickFilterIds=${filterObj.filterIds.join(',')}`;
    }
    return query;
  };

  loadBacklogIssues = () => {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const type = this.mode;
    const filters = this.getFiltersObj('currentBacklogFilters');
    const query = this.getQueryString(filters);
    axios.get(`/agile/v1/projects/${projectId}/issues/storymap/issues?organizationId=${orgId}&type=${type}&pageType=backlog${query}`)
      .then((res) => {
        if (res.failed) {
          this.setBacklogIssues([]);
        } else if (this.mode === 'version') {
          const uniqIssues = _.uniqBy(_.orderBy(res, ['versionId'], ['desc']), 'issueId');
          const sortedUniqIssues = _.orderBy(uniqIssues, 'mapRank', 'asc');
          this.setBacklogIssues(sortedUniqIssues);
        } else {
          const sortedUniqIssues = _.orderBy(res, 'mapRank', 'asc');
          this.setBacklogIssues(sortedUniqIssues);
        }
        this.setBacklogExpand([]);

        const arrAssignee = [];
        _.forEach(res, (item) => {
          if (item.assigneeId && item.assigneeName) {
            arrAssignee.push({
              id: item.assigneeId,
              realName: item.assigneeName,
            });
          }
        });
        this.setAssigneeProps(_.map(_.union(_.map(arrAssignee, JSON.stringify)), JSON.parse));
      });
  };

  modifyEpic(issueId, epicName, objectVersionNumber) {
    const epicsCopy = _.cloneDeep(toJS(this.epics));
    const index = _.findIndex(epicsCopy, epic => epic.issueId === issueId);
    epicsCopy[index].epicName = epicName;
    epicsCopy[index].objectVersionNumber = objectVersionNumber;
    this.setEpics(epicsCopy);
  }

  freshIssue = (issueId, objectVersionNumber, summary) => {
    const index = this.issues.findIndex(issue => issue.issueId === issueId);
    const cacheIndex = this.cacheIssues.findIndex(issue => issue.issueId === issueId);
    if (index !== -1) {
      this.issues[index].objectVersionNumber = objectVersionNumber;
      this.issues[index].summary = summary;
    }
    if (cacheIndex !== -1) {
      this.cacheIssues[cacheIndex].objectVersionNumber = objectVersionNumber;
      this.cacheIssues[cacheIndex].summary = summary;
    }
  };

  handleEpicDrag = data => axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/epic_drag`, data)
    .then((res) => {
      this.loadEpic();
    })
    .catch((error) => {
      this.loadEpic();
    });

  handleMoveIssue = (data, type = 'userMap') => axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/storymap/move`, data)
    .then((res) => {
      if (type === 'userMap' && this.showBackLog) {
        this.loadIssues('usermap', false);
        this.loadBacklogIssues();
      } else if (type === 'userMap' && !this.showBackLog) {
        this.loadIssues('usermap', false);
      } else {
        this.loadBacklogIssues();
      }
    });

  deleteIssue = (issueId) => {
    const issue = _.find(this.issues, v => v.issueId === issueId);
    const key = `${this.mode}Id`;
    const postData = {
      before: false,
      outsetIssueId: 0,
      rankIndex: null,
      issueIds: [issueId],
      versionIssueIds: this.mode === 'version' && issue[key] ? [issueId] : undefined,
      versionId: this.mode === 'version' && issue[key] ? 0 : undefined,
      sprintIssueIds: this.mode === 'sprint' && issue[key] ? [issueId] : undefined,
      sprintId: this.mode === 'sprint' && issue[key] ? 0 : undefined,
      epicIssueIds: [issueId],
      epicId: 0,
    };
    const resIssues = _.clone(toJS(this.issues));
    const issueIndex = resIssues.findIndex(v => v.issueId === issueId);
    if (issueIndex) {
      resIssues.splice(issueIndex, 1);
    }
    this.setIssues(resIssues);
    // let issues;
    // let len;
    // const tarData = _.cloneDeep(toJS(this.issues));
    // const index = _.findIndex(tarData, issue => issue.issueId === issueId);
    // if (this.mode === 'none') {
    //   issues = this.backlogIssues;
    //   len = issues.length;
    //   if (issues && !len) {
    //     obj.before = true;
    //     obj.outsetIssueId = 0;
    //   } else {
    //     obj.outsetIssueId = issues[len - 1].issueId;
    //   }
    //   tarData[index].epicId = 0;
    // } else {
    //   obj[`${this.mode}Id`] = 0;
    //   issues = this.backlogIssues.filter(v => v[`${this.mode}Id`] === null);
    //   len = issues.length;
    //   obj.outsetIssueId = issues[len - 1].issueId;
    //   tarData[index].epicId = 0;
    //   tarData[`${this.mode}Id`] = 0;
    // }
    // this.setIssues(tarData);
    this.handleMoveIssue(postData);
  }

  @computed get getIssueTypes() {
    return this.issueTypes.slice();
  }

  @action setIssueTypes(data) {
    this.issueTypes = data;
  }

  axiosGetIssueTypes() {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${proId}/schemes/query_issue_types_with_sm_id?apply_type=agile`).then((data) => {
      if (data && !data.failed) {
        this.setIssueTypes(data);
      } else {
        this.setIssueTypes([]);
      }
    });
  }

  @computed get getDefaultPriority() {
    return this.defaultPriority;
  }

  @action setDefaultPriority(data) {
    this.defaultPriority = data;
  }

  axiosGetDefaultPriority() {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${proId}/priority/default`).then((data) => {
      if (data && !data.failed) {
        this.setDefaultPriority(data);
      } else {
        this.setDefaultPriority(false);
      }
    });
  }
}

const userMapStore = new UserMapStore();
export default userMapStore;
