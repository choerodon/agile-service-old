import axios from 'axios';
import {
  observable, action, computed, toJS, reaction,
} from 'mobx';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('BacklogStore')
class BacklogStore {
  // issue
  @observable issue = {};

  @action setIssue(data) {
    this.issue = data;
  }

  @computed get getIssue() {
    return this.issue;
  }

  // fields
  @observable fields = [];

  @action setIssueFields(issue, fields) {
    this.fields = fields;
    this.issue = issue;
  }

  @computed get getFields() {
    return this.fields;
  }

  // issue attribute
  @observable wiki = {};

  @observable workLogs = [];

  @observable dataLogs = [];

  @observable linkIssues = [];

  @observable branches = {};

  @action setWiki(data) {
    this.wiki = data;
  }

  @computed get getWiki() {
    return this.wiki;
  }

  @action setWorkLogs(data) {
    this.workLogs = data;
  }

  @computed get getWorkLogs() {
    return this.workLogs.slice();
  }

  @action setDataLogs(data) {
    this.dataLogs = data;
  }

  @computed get getDataLogs() {
    return this.dataLogs;
  }

  @action setLinkIssues(data) {
    this.linkIssues = data;
  }

  @computed get getLinkIssues() {
    return this.linkIssues;
  }

  @action setBranches(data) {
    this.branches = data;
  }

  @computed get getBranches() {
    return this.branches;
  }

  @action initIssueAttribute(wiki, workLogs, dataLogs, linkIssues, branches) {
    this.wiki = wiki;
    this.workLogs = workLogs;
    this.dataLogs = dataLogs;
    this.linkIssues = linkIssues;
    this.branches = branches;
  }

  @observable createdSprint = '';

  @observable hasActiveSprint = false;

  @observable issueCantDrag = false;

  @observable epicFilter = {};

  @observable versionFilter = {};

  @observable filter = { advancedSearchArgs: {} };

  @observable filterSelected = false;

  @observable epicList = [];

  @observable featureList = [];

  @observable epicMap = observable.map();

  @observable selectedIssueId = [];

  @observable issueMap = observable.map();

  @observable currentDrag = observable.map();

  @observable showRealQuickSearch = true;

  @observable ctrlClicked = false;

  @observable shiftClicked = false;

  @observable multiSelected = observable.map();

  @observable prevClickedIssue = null;

  @observable loadCompleted = false;

  @observable spinIf = false;

  @observable whichVisible = '';

  @observable sprintData = {};

  @observable versionData = [];

  @observable epicData = [];

  @observable chosenVersion = 'all';

  @observable chosenEpic = 'all';

  @observable chosenFeature = 'all';

  @observable onlyMe = false;

  @observable recent = false;

  @observable isDragging = '';

  @observable isLeaveSprint = false;

  @observable clickIssueDetail = {};

  @observable clickIssueId = null;

  @observable sprintCompleteMessage = {};

  @observable openSprintDetail = {};

  @observable sprintWidth;

  @observable colorLookupValue = [];

  @observable quickFilters = [];

  @observable projectInfo = {};

  @observable quickSearchList = [];

  @observable selectIssues = [];

  @observable more = false;

  @observable workSetting = {
    saturdayWork: false,
    sundayWork: false,
    useHoliday: false,
    timeZoneWorkCalendarDTOS: [],
    workHolidayCalendarDTOS: [],
  };

  @observable issueTypes = [];

  @observable defaultPriority = false;

  @observable cleanQuickSearch = false;

  @observable newIssueVisible = false;

  @computed get getNewIssueVisible() {
    return this.newIssueVisible;
  }

  @action setNewIssueVisible(data) {
    this.newIssueVisible = data;
  }

