import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Modal, Form, Select, Input, message, IconSelect,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import {
  Content, Header, Page, Permission, stores,
} from '@choerodon/boot';
import _ from 'lodash';
import { CompactPicker } from 'react-color';
import './IssueTypeCreate.scss';

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
class IssueTypeCreate extends Component {
  constructor(props) {
    const menu = AppState.currentMenuType;
    super(props);

    this.state = {
      submitting: false,
      displayColorPicker: false,
      issueTypeColor: '#3F51B5',
    };
  }

  componentDidMount() {
    this.loadIssueType();
    const { id, store } = this.props;
    this.setState({
      issueTypeColor: _.find(store.issueTypes, item => item.id === id) ? _.find(store.issueTypes, item => item.id === id).colour : '#3F51B5',
    });
  }

  loadIssueType = () => {
    const { store, id } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    if (id) {
      store.loadIssueTypeById(orgId, id);
    }
  };

  handleClose = () => {
    const { store } = this.props;
    store.setIssueType({});
    this.props.form.resetFields();
    this.props.onClose();
  };

  handleSubmit = () => {
    const { store, id, intl } = this.props;
    const { issueTypeColor } = this.state;
    const orgId = AppState.currentMenuType.organizationId;
    const objectVersionNumber = store.issueType ? store.issueType.objectVersionNumber : 0;
    this.props.form.validateFieldsAndScroll((err, data) => {
      if (!err) {
        const postData = data;
        postData.colour = issueTypeColor;
        postData.organizationId = orgId;
        postData.objectVersionNumber = objectVersionNumber;
        this.setState({
          submitting: true,
        });
        if (id) {
          store.updateIssueType(orgId, id, postData)
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
          store.createIssueType(orgId, postData)
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
      }
    });
  };

  checkName = (rule, value, callback) => {
    const { store, intl, id } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const name = store.issueType ? store.issueType.name : false;
    if ((name && value === name) || !value) {
      callback();
    } else {
      store.checkName(orgId, value, id)
        .then((data) => {
          if (data) {
            callback();
          } else {
            callback(intl.formatMessage({ id: 'issueType.name.check.exist' }));
          }
        }).catch((error) => {
          callback();
        });
    }
  };

  handleClickSwatch = () => {
    this.setState({
      displayColorPicker: !this.state.displayColorPicker,
    });
  };

  handleCloseColorPicker = () => {
    this.setState({
      displayColorPicker: false,
    });
  }

  handleChangeColorComplete = (color) => {
    this.setState({
      issueTypeColor: color.hex,
    });
  }

  render() {
    const {
      visible, intl, store, id, form,
    } = this.props;
    const { submitting, displayColorPicker, issueTypeColor } = this.state;
    const { getFieldDecorator } = form;
    const name = store.issueType ? store.issueType.name : '';
    const description = store.issueType ? store.issueType.description : '';
    const icon = store.issueType ? store.issueType.icon : '';

    return (
      <Sidebar
        title={id ? <FormattedMessage id="edit" /> : <FormattedMessage id="issueType.create" />}
        visible={visible}
        onOk={this.handleSubmit}
        onCancel={this.handleClose}
        okText={id ? <FormattedMessage id="save" /> : <FormattedMessage id="create" />}
        cancelText={<FormattedMessage id="cancel" />}
        confirmLoading={submitting}
      >
        <div className="issue-region">
          <p className="issue-issueType-list-tip">
            <FormattedMessage id="issueType.createDes" />
          </p>
          <Form layout="vertical" onSubmit={this.handleOk} className="c7n-sidebar-form c7nagile-form">
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
                  label={<FormattedMessage id="issueType.label.name" />}
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
                  label={<FormattedMessage id="issueType.label.des" />}
                />,
              )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              className="issue-sidebar-form"
            >
              {getFieldDecorator('icon', {
                initialValue: icon || 'help',
              })(
                <IconSelect
                  label={<FormattedMessage id="issueType.label.icon" />}
                />,
              )}
            </FormItem>
            <div className="issue-issueTypeColor-picker">
              <div className="issue-issueTypeColor-swatch" onClick={this.handleClickSwatch} role="none">
                <div className="issue-issueTypeColor-color" style={{ background: issueTypeColor }} />
              </div>
              {
                displayColorPicker
                  ? (
                    <div className="popover">
                      <div className="cover" onClick={this.handleCloseColorPicker} role="none" />
                      <CompactPicker
                        color={issueTypeColor}
                        onChange={this.handleChangeColorComplete}
                      />
                    </div>
                  )
                  : null
              }
            </div>
          </Form>
        </div>
      </Sidebar>
    );
  }
}

export default Form.create({})(withRouter(injectIntl(IssueTypeCreate)));
