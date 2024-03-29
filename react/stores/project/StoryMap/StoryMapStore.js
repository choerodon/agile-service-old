
import {
  observable, action, computed, set, toJS,
} from 'mobx';
import {
  find, findIndex, max, remove, groupBy, sortBy,
} from 'lodash';
import { getProjectId } from '../../../common/utils';
import {
  getStoryMap, getSideIssueList, createWidth, changeWidth, sort,
} from '../../../api/StoryMapApi';
import { loadIssueTypes, loadVersions, loadPriorities } from '../../../api/NewIssueApi';

class StoryMapStore {
  @observable swimLine = localStorage.getItem('agile.StoryMap.SwimLine') || 'none';

  @observable sideIssueListVisible = false;

  @observable createModalVisible = false;

  @observable createEpicModalVisible = false;

  @observable createFeatureModalVisible = false;

  @observable isFullScreen = false;

  @observable sideSearchVO = {
    searchArgs: {
      assigneeId: null,
    },
    advancedSearchArgs: {
      versionList: [],
      statusList: [],
    },
  };

  @observable searchVO = {
    advancedSearchArgs: {
      versionList: [],
      statusList: [],
    },
  }

  @observable issueList = [];

  @observable issueTypes = [];

  @observable prioritys = [];

  @observable versionList = [];

  @observable resizing = false;

  @observable storyMapData = {};

  @observable storyData = {};

  @observable loading = false;

  @observable selectedIssueMap = observable.map({});

  @action clear() {
    this.storyMapData = {};
    this.storyData = {};
    this.searchVO = {
      advancedSearchArgs: {
        versionList: [],
        statusList: [],
      },
    };
    this.versionList = [];
  }

  getStoryMap = () => {
    this.setLoading(true);
    Promise.all([getStoryMap(this.searchVO), loadIssueTypes(), loadVersions(), loadPriorities()]).then(([storyMapData, issueTypes, versionList, prioritys]) => {
      let { epicWithFeature } = storyMapData;
      const { featureWithoutEpic } = storyMapData;
      epicWithFeature = sortBy(epicWithFeature, 'epicRank');
      const newStoryMapData = {
        ...storyMapData,
        epicWithFeature: featureWithoutEpic.length > 0 ? epicWithFeature.concat({
          issueId: 0,
          featureCommonDTOList: featureWithoutEpic,
        }) : epicWithFeature,
      };
      this.issueTypes = issueTypes;
      this.prioritys = prioritys;
      this.initVersionList(versionList);
      this.initStoryData(newStoryMapData);      
      this.setLoading(false);
    });
  }

  loadIssueList = () => {
    getSideIssueList(this.sideSearchVO).then((res) => {
      this.setIssueList(res.demandStoryList);
    });
  }

  @action
  handleFilterChange = (field, values) => {
    this.searchVO.advancedSearchArgs[field] = values;
    this.getStoryMap();
  }

  @action
  handleSideFilterChange = (field, values) => {
    this.sideSearchVO.advancedSearchArgs[field] = values;
    this.loadIssueList();
  }

  clearSideFilter = () => {
    this.sideSearchVO = {
      searchArgs: {
        assigneeId: null,
      },
      advancedSearchArgs: {
        versionList: [],
        statusList: [],
      },
    };
  }

  @action setIssueList(issueList) {
    this.issueList = issueList;
  }

  @action setSideIssueListVisible(sideIssueListVisible) {
    this.sideIssueListVisible = sideIssueListVisible;
  }

  @action setCreateModalVisible(createModalVisible) {
    this.createModalVisible = createModalVisible;
  }

  @action setCreateEpicModalVisible(createEpicModalVisible) {
    this.createEpicModalVisible = createEpicModalVisible;
  }

  @action setCreateFeatureModalVisible(createFeatureModalVisible) {
    this.createFeatureModalVisible = createFeatureModalVisible;
  }

  @action toggleSideIssueListVisible() {
    // 关闭Issue详情侧边
    if (!this.sideIssueListVisible) {
      this.setClickIssue();
    }

    this.sideIssueListVisible = !this.sideIssueListVisible;
  }

