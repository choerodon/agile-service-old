const config = {
  // use for c7n start
  server: 'http://api.staging.saas.hand-china.com',
  // server: 'http://api.alpha.saas.hand-china.com',
  // server: 'http://10.211.102.55:8080',
  // server: 'http://10.211.103.61:8080', // 富强
  // port: 8080,
  // server: 'http://10.211.108.232:8080', // 王喆
  master: '@choerodon/master', // 此处如果是一个相对路径，就如上文说的master会被替换
  projectType: 'choerodon',
  buildType: 'single',
  dashboard: {},
  resourcesLevel: ['site', 'origanization', 'project', 'user'],
  webSocketServer: 'ws://notify.staging.saas.hand-china.com',
};
module.exports = config;
