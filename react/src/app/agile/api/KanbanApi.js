import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

export function loadBoardData(boardId, quickSearchObj = {}) {
  const {
    onlyMe, onlyStory, quickSearchArray, assigneeFilterIds,
  } = quickSearchObj;
  // {
  //   "exportFieldCodes": [
  //     "array"
  //   ],
  //   "advancedSearchArgs": "{}",
  //   "quickFilterIds": [
  //     "array"
  //   ],
  //   "contents": [
  //     "array"
  //   ],
  //   "otherArgs": "{}",
  //   "assigneeFilterIds": [
  //     "array"
  //   ],
  //   "onlyStory": "boolean",
  //   "searchArgs": "{}",
  //   "content": "string"
  // }
  return axios.post(`/agile/v1/projects/${getProjectId()}/board/${boardId}/all_data_program/${getOrganizationId()}?quickFilterIds=${quickSearchArray || []}`, quickSearchObj);
}
/**
 *
 *
 * @export
 * @param {*} sortDTO 
 * {
 * boardId:593
 * columnId:2529
 * objectVersionNumber:4
 * projectId:322
 * sequence:0
 * }
 * @returns
 */
export function sortColumn(sortDTO) {
  return axios.post(`/agile/v1/projects/${getProjectId()}/board_column/program/column_sort`, sortDTO);
}
/**
 * 删除列
 *
 * @export
 * @param {*} columnId
 * @returns
 */
export function deleteColumn(columnId) {
  return axios.delete(`/agile/v1/projects/${getProjectId()}/board_column/program/${columnId}`);
}
