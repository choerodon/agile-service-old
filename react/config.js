const config = {
    // use for c7n start
    server: 'http://api.staging.saas.hand-china.com',
    master: '@choerodon/master',    // 此处如果是一个相对路径，就如上文说的master会被替换
    projectType: 'choerodon',
    buildType: 'single',
    dashboard: {},
    resourcesLevel: ['site', 'origanization', 'project', 'user'],
};
module.exports = config;