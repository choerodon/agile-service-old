import React, { Component } from 'react';
import {
  Modal, Form, Input, Select, message, Icon, Button, DatePicker,
} from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';
import { NumericInput } from '../../../../../components/CommonComponent';
import './Filter.scss';

const { Sidebar } = Modal;
const { TextArea } = Input;
const { Option } = Select;
const { AppState } = stores;
const FormItem = Form.Item;

const arrOperation1 = [
  {
    value: '=',
    text: '等于',
  },
  {
    value: '!=',
    text: '不等于',
  },
  {
    value: 'in',
    text: '包含',
  },
  {
    value: 'notIn',
    text: '不包含',
  },
];
const arrOperation2 = [
  ...arrOperation1,
  {
    value: 'is',
    text: '是',
  },
  {
    value: 'isNot',
    text: '不是',
  },
];

const arrOperation3 = [
  {
    value: '>',
    text: '大于',
  },
  {
    value: '>=',
    text: '大于或等于',
  },
  {
    value: '<',
    text: '小于',
  },
  {
    value: '<=',
    text: '小于或等于',
  },
];
const arrOperation4 = [
  ...arrOperation3,
  {
    value: '=',
    text: '等于',
  },
  {
    value: 'is',
    text: '是',
  },
  {
    value: 'isNot',
    text: '不是',
  },
];

