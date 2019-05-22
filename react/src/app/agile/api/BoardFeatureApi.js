import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

export function getBoard() {
  return axios.get(`/agile/v1/projects/${getProjectId()}/board_feature/query_board_info`);
}
// {
//   "piId": "integer",  //piId
//   "sprintId": "integer",  //冲刺id
//   "before": "boolean",  //是否拖动到第一个
//   "teamProjectId": "integer",  //团队项目id
//   "featureId": "integer",  //特性id
//   "outsetId": "integer"  //before：true，在当前移动的值之后，false，在当前移动的值之前，若为0L则为第一次创建
// }
export function featureToBoard(data) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/board_feature`, data);
}
// {
//   "objectVersionNumber": "integer",  //乐观锁
//   "sprintId": "integer",  //冲刺id
//   "before": "boolean",  //是否拖动到第一个
//   "teamProjectId": "integer",  //团队项目id
//   "outsetId": "integer"  //before：true，在当前移动的值之后，false，在当前移动的值之前，若为0L则为第一次创建
// }
export function featureBoardMove(id, data) {
  return axios.put(`/agile/v1/projects/${getProjectId()}/board_feature/${id}`, data);
}
export function projectMove(id, data) {
  return axios.put(`/agile/v1/projects/${getProjectId()}/board_team/${id}`, data);
}
export function deleteFeatureFromBoard(id) {
  return axios.delete(`/agile/v1/projects/${getProjectId()}/board_feature/${id}`);
}
export function changeSprintWidth(sprintId, columnWidth) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/board_sprint_attr/update?sprintId=${sprintId}&columnWidth=${columnWidth}`);
}
export function getSideFeatures(piId, searchDTO) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/issues/program/query_by_pi_id?piId=${piId}&organizationId=${getOrganizationId()}`);
}
export function createConnection(createDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/board_depend`, createDTO);
}
export function deleteConnection(boardDependId) {
  return axios.delete(`/agile/v1/projects/${getProjectId()}/board_depend/${boardDependId}`);
}
