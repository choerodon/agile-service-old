import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Form, Button, Input, Divider, Select, TimePicker, Spin,
  DatePicker, InputNumber, Checkbox, message,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, stores,
} from '@choerodon/boot';
import _ from 'lodash';
import moment from 'moment';
import UserHead from '../../../../components/UserHead';
import { randomString } from '../../../../common/utils';
import './ObjectSchemeField.scss';
import DragList from '../Components/DragList';

const { AppState } = stores;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const singleList = ['radio', 'single'];
const multipleList = ['checkbox', 'multiple'];
const dateList = ['time', 'datetime', 'date'];
const textList = ['input', 'text', 'url'];
const dateFormat = 'YYYY-MM-DD HH:mm:ss';

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 100 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 26 },
  },
};
let sign = false;

@observer
class ObjectSchemeField extends Component {
  debounceFilterUsers = _.debounce((input) => {
    const { ObjectSchemeStore } = this.props;
    this.setState({ selectLoading: true });
    ObjectSchemeStore.getUsers(input).then((res) => {
      this.setState({
        originUsers: res.list.filter(u => u.enabled),
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    const { match } = this.props;
    this.state = {
      id: match.params.id,
      fieldOptions: [],
      defaultValue: '',
      isCheck: false,
      dateDisable: false,
      spinning: true,
      fieldContext: [],
      selectLoading: true,
      originUsers: [],
    };
  }

  componentDidMount() {
    this.initCurrentMenuType();
    this.loadContext();
    this.loadFieldById();
  }

  initCurrentMenuType = () => {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  loadContext = () => {
    const { ObjectSchemeStore } = this.props;
    ObjectSchemeStore.loadLookupValue('object_scheme_field_context').then((res) => {
      if (!res.failed) {
        this.setState({
          fieldContext: res.lookupValues,
        });
      }
    });
  };

  loadFieldById = () => {
    const { ObjectSchemeStore } = this.props;
    const { id } = this.state;
    ObjectSchemeStore.loadFieldDetail(id).then((data) => {
      if (data) {
        if (singleList.indexOf(data.fieldType) !== -1) {
          // 单选
          const defaultOption = data.fieldOptions ? data.fieldOptions.filter(f => f.isDefault).map(f => f.id) : [];
          this.setState({
            fieldOptions: data.fieldOptions || [],
            defaultValue: defaultOption.length ? defaultOption[0] : [],
          });
        } else if (multipleList.indexOf(data.fieldType) !== -1) {
          // 多选
          const defaultValue = data.fieldOptions ? data.fieldOptions.filter(f => f.isDefault).map(f => String(f.id)) : [];
          this.setState({
            fieldOptions: data.fieldOptions || [],
            defaultValue,
          });
        } else if (dateList.indexOf(data.fieldType) !== -1) {
          // 时间日期
          let defaultValue = data.defaultValue && moment(data.defaultValue);
          if (data.extraConfig) {
            defaultValue = moment();
          }
          this.setState({
            isCheck: data.extraConfig,
            defaultValue,
            dateDisable: data.extraConfig,
          });
        } else if (data.fieldType === 'number') {
          // 数字
          const defaultValue = data.defaultValue && Number(data.defaultValue);
          this.setState({
            isCheck: data.extraConfig,
            defaultValue,
          });
        } else if (textList.indexOf(data.fieldType) !== -1) {
          // 文本
          this.setState({
            defaultValue: data.defaultValue,
          });
        } else if (data.fieldType === 'member') {
          ObjectSchemeStore.getUsers('', data.defaultValue).then((res) => {
            this.setState({
              originUsers: res.list.filter(u => u.enabled),
              selectLoading: false,
              defaultValue: Number(data.defaultValue),
            });
          });
        }
        this.setState({
          spinning: false,
        });
      }
    });
  };

  saveEdit = () => {
    const {
      ObjectSchemeStore, form,
    } = this.props;
    const { fieldOptions, id } = this.state;
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        this.setState({
          submitting: true,
        });
        const field = ObjectSchemeStore.getField;
        const postData = {
          ...field,
          name: data.name,
          context: data.context,
          defaultValue: String(data.defaultValue || ''),
          extraConfig: !!data.check,
        };
        if (singleList.indexOf(field.fieldType) !== -1) {
          postData.fieldOptions = fieldOptions.map((o) => {
            if (data.defaultValue && (o.tempKey === data.defaultValue || o.id === data.defaultValue)) {
              return { ...o, isDefault: true };
            } else {
              return { ...o, isDefault: false };
            }
          });
        } else if (multipleList.indexOf(field.fieldType) !== -1) {
          postData.fieldOptions = fieldOptions.map((o) => {
            if (data.defaultValue.indexOf(String(o.tempKey)) !== -1
              || data.defaultValue.indexOf(String(o.id)) !== -1) {
              return { ...o, isDefault: true };
            } else {
              return { ...o, isDefault: false };
            }
          });
        } else if (dateList.indexOf(field.fieldType) !== -1) {
          postData.defaultValue = (data.defaultValue && data.defaultValue.format(dateFormat)) || '';
          if (data.check) {
            postData.defaultValue = moment().format(dateFormat);
          }
        } else if (field.fieldType === 'number') {
          postData.defaultValue = data.defaultValue === 0 || data.defaultValue ? String(data.defaultValue) : '';
        }
        ObjectSchemeStore.updateField(id, postData)
          .then(() => {
            this.setState({
              submitting: false,
            });
            this.cancelEdit();
          });
      }
    });
  };

  cancelEdit = () => {
    const { history, ObjectSchemeStore } = this.props;
    const field = ObjectSchemeStore.getField;
    ObjectSchemeStore.setField({});
    const {
      name, id, organizationId, type,
    } = AppState.currentMenuType;
    history.push(`/agile/objectScheme/detail/${field.schemeCode}?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  onTreeChange = (fieldOptions) => {
    this.setState({
      fieldOptions,
    });
  };

  onTreeCreate = (code, value) => {
    const { fieldOptions } = this.state;
    this.setState({
      fieldOptions: [
        ...fieldOptions,
        {
          enabled: true,
          status: 'add',
          code,
          value,
          tempKey: randomString(5),
        },
      ],
    });
  };

  onTreeDelete = (tempKey) => {
    const { form, ObjectSchemeStore } = this.props;
    const field = ObjectSchemeStore.getField;
    const defaultValue = form.getFieldValue('defaultValue');
    if (multipleList.indexOf(field.fieldType) !== -1) {
      const newValue = defaultValue.filter(v => v !== String(tempKey));
      form.setFieldsValue({ defaultValue: newValue });
    } else if (singleList.indexOf(field.fieldType) !== -1) {
      if (defaultValue === tempKey) {
        form.setFieldsValue({ defaultValue: '' });
      }
    }
  };

  handleCheck = (e, type) => {
    const { form } = this.props;
    if (dateList.indexOf(type) !== -1) {
      form.setFieldsValue({ defaultValue: moment() });
    }
    this.setState({
      isCheck: e.target.checked,
      dateDisable: e.target.checked,
    });
  };

  checkName = (rule, value, callback) => {
    const { ObjectSchemeStore, intl } = this.props;
    const field = ObjectSchemeStore.getField;
    const name = ObjectSchemeStore.getField ? ObjectSchemeStore.getField.name : false;
    if ((name && value === name) || !value) {
      callback();
    } else {
      ObjectSchemeStore.checkName(value, field.schemeCode)
        .then((data) => {
          if (data) {
            callback(intl.formatMessage({ id: 'field.name.exist' }));
          } else {
            callback();
          }
        }).catch(() => {
          callback();
        });
    }
  };

  onFilterChangeAssignee(input) {
    const { ObjectSchemeStore } = this.props;
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      ObjectSchemeStore.getUsers(input).then((res) => {
        this.setState({
          originUsers: res.list.filter(u => u.enabled),
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterUsers(input);
    }
  }

  render() {
    const { form, intl, ObjectSchemeStore } = this.props;
    const { getFieldDecorator } = form;
    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name,
    } = menu;
    const field = ObjectSchemeStore.getField;
    const {
      fieldOptions, submitting, defaultValue, isCheck,
      dateDisable, spinning, fieldContext, originUsers, selectLoading,
    } = this.state;

    return (
      <Page>
        <Header
          title={<FormattedMessage id="field.edit" />}
          backPath={`/agile/objectScheme/detail/${field.schemeCode}?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
        />
        <Spin spinning={spinning}>
          <Content>
            <Form layout="vertical" onSubmit={this.handleOk} className="c7n-sidebar-form">
              <FormItem
                {...formItemLayout}
                className="issue-sidebar-form"
              >
                {getFieldDecorator('name', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: 'required' }),
                  }, {
                    validator: this.checkName,
                  }],
                  initialValue: field.name,
                })(
                  <Input
                    maxLength={6}
                    label={<FormattedMessage id="name" />}
                  />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                className="issue-sidebar-form"
              >
                {getFieldDecorator('type', {
                  initialValue: field.fieldTypeName,
                })(
                  <Input
                    disabled
                    label={<FormattedMessage id="field.type" />}
                  />,
                )}
              </FormItem>
              <FormItem
                {...formItemLayout}
                className="issue-sidebar-form"
              >
                {getFieldDecorator('context', {
                  rules: [{
                    required: true,
                    message: '显示范围为必填项！',
                  }],
                  initialValue: field.context && field.context.slice(),
                })(
                  <Select
                    style={{ width: 520 }}
                    label={<FormattedMessage id="field.context" />}
                    dropdownMatchSelectWidth
                    size="default"
                    mode="multiple"
                  >
                    {fieldContext.map(ctx => (
                      <Option
                        value={ctx.valueCode}
                        key={ctx.valueCode}
                      >
                        {ctx.name}
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>
              {
                singleList.indexOf(field.fieldType) !== -1
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || [],
                          rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                        })(
                          <Select
                            label={<FormattedMessage id="field.default" />}
                            dropdownMatchSelectWidth
                            notFoundContent={intl.formatMessage({ id: 'field.value.null' })}
                            allowClear
                          >
                            {fieldOptions && fieldOptions.length > 0
                              && fieldOptions.map((item) => {
                                if (item.enabled) {
                                  return (
                                    <Option
                                      value={item.tempKey || item.id}
                                      key={item.tempKey || item.id}
                                    >
                                      {item.value}
                                    </Option>
                                  );
                                }
                                return [];
                              })}
                          </Select>,
                        )}
                      </FormItem>
                      <DragList
                        title={intl.formatMessage({ id: `field.${field.fieldType}` })}
                        data={fieldOptions}
                        tips={intl.formatMessage({ id: 'field.dragList.tips' })}
                        onChange={this.onTreeChange}
                        onCreate={this.onTreeCreate}
                        onDelete={this.onTreeDelete}
                        onInvalid={this.onTreeDelete}
                      />
                    </Fragment>
                  ) : ''
              }
              {
                multipleList.indexOf(field.fieldType) !== -1
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || [],
                          rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                        })(
                          <Select
                            label={<FormattedMessage id="field.default" />}
                            dropdownMatchSelectWidth
                            mode="multiple"
                            notFoundContent={intl.formatMessage({ id: 'field.value.null' })}
                          >
                            {fieldOptions && fieldOptions.length > 0
                              && fieldOptions.map((item) => {
                                if (item.enabled) {
                                  return (
                                    <Option
                                      value={item.tempKey || String(item.id)}
                                      key={item.tempKey || String(item.id)}
                                    >
                                      {item.value}
                                    </Option>
                                  );
                                }
                                return [];
                              })}
                          </Select>,
                        )}
                      </FormItem>
                      <DragList
                        title={intl.formatMessage({ id: `field.${field.fieldType}` })}
                        data={fieldOptions}
                        tips={intl.formatMessage({ id: 'field.dragList.tips' })}
                        onChange={this.onTreeChange}
                        onCreate={this.onTreeCreate}
                        onDelete={this.onTreeDelete}
                        onInvalid={this.onTreeDelete}
                      />
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'time'
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || null,
                          rules: [{ required: field.required && !dateDisable, message: '必填字段默认值不能为空！' }],
                        })(
                          <TimePicker
                            label={<FormattedMessage id="field.default" />}
                            defaultOpenValue={moment('00:00:00', 'HH:mm:ss')}
                            style={{ width: 520 }}
                            disabled={dateDisable}
                            allowEmpty
                          />,
                        )}
                      </FormItem>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => this.handleCheck(e, 'time')}>
                            <FormattedMessage id="field.useCurrentTime" />
                          </Checkbox>,
                        )}
                      </FormItem>
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'datetime'
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || null,
                          rules: [{ required: field.required && !dateDisable, message: '必填字段默认值不能为空！' }],
                        })(
                          <DatePicker
                            label={<FormattedMessage id="field.default" />}
                            format="YYYY-MM-DD HH:mm:ss"
                            showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                            style={{ width: 520 }}
                            disabled={dateDisable}
                            allowClear
                          />,
                        )}
                      </FormItem>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => this.handleCheck(e, 'datetime')}>
                            <FormattedMessage id="field.useCurrentDate" />
                          </Checkbox>,
                        )}
                      </FormItem>
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'date'
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || null,
                          rules: [{ required: field.required && !dateDisable, message: '必填字段默认值不能为空！' }],
                        })(
                          <DatePicker
                            label={<FormattedMessage id="field.default" />}
                            format="YYYY-MM-DD"                            
                            style={{ width: 520 }}
                            disabled={dateDisable}
                            allowClear
                          />,
                        )}
                      </FormItem>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => this.handleCheck(e, 'datetime')}>
                            <FormattedMessage id="field.useCurrentDate" />
                          </Checkbox>,
                        )}
                      </FormItem>
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'number'
                  ? (
                    <Fragment>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={this.handleCheck}>
                            <FormattedMessage id="field.decimal" />
                          </Checkbox>,
                        )}
                      </FormItem>
                      <FormItem
                        {...formItemLayout}
                        className="issue-sidebar-form"
                      >
                        {getFieldDecorator('defaultValue', {
                          initialValue: defaultValue || 0,
                          rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                        })(
                          <InputNumber
                            step={isCheck ? 0.1 : 1}
                            label={<FormattedMessage id="field.default" />}
                            maxLength={8}
                          />,
                        )}
                      </FormItem>
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'input'
                  ? (
                    <FormItem
                      {...formItemLayout}
                      className="issue-sidebar-form"
                    >
                      {getFieldDecorator('defaultValue', {
                        initialValue: defaultValue || '',
                        rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                      })(
                        <Input
                          label={<FormattedMessage id="field.default" />}
                          maxLength={100}
                        />,
                      )}
                    </FormItem>
                  ) : ''
              }
              {
                field.fieldType === 'text'
                  ? (
                    <FormItem
                      {...formItemLayout}
                      className="issue-sidebar-form"
                    >
                      {getFieldDecorator('defaultValue', {
                        initialValue: defaultValue || '',
                        rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                      })(
                        <TextArea
                          label={<FormattedMessage id="field.default" />}
                          maxLength={255}
                        />,
                      )}
                    </FormItem>
                  ) : ''
              }
              {
                field.fieldType === 'url'
                  ? (
                    <FormItem
                      {...formItemLayout}
                      className="issue-sidebar-form"
                    >
                      {getFieldDecorator('defaultValue', {
                        rules: [{
                          type: 'url',
                          message: intl.formatMessage({ id: 'field.urlError' }),
                        }, {
                          required: field.required,
                          message: '必填字段默认值不能为空！',
                        }],
                        initialValue: defaultValue || '',
                      })(
                        <Input
                          label={<FormattedMessage id="field.default" />}
                        />,
                      )}
                    </FormItem>
                  ) : ''
              }
              {
                field.fieldType === 'member'
                  ? (
                    <FormItem
                      {...formItemLayout}
                      className="issue-sidebar-form"
                    >
                      {getFieldDecorator('defaultValue', {
                        initialValue: defaultValue || [],
                        rules: [{ required: field.required, message: '必填字段默认值不能为空！' }],
                      })(
                        <Select
                          width="512px"
                          label={<FormattedMessage id="field.default" />}
                          dropdownMatchSelectWidth
                          notFoundContent="没有符合条件的用户"
                          allowClear
                          loading={selectLoading}
                          filter
                          filterOption={false}
                          onFilterChange={this.onFilterChangeAssignee.bind(this)}
                        >
                          {originUsers.map(user => (
                            <Option key={user.id} value={user.id}>
                              <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
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
                        </Select>,
                      )}
                    </FormItem>
                  ) : ''
              }
            </Form>
            <Divider />
            <Button
              type="primary"
              funcType="raised"
              onClick={this.saveEdit}
              className="issue-save-btn"
              loading={submitting}
            >
              <FormattedMessage id="save" />
            </Button>
            <Button
              funcType="raised"
              onClick={this.cancelEdit}
            >
              <FormattedMessage id="cancel" />
            </Button>
          </Content>
        </Spin>
      </Page>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(ObjectSchemeField)));
