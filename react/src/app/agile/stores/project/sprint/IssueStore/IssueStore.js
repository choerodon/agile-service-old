import {
  observable, action, computed, toJS,
} from 'mobx';
import { store, stores, axios } from 'choerodon-front-boot';
import moment from 'moment';
import _ from 'lodash';
import { empty } from 'rxjs/Observer';

const { AppState } = stores;
// 当前跳转是否需要单选信息（跳转单个任务时使用）
let paramIssueSelected = false;
const defaultTableShowColumns = [
  'issueNum',
  'issueTypeId',
  'summary',
  'statusId',
  'priorityId',
  'assignee',
  'sprint',
  'lastUpdateDate',    
];
@store('SprintCommonStore')
class SprintCommonStore {
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

  // 任务信息
  @observable issues = [];

  // 分页信息存取（默认信息）
  @observable pagination = {
    current: 0,
    pageSize: 10,
    total: 0,
  };

  // filter（请求对象）缓存
  @observable filterMap = new Map([
    [
      'filter', {
        advancedSearchArgs: {
          statusId: [],
          priorityId: [],
          issueTypeId: [],
        },
        content: '',
        quickFilterIds: [],
        assigneeFilterIds: null,
        otherArgs: {
          issueIds: [],
          reporter: [],
          component: [],
          epic: [],
          label: [],
          summary: [],
          version: [],
          sprint: [],
        },
        searchArgs: {
          assignee: '',
          component: '',
          epic: '',
          issueNum: '',
          sprint: '',
          summary: '',
          version: '',
          createStartDate: '',
          createEndDate: '',
        },
      },
    ],
    [
      'paramFilter', {},
    ],
    [
      'userFilter', {},
    ],
  ]);

  // paramName 传入值，为项目跳转初始化 Table 的 Filter 名称
  @observable paramFilter = '';

  // 控制 Table Filter 内容（必须是字符串，否则 Filter 内部的 Select 会出现问题）
  @observable barFilter = [];

  // Table Filter 使用的筛选内容
  @observable columnFilter = new Map();

  // 当前加载状态
  @observable loading = true;

  // 创建问题窗口是否展开
  @observable createFlag = false;

  // 问题详情是否展开
  @observable expand = false;

  // 当前选中 Issue 详细信息
  @observable selectedIssue = {};

  // 当前项目默认优先级（QuickCreate，CreateIssue 使用）
  @observable defaultPriorityId = false;

  // 项目优先级
  @observable issuePriority = [];

  // 项目状态
  @observable issueStatus = [];

  // 项目类型
  @observable issueTypes = [];

  // 经办人
  @observable users = [];

  // 筛选列表是否显示
  @observable filterListVisible = false;

  // 跳入问题列表的url,用于返回
  @observable paramUrl = false;

  @computed get getFilterListVisible() {
    return this.filterListVisible;
  }

  @action setFilterListVisible(data) {
    this.filterListVisible = data;
  }

  @observable updateFilterName = '';

  @computed get getUpdateFilterName() {
    return this.updateFilterName;
  }

  @action setUpdateFilterName(data) {
    this.updateFilterName = data;
  }

  /**
   * 创建筛选时传递的数据(筛选的条件数据)
   */
  @observable createFilterData = {
    filterId: 0,
    objectVersionNumber: 0,
    name: '',
    personalFilterSearchDTO: {
      advancedSearchArgs: {
        issueTypeId: [],
        statusId: [],
        assigneeIds: [],
        priorityId: [],
      },
      searchArgs: {
        createStartDate: '',
        createEndDate: '',
        summary: '',
        issueNum: '',
        reporter: '',
        component: '',
        sprint: '',
        epic: '',
        label: '',
        version: '',
      },
      otherArgs: {
        component: [],
        sprint: [],
        epic: [],
        label: [],
        version: [],
      },
    },
    projectId: AppState.currentMenuType.id,
    userId: AppState.userInfo.id,
  }

  @computed get getCreateFilterData() {
    return toJS(this.createFilterData);
  }

  @action setCreateFilterData(target, origin) {
    this.createFilterData = Object.assign(target, origin);
  }

  @action setCFDArgs(advArgsData, searchArgsData, otherArgsData, contentsData) {
    const { personalFilterSearchDTO } = this.createFilterData;
    if (advArgsData) { personalFilterSearchDTO.advancedSearchArgs = advArgsData; }
    if (searchArgsData) { Object.assign(personalFilterSearchDTO.searchArgs, searchArgsData); }
    if (otherArgsData) { Object.assign(personalFilterSearchDTO.otherArgs, otherArgsData); }
    if (contentsData) { personalFilterSearchDTO.contents = contentsData; }
    return personalFilterSearchDTO;
  }

  // 控制保存按钮是否显示
  @observable isExistFilter = true;

  @computed get getIsExistFilter() {
    return this.isExistFilter;
  }

  @action setIsExistFilter(data) {
    this.isExistFilter = data;
  }

  // 控制保存模态框是否显示
  @observable saveFilterVisible = false;

  @computed get getSaveFilterVisible() {
    return this.saveFilterVisible;
  }

  @action setSaveFilterVisible(data) {
    this.saveFilterVisible = data;
  }

  // 控制导出模态框是否显示
  @observable exportModalVisible = false;

  @computed get getExportModalVisible() {
    return this.exportModalVisible;
  }

  @action setExportModalVisible(visible) {
    this.exportModalVisible = visible;
  }

  // table列显示存储
  @observable tableShowColumns = defaultTableShowColumns

  @computed get getTableShowColumns() {
    const transform = {
      issueNum: 'issueNum',
      summary: 'summary',
      //  "description": 
      issueTypeId: 'typeName',
      //  "projectName":
      assignee: 'assigneeName',
      // "assigneeRealName": 
      reporter: 'reporterName',
      //  "reporterRealName":
      //   "resolution": 
      statusId: 'statusName',
      sprint: 'sprintName',
      // "creationDate": 
      lastUpdateDate: 'lastUpdateDate',
      priorityId: 'priorityName',
      //  "subTask": 
      //  "remainingTime": 
      version: 'versionName',
      epic: 'epicName',
      label: 'labelName',
      storyPoints: 'storyPoints',
      component: 'componentName',
    };

    return this.tableShowColumns.map(key => transform[key]);
  }

  @action setTableShowColumns(tableShowColumns) {
    this.tableShowColumns = tableShowColumns;
  }

  @action setDefaultTableShowColumns() {
    this.tableShowColumns = defaultTableShowColumns;
  }

  // 控制清除筛选按钮是否显示

  @observable emptyBtnVisible = false;

  @computed get getEmptyBtnVisible() {
    return this.emptyBtnVisible;
  }

  @action setEmptyBtnVisible(data) {
    this.emptyBtnVisible = data;
  }

  // 我的筛选列表
  @observable myFilters = [];

  @computed get getMyFilters() {
    return toJS(this.myFilters);
  }

  @action setMyFilters(data) {
    this.myFilters = data;
  }

  // 被选中的筛选Id
  @observable selectedFilterId = undefined;

  @computed get getSelectedFilterId() {
    return this.selectedFilterId;
  }

  @action setSelectedFilterId(data) {
    this.selectedFilterId = data;
  }

  @observable projectInfo = {};

  @computed get getProjectInfo() {
    return toJS(this.projectInfo);
  }

  @action setProjectInfo(data) {
    this.projectInfo = data;
  }

  @observable selectedMyFilterInfo = {};

  @computed get getSelectedMyFilterInfo() {
    return this.selectedMyFilterInfo;
  }

  @action setSelectedMyFilterInfo(data) {
    this.selectedMyFilterInfo = data;
  }

  @observable editFilterInfo = [];

  @computed get getEditFilterInfo() {
    return toJS(this.editFilterInfo);
  }

  @action setEditFilterInfo(data) {
    this.editFilterInfo = data;
  }

