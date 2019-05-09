import { observable, action, computed, toJS } from 'mobx';
import { store, stores, axios } from 'choerodon-front-boot';

const { AppState } = stores;

@store('StatusStore')
class StatusStore {
    @observable statusList = [];
    
  @computed get getStatusList() {
      return toJS(this.statusList);
    }

  @action setStatusList(data) {
      this.statusList = data;
    }

    axiosGetStatusList(data) {
      return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_status/statuses?page=${data.current - 1}&size=${data.pageSize}`);
    }
}

const statusStore = new StatusStore();
export default statusStore;

