
import {
  observable, action, computed, extendObservable, toJS,
} from 'mobx';
import {
  find, findIndex, max, remove, groupBy,
} from 'lodash';
import { getProjectId } from '../../../common/utils';
import {
  getStoryMap, getSideIssueList, createWidth, changeWidth,
} from '../../../api/StoryMapApi';
import { loadIssueTypes, loadVersions, loadPriorities } from '../../../api/NewIssueApi';

class StoryMapStore {
  @observable swimLine = localStorage.getItem('agile.StoryMap.SwimLine') || 'none';

  @observable sideIssueListVisible = false;

  @observable createModalVisible = false;

  @observable createEpicModalVisible = false;

  @observable isFullScreen = false;

  @observable sideSearchDTO = {
    searchArgs: {
      assigneeId: null,
    },
  };

  @observable issueList = [];

  @observable issueTypes = [];

  @observable prioritys = [];

  @observable versionList = [];

  @observable resizing = false;

  @observable storyMapData = {};

  @observable storyData = {};

  @observable loading = false;

  getStoryMap = () => {
    this.setLoading(true);
    Promise.all([getStoryMap(), loadIssueTypes(), loadVersions(), loadPriorities()]).then(([storyMapData, issueTypes, versionList, prioritys]) => {
      const { epicWithFeature, featureWithoutEpic } = storyMapData;
      const newStoryMapData = {
        ...storyMapData, 
        epicWithFeature: epicWithFeature.concat({
          issueId: 0,
          featureCommonDOList: featureWithoutEpic,
        }),
      };
      this.issueTypes = issueTypes;
      this.prioritys = prioritys;
      this.initVersionList(versionList);
      this.setStoryMapData(newStoryMapData);
      this.initStoryData(newStoryMapData);
      this.setLoading(false);
    });
  }