  @action setIsFullScreen(isFullScreen) {
    this.isFullScreen = isFullScreen;
  }

  @action setLoading(loading) {
    this.loading = loading;
  }


  @action switchSwimLine(swimLine) {
    this.swimLine = swimLine;
    localStorage.setItem('agile.StoryMap.SwimLine', swimLine);
  }

  @action initVersionList(versionList) {
    this.versionList = versionList.concat([{
      versionId: 'none',
      name: '未计划部分',
    }]).map((version) => {
      const oldVersion = find(this.versionList, { versionId: version.versionId });
      if (oldVersion) {
        return { ...version, storyNum: 0, collapse: oldVersion.collapse };
      } else {
        return { ...version, storyNum: 0, collapse: false };
      }
    });
  }

  getInitVersions() {
    const versionObj = {};
    this.versionList.forEach((version) => {
      versionObj[version.versionId] = [];
    });
    return versionObj;
  }

  @action afterCreateVersion(version) {
    this.setCreateModalVisible(false);
    // const index = this.versionList.length - 1;
    this.versionList.unshift({
      ...version,
      storyNum: 0,
      collapse: false,
    });
    Object.keys(this.storyData).forEach((epicId) => {
      const epic = this.storyData[epicId];
      Object.keys(epic.feature).forEach((featureId) => {
        const feature = epic.feature[featureId];
        set(feature.version, {
          [version.versionId]: [],
        });
      });
    });
  }

  @action initStoryData(storyMapData) {
    const {
      epicWithFeature, storyList, storyMapWidth,
    } = storyMapData;
    const storyData = {};
    epicWithFeature.forEach((epic) => {
      const { issueId: epicId } = epic;
      const epicWithWidth = find(storyMapWidth, { issueId: epic.issueId, type: 'epic' });
      storyData[epicId] = {
        epicId,
        collapse: this.storyData[epicId] ? this.storyData[epicId].collapse : false,
        storys: [],
        feature: epicId ? { // 无史诗不显示无特性
          none: {
            storys: [],
            version: this.getInitVersions(),
            width: epicWithWidth ? epicWithWidth.width : 1,
          },
        } : {},
      };
      const targetFeature = storyData[epicId].feature;
      epic.featureCommonDTOList.forEach((feature) => {
        if (!targetFeature[feature.issueId]) {
          const featureWithWidth = find(storyMapWidth, { issueId: feature.issueId, type: 'feature' });
          targetFeature[feature.issueId] = {
            storys: [],
            version: this.getInitVersions(),
            width: featureWithWidth ? featureWithWidth.width : 1,
          };
        }
      });
    });
    storyList.forEach((story) => {
      this.addStoryToStoryData(story, storyData);
    });
    // console.log(storyData);
    this.storyData = storyData;
    this.storyMapData = storyMapData;
  }

  @action addStoryToStoryData(story, storyData = this.storyData) {
    const { epicId, featureId, storyMapVersionDTOList } = story;
    if (epicId !== undefined && storyData[epicId]) {
      const targetEpic = storyData[epicId];
      const { feature, storys } = targetEpic;
      storys.push(story);
      const targetFeature = feature[featureId || 'none'];
      if (targetFeature) {
        targetFeature.storys.push(story);
        // 故事按照version泳道分类
        // if (this.swimLine === 'version') {          
        if (storyMapVersionDTOList.length === 0) {
          this.addStoryNumToVersion('none');
          if (!targetFeature.version.none) {
            targetFeature.version.none = [];
          }
          targetFeature.version.none.push(story);
        }
        storyMapVersionDTOList.forEach((version) => {
          const { versionId } = version;
          // if (!targetFeature.version[versionId]) {
          //   set(targetFeature.version, {
          //     [versionId]: [],
          //   }); 
          // }
          this.addStoryNumToVersion(versionId);
          targetFeature.version[versionId].push(story);
        });
      }

      // }
    }
  }

