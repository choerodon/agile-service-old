
import { observable, action, computed } from 'mobx';
import {
  find, findIndex, max, remove, groupBy,
} from 'lodash';
import { getStoryMap } from '../../../api/StoryMapApi';
import { loadIssueTypes } from '../../../api/NewIssueApi';

class StoryMapStore {
  @observable issueTypes = [];

  @observable resizing = false;

  @observable storyMapData = {};

  @observable storyData = null;

  @observable loading = false;

  getStoryMap = () => {
    this.setLoading(true);
    Promise.all([getStoryMap(), loadIssueTypes()]).then(([storyMapData, issueTypes]) => {
      this.issueTypes = issueTypes;
      this.setStoryMapData(storyMapData);
      this.initStoryData(storyMapData);
      this.setLoading(false);
    });
  }

  @action setLoading(loading) {
    this.loading = loading;
  }

  @action setStoryMapData(storyMapData) {
    this.storyMapData = storyMapData;
  }

  @action initStoryData({ epicWithFeature, storyList }) {
    const storyData = {};
    epicWithFeature.forEach((epic) => {
      const { issueId: epicId } = epic;
      storyData[epicId] = {
        epicId,
        collapse: false,
        storys: [],
        feature: {},
      };
      const targetFeature = storyData[epicId].feature;
      epic.featureCommonDOList.forEach((feature) => {
        if (!targetFeature[feature.issueId]) {
          targetFeature[feature.issueId] = {
            storys: [],
            // width: 2,
          };
        }
      });
    });
    storyList.forEach((story) => {
      const { epicId, featureId } = story;
      if (epicId && storyData[epicId]) {
        const targetEpic = storyData[epicId];
        const { feature, storys } = targetEpic;
        storys.push(story);
        const targetFeature = feature[featureId];
        if (targetFeature) {
          targetFeature.storys.push(story);
        }
      }
    });
    // console.log(storyData);
    this.storyData = storyData;
  }

  @action collapse(epicId) {
    this.storyData[epicId].collapse = !this.storyData[epicId].collapse;
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
}


export default new StoryMapStore();
