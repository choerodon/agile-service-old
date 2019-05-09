import { axios } from 'choerodon-front-boot';
import { getProjectId, getOrganizationId } from '../common/utils';

// eslint-disable-next-line import/prefer-default-export
export function getRoadMap() {
  return axios.get(`/agile/v1/projects/${getProjectId()}/pi/road_map?organizationId=${getOrganizationId()}`);
}