  @computed get asJson() {
    return {
      sprintData: this.sprintData,
      versionData: this.versionData,
      epicData: this.epicData,
      chosenVersion: this.chosenVersion,
      chosenEpic: this.chosenEpic,
      onlyMe: this.onlyMe,
      recent: this.recent,
      isDragging: this.isDragging,
      isLeaveSprint: this.isLeaveSprint,
      clickIssueDetail: this.clickIssueDetail,
      sprintCompleteMessage: this.sprintCompleteMessage,
      openSprintDetail: this.openSprintDetail,
      sprintWidth: this.sprintWidth,
      colorLookupValue: this.colorLookupValue,
      quickFilters: this.quickFilters,
      projectInfo: this.projectInfo,
      quickSearchList: this.quickSearchList,
      selectIssues: this.selectIssues,
    };
  }

  saveHandler = reaction(
    // 观察在 JSON 中使用了的任何东西:
    () => this.asJson,
    // 如何 autoSave 为 true, 把 json 发送到服务端
    (json, reactions) => {
      reactions.dispose();
    },
  );

  dispose() {
    // 清理观察者
    this.saveHandler();
  }

  @computed get getSelectIssue() {
    return this.selectIssues;
  }

  @action setSelectIssue(data) {
    this.selectIssues = data;
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

  @computed get getQuickFilters() {
    return toJS(this.quickFilters);
  }

  @action setQuickSearchClean(data) {
    this.cleanQuickSearch = data;
  }

  @computed get getQuickSearchClean() {
    return toJS(this.cleanQuickSearch);
  }

  @observable assigneeProps = [];

  @computed get getAssigneeProps() {
    return this.assigneeProps;
  }

  @action hideQuickSearch() {
    this.showRealQuickSearch = false;
  }

  @action showQuickSearch() {
    this.showRealQuickSearch = true;
  }

  @computed get getShowRealQuickSearch() {
    return this.showRealQuickSearch;
  }

  @action setAssigneeProps(data) {
    this.assigneeProps = data;
  }

  getSprintFilter() {
    const data = {
      advancedSearchArgs: {},
    };
    if (this.chosenEpic !== 'all') {
      if (this.chosenEpic === 'unset') {
        data.advancedSearchArgs.noEpic = 'true';
      } else {
        data.advancedSearchArgs.epicId = this.chosenEpic;
      }
    }
    if (this.chosenVersion !== 'all') {
      if (this.chosenVersion === 'unset') {
        data.advancedSearchArgs.noVersion = 'true';
      } else {
        data.advancedSearchArgs.versionId = this.chosenVersion;
      }
    }
    if (this.onlyMe) {
      data.advancedSearchArgs.ownIssue = 'true';
    }
    if (this.recent) {
      data.advancedSearchArgs.onlyStory = 'true';
    }
    return data;
  }

  axiosDeleteSprint(id) {
    return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/${id}`);
  }

  axiosGetColorLookupValue() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/lookup_values/epic_color`);
  }

  @computed get getColorLookupValue() {
    return this.colorLookupValue;
  }

  @action setColorLookupValue(data) {
    this.colorLookupValue = data;
  }

  @computed get getSprintWidth() {
    return this.sprintWidth;
  }

  @action setSprintWidth() {
    this.sprintWidth = document.getElementsByClassName('c7n-backlog-sprint')[0].offsetWidth;
  }

