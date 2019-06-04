/* eslint-disable import/prefer-default-export */
import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

/**
 * @returns
 */
export function getStoryMap() {
  return axios.get(`/agile/v1/projects/${getProjectId()}/story_map/main?organizationId=${getOrganizationId()}`);
}
