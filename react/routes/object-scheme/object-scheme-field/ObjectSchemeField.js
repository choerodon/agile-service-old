import React, {
  useState, Fragment, useEffect, useContext, 
} from 'react';
import { observer } from 'mobx-react-lite';
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
import UserHead from '../../../components/UserHead';
import { randomString } from '../../../common/utils';
import './ObjectSchemeField.scss';
import DragList from '../components/drag-list';
import Store from '../stores';

const { AppState } = stores;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const singleList = ['radio', 'single'];
const multipleList = ['checkbox', 'multiple'];
const dateList = ['time', 'datetime', 'date'];
const textList = ['input', 'text', 'url'];
const dateFormat = 'YYYY-MM-DD HH:mm:ss';


let sign = false;

function ObjectSchemeField(props) {
  const context = useContext(Store);

  const { match, objectSchemeStore, intl } = context;
  const { form } = props;
  // use State
  // const [id, setId] = useState(match.params.id);
  const [fieldOptions, setFieldOptions] = useState([]);
  const [defaultValue, setDefaultValue] = useState('');
  const [isCheck, setIsCheck] = useState(false);
  const [dateDisable, setDateDisable] = useState(false);
  const [spinning, setSpinning] = useState(true);
  const [fieldContext, setFieldContext] = useState([]);
  const [submitting, setSubmitting] = useState(false);
  // 这两个可以合并
  const [selectLoading, setSelectLoading] = useState(true);
  const [originUsers, setOriginUsers] = useState([]);

  const debounceFilterUsers = _.debounce((input) => {
    setSelectLoading(true);
    objectSchemeStore.getUsers(input).then((res) => {
      setOriginUsers(res.list.filter(u => u.enabled));
      setSelectLoading(false);
    });
  }, 500);


  const initCurrentMenuType = () => {
    objectSchemeStore.initCurrentMenuType(AppState.currentMenuType);
  };

  const loadContext = () => {
    objectSchemeStore.loadLookupValue('object_scheme_field_context').then((res) => {
      if (!res.failed) {
        setFieldContext(res.lookupValues);
      }
    });
  };

  const loadFieldById = () => {
    objectSchemeStore.loadFieldDetail(match.params.id).then((data) => {
      if (data) {
        if (singleList.indexOf(data.fieldType) !== -1) {
          // 单选
          const defaultOption = data.fieldOptions ? data.fieldOptions.filter(f => f.isDefault).map(f => f.id) : [];
          setFieldOptions(data.fieldOptions || []);
          setDefaultValue(defaultOption.length ? defaultOption[0] : []);
        } else if (multipleList.indexOf(data.fieldType) !== -1) {
          // 多选
          const newDefaultValue = data.fieldOptions ? data.fieldOptions.filter(f => f.isDefault).map(f => String(f.id)) : [];
          setFieldOptions(data.fieldOptions || []);
          setDefaultValue(newDefaultValue);
        } else if (dateList.indexOf(data.fieldType) !== -1) {
          // 时间日期
          let newDefaultValue = data.defaultValue && moment(data.defaultValue);
          if (data.extraConfig) {
            newDefaultValue = moment();
          }

          setIsCheck(data.extraConfig);
          setDefaultValue(newDefaultValue);
          setDateDisable(data.extraConfig);
        } else if (data.fieldType === 'number') {
          // 数字
          const newDefaultValue = data.defaultValue && Number(data.defaultValue);

          setIsCheck(data.extraConfig);
          setDefaultValue(newDefaultValue);
        } else if (textList.indexOf(data.fieldType) !== -1) {
          // 文本

          setDefaultValue(data.defaultValue);
        } else if (data.fieldType === 'member') {
          objectSchemeStore.getUsers('', data.defaultValue).then((res) => {
            setOriginUsers(res.list.filter(u => u.enabled));
            setSelectLoading(false);
            setDefaultValue(Number(data.defaultValue));
          });
        }

        setSpinning(false);
      }
    });
  };

  const cancelEdit = () => {
    const { history } = context;
    const field = objectSchemeStore.getField;
    objectSchemeStore.setField({});
    // const {
    //   name, id, organizationId, type,
    // } = AppState.currentMenuType;
    const {
      name, organizationId, type,
    } = AppState.currentMenuType;
    history.push(`/issue/objectScheme/detail/${field.schemeCode}?type=${type}&id=${match.params.id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`);
  };

  const onTreeChange = (newFieldOptions) => {
    setFieldOptions(newFieldOptions);
  };

  const saveEdit = () => {
    form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        // this.setState({
        //   submitting: true,
        // });
        setSubmitting(true);
        const field = objectSchemeStore.getField;
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
        objectSchemeStore.updateField(match.params.id, postData)
          .then(() => {
            // this.setState({
            //   submitting: false,
            // });
            setSubmitting(false);
            cancelEdit();
          });
      }
    });
  };

  const onTreeCreate = (code, value) => {
    setFieldOptions([...fieldOptions, {
      enabled: true,
      status: 'add',
      code,
      value,
      tempKey: randomString(5),
    }]);
  };

  const onTreeDelete = (tempKey) => {
    const field = objectSchemeStore.getField;
    const newDefaultValue = form.getFieldValue('defaultValue');
    if (multipleList.indexOf(field.fieldType) !== -1) {
      const newValue = newDefaultValue.filter(v => v !== String(tempKey));
      form.setFieldsValue({ newDefaultValue: newValue });
    } else if (singleList.indexOf(field.fieldType) !== -1) {
      if (newDefaultValue === tempKey) {
        form.setFieldsValue({ newDefaultValue: '' });
      }
    }
  };

  const handleCheck = (e, type) => {
    if (dateList.indexOf(type) !== -1) {
      form.setFieldsValue({ defaultValue: moment() });
    }

    setIsCheck(e.target.checked);
    setDateDisable(e.target.checked);
  };

  const checkName = (rule, value, callback) => {
    // const { objectSchemeStore, intl } = this.props;
    const field = objectSchemeStore.getField;
    const name = objectSchemeStore.getField ? objectSchemeStore.getField.name : false;
    if ((name && value === name) || !value) {
      callback();
    } else {
      objectSchemeStore.checkName(value, field.schemeCode)
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

  function onFilterChangeAssignee(input) {
    if (!sign) {
      setSelectLoading(true);
      objectSchemeStore.getUsers(input).then((res) => {
        setOriginUsers(res.list.filter(u => u.enabled));
        setSelectLoading(false);
      });
      sign = true;
    } else {
      debounceFilterUsers(input);
    }
  }
  useEffect(() => {
    initCurrentMenuType();
    loadContext();
    loadFieldById();
  }, []);


  // const {
  //   type, id, organizationId, name,
  // } = menu;

  function render() {
    const { getFieldDecorator, getFieldValue } = form;
    const menu = AppState.currentMenuType;
    const {
      type, id, organizationId, name,
    } = menu;
    const field = objectSchemeStore.getField;

    const selectedContext = getFieldValue('context') || [];
    return (
      <Page>
        <Header
          title={<FormattedMessage id="field.edit" />}
          backPath={`/issue/objectScheme/detail/${field.schemeCode}?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}`}
        />
        <Spin spinning={spinning}>
          <Content>
            {/* this.handleOk */}
            <Form layout="vertical" onSubmit={null} style={{ width: 520 }} className="c7nagile-form">
              <FormItem>
                {getFieldDecorator('name', {
                  rules: [{
                    required: true,
                    whitespace: true,
                    message: intl.formatMessage({ id: 'required' }),
                  }, {
                    validator: checkName,
                  }],
                  initialValue: field.name,
                })(
                  <Input
                    maxLength={6}
                    label={<FormattedMessage id="name" />}
                  />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('type', {
                  initialValue: field.fieldTypeName,
                })(
                  <Input
                    disabled
                    label={<FormattedMessage id="field.type" />}
                  />,
                )}
              </FormItem>
              <FormItem>
                {getFieldDecorator('context', {
                  rules: [{
                    required: true,
                    message: '显示范围为必填项！',
                  }],
                  initialValue: field.context && field.context.slice(),
                })(
                  <Select
                    label={<FormattedMessage id="field.context" />}
                    dropdownMatchSelectWidth
                    showCheckAll={false}
                    size="default"
                    mode="multiple"
                  >
                    {fieldContext.map(ctx => (
                      <Option
                        disabled={ctx.valueCode === 'global' ? selectedContext.length > 0 && !selectedContext.includes('global') : selectedContext.includes('global')}
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
                      <FormItem>
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
                        onChange={onTreeChange}
                        onCreate={onTreeCreate}
                        onDelete={onTreeDelete}
                        onInvalid={onTreeDelete}
                      />
                    </Fragment>
                  ) : ''
              }
              {
                multipleList.indexOf(field.fieldType) !== -1
                  ? (
                    <Fragment>
                      <FormItem>
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
                        onChange={onTreeChange}
                        onCreate={onTreeCreate}
                        onDelete={onTreeDelete}
                        onInvalid={onTreeDelete}
                      />
                    </Fragment>
                  ) : ''
              }
              {
                field.fieldType === 'time'
                  ? (
                    <Fragment>
                      <FormItem>
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
                      <FormItem>
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => handleCheck(e, 'time')}>
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
                      <FormItem>
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
                      <FormItem>
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => handleCheck(e, 'datetime')}>
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
                      <FormItem>
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
                      <FormItem>
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={e => handleCheck(e, 'datetime')}>
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
                      <FormItem>
                        {getFieldDecorator('check', {
                          valuePropName: 'checked',
                          initialValue: isCheck || false,
                        })(
                          <Checkbox onChange={handleCheck}>
                            <FormattedMessage id="field.decimal" />
                          </Checkbox>,
                        )}
                      </FormItem>
                      <FormItem>
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
                    <FormItem>
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
                    <FormItem>
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
                    <FormItem>
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
                    <FormItem>
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
                          onFilterChange={onFilterChangeAssignee.bind(this)}
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
              onClick={saveEdit}
              className="issue-save-btn"
              loading={submitting}
            >
              <FormattedMessage id="save" />
            </Button>
            <Button
              funcType="raised"
              onClick={cancelEdit}
            >
              <FormattedMessage id="cancel" />
            </Button>
          </Content>
        </Spin>
      </Page>
    );
  }
  return render();
}

// export default observer(Form.create({})(ObjectSchemeField));
// export default observer(ObjectSchemeField);
export default Form.create({})(observer(ObjectSchemeField));
