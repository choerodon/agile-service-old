import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

// eslint-disable-next-line import/prefer-default-export
export function getRoadMap(projectId) {
  return axios.get(`/agile/v1/projects/${projectId || getProjectId()}/pi/road_map?organizationId=${getOrganizationId()}`);
}