  @observable selectedIssueType = [];

  @computed get getSelectedIssueType() {
    return this.selectedIssueType;
  }

  @action setSelectedIssueType(data) {
    this.selectedIssueType = data;
  }

  @observable selectedStatus = [];

  @computed get getSelectedStatus() {
    return this.selectedStatus;
  }

  @action setSelectedStatus(data) {
    this.selectedStatus = data;
  }

  @observable selectedPriority = [];

  @computed get getSelectedPriority() {
    return this.selectedPriority;
  }

  @action setSelectedPriority(data) {
    this.selectedPriority = data;
  }

  @observable selectedAssignee = [];

  @computed get getSelectedAssignee() {
    return toJS(this.selectedAssignee);
  }

  @action setSelectedAssignee(data) {
    this.selectedAssignee = data;
  }

  @observable createStartDate = '';

  @computed get getCreateStartDate() {
    return this.createStartDate;
  }

  @action setCreateStartDate(data) {
    this.createStartDate = data;
  }

  @observable createEndDate = '';

  @computed get getCreateEndDate() {
    return this.createEndDate;
  }

  @action setCreateEndDate(data) {
    this.createEndDate = data;
  }

  /**
   * 跳转至问题管理页时设定传入参数
   * @param paramSelected => Boolean => 单个任务跳转
   * @param paramName => String => 跳转时 paramName 信息
   * @param paramUrl => String => 跳转时 Header 部分 Back 按钮需要的信息
   */
  @action initPram(paramSelected, paramName = null, paramUrl) {
    paramIssueSelected = paramSelected;
    this.paramUrl = paramUrl;
    // if (paramName) {
    //   this.paramFilter = paramName;
    //   this.barFilter = [paramName];
    // }
  }

  /**
   * 设置初始化信息
   * @param res（loadCurrentSetting 返回数据）
   */
  @action setCurrentSetting([issueTypes, issueStatus, issuePriority, users, tagData, issueComponents, issueVersions, issueEpics, issueSprints, issues]) {
    /* eslint-disable */
    this.issueTypes = issueTypes;
    this.issueStatus = issueStatus;
    this.issuePriority = issuePriority;
    // this.users = users.content;
    this.users = [...users.content, { id: 'none', realName: '未分配' }];
    this.tagData = tagData;
    this.issueComponents = issueComponents;
    this.issueVersions = issueVersions;
    this.issueEpics = issueEpics;
    this.issueSprints = issueSprints;
    // 生成 Filter 单选项所需数据
    this.columnFilter = new Map([
      [
        'typeId', this.issueTypes.map(item => ({
          text: item.name,
          value: item.id.toString(),
        })),
      ],
      [
        'statusId', this.issueStatus.map(item => ({
          text: item.name,
          value: item.id.toString(),
        })),
      ],
      [
        'priorityId', this.issuePriority.map(item => ({
          text: item.name,
          value: item.id.toString(),
        })),
      ],
      [
        'label', this.tagData.map(item => ({
          text: item.labelName,
          // value: item.labelId.toString(),
          value: JSON.stringify({ id: item.labelId.toString() }),
        }))
      ],
      [
        'component', this.issueComponents.content.map(item => ({
          text: item.name,
          // value: item.componentId.toString(),
          value: JSON.stringify({ id: item.componentId.toString() }),
        }))
      ],
      [
        'version', this.issueVersions.map(item => ({
          text: item.name,
          // value: item.versionId.toString(),
          value: JSON.stringify({ id: item.versionId.toString() }),
        }))
      ],
      [
        'epic', this.issueEpics.map(item => ({
          text: item.epicName,
          // value: item.issueId.toString(),
          value: JSON.stringify({ id: item.issueId.toString() }),
        }))
      ],
      [
        'sprint', this.issueSprints.map(item => ({
          text: item.sprintName,
          // value: item.sprintId.toString(),
          value: JSON.stringify({ id: item.sprintId.toString() }),
        }))
      ],
    ]);
    // 设置 issue 信息
    this.issues = issues.content;
    // 设置分页总数
    this.pagination.total = issues.totalElements;
    // 当跳转为单任务时
    if (paramIssueSelected === true) {
      // 设置当前展开任务为请求返回第一项
      this.selectedIssue = this.issues[0];
      this.expand = true;
      paramIssueSelected = false;
    }
    // 退出 loading 状态
    this.loading = false;
    /* eslint-enable */
  }

