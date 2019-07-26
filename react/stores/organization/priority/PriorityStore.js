import { observable, computed, action, runInAction, autorun  } from 'mobx';
import { axios, store } from '@choerodon/boot';

@store('PriorityStore')
class PriorityStore {
  @observable allPriority = [];

  @observable onLoadingList = false;

  @observable priorityList = [];

  @observable onCreatingPriority = false;

  @observable onEditingPriority = false;

  @computed get editingPriority() {
    return this.priorityList.find(item => item.id === this.editingPriorityId);
  }

  @action
  setPriorityList(newPriorityList) {
    this.priorityList = [...newPriorityList];
  }

  @computed
  get getPriorityList() {
    return this.priorityList.slice();
  }

  @action
  setOnLoadingList(state) {
    this.onLoadingList = state;
  }

  @action
  setOnCreatingPriority(state) {
    this.onCreatingPriority = state;
  }

  @action
  setOnEditingPriority(state) {
    this.onEditingPriority = state;
  }

  @action
  setEditingPriorityId(id) {
    this.editingPriorityId = id;
  }

  checkDelete = (orgId, priorityId) => axios.get(`/agile/v1/organizations/${orgId}/priority/check_delete/${priorityId}`);

  @action
  loadPriorityList = async (orgId) => {
    const URL = `/agile/v1/organizations/${orgId}/priority`;
    try {
      this.onLoadingList = true;
      const data = await axios.get(URL);
      runInAction(
        () => {
          this.priorityList = data;
        },
      );
    } catch (err) {
      throw err;
    } finally {
      runInAction(
        () => {
          this.onLoadingList = false;
        },
      );
    }
  };

  loadAllPriority = async (orgId) => {
    const URL = `/agile/v1/organizations/${orgId}/priority`;
    try {
      const data = await axios.get(URL);
      runInAction(
        () => {
          const { content } = data;
          this.allPriority = data;
        },
      );
    } catch (err) {
      throw err;
    }
  };

  checkName = (orgId, name) => axios.get(
    `/agile/v1/organizations/${orgId}/priority/check_name?name=${name}`,
  );

  editPriorityById = (orgId, priority) => axios.put(`/agile/v1/organizations/${orgId}/priority/${priority.id}`, priority);

  createPriority = (orgId, priority) => axios.post(`/agile/v1/organizations/${orgId}/priority`, priority);

  deletePriorityById = (orgId, priorityId, changePriorityId) => axios.delete(
    `/agile/v1/organizations/${orgId}/priority/delete/${priorityId}${changePriorityId ? `?changePriorityId=${changePriorityId}` : ''}`,
  );

  deleteAndChooseNewPriority = (orgId, prePriorityId, newPriorityId) => axios.post('');

  enablePriority = (orgId, id, enable) => axios.get(`/agile/v1/organizations/${orgId}/priority/enable/${id}?enable=${enable}`);

  reOrder = orgId => axios.put(
    `/agile/v1/organizations/${orgId}/priority/sequence`,
    this.priorityList.map(item => ({
      id: item.id,
      sequence: item.sequence,
    })),
  );
}

const priorityStore = new PriorityStore();

export default priorityStore;
