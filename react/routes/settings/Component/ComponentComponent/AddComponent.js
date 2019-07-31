/* eslint-disable */
import React, { Component, Fragment } from 'react';
import {
  Modal, Form, Input, Select, message, Button
} from 'choerodon-ui';
import { Content, stores, axios } from '@choerodon/boot';
import _ from 'lodash';
import UserHead from '../../../../components/UserHead';
import { getUsers } from '../../../../api/CommonApi';
import { createComponent } from '../../../../api/ComponentApi';
import './component.less';

const { Sidebar } = Modal;
const { TextArea } = Input;
const { Option } = Select;
const { AppState } = stores;
const FormItem = Form.Item;
let sign = false;

class AddComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originUsers: [],
      selectLoading: false,
      createLoading: false,
      page: 1,
      canLoadMore: true,
      input: ''
    };
  }


  onFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input, undefined, this.state.page).then((res) => {
        this.setState({
          input,
          originUsers: res.list.filter(u => u.enabled),
          selectLoading: false,
          // page: 1,
          canLoadMore: res.hasNextPage
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input, undefined, this.state.page);
    }
  }

  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
      page: 1
    });
    getUsers(input, undefined, this.state.page).then((res) => {
      this.setState({
        input,
        page: 1,
        originUsers: res.list.filter(u => u.enabled),
        selectLoading: false,
        canLoadMore: res.hasNextPage
      });
    });
  }, 500);

  getFirst(str) {
    if (!str) {
      return '';
    }
    const re = /[\u4E00-\u9FA5]/g;
    for (let i = 0, len = str.length; i < len; i += 1) {
      if (re.test(str[i])) {
        return str[i];
      }
    }
    return str[0];
  }

  handleOk = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const {
          defaultAssigneeRole, description, managerId, name,
        } = values;
        const component = {
          defaultAssigneeRole,
          description,
          managerId: managerId ? JSON.parse(managerId).id || 0 : 0,
          name: name.trim(),
        };
        this.setState({ createLoading: true });
        createComponent(component)
          .then((res) => {
            this.setState({
              createLoading: false,
            });
            this.props.onOk();
          })
          .catch((error) => {
            this.setState({
              createLoading: false,
            });
            Choerodon.prompt('创建模块失败');
          });
      }
    });
  }

  checkComponentNameRepeat = (rule, value, callback) => {
    if (value && value.trim()) {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/component/check_name?componentName=${value.trim()}`)
        .then((res) => {
          if (res) {
            callback('模块名称重复');
          } else {
            callback();
          }
        });
    } else {
      callback();
    }
  };

  loadMoreUsers = (e) => {
    e.preventDefault();
    const { page, input, originUsers } = this.state;
    this.setState({
      selectLoading: true,
    });
    getUsers(input, undefined, page + 1).then((res) => {
      this.setState({
        originUsers: [...originUsers, ...res.list.filter(u => u.enabled)],
        selectLoading: false,
        canLoadMore: res.hasNextPage,
        page: this.state.page + 1
      });
    });
  }

  render() {
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const { canLoadMore } = this.state;
    return (
      <Sidebar
        className="c7n-component-component"
        title="创建模块"
        okText="创建"
        cancelText="取消"
        visible={this.props.visible || false}
        confirmLoading={this.state.createLoading}
        onOk={this.handleOk}
        onCancel={this.props.onCancel}
      >
        <Content
          style={{
            padding: 0,
          }}
          title={`在项目“${AppState.currentMenuType.name}”中创建模块`}
          description="请在下面输入模块名称、模块概要、负责人和默认经办人策略，创建新模版。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/component/"
        >
          <Form style={{
            width: 512,
          }}
          >
            <FormItem style={{ marginBottom: 20 }}>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '模块名称必填',
                  whitespace: true,
                }, {
                  validator: this.checkComponentNameRepeat,
                }],
              })(
                <Input label="模块名称" maxLength={10} />,
              )}
            </FormItem>
            <FormItem style={{ marginBottom: 20 }}>
              {getFieldDecorator('description', {})(
                <Input label="模块描述" maxLength={30} />,
              )}
            </FormItem>
            <FormItem style={{ marginBottom: 20 }}>
              {getFieldDecorator('defaultAssigneeRole', {
                rules: [{
                  required: true,
                  message: '默认经办人必填',
                }],
              })(
                <Select label="默认经办人">
                  {['模块负责人', '无'].map(defaultAssigneeRole => (
                    <Option key={defaultAssigneeRole} value={defaultAssigneeRole}>
                      {defaultAssigneeRole}
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            {getFieldsValue(['defaultAssigneeRole']).defaultAssigneeRole && getFieldsValue(['defaultAssigneeRole']).defaultAssigneeRole === '模块负责人' && (
              <FormItem style={{ marginBottom: 20 }}>
                {getFieldDecorator('managerId', {})(
                  <Select
                    label="负责人"
                    loading={this.state.selectLoading}
                    allowClear
                    filter
                    onFilterChange={this.onFilterChange.bind(this)}
                    getPopupContainer={(trigger) => (trigger.parentNode)}
                  >
                    {
                      this.state.originUsers.map(user => (
                        <Option key={JSON.stringify(user)} value={JSON.stringify(user)}>
                          <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
                            <UserHead
                              user={{
                                id: user.id,
                                loginName: user.loginName,
                                realName: user.realName,
                                avatar: user.imageUrl,
                              }}
                            />
                          </div>
                        </Option>
                      ))
                    }
                    {
                      canLoadMore && <Option key='loadMore' disabled className='loadMore-option'>
                        <Button type="primary" onClick={this.loadMoreUsers} className="option-btn">更多</Button>
                      </Option>
                    }
                  </Select> ,
                )}
              </FormItem>
            )}
          </Form>
        </Content>
      </Sidebar >
    );
  }
}

export default Form.create()(AddComponent);