const OPERATION_FILTER = {
  // assignee: [{'=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // priority: ['=', '!=', 'in', 'notIn'],
  // issue_type: ['=', '!=', 'in', 'notIn'],
  // status: ['=', '!=', 'in', 'notIn'],
  // reporter: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // created_user: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // last_updated_user: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // epic: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // sprint: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // label: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // component: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // influence_version: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // fix_version: ['=', '!=', 'is', 'isNot', 'in', 'notIn'],
  // creation_date: ['>', '>=', '<', '<='],
  // last_update_date: ['>', '>=', '<', '<='],
  // story_point: ['<', '<=', '=', '>=', '>', 'is', 'isNot'],
  // remain_time: ['<', '<=', '=', '>=', '>', 'is', 'isNot'],
  assignee: arrOperation2,
  priority: arrOperation1,
  issue_type: arrOperation1,
  status: arrOperation1,
  reporter: arrOperation2,
  created_user: arrOperation2,
  last_updated_user: arrOperation2,
  epic: arrOperation2,
  sprint: arrOperation2,
  label: arrOperation2,
  component: arrOperation2,
  influence_version: arrOperation2,
  fix_version: arrOperation2,
  creation_date: arrOperation3,
  last_update_date: arrOperation3,
  story_point: arrOperation4,
  remain_time: arrOperation4,
};
class AddComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      filters: [
        {
          prop: undefined,
          rule: undefined,
          value: undefined,
        },
      ],
      quickFilterFiled: [],
      deleteItem: [],
      temp: [],
    };
  }

  componentDidMount() {
    this.loadQuickFilterFiled();
  }

  /**
   * 根据值和属性转化值
   * @param value
   * @param filter
   * @returns {*}
   */
  getValue = (value, filter) => {
    const type = Object.prototype.toString.call(value);
    // priority和issue_type的值存在数字和数组两种形式
    if (filter === 'priority') {
      if (type === '[object Array]') {
        const v = _.map(value, 'key');
        const vv = v.map(e => `${e}`);
        return `(${vv.join(',')})`;
      } else {
        const v = value.key;
        return `${v}`;
      }
    } else if (filter === 'issue_type') {
      if (type === '[object Array]') {
        const v = _.map(value, 'key');
        const vv = v.map(e => `'${e}'`);
        return `(${vv.join(',')})`;
      } else {
        const v = value.key;
        return `'${v}'`;
      }
    } else if (type === '[object Array]') {
      const v = _.map(value, 'key');
      return `(${v.join(',')})`;
    } else if (type === '[object Object]') {
      if (value.key) {
        const v = value.key;
        if (Object.prototype.toString.call(v) === '[object Number]') {
          return v;
        } else if (Object.prototype.toString.call(v) === '[object String]') {
          return v;
        }
      } else {
        return value.format('YYYY-MM-DD HH:mm:ss');
      }
    } else {
      return value;
    }
    return '';
  };

  /**
   * 根据值获取名称
   * @param value
   * @returns {*}
   */
  getLabel = (value) => {
    if (Object.prototype.toString.call(value) === '[object Array]') {
      const v = _.map(value, 'label');
      return `[${v.join(',')}]`;
    } else if (Object.prototype.toString.call(value) === '[object Object]') {
      if (value.key) {
        const v = value.label;
        if (Object.prototype.toString.call(v) === '[object Number]') {
          return v;
        } else if (Object.prototype.toString.call(v) === '[object String]') {
          return v;
        }
      } else {
        return value.format('YYYY-MM-DD HH:mm:ss');
      }
    } else {
      return value;
    }
    return '';
  };

  /**
   * 字段的关系列表
   * @param filter
   * @returns {*|Array}
   */
  getOperation = filter => OPERATION_FILTER[filter] || [];

  /**
   * 调用接口，获取'属性'的值列表
   * @param filter 属性
   * @param addEmpty
   */
  getOption(filter, addEmpty) {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const OPTION_FILTER = {
      assignee: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      priority: {
        url: `/issue/v1/projects/${projectId}/priority/list_by_org`,
        prop: '',
        id: 'id',
        name: 'name',
      },
      status: {
        url: `/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=agile`,
        prop: '',
        id: 'id',
        name: 'name',
      },
      reporter: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      created_user: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      last_updated_user: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      epic: {
        url: `/agile/v1/projects/${projectId}/issues/epics/select_data`,
        prop: '',
        id: 'issueId',
        name: 'epicName',
      },
      sprint: {
        // post
        url: `/agile/v1/projects/${projectId}/sprint/names`,
        prop: '',
        id: 'sprintId',
        name: 'sprintName',
      },
      label: {
        url: `/agile/v1/projects/${projectId}/issue_labels`,
        prop: '',
        id: 'labelId',
        name: 'labelName',
      },
      component: {
        url: `/agile/v1/projects/${projectId}/component`,
        prop: '',
        id: 'componentId',
        name: 'name',
      },
      influence_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
      },
      fix_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
      },
      issue_type: {
        url: `/issue/v1/projects/${projectId}/schemes/query_issue_types?apply_type=agile`,
        prop: '',
        id: 'typeCode',
        name: 'name',
      },
    };
    axios[filter === 'sprint'
    || filter === 'influence_version'
    || filter === 'fix_version' ? 'post' : 'get'](OPTION_FILTER[filter].url)
      .then((res) => {
        this.setState({
          temp: OPTION_FILTER[filter].prop === '' ? res : res[OPTION_FILTER[filter].prop],
        });
      });
  }

  /**
   * 加载属性列表
   */
  loadQuickFilterFiled = () => {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/fields`)
      .then((res) => {
        this.setState({
          quickFilterFiled: res,
        });
      });
  };

  /**
   * 保存配置
   * @param e
   */
  handleOk = (e) => {
    e.preventDefault();
    const { form, onOk } = this.props;
    const { filters, quickFilterFiled, deleteItem } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const arr = []; // 属性-关系-值
        const expressQueryArr = []; // 列表显示
        const o = []; // 多个条件间关系
        const f = filters.slice();
        f.forEach((v, i) => {
          if (deleteItem.indexOf(i) !== -1) {
            return;
          }
          const a = {
            fieldCode: values[`filter-${i}-prop`],
            operation: this.transformOperation(values[`filter-${i}-rule`]),
            value: this.getValue(values[`filter-${i}-value`], values[`filter-${i}-prop`]),
          };
          // 如果不是第一项
          if (i) {
            o.push(values[`filter-${i}-ao`]);
            expressQueryArr.push(values[`filter-${i}-ao`].toUpperCase());
          }
          arr.push(a);
          expressQueryArr.push(_.find(quickFilterFiled, { fieldCode: a.fieldCode }).name);
          expressQueryArr.push(a.operation);
          expressQueryArr.push(this.getLabel(values[`filter-${i}-value`]));
        });
        const json = JSON.stringify({
          arr,
          o,
        });
        const obj = {
          childIncluded: true,
          expressQuery: expressQueryArr.join(' '),
          name: values.name,
          description: `${values.description || ''}+++${json}`,
          projectId: AppState.currentMenuType.id,
          quickFilterValueDTOList: arr,
          relationOperations: o,
        };
        this.setState({
          loading: true,
        });
        axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter`, obj)
          .then((res) => {
            this.setState({
              loading: false,
            });
            onOk();
          });
      }
    });
  };


/**
 *校验快速搜索名称是否重复
 *
 * @memberof AddComponent
 */
