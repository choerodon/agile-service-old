export default props => ({
  autoQuery: false,
  selection: false,
  paging: false,
  autoCreate: true,
  fields: [
    {
      name: 'type', type: 'string', label: '问题类型', required: true, 
    }, 
    {
      name: 'summary', type: 'string', label: '问题概要', required: true, maxLength: 50,
    },
    {
      name: 'description', type: 'object', label: '描述', 
    },
    {
      name: 'file', type: 'object', label: '附件', 
    },
    {
      name: 'reporter', type: 'string', label: '报告人', required: true, defaultValue: props.AppState.userInfo.realName,
    },
    {
      name: 'email', type: 'email', label: '邮箱', 
    },
  ],
});