  /**
   * 重置 filterMap，与受控的 barFilter
   * @param data => Map => 重置所需数据
   */
  @action reset(data) {
    this.issues = [];
    this.filterMap = data;
    this.barFilter = [];
    this.loading = true;
  }

  /**
   * 用于 Table 更新 ajax 请求对象时时重设数据
   * @param pagination => Object => 分页对象
   * @param data => issue 数据
   * @param barFilters => 受控 Table Filter
   */
  @action updateFiltedIssue(pagination, data, barFilters) {
    this.pagination = pagination;
    this.issues = data;
    this.barFilter = barFilters;
    this.loading = false;
  }

  @computed get getParamFilter() {
    return toJS(this.paramFilter);
  }

  @computed get getColumnFilter() {
    return toJS(this.columnFilter);
  }

  @action setFilterMap(data) {
    this.filterMap = data;
  }

  @computed get getFilterMap() {
    return toJS(this.filterMap);
  }

  @action setBarFilter(data) {
    this.barFilter = data;
  }

  @computed get getBarFilter() {
    return toJS(this.barFilter);
  }

  @action setIssues(data) {
    this.loading = false;
    this.issues = data;
  }

  @computed get getIssues() {
    return toJS(this.issues);
  }

  @action setIssueTypes(data) {
    this.issueTypes = data;
  }

  @computed get getIssueTypes() {
    return toJS(this.issueTypes);
  }

  @action setIssueStatus(data) {
    this.issueStatus = data;
  }

  @computed get getIssueStatus() {
    return toJS(this.issueStatus);
  }

  @action setIssuePriority(data) {
    this.issuePriority = data;
  }

  @computed get getIssuePriority() {
    return toJS(this.issuePriority);
  }

  @action setUsers(data) {
    this.users = data;
  }

  @computed get getUsers() {
    return toJS(this.users);
  }

  @action setLoading(data) {
    this.loading = data;
  }

  @computed get getLoading() {
    return toJS(this.loading);
  }

  @action createQuestion(data) {
    this.createFlag = data;
  }

  @computed get getCreateQuestion() {
    return this.createFlag;
  }

  @action setClickedRow(data) {
    this.selectedIssue = data.selectedIssue;
    this.expand = data.expand;
  }

  @computed get getSelectedIssue() {
    return toJS(this.selectedIssue);
  }

  @computed get getExpand() {
    return toJS(this.expand);
  }

  @computed get getPagination() {
    return toJS(this.pagination);
  }

  /**
   * 更新时所调用的用于设置数据的函数
   * @param res
   */
  @action refreshTrigger(res) {
    this.issues = res.content;
    this.pagination.total = res.totalElements;
    this.pagination.pageSize = res.size;
    this.pagination.current = res.number + 1;
    this.loading = false;
  }

  /**
   * 用于根据 paramUrl 拼接返回原地址的 URL
   * @returns URL => String => 跳转回源地址的 URL
   */
  @computed get getBackUrl() {
    const urlParams = AppState.currentMenuType;
    if (!this.paramUrl) {
      return undefined;
    } else if (this.paramUrl === 'backlog') {
      return `/agile/${this.paramUrl}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramIssueId=${this.paramIssueId}&paramOpenIssueId=${this.paramOpenIssueId}`;
    } else {
      return `/agile/${this.paramUrl}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`;
    }
  }

  @computed get getDefaultPriorityId() {
    return this.defaultPriorityId;
  }

  @action setDefaultPriorityId(data) {
    this.defaultPriorityId = data;
  }

