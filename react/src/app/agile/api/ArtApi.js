import { stores, axios } from 'choerodon-front-boot';

const { AppState } = stores;


export function getArtsByProjectId() {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/list`);
}
export function getArtList() {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/all`);
}
export function getActiveArt() {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/active`);
}
export function checkArtName(name) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/check_name?artName=${name}`);
}
export function createArt(data) {
  const projectId = AppState.currentMenuType.id;
  return axios.post(`/agile/v1/projects/${projectId}/art`, data);
}
export function getArtById(artId) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art?id=${artId}`);
}
export function editArt(data) {
  const projectId = AppState.currentMenuType.id;
  return axios.put(`/agile/v1/projects/${projectId}/art`, data);
}
export function releaseArt(artId, piNumber) {
  const projectId = AppState.currentMenuType.id;
  return axios.post(`/agile/v1/projects/${projectId}/art/release_art?artId=${artId}&piNumber=${piNumber}`);
}
export function getArtCalendar(artId) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/art_calendar?id=${artId}`);
}
export function createPI(artId, startDate) {
  const projectId = AppState.currentMenuType.id;
  // return axios.post(`/agile/v1/projects/${projectId}/art/create_other_pi?artId=${artId}&startdate=${startDate}`);
  return axios.post(`/agile/v1/projects/${projectId}/art/create_other_pi`, {
    artId,
    startDate,
  });
}
export function beforeArtFinish(artId) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/before_complete?id=${artId}`);
}

export function beforeStop(artId) {
  const projectId = AppState.currentMenuType.id;
  return axios.get(`/agile/v1/projects/${projectId}/art/before_stop?id=${artId}`);
}

export function stopArt(data) {
  const projectId = AppState.currentMenuType.id;
  return axios.post(`agile/v1/projects/${projectId}/art/stop`, data);
}

export function startArt(data) {
  const projectId = AppState.currentMenuType.id;
  return axios.post(`agile/v1/projects/${projectId}/art/start`, data);
}
