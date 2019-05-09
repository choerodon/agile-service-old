import axios from 'axios';
import {
  observable, action, computed, toJS, 
} from 'mobx';
import { store, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import moment from 'moment';

const { AppState } = stores;
const format = 'YYYY-MM-DD';

@store('PIStore')
class PIStore {
    @observable PIAimsLoading = false;

    @computed get getPIAimsLoading() {
      return toJS(this.PIAimsLoading);
    }

    @action setPIAimsLoading(data) {
      this.PIAimsLoading = data;
    }

    @observable PIList = [];
    
    @computed get getPIList() {
      return toJS(this.PIList);
    }

    @action setPIList(data) {
      this.PIList = data;
    }

    @observable PIAims = {}
      
    @computed get getPIAims() {
      return toJS(this.PIAims);
    }
  
    @action setPIAims(data) {
      this.PIAims = data;
    }

    @observable editPIVisible=false;

    @computed get getEditPIVisible() {
      return toJS(this.editPIVisible);
    }

    @action setEditPIVisible(data) {
      this.editPIVisible = data;
    }

    @observable createPIVisible=false;

    @computed get getCreatePIVisible() {
      return toJS(this.createPIVisible);
    }

    @action setCreatePIVisible(data) {
      this.createPIVisible = data;
    }

    @observable editPiAimsCtrl = [];

    @computed get getEditPiAimsCtrl() {
      return toJS(this.editPiAimsCtrl);
    }

    @action setEditPiAimsCtrl(data) {
      this.editPiAimsCtrl = data;
    }

    @observable createStretch = false;

    @computed get getCreateStretch() {
      return toJS(this.createStretch);
    }

    @action setCreateStretch(data) {
      this.createStretch = data;
    }
}

const piStore = new PIStore();

export default piStore;
