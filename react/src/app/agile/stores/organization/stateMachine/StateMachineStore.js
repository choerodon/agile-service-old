import { observable, action, computed } from 'mobx';
import { axios, store } from '@choerodon/boot';
import querystring from 'query-string';

@store('StateMachineStore')
class StateMachineStore {

  @observable isLoading = false;
  @observable stateMachine = {};
  @observable configType = 'config_condition';

  @computed get getIsLoading() {
    return this.isLoading;
  }

  @computed get getConfigType() {
    return this.configType;
  }

  @computed get getStateMachine() {
    return this.stateMachine;
  }

  @action setConfigType(type) {
    this.configType = type;
  }

  @action setIsLoading(loading) {
    this.isLoading = loading;
  }

  @action setStateMachine(data) {
    this.stateMachine = data;
  }

  loadStateMachineList = (orgId, sort = { field: 'id', order: 'desc' }, map = {}) => {
    this.setIsLoading(true);
    return axios.get(`/agile/v1/organizations/${orgId}/state_machine?${querystring.stringify(map)}&sort=${sort.field},${sort.order}`).then((data) => {
      // this.setStateList(data);
      this.setIsLoading(false);
      if (data && data.failed) {
        Choerdon.propmt(data.message);
      } else {
        return Promise.resolve(data);
      }
    });
  }

  loadStateMachineDeployById = (orgId, stateId) => axios.get(`/agile/v1/organizations/${orgId}/state_machine/with_config_deploy/${stateId}`).then((data) => {
    const res = this.handleProptError(data);
    if (data) {
      this.setStateMachine(data);
    }
    return res;
  });

  loadStateMachineDraftById = (orgId, stateId) => axios.get(`/agile/v1/organizations/${orgId}/state_machine/with_config_draft/${stateId}`).then((data) => {
    const res = this.handleProptError(data);
    if (data) {
      this.setStateMachine(data);
    }
    return res;
  });

  createStateMachine = (orgId, map) => axios.post(`/agile/v1/organizations/${orgId}/state_machine`, JSON.stringify(map));

  deleteStateMachine = (orgId, stateId) => axios.delete(`/agile/v1/organizations/${orgId}/state_machine/${stateId}`)
    .then(data => this.handleProptError(data));

  updateStateMachine = (orgId, stateId, map) => axios
    .put(`/agile/v1/organizations/${orgId}/state_machine/${stateId}`, JSON.stringify(map));

  // 编辑状态机时添加状态
  addStateMachineNode = (orgId, stateMachineId, map) => axios
    .post(`/agile/v1/organizations/${orgId}/state_machine_node?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  updateStateMachineNode = (orgId, nodeId, stateMachineId, map) => axios
    .put(`/agile/v1/organizations/${orgId}/state_machine_node/${nodeId}?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  deleteStateMachineNode = (orgId, nodeId, stateMachineId) => axios
    .delete(`/agile/v1/organizations/${orgId}/state_machine_node/${nodeId}?stateMachineId=${stateMachineId}`);

  checkDeleteNode = (orgId, statusId, stateMachineId) => axios
    .get(`/agile/v1/organizations/${orgId}/state_machine_node/check_delete?statusId=${statusId}&stateMachineId=${stateMachineId}`);

  // 编辑状态机时添加转换
  addStateMachineTransfer = (orgId, stateMachineId, map) => axios
    .post(`/agile/v1/organizations/${orgId}/state_machine_transform?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  updateStateMachineTransfer = (orgId, nodeId, stateMachineId, map) => axios
    .put(`/agile/v1/organizations/${orgId}/state_machine_transform/${nodeId}?stateMachineId=${stateMachineId}`, JSON.stringify(map));

  deleteStateMachineTransfer = (orgId, nodeId, stateMachineId) => axios
    .delete(`/agile/v1/organizations/${orgId}/state_machine_transform/${nodeId}?stateMachineId=${stateMachineId}`);

  getTransferById = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/state_machine_transform/${id}`).then(data => this.handleProptError(data));

  getStateById = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/state_machine_node/${id}`).then(data => this.handleProptError(data));

  loadTransferConfigList = (orgId, id, type) => {
    this.setIsLoading(true);
    return axios.get(`/agile/v1/organizations/${orgId}/config_code/${id}?type=${type}`)
      .then((data) => {
        this.setIsLoading(false);
        return this.handleProptError(data);
      });
  };

  addConfig = (orgId, stateMachineId, map) => axios.post(`/agile/v1/organizations/${orgId}/state_machine_config/${stateMachineId}?transform_id=${map.transformId}`, JSON.stringify(map))
    .then(data => this.handleProptError(data));

  deleteConfig = (orgId, id) => axios.delete(`/agile/v1/organizations/${orgId}/state_machine_config/${id}`)
    .then(item => this.handleProptError(item));

  publishStateMachine = (orgId, id) => axios.get(`/agile/v1/organizations/${orgId}/state_machine/deploy/${id}`)
    .then(data => this.handleProptError(data));

  deleteDraft = (orgId, id) => axios.delete(`/agile/v1/organizations/${orgId}/state_machine/delete_draft/${id}`)
    .then(data => this.handleProptError(data));

  updateCondition = (orgId, id, type) => axios.get(`/agile/v1/organizations/${orgId}/state_machine_transform/update_condition_strategy/${id}?condition_strategy=${type}`)
    .then(data => this.handleProptError(data));

  linkAllToNode = (orgId, id, stateMachineId) => axios.post(`/agile/v1/organizations/${orgId}/state_machine_transform/create_type_all?end_node_id=${id}&state_machine_id=${stateMachineId}`)
    .then(data => this.handleProptError(data));

  deleteAllToNode = (orgId, id) => axios.delete(`/agile/v1/organizations/${orgId}/state_machine_transform/delete_type_all/${id}`)
    .then(data => this.handleProptError(data));

  checkName = (orgId, name) => axios.get(
    `/agile/v1/organizations/${orgId}/state_machine/check_name?name=${name}`,
  );

  checkStateName = (orgId, name) => axios.get(
    `/agile/v1/organizations/${orgId}/status/check_name?name=${name}`,
  );

  checkTransferName = (orgId, startNodeId, endNodeId, id, name) => axios.get(
    `/agile/v1/organizations/${orgId}/state_machine_transform/check_name?startNodeId=${startNodeId}&endNodeId=${endNodeId}&stateMachineId=${id}&name=${name}`,
  );

  handleProptError = (error) => {
    if (error && error.failed) {
      Choerodon.prompt(error.message);
      return false;
    } else {
      return error;
    }
  }
}

const stateMachineStore = new StateMachineStore();
export default stateMachineStore;
