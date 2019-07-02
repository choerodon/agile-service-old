/* eslint-disable import/prefer-default-export */
import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

/**
 * @returns
 */
export function getStoryMap(searchDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/main?organizationId=${getOrganizationId()}`, searchDTO);
}
/** {
  //问题id列表，移动到版本，配合versionId使用
  "versionIssueIds": [
    "array"
  ],
  "versionId": "integer",  //要关联的版本id
  "epicId": "integer",  //要关联的史诗id
  "versionIssueRelDTOList": [
    {
      "relationType": "string",  //版本关系：fix、influence
      "issueId": "integer",  //问题id
      "versionId": "integer",  //版本id
      "name": "string",  //版本名称
      "projectId": "integer",  //项目id
      "statusCode": "string"  //版本状态
    }
  ],
  //问题id列表，移动到史诗，配合epicId使用
  "epicIssueIds": [
    "array"
  ],
  "featureId": "integer",  //要关联的特性id
  //问题id列表，移动到特性，配合featureId使用
  "featureIssueIds": [
    "array"
  ]
}
 * @returns
 */
export function storyMove(storyMapDragDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/move?organizationId=${getOrganizationId()}`, storyMapDragDTO);
}

export function getSideIssueList(searchDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/demand?organizationId=${getOrganizationId()}`, searchDTO);
}
// {
//   "objectVersionNumber": "integer",
//   "issueId": "integer",
//   "width": "integer",
//   "id": "integer",
//   "type": "string",
//   "projectId": "integer"
// }
export function createWidth(storyMapWidthDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map_width/?organizationId=${getOrganizationId()}`, storyMapWidthDTO);
}
export function changeWidth(storyMapWidthDTO) {
  return axios.put(`/agile/v1/projects/${getProjectId()}/story_map_width/?organizationId=${getOrganizationId()}`, storyMapWidthDTO);
}
// {
//   "projectId": 28,
//   "type": "feature",
//   "before": false,
//   "after": true,
//   "referenceIssueId": 65768,
//   "issueId": 65771,
//   "objectVersionNumber": null
//  }
export function sort(sortDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/rank`, sortDTO);
}
