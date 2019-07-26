import { axios } from '@choerodon/boot';
import { getProjectId, getOrganizationId } from '../common/utils';

const IssueLinkType = {
  queryAll: ({
    page = 1, size = 999, filter = { 
      contents: [],
      linkName: '',  
    },
  } = {}) => axios.post(`/agile/v1/projects/${getProjectId()}/issue_link_types/query_all?page=${page}&size=${size}`, filter),
};
export default IssueLinkType;