  @action addStoryNumToVersion(versionId) {
    const version = find(this.versionList, { versionId });
    if (version) {
      version.storyNum += 1;
    }
  }

  @action collapse(epicId) {
    this.storyData[epicId].collapse = !this.storyData[epicId].collapse;
  }

  @action collapseStory(id) {
    switch (this.swimLine) {
      case 'version': {
        const targetVersion = find(this.versionList, { versionId: id });
        targetVersion.collapse = !targetVersion.collapse;
        break;
      }
      default: break;
    }
  }

  @action addEpic(epicData) {
    const epic = {
      adding: true,
      featureCommonDTOList: [],
    };
    // 删掉之前正在创建的
    this.removeAddingEpic();
    const currentIndex = findIndex(this.storyMapData.epicWithFeature, { issueId: epicData.issueId });
    // console.log(currentIndex);
    this.storyMapData.epicWithFeature.splice(currentIndex + 1, 0, epic);
  }

  @action removeAddingEpic() {
    remove(this.storyMapData.epicWithFeature, { adding: true });
  }

  @action removeAddingFeature(epicId) {
    const targetEpic = find(this.storyMapData.epicWithFeature, { issueId: epicId });
    if (targetEpic) {
      remove(targetEpic.featureCommonDTOList, { adding: true });
    }
  }

  @action afterCreateEpic(index, newEpic) {
    this.storyMapData.epicWithFeature[index] = { ...newEpic, featureCommonDTOList: [] };
    set(this.storyData, {
      [newEpic.issueId]: {
        epicId: newEpic.issueId,
        collapse: false,
        storys: [],
        feature: {
          none: {
            storys: [],
            version: this.getInitVersions(),
            width: 1,
          },
        },
      },
    });
  }

  @action afterCreateEpicInModal(newEpic) {
    this.storyMapData.epicWithFeature.unshift({ ...newEpic, featureCommonDTOList: [] });
    set(this.storyData, {
      [newEpic.issueId]: {
        epicId: newEpic.issueId,
        collapse: false,
        storys: [],
        feature: {
          none: {
            storys: [],
            version: this.getInitVersions(),
            width: 1,
          },
        },
      },
    });
  }

  @action addFeature(epic) {
    const feature = {
      adding: true,
    };

    const currentIndex = findIndex(this.storyMapData.epicWithFeature, { issueId: epic.issueId });
    // console.log(currentIndex);
    // console.log(epic, currentIndex);
    this.storyMapData.epicWithFeature[currentIndex].featureCommonDTOList.push(feature);
  }

  @action afterCreateFeature(epicIndex, newFeature) {
    const { length } = this.storyMapData.epicWithFeature[epicIndex].featureCommonDTOList;
    this.storyMapData.epicWithFeature[epicIndex].featureCommonDTOList[length - 1] = newFeature;
    const { issueId: epicId } = this.storyMapData.epicWithFeature[epicIndex];
    set(this.storyData[epicId].feature, {
      [newFeature.issueId]: {
        storys: [],
        version: this.getInitVersions(),
        width: 1,
      },
    });
  }

  @action afterCreateStory(newStory) {
    this.addStoryToStoryData(newStory);
    this.storyMapData.storyList.push(newStory);
  }

  @action removeStoryFromStoryMap(story, targetVersionId) {
    const { epicId, featureId, storyMapVersionDTOList } = story;
    if (targetVersionId) {
      this.getStoryMap();
      this.setClickIssue();
      // if (this.storyData[epicId]) {
      //   const targetEpic = this.storyData[epicId];
      //   const { feature } = targetEpic;
      //   const targetFeature = feature[featureId || 'none'];      
      //   remove(targetFeature.version[targetVersionId], { issueId: story.issueId });
      //   remove(storyMapVersionDTOList, { versionId: targetVersionId });  
      //   // 版本全删掉后，移到未规划
      //   if (story.storyMapVersionDTOList.length === 0) {
      //     targetFeature.version.none.push(story);
      //   }
      // }
    } else {
      remove(this.storyMapData.storyList, { issueId: story.issueId });
      if (this.storyData[epicId]) {
        const targetEpic = this.storyData[epicId];
        const { feature } = targetEpic;
        const targetFeature = feature[featureId || 'none'];
        remove(targetFeature.storys, { issueId: story.issueId });
        // 从各个版本移除
        if (storyMapVersionDTOList.length === 0) {
          if (targetFeature.version.none) {
            remove(targetFeature.version.none, { issueId: story.issueId });
          }
        }
        storyMapVersionDTOList.forEach((version) => {
          const { versionId } = version;
          remove(targetFeature.version[versionId], { issueId: story.issueId });
        });
      }
    }
  }

