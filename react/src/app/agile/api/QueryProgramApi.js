import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';


export function getRoadMapInProject(programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/road_map?programId=${programId}&organizationId=${getOrganizationId()}`);
}
export function loadIssue(issueId, programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/issue/${issueId}?programId=${programId}&organizationId=${getOrganizationId()}`);
}
export function loadDatalogs(issueId, programId) {
  return axios.get(`agile/v1/projects/${getProjectId()}/project_invoke_program/datalog?programId=${programId}&issueId=${issueId}`);
}
/**
 * 加载字段配置（包含值）
 * @returns {V|*}
 */
export function getFieldAndValue(id, dto, programId) {  
  return axios.post(`/foundation/v1/projects/${getProjectId()}/field_value/list/${id}?programId=${programId}&organizationId=${getOrganizationId()}`, dto);
}
export function getBoard(programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/query_board_info?programId=${programId}`);
}
export function getBoardList(programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/board?programId=${programId}`);
}

export function loadBoardData(boardId, quickSearchObj = {}, programId) {
  const {
    onlyMe, onlyStory, quickSearchArray, assigneeFilterIds,
  } = quickSearchObj; 
  return axios.post(`/agile/v1/projects/${getProjectId()}/project_invoke_program/${boardId}/all_data_program/${getOrganizationId()}?programId=${programId}&quickFilterIds=${quickSearchArray || []}`, quickSearchObj);
}
/**
 * 获取PI列表
 */
export function getPIList(programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/pi_objective/unfinished?programId=${programId}`);
}
/**
 * 获取单个PI的目标列表
 * @param {*} piId 
 */
export function getPIAims(piId, programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/pi_objective/list_by_project?piId=${piId}&programId=${programId}`);
}