  loadIssueList = () => {
    getSideIssueList(this.sideSearchDTO).then((res) => {
      this.setIssueList(res.demandStoryList);
    });
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

  @action toggleSideIssueListVisible() {
    this.sideIssueListVisible = !this.sideIssueListVisible;
  }

  @action setIsFullScreen(isFullScreen) {
    this.isFullScreen = isFullScreen;
  }

  @action setLoading(loading) {
    this.loading = loading;
  }

  @action setStoryMapData(storyMapData) {   
    this.storyMapData = storyMapData;
  }

  @action switchSwimLine(swimLine) {
    this.swimLine = swimLine;
    localStorage.setItem('agile.StoryMap.SwimLine', swimLine);
  }

  @action initVersionList(versionList) {
    this.versionList = versionList.reverse().concat([{
      versionId: 'none',
      name: '未计划部分',
    }]).map((version) => {
      const oldVersion = find(this.versionList, { versionId: version.versionId });
      if (oldVersion) {
        return { ...version, collapse: oldVersion.collapse };
      } else {
        return { ...version, collapse: false };
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
    const index = this.versionList.length - 1;
    this.versionList.splice(index, 0, {
      ...version,
      collapse: false,
    });
    Object.keys(this.storyData).forEach((epicId) => {
      const epic = this.storyData[epicId];
      Object.keys(epic.feature).forEach((featureId) => {
        const feature = epic.feature[featureId];
        extendObservable(feature.version, {
          [version.versionId]: [],
        });
      });
    });
  }

  @action initStoryData({
    epicWithFeature, storyList, storyMapWidth, 
  }) {
    const storyData = {};
    epicWithFeature.forEach((epic) => {
      const { issueId: epicId } = epic;
      const epicWithWidth = find(storyMapWidth, { issueId: epic.issueId, type: 'epic' });
      storyData[epicId] = {
        epicId,
        collapse: this.storyData[epicId] ? this.storyData[epicId].collapse : false,
        storys: [],
        feature: {
          none: {
            storys: [],
            version: this.getInitVersions(),
            width: epicWithWidth ? epicWithWidth.width : 1,
          },
        },
      };
      const targetFeature = storyData[epicId].feature;
      epic.featureCommonDOList.forEach((feature) => {
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
  }

  @action addStoryToStoryData(story, storyData = this.storyData) {
    const { epicId, featureId, storyMapVersionDOList } = story;
    if (epicId !== undefined && storyData[epicId]) {
      const targetEpic = storyData[epicId];
      const { feature, storys } = targetEpic;
      storys.push(story);
      const targetFeature = feature[featureId || 'none'];
      if (targetFeature) {
        targetFeature.storys.push(story);
        // 故事按照version泳道分类
        // if (this.swimLine === 'version') {
        if (storyMapVersionDOList.length === 0) {
          if (!targetFeature.version.none) {
            targetFeature.version.none = [];
          }
          targetFeature.version.none.push(story);
        }
        storyMapVersionDOList.forEach((version) => {
          const { versionId } = version;
          // if (!targetFeature.version[versionId]) {
          //   extendObservable(targetFeature.version, {
          //     [versionId]: [],
          //   }); 
          // }
          targetFeature.version[versionId].push(story);
        });
      }

      // }
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
      remove(targetEpic.featureCommonDOList, { adding: true });
    }
  }

  @action afterCreateEpic(index, newEpic) {
    this.storyMapData.epicWithFeature[index] = { ...newEpic, featureCommonDOList: [] };
    extendObservable(this.storyData, {
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
    this.storyMapData.epicWithFeature.unshift({ ...newEpic, featureCommonDOList: [] });
    extendObservable(this.storyData, {
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

  @action addFeature(epicData) {
    const feature = {
      adding: true,
    };
    
    const currentIndex = findIndex(this.storyMapData.epicWithFeature, { issueId: epicData.issueId });
    // console.log(currentIndex);
    // console.log(epicData, currentIndex);
    this.storyMapData.epicWithFeature[currentIndex].featureCommonDOList.push(feature);
  }

  @action afterCreateFeature(EpicIndex, newFeature) {
    const { length } = this.storyMapData.epicWithFeature[EpicIndex].featureCommonDOList;
    this.storyMapData.epicWithFeature[EpicIndex].featureCommonDOList[length - 1] = newFeature;
    const { issueId: epicId } = this.storyMapData.epicWithFeature[EpicIndex];
    extendObservable(this.storyData[epicId].feature, {
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

  @action removeStoryFromStoryMap(story) {
    const { epicId, featureId, storyMapVersionDOList } = story;
    remove(this.storyMapData.storyList, { issueId: story.issueId });
    if (epicId && this.storyData[epicId]) {
      const targetEpic = this.storyData[epicId];
      const { feature } = targetEpic;
      const targetFeature = feature[featureId || 'none'];
      remove(targetFeature.storys, { issueId: story.issueId });
      // 从各个版本移除
      if (storyMapVersionDOList.length === 0) {
        if (targetFeature.version.none) {
          remove(targetFeature.version.none, { issueId: story.issueId });
        }
      }
      storyMapVersionDOList.forEach((version) => {
        const { versionId } = version;
        remove(targetFeature.version[versionId], { issueId: story.issueId });
      });
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
    const storyMapWidthDTO = {
      ...targetWidth,
      projectId: getProjectId(),
      width,
      issueId,
      type,
    };
    if (!targetWidth) {
      createWidth(storyMapWidthDTO).then((res) => {
        if (res.failed) {
          this.setFeatureWidth({
            epicId,
            featureId,
            width: initWidth,
          });
        } else {
          this.addWidthDTO(res);
        }
      }).catch((err) => {
        this.setFeatureWidth({
          epicId,
          featureId,
          width: initWidth,
        });
      });
    } else {
      changeWidth(storyMapWidthDTO).then((res) => {
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

  @action addWidthDTO(storyMapWidthDTO) {
    const { storyMapWidth } = this.storyMapData;
    storyMapWidth.push(storyMapWidthDTO);
  }

  getIssueTypeByCode(typeCode) {
    return find(this.issueTypes, { typeCode });
  }

  @computed get getEpicList() {
    const { epicWithFeature } = this.storyMapData || {};
    return epicWithFeature || [];
  }

  @computed get getIsEmpty() {
    const { epicWithFeature } = this.storyMapData;  
    return epicWithFeature ? epicWithFeature.filter(epic => epic.issueId).length === 0 : true;
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
