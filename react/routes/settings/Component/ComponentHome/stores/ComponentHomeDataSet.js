export default ({ id, intl }) => {
  return ({
    autoQuery: true,
    transport: {
      read: {
        url: `/agile/v1/projects/${id}/component/query_all?no_issue_test=true`,
        method: 'post',
      },
    },
    fields: [
      {
        name: 'name',
        type: 'string',
        label: '模块',
      },
      {
        name: 'issueCount',
        type: 'number',
        label: '问题',
      },
      {
        name: 'managerId',
        type: 'number',
        label: '负责人',
      },
      {
        name: 'description',
        type: 'string',
        label: '模块描述',
      },
      {
        name: 'defaultAssigneeRole',
        type: 'string',
        label: '默认经办人',
      },
    ],
  });
};
