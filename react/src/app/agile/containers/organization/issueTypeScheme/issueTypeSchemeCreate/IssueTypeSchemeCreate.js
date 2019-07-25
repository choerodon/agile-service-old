import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Modal, Form, Select, Input, message, Icon,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';
import TransferDrag from '../../../../components/TransferDrag';
import './IssueTypeSchemeCreate.scss';
import TypeTag from '../../../../components/TypeTag/TypeTag';


const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
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

@observer
class IssueTypeSchemeCreate extends Component {
  constructor(props) {
    super(props);
    this.state = {
      submitting: false,
      target: [],
      origin: [],
      dragValidator: true,
    };
  }

  componentDidMount() {
    this.loadScheme();
  }

  onDragChange = (target, origin) => {
    // 如果默认类型不在已选内，清空
    const { form } = this.props;
    const defaultIssueTypeId = form.getFieldValue('defaultIssueTypeId');
    if (!target.filter(t => t.id === defaultIssueTypeId).length) {
      form.setFieldsValue({ defaultIssueTypeId: '' });
    }
    this.setState({
      dragValidator: !!target.length,
      target,
      origin,
    });
  };

  loadScheme = () => {
    const { store, id } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    if (id) {
      store.loadIssueTypes(orgId).then((list) => {
        if (list) {
          store.loadSchemeById(orgId, id).then((data) => {
            if (data && data.issueTypes) {
              data.issueTypes.map((issueType) => {
                list = list.filter(t => t.id !== issueType.id);
                return list;
              });
              this.setState({ target: data.issueTypes, origin: list });
            }
          });
        }
      });
    } else {
      store.loadIssueTypes(orgId).then((data) => {
        if (data) {
          this.setState({ origin: data });
        }
      });
    }
  };

  handleClose = () => {
    const { store, form, onClose } = this.props;
    store.setScheme(false);
    form.resetFields();
    onClose();
  };

  handleSubmit = () => {
    const {
      store, id, intl, form,
    } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const objectVersionNumber = store.scheme ? store.scheme.objectVersionNumber : 0;
    const { target } = this.state;
    form.validateFieldsAndScroll((err, data) => {
      if (!err && target.length) {
        const postData = data;
        // 如果未设置默认值，传0
        if (!data.defaultIssueTypeId) {
          postData.defaultIssueTypeId = 0;
        }
        postData.organizationId = orgId;
        postData.objectVersionNumber = objectVersionNumber;
        postData.issueTypes = target;
        this.setState({
          submitting: true,
          dragValidator: true,
        });
        if (id && store.createSchemeShow === 'edit') {
          store.updateScheme(orgId, id, postData)
            .then((res) => {
              if (res) {
                message.success(intl.formatMessage({ id: 'editSuccess' }));
              } else {
                message.error(intl.formatMessage({ id: 'editFailed' }));
              }
              this.setState({
                submitting: false,
              });
              this.handleClose();
            }).catch(() => {
              message.error(intl.formatMessage({ id: 'editFailed' }));
              this.setState({
                submitting: false,
              });
              this.handleClose();
            });
        } else {
          store.createScheme(orgId, postData)
            .then(() => {
              message.success(intl.formatMessage({ id: 'createSuccess' }));
              this.setState({
                submitting: false,
              });
              this.handleClose();
            }).catch(() => {
              message.error(intl.formatMessage({ id: 'createFailed' }));
              this.setState({
                submitting: false,
              });
            });
        }
      } else {
        this.setState({ dragValidator: false });
      }
    });
  };

  checkName = (rule, value, callback) => {
    const { store, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const name = store.scheme ? store.scheme.name : false;
    if ((name && value === name) || !value) {
      callback();
    } else {
      store.checkName(orgId, value)
        .then((data) => {
          if (data) {
            callback();
          } else {
            callback(intl.formatMessage({ id: 'issueTypeScheme.name.check.exist' }));
          }
        }).catch((error) => {
          callback();
        });
    }
  };

  render() {
    const {
      visible, intl, store, form,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      target, origin, dragValidator, submitting,
    } = this.state;
    let name = store.scheme ? store.scheme.name : '';
    const description = store.scheme ? store.scheme.description : '';
    const defaultIssueTypeId = store.scheme && store.scheme.defaultIssueTypeId ? store.scheme.defaultIssueTypeId : '';
    let titleId = 'issueTypeScheme.create';
    if (store.createSchemeShow === 'edit') {
      titleId = 'issueTypeScheme.edit';
    } else if (store.createSchemeShow === 'copy') {
      name = name ? `Copy ${name}` : '';
      titleId = 'issueTypeScheme.copy';
    }
    return (
      <Sidebar
        title={<FormattedMessage id={titleId} />}
        visible={visible}
        onOk={this.handleSubmit}
        onCancel={this.handleClose}
        okText={store.createSchemeShow === 'edit' ? <FormattedMessage id="save" /> : <FormattedMessage id="create" />}
        cancelText={<FormattedMessage id="cancel" />}
        confirmLoading={submitting}
      >
        <div className="issue-region">
          <p className="issue-issueTypeScheme-list-tip">
            <FormattedMessage id="issueTypeScheme.createDes" />
          </p>
          <Form layout="vertical" className="c7n-sidebar-form c7nagile-form">
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
                initialValue: name,
              })(
                <Input
                  maxLength={15}
                  label={<FormattedMessage id="issueTypeScheme.label.name" />}
                />,
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              className="issue-sidebar-form"
            >
              {getFieldDecorator('description', {
                initialValue: description,
              })(
                <TextArea
                  maxLength={45}
                  label={<FormattedMessage id="issueTypeScheme.label.des" />}
                />,
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              className="issue-sidebar-form"
            >
              {getFieldDecorator('defaultIssueTypeId', {
                initialValue: defaultIssueTypeId,
              })(
                <Select
                  label={<FormattedMessage id="issueTypeScheme.label.default" />}
                  dropdownMatchSelectWidth
                  size="default"
                >
                  {target && target.length > 0 && target.map(issueType => (
                    <Option
                      value={issueType.id}
                      key={issueType.id}
                    >
                      <div
                        className="issue-type-wapper"
                      >
                        <TypeTag
                          data={issueType}
                          showName
                        />
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
          <TransferDrag
            origin={origin}
            target={target}
            onDragChange={this.onDragChange}
            validator={dragValidator}
          />
        </div>
      </Sidebar>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(IssueTypeSchemeCreate)));
