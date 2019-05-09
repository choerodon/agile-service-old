import {
  observable, action, computed, toJS,
} from 'mobx';
import axios from 'axios';
import { find, findIndex } from 'lodash';
import { stores } from 'choerodon-front-boot';
import { loadBoardData, sortColumn, deleteColumn } from '../../../api/BoardApi';

const { AppState } = stores;

class KanbanStore {
  // issue
  @observable issue = {};

  // fields
  @observable fields = [];

  @action setIssueFields(issue, fields) {
    this.fields = fields;
    this.issue = issue;
  }

  @computed get getFields() {
    return this.fields;
  }

  @computed get getIssue() {
    return this.issue;
  }

  // issue attribute
  @observable wiki = {};

  @observable dataLogs = [];

  @observable linkIssues = [];

  @action setWiki(data) {
    this.wiki = data;
  }

  @computed get getWiki() {
    return this.wiki;
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

  @action initIssueAttribute(wiki, dataLogs, linkIssues) {
    this.wiki = wiki;
    this.dataLogs = dataLogs;
    this.linkIssues = linkIssues;
  }

  @observable quickSearchObj = {
    onlyMe: false,
    onlyStory: false,
    quickSearchArray: [],
    assigneeFilterIds: [],
  };

  @observable allColumnCount = [];

  @observable calanderCouldUse = false;

  @observable currentSprintExist = true;

  @observable activePi = null;

  @observable prevClick = {};

  @observable currentDrag = {};

  @observable currentClick = 0;

  @observable translateToCompleted = [];

  @observable clickedIssue = false;

  @observable moveOverRef = {};

  @observable updateParent = false;

  @observable statusMap = new Map();

  @observable headerData = new Map();

  @observable updatedParentIssue = {
    statusId: 0,
    issueTypeId: 0,
  };

  @observable dragStartItem = {};

  @observable otherIssue = [];

  @observable dragStart = false;

  @observable swimLaneData = new Map();

  @observable canDragOn = new Map();

  @observable allDataMap = new Map();

  @observable boardData = [];

  @observable parentIds = [];

  @observable statusCategory = {};

  @observable boardList = new Map();

  @observable selectedBoardId = '';

  @observable unParentIds = [];

  @observable lookupValue = {
    constraint: [],
  };

  @observable stateMachineMap = {

  };

  @observable statusColumnMap = new Map();

  @observable spinIf = true;

  @observable dayRemain = 0;

  @observable sprintId = null;

  @observable sprintName = null;

  @observable interconnectedData = new Map();

  @observable parentId = [];

  @observable mapStructure = {};

  @observable currentConstraint = '';

  @observable currentSprint = {};

  @observable clickIssueDetail = {};

  @observable IssueNumberCount = {};

  @observable assigneer = [];

  @observable swimlaneBasedCode = null;

  @observable quickSearchList = [];

  @observable epicData = [];

  @observable allEpicData = [];

  @observable statusList = [];

  @observable parentIssueIdData = new Set();

  @observable otherQuestionCount = 0;

  @observable workSetting = {
    saturdayWork: false,
    sundayWork: false,
    useHoliday: false,
    timeZoneWorkCalendarDTOS: [],
    workHolidayCalendarDTOS: [],
  };

  @observable workDate = false;

  @observable parentCompleted = [];

  @observable issueTypes = [];

  @observable canAddStatus = true;

  @observable sprintData = true;

  @observable assigneeFilterIds = [];

  @observable isDragging = false;

  @observable createFeatureVisible = false;

  @computed get getIsDragging() {
    return this.isDragging;
  }

  @action setIsDragging(data) {
    this.isDragging = data;
  }

  @computed get getAssigneeFilterIds() {
    return this.assigneeFilterIds;
  }

  @action setAssigneeFilterIds(data) {
    this.assigneeFilterIds = data;
  }

  @computed get getSprintData() {
    return this.sprintData;
  }

  @action setSprintData(data) {
    this.sprintData = data;
  }

  @computed get getCanAddStatus() {
    return this.canAddStatus;
  }

  @action setCanAddStatus(data) {
    this.canAddStatus = data;
  }

  axiosCanAddStatus() {
    axios.get(`/issue/v1/projects/${AppState.currentMenuType.id}/schemes/check_create_status_for_agile?applyType=program`)
      .then((data) => {
        this.setCanAddStatus(data);
      })
      .catch((e) => {
        Choerodon.prompt(e.message);
      });
  }

  @computed get getStatusList() {
    return this.statusList.slice();
  }

  @action setStatusList(data) {
    this.statusList = data;
  }

  @computed get getAllEpicData() {
    return toJS(this.allEpicData);
  }

  @action setBoardParentIssueId(data) {
    data.forEach(item => item !== 0 && this.parentIssueIdData.add(item));
  }

  @computed get getBoardParentIssueId() {
    return toJS(this.parentIssueIdData);
  }

  // 其他问题计数 -- 临时逻辑
  @action addOtherQuestionCount() {
    this.otherQuestionCount += 1;
  }

  @action clearOtherQuestionCount() {
    this.otherQuestionCount = 0;
  }

  // 其他问题计数 -- 临时逻辑
  @computed get getOtherQuestionCount() {
    return this.otherQuestionCount;
  }

  @action setAllEpicData(data) {
    this.allEpicData = data;
  }

  axiosGetAllEpicData() {
    const orgId = AppState.currentMenuType.organizationId;
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/epics?organizationId=${orgId}`);
  }

  @computed get getEpicData() {
    return toJS(this.epicData);
  }

  @action setEpicData(data) {
    this.epicData = data;
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

  @computed get getSwimLaneCode() {
    return this.swimlaneBasedCode;
  }

  @action setSwimLaneCode(data) {
    this.swimlaneBasedCode = data;
  }

  @action setDragStart(data) {
    this.dragStart = data;
  }

  @computed get getDragStart() {
    return this.dragStart;
  }

  @computed get getAssigneer() {
    return toJS(this.assigneer);
  }

  @action setAssigneer(data) {
    this.assigneer = data;
  }

  @computed get getParentId() {
    return toJS(this.parentId);
  }

  @action setParentId(data) {
    this.parentId = data;
  }

  // @action resetClickedIssue() {
  //   this.currentClick = null;
  //   this.clickIssueDetail = {};
  //   this.clickedIssue = false;
  // }

  @computed get getClickedIssue() {
    return this.clickedIssue;
  }

  @action resetClickedIssue() {
    this.currentClick = 0;
    if (this.currentClickTarget) {
      this.currentClickTarget.style.backgroundColor = '#fff';
    }
    this.currentClickTarget = null;
    this.clickedIssue = false;
    this.clickIssueDetail = null;
  }

  @action setClickedIssue(issue, ref) {
    this.currentClick = issue.issueId;
    if (this.currentClickTarget && ref !== this.currentClickTarget) {
      this.currentClickTarget.style.backgroundColor = '#fff';
    }
    this.currentClickTarget = ref;
    this.clickIssueDetail = issue;
    this.clickedIssue = true;
  }

  @computed get getCurrentClickId() {
    return this.currentClick;
  }

  @computed get prevClickId() {
    return this.prevClick;
  }

  @action setMoveOverRef(data) {
    this.moveOverRef = data;
  }

  @computed get getMoveOverRef() {
    return this.moveOverRef;
  }

  @action judgeMoveParentToDone(destinationStatus, swimLaneId, parentId, statusIsDone) {
    const completedStatusIssueLength = Object.keys(this.swimLaneData[swimLaneId])
      .filter(statusId => this.statusMap.get(+statusId).completed === true)
      .map(statusId => this.swimLaneData[swimLaneId][+statusId].length)
      .reduce((accumulator, currentValue) => accumulator + currentValue);
    if (statusIsDone && completedStatusIssueLength === this.interconnectedData.get(parentId).subIssueData.length && this.interconnectedData.get(parentId).categoryCode !== 'done') {
      this.updatedParentIssue = this.interconnectedData.get(parentId);
      this.setTransFromData(this.updatedParentIssue, parentId);
    } else {
      this.interconnectedData.set(parentId, {
        ...this.interconnectedData.get(parentId),
        canMoveToComplish: false,
      });
    }
  }

  @action addAssigneeFilter(data) {
    this.quickSearchObj.assigneeFilterIds = data;
  }

  @action addQuickSearchFilter(onlyMeChecked = false, onlyStoryChecked = false, moreChecked = []) {
    this.quickSearchObj.onlyMe = onlyMeChecked;
    this.quickSearchObj.onlyStory = onlyStoryChecked;
    this.quickSearchObj.quickSearchArray = moreChecked;
  }

  @computed get hasSetFilter() {
    const {
      onlyMe, onlyStory, quickSearchArray, assigneeFilterIds,
    } = this.quickSearchObj;
    if (onlyMe === false && onlyStory === false && quickSearchArray.length === 0 && assigneeFilterIds.length === 0) {
      return true;
    }
    return false;
  }


  setTransFromData(parentIssue, parentId) {
    const projectId = AppState.currentMenuType.id;
    axios.get(
      `/issue/v1/projects/${projectId}/schemes/query_transforms?current_status_id=${parentIssue.statusId}&issue_id=${parentIssue.issueId}&issue_type_id=${parentIssue.issueTypeId}&apply_type=agile`,
    ).then(
      action('fetchSuccess', (res) => {
        this.updatedParentIssue = this.interconnectedData.get(parentId);
        this.translateToCompleted = res.filter(transform => transform.statusDTO.type === 'done');
        this.interconnectedData.set(+parentId, {
          ...this.interconnectedData.get(parentId),
          canMoveToComplish: true,
        });
        this.updateParent = true;
      }),
    );
  }

  @computed get getTransformToCompleted() {
    return this.translateToCompleted;
  }

  @computed get getUpdatedParentIssue() {
    return this.updatedParentIssue;
  }

  axiosUpdateIssueStatus(id, data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/${id}`, data);
  }