checkSearchNameRepeat = (rule, value, callback) => {
  axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/quick_filter/check_name?quickFilterName=${value}`)
    .then((res) => {
      if (res) {
        callback('快速搜索名称重复');
      } else {
        callback();
      }
    });
};

  /**
   * 转化关系
   * @param value
   * @returns {*}
   */
  transformOperation = (value) => {
    const OPERATION = {
      '=': '=',
      '!=': '!=',
      in: 'in',
      notIn: 'not in',
      is: 'is',
      isNot: 'is not',
      '<': '<',
      '<=': '<=',
      '>': '>',
      '>=': '>=',
    };
    return OPERATION[value];
  };

  /**
   *
   * @param filter
   * @param addEmpty
   * @returns {Array}
   */
  tempOption = (filter, addEmpty) => {
    const { temp } = this.state;
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const OPTION_FILTER = {
      assignee: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      priority: {
        url: `/issue/v1/projects/${projectId}/priority/list_by_org`,
        prop: '',
        id: 'id',
        name: 'name',
      },
      status: {
        url: `/issue/v1/projects/${projectId}/schemes/query_status_by_project_id?apply_type=agile`,
        prop: '',
        id: 'id',
        name: 'name',
      },
      reporter: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      created_user: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      last_updated_user: {
        url: `/iam/v1/projects/${projectId}/users?page=0&size=9999`,
        prop: 'content',
        id: 'id',
        name: 'realName',
      },
      epic: {
        url: `/agile/v1/projects/${projectId}/issues/epics/select_data`,
        prop: '',
        id: 'issueId',
        name: 'epicName',
      },
      sprint: {
        // post
        url: `/agile/v1/projects/${projectId}/sprint/names`,
        prop: '',
        id: 'sprintId',
        name: 'sprintName',
      },
      label: {
        url: `/agile/v1/projects/${projectId}/issue_labels`,
        prop: '',
        id: 'labelId',
        name: 'labelName',
      },
      component: {
        url: `/agile/v1/projects/${projectId}/component`,
        prop: '',
        id: 'componentId',
        name: 'name',
      },
      influence_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
      },
      fix_version: {
        // post
        url: `/agile/v1/projects/${projectId}/product_version/names`,
        prop: '',
        id: 'versionId',
        name: 'name',
      },
      issue_type: {
        url: '',
        prop: '',
        id: 'typeCode',
        name: 'name',
      },
    };
    const arr = temp.map(v => (
      <Option key={v[OPTION_FILTER[filter].id]} value={v[OPTION_FILTER[filter].id]}>
        {v[OPTION_FILTER[filter].name]}
      </Option>
    ));
    return arr;
  };

  /**
   * 根据'属性'获取'关系'列表
   * @param filter
   * @param index
   * @returns {XML}
   */
  renderOperation(filter, index) {
    const { form } = this.props;
    if (!filter) {
      return (
        <Select label="关系" />
      );
    } else {
      return (
        <Select
          label="关系"
          style={['in', 'notIn'].indexOf(form.getFieldValue(`filter-${index}-prop`)) > -1 ? { marginTop: 8 } : {}}
          onChange={() => {
            const str = `filter-${index}-value`;
            form.setFieldsValue({
              [str]: undefined,
            });
          }}
        >
          {
            this.getOperation(filter).map(v => (
              <Option key={v.value} value={v.value}>{v.text}</Option>
            ))
          }
        </Select>
      );
    }
  }

  /**
   * 根据'属性'和'关系'获取'值'列表
   * @param filter
   * @param operation
   * @returns {XML}
   */
  renderValue(filter, operation) {
    if (!filter || !operation) {
      return (
        <Select label="值" />
      );
    } else if (
      ['assignee', 'priority', 'status', 'reporter', 'created_user',
        'last_updated_user', 'epic', 'sprint', 'label', 'component',
        'influence_version', 'fix_version', 'issue_type'].indexOf(filter) > -1) {
      if (['=', '!='].indexOf(operation) > -1) {
        // return normal value
        return (
          <Select
            label="值"
            labelInValue
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
            onFocus={() => {
              this.getOption(filter, false);
            }}
          >
            {this.tempOption(filter, false)}
          </Select>
        );
      } else if (['is', 'isNot'].indexOf(operation) > -1) {
        // return value add empty
        return (
          <Select
            label="值"
            labelInValue
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
          >
            <Option key="'null'" value="'null'">
              {'空'}
            </Option>
          </Select>
        );
      } else {
        // return multiple value
        return (
          <Select
            label="值"
            labelInValue
            mode="multiple"
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
            onFocus={() => {
              this.getOption(filter, false);
            }}
          >
            {this.tempOption(filter, false)}
          </Select>
        );
      }
    } else if (['creation_date', 'last_update_date'].indexOf(filter) > -1) {
      // time
      // return data picker
      return (
        <DatePicker
          label="值"
          format="YYYY-MM-DD HH:mm:ss"
          showTime
        />
      );
    } else {
      // story points && remainning time
      // return number input
      return (operation === 'is' || operation === 'isNot'
        ? (
          <Select
            label="值"
            labelInValue
            filter
            optionFilterProp="children"
            filterOption={(input, option) => option.props.children.toLowerCase()
              .indexOf(input.toLowerCase()) >= 0}
          >
            <Option key="'null'" value="'null'">
              {'空'}
            </Option>
          </Select>
        )
        : (
          <NumericInput
            label="值"
            style={{ lineHeight: '22px', marginBottom: 0, width: 300 }}
          />
        )
      );
    }
  }

  render() {
    const { form, onCancel } = this.props;
    const {
      loading,
      filters,
      quickFilterFiled,
      deleteItem,
    } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Sidebar
        className="c7n-filter"
        title="创建快速搜索"
        okText="创建"
        cancelText="取消"
        visible
        confirmLoading={loading}
        onOk={this.handleOk.bind(this)}
        onCancel={onCancel}
      >
        <Content
          style={{
            padding: 0,
            width: 700,
          }}
          title={`在项目“${AppState.currentMenuType.name}”中创建快速搜索`}
          description="通过定义快速搜索，可以在待办事项和活跃冲刺的快速搜索工具栏生效，帮助您更好的筛选过滤问题面板。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/quick-search/"
        >
          <Form layout="vertical">
            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '名称必填',
                }, {
                  validator: this.checkSearchNameRepeat,
                }],
              })(
                <Input
                  label="名称"
                  maxLength={10}
                />,
              )}
            </FormItem>
            {
              filters.map((filter, index) => (
                <div key={index.toString()}>
                  {
                    deleteItem.indexOf(index) === -1 && (
                      <div>
                        {
                          index !== 0 && (
                            <FormItem style={{ width: 80, display: 'inline-block', marginRight: 10 }}>
                              {getFieldDecorator(`filter-${index}-ao`, {
                                rules: [{
                                  required: true,
                                  message: '关系不可为空',
                                }],
                              })(
                                <Select label="关系">
                                  <Option key="and" value="and">且</Option>
                                  <Option key="or" value="or">或</Option>
                                </Select>,
                              )}
                            </FormItem>
                          )
                        }
                        <FormItem style={{ width: 120, display: 'inline-block', marginRight: 10 }}>
                          {getFieldDecorator(`filter-${index}-prop`, {
                            rules: [{
                              required: true,
                              message: '属性不可为空',
                            }],
                          })(
                            <Select
                              label="属性"
                              onChange={() => {
                                form.setFieldsValue({
                                  [`filter-${index}-rule`]: undefined,
                                  [`filter-${index}-value`]: undefined,
                                });
                              }}
                            >
                              {
                                quickFilterFiled.map(v => (
                                  <Option key={v.fieldCode} value={v.fieldCode}>{v.name}</Option>
                                ))
                              }
                            </Select>,
                          )}
                        </FormItem>
                        <FormItem style={{ width: 80, display: 'inline-block', marginRight: 10 }}>
                          {getFieldDecorator(`filter-${index}-rule`, {
                            rules: [{
                              required: true,
                              message: '关系不可为空',
                            }],
                          })(
                            this.renderOperation(form.getFieldValue(`filter-${index}-prop`), index),
                          )}
                        </FormItem>
                        <FormItem style={{ width: 300, display: 'inline-block' }}>
                          {getFieldDecorator(`filter-${index}-value`, {
                            rules: [{
                              required: true,
                              message: '值不可为空',
                            }],
                          })(
                            this.renderValue(form.getFieldValue(`filter-${index}-prop`), form.getFieldValue(`filter-${index}-rule`)),
                          )}
                        </FormItem>
                        {
                          index ? (
                            <Button
                              shape="circle"
                              style={{ margin: 10 }}
                              onClick={() => {
                                const arr = deleteItem.slice();
                                arr.push(index);
                                this.setState({
                                  deleteItem: arr,
                                });
                              }}
                            >
                              <Icon type="delete" />
                            </Button>
                          ) : null
                        }
                      </div>
                    )
                  }

                </div>
              ))
            }
            <Button
              type="primary"
              funcType="flat"
              onClick={() => {
                const arr = filters.slice();
                arr.push({
                  prop: undefined,
                  rule: undefined,
                  value: undefined,
                });
                this.setState({
                  filters: arr,
                });
              }}
            >
              <Icon type="add icon" />
              <span>添加属性</span>
            </Button>
            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('description', {})(
                <TextArea label="描述" autosize maxLength={30} />,
              )}
            </FormItem>
          </Form>

        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(AddComponent);
