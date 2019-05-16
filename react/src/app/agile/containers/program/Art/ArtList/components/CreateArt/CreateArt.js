import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {
  Form, Input, Modal, DatePicker,
} from 'choerodon-ui';
import moment from 'moment';
import SelectFocusLoad from '../../../../../../components/SelectFocusLoad';
import { checkArtName } from '../../../../../../api/ArtApi';
import './CreateArt.scss';

const FormItem = Form.Item;
const { Sidebar } = Modal;

const propTypes = {
  visible: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  onCancel: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
class CreateArt extends Component {
  // eslint-disable-next-line no-unused-vars
  componentDidUpdate(prevProps, prevState) {
    const { form, visible } = this.props;
    if (!prevProps.visible && visible) {
      form.resetFields();
    }
  }

  checkArtNameRepeat=(rule, value, callback) => {
    checkArtName(value).then((res) => {
      if (res) {
        callback('Art名称重复');
      } else {
        callback();
      }
    });
  };

  handleOk = () => {
    const { onSubmit, form } = this.props;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSubmit(values);
      }
    });
  };

  render() {
    const {
      visible, onCancel, loading, form,
    } = this.props;
    const { getFieldDecorator } = form;
   
    return (
      <Sidebar
        title="创建ART"
        visible={visible}
        onOKText="保存"
        onCancelText="取消"
        confirmLoading={loading}
        onOk={this.handleOk}
        onCancel={onCancel}
      >
        <Form>
          <FormItem>
            {getFieldDecorator('name', {
              rules: [{
                required: true, message: '请输入ART名称',
              }, {
                validator: this.checkArtNameRepeat,
              }],
            })(
              <Input style={{ width: 500 }} maxLength={15} label="名称" placeholder="请输入ART名称" />,
            )}
          </FormItem>
          <FormItem>
            {getFieldDecorator('rteId')(
              <SelectFocusLoad allowClear type="user" label="发布火车工程师" style={{ width: 500 }} />,
            )}
          </FormItem>
          <FormItem>
            {getFieldDecorator('startDate', {
              rules: [{
                required: true,
                message: '请选择火车开始时间!',
              }],
            })(
              <DatePicker
                format="YYYY-MM-DD"
                style={{ width: 500 }}
                label="火车开始时间"
                disabledDate={current => current < moment().subtract(1, 'days')}
              />,
            )}
          </FormItem>
        </Form>
      </Sidebar>
    );
  }
}

CreateArt.propTypes = propTypes;

export default Form.create()(CreateArt);
