import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';


export function getRoadMapInProject(programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/road_map?programId=${programId}&organizationId=${getOrganizationId()}`);
}
export function loadIssue(issueId, programId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/project_invoke_program/${issueId}?programId=${programId}&organizationId=${getOrganizationId()}`);
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
