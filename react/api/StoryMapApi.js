/* eslint-disable import/prefer-default-export */
import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

/**
 * @returns
 */
export function getStoryMap(searchVO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/main?organizationId=${getOrganizationId()}`, searchVO);
}
/** {
  //问题id列表，移动到版本，配合versionId使用
  "versionIssueIds": [
    "array"
  ],
  "versionId": "integer",  //要关联的版本id
  "epicId": "integer",  //要关联的史诗id
  "versionIssueRelVOList": [
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
export function storyMove(storyMapDragVO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/move?organizationId=${getOrganizationId()}`, storyMapDragVO);
}

export function getSideIssueList(searchVO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map/demand?organizationId=${getOrganizationId()}`, searchVO);
}
// {
//   "objectVersionNumber": "integer",
//   "issueId": "integer",
//   "width": "integer",
//   "id": "integer",
//   "type": "string",
//   "projectId": "integer"
// }
export function createWidth(storyMapWidthVO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/story_map_width/?organizationId=${getOrganizationId()}`, storyMapWidthVO);
}
export function changeWidth(storyMapWidthVO) {
  return axios.put(`/agile/v1/projects/${getProjectId()}/story_map_width/?organizationId=${getOrganizationId()}`, storyMapWidthVO);
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
export function sort(sortVO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/rank`, sortVO);
}
