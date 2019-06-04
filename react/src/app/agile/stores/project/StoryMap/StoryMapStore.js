
import { observable, action, computed } from 'mobx';
import {
  find, findIndex, max, remove, groupBy, 
} from 'lodash';
import { getStoryMap } from '../../../api/StoryMapApi';

class StoryMapStore {
  @observable resizing = false;

  @observable storyMapData = {};

  @observable storyData = null;

  @observable loading = false;

  getStoryMap = () => {
    this.setLoading(true);
    getStoryMap().then((storyMapData) => {
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
}


export default new StoryMapStore();
