import axios from 'axios';
import {
  observable, action, computed, toJS, reaction,
} from 'mobx';
import { store, stores } from 'choerodon-front-boot';

const { AppState } = stores;

@store('FeatureStore')
class FeatureStore {
  @observable featureList = [];

  @observable spinIf = false;

  getFeatureListData = () => axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/pi/backlog_pi_list`);

  getCurrentEpicList = () => axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/program/epics`);

  @action featureListInit(listData) {

  }

  @action setSpinIf(data) {
    this.spinIf = data;
  }

  @computed get getSpinIf() {
    return this.spinIf;
  }
}

const featureStore = new FeatureStore();
export default featureStore;
