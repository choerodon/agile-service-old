import { stores, axios } from 'choerodon-front-boot';

const { AppState } = stores;

/**
 * 获取PI列表
 */
export function getPIList(artId) {
  return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/pi/list?artId=${artId}`);
}

/**
 * 获取PI列表
 */
export function getPISelect() {
  return axios.get(`/agile//v1/projects/${AppState.currentMenuType.id}/pi/unfinished`);
}

/**
 * 获取PI列表
 */
export function getAllPIList() {
  return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/pi/all`);
}
/**
 * 删除PI
 */
export function deletePI(piId, artId) {
  return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/pi/${piId}?artId=${artId}`);
}

/**
 * 获取单个PI的目标列表
 * @param {*} piId 
 */
export function getPIAims(piId) {
  return axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/pi_objective/list?piId=${piId}`);
}

/**
 * 更新某个PI目标
 *
 * @export
 * @param {*} piObjectiveDTO
 * @returns
 */
export function upDatePIAmix(piObjectiveDTO) {
  return axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/pi_objective`, piObjectiveDTO);
}


/**
 * 删除PI目标
 *
 * @export
 * @param {*} piId
 * @returns
 */
export function deletePIAims(piId) {
  return axios.delete(`/agile/v1/projects/${AppState.currentMenuType.id}/pi_objective/${piId}`);
}

/**
 * 创建PI目标
 *
 * @export
 * @param {*} piObjectiveDTO
 * @returns
 */
export function createPIAims(piObjectiveDTO) {
  return axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/pi_objective`, piObjectiveDTO);
}
