
import { observable, action, computed } from 'mobx';
import {
  find, findIndex, max, remove, groupBy,
} from 'lodash';
import { getStoryMap } from '../../../api/StoryMapApi';
import { loadIssueTypes, loadVersions } from '../../../api/NewIssueApi';

class StoryMapStore {
  @observable swimLine = 'none';

  @observable issueTypes = [];

  @observable versionList = [];

  @observable resizing = false;

  @observable storyMapData = {};

  @observable storyData = null;

  @observable loading = false;

  getStoryMap = () => {
    this.setLoading(true);
    Promise.all([getStoryMap(), loadIssueTypes(), loadVersions()]).then(([storyMapData, issueTypes, versionList]) => {
      this.issueTypes = issueTypes;
      this.initVersionList(versionList);
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
            version: {},
            // width: 2,
          };
        }
      });
    });
    storyList.forEach((story) => {
      const { epicId, featureId, storyMapVersionDOList } = story;
      if (epicId && storyData[epicId]) {
        const targetEpic = storyData[epicId];
        const { feature, storys } = targetEpic;
        storys.push(story);
        const targetFeature = feature[featureId];
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
          if (!targetFeature.version[versionId]) {
            targetFeature.version[versionId] = [];
          }
          targetFeature.version[versionId].push(story);
        });
        // }
      }
    });
    // console.log(storyData);
    this.storyData = storyData;
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
