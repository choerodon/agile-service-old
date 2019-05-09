import React, { Component } from 'react';
import _ from 'lodash';
import {
  Modal, Form, Input, Select, Icon,
} from 'choerodon-ui';
import { stores, Content, axios } from 'choerodon-front-boot';
import './CreateBranch.scss';
import './commom.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option, OptGroup } = Select;
const FormItem = Form.Item;
const MAP = {
  bug: 'bugfix',
  task: 'feature',
  story: 'feature',
  issue_epic: 'feature',
  sub_task: 'feature',
};

class CreateBranch extends Component {
  constructor(props) {
    super(props);
    this.state = {
      // name: 'feature',
      // value: '',
      confirmLoading: false,
      selectLoading: true,
      branchLoading: true,
      originApps: [],
      branchs: [],
      // branchsShowMore: false,
      branchsInput: '',
      branchsSize: 5,
      branchsObj: {},
      tags: [],
      // tagsShowMore: false,
      tagsSize: 5,
      tagsObj: {},
    };
  }

  handleOk = (e) => {
    e.preventDefault();
    const { form, issueId, onOk } = this.props;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const devopsBranchDTO = {
          branchName: values.type === 'custom' ? values.name : `${values.type}-${values.name}`,
          issueId,
          originBranch: values.branch,
        };
        const applicationId = values.app;
        const projectId = AppState.currentMenuType.id;
        this.setState({
          confirmLoading: true,
        });
        axios.post(`/devops/v1/projects/${projectId}/apps/${applicationId}/git/branch`, devopsBranchDTO)
          .then((res) => {
            this.setState({
              confirmLoading: false,
            });
            onOk();
          })
          .catch((error) => {
            this.setState({
              confirmLoading: false,
            });
          });
      }
    });
  };

  checkName =(rule, value, callback) => {
    // eslint-disable-next-line no-useless-escape
    const endWith = /(\/|\.|\.lock)$/;
    const contain = /(\s|~|\^|:|\?|\*|\[|\\|\.\.|@\{|\/{2,}){1}/;
    const single = /^@+$/;
    if (endWith.test(value)) {
      callback('不能以"/"、"."、".lock"结尾');
    } else if (contain.test(value) || single.test(value)) {
      callback('只能包含字母、数字、\'——\'、\'_\'');
    } else {
      callback();
    }
  };

  onApplicationNameChange = () => {
    // this.setState({ selectLoading: true });
    axios.get(`/devops/v1/projects/${AppState.currentMenuType.id}/apps`)
      .then((res) => {
        this.setState({
          originApps: res,
          selectLoading: false,
          branchLoading: true,
        });
      });
  };

  render() {
    const {
      visible, store, form, form: { getFieldDecorator },
      onCancel, issueNum, typeCode,
    } = this.props;
    const {
      confirmLoading, selectLoading, branchLoading,
      originApps, branchs, branchsObj, branchsSize,
      branchsInput, tags, tagsObj, tagsSize,
    } = this.state;
    return (
      <Sidebar
        className="c7n-createBranch"
        title="创建分支"
        visible={visible}
        onOk={this.handleOk}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={confirmLoading}
      >
        <Content
          style={{
            padding: 0,
            width: 512,
          }}
          title={`对问题“${issueNum}”创建分支`}
          description="您可以在此选择应用、分支来源，可以修改默认的分支类型及分支名称，即可为该问题创建关联的分支。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/issue/manage-branch/"
        >
          <Form layout="vertical" className="c7n-sidebar-form">
            <div className="branch-formItem-icon">
              <span className="icon icon-widgets" />
            </div>
            <FormItem className="branch-formItem">
              {getFieldDecorator('app', {
                rules: [{ required: true, message: '请选择应用' }],
              })(
                <Select
                  label="应用名称"
                  allowClear
                  onFocus={this.onApplicationNameChange}
                  filter
                  optionFilterProp="children"
                  filterOption={
                    (input, option) => option.props.children.toLowerCase()
                      .indexOf(input.toLowerCase()) >= 0
                  }
                  loading={selectLoading}
                >
                  {originApps.map(app => (
                    <Option value={app.id} key={app.id}>{app.name}</Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            <div className="branch-formItem-icon">
              <span className="icon icon-wrap_text" />
            </div>
            <FormItem className="branch-formItem">
              {getFieldDecorator('branch', {
                rules: [{ required: true, message: '请选择分支来源' }],
              })(
                <Select
                  label="分支来源"
                  allowClear
                  disabled={!form.getFieldValue('app')}
                  filter
                  filterOption={false}
                  optionLabelProp="value"
                  loading={branchLoading}
                  onFilterChange={(input) => {
                    this.setState({
                      branchsInput: input,
                    });
                    axios.post(`/devops/v1/projects/${AppState.currentMenuType.id}/apps/${form.getFieldValue('app')}/git/branches?page=0&size=5`, {
                      searchParam: {
                        branchName: [input],
                      },
                      param: '',
                    })
                      .then((res) => {
                        if (res && !res.failed) {
                          this.setState({
                            branchs: res.content,
                            branchsSize: res.numberOfElements,
                            // branchsShowMore: res.totalPages !== 1,
                            branchsObj: res,
                            branchLoading: false,
                          });
                        } else {
                          Choerodon.prompt(res.message);
                        }
                      });
                    axios.post(`/devops/v1/projects/${AppState.currentMenuType.id}/apps/${form.getFieldValue('app')}/git/tags_list_options?page=0&size=5`, {
                      searchParam: {
                        tagName: [input],
                      },
                      param: '',
                    })
                      .then((res) => {
                        if (res && !res.failed) {
                          this.setState({
                            tags: res.content || [],
                            tagsSize: res.numberOfElements,
                            // tagsShowMore: res.totalPages !== 1,
                            tagsObj: res,
                          });
                        } else {
                          Choerodon.prompt(res.message);
                        }
                      });
                  }}
                >
                  <OptGroup label="分支" key="branchGroup">
                    {branchs.map(s => (
                      <Option value={s.branchName} key={s.branchName}>
                        <Icon type="branch" />
                        {s.branchName}
                      </Option>
                    ))}
                    {
                      branchsObj.totalElements > branchsObj.numberOfElements
                      && branchsObj.numberOfElements > 0 ? (
                        <Option key="more">
                          <div
                            role="none"
                            style={{
                              margin: '-4px -20px',
                              padding: '4px 20px',
                            }}
                            onClick={(e) => {
                              e.stopPropagation();
                              axios.post(`/devops/v1/projects/${AppState.currentMenuType.id}/apps/${form.getFieldValue('app')}/git/branches?page=0&size=${branchsSize + 5}`, {
                                searchParam: {
                                  branchName: [branchsInput],
                                },
                                param: null,
                              })
                                .then((res) => {
                                  if (res && !res.failed) {
                                    this.setState({
                                      branchs: res.content || [],
                                      branchsSize: res.numberOfElements,
                                      // branchsShowMore: res.totalPages !== 1,
                                      branchsObj: res,
                                    });
                                  } else {
                                    Choerodon.prompt(res.message);
                                  }
                                });
                            }}
                          >
                            查看更多
                          </div>
                        </Option>
                        ) : null
                      }
                  </OptGroup>
                  <OptGroup label="tag" key="tagGroup">
                    {tags.map(s => (
                      <Option value={s.tagName} key={s.tagName}>
                        <Icon type="local_offer" />
                        {s.tagName}
                      </Option>
                    ))}
                    {
                      tagsObj.totalElements > branchsObj.numberOfElements
                      && branchsObj.numberOfElements > 0 ? (
                        <Option key="more">
                          <div
                            role="none"
                            style={{
                              margin: '-4px -20px',
                              padding: '4px 20px',
                            }}
                            onClick={(e) => {
                              e.stopPropagation();
                              axios.post(`/devops/v1/projects/${AppState.currentMenuType.id}/apps/${form.getFieldValue('app')}/git/tags_list_options?page=0&size=${tagsSize + 5}`, {
                                searchParam: {
                                  tagName: [branchsInput],
                                },
                                param: null,
                              })
                                .then((res) => {
                                  if (res && !res.failed) {
                                    this.setState({
                                      tags: res.content || [],
                                      tagsSize: res.numberOfElements,
                                      // tagsShowMore: res.totalPages !== 1,
                                      tagsObj: res,
                                    });
                                  } else {
                                    Choerodon.prompt(res.message);
                                  }
                                });
                            }}
                          >
                            查看更多
                          </div>
                        </Option>
                        ) : null
                    }
                  </OptGroup>
                </Select>,
              )}
            </FormItem>

            <div className="branch-formItem-icon">
              <span className="icon icon-branch" />
            </div>
            <FormItem className="c7n-formItem_180">
              {getFieldDecorator('type', {
                rules: [{ required: true, message: '请选择分支类型' }],
                initialValue: MAP[typeCode || 'task'],
              })(
                <Select
                  allowClear
                  label="分支类型"
                >
                  {['feature', 'bugfix', 'release', 'hotfix', 'custom'].map(s => (
                    <Option value={s} key={s}>
                      <span className={`c7n-branch-icon icon-${s === 'bugfix' ? 'develop' : s}`}>
                        {s.slice(0, 1).toUpperCase()}
                      </span>
                      <span>{s}</span>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            <FormItem className="c7n-formItem_281">
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '请输入分支名称',
                }, {
                  validator: this.checkName,
                }],
                initialValue: issueNum,
              })(
                <Input
                  label="分支名称"
                  prefix={form.getFieldValue('type') === 'custom' || !form.getFieldValue('type') ? '' : `${form.getFieldValue('type')}-`}
                  maxLength={30}
                />,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(CreateBranch);
