import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Select, Form, Input, Button, Modal, Icon, InputNumber,
  Checkbox, TimePicker, Row, Col, Radio, DatePicker, Spin,
} from 'choerodon-ui';
import moment from 'moment';
import { stores, Content } from 'choerodon-front-boot';
import WYSIWYGEditor from '../../../../../components/WYSIWYGEditor';
import FullEditor from '../../../../../components/FullEditor';
import UploadButton from '../../../../../components/CommonComponent/UploadButton';
import TypeTag from '../../../../../components/TypeTag';
import {
  loadPriorities, loadProgramEpics, loadIssueTypes, createIssue,
  createFieldValue, getFields,
} from '../../../../../api/NewIssueApi';
import { getPISelect } from '../../../../../api/PIApi';
import { beforeTextUpload, handleFileUpload } from '../../../../../common/utils';
import './CreateFeature.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const { TextArea } = Input;
const FormItem = Form.Item;
const storyPointList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

@observer
class CreateFeature extends Component {
  constructor(props) {
    super(props);
    this.state = {
      originEpics: [],
      selectedIssueType: undefined,
      defaultPriority: undefined,
      storyPoints: '',
      fullEdit: false,
      delta: '',
      fileList: [],
      selectLoading: false,
      loading: true,
      PIList: [],
    };
  }

  componentDidMount() {
    loadIssueTypes('program').then((res) => {
      this.setState({
        selectedIssueType: res.find(item => item.typeCode === 'feature'),
      });
    });

    loadPriorities().then((res) => {
      this.setState({
        defaultPriority: res.find(item => item.default) || res[0],
      });
    });
    this.loadFields();
    getPISelect().then((res) => {
      this.setState({
        PIList: res,
      });
    });
  }

  loadFields = () => {
    const param = {
      schemeCode: 'agile_issue',
      context: 'feature',
      pageCode: 'agile_issue_create',
    };
    getFields(param).then((fields) => {
      this.setState({
        fields,
        loading: false,
      });
    });
  };

