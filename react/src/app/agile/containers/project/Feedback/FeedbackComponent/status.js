const statusList = new Map([
  [
    'cancel', {
      color: '#393E46',
      name: '取消',
    },
  ],
  [
    'done', {
      color: '#00bfa5',
      name: '已完成',
    },
  ],
  [
    'todo', {
      color: '#FFB100',
      name: '待处理',
    },
  ],
  [
    'doing', {
      color: '#4D90FE',
      name: '处理中',
    },
  ],
]);

export default statusList;
