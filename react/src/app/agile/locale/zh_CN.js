const docServer = 'http://v0-16.choerodon.io/zh/docs';
/*eslint-disable*/
const pageDetail = {
  'branch.head': '项目\"{name}\"的分支管理',
  'branch.createHead': '在应用\"{name}\"中创建分支',
  'branch.createDes': '采用Gitflow工作流模式，请在下面选择分支类型，并填写issue号或版本号，即可创建分支。',
  'branch.mergeDev': '是否将分支\"{name}\"合并到develop分支？',
  'branch.noCommitDev': '是否将分支\"{name}\"分支无提交，是否删除？',
  'branch.noCommit': '是否将分支\"{name}\"分支无提交，不生成版本号，是否删除？',
  'branch.mergeDevMas': '是否将分支\"{name}\"合并到master，develop分支，并以为\"{version}\"版本号？',
  'branch.description': '分支是将您的工作从开发主线上分离开来，以免影响开发主线。平台采用gitflow分支模型，您可以在此创建分支，然后将代码拉至本地开发后提交代码，再结束分支，平台会为您合并代码并触发相应的持续集成流水线。',
  'branch.createTip': '采用gitflow分支模型，可创建feature、release、hotfix等分支，结束分支时自动触发分支合并和特有的持续集成流水线。',
  'branch.link': `${docServer}/user-guide/development-pipeline/branch-management/`,
  'branch.editDes': '采用Git flow工作流模式，自动创建分支模式所特有的流水线，持续交付过程中对feature、release、hotfix等分支进行管理。',
  'branch.editHead': '修改分支\"{name}\"完成的问题',
  'branch.detailHead': '分支\"{name}\"完成的问题',
};

const zh_CN = {
  save: '保存',
  cancel: '取消',
  add: '添加',
  name: '名称',
  code: '编码',
  required: '字段不能为空！',
  'network.error': '网络错误',
  createSuccess: '创建成功',
  createFailed: '创建失败',
  edit: '编辑',
  delete: '删除',

  "issue.detail": '详情',
  "issue.des": '描述',
  "issue.attachment": '附件',
  "issue.wiki": 'Wiki文档',
  "issue.commit": '评论',
  "issue.log": '工作日志',
  "issue.data_log": '活动日志',
  "issue.sub_task": '子任务',
  "issue.bug": '缺陷',
  "issue.link_task": '问题链接',
  "issue.link_test": '测试用例',
  "issue.branch": '开发',
  "issue.branch.create": '创建分支',
  "issue.commit.create": '添加评论',
  "issue.log.create": '登记工作',

  'field.create': '添加自定义字段',
  'field.edit': '编辑自定义字段',
  'field.context': '显示范围',
  'field.code.rule': '编码只允许数字、字母及下划线',
  'field.code.exist': '编码已经存在',
  'field.name.exist': '名称已经存在',
  'field.type': '字段类型',
  'field.radio': '单选框',
  'field.checkbox': '复选框',
  'field.time': '时间选择器',
  'field.datetime': '日期时间选择器',
  'field.date': '日期选择器',
  'field.number': '数字输入框',
  'field.input': '文本框（单行）',
  'field.text': '文本框（多行）',
  'field.single': '选择器（单选）',
  'field.multiple': '选择器（多选）',
  'field.cascade': '选择器（级联选择）',
  'field.url': 'URL',
  'field.label': '标签',
  'field.member': '成员选择',
  'field.default': '默认值',
  'field.decimal': '小数输入',
  'field.urlError': 'URL格式错误',
  'field.useCurrentDate': '使用当前日期和时间为默认值',
  'field.useCurrentTime': '使用当前时间为默认值',
  'field.dragList.tips': '请为该字段添加值，你可以通过上下 拖拽 改变显示顺序。',
  'field.value.null': '请先设置字段值列表',

  'dragList.invalid': '禁用',
  'dragList.active': '启用',
  'dragList.placeholder': '请输入选项值',
  'dragList.placeholder.code': '请输入选项编码',

  "learnmore": "了解更多",
  'branch.branch': '分支',
  'branch.tag': '标记',
  'branch.create': '创建分支',
  'branch.title': '分支管理',
  'branch.branchType': '分支类型',
  'branch.list': '分支列表',
  'branch.tagList': '标记列表',
  'branch.name': '分支名称',
  'branch.type': '分支类型',
  'branch.code': '提交编码',
  'branch.des': '提交描述',
  'branch.commit': '提交',
  'branch.issue': '问题',
  'branch.issueName': '问题名称',
  'branch.source': '分支来源',
  'branch.owner': '提交者',
  'branch.time': '创建',
  'branch.edit': '修改相关联问题',
  'branch.request': '创建合并请求',
  'branch.checkName': '名称只能包含数字和".",并且以数字开头和结尾',
  'branch.checkNameEnd': '不能以"/"、"."、".lock"结尾',
  'branch.check': '只能包含字母、数字、\'——\'、\'_\'',
  'branch.master': 'Master',
  'branch.bugfix': 'Bugfix',
  'branch.feature': 'Feature',
  'branch.release': 'Release',
  'branch.hotfix': 'Hotfix',
  'branch.custom': 'Custom',
  'branch.issue.priority': '优先级',
  'branch.issue.module': '模块',
  'branch.issue.type': '类型',
  'branch.issue.label': '标签',
  'branch.issue.creator': '经办人',
  'branch.issue.summary': '描述',

  'branch.masterDes': '即主分支，用于版本持续发布。在开发的整个阶段一直存在，平时不在此分支开发，因此代码比较稳定。',
  'branch.bugfixDes': '即漏洞修补分支，通常用于对发布分支进行错误修复',
  'branch.featureDes': '即特性分支，用于日常开发时切出分支进行单功能开发。基于develop分支创建，结束分支时合并至develop分支。',
  'branch.releaseDes': '即发布分支，用于产品发布、产品迭代。基于develop分支创建，结束分支时合并到develop分支和master分支。',
  'branch.hotfixDes': ' 即热修分支，用于产品发布后修复缺陷。基于master分支创建，结束分支时合并到master分支和develop分支。',
  'branch.customDes': ' 即自定义分支。',
  'branch.delete.tooltip': '确定要删除该分支吗?',
  'branch.action.delete': '删除分支',
  'branch.issue.story': '用户故事',
  'branch.issue.task': '任务',
  'branch.issue.bug': '故障',
  'branch.issue.epic': '史诗',
  ...pageDetail,
};
export default zh_CN;  
