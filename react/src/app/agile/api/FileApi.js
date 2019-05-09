import { stores, axios } from 'choerodon-front-boot';

const { AppState } = stores;
/**
 * 删除文件
 * @param {number} resourceId 资源id
 * @param {string} 文件id
 */
export function deleteFile(id) {
  const projectId = AppState.currentMenuType.id;
  return axios.delete(`/agile/v1/projects/${projectId}/issue_attachment/${id}`);
}

/**
 * 上传图片
 * @param {any} data
 */
export function uploadImage(data) {
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-data' },
  };
  const projectId = AppState.currentMenuType.id;
  return axios.post(
    `/agile/v1/projects/${projectId}/issue_attachment/upload_for_address`,
    data,
    axiosConfig,
  );
}

/**
 * 上传issue的附件
 * @param {*} data
 * @param {*} config
 */
export function uploadFile(data, config) {
  const {
    issueType, issueId, fileName, projectId, 
  } = config;
  const axiosConfig = {
    headers: { 'content-type': 'multipart/form-datal' },
  };
  return axios.post(
    `/zuul/agile/v1/projects/${projectId}/issue_attachment?projectId=${projectId}&issueId=${issueId}`,
    data,
    axiosConfig,
  );
}