  axiosCheckRepeatName(name) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/board_column/check?statusName=${name}`);
  }

  axiosUpdateMaxMinNum(columnId, data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/board_column/${columnId}/column_contraint`, data);
  }

  @computed get getIssueNumberCount() {
    return toJS(this.IssueNumberCount);
  }

  @action updateParentIssueToDone(issueId, data) {
    this.interconnectedData.set(issueId, {
      ...this.updatedParentIssue,
      ...data,
    });
  }

  @action setIssueNumberCount(data) {
    this.IssueNumberCount = data;
  }

  @computed get getClickIssueDetail() {
    return this.clickIssueDetail;
  }

  @action resetCurrentClick(parentIssueId) {
    if (this.currentClickTarget) {
      this.currentClickTarget.style.backgroundColor = '#fff';
    }
    this.currentClickTarget = null;
    this.currentClick = parentIssueId;
    this.clickIssueDetail = this.allDataMap.get(parentIssueId);
  }

  @action resetDataBeforeUnmount() {
    this.spinIf = true;
    this.clickIssueDetail = {};
    this.swimLaneData = null;
    this.headerData = new Map();
    this.clickedIssue = false;
    // this.swimlaneBasedCode = null;
    this.quickSearchObj = {
      onlyMe: false,
      onlyStory: false,
      quickSearchArray: [],
      assigneeFilterIds: [],
    };
    this.currentSprintExist = false;
  }

  @computed get getDayRemain() {
    return toJS(this.dayRemain);
  }

  @action setCurrentSprint(data) {
    this.currentSprint = data;
  }

  @computed get getSprintId() {
    return this.sprintId;
  }

  @computed get getSprintName() {
    return this.sprintName;
  }

  axiosCreateBoard(name) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/board?boardName=${name}`);
  }

  axiosDeleteBoard() {
    return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/board/${this.selectedBoardId}`);
  }

  axiosUpdateBoardDefault(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/board/user_setting/${data.boardId}?swimlaneBasedCode=${data.swimlaneBasedCode}`, {});
  }

  axiosUpdateBoard(data) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/board/${this.selectedBoardId}`, data);
  }

  @computed get getCurrentConstraint() {
    return this.currentConstraint;
  }

  @action setCurrentConstraint(data) {
    this.currentConstraint = data;
  }

  axiosGetLookupValue(code) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/lookup_values/${code}`);
  }

  @computed get getLookupValue() {
    return toJS(this.lookupValue);
  }

  @action setLookupValue(data) {
    this.lookupValue = data;
  }

  @computed get getUnParentIds() {
    return toJS(this.unParentIds);
  }

  @action setUnParentIds(data) {
    this.unParentIds = data;
  }

  axiosUpdateColumn(columnId, data, boardId) {
    return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/board_column/${columnId}?boardId=${boardId}`, data);
  }

  @computed get getSelectedBoard() {
    return this.selectedBoardId;
  }

  @action setSelectedBoard(data) {
    this.currentSprintExist = false;
    this.selectedBoardId = data;
  }

  @computed get getBoardList() {
    return this.boardList;
  }

  @action setBoardList(key, data) {
    this.boardList.set(key, data);
  }

  @action rewriteCurrentConstraint({ columnConstraint, objectVersionNumber }, boardId, boardData) {
    this.currentConstraint = columnConstraint;
    this.boardList.set(boardId, {
      ...boardData,
      columnConstraint,
      objectVersionNumber,
    });
  }

  @action setBoardListData(boardListData, { boardId, userDefaultBoard, columnConstraint }) {
    this.boardList = boardListData;
    this.selectedBoardId = boardId;
    this.swimlaneBasedCode = userDefaultBoard;
    this.currentConstraint = columnConstraint;
  }

  axiosGetBoardList() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/board`);
  }

  @computed get getStatusCategory() {
    return toJS(this.statusCategory);
  }

  @action setStatusCategory(data) {
    this.statusCategory = data;
  }

  axiosGetStatusCategory() {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/lookup_values/status_category`);
  }

  @computed get getParentIds() {
    return toJS(this.parentIds);
  }

  @action setParentIds(data) {
    this.parentIds = data;
  }

  @computed get getParentCompleted() {
    return toJS(this.parentCompleted);
  }

  @action setParentCompleted(data) {
    this.parentCompleted = data;
  }

  @computed get getBoardData() {
    return toJS(this.boardData);
  }

  @action setBoardData(data) {
    this.boardData = data;
  }

  @observable assigneeProps = [];

  @computed get getAssigneeProps() {
    return this.assigneeProps;
  }

  @action setAssigneeProps(data) {
    this.assigneeProps = data;
  }

  @computed get getCalanderCouldUse() {
    return this.calanderCouldUse;
  }

  axiosUpdateColumnSequence(boardId, data) {
    return sortColumn(data);
  }

  axiosDeleteColumn(columnId) {
    return deleteColumn(columnId);
  }

  axiosAddColumn(categoryCode, data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/board_column?categoryCode=${categoryCode}&applyType=program`, data);
  }

  axiosAddStatus(data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status?applyType=program`, data);
  }

  // eslint-disable-next-line consistent-return
  axiosGetBoardDataBySetting(boardId) {
    return loadBoardData(boardId);
  }

  axiosGetBoardData(boardId) {
    // const {
    //   onlyMe, onlyStory, quickSearchArray, assigneeFilterIds,
    // } = this.quickSearchObj;
    return loadBoardData(boardId, this.quickSearchObj);
  }

  axiosFilterBoardData(boardId, assign, recent) {
    if (assign === 0) {
      return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/board/${boardId}/all_data_program/${AppState.currentMenuType.organizationId}?onlyStory=${recent}`);
    } else {
      return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/board/${boardId}/all_data_program/${AppState.currentMenuType.organizationId}?assigneeId=${assign}&onlyStory=${recent}`);
    }
  }

  axiosGetUnsetData(boardId) {
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/list_by_options?boardId=${boardId}&applyType=program`);
  }

  axiosStatusCanBeDelete(code) {
    return axios.get(`/issue/v1/projects/${AppState.currentMenuType.id}/schemes/check_remove_status_for_agile?status_id=${code}&applyType=program`);
  }

  axiosDeleteStatus(code) {
    return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/${code}?applyType=program`);
  }

  updateIssue = (
    {
      issueId, objectVersionNumber, boardId, originColumnId, columnId,
      before, sprintId, issueTypeId,
    }, startStatus, startStatusIndex, destinationStatus, destinationStatusIndex, SwimLaneId, piId, rank, piChange,
  ) => {
    const proId = AppState.currentMenuType.id;
    let outsetIssueId = '';

    if (destinationStatusIndex !== 0) {
      // 从另一个列拖过来传，目标位置-1的id
      if (startStatus !== destinationStatus) {
        outsetIssueId = this.swimLaneData[SwimLaneId][destinationStatus][destinationStatusIndex - 1].issueId;
        // 从同一列的前面拖过来传，目标位置的id
      } else if (startStatusIndex < destinationStatusIndex) {
        outsetIssueId = this.swimLaneData[SwimLaneId][destinationStatus][destinationStatusIndex].issueId;
        // 从同一列的后面拖过来传，目标位置-1的id
      } else if (startStatusIndex > destinationStatusIndex) {
        outsetIssueId = this.swimLaneData[SwimLaneId][destinationStatus][destinationStatusIndex - 1].issueId;
      }
    }
    // debugger;
    const data = {
      issueId,
      objectVersionNumber,
      statusId: destinationStatus,
      boardId: this.selectedBoardId,
      originColumnId: this.statusColumnMap.get(startStatus),
      columnId: this.statusColumnMap.get(destinationStatus),
      // before: destinationStatusIndex === 0,
      // outsetIssueId,
      piId, // 从准备拖到其他或从其他拖到其他传piId,其他情况传0，没有活跃也传0
      // rank,
      piChange,
    };
    const { id: transformId } = this.stateMachineMap[issueTypeId] ? this.stateMachineMap[issueTypeId][startStatus].find(issue => issue.endStatusId === parseInt(destinationStatus, 10)) : this.stateMachineMap[0][startStatus].find(issue => issue.endStatusId === parseInt(destinationStatus, 10));
    return axios.post(`/agile/v1/projects/${proId}/board/feature/${issueId}/move?transformId=${transformId}`, data);
  };

  moveStatusToUnset(code, data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/${code}/move_to_uncorrespond`, data);
  }

  moveStatusToColumn(code, data) {
    return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/${code}/move_to_column`, data);
  }

  @computed get getDragStartItem() {
    return this.dragStartItem;
  }

  @action setDragStartItem(data) {
    this.dragStartItem = data;
  }

  @action setWorkSetting(data) {
    this.workSetting = data;
  }

  @computed get getWorkSetting() {
    return this.workSetting;
  }

  // 查询组织层工作日历设置
  axiosGetWorkSetting(year) {
    const proId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    return axios.get(`/agile/v1/projects/${proId}/sprint/time_zone_detail/${orgId}?year=${year}`).then((data) => {
      if (data) {
        this.setWorkSetting(data);
      }
    });
  }

  axiosDeleteCalendarData(calendarId) {
    const proId = AppState.currentMenuType.id;
    return axios.delete(`/agile/v1/projects/${proId}/work_calendar_ref/${calendarId}`);
  }

  axiosCreateCalendarData(sprintId, data) {
    const proId = AppState.currentMenuType.id;
    return axios.post(`/agile/v1/projects/${proId}/work_calendar_ref/sprint/${sprintId}`, data);
  }

  @action setWorkDate(data) {
    this.calanderCouldUse = true;
    this.workDate = data;
  }

  @computed get getWorkDate() {
    return this.workDate;
  }

  // 获取项目层工作日历
  axiosGetCalendarData = (year) => {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/agile/v1/projects/${proId}/work_calendar_ref/sprint?year=${year}`).then((data) => {
      if (data) {
        this.setWorkDate(data);
      } else {
        this.setWorkDate(false);
      }
    }).catch(() => {
      this.setWorkDate(false);
    });
  };

  @computed get getIssueTypes() {
    return toJS(this.issueTypes);
  }

  @action setIssueTypes(data) {
    this.issueTypes = data;
  }

  axiosGetIssueTypes() {
    const proId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${proId}/schemes/query_issue_types_with_sm_id?apply_type=program`);
  }

  loadTransforms = (statusId, issueId, typeId) => {
    const projectId = AppState.currentMenuType.id;
    return axios.get(
      `/issue/v1/projects/${projectId}/schemes/query_transforms?current_status_id=${statusId}&issue_id=${issueId}&issue_type_id=${typeId}&apply_type=program`,
    );
  }

  loadStatus = () => {
    const projectId = AppState.currentMenuType.id;
    axios.get(`/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=program`).then((data) => {
      if (data && !data.failed) {
        this.setStatusList(data);
      } else {
        this.setStatusList([]);
      }
    }).catch(() => {
      this.setStatusList([]);
    });
  };

  axiosGetStateMachine = () => {
    const projectId = AppState.currentMenuType.id;
    return axios.get(`/issue/v1/projects/${projectId}/schemes/query_transforms_map?apply_type=program`);
  }

  axiosUpdateIssue(data) {
    const proId = AppState.currentMenuType.id;
    const { issueId, objectVersionNumber, transformId } = data;
    return axios.put(`/agile/v1/projects/${proId}/issues/update_status?applyType=program&transformId=${transformId}&issueId=${issueId}&objectVersionNumber=${objectVersionNumber}`);
  }

  // 校验看板名称是否重复
  checkBoardNameRepeat = (proId, name) => axios.get(
    `/agile/v1/projects/${proId}/board/check_name?boardName=${name}`,
  );

  // @action async scrumBoardInit(AppState, url) {
  //   try {
  //     const boardData = await this.axiosGetBoardList;
  //   } catch (e) {
  //     console.log(e);
  //   }
  // };

  @action setSpinIf(data) {
    // this.currentSprintExist = false;
    this.spinIf = data;
  }

  @computed get getSpinIf() {
    return this.spinIf;
  }

  @action scrumBoardInit(AppStates, url = null, boardListData = null, { boardId, userDefaultBoard, columnConstraint }, { currentSprint, allColumnNum = [], activePi }, quickSearchList, issueTypes, stateMachineMap, canDragOn, statusColumnMap, allDataMap, mapStructure, statusMap, renderData, headerData) {
    this.boardData = [];
    this.spinIf = false;
    // this.currentClick = 0;
    this.quickSearchList = [];
    this.sprintData = false;
    this.assigneer = [];
    this.parentIds = [];
    this.epicData = [];
    if (boardListData) {
      this.boardList = observable.map(boardListData.map(board => [board.boardId, board]));
    }
    this.selectedBoardId = boardId;
    this.swimlaneBasedCode = userDefaultBoard;
    this.currentConstraint = columnConstraint;
    this.quickSearchList = quickSearchList;
    this.allColumnCount = observable.map(allColumnNum.map(({ columnId, issueCount }) => [columnId, issueCount]));
    // if (currentSprint) {
    //   this.currentSprintExist = true;
    //   this.dayRemain = currentSprint.dayRemain;
    //   this.sprintId = currentSprint.sprintId;
    //   this.sprintName = currentSprint.sprintName;
    // } else {
    //   this.currentSprintExist = false;
    // }
    this.currentSprintExist = true;
    this.allDataMap = allDataMap;
    this.mapStructure = mapStructure;
    if (url && url.paramIssueId) {
      this.clickIssueDetail = { issueId: url.paramIssueId };
    }
    if (issueTypes && !issueTypes.failed) {
      this.issueTypes = issueTypes;
    } else {
      this.issueTypes = [];
    }
    this.stateMachineMap = stateMachineMap;
    this.canDragOn = observable.map(canDragOn);
    this.statusColumnMap = statusColumnMap;
    const { unInterConnectedDataMap, interConnectedDataMap, swimLaneData } = renderData;
    this.otherIssue = unInterConnectedDataMap;
    this.interconnectedData = observable.map(interConnectedDataMap);
    this.swimLaneData = swimLaneData;
    this.statusMap = observable.map(statusMap);
    this.headerData = observable.map(headerData);
    this.activePi = activePi;
  }

  @computed get getAllColumnCount() {
    return this.allColumnCount;
  }

  @computed get getHeaderData() {
    return this.headerData;
  }

  @computed get getActivePi() {
    return this.activePi;
  }

  @action resetHeaderData(startColumnId, destinationColumnId, issueType) {
    const startColumnData = this.headerData.get(startColumnId);
    const destinationColumnData = this.headerData.get(destinationColumnId);
    this.headerData.set(+startColumnId, {
      ...startColumnData,
      columnIssueCount: startColumnData.columnIssueCount - 1,
    });
    this.headerData.set(+destinationColumnId, {
      ...destinationColumnData,
      columnIssueCount: destinationColumnData.columnIssueCount + 1,
    });
    if (this.getAllColumnCount.size > 0) {
      this.setColumnConstrint(startColumnId, destinationColumnId, issueType);
    }
  }

  @action setColumnConstrint(startColumnId, destinationColumnId, issueType) {
    const startColumnCount = this.allColumnCount.get(startColumnId);
    const destinationColumnCount = this.allColumnCount.get(destinationColumnId);
    if ((this.currentConstraint === 'issue_without_sub_task' && issueType !== 'sub_task') || this.currentConstraint === 'issue') {
      this.allColumnCount.set(+startColumnId, startColumnCount - 1);
      this.allColumnCount.set(+destinationColumnId, destinationColumnCount + 1);
    }
  }

  @computed get getStateMachineMap() {
    return this.stateMachineMap;
  }

  @computed get getStatusMap() {
    return this.statusMap;
  }

  @computed get getMapStructure() {
    return this.mapStructure;
  }

  @computed get getCurrentDrag() {
    return this.currentDrag;
  }

  @action setCurrentDrag(data) {
    this.currentDrag = data;
  }

  @action setWhichCanNotDragOn(statusId, { id: typeId }) {
    [...this.canDragOn.keys()].forEach((status) => {
      if (this.stateMachineMap[typeId]) {
        if (this.stateMachineMap[typeId][statusId].find(issue => issue.endStatusId === status)) {
          this.canDragOn.set(status, false);
        } else {
          this.canDragOn.set(status, true);
        }
      } else if (this.stateMachineMap[0][statusId].find(issue => issue.endStatusId === status)) {
        this.canDragOn.set(status, false);
      } else {
        this.canDragOn.set(status, true);
      }
    });
  }

  @computed get getCanDragOn() {
    return this.canDragOn;
  }

  @computed get getCanDragOnToJS() {
    // console.log(toJS(this.canDragOn));
    return toJS(this.canDragOn);
  }

  @computed get getIssueWithStatus() {
    return toJS(this.interconnectedData);
  }

  @action setOtherQuestion({ parentDataMap }) {
    // 数组 单独管理
    this.otherIssue = parentDataMap;
  }

  @computed get getOtherQuestion() {
    return this.otherIssue;
  }

  @action setInterconnectedData(data) {
    // id - Map 相关联
    this.interconnectedData = data;
  }

  @computed get getInterconnectedData() {
    return this.interconnectedData;
  }

  @computed get didCurrentSprintExist() {
    return this.currentSprintExist;
  }

  @action resetCurrentSprintExist() {
    this.currentSprintExist = null;
  }

  @action resetCanDragOn() {
    [...this.canDragOn.keys()].forEach((status) => {
      this.canDragOn.set(status, false);
    });
  }

  @action setSwimLaneData(startSwimLane, startStatus, startStatusIndex, destinationSwimLane, destinationStatus, destinationStatusIndex, issue, revert) {
    const startLine = this.swimLaneData[startSwimLane][startStatus];
    const destinationLine = this.swimLaneData[startSwimLane][destinationStatus];
    const { issueId } = issue;
    // 插入
    if (!revert) {
      let index = 0;
      // eslint-disable-next-line prefer-destructuring
      const length = destinationLine.length;
      // 按id排序，插入到适当位置
      for (let i = 0; i < length; i += 1) {
        if (destinationLine[i].issueId < issueId && (!destinationLine[i + 1] || destinationLine[i + 1].issueId > issueId)) {
          index = i + 1;
          break;
        }
      }      
      startLine.splice(startStatusIndex, 1);
      destinationLine.splice(index, 0, {
        ...issue,
        statusId: destinationStatus,
      });
    } else {
      // 重置
      const index = findIndex(destinationLine, { issueId });
      if (index !== null) {
        destinationLine.splice(index, 1); 
        startLine.splice(destinationStatusIndex, 0, {
          ...issue,
          statusId: startStatus,
        });
      }
    }
  }

  @action rewriteObjNumber({ objectVersionNumber }, issueId, issue) {
    this.allDataMap.set(+issueId, {
      ...issue,
      objectVersionNumber,
    });
  }

  @computed get getSwimLaneData() {
    // swimLaneCode - Map 相关联
    return this.swimLaneData;
  }

  @computed get getAllDataMap() {
    return this.allDataMap;
  }

  @action setUpdateParent(data) {
    this.updateParent = data;
  }

  @computed get getUpdateParent() {
    return this.updateParent;
  }

  @action setEditRef(ref) {
    this.editRef = ref;
  }

  @computed get getEditRef() {
    return this.editRef;
  }

  @action setCreateFeatureVisible = (visible) => {
    this.createFeatureVisible = visible;
  }
}


export default new KanbanStore();