  axiosGetMyFilterList = () => {
    const { userInfo: { id } } = AppState;
    this.setLoading(true);
    return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/personal_filter/query_all/${id}`).then((myFilters) => {
      this.setLoading(false);
      const reverseMyFilters = _.reverse(myFilters);
      this.setMyFilters(reverseMyFilters);
      this.setEditFilterInfo(_.map(_.map(reverseMyFilters, item => ({
        filterId: item.filterId,
      })), (item, index) => ({
        ...item,
        isEditing: false,
        isEditingIndex: index,
      })));
    }).catch(() => {
      this.setLoading(false);
      Choerodon.prompt('获取我的筛选列表失败');
    });
  };

  // noLoad: Issue卸载时，只需要清空筛选，不用加载数据
  resetFilterSelect = (filterControler, noLoad) => {
    const projectInfo = this.getProjectInfo;
    this.setSelectedFilterId(undefined);
    this.setSelectedMyFilterInfo({});
    this.setSelectedIssueType([]);
    this.setSelectedStatus([]);
    this.setSelectedPriority([]);
    this.setSelectedAssignee([]);
    this.setCreateStartDate('');
    this.setCreateEndDate('');
    this.setUpdateFilterName('');
    this.setBarFilter([]);
    this.setIsExistFilter(true);
    this.setEmptyBtnVisible(false);
    filterControler.myFilterUpdate({
      assigneeId: [],
      component: [],
      sprint: [],
      epic: [],
      label: [],
      version: [],
      issueIds: [],
    }, [], {
      summary: '',
      issueNum: '',
      reporter: '',
      component: '',
      sprint: '',
      epic: '',
      label: '',
      version: '',
    });
    filterControler.assigneeFilterUpdate([]);
    filterControler.advancedSearchArgsFilterUpdate([], [], []);
    filterControler.searchArgsFilterUpdate('', '');
    if (!noLoad) {
      this.updateIssues(filterControler, []);
    }
  };

  updateIssues = (filterControler, barFilter) => {
    this.setLoading(true);
    filterControler.update().then(
      (res) => {
        this.updateFiltedIssue({
          current: res.number + 1,
          pageSize: res.size,
          total: res.totalElements,
        }, res.content, barFilter);
      },
    );
  }

  judgeFilterConditionIsEmpty = () => {
    const userFilter = this.getFilterMap.get('userFilter');
    const userFilterAdvancedSearchArgs = _.pick(userFilter.advancedSearchArgs, ['issueTypeId', 'priorityId', 'statusId']);
    const userFilterSearchArgs = _.pick(userFilter.searchArgs, ['issueNum', 'summary', 'reporter', 'component', 'epic', 'version', 'sprint', 'label']);
    const userFilterOtherArgs = _.pick(userFilter.otherArgs, ['assigneeId', 'component', 'epic', 'version', 'sprint', 'label']);
    const userFilterContents = userFilter.contents || [];
    const userFilterAssigneeFilterIds = userFilter.assigneeFilterIds || [];
    const userFilterCreateStartDate = userFilter.searchArgs.createStartDate;
    const userFilterCreateEndDate = userFilter.searchArgs.createEndDate;

    const userFilterAdvEveryFieldIsEmpty = Object.keys(userFilterAdvancedSearchArgs).every(key => userFilterAdvancedSearchArgs[key].length === 0);
    const userFilterAssigneeIsEmpty = userFilterAssigneeFilterIds.length === 0;
    const userFilterSeaArgsFieldIsEmpty = Object.keys(userFilterSearchArgs).every(key => !userFilterSearchArgs[key]);
    const userFilterOtherArgsFieldIsEmpty = Object.keys(userFilterOtherArgs).every(key => !userFilterOtherArgs[key] || userFilterOtherArgs[key].length === 0);
    const userFilterContentsIsEmpty = userFilterContents.length === 0;
    const uFCreateStartDateIsProjectStartDate = !userFilterCreateStartDate || moment(userFilterCreateStartDate).format('YYYY-MM-DD') === moment(this.getProjectInfo.creationDate).format('YYYY-MM-DD');
    const uFCreateEndDateIsProjectEndDate = !userFilterCreateEndDate || moment(userFilterCreateEndDate).format('YYYY-MM-DD') === moment().format('YYYY-MM-DD');

    this.setEmptyBtnVisible(!userFilterAdvEveryFieldIsEmpty || !userFilterAssigneeIsEmpty || !userFilterSeaArgsFieldIsEmpty || !userFilterOtherArgsFieldIsEmpty || !userFilterContentsIsEmpty || !uFCreateStartDateIsProjectStartDate || !uFCreateEndDateIsProjectEndDate);
  }

  judgeConditionWithFilter = () => {
    const myFilters = this.getMyFilters;
    if (myFilters.length === 0) {
      this.setIsExistFilter(false);
    } 
    const filter = this.getFilterMap.get('userFilter');
    const filterAdvancedSearchArgs = _.pick(filter.advancedSearchArgs, ['issueTypeId', 'priorityId', 'statusId']);
    const filterAssigneeFilterIds = filter.assigneeFilterIds || [];
    const filterOtherAssignee = filter.otherArgs.assigneeId || [];
    const filterSearchArgs = _.pick(filter.searchArgs, ['issueNum', 'summary', 'reporter', 'component', 'epic', 'version', 'sprint', 'label', 'createStartDate', 'createEndDate']);
    const filterOtherArgs = _.pick(filter.otherArgs, ['component', 'epic', 'version', 'sprint', 'label']);
    const filterContents = filter.contents || [];
    const filterCreateStartDate = filter.searchArgs.createStartDate;
    const filterCreateEndDate = filter.searchArgs.createEndDate;

    const filterAdvEveryFieldIsEmpty = Object.keys(filterAdvancedSearchArgs).every(key => filterAdvancedSearchArgs[key].length === 0);
    const filterAssigneeIsEmpty = filterAssigneeFilterIds.length === 0;
    const filterSeaArgsFieldIsEmpty = Object.keys(filterSearchArgs).every(key => !filterSearchArgs[key]);
    const filterOtherArgsFieldIsEmpty = Object.keys(filterOtherArgs).every(key => filterOtherArgs[key].length === 0);
    const filterContentsIsEmpty = filterContents.length === 0;

    if (filterAdvEveryFieldIsEmpty && filterAssigneeIsEmpty && filterOtherAssignee.length === 0 && filterSeaArgsFieldIsEmpty && filterOtherArgsFieldIsEmpty && filterContentsIsEmpty) {
      for (let i = 0; i < myFilters.length; i += 1) {
        if (myFilters[i].personalFilterSearchDTO) {
          const { searchArgs } = myFilters[i].personalFilterSearchDTO;
          const createStartDateIsEqual = filterCreateStartDate && moment(filterCreateStartDate).format('YYYY-MM-DD') !== moment(this.getProjectInfo.creationDate).format('YYYY-MM-DD') && moment(searchArgs.createStartDate).format('YYYY-MM-DD') === moment(filterCreateStartDate).format('YYYY-MM-DD');
          const createEndDateIsEqual = filterCreateEndDate && moment(filterCreateEndDate).format('YYYY-MM-DD') !== moment().format('YYYY-MM-DD') && moment(searchArgs.createEndDate).format('YYYY-MM-DD') === moment(filterCreateEndDate).format('YYYY-MM-DD');
          if (createStartDateIsEqual && createEndDateIsEqual) { // 其他条件都为空 & 创建时间范围和已有筛选一样
            this.setSelectedFilterId(myFilters[i].filterId);
            this.setIsExistFilter(true);
            break;
          } else if (i === myFilters.length - 1 && (!filterCreateStartDate || moment(filterCreateStartDate).format('YYYY-MM-DD') === moment(this.getProjectInfo.creationDate).format('YYYY-MM-DD')) && (!filterCreateEndDate || moment(filterCreateEndDate).format('YYYY-MM-DD') === moment().format('YYYY-MM-DD'))) { // 直到最后一项，创建时间范围为项目开始时间到今天
            this.setSelectedFilterId(undefined);
            this.setIsExistFilter(true);
          } else { // 和已有筛选时间范围都不一样 且 范围不是项目创建时间到今天
            this.setSelectedFilterId(undefined);
            this.setIsExistFilter(false);
          }
        }
      }
    } else {
      this.setSelectedAssignee(filterOtherAssignee.length === 0 ? filterAssigneeFilterIds.map(item => Number(item)) : _.map(filterOtherAssignee, id => (id === '0' ? 'none' : id)));
      this.setSelectedIssueType(filterAdvancedSearchArgs.issueTypeId ? filterAdvancedSearchArgs.issueTypeId.map(item => Number(item)) : []);
      this.setSelectedPriority(filterAdvancedSearchArgs.priorityId ? filterAdvancedSearchArgs.priorityId.map(item => Number(item)) : []);
      this.setSelectedStatus(filterAdvancedSearchArgs.statusId ? filterAdvancedSearchArgs.statusId.map(item => Number(item)) : []);

      for (let i = 0; i < myFilters.length; i += 1) {
        if (myFilters[i].personalFilterSearchDTO) {
          const {
            advancedSearchArgs, searchArgs, otherArgs, contents, 
          } = myFilters[i].personalFilterSearchDTO;
            
          const advancedSearchArgsIsEqual = _.pull(Object.keys(advancedSearchArgs), 'reporterIds', 'assigneeIds').every(key => _.isEqual(advancedSearchArgs[key].sort(), filterAdvancedSearchArgs[key].map(item => Number(item)).sort()));
          const assigneeIdsIsEqual = _.isEqual(advancedSearchArgs.assigneeIds.sort(), filterAssigneeFilterIds.map(item => Number(item)).sort());
          const createStartDateIsEqual = !filterSearchArgs.createStartDate || moment(searchArgs.createStartDate).format('YYYY-MM-DD') === moment(filterSearchArgs.createStartDate).format('YYYY-MM-DD');
          const createEndDateIsEqual = !filterSearchArgs.createEndDate || moment(searchArgs.createEndDate).format('YYYY-MM-DD') === moment(filterSearchArgs.createEndDate).format('YYYY-MM-DD');
          const searchArgsIsEqual = createStartDateIsEqual && createEndDateIsEqual && _.pull(Object.keys(searchArgs), 'createStartDate', 'createEndDate', 'assignee').every(key => (searchArgs[key] || '') === (filterSearchArgs[key] || ''));
          const otherArgsIsEqual = (otherArgs === null && Object.keys(filterOtherArgs).every(key => filterOtherArgs[key].length === 0)) || (Boolean(otherArgs) && Object.keys(_.pick(otherArgs, ['component', 'epic', 'version', 'sprint', 'label'])).every(key => _.isEqual(otherArgs[key].sort(), filterOtherArgs[key].map(item => Number(item)).sort())));
          const contentsIsEqual = (contents === null && filterContents.length === 0) || (Boolean(contents) && _.isEqual(contents.sort(), filterContents.sort()));
            
          const itemIsEqual = advancedSearchArgsIsEqual && assigneeIdsIsEqual && searchArgsIsEqual && otherArgsIsEqual && contentsIsEqual;
      
          if (itemIsEqual) {
            const { filterId, personalFilterSearchDTO } = myFilters[i];
            // const advSearchArgs = personalFilterSearchDTO.advancedSearchArgs;
            this.setSelectedFilterId(filterId);
               
            this.setIsExistFilter(true);
            break;
          } else if (i === myFilters.length - 1 && !itemIsEqual) {
            this.setSelectedFilterId(undefined);
            this.setIsExistFilter(false);
          }
        }
      }
    }
  }
}
const sprintCommonStore = new SprintCommonStore();
export default sprintCommonStore;
