import React, { Component } from 'react';
import { stores, axios, Content } from '@choerodon/boot';
import _ from 'lodash';
import {
  Select, Form, Input, Button, Modal, Icon, InputNumber,
  Checkbox, TimePicker, Row, Col, Radio, DatePicker, Spin,
} from 'choerodon-ui';
import moment from 'moment';
import { UploadButton } from '../CommonComponent';
import {
  handleFileUpload, beforeTextUpload, randomString,
} from '../../common/utils';
import IsInProgramStore from '../../stores/common/program/IsInProgramStore';
import IssueLinkType from '../../api/IssueLinkType';
import {
  createIssue, loadLabels, loadPriorities, loadVersions,
  loadSprints, loadComponents, loadEpics, loadIssuesInLink,
  getFields, createFieldValue,
} from '../../api/NewIssueApi';
import { getUsers } from '../../api/CommonApi';
import WYSIWYGEditor from '../WYSIWYGEditor';
import UserHead from '../UserHead';
import TypeTag from '../TypeTag';
import './CreateIssue.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;
let sign = false;

const storyPointList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

class CreateIssue extends Component {
  debounceFilterUsers = _.debounce((input) => {
    this.setState({ selectLoading: true });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.list.filter(u => u.enabled),
        selectLoading: false,
      });
    });
  }, 500);

  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    loadIssuesInLink(1, 20, undefined, input).then((res) => {
      this.setState({
        originIssues: res.list,
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      fileList: [],
      selectLoading: true,
      loading: true,
      originLabels: [],
      originComponents: [],
      originEpics: [],
      originPriorities: [],
      originFixVersions: [],
      originSprints: [],
      originUsers: [],
      originIssueTypes: [],
      defaultPriority: false,
      defaultTypeId: false,
      newIssueTypeCode: '',
      storyPoints: '',
      estimatedTime: '',
      issueLinkArr: [randomString(5)],
      links: [],
      originLinks: [],
      originIssues: [],
    };
  }

  componentDidMount() {
    this.loadPriorities();
    this.loadIssueTypes();
  }

  onFilterChangeAssignee(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input).then((res) => {
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

  onIssueSelectFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      loadIssuesInLink(1, 20, undefined, input).then((res) => {
        this.setState({
          originIssues: res.list,
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  getLinks() {
    this.setState({
      selectLoading: true,
    });
    IssueLinkType.queryAll().then((res) => {
      this.setState({
        selectLoading: false,
        links: res.list,
        originLinks: res.list,
      });
      this.transform(res.list);
    });
  }

  setFileList = (data) => {
    this.setState({ fileList: data });
  };

  handleSave = (data) => {
    const { fileList, fields } = this.state;
    const { onOk, form } = this.props;
    const callback = (newFileList) => {
      this.setState({ fileList: newFileList });
    };
    createIssue(data)
      .then((res) => {
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
        form.resetFields();
        this.setState({
          createLoading: false,
        });
        onOk(res);
      })
      .catch(() => {
        form.resetFields();
        this.setState({
          createLoading: false,
        });
        onOk();
      });
  };

  loadPriorities = () => {
    loadPriorities().then((res) => {
      const defaultPriorities = res.filter(p => p.default);
      this.setState({
        originPriorities: res,
        defaultPriority: defaultPriorities.length ? defaultPriorities[0] : '',
      });
    });
  };

  loadIssueTypes = () => {
    axios.get(`/issue/v1/projects/${AppState.currentMenuType.projectId}/schemes/query_issue_types_with_sm_id?apply_type=agile`)
      .then((res) => {
        if (res && res.length) {
          const story = res.filter(item => item.typeCode === 'story');
          const defaultType = (story && story.length) ? story[0] : res[0];
          const param = {
            schemeCode: 'agile_issue',
            context: defaultType.typeCode,
            pageCode: 'agile_issue_create',
          };
          getFields(param).then((fields) => {
            this.setState({
              fields,
              originIssueTypes: res,
              defaultTypeId: defaultType.id,
              loading: false,
              newIssueTypeCode: defaultType.typeCode,
            });
          });
        }
      });
  };

  handleChangeStoryPoint = (value) => {
    const { storyPoints } = this.state;
    // 只允许输入整数，选择时可选0.5
    if (value === '0.5') {
      this.setState({
        storyPoints: '0.5',
      });
    } else if (/^(0|[1-9][0-9]*)(\[0-9]*)?$/.test(value) || value === '') {
      this.setState({
        storyPoints: String(value).slice(0, 3),
      });
    } else if (value.toString().charAt(value.length - 1) === '.') {
      this.setState({
        storyPoints: value.slice(0, -1),
      });
    } else {
      this.setState({
        storyPoints,
      });
    }
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

  transform = (links) => {
    // split active and passive
    const active = links.map(link => ({
      name: link.outWard,
      linkTypeId: link.linkTypeId,
    }));
    const passive = [];
    links.forEach((link) => {
      if (link.inWard !== link.outWard) {
        passive.push({
          name: link.inWard,
          linkTypeId: link.linkTypeId,
        });
      }
    });
    this.setState({
      links: active.concat(passive),
    });
  };

  handleCreateIssue = () => {
    const { form } = this.props;
    const {
      originComponents,
      originLabels,
      originFixVersions,
      originIssueTypes,
      storyPoints,
      estimatedTime,
      originLinks,
    } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const {
          typeId,
          summary,
          description,
          sprintId,
          epicId,
          epicName,
          assigneedId,
          benfitHypothesis,
          acceptanceCritera,
          featureType,
          componentIssueRel,
          priorityId,
          issueLabel,
          fixVersionIssueRel,
          linkTypeId,
          linkIssues,
        } = values;
        const { typeCode } = originIssueTypes.find(t => t.id === typeId);
        const exitComponents = originComponents;
        const componentIssueRelDTOList = _.map(componentIssueRel
          && componentIssueRel.filter(v => v && v.trim()), (component) => {
          const target = _.find(exitComponents, { name: component.trim() });
          if (target) {
            return target;
          } else {
            return ({
              name: component.trim(),
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitLabels = originLabels;
        const labelIssueRelDTOList = _.map(issueLabel, (label) => {
          const target = _.find(exitLabels, { labelName: label });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const exitFixVersions = originFixVersions;
        const fixVersionIssueRelDTOList = _.map(fixVersionIssueRel
          && fixVersionIssueRel.filter(v => v && v.trim()), (version) => {
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
        const issueLinkCreateDTOList = [];
        if (linkTypeId) {
          Object.keys(linkTypeId).forEach((link) => {
            if (linkTypeId[link] && linkIssues[link]) {
              const currentLinkType = _.find(originLinks, { linkTypeId: linkTypeId[link].split('+')[0] * 1 });
              linkIssues[link].forEach((issueId) => {
                if (currentLinkType.inWard === linkTypeId[link].split('+')[1]) {
                  issueLinkCreateDTOList.push({
                    linkTypeId: linkTypeId[link].split('+')[0] * 1,
                    linkedIssueId: issueId * 1,
                    in: false,
                  });
                } else {
                  issueLinkCreateDTOList.push({
                    linkTypeId: linkTypeId[link].split('+')[0] * 1,
                    linkedIssueId: issueId * 1,
                    in: true,
                  });
                }
              });
            }
          });
        }

        const extra = {
          issueTypeId: typeId,
          typeCode,
          summary: summary.trim(),
          priorityId,
          priorityCode: `priority-${priorityId}`,
          sprintId: sprintId || 0,
          epicId: epicId || 0,
          epicName,
          parentIssueId: 0,
          assigneeId: assigneedId,
          labelIssueRelDTOList,
          versionIssueRelDTOList: fixVersionIssueRelDTOList,
          componentIssueRelDTOList,
          storyPoints,
          remainingTime: estimatedTime,
          issueLinkCreateDTOList,
          featureDTO: {
            benfitHypothesis,
            acceptanceCritera,
            featureType,
          },
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

  handleCancel = () => {
    const { onCancel, form } = this.props;
    form.resetFields();
    this.setState({
      createLoading: false,
    });
    if (onCancel) {
      onCancel();
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
            className="fieldWith"
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
            className="fieldWith"
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
            className="fieldWith"
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
            className="fieldWith"
          >
            <span style={{ color: '#D50000' }}>暂无选项，请联系管理员</span>
          </Checkbox.Group>
        );
      }
    } else if (field.fieldType === 'time') {
      return (
        <TimePicker
          label={fieldName}
          className="fieldWith"
          defaultOpenValue={moment('00:00:00', 'HH:mm:ss')}
          allowEmpty={!required}
        />
      );
    } else if (field.fieldType === 'datetime') {
      return (
        <DatePicker
          showTime
          label={fieldName}
          format="YYYY-MM-DD HH:mm:ss"
          className="fieldWith"
          allowClear={!required}
        />
      );
    } else if (field.fieldType === 'date') {
      return (
        <DatePicker
          label={fieldName}
          format="YYYY-MM-DD"
          className="fieldWith"
          allowClear={!required}
        />
      );
    } else if (field.fieldType === 'single') {
      return (
        <Select
          label={fieldName}
          dropdownMatchSelectWidth
          className="fieldWith"
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
          className="fieldWith"
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
          className="fieldWith"
          step={field.extraConfig === '1' ? 0.1 : 1}
          maxLength={8}
        />
      );
    } else if (field.fieldType === 'text') {
      return (
        <TextArea
          autosize
          label={fieldName}
          className="fieldWith"
          maxLength={255}
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
          onFilterChange={this.onFilterChangeAssignee.bind(this)}
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
          className="fieldWith"
          maxLength={100}
        />
      );
    }
  };

  getIssueTypes = () => {
    const { originIssueTypes } = this.state;
    return IsInProgramStore.isInProgram
      ? originIssueTypes.filter(type => (!['issue_epic', 'feature', 'sub_task'].includes(type.typeCode)))
      : originIssueTypes.filter(type => (!['feature', 'sub_task'].includes(type.typeCode)));
  }

  getFieldComponent = (field) => {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    const {
      defaultValue, fieldName, fieldCode, fieldType, required,
    } = field;
    const {
      originIssueTypes, originPriorities, defaultPriority, storyPoints,
      originUsers, selectLoading, estimatedTime,
      originEpics, originSprints, originFixVersions, originComponents,
      originLabels, newIssueTypeCode, defaultTypeId,
    } = this.state;
    switch (field.fieldCode) {
      case 'issueType':
        return (
          [
            <FormItem label="问题类型" style={{ width: 520 }}>
              {getFieldDecorator('typeId', {
                rules: [{ required: true, message: '问题类型为必输项' }],
                initialValue: defaultTypeId || '',
              })(
                <Select
                  label="问题类型"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  onChange={((value) => {
                    const { typeCode } = originIssueTypes.find(item => item.id === value);
                    this.setState({
                      newIssueTypeCode: typeCode,
                    });
                    const param = {
                      schemeCode: 'agile_issue',
                      context: typeCode,
                      pageCode: 'agile_issue_create',
                    };
                    getFields(param).then((res) => {
                      this.setState({
                        fields: res,
                      });
                    });
                  })}
                >
                  {this.getIssueTypes().map(type => (
                    <Option key={type.id} value={type.id}>
                      <TypeTag
                        data={type}
                        showName
                      />
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>,
            newIssueTypeCode === 'feature' ? (
              <FormItem style={{ width: 520 }}>
                {getFieldDecorator('featureType', {
                  rules: [{ required: true, message: '特性类型为必输项' }],
                  initialValue: 'business',
                })(
                  <Select
                    label="特性类型"
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                  >
                    <Option key="business" value="business">
                      特性
                    </Option>
                    <Option key="enabler" value="enabler">
                      使能
                    </Option>
                  </Select>,
                )}
              </FormItem>
            ) : null]
        );
      case 'assignee':
        return (
          <React.Fragment>
            <FormItem label="经办人" style={{ width: 520, display: 'inline-block' }}>
              {getFieldDecorator('assigneedId', {})(
                <Select
                  label="经办人"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  loading={selectLoading}
                  filter
                  filterOption={false}
                  allowClear
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
            <span
              onClick={this.assigneeMe}
              role="none"
              style={{
                display: 'inline-block',
                color: 'rgba(63, 81, 181)',
                marginLeft: 10,
                marginTop: 15,
                cursor: 'pointer',
              }}
            >
              {'分派给我'}
            </span>
          </React.Fragment>
        );
      case 'sprint':
        return (
          <FormItem label="冲刺" style={{ width: 520 }}>
            {getFieldDecorator('sprintId', {})(
              <Select
                label="冲刺"
                allowClear
                filter
                filterOption={
                  (input, option) => option.props.children && option.props.children.toLowerCase().indexOf(
                    input.toLowerCase(),
                  ) >= 0
                }
                getPopupContainer={triggerNode => triggerNode.parentNode}
                loading={selectLoading}
                onFilterChange={() => {
                  this.setState({
                    selectLoading: true,
                  });
                  loadSprints(['sprint_planning', 'started']).then((res) => {
                    this.setState({
                      originSprints: res,
                      selectLoading: false,
                    });
                  });
                }}
              >
                {originSprints.map(sprint => (
                  <Option key={sprint.sprintId} value={sprint.sprintId}>
                    {sprint.sprintName}
                  </Option>
                ))}
              </Select>,
            )}
          </FormItem>
        );
      case 'priority':
        return (
          <FormItem label="优先级" style={{ width: 520 }}>
            {getFieldDecorator('priorityId', {
              rules: [{ required: true, message: '优先级为必选项' }],
              initialValue: defaultPriority ? defaultPriority.id : '',
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
          <FormItem label="标签" style={{ width: 520 }}>
            {getFieldDecorator('issueLabel', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
              normalize: value => (value ? value.map(s => s.toString().substr(0, 10)) : value), // 限制最长10位
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
          <FormItem label="版本" style={{ width: 520 }}>
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
                {
                  originFixVersions.map(
                    version => (
                      <Option
                        key={version.name}
                        value={version.name}
                      >
                        {version.name}
                      </Option>
                    ),
                  )}
              </Select>,
            )}
          </FormItem>
        );
      case 'epic':
        return (
          newIssueTypeCode !== 'issue_epic' && (
            <FormItem label="史诗" style={{ width: 520 }}>
              {getFieldDecorator('epicId', {})(
                <Select
                  label="史诗"
                  allowClear
                  filter
                  filterOption={
                    (input, option) => option.props.children && option.props.children.toLowerCase().indexOf(
                      input.toLowerCase(),
                    ) >= 0
                  }
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  loading={selectLoading}
                  onFilterChange={() => {
                    this.setState({
                      selectLoading: true,
                    });
                    loadEpics().then((res) => {
                      this.setState({
                        originEpics: res,
                        selectLoading: false,
                      });
                    });
                  }}
                >
                  {originEpics.map(
                    epic => (
                      <Option
                        key={epic.issueId}
                        value={epic.issueId}
                      >
                        {epic.epicName}
                      </Option>
                    ),
                  )}
                </Select>,
              )}
            </FormItem>
          )
        );
      case 'component':
        return (
          <FormItem label="模块" style={{ width: 520 }}>
            {getFieldDecorator('componentIssueRel', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
            })(
              <Select
                label="模块"
                mode="multiple"
                loading={selectLoading}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                tokenSeparators={[',']}
                onFocus={() => {
                  this.setState({
                    selectLoading: true,
                  });
                  loadComponents().then((res) => {
                    this.setState({
                      originComponents: res.list,
                      selectLoading: false,
                    });
                  });
                }}
              >
                {
                  originComponents.map(
                    component => (
                      <Option
                        key={component.name}
                        value={component.name}
                      >
                        {component.name}
                      </Option>
                    ),
                  )}
              </Select>,
            )}
          </FormItem>
        );
      case 'summary':
        return (
          <FormItem label="概要" style={{ width: 520 }}>
            {getFieldDecorator('summary', {
              rules: [{ required: true, message: '概要为必输项', whitespace: true }],
            })(
              <Input autoFocus label="概要" maxLength={44} />,
            )}
          </FormItem>
        );
      case 'epicName':
        return (
          newIssueTypeCode === 'issue_epic' && (
            <FormItem label="史诗名称" style={{ width: 520 }}>
              {getFieldDecorator('epicName', {
                rules: [{ required: true, message: '史诗名称为必输项' }, {
                  validator: this.checkEpicNameRepeat,
                }],
              })(
                <Input label="史诗名称" maxLength={20} />,
              )}
            </FormItem>
          )
        );
      case 'remainingTime':
        return (
          newIssueTypeCode !== 'issue_epic' && (
            <div style={{ width: 520, paddingBottom: 8, marginBottom: 12 }}>
              <Select
                label="预估时间"
                value={estimatedTime && estimatedTime.toString()}
                getPopupContainer={triggerNode => triggerNode.parentNode}
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
            </div>
          )
        );
      case 'storyPoints':
        return (
          newIssueTypeCode === 'story' && (
            <div style={{ width: 520, paddingBottom: 8, marginBottom: 12 }}>
              <Select
                label="故事点"
                value={storyPoints && storyPoints.toString()}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                mode="combobox"
                ref={(e) => {
                  this.componentRef = e;
                }}
                onPopupFocus={() => {
                  this.componentRef.rcSelect.focus();
                }}
                tokenSeparators={[',']}
                style={{ marginTop: 0, paddingTop: 0 }}
                onChange={value => this.handleChangeStoryPoint(value)}
              >
                {storyPointList.map(sp => (
                  <Option key={sp.toString()} value={sp}>
                    {sp}
                  </Option>
                ))}
              </Select>
            </div>
          )
        );
      case 'description':
        return (
          <FormItem label={fieldName} style={{ width: 520 }}>
            {getFieldDecorator(fieldCode)(
              <WYSIWYGEditor
                style={{ height: 200, width: '100%' }}
              />,
            )}
          </FormItem>
        );
      default:
        return (
          <FormItem label={fieldName} style={{ width: 520 }}>
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
      visible,
      form,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      createLoading, selectLoading, fields, loading,
      fileList, newIssueTypeCode, issueLinkArr, originIssues, links,
    } = this.state;
    return (
      <Sidebar
        className="c7n-createIssue"
        title="创建问题"
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={this.handleCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <Content
          title={`在项目“${AppState.currentMenuType.name}”中创建问题`}
          description="请在下面输入问题的详细信息，包含详细描述、人员信息、版本信息、进度预估、优先级等等。您可以通过丰富的任务描述帮助相关人员更快更全面的理解任务，同时更好的把控问题进度。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/issue/create-issue/"
        >
          <Spin spinning={loading}>
            <Form layout="vertical">
              {fields && fields.map(field => this.getFieldComponent(field))}
              {
                newIssueTypeCode !== 'issue_epic' && (
                  issueLinkArr && issueLinkArr.length > 0 && (
                    issueLinkArr.map((item, index, arr) => (
                      <Row gutter={16} style={{ width: 520 }}>
                        <Col span={8}>
                          <FormItem label="关系">
                            {getFieldDecorator(`linkTypeId[${item}]`, {
                            })(
                              <Select
                                label="关系"
                                loading={selectLoading}
                                getPopupContainer={triggerNode => triggerNode.parentNode}
                                tokenSeparators={[',']}
                                onFocus={() => {
                                  this.getLinks();
                                }}
                              >
                                {links.map(link => (
                                  <Option key={`${link.linkTypeId}+${link.name}`} value={`${link.linkTypeId}+${link.name}`}>
                                    {link.name}
                                  </Option>
                                ))}
                              </Select>,
                            )}
                          </FormItem>
                        </Col>
                        <Col span={12}>
                          <FormItem label="问题">
                            {getFieldDecorator(`linkIssues[${item}]`, {
                            })(
                              <Select
                                label="问题"
                                mode="multiple"
                                loading={selectLoading}
                                optionLabelProp="showName"
                                filter
                                filterOption={false}
                                onFilterChange={this.onIssueSelectFilterChange.bind(this)}
                                getPopupContainer={triggerNode => triggerNode.parentNode}
                              >
                                {originIssues.map(issue => (
                                  <Option
                                    key={issue.issueId}
                                    value={issue.issueId}
                                    showName={issue.issueNum}
                                  >
                                    <div style={{
                                      display: 'inline-flex',
                                      flex: 1,
                                      width: 'calc(100% - 30px)',
                                      alignItems: 'center',
                                      verticalAlign: 'bottom',
                                    }}
                                    >
                                      <TypeTag
                                        data={issue.issueTypeDTO}
                                      />
                                      <span style={{
                                        paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                                      }}
                                      >
                                        {issue.issueNum}
                                      </span>
                                      <div style={{ overflow: 'hidden', flex: 1 }}>
                                        <p style={{
                                          paddingRight: '25px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
                                        }}
                                        >
                                          {issue.summary}
                                        </p>
                                      </div>
                                    </div>
                                  </Option>
                                ))}
                              </Select>,
                            )}
                          </FormItem>
                        </Col>
                        <Col span={4} style={{ marginTop: 10 }}>
                          <Button
                            shape="circle"
                            icon="add"
                            onClick={() => {
                              arr.splice(index + 1, 0, randomString(5));
                              this.setState({
                                issueLinkArr: arr,
                              });
                            }}
                          />
                          {
                            issueLinkArr.length > 1 ? (
                              <Button
                                shape="circle"
                                style={{ marginLeft: 10 }}
                                icon="delete"
                                onClick={() => {
                                  arr.splice(index, 1);
                                  this.setState({
                                    issueLinkArr: arr,
                                  });
                                }}
                              />
                            ) : null
                          }
                        </Col>
                      </Row>
                    )))
                )
              }
            </Form>
            <div className="sign-upload" style={{ marginTop: 20 }}>
              <div style={{ display: 'flex', marginBottom: '13px', alignItems: 'center' }}>
                <div style={{ fontWeight: 'bold' }}>附件</div>
              </div>
              <div style={{ marginTop: -38 }}>
                <UploadButton
                  onRemove={this.setFileList}
                  onBeforeUpload={this.setFileList}
                  fileList={fileList}
                />
              </div>
            </div>
          </Spin>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(CreateIssue);
