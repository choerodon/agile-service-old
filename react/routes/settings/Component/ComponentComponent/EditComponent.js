import React, { Component } from 'react';
import {
  Modal, Form, Input, Select, message, Button,
} from 'choerodon-ui';
import { Content, stores, axios } from '@choerodon/boot';
import _ from 'lodash';
import UserHead from '../../../../components/UserHead';
import { getUsers, getUser } from '../../../../api/CommonApi';
import { loadComponent, updateComponent } from '../../../../api/ComponentApi';
import './component.less';

const { Sidebar } = Modal;
const { TextArea } = Input;
const { Option } = Select;
const { AppState } = stores;
const FormItem = Form.Item;
let sign = false;

class EditComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originUsers: [],
      selectLoading: false,
      createLoading: false,

      component: {},
      defaultAssigneeRole: undefined,
      description: undefined,
      managerId: undefined,
      name: undefined,

      page: 1,
      canLoadMore: true,
      input: '',
    };
  }

  componentDidMount() {
    this.loadComponent(this.props.componentId);
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
          canLoadMore: res.hasNextPage,
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
      page: 1,
    });
    getUsers(input, undefined, this.state.page).then((res) => {
      this.setState({
        input,
        page: 1,
        originUsers: res.list.filter(u => u.enabled),
        selectLoading: false,
        canLoadMore: res.hasNextPage,
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

  loadComponent(componentId) {
    loadComponent(componentId)
      .then((res) => {
        const {
          defaultAssigneeRole, description, managerId, name,
        } = res;
        this.setState({
          defaultAssigneeRole,
          description,
          managerId: managerId || undefined,
          name,
          component: res,
        });
        if (managerId) {
          this.loadUser(managerId);
        }
      });
  }

  loadUser(managerId) {
    getUser(managerId).then((res) => {
      this.setState({
        managerId: JSON.stringify(res.list[0]),
        originUsers: res.list.length ? [res.list[0]] : [],
      });
    });
  }

  handleOk(e) {
    e.preventDefault();
    this.props.form.validateFields((err, values, modify) => {
      if (!err && modify) {
        const {
          defaultAssigneeRole, description, managerId, name,
        } = values;
        const component = {
          objectVersionNumber: this.state.component.objectVersionNumber,
          componentId: this.state.component.componentId,
          defaultAssigneeRole,
          description,
          managerId: managerId ? JSON.parse(managerId).id || 0 : 0,
          name: name.trim(),
        };
        this.setState({ createLoading: true });
        updateComponent(component.componentId, component)
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
            Choerodon.prompt('修改模块失败');
          });
      }
    });
  }


  checkComponentNameRepeat = (rule, value, callback) => {
    const { name } = this.state;
    if (value && value.trim() && value.trim() !== name) {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/component/check_name?componentName=${value}`)
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
        page: page + 1,
      });
    });
  }

  render() {
    const { getFieldDecorator, getFieldsValue } = this.props.form;
    const { canLoadMore } = this.state;
    return (
      <Sidebar
        className="c7n-component-component"
        title="修改模块"
        onText="修改"
        cancelText="取消"
        visible={this.props.visible || false}
        confirmLoading={this.state.createLoading}
        onOk={this.handleOk.bind(this)}
        onCancel={this.props.onCancel.bind(this)}
      >
        <Content
          title={`在项目“${AppState.currentMenuType.name}”中修改模块`}
          description="请在下面输入模块名称、模块概要、负责人和默认经办人策略，修改模版。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/component/"
          style={{
            padding: 0,
          }}
        >
          <Form style={{
            width: 512,
          }}
          >
            <FormItem style={{ marginBottom: 20 }}>
              {getFieldDecorator('name', {
                initialValue: this.state.name,
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
              {getFieldDecorator('description', {
                initialValue: this.state.description,
              })(
                <Input label="模块描述" autosize maxLength={30} />,
              )}
            </FormItem>
            <FormItem style={{ marginBottom: 20 }}>
              {getFieldDecorator('defaultAssigneeRole', {
                initialValue: this.state.defaultAssigneeRole,
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

            {
              getFieldsValue(['defaultAssigneeRole']).defaultAssigneeRole && getFieldsValue(['defaultAssigneeRole']).defaultAssigneeRole === '模块负责人' && (
                <FormItem style={{ marginBottom: 20 }}>
                  {getFieldDecorator('managerId', {
                    initialValue: this.state.managerId,
                  })(
                    <Select
                      label="负责人"
                      loading={this.state.selectLoading}
                      allowClear
                      filter
                      onFilterChange={this.onFilterChange.bind(this)}
                      getPopupContainer={trigger => (trigger.parentNode)}
                    >
                      {this.state.originUsers.map(user => (
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
                      ))}
                      {
                        canLoadMore && (
                          <Option key="loadMore" disabled className="loadMore-option">
                            <Button type="primary" onClick={this.loadMoreUsers} className="option-btn">更多</Button>
                          </Option>
                        )
                      }
                    </Select>,
                  )}
                </FormItem>
              )
            }
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create()(EditComponent);
