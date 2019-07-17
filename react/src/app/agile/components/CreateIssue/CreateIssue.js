import React, { Component, Fragment } from 'react';
import { stores, axios, Content } from '@choerodon/boot';
import { map, find } from 'lodash';
import {
  Select, Form, Input, Button, Modal, Spin,
} from 'choerodon-ui';
import moment from 'moment';
import { UploadButton } from '../CommonComponent';
import {
  handleFileUpload, beforeTextUpload, validateFile, normFile, getProjectName,
} from '../../common/utils';
import IsInProgramStore from '../../stores/common/program/IsInProgramStore';
import {
  createIssue, getFields, createFieldValue, loadIssue,
} from '../../api/NewIssueApi';
import SelectNumber from '../SelectNumber';
import WYSIWYGEditor from '../WYSIWYGEditor';
import TypeTag from '../TypeTag';
import './CreateIssue.scss';
import SelectFocusLoad from '../SelectFocusLoad';
import renderField from './renderField';
import FieldIssueLinks from './FieldIssueLinks';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;

const defaultProps = {
  mode: 'default',
  request: createIssue,
  defaultTypeCode: 'story',
  title: '创建问题',
  contentTitle: `在项目“${getProjectName()}”中创建问题`,
  contentDescription: '请在下面输入问题的详细信息，包含详细描述、人员信息、版本信息、进度预估、优先级等等。您可以通过丰富的任务描述帮助相关人员更快更全面的理解任务，同时更好的把控问题进度。',
  contentLink: 'http://v0-16.choerodon.io/zh/docs/user-guide/agile/issue/create-issue/',
};
class CreateIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLoading: false,
      loading: true,
      originLabels: [],
      originComponents: [],
      originIssueTypes: [],
      defaultTypeId: false,
      newIssueTypeCode: '',
    };
  }

  componentDidMount() {
    this.loadIssueTypes();
  }

  /**
   * 为子任务和子bug设置默认冲刺
   *
   * @memberof CreateIssue
   */
  setDefaultSprint = () => {
    const { mode, parentIssueId, relateIssueId } = this.props;
    if (['sub_task', 'sub_bug'].includes(mode)) {
      loadIssue(parentIssueId || relateIssueId).then((res) => {
        const { form: { setFieldsValue } } = this.props;
        const { activeSprint } = res;
        if (activeSprint) {
          setFieldsValue({
            sprintId: activeSprint.sprintId,
          });
        }
      });
    }
  }

  getDefaultType = (issueTypes = this.state.originIssueTypes) => {
    const { defaultTypeCode } = this.props;
    return find(issueTypes, { typeCode: defaultTypeCode });
  }

  handleSave = (data, fileList) => {
    const { fields } = this.state;
    const {
      onOk, form, request,
    } = this.props;
    request(data).then((res) => {
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
          handleFileUpload(fileList, () => { }, config);
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

  loadIssueTypes = () => {
    axios.get(`/issue/v1/projects/${AppState.currentMenuType.projectId}/schemes/query_issue_types_with_sm_id?apply_type=agile`)
      .then((res) => {
        if (res && res.length) {
          const defaultType = this.getDefaultType(res);
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
            }, () => {
              this.setDefaultSprint();
            });
          });
        }
      });
  };

  getIssueLinks = (linkTypes, linkIssues) => {
    const issueLinkCreateVOList = [];
    linkTypes.forEach((link, index) => {
      if (link) {
        const [linkTypeId, isIn] = link.split('+');
        if (linkIssues[index]) {
          linkIssues[index].forEach((issueId) => {
            issueLinkCreateVOList.push({
              linkTypeId,
              linkedIssueId: issueId,
              in: isIn === 'true',
            });
          });
        }
      }
    });
    return issueLinkCreateVOList;
  }

  handleCreateIssue = () => {
    const { form, parentIssueId, relateIssueId } = this.props;
    const {
      originComponents,
      originLabels,
      originIssueTypes,
    } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const {
          typeId,
          summary,
          description,
          storyPoints,
          estimatedTime,
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
          linkTypes,
          linkIssues,
          fileList,
        } = values;
        const { typeCode } = originIssueTypes.find(t => t.id === typeId);
        const exitComponents = originComponents;
        const componentIssueRelVOList = map(componentIssueRel
          && componentIssueRel.filter(v => v && v.trim()), (component) => {
          const target = find(exitComponents, { name: component.trim() });
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
        const labelIssueRelVOList = map(issueLabel, (label) => {
          const target = find(exitLabels, { labelName: label });
          if (target) {
            return target;
          } else {
            return ({
              labelName: label,
              projectId: AppState.currentMenuType.id,
            });
          }
        });
        const fixVersionIssueRelVOList = map(fixVersionIssueRel
          && fixVersionIssueRel.filter(v => v && v.trim()), versionId => ({
          versionId,
          relationType: 'fix',
        }));
        const issueLinkCreateVOList = this.getIssueLinks(linkTypes, linkIssues);

        const extra = {
          issueTypeId: typeId,
          typeCode,
          summary: summary.trim(),
          priorityId,
          priorityCode: `priority-${priorityId}`,
          sprintId: sprintId || 0,
          epicId: epicId || 0,
          epicName,
          parentIssueId: parentIssueId || 0, // 子任务
          relateIssueId: relateIssueId || 0, // 子bug
          assigneeId: assigneedId,
          labelIssueRelVOList,
          versionIssueRelVOList: fixVersionIssueRelVOList,
          componentIssueRelVOList,
          storyPoints,
          remainingTime: estimatedTime,
          issueLinkCreateVOList,
          featureVO: {
            benfitHypothesis,
            acceptanceCritera,
            featureType,
          },
        };
        this.setState({ createLoading: true });     
        const deltaOps = description;
        if (deltaOps) {
          beforeTextUpload(deltaOps, extra, (data) => {
            this.handleSave(data, fileList);
          });
        } else {
          extra.description = '';
          this.handleSave(extra, fileList);
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
    const { id } = AppState.userInfo;
    const { form } = this.props;
    form.setFieldsValue({
      assigneedId: id,
    });
  };


  getIssueTypes = () => {
    const { originIssueTypes } = this.state;
    return IsInProgramStore.isInProgram
      ? originIssueTypes.filter(type => (!['issue_epic', 'feature', 'sub_task'].includes(type.typeCode)))
      : originIssueTypes.filter(type => (!['feature', 'sub_task'].includes(type.typeCode)));
  }

  setDefaultSelect = field => (list, defaultValue) => {
    const { form } = this.props;
    form.setFieldsValue({
      [field]: defaultValue,
    });
  }

  getFieldComponent = (field) => {
    const { form, mode } = this.props;
    const { getFieldDecorator } = form;
    const {
      defaultValue, fieldName, fieldCode, fieldType, required,
    } = field;
    const {
      originIssueTypes,
      newIssueTypeCode, defaultTypeId,
    } = this.state;
    switch (field.fieldCode) {
      case 'issueType':
        return (
          [
            ['sub_task', 'sub_bug'].includes(mode)
              ? getFieldDecorator('typeId', {
                rules: [{ required: true, message: '问题类型为必输项' }],
                initialValue: defaultTypeId || '',
              })
              : (
                <FormItem label="问题类型">
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
                        getFields(param, typeCode).then((res) => {
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
                </FormItem>
              ),
            newIssueTypeCode === 'feature' ? (
              <FormItem>
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
          <FormItem label="经办人">
            <div style={{ display: 'flex', alignItems: 'center' }}>
              {getFieldDecorator('assigneedId', {})(
                <SelectFocusLoad
                  type="user"
                  label="经办人"
                  style={{ flex: 1 }}
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  allowClear
                />,
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
            {getFieldDecorator('sprintId', {})(
              <SelectFocusLoad
                label="冲刺"
                allowClear
                type="sprint"
                disabled={['sub_task', 'sub_bug'].includes(mode)}
              />,
            )}
          </FormItem>
        );
      case 'priority':
        return (
          <FormItem label="优先级">
            {getFieldDecorator('priorityId', {
              rules: [{ required: true, message: '优先级为必选项' }],
            })(
              <SelectFocusLoad
                label="优先级"
                type="priority"
                afterLoad={this.setDefaultSelect('priorityId')}
              />,
            )}
          </FormItem>
        );
      case 'label':
        return (
          <FormItem label="标签">
            {getFieldDecorator('issueLabel', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
              normalize: value => (value ? value.map(s => s.toString().substr(0, 10)) : value), // 限制最长10位
            })(
              <SelectFocusLoad
                label="标签"
                mode="tags"
                loadWhenMount
                type="label"
              />,
            )}
          </FormItem>
        );
      case 'fixVersion':
        return (
          <FormItem label="版本">
            {getFieldDecorator('fixVersionIssueRel', {
              rules: [{ transform: value => (value ? value.toString() : value) }],
            })(
              <SelectFocusLoad
                label="版本"
                mode="multiple"
                type="version"
              />,
            )}
          </FormItem>
        );
      case 'epic':
        return (
          ['issue_epic', 'sub_task'].includes(newIssueTypeCode) ? null : (
            <FormItem label="史诗">
              {getFieldDecorator('epicId', {})(
                <SelectFocusLoad
                  label="史诗"
                  allowClear
                  type="epic"
                />,
              )}
            </FormItem>
          )
        );
      case 'component':
        return (
          !['sub_task'].includes(newIssueTypeCode) ? null : (
            <FormItem label="模块">
              {getFieldDecorator('componentIssueRel', {
                rules: [{ transform: value => (value ? value.toString() : value) }],
              })(
                <SelectFocusLoad
                  label="模块"
                  mode="multiple"
                  type="component"
                  allowClear
                />,
              )}
            </FormItem>
          )
        );
      case 'summary':
        return (
          // 切换类型时将组件卸载，保证切换到史诗时的聚焦生效
          <FormItem label="概要" className="c7nagile-line" key={`${newIssueTypeCode}-summary`}>
            {getFieldDecorator('summary', {
              rules: [{ required: true, message: '概要为必输项', whitespace: true }],
            })(
              <Input autoFocus={newIssueTypeCode !== 'issue_epic'} label="概要" maxLength={44} />,
            )}
          </FormItem>
        );
      case 'epicName':
        return (
          newIssueTypeCode === 'issue_epic' && (
            <FormItem label="史诗名称" className="c7nagile-line">
              {getFieldDecorator('epicName', {
                rules: [{ required: true, message: '史诗名称为必输项' }, {
                  validator: this.checkEpicNameRepeat,
                }],
              })(
                <Input autoFocus label="史诗名称" maxLength={20} />,
              )}
            </FormItem>
          )
        );
      case 'remainingTime':
        return (
          newIssueTypeCode !== 'issue_epic' && (
            <FormItem>
              {getFieldDecorator('estimatedTime')(
                <SelectNumber
                  label="预估时间"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                />,
              )}
            </FormItem>
          )
        );
      case 'storyPoints':
        return (
          newIssueTypeCode === 'story' && (
            <FormItem>
              {getFieldDecorator('storyPoints')(
                <SelectNumber
                  label="故事点"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                />,
              )}
            </FormItem>
          )
        );
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
              {getFieldDecorator('fileList', {
                valuePropName: 'fileList',
                getValueFromEvent: normFile,
                rules: [{
                  validator: validateFile,
                }],
              })(
                <UploadButton />,
              )}
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
              renderField(field),
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


  renderIssueLinks = () => {
    const { newIssueTypeCode } = this.state;
    const { form: { getFieldDecorator, getFieldValue } } = this.props;
    getFieldDecorator('keys', { initialValue: [] });
    const keys = getFieldValue('keys');
    if (newIssueTypeCode !== 'issue_epic') {
      return keys.map((k, index) => (
        <div style={{ display: 'flex' }} key={k}>
          <div style={{ flex: 1, display: 'flex' }}>
            <FormItem label="关系" style={{ width: '30%' }}>
              {getFieldDecorator(`linkTypes[${index}]`, {
              })(
                <SelectFocusLoad
                  label="关系"
                  type="issue_link"
                />,
              )}
            </FormItem>
            <FormItem label="问题" style={{ marginLeft: 20, width: 'calc(70% - 20px)' }}>
              {getFieldDecorator(`linkIssues[${index}]`, {
              })(
                <SelectFocusLoad
                  label="问题"
                  type="issues_in_link"
                />,
              )}
            </FormItem>
          </div>
          <div style={{ marginTop: 10, width: 70, marginLeft: 20 }}>
            <Button
              shape="circle"
              icon="add"
              onClick={this.add}
            />
            {
              keys.length > 1 ? (
                <Button
                  shape="circle"
                  style={{ marginLeft: 10 }}
                  icon="delete"
                  onClick={() => this.remove(k)}
                />
              ) : null
            }
          </div>
        </div>
      ));
    }
    return null;
  }


  render() {
    const {
      visible, form, parentSummary, title, mode,
      contentTitle,
      contentDescription,
      contentLink,
    } = this.props;
    const {
      createLoading, fields, loading, newIssueTypeCode,
    } = this.state;
    return (
      <Sidebar
        className="c7n-createIssue"
        title={title}
        visible={visible}
        onOk={this.handleCreateIssue}
        onCancel={this.handleCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <Content
          title={contentTitle}
          description={contentDescription}
          link={contentLink}
        >
          <Spin spinning={loading}>
            <Form layout="vertical" style={{ width: 670 }} className="c7nagile-form">
              <div className="c7nagile-createIssue-fields">
                {['sub_task', 'sub_bug'].includes(mode) && (
                  <FormItem>
                    <Input label="父任务概要" value={parentSummary} disabled />
                  </FormItem>
                )}
                {fields && fields.map(field => this.getFieldComponent(field))}
              </div>
              {newIssueTypeCode !== 'issue_epic' && <FieldIssueLinks form={form} />}
            </Form>
          </Spin>
        </Content>
      </Sidebar>
    );
  }
}
CreateIssue.defaultProps = defaultProps;
export default Form.create({})(CreateIssue);
