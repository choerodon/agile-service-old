import { stores, axios } from 'choerodon-front-boot';
import { getProjectId, getOrganizationId } from '../common/utils';

const { AppState } = stores;

export function getSelf() {
  return axios.get('/iam/v1/users/self');
}

export function getUsers(param, userId) {
  const projectId = AppState.currentMenuType.id;
  if (param) {
    return axios.get(`/iam/v1/projects/${projectId}/users?param=${param}${userId ? `&id=${userId}` : ''}`);
  }
  return axios.get(`/iam/v1/projects/${projectId}/users?size=40${userId ? `&id=${userId}` : ''}`);
}

export function getUser(userId) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`iam/v1/projects/${projectId}/users?id=${userId}`);
}
export function getProjectsInProgram() {
  return axios.get(`iam/v1/organizations/${getOrganizationId()}/projects/${getProjectId()}/program`);
}
