/* eslint-disable import/prefer-default-export */
import { stores, axios } from 'choerodon-front-boot';
import { getProjectId, getOrganizationId } from '../common/utils';

export function getFeatures(pagination, searchDTO) {
  const { size, page } = pagination;
  return axios.post(`/agile/v1/projects/${getProjectId()}/issues/program?size=${size}&page=${page}&organizationId=${getOrganizationId()}`, searchDTO);
}
export function getFeaturesInProject() {
  return axios.get(`/agile/v1/projects/${getProjectId()}/issues/features?organizationId=${getOrganizationId()}`);
}

export function getFeaturesByEpic(epicId) {
  return axios.get(`/agile/v1/projects/${getProjectId()}/issues/feature/select_data?organizationId=${getOrganizationId()}${epicId ? `&epicId=${epicId}` : ''}`);
}
export function exportFeatures(search) {
  return axios.post(`/zuul/agile/v1/projects/${getProjectId()}/issues/program/export?organizationId=${getOrganizationId()}`, search, { responseType: 'arraybuffer' });
}
