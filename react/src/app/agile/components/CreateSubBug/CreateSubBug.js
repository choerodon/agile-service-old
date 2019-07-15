import React, { Component, Fragment } from 'react';
import { stores } from '@choerodon/boot';
import _ from 'lodash';
import {
  Select, Form, Input, Button, Modal, Icon, InputNumber,
  Checkbox, TimePicker, Row, Col, Radio, DatePicker, Spin,
} from 'choerodon-ui';
import moment from 'moment';
import { UploadButton } from '../CommonComponent';
import { handleFileUpload, beforeTextUpload } from '../../common/utils';
import {
  loadIssue, loadLabels, loadPriorities, loadVersions,
  createIssue, getFields, createFieldValue,
} from '../../api/NewIssueApi';
import { getUsers } from '../../api/CommonApi';
import WYSIWYGEditor from '../WYSIWYGEditor';
import UserHead from '../UserHead';
import './CreateSubBug.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;

let sign = false;

const storyPointList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

class CreateSubBug extends Component {
  debounceFilterIssues = _.debounce((input) => {
    this.setState({ selectLoading: true });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.list,
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      fileList: [],
      sprint: {},
      selectLoading: true,
      originLabels: [],
      originPriorities: [],
      originFixVersions: [],
      originUsers: [],
      defaultPriorityId: false,
      estimatedTime: '',
      loading: true,
    };
  }

  componentDidMount() {
    const { issueId } = this.props;
    loadIssue(issueId).then((res) => {
      this.setState({
        sprint: {
          sprintId: res.activeSprint ? res.activeSprint.sprintId || undefined : undefined,
          sprintName: res.activeSprint ? res.activeSprint.sprintName || '' : undefined,
        },
      });
    });
    this.loadPriority();
    this.loadFields();
  }

  onFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input).then((res) => {
        this.setState({
          originUsers: res.list,
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  setFileList = (data) => {
    this.setState({ fileList: data });
  };

  loadFields = () => {
    const param = {
      schemeCode: 'agile_issue',
      context: 'bug',
      pageCode: 'agile_issue_create',
    };
    getFields(param).then((fields) => {
      const Fields = fields.filter(({ fieldCode }) => !['issueType', 'epic', 'component'].includes(fieldCode));
      this.setState({
        fields: Fields,
        loading: false,
      });
    });
  };

  loadPriority = () => {
    loadPriorities().then((res) => {
      const defaultPriorities = res.filter(p => p.default);
      this.setState({
        originPriorities: res,
        defaultPriorityId: defaultPriorities.length ? defaultPriorities[0].id : '',
      });
    });
  };

  handleCreateIssue = () => {
    const { sprint, estimatedTime } = this.state;
    const {
      store, form, issueId,
    } = this.props;
    const { originLabels, originFixVersions } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const { description } = values;
        const subIssueType = store.getIssueTypes && store.getIssueTypes.find(t => t.typeCode === 'bug');
        const exitLabels = originLabels;
        const labelIssueRelVOList = _.map(values.issueLink, (label) => {
          const target = _.find(exitLabels, { labelName: label.substr(0, 10) });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label.substr(0, 10),
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitFixVersions = originFixVersions;
        const fixVersionIssueRelVOList = _.map(values.fixVersionIssueRel
          && values.fixVersionIssueRel.filter(v => v && v.trim()), (version) => {
          const target = _.find(exitFixVersions, { name: version.trim() });
          if (target) {
            return {
              ...target,
              relationType: 'fix',
            };
          } else {
            return ({
              name: version.trim(),
              relationType: 'fix',
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const extra = {
          summary: values.summary,
          priorityId: values.priorityId,
          priorityCode: `priority-${values.priorityId}`,
          assigneeId: values.assigneedId,
          projectId: AppState.currentMenuType.id,
          labelIssueRelVOList,
          sprintId: sprint.sprintId || 0,
          versionIssueRelVOList: fixVersionIssueRelVOList,
          issueTypeId: subIssueType && subIssueType.id,
          typeCode: 'bug',
          remainingTime: estimatedTime,
          relateIssueId: issueId,
        };
        this.setState({ createLoading: true });
        const deltaOps = description;
        if (deltaOps) {
          beforeTextUpload(deltaOps, extra, this.handleSave);
        } else {
          extra.description = '';
          this.handleSave(extra);
        }
      }
    });
  };

  handleSave = (data) => {
    const { fileList, fields } = this.state;
    const { issueId, onOk, form } = this.props;
    const callback = (newFileList) => {
      this.setState({ fileList: newFileList });
    };
    createIssue(data).then((res) => {
      const fieldList = [];
      fields.forEach((item) => {
        if (!item.system) {
          let value = form.getFieldValue(item.fieldCode);
          if (item.fieldType === 'time' || item.fieldType === 'datetime' || item.fieldType === 'date') {
            value = value && value.format('YYYY-MM-DD HH:mm:ss');
          }
          fieldList.push({
            fieldType: item.fieldType,
            value,
            fieldId: item.fieldId,
          });
        }
      });
      createFieldValue(res.issueId, 'agile_issue', fieldList);
      if (fileList.length > 0) {
        const config = {
          issueType: res.statusId,
          issueId: res.issueId,
          fileName: fileList[0].name,
          projectId: AppState.currentMenuType.id,
        };
        if (fileList.some(one => !one.url)) {
          handleFileUpload(fileList, callback, config);
        }
      }
      onOk(res);
    })
      .catch(() => {
      });
  };

  handleChangeEstimatedTime = (value) => {
    const { estimatedTime } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        estimatedTime: '0.5',
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        estimatedTime: String(value).slice(0, 3), // 限制最长三位
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        estimatedTime: value.slice(0, -1),
      });
    } else {
      this.setState({
        estimatedTime,
      });
    }
  };

  // 分派给我
  assigneeMe = () => {
    const {
      id, imageUrl, loginName, realName,
    } = AppState.userInfo;
    const { originUsers } = this.state;
    const { form } = this.props;
    const newUsers = originUsers.filter(user => user.id !== id);
    this.setState({
      originUsers: [
        ...newUsers,
        {
          id,
          imageUrl,
          loginName,
          realName,
          enabled: true,
        },
      ],
    }, () => {
      form.setFieldsValue({
        assigneedId: id,
      });
    });
  };

  renderField = (field) => {
    const { selectLoading, originUsers } = this.state;
    const {
      fieldOptions, fieldType, required, fieldName,
    } = field;
    if (fieldType === 'radio') {
      if (fieldOptions && fieldOptions.length > 0) {
        return (
          <Radio.Group
            label={fieldName}

          >
            {fieldOptions && fieldOptions.length > 0
              && fieldOptions.filter(option => option.enabled).map(item => (
                <Radio
                  className="radioStyle"
                  value={item.id}
                  key={item.id}
                >
                  {item.value}
                </Radio>
              ))}
          </Radio.Group>
        );
      } else {
        return (
          <Radio.Group
            label={fieldName}

          >
            <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
          </Radio.Group>
        );
      }
    } else if (field.fieldType === 'checkbox') {
      if (fieldOptions && fieldOptions.length > 0) {
        return (
          <Checkbox.Group
            label={fieldName}

          >
            <Row>
              {fieldOptions && fieldOptions.length > 0
                && fieldOptions.filter(option => option.enabled).map(item => (
                  <Col
                    span={24}
                    key={item.id}
                  >
                    <Checkbox
                      value={item.id}
                      key={item.id}
                      className="checkboxStyle"
                    >
                      {item.value}
                    </Checkbox>
                  </Col>
                ))}
            </Row>
          </Checkbox.Group>
        );
      } else {
        return (
          <Checkbox.Group
            label={fieldName}

          >
            <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
          </Checkbox.Group>
        );
      }
    } else if (field.fieldType === 'time') {
      return (
        <TimePicker
          label={fieldName}
          style={{ display: 'block' }}
          defaultOpenValue={moment('00:00:00', 'HH:mm:ss')}
          allowEmpty={!required}
        />
      );
    } else if (field.fieldType === 'datetime') {
      return (
        <DatePicker
          label={fieldName}
          format="YYYY-MM-DD HH:mm:ss"
          style={{ display: 'block' }}
          allowClear={!required}
        />
      );
    } else if (field.fieldType === 'date') {
      return (
        <DatePicker
          label={fieldName}
          format="YYYY-MM-DD"
          style={{ display: 'block' }}
          allowClear={!required}
        />
      );
    } else if (field.fieldType === 'single') {
      return (
        <Select
          label={fieldName}
          dropdownMatchSelectWidth

          allowClear={!required}
        >
          {field.fieldOptions && field.fieldOptions.length > 0
            && field.fieldOptions.filter(option => option.enabled).map(item => (
              <Option
                value={item.id}
                key={item.id}
              >
                {item.value}
              </Option>
            ))}
        </Select>
      );
    } else if (field.fieldType === 'multiple') {
      return (
        <Select
          label={fieldName}
          dropdownMatchSelectWidth
          mode="multiple"

        >
          {field.fieldOptions && field.fieldOptions.length > 0
            && field.fieldOptions.filter(option => option.enabled).map(item => (
              <Option
                value={item.id}
                key={item.id}
              >
                {item.value}
              </Option>
            ))}
        </Select>
      );
    } else if (field.fieldType === 'number') {
      return (
        <InputNumber
          label={fieldName}

          step={field.extraConfig === '1' ? 0.1 : 1}
        />
      );
    } else if (field.fieldType === 'text') {
      return (
        <TextArea
          autosize
          label={fieldName}

        />
      );
    } else if (field.fieldType === 'member') {
      return (
        <Select
          label={fieldName}
          loading={selectLoading}
          filter
          filterOption={false}
          allowClear
          onFilterChange={this.onFilterChange.bind(this)}
        >
          {originUsers.filter(user => (field.defaultValueObj ? user.id !== field.defaultValueObj.id : true) && user.enabled).concat(field.defaultValueObj || []).map(user => (
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
        </Select>
      );
    } else {
      return (
        <Input
          label={fieldName}

        />
      );
    }
  };

  getFieldComponent = (field) => {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    const {
      defaultValue, fieldName, fieldCode, fieldType, required,
    } = field;
    const {
      originPriorities, defaultPriorityId,
      originUsers, selectLoading, estimatedTime,
      originFixVersions, originLabels, sprint,
    } = this.state;
    switch (field.fieldCode) {
      case 'issueType':
        return '';
      case 'assignee':
        return (
          <FormItem label="经办人">
            <div style={{ display: 'flex', alignItems: 'center' }}>
              {getFieldDecorator('assigneedId', {})(
                <Select
                  style={{ flex: 1 }}
                  label="经办人"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  loading={selectLoading}
                  filter
                  filterOption={false}
                  allowClear
                  onFilterChange={this.onFilterChange.bind(this)}
                >
                  {originUsers.filter(u => u.enabled).map(user => (
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
              <span
                onClick={this.assigneeMe}
                role="none"
                style={{
                  display: 'inline-block',
                  color: 'rgba(63, 81, 181)',
                  marginLeft: 10,
                  cursor: 'pointer',
                }}
              >
                {'分派给我'}
              </span>
            </div>
          </FormItem>
        );
      case 'sprint':
        return (
          <FormItem label="冲刺">
            {getFieldDecorator('sprintId', {
              initialValue: sprint.sprintName,
            })(
              <Input label="冲刺" disabled />,
            )}
          </FormItem>
        );
      case 'priority':
        return (
          <FormItem label="优先级">
            {getFieldDecorator('priorityId', {
              rules: [{ required: true, message: '优先级为必选项' }],
              initialValue: defaultPriorityId,
            })(
              <Select
                label="优先级"
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {originPriorities.filter(p => p.enable).map(priority => (
                  <Option key={priority.id} value={priority.id}>
                    <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
                      <span>{priority.name}</span>
                    </div>
                  </Option>
                ))}
              </Select>,
            )}
          </FormItem>
        );
      case 'label':
        return (
          <FormItem label="标签">
            {getFieldDecorator('issueLink', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
              normalize: value => (value ? value.map(s => s.toString().substr(0, 10)) : value),
            })(
              <Select
                label="标签"
                mode="tags"
                loading={selectLoading}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                tokenSeparators={[',']}
                onFocus={() => {
                  this.setState({
                    selectLoading: true,
                  });
                  loadLabels().then((res) => {
                    this.setState({
                      originLabels: res,
                      selectLoading: false,
                    });
                  });
                }}
              >
                {originLabels.map(label => (
                  <Option key={label.labelName} value={label.labelName}>
                    {label.labelName}
                  </Option>
                ))}
              </Select>,
            )}
          </FormItem>
        );
      case 'fixVersion':
        return (
          <FormItem label="版本">
            {getFieldDecorator('fixVersionIssueRel', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
            })(
              <Select
                label="版本"
                mode="multiple"
                loading={selectLoading}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                tokenSeparators={[',']}
                onFocus={() => {
                  this.setState({
                    selectLoading: true,
                  });
                  loadVersions(['version_planning', 'released']).then((res) => {
                    this.setState({
                      originFixVersions: res,
                      selectLoading: false,
                    });
                  });
                }}
              >
                {originFixVersions.map(version => <Option key={version.name} value={version.name}>{version.name}</Option>)}
              </Select>,
            )}
          </FormItem>
        );
      case 'epic':
        return '';
      case 'component':
        return '';
      case 'summary':
        return (
          <FormItem label="缺陷概要" className="c7nagile-line">
            {getFieldDecorator('summary', {
              rules: [{ required: true, message: '缺陷概要为必输项' }],
            })(
              <Input autoFocus label="缺陷概要" maxLength={44} />,
            )}
          </FormItem>
        );
      case 'epicName':
        return '';
      case 'remainingTime':
        return (
          <FormItem>
            <Select
              label="预估时间"
              value={estimatedTime && estimatedTime.toString()}
              mode="combobox"
              ref={(e) => {
                this.componentRef = e;
              }}
              onPopupFocus={() => {
                this.componentRef.rcSelect.focus();
              }}
              tokenSeparators={[',']}
              style={{ marginTop: 0, paddingTop: 0 }}
              onChange={value => this.handleChangeEstimatedTime(value)}
            >
              {storyPointList.map(sp => (
                <Option key={sp.toString()} value={sp}>
                  {sp}
                </Option>
              ))}
            </Select>
          </FormItem>
        );
      case 'storyPoints':
        return '';
      case 'description':
        return (
          <Fragment>
            <FormItem label={fieldName} className="c7nagile-line">
              {getFieldDecorator(fieldCode)(
                <WYSIWYGEditor
                  style={{ height: 200, width: '100%' }}
                />,
              )}
            </FormItem>
            <FormItem className="c7nagile-line">
              <UploadButton
                onRemove={this.setFileList}
                onBeforeUpload={this.setFileList}
                fileList={this.state.fileList}
              />
            </FormItem>
          </Fragment>
        );
      default:
        return (
          <FormItem label={fieldName}>
            {getFieldDecorator(fieldCode, {
              rules: [{ required, message: `${fieldName}为必填项` }],
              initialValue: this.transformValue(fieldType, defaultValue),
            })(
              this.renderField(field),
            )}
          </FormItem>
        );
    }
  };

  transformValue = (fieldType, value) => {
    if (value) {
      if (fieldType === 'time' || fieldType === 'datetime' || fieldType === 'date') {
        return value ? moment(value) : undefined;
      } else if (value instanceof Array) {
        return value.slice();
      } else {
        return value;
      }
    } else {
      return undefined;
    }
  };

  render() {
    const {
      visible, onCancel, parentSummary,
    } = this.props;

    const {
      createLoading,
      fields,
      fileList,
      loading,
    } = this.state;

    return (
      <Sidebar
        className="c7n-createSubIssue"
        title="创建缺陷"
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <Spin spinning={loading}>

          <Form layout="vertical" style={{ width: 670 }} className="c7nagile-form">
            <h2>
              {'在项目“'}
              {AppState.currentMenuType.name}
              {' ”中创建缺陷'}
            </h2>
            <p style={{ marginBottom: 24 }}>
              {' 请在下面输入缺陷的详细信息，创建问题的缺陷。缺陷会与父级问题的冲刺、史诗保持一致，并且缺陷的状态会受父级问题的限制。'}
            </p>
            <div className="c7nagile-createIssue-fields">
              <FormItem>
                <Input label="父任务概要" value={parentSummary} disabled />
              </FormItem>
              {fields && fields.map(field => this.getFieldComponent(field))}
            </div>
          </Form>
        </Spin>
      </Sidebar>
    );
  }
}
export default Form.create({})(CreateSubBug);