  axiosGetIssueDetail(issueId) {
    const orgId = AppState.currentMenuType.organizationId;
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/${issueId}${orgId ? `?organizationId=${orgId}` : ''}`);
  }

  @computed get getOpenSprintDetail() {
    return toJS(this.openSprintDetail);
  }

  @action setOpenSprintDetail(data) {
    this.openSprintDetail = data;
  }

  axiosGetOpenSprintDetail(sprintId) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/${sprintId}`);
  }

  axiosStartSprint(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/start`, data);
  }

  axiosCloseSprint(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/complete`, data);
  }

  @computed get getSprintCompleteMessage() {
    return toJS(this.sprintCompleteMessage);
  }

  @action setSprintCompleteMessage(data) {
    this.sprintCompleteMessage = data;
  }

  axiosGetSprintCompleteMessage(sprintId) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/${sprintId}/names`);
  }

  axiosEasyCreateIssue(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues?applyType=agile`, data);
  }

  @computed get getOnlyMe() {
    return this.onlyMe;
  }

  @action setOnlyMe(data) {
    this.onlyMe = data;
  }

  @computed get getRecent() {
    return this.recent;
  }

  @action setRecent(data) {
    this.recent = data;
  }

  axiosUpdateSprint(data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint`, data);
  }

  axiosUpdateVerison(versionId, data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/update/${versionId}`, data);
  }

  @computed get getClickIssueDetail() {
    return this.clickIssueDetail;
  }

  @computed get getClickIssueId() {
    return toJS(this.clickIssueId);
  }

  @computed get getPrevClickedIssue() {
    return this.prevClickedIssue;
  }

  axiosUpdateIssuesToVersion(versionId, ids) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/to_version/${versionId}`, ids);
  }

  axiosUpdateIssuesToEpic(epicId, ids) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/to_epic/${epicId}`, ids);
  }

  @computed get getIsLeaveSprint() {
    return this.isLeaveSprint;
  }

  @action setIsLeaveSprint(data) {
    this.isLeaveSprint = data;
  }

  @computed get getIsDragging() {
    return this.isDragging;
  }

  @action setIsDragging(data) {
    this.isDragging = data;
  }

  axiosCreateSprint(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint`, data);
  }

  axiosUpdateIssuesToSprint(sprintId, data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/to_sprint/${sprintId}`, data);
  }

  axiosUpdateIssue(data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/issues`, data);
  }

  @computed get getChosenVersion() {
    return this.chosenVersion;
  }

  @action setChosenVersion(data) {
    if (data === 'all') {
      this.filterSelected = false;
    }
    this.spinIf = true;
    this.addToVersionFilter(data);
    this.chosenVersion = data;
  }

  @computed get getChosenEpic() {
    return this.chosenEpic;
  }

  @action setChosenEpic(data) {
    if (data === 'all') {
      this.filterSelected = false;
    }
    this.spinIf = true;
    this.addToEpicFilter(data);
    this.chosenEpic = data;
  }

  @computed get getChosenFeature() {
    return this.chosenFeature;
  }

  @action setChosenFeature(data) {
    if (data === 'all') {
      this.filterSelected = false;
    }
    this.spinIf = true;
    this.addToFeatureFilter(data);
    this.chosenFeature = data;
  }

  @action setEpicData(data) {
    this.epicList = data;
  }

  @action setFeatureData(data) {
    this.featureList = data;
  }

  axiosGetEpic() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/epics`);
  }

  axiosGetVersion() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version`);
  }

  @action setSprintData({ backlogData, sprintData }) {
    this.spinIf = false;
    // this.multiSelected = observable.map();
    this.issueMap.set('0', backlogData.backLogIssue);
    this.backlogData = backlogData;
    sprintData.forEach((sprint) => {
      this.issueMap.set(sprint.sprintId.toString(), sprint.issueSearchDTOList);
    });
    this.sprintData = sprintData;
  }

  @computed get getQuickSearchList() {
    return toJS(this.quickSearchList);
  }

  @action setQuickSearchList(data) {
    this.quickSearchList = data;
  }

  axiosGetQuickSearchList() {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/query_all`, {
      contents: [],
      filterName: '',
    });
  }

  @observable assigneeFilterIds = [];

  @computed get getAssigneeFilterIds() {
    return this.assigneeFilterIds;
  }

  @action setAssigneeFilterIds(data) {
    this.spinIf = true;
    this.filterSelected = true;
    this.assigneeFilterIds = data;
  }

  axiosGetSprint = () => {
    const orgId = AppState.currentMenuType.organizationId;
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/issues?organizationId=${orgId}&quickFilterIds=${this.quickFilters}${this.assigneeFilterIds.length > 0 ? `&assigneeFilterIds=${this.assigneeFilterIds}` : ''}`, this.filter);
  }

  handleEpicDrap = data => axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/epic_drag`, data);


  handleVersionDrap = data => axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/drag`, data);

  @action setWorkSetting(data) {
    this.workSetting = data;
  }

  @computed get getWorkSetting() {
    return this.workSetting;
  }

  axiosGetWorkSetting(year) {
    const proId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${proId}/sprint/time_zone_detail/${orgId}?year=${year}`).then((data) => {
      if (data) {
        this.setWorkSetting(data);
      }
    });
  }

  @computed get getMore() {
    return this.more;
  }

  @action setMore() {
    this.more = !this.more;
  }

  @computed get getIssueTypes() {
    return this.issueTypes;
  }

  axiosGetIssueTypes() {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${proId}/schemes/query_issue_types_with_sm_id?apply_type=agile`);
  }

  @computed get getDefaultPriority() {
    return this.defaultPriority;
  }

  @action setDefaultPriority(data) {
    this.defaultPriority = data;
  }

  axiosGetDefaultPriority() {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${proId}/priority/default`);
  }

  checkSprintName(proId, name) {
    return axios.get(`/agile/v1/projects/${proId}/sprint/check_name?sprintName=${name}`);
  }

  @action setSpinIf(data) {
    this.loadCompleted = false;
    this.spinIf = data;
  }

  @computed get getSpinIf() {
    return this.spinIf;
  }

  @action initBacklogData(quickSearchData, issueTypesData, priorityArrData, { backlogData, sprintData }) {
    this.issueCantDrag = false;
    this.onBlurClick();
    // this.multiSelected = observable.map();
    this.quickSearchList = quickSearchData;
    if (issueTypesData && !issueTypesData.failed) {
      this.issueTypes = issueTypesData;
    }
    if (priorityArrData && !priorityArrData.failed) {
      this.defaultPriority = priorityArrData;
    }
    this.issueMap.set('0', backlogData.backLogIssue ? backlogData.backLogIssue : []);
    this.backlogData = backlogData;
    sprintData.forEach((sprint, index) => {
      if (sprint.sprintId === this.createdSprint) {
        // eslint-disable-next-line no-param-reassign
        sprintData[index].isCreated = true;
      }
      this.issueMap.set(sprint.sprintId.toString(), sprint.issueSearchDTOList);
    });
    this.sprintData = sprintData;
    this.hasActiveSprint = Boolean(sprintData.find(element => element.statusCode === 'started'));
    this.loadCompleted = true;
    this.spinIf = false;
  }

  @computed get getHasActiveSprint() {
    return this.hasActiveSprint;
  }

  @computed get getLoadCompleted() {
    return this.loadCompleted;
  }

  @computed get getBacklogData() {
    return this.backlogData;
  }

  @computed get getSprintData() {
    return this.sprintData;
  }

  @action toggleVisible(data) {
    this.whichVisible = data;
  }

  @computed get getCurrentVisible() {
    return this.whichVisible;
  }

  checkStartAndEnd = (prevIndex, currentIndex) => (prevIndex > currentIndex ? [currentIndex, prevIndex] : [prevIndex, currentIndex]);

  @action dealWithMultiSelect(sprintId, currentClick, type) {
    const data = this.issueMap.get(sprintId);
    const currentIndex = data.findIndex(issue => currentClick.issueId === issue.issueId);
    if (this.prevClickedIssue && this.prevClickedIssue.sprintId === currentClick.sprintId) {
      // 如果以后想利用 ctrl 从多个冲刺中选取 issue，可以把判断条件2直接挪到 shift 上
      // 但是请考虑清楚操作多个数组可能带来的性能开销问题
      if (type === 'shift') {
        this.dealWithShift(data, currentIndex);
      } else if (type === 'ctrl') {
        this.dealWithCtrl(data, currentIndex, currentClick);
      }
    } else {
      this.clickedOnce(currentClick, currentIndex);
    }
  }

  @action dealWithShift(data, currentIndex) {
    const [startIndex, endIndex] = this.checkStartAndEnd(this.prevClickedIssue.index, currentIndex);
    for (let i = startIndex; i <= endIndex; i += 1) {
      this.multiSelected.set(data[i].issueId, data[i]);
    }
  }

  @action dealWithCtrl(data, currentIndex, item) {
    if (this.multiSelected.has(item.issueId)) {
      const prevClickedStatus = this.multiSelected.get(item.issueId);
      if (prevClickedStatus) {
        this.multiSelected.delete(item.issueId);
      } else {
        this.multiSelected.set(item.issueId, item);
      }
    } else {
      this.multiSelected.set(data[currentIndex].issueId, data[currentIndex]);
    }
    this.prevClickedIssue = {
      ...item,
      index: currentIndex,
    };
  }

  @action clickedOnce(sprintId, currentClick) {
    const index = this.issueMap.get(sprintId).findIndex(issue => issue.issueId === currentClick.issueId);
    this.multiSelected = observable.map();
    this.multiSelected.set(currentClick.issueId, currentClick);
    this.prevClickedIssue = {
      ...currentClick,
      index,
    };
    this.setClickIssueDetail(currentClick);
  }

  @action setClickIssueDetail(data) {
    this.clickIssueDetail = data;
    if (this.clickIssueDetail) {
      this.clickIssueId = data.issueId;
    }
  }

  @computed get getMultiSelected() {
    return this.multiSelected;
  }

  @action setCurrentDrag(issueId) {
    this.currentDrag.set(issueId, true);
  }

  @computed get getCurrentDrag() {
    return this.currentDrag;
  }

  @action resetData() {
    this.createdSprint = '';
    this.issueMap = observable.map();
    this.whichVisible = null;
    this.assigneeFilterIds = [];
    this.multiSelected = observable.map();
    this.sprintData = {};
    this.clickIssueDetail = {};
  }

  @computed get getIssueMap() {
    return this.issueMap;
  }

  getModifiedArr = (dragItem, type) => {
    const result = [];
    if (!this.multiSelected.has(dragItem.issueId) || type === 'single') {
      result.push(dragItem.issueId);
    }
    if (type === 'multi') {
      result.push(...this.multiSelected.keys());
    }
    return result;
  };

  findOutsetIssue = (sourceIndex, destinationIndex, sourceId, destinationId, destinationArr) => {
    // 看不懂就去让后端给你逐字逐句解释去, 解释不通就怼他
    if (sourceId === destinationId) {
      if (sourceIndex < destinationIndex) {
        return destinationArr[destinationIndex];
      } else if (destinationIndex === 0) {
        return destinationArr[destinationIndex];
      } else {
        return destinationArr[destinationIndex - 1];
      }
    } else if (destinationIndex === 0 && destinationArr.length) {
      return destinationArr[destinationIndex];
    } else {
      return destinationArr[destinationIndex - 1];
    }
  };

  @action moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, issueItem, type) {
    const sourceArr = this.issueMap.get(sourceId);
    // const revertSourceArr = sourceArr.slice();
    const destinationArr = this.issueMap.get(destinationId);
    // const revertDestinationArr = destinationArr.slice();
    const prevIssue = this.findOutsetIssue(sourceIndex, destinationIndex, sourceId, destinationId, destinationArr);
    const modifiedArr = this.getModifiedArr(issueItem, type);

    if (type === 'single') {
      sourceArr.splice(sourceIndex, 1);
      destinationArr.splice(destinationIndex, 0, issueItem);
      this.issueMap.set(sourceId, sourceArr);
      this.issueMap.set(destinationId, destinationArr);
    } else if (type === 'multi') {
      const modifiedSourceArr = sourceArr.filter(issue => !this.multiSelected.has(issue.issueId));
      destinationArr.splice(destinationIndex, 0, ...[...this.multiSelected.values()]);
      if (!this.multiSelected.has(issueItem.issueId)) {
        modifiedSourceArr.splice(sourceIndex, 1);
        destinationArr.unshift(issueItem);
      }
      if (sourceId === destinationId) {
        const dragInSingleSprint = sourceArr.filter(issue => !this.multiSelected.has(issue.issueId));
        dragInSingleSprint.splice(destinationIndex, 0, ...[...this.multiSelected.values()]);
        this.issueMap.set(destinationId, dragInSingleSprint);
      } else {
        this.issueMap.set(sourceId, modifiedSourceArr);
        this.issueMap.set(destinationId, destinationArr);
      }
    }
    // this.multiSelected = observable.map();
    // this.clickIssueDetail = {};
    this.onBlurClick();
    return axios.post(`agile/v1/projects/${AppState.currentMenuType.id}/issues/to_sprint/${destinationId}`, {
      before: destinationIndex === 0,
      issueIds: modifiedArr,
      outsetIssueId: prevIssue ? prevIssue.issueId : 0,
      rankIndex: destinationId * 1 === 0 || (destinationId === sourceId && destinationId !== 0),
    }).then(this.axiosGetSprint).then((res) => {
      this.setSprintData(res);
    });
  }

  @action setIssueWithEpicOrVersion(item) {
    this.selectedIssueId = this.getModifiedArr(item, this.multiSelected.size > 1 ? 'multi' : 'single');
  }

  @computed get getIssueWithEpicOrVersion() {
    return this.selectedIssueId;
  }

  @action createIssue(issue, sprintId, { backlogData, sprintData }) {
    this.backlogData = backlogData;
    this.sprintData = sprintData;
    this.clickIssueDetail = issue;
    if (this.clickIssueDetail) {
      this.clickIssueId = issue.issueId;
    }
    const modifiedArr = [...this.issueMap.get(sprintId), issue];
    this.issueMap.set(sprintId, modifiedArr);
  }

  @action addEpic(data) {
    this.epicList.unshift(data);
  }

  @action initEpicList(epiclist, { lookupValues }) {
    this.colorLookupValue = lookupValues;
    this.epicList = epiclist;
  }

  @computed get getEpicData() {
    return this.epicList;
  }

  @computed get getFeatureData() {
    return this.featureList;
  }

  @action updateEpic(epic) {
    const updateIndex = this.epicList.findIndex(item => epic.issueId === item.issueId);
    this.epicList[updateIndex].name = epic.name;
    this.epicList[updateIndex].objectVersionNumber = epic.objectVersionNumber;
  }

  @action moveEpic(sourceIndex, destinationIndex) {
    const movedItem = this.epicList[sourceIndex];
    const { issueId, objectVersionNumber } = movedItem;
    this.epicList.splice(sourceIndex, 1);
    this.epicList.splice(destinationIndex, 0, movedItem);
    const req = {
      beforeSequence: destinationIndex !== 0 ? this.epicList[destinationIndex - 1].epicSequence : null,
      afterSequence: destinationIndex !== (this.epicList.length - 1) ? this.epicList[destinationIndex + 1].epicSequence : null,
      epicId: issueId,
      objectVersionNumber,
    };
    this.handleEpicDrap(req).then(
      action('fetchSuccess', (res) => {
        if (!res.message) {
          this.axiosGetEpic().then((epics) => {
            this.setEpicData(epics);
          });          
        } else {
          this.epicList.splice(destinationIndex, 1);
          this.epicList.splice(sourceIndex, 0, movedItem);
        }
      }),
    );
  }

  @action addVersion(data) {
    this.versionData.unshift(data);
  }

  @action setVersionData(data) {
    this.versionData = data.sort((a, b) => b.sequence - a.sequence);
  }

  @computed get getVersionData() {
    return this.versionData;
  }

  @action updateVersion(version, type) {
    const updateIndex = this.versionData.findIndex(item => item.versionId === version.versionId);
    if (type === 'name') {
      this.versionData[updateIndex].name = version.name;
    } else if (type === 'description') {
      this.versionData[updateIndex].description = version.description;
    } else if (type === 'date') {
      this.versionData[updateIndex].startDate = version.startDate;
      this.versionData[updateIndex].expectReleaseDate = version.expectReleaseDate;
    }
    this.versionData[updateIndex].objectVersionNumber = version.objectVersionNumber;
  }

  @action moveVersion(sourceIndex, destinationIndex) {
    const movedItem = this.versionData[sourceIndex];
    const { versionId, objectVersionNumber } = movedItem;
    this.versionData.splice(sourceIndex, 1);
    this.versionData.splice(destinationIndex, 0, movedItem);
    const req = {
      beforeSequence: destinationIndex !== 0 ? this.versionData[destinationIndex - 1].sequence : null,
      afterSequence: destinationIndex !== (this.versionData.length - 1) ? this.versionData[destinationIndex + 1].sequence : null,
      versionId,
      objectVersionNumber,
    };
    this.handleVersionDrap(req).then(
      action('fetchSuccess', (res) => {        
        if (!res.message) {
          this.axiosGetVersion().then((versions) => {
            this.setVersionData(versions);
          });
        } else {
          this.versionData.splice(destinationIndex, 1);
          this.versionData.splice(sourceIndex, 0, movedItem);
        }
      }),
    );
  }

  @action addToEpicFilter(data) {
    this.filterSelected = true;
    if (data === 'unset') {
      delete this.filter.advancedSearchArgs.epicId;
      this.filter.advancedSearchArgs.noEpic = 'true';
    } else if (typeof data === 'number') {
      delete this.filter.advancedSearchArgs.noEpic;
      this.filter.advancedSearchArgs.epicId = data;
    } else {
      delete this.filter.advancedSearchArgs.noEpic;
      delete this.filter.advancedSearchArgs.epicId;
    }
  }

  @action addToFeatureFilter(data) {
    this.filterSelected = true;
    if (data === 'unset') {
      delete this.filter.advancedSearchArgs.featureId;
      this.filter.advancedSearchArgs.noFeature = 'true';
    } else if (typeof data === 'number') {
      delete this.filter.advancedSearchArgs.noFeature;
      this.filter.advancedSearchArgs.featureId = data;
    } else {
      delete this.filter.advancedSearchArgs.noFeature;
      delete this.filter.advancedSearchArgs.featureId;
    }
  }

  @action addToVersionFilter(data) {
    this.filterSelected = true;
    if (data === 'unset') {
      delete this.filter.advancedSearchArgs.versionId;
      this.filter.advancedSearchArgs.noVersion = 'true';
    } else if (typeof data === 'number') {
      delete this.filter.advancedSearchArgs.noVersion;
      this.filter.advancedSearchArgs.versionId = data;
    } else {
      this.filterSelected = false;
      delete this.filter.advancedSearchArgs.noVersion;
      delete this.filter.advancedSearchArgs.versionId;
    }
  }

  @action resetFilter() {
    this.spinIf = true;
    this.filter = { advancedSearchArgs: {} };
    this.versionFilter = 'all';
    this.epicFilter = 'all';
    this.quickFilters = [];
    this.assigneeFilterIds = [];
  }

  @computed get hasFilter() {
    return this.filterSelected;
  }

  @action clearSprintFilter() {
    this.filterSelected = false;
    this.resetFilter();
    this.axiosGetSprint().then(action('fetchSuccess', (res) => {
      this.setSprintData(res);
    }));
  }

  @action setQuickFilters(onlyMeChecked, onlyStoryChecked, moreChecked = []) {
    this.spinIf = true;
    if (onlyMeChecked) {
      this.filter.advancedSearchArgs.ownIssue = 'true';
      this.filterSelected = true;
    } else {
      delete this.filter.advancedSearchArgs.ownIssue;
    }
    if (onlyStoryChecked) {
      this.filter.advancedSearchArgs.onlyStory = 'true';
      this.filterSelected = true;
    } else {
      delete this.filter.advancedSearchArgs.onlyStory;
    }
    this.quickFilters = moreChecked;
    if (moreChecked.length) {
      this.filterSelected = true;
    }
    // 如果一个都没选，则不显示清空
    if (!onlyMeChecked && !onlyStoryChecked && !moreChecked.length) {
      this.filterSelected = false;
    }
  }

  @action toggleIssueDrag(data) {
    this.issueCantDrag = data;
  }

  @computed get getIssueCantDrag() {
    return this.issueCantDrag;
  }

  @action onBlurClick() {
    this.multiSelected = observable.map();
    if (this.clickIssueDetail && this.clickIssueDetail.issueId) {
      this.multiSelected.set(this.clickIssueDetail.issueId, this.clickIssueDetail);
    }
  }

  @action clearMultiSelected() {
    this.multiSelected = observable.map();
  }

  @action setCreatedSprint(data) {
    this.createdSprint = data;
  }
}

const backlogStore = new BacklogStore();
export default backlogStore;
