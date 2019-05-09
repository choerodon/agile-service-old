/* eslint-disable camelcase */
import _ from 'lodash';
/**
 * IssueFilterControler
 * 用于拼接 Issue 整体页面的请求，根据页面函数需求返回相应的请求结果
 * 由以下内容组成
 * cache：用于缓存用户请求内容的 Map，由
 * */

export default class ScrumBoardDataController {
  constructor() {
    this.dataReady = false;
    this.flattenedArr = [];
    this.epicDataMap = [];
    this.assigneeDataMap = [];
    this.parentWithSubsDataMapCollection = {};
    this.columnStructureMap = new Map();
    this.statusStructureMap = new Map();
    this.statusMap = [];
    this.column_statusStructureMap = new Map();
    this.issueIdFullDataMap = new Map();
    this.canDragOn = new Map();
    this.statusId_ColumnMap = new Map();
    this.flattenedArrSet = new Set();
  }

  getHeaderData() {
    if (!this.dataReady) {
      return null;
    }
    return this.columnStructureMap.map(({
      maxNum, minNum, columnId, name, subStatuses,
    }) => [columnId, {
      maxNum,
      minNum,
      columnId,
      columnName: name,
      columnIssueCount: this.flattenedArr.filter(issue => issue.columnId === columnId).length,
      hasStatus: subStatuses.length > 0,
    }]);
  }

  getCanDragOn() {
    return this.canDragOn;
  }

  getStatusColumnMap() {
    return this.statusId_ColumnMap;
  }

  getStatusMap() {
    return this.statusMap;
  }

  getMapStructure() {
    return {
      columnStructure: [...this.columnStructureMap.values()],
      statusStructure: this.statusStructureMap,
      column_status_RelationMap: this.column_statusStructureMap,
    };
  }

  setSourceData(epicData, boardData) {
    const {
      assigneeIds, epicInfo, parentWithSubs, parentCompleted, parentIssues,
    } = boardData;
    this.dataConvertToFlatten(boardData);
    this.epicDataMap = this.addEpicLabelToFlattenData(this.flattenedArr, epicInfo, parentWithSubs, parentIssues, parentCompleted);
    this.assigneeDataMap = this.addAssigneeLabelToFlattenData(this.flattenedArr, assigneeIds);
    this.parentWithSubsDataMapCollection = this.addParentIdsLabelToFlattenData(this.flattenedArr, parentWithSubs, parentIssues, parentCompleted);
    this.dataReady = true;
  }

  addEpicLabelToFlattenData(flattenedArr, epicInfo, parentWithSubs, parentIssues, parentCompleted) {
    const combinedIssueArr = [...flattenedArr, ...parentIssues.filter(issue => !this.flattenedArrSet.has(issue.issueId)).map(issue => ({
      ...issue,
      statusName: issue.statusMapDTO.name,
      categoryCode: issue.statusMapDTO.type,
    }))];
    // 遍历 epicInfo，找出与史诗相对应的 issue 组成 Arr
    return {
      interConnectedDataMap: epicInfo ? epicInfo.map(({ epicId, epicName }) => [epicId, {
        epicId,
        epicName,
        issueArrLength: combinedIssueArr.filter(issue => issue.epicId === epicId).length,
        // 父子任务关系处理
        subIssueData: {
          ...this.addParentIdsLabelToFlattenData(
            combinedIssueArr.filter(issue => issue.epicId === epicId),
            parentWithSubs,
            parentIssues,
            parentCompleted,
            `swimlane_epic-${epicId}`,
          ),
        },
      }]) : [],
      unInterConnectedDataMap: {
        issueArrLength: combinedIssueArr.filter(issue => !issue.epicId).length,
        ...this.addParentIdsLabelToFlattenData(
          combinedIssueArr.filter(issue => !issue.epicId),
          parentWithSubs,
          parentIssues,
          parentCompleted,
          'swimlane_epic-unInterconnected',
        ),
      },
    };
  }

