export default (props) => {
  const { projectId } = props;
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: ({ data, params, dataSet }) => {
        const postData = { searchArgs: {} };
        if (data.typeList) {
          if (data.typeList !== 'all') {
            postData.searchArgs.typeList = data.typeList;
          }
        }
        if (data.feedbackNum) {
          postData.searchArgs.feedbackNum = data.feedbackNum;
        }
        if (data.summary) {
          postData.searchArgs.summary = data.summary;
        }
        if (data.params) {
          postData.contents = data.params;
        }
        // console.log(postData);
        return { 
          url: `/agile/v1/projects/${projectId}/feedback/list`,
          method: 'post',
          data: {
            ...postData,
          }, 
        };
      },
    },
    fields: [
      {
        name: 'feedbackNum', type: 'string', label: '问题编号', required: true, 
      },
      {
        name: 'type', type: 'string', label: '问题类型', required: true,
      },
      {
        name: 'summary', type: 'string', label: '概要', required: true,  
      },
      { name: 'status', type: 'string', label: '状态' },
      { name: 'assignee', type: 'object', label: '经办人' },
      { name: 'lastUpdateDate', type: 'string', label: '最后更新时间' },
      {
        name: 'reporter', type: 'string', label: '报告人', required: true, 
      },
    ],
    queryFields: [
      {
        name: 'feedbackNum', type: 'string', label: '问题编号', 
      },
      { name: 'summary', type: 'string', label: '概要' },
    ],
  };
};
