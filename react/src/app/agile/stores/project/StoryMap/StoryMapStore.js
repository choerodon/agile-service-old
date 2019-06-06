
import {
  observable, action, computed, extendObservable,
} from 'mobx';
import {
  find, findIndex, max, remove, groupBy,
} from 'lodash';
import { getStoryMap, getSideIssueList } from '../../../api/StoryMapApi';
import { loadIssueTypes, loadVersions, loadPriorities } from '../../../api/NewIssueApi';

class StoryMapStore {
  @observable swimLine = 'none';

  @observable sideIssueListVisible = false;

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

  @observable storyData = null;

  @observable loading = false;

  getStoryMap = () => {
    this.setLoading(true);
    Promise.all([getStoryMap(), loadIssueTypes(), loadVersions(), loadPriorities()]).then(([storyMapData, issueTypes, versionList, prioritys]) => {
      this.issueTypes = issueTypes;
      this.prioritys = prioritys;
      this.initVersionList(versionList);
      this.setStoryMapData(storyMapData);
      this.initStoryData(storyMapData);
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

  @action toggleSideIssueListVisible() {
    this.sideIssueListVisible = !this.sideIssueListVisible;
  }

  @action setLoading(loading) {
    this.loading = loading;
  }

  @action setStoryMapData(storyMapData) {
    this.storyMapData = storyMapData;
  }

  @action switchSwimLine(swimLine) {
    this.swimLine = swimLine;
  }

  @action initVersionList(versionList) {
    this.versionList = versionList.map(version => ({ ...version, collapse: false })).concat([{
      versionId: 'none',
      name: '未计划部分',
      collapse: false,
    }]);
  }

  getInitVersions() {
    const versionObj = {};
    this.versionList.forEach((version) => {
      versionObj[version.versionId] = [];
    });
    return versionObj;
  }

  @action initStoryData({ epicWithFeature, storyList, storyMapWidth }) {
    const storyData = {};
    epicWithFeature.forEach((epic) => {
      const { issueId: epicId } = epic;
      const epicWithWidth = find(storyMapWidth, { issueId: epic.issueId, type: 'epic' });
      storyData[epicId] = {
        epicId,
        collapse: false,
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
    if (epicId && storyData[epicId]) {
      const targetEpic = storyData[epicId];
      const { feature, storys } = targetEpic;
      storys.push(story);
      const targetFeature = feature[featureId || 'none'];
      if (targetFeature) {
        targetFeature.storys.push(story);
      }
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
    remove(this.storyMapData.epicWithFeature, { adding: true });
    const currentIndex = findIndex(this.storyMapData.epicWithFeature, { issueId: epicData.issueId });
    // console.log(currentIndex);
    this.storyMapData.epicWithFeature.splice(currentIndex + 1, 0, epic);
  }

  @action afterCreateEpic(index, newEpic) {
    this.storyMapData.epicWithFeature[index] = { ...newEpic, featureCommonDOList: [] };
    extendObservable(this.storyData, {
      [newEpic.issueId]: {
        epicId: newEpic.issueId,
        collapse: false,
        storys: [],
        feature: {},
      },
    });
  }

  @action addFeature(epicData) {
    const feature = {
      adding: true,
    };
    // 删掉之前正在创建的
    remove(this.storyMapData.epicWithFeature, { adding: true });
    const currentIndex = findIndex(this.storyMapData.epicWithFeature, { issueId: epicData.issueId });
    // console.log(currentIndex);
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

  getIssueTypeByCode(typeCode) {
    return find(this.issueTypes, { typeCode });
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