  @action setFeatureWidth({
    epicId,
    featureId,
    width,
  }) {
    this.storyData[epicId].feature[featureId].width = width;
  }


  changeWidth({
    width,
    issueId,
    type,
  }, {
    epicId,
    featureId,
    initWidth,
  }) {
    const { storyMapWidth } = this.storyMapData;
    const targetWidth = find(storyMapWidth, { type, issueId });
    const targetIndex = findIndex(storyMapWidth, { type, issueId });
    const storyMapWidthVO = {
      ...targetWidth,
      projectId: getProjectId(),
      width,
      issueId,
      type,
    };
    if (!targetWidth) {
      createWidth(storyMapWidthVO).then((res) => {
        if (res.failed) {
          this.setFeatureWidth({
            epicId,
            featureId,
            width: initWidth,
          });
        } else {
          this.addWidthVO(res);
        }
      }).catch((err) => {
        this.setFeatureWidth({
          epicId,
          featureId,
          width: initWidth,
        });
      });
    } else {
      changeWidth(storyMapWidthVO).then((res) => {
        if (res.failed) {
          this.setFeatureWidth({
            epicId,
            featureId,
            width: initWidth,
          });
        } else {
          action(() => {
            storyMapWidth[targetIndex] = res;
          })();
        }
      }).catch((err) => {
        this.setFeatureWidth({
          epicId,
          featureId,
          width: initWidth,
        });
      });
    }
  }

  @action addWidthVO(storyMapWidthVO) {
    const { storyMapWidth } = this.storyMapData;
    storyMapWidth.push(storyMapWidthVO);
  }

  @action setClickIssue(clickIssue) {
    this.selectedIssueMap.clear();
    if (clickIssue) {
      this.sideIssueListVisible = false;
      this.selectedIssueMap.set(clickIssue.issueId, clickIssue);
    }
  }

  sortEpic(source, destination, sourceIndex, targetIndex) {
    if (!source || !destination || source.issueId === destination.issueId) {
      return;
    }
    const sortVO = {
      projectId: getProjectId(),
      objectVersionNumber: source.epicRankObjectVersionNumber, // 乐观锁     
      issueId: source.issueId,
      type: 'epic',
      before: true, // 是否拖动到第一个
      after: false,
      referenceIssueId: destination.issueId,
    };

    sort(sortVO).then(() => {
      // this.getStoryMap();
      const [removed] = this.storyMapData.epicWithFeature.splice(sourceIndex, 1);
      this.storyMapData.epicWithFeature.splice(targetIndex, 0, removed);
    });
  }

  getIssueTypeByCode(typeCode) {
    return find(this.issueTypes, { typeCode });
  }

  @computed get getEpicList() {
    const { epicWithFeature } = this.storyMapData || {};
    return epicWithFeature || [];
  }

  @computed get getIsEmpty() {
    const { epicWithFeature, featureWithoutEpic } = this.storyMapData;
    if (epicWithFeature && featureWithoutEpic) {
      return featureWithoutEpic.length === 0 && epicWithFeature.filter(epic => epic.issueId).length === 0;
    }
    return true;
  }

  @computed get getEpicType() {
    return find(this.issueTypes, { typeCode: 'issue_epic' });
  }

  @computed get getFeatureType() {
    return find(this.issueTypes, { typeCode: 'feature' });
  }

  @computed get getDefaultPriority() {
    return find(this.prioritys, { default: true }) || this.prioritys[0];
  }
}


export default new StoryMapStore();