  handleOnOk = () => {
    const { form } = this.props;
    const {
      selectedIssueType, defaultPriority, storyPoints, delta,
    } = this.state;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        const issueObj = {
          projectId: AppState.currentMenuType.id,
          programId: AppState.currentMenuType.id,
          issueTypeId: selectedIssueType.id,
          typeCode: 'feature',
          summary: values.summary,
          priorityId: defaultPriority.id,
          priorityCode: `priority-${defaultPriority.id}`,
          epicId: values.epicId || 0,
          piId: values.pi || 0,
          parentIssueId: 0,
          storyPoints,
          featureDTO: {
            benfitHypothesis: values.benfitHypothesis,
            acceptanceCritera: values.acceptanceCritera,
            featureType: values.featureType,
          },
        };
        const deltaOps = delta;
        if (deltaOps) {
          beforeTextUpload(deltaOps, issueObj, this.handleCreateFeature);
        } else {
          issueObj.description = '';
          this.handleCreateFeature(issueObj);
        }
      }
    });
  };

  handleCreateFeature = (issueObj) => {
    const { form, onOk } = this.props;
    const { fileList, fields } = this.state;
    const fileUpdateCallback = () => {
      this.setState({ fileList: [] });
    };
      
    createIssue(issueObj, 'program').then((res) => {
      const fieldList = [];
      fields.forEach((item) => {
        if (!item.system) {
          let value = form.getFieldValue(item.fieldCode);
          if (item.fieldType === 'time' || item.fieldType === 'datetime') {
            value = value.format('YYYY-MM-DD HH:mm:ss');
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
          handleFileUpload(fileList, fileUpdateCallback, config);
        }
      }
      Choerodon.prompt('创建成功');
      this.resetForm();
      if (onOk) {
        onOk();
      }
    }).catch(() => {
      Choerodon.prompt('创建失败');
    });
  };
 
  resetForm = () => {
    const { form } = this.props;
    form.resetFields();
    this.setState({
      storyPoints: '',
      delta: '',
      fileList: [],
    });
    this.editor.setEditorContents(this.editor.getEditor(), '');
  };

  handleOnCancel = () => {
    const { onCancel } = this.props;
    this.resetForm();
    if (onCancel) {
      onCancel();
    }
  };

  handleEpicFilterChange = () => {
    this.setState({
      selectLoading: true,
    });
    loadProgramEpics().then((res) => {
      this.setState({
        originEpics: res,
        selectLoading: false,
      });
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

  handleFullEditOnOk = (value) => {
    this.setState({
      delta: value,
      fullEdit: false,
    });
  };

  handleFullEditonCancel = () => {
    this.setState({
      fullEdit: false,
    });
  };

  setFileList = (data) => {
    this.setState({ fileList: data });
  };

  renderField = (field) => {
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

  getFieldComponent = (field) => {
    const { form } = this.props;
    const { getFieldDecorator } = form;
    const {
      defaultValue, fieldName, fieldCode, fieldType, required,
    } = field;
    const {
      originEpics, delta, selectLoading, PIList,
      selectedIssueType, storyPoints, fullEdit,
    } = this.state;

    let featureTypeList = [];
    if (selectedIssueType) {
      featureTypeList = [
        {
          ...selectedIssueType,
          colour: '#29B6F6',
          typeCode: 'business',
          name: '特性',
        }, {
          ...selectedIssueType,
          colour: '#FFCA28',
          typeCode: 'enabler',
          name: '使能',
        },
      ];
    }

    switch (field.fieldCode) {
      case 'issueType':
        return (
          selectedIssueType ? (
            <FormItem label="问题类型" style={{ width: 520, marginBottom: 20 }}>
              {getFieldDecorator('featureType', {
                rules: [{ required: true, message: '问题类型为必输项' }],
                initialValue: 'business',
              })(
                <Select
                  label="问题类型"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  {featureTypeList.map(type => (
                    <Option key={type.typeCode} value={type.typeCode}>
                      <div style={{ display: 'inline-flex', alignItems: 'center', padding: '0 2px' }}>
                        <TypeTag
                          data={type}
                          showName
                        />
                      </div>
                    </Option>
                  ))
                  }
                </Select>,
              )}
            </FormItem>
          ) : ''
        );
      case 'assignee':
        return '';
      case 'sprint':
        return '';
      case 'priority':
        return '';
      case 'label':
        return '';
      case 'fixVersion':
        return '';
      case 'epic':
        return (
          <FormItem label="史诗" style={{ width: 520, marginBottom: 15 }}>
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
                onFilterChange={this.handleEpicFilterChange}
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
        );
      case 'component':
        return '';
      case 'summary':
        return (
          <FormItem label="特性名称" style={{ width: 520, marginBottom: 20 }}>
            {getFieldDecorator('summary', {
              rules: [{ required: true, message: '特性名称为必输项' }],
            })(
              <Input label="特性名称" maxLength={44} />,
            )}
          </FormItem>
        );
      case 'epicName':
        return '';
      case 'remainingTime':
        return '';
      case 'storyPoints':
        return (
          <div style={{ width: 520, paddingBottom: 8, marginBottom: 12 }}>
            <Select
              label="故事点"
              value={storyPoints && storyPoints.toString()}
              mode="combobox"
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
        );
      case 'description':
        return (
          <div style={{ width: 520 }}>
            <div style={{ display: 'flex', marginBottom: 3, alignItems: 'center' }}>
              <div style={{ fontWeight: 'bold' }}>描述</div>
              <div style={{ marginLeft: 80 }}>
                <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ fullEdit: true })} style={{ display: 'flex', alignItems: 'center' }}>
                  <Icon type="zoom_out_map" style={{ color: '#3f51b5', fontSize: '18px', marginRight: 12 }} />
                  <span style={{ color: '#3f51b5' }}>全屏编辑</span>
                </Button>
              </div>
            </div>
            {
              !fullEdit && (
                <div className="clear-p-mw">
                  <WYSIWYGEditor
                    saveRef={(editor) => { this.editor = editor; }}
                    value={delta}
                    style={{ height: 200, width: '100%' }}
                    onChange={(value) => {
                      this.setState({ delta: value });
                    }}
                  />
                </div>
              )
            }
          </div>
        );
      case 'benfitHypothesis':
        return (
          <FormItem style={{ width: 520, marginBottom: 15 }}>
            {getFieldDecorator('benfitHypothesis', {
            })(
              <Input label="特性价值" placeholder="请输入特性价值" maxLength={100} />,
            )}
          </FormItem>
        );
      case 'acceptanceCritera':
        return (
          <FormItem style={{ width: 520 }}>
            {getFieldDecorator('acceptanceCritera', {
            })(
              <Input label="验收标准" placeholder="请输入验收标准" maxLength={100} />,
            )}
          </FormItem>
        );
      case 'pi':
        return (
          <FormItem label="PI" style={{ width: 520, marginBottom: 20 }}>
            {getFieldDecorator('pi')(
              <Select
                label="PI"
                getPopupContainer={triggerNode => triggerNode.parentNode}
              >
                {PIList.map(pi => (
                  <Option key={pi.id} value={pi.id}>
                    {`${pi.code}-${pi.name}`}
                  </Option>
                ))
                }
              </Select>,
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
      if (fieldType === 'time' || fieldType === 'datetime') {
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
    const { visible } = this.props;
    const {
      fullEdit, delta, fields, fileList, loading,
    } = this.state;

    return (
      <Sidebar
        className="c7n-feature-createFeatureSideBar"
        title="创建特性"
        visible={visible}
        cancelText="取消"
        okText="创建"
        onOk={this.handleOnOk}
        onCancel={this.handleOnCancel}
      >
        <Content
          title="在项目群中创建特性"
          description="请在下面输入问题的详细信息，包含详细描述、特性价值、验收标准等等。您可以通过丰富的问题描述帮助相关人员更快更全面的理解任务，同时更好的把控问题进度。"
          link=""
        >
          <Spin spinning={loading}>
            <Form>
              {fields && fields.map(field => this.getFieldComponent(field))}
            </Form>

            <div className="c7n-feature-signUpload" style={{ marginTop: 20, height: 33 }}>
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
            {
              fullEdit ? (
                <FullEditor
                  initValue={delta}
                  visible={fullEdit}
                  onCancel={this.handleFullEditonCancel}
                  onOk={this.handleFullEditOnOk}
                />
              ) : null
            }
          </Spin>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(CreateFeature);
