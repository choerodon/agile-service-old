import { observable, action, computed } from 'mobx';
import { axios, store } from '@choerodon/boot';
import querystring from 'query-string';

@store('StateStore')
class StateStore {
  @observable stateList = [];

  @observable isLoading = false;

  @computed get getStateList() {
    return this.stateList;
  }

  @action setStateList(data) {
    this.stateList = data;
  }

  @computed get getIsLoading() {
    return this.isLoading;
  }

  @action setIsLoading(loading) {
    this.isLoading = loading;
  }

  loadStateList = (orgId, page, size, sort = { field: 'id', order: 'desc' }, param) => {
    this.setIsLoading(true);
    return axios.post(
      `/agile/v1/organizations/${orgId}/status/list?page=${page}&size=${size}&sort=${sort.field},${sort.order}`,
      JSON.stringify(param),
    ).then((data) => {
      this.setStateList(data.list);
      if (data && data.failed) {
        Choerodon.prompt(data.message);
        return Promise.reject(data);
      } else {
        this.setIsLoading(false);
        return Promise.resolve(data);
      }
    }).catch(() => Promise.reject());
  };

  checkName = (orgId, name) => axios.get(
    `/agile/v1/organizations/${orgId}/status/check_name?name=${name}`,
  );

  loadStateById = (orgId, stateId) => axios.get(`/agile/v1/organizations/${orgId}/status/${stateId}`);

  createState = (orgId, map) => axios.post(`/agile/v1/organizations/${orgId}/status`, JSON.stringify(map));

  deleteState = (orgId, stateId) => axios.delete(`/agile/v1/organizations/${orgId}/status/${stateId}`);

  updateState = (orgId, stateId, map) => axios.put(`/agile/v1/organizations/${orgId}/status/${stateId}`, JSON.stringify(map));

  loadAllState = orgId => axios.get(`/agile/v1/organizations/${orgId}/status/query_all`);
}

const stateStore = new StateStore();
export default stateStore;
