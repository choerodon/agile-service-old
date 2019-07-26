import {
  Content, Header, Page, stores,
} from '@choerodon/boot';
import {
  Form, Input, message, Modal, Checkbox,
} from 'choerodon-ui';
import { observer } from 'mobx-react';
import React, { Component } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { CompactPicker } from 'react-color';

import './priorityEdit.scss';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { confirm } = Modal;
const { TextArea } = Input;

const { AppState } = stores;

@Form.create({})
@injectIntl
@observer
class PriorityEdit extends Component {
  state = {
    priorityColor: null,
    displayColorPicker: false,
    loading: false,
  };

  componentDidMount() {
    const { PriorityStore } = this.props;
    const { editingPriority } = PriorityStore;
    this.setState({
      priorityColor: editingPriority.colour,
    });
  }

  handleEditingOk = () => {
    const { form, PriorityStore } = this.props;
    form.validateFieldsAndScroll(async (err, data) => {
      if (!err) {
        const { name, des, default: isDefault } = data;
        const { editingPriority: { id, objectVersionNumber } } = PriorityStore;
        const { priorityColor } = this.state;
        const orgId = AppState.currentMenuType.organizationId;

        try {
          await PriorityStore.editPriorityById(orgId, {
            id,
            name,
            description: des,
            default: !!isDefault,
            objectVersionNumber,
            colour: priorityColor,
            organizationId: orgId,
          });
          message.success('修改成功');
          PriorityStore.loadPriorityList(orgId);
          this.hideSidebar();
        } catch (e) {
          message.error('修改失败');
          this.setState({
            loading: false,
          });
        }
      } else {
        this.setState({
          loading: false,
        });
      }
    });
  };

  handleEditingCancel = () => {
    this.hideSidebar();
  };

  handleChangeComplete = async (color) => {
    this.setState({
      priorityColor: color.hex,
    });
  };

  handleClickSwatch = () => {
    const { displayColorPicker } = this.state;
    this.setState({
      displayColorPicker: !displayColorPicker,
    });
  };

  handleCloseColorPicker = () => {
    this.setState({
      displayColorPicker: false,
    });
  };

  hideSidebar() {
    const { PriorityStore, form } = this.props;
    PriorityStore.setOnEditingPriority(false);
    form.resetFields();
    this.setState({
      loading: false,
    });
  }

  checkName = async (rule, value, callback) => {
    const { PriorityStore, intl } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const { editingPriority } = PriorityStore;
    if (editingPriority.name === value) {
      callback();
    } else {
      const res = await PriorityStore.checkName(orgId, value);
      if (res) {
        callback(intl.formatMessage({ id: 'priority.create.name.error' }));
      } else {
        callback();
      }
    }
  };

  render() {
    const { priorityColor, displayColorPicker, loading } = this.state;
    const { PriorityStore, form, intl } = this.props;
    const { onEditingPriority, editingPriority } = PriorityStore;
    const { getFieldDecorator } = form;

    return (
      <Sidebar
        title={<FormattedMessage id="priority.edit" />}
        visible={onEditingPriority}
        okText={<FormattedMessage id="save" />}
        cancelText={<FormattedMessage id="cancel" />}
        onOk={this.handleEditingOk}
        onCancel={this.handleEditingCancel}
        confirmLoading={loading}
      >
        <Form className="form c7nagile-form">
          <FormItem
            label="name"
          >
            {
              getFieldDecorator(
                'name',
                {
                  rules: [
                    {
                      required: true,
                      message: intl.formatMessage({ id: 'required' }),
                    },
                    {
                      validator: this.checkName,
                    },
                  ],
                  initialValue: editingPriority.name,
                },
              )(
                <Input
                  label={<FormattedMessage id="priority.name" />}
                  placeholder={intl.formatMessage({ id: 'priority.create.name.placeholder' })}
                  maxLength={10}
                />,
              )
            }
          </FormItem>
          <FormItem
            label="des"
          >
            {
              getFieldDecorator(
                'des',
                {
                  initialValue: editingPriority.description,
                },
              )(
                <TextArea
                  label={<FormattedMessage id="priority.des" />}
                  placeholder={intl.formatMessage({ id: 'priority.create.des.placeholder' })}
                  maxLength={45}
                />,
              )
            }
          </FormItem>
          <div className="issue-color-picker">
            <div className="swatch" onClick={this.handleClickSwatch} role="none">
              <div className="color" style={{ background: priorityColor }} />
            </div>
            {
              displayColorPicker
                ? (
                  <div className="popover">
                    <div className="cover" onClick={this.handleCloseColorPicker} role="none" />
                    <CompactPicker color={priorityColor} onChange={this.handleChangeComplete} />
                  </div>
                )
                : null
            }
          </div>
          <FormItem
            label="default"
          >
            {
              getFieldDecorator(
                'default',
                {
                  valuePropName: 'checked',
                  initialValue: !!editingPriority.default,
                },
              )(
                <Checkbox
                  disabled={!!editingPriority.default || !editingPriority.enable}
                >
设置为默认优先级

                </Checkbox>,
              )
            }
          </FormItem>
        </Form>
      </Sidebar>
    );
  }
}

export default PriorityEdit;