  getEpicData = () => {
    const ret = {};
    this.epicDataMap.interConnectedDataMap.forEach(([epicId, item]) => {
      Object.assign(ret, item.subIssueData.swimLaneData);
    });
    Object.assign(ret, this.epicDataMap.unInterConnectedDataMap.swimLaneData);
    return {
      swimLaneData: ret,
      ...this.epicDataMap,
    };
  };

  getAllData = () => ({
    swimLaneData: this.swimLaneDataConstructor(null, this.flattenedArr, 'swimlane_none'),
    unInterConnectedDataMap: this.flattenedArr,
  })

  getAllDataMap(mode) {
    if (mode === 'parent_child') {
      return new Map(this.combinedIssueArr.map(issue => [issue.issueId, issue]));
    } else {
      return new Map(this.flattenedArr.map(issue => [issue.issueId, issue]));
    }
  }

  addAssigneeLabelToFlattenData(flattenedArr, assigneeIds) {
    const issueWithAssigneeArr = assigneeIds.map(assigneeId => ({
      assigneeId,
      assigneeAvatarUrl: flattenedArr.filter(issue => issue.assigneeId === assigneeId)[0].imageUrl,
      assigneeName: flattenedArr.filter(issue => issue.assigneeId === assigneeId)[0].assigneeName,
      assigneeRealName: flattenedArr.filter(issue => issue.assigneeId === assigneeId)[0].assigneeRealName,
      assigneeLoginName: flattenedArr.filter(issue => issue.assigneeId === assigneeId)[0].assigneeLoginName,
      subIssueData: flattenedArr.filter(issue => issue.assigneeId === assigneeId),
    }));
    const issueWithAssigneeMap = issueWithAssigneeArr.map(assigneeObj => [assigneeObj.assigneeId, assigneeObj]);
    const issueWithoutAssignee = flattenedArr.filter(issue => !issue.assigneeId);
    return {
      swimLaneData: this.swimLaneDataConstructor(issueWithAssigneeArr, issueWithoutAssignee, 'assignee', 'assigneeId'),
      interConnectedDataMap: issueWithAssigneeMap,
      unInterConnectedDataMap: issueWithoutAssignee,
    };
  }

  getAssigneeData = () => ({
    ...this.assigneeDataMap,
  });

  addParentIdsLabelToFlattenData(flattenedArr, parentWithSubs, parentIssues, parentCompleted, mode = 'parent_child') {
    let combinedIssueArr = [];
    if (mode !== 'parent_child') {
      combinedIssueArr = flattenedArr;
    } else {
      combinedIssueArr = [...flattenedArr, ...parentIssues.filter(issue => !this.flattenedArrSet.has(issue.issueId)).map(issue => ({
        ...issue,
        statusName: issue.statusMapDTO.name,
        categoryCode: issue.statusMapDTO.type,
      }))];
      this.combinedIssueArr = combinedIssueArr;
    }
    // 第一个 Map，issue Id 与 issue 相对应（临时 Map）
    const issueIdDataMap = new Map(combinedIssueArr.map(issue => [issue.issueId, issue]));
    const issueIdFullDataMap = new Map(this.flattenedArr.map(issue => [issue.issueId, issue]));

    const parentCompletedSet = new Set(parentCompleted);
    const parentIdsSet = new Set(parentIssues.map(issue => issue.issueId));
    const parentIssueIdsSet = new Set(combinedIssueArr.filter(issue => !issue.parentIssueId && parentIdsSet.has(issue.issueId)).map(issue => issue.issueId));
    const noParentIssueIdsSet = new Set(combinedIssueArr.filter(issue => !issue.parentIssueId && !parentIdsSet.has(issue.issueId)).map(issue => issue.issueId));
    let parentDataMap = new Map();
    let parentDataArr = [];
    let otherIssueWithoutParent = [];

    if (parentIssueIdsSet.size) {
      const unCompletedArr = [...parentIssueIdsSet].filter(id => !parentCompletedSet.has(id)).map(id => [id, {
        isComplish: false,
        canMoveToComplish: parentWithSubs[id].every(subId => issueIdFullDataMap.get(subId).categoryCode === 'done'),
        subIssueData: parentWithSubs[id].map(subId => issueIdFullDataMap.get(subId)),
        ...issueIdDataMap.get(id),
      }] || []);
      const completedArr = [...parentIssueIdsSet].filter(id => parentCompletedSet.has(id)).map(id => [id, {
        isComplish: true,
        canMoveToComplish: parentWithSubs[id].every(subId => issueIdFullDataMap.get(subId).categoryCode === 'done'),
        subIssueData: parentWithSubs[id].map(subId => issueIdFullDataMap.get(subId)),
        ...issueIdDataMap.get(id),
      }] || []);
      parentDataArr = [...unCompletedArr, ...completedArr];
      parentDataMap = new Map([...unCompletedArr, ...completedArr]);
    }
    if (noParentIssueIdsSet.size) {
      // parentIssueId 没有值可能为 null，也可能为 0（后端返回数据不可信）
      otherIssueWithoutParent = combinedIssueArr.filter(issue => (issue.parentIssueId === 0 || issue.parentIssueId === null) && noParentIssueIdsSet.has(issue.issueId));
    }

    return {
      swimLaneData: this.swimLaneDataConstructor([...parentDataMap.values()], otherIssueWithoutParent, mode, 'issueId'),
      interConnectedDataMap: mode.includes('swimlane_epic') ? parentDataMap : parentDataArr,
      unInterConnectedDataMap: otherIssueWithoutParent,
    };
  }

