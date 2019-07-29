export default (props) => {
  console.log('dataSetProps:');
  console.log(props);
  const { projectId } = props;
  return {
    autoQuery: true,
    selection: false,
    paging: true,
    pageSize: 10,
    transport: {
      read: {
        url: `/agile/v1/projects/${projectId}/feedback/list`,
        method: 'post',
      },
      create: ({ data: [data] }) => ({
        url: '/agile/v1/feedback',
        method: 'post',
        data,
      }),
    },
    fields: [
      {
        name: 'feedbackNum', type: 'string', label: '问题编号', required: true, 
      },
      {
        name: 'type', type: 'string', label: '问题类型', required: true, unique: true, 
      },
      { name: 'summary', type: 'string', label: '概要' },
      { name: 'status', type: 'string', label: '状态' },
      { name: 'assignee', type: 'object', label: '经办人' },
      { name: 'lastUpdateDate', type: 'string', label: '最后更新时间' },
      { name: 'reporter', type: 'string', label: '报告人' },
    ],
    queryFields: [
      {
        name: 'feedbackNum', type: 'string', label: '问题编号', 
      },
      { name: 'summary', type: 'string', label: '概要' },
    ],
    events: {
    //   update: handleUpdate,
    },
  };
};