  getParentWithSubData = () => ({
    ...this.parentWithSubsDataMapCollection,
  });

  swimLaneDataConstructor(interConnectedDataMap = null, unInterConnectedDataMap, mode, identifyId) {
    const ret = {};
    if (interConnectedDataMap) {
      interConnectedDataMap.forEach((parentIssue) => {
        ret[`${mode}-${parentIssue[identifyId]}`] = this.convertDataWithStructure(parentIssue.subIssueData);
      });
    }
    ret[`${mode}-other`] = this.convertDataWithStructure(unInterConnectedDataMap);
    return ret;
  }

  convertDataWithStructure(data) {
    const structureMap = JSON.parse(JSON.stringify(this.statusStructureMap));
    Object.keys(structureMap).forEach((status) => {
      structureMap[status] = _.sortBy(data.filter(issue => issue.statusId === status * 1), o => o.rank);
    });
    return structureMap;
  }

  dataConvertToFlatten(data) {
    const flattenedArr = [];
    const columnStructureMap = [];
    const statusStructureMap = {};
    const statusId_ColumnMap = new Map();
    const statusMap = [];
    const canDragOn = [];
    const column_StatusStructureMap = new Map();
    data.columnsData.columns.forEach((columnObj) => {
      columnStructureMap.push(columnObj);
      column_StatusStructureMap.set(columnObj.columnId, columnObj.subStatuses);
      columnObj.subStatuses.forEach((subStatusObj) => {
        statusId_ColumnMap.set(subStatusObj.statusId, columnObj.columnId);
        statusMap.push([subStatusObj.statusId, subStatusObj]);
        statusStructureMap[subStatusObj.statusId] = [];
        canDragOn.push([subStatusObj.statusId, false]);
        subStatusObj.issues.forEach((issue) => {
          if (issue) {
            flattenedArr.push({
              clicked: false,
              columnId: columnObj.columnId,
              statusId: subStatusObj.statusId,
              statusName: subStatusObj.name,
              categoryCode: subStatusObj.categoryCode,
              completed: subStatusObj.completed,
              ...issue,
            });
          }
        });
      });
    });
    this.flattenedArr = flattenedArr;
    this.flattenedArrSet = new Set(flattenedArr.map(issue => issue.issueId));
    this.columnStructureMap = columnStructureMap;
    this.statusStructureMap = statusStructureMap;
    this.statusMap = statusMap;
    this.canDragOn = canDragOn;
    this.statusId_ColumnMap = statusId_ColumnMap;
    this.column_statusStructureMap = column_StatusStructureMap;
  }
}
