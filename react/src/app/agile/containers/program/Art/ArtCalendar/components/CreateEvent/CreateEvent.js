import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import {
  Form, Input, Select, Modal, DatePicker, Radio, Checkbox,
} from 'choerodon-ui';
import { Content } from 'choerodon-front-boot';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { Option } = Select;
const { RangePicker } = DatePicker;
const RadioGroup = Radio.Group;
const radioStyle = {
  display: 'block',
  height: '30px',
  lineHeight: '30px',
};
const defaultProps = {

};

const propTypes = {
  visible: PropTypes.bool.isRequired,
  loading: PropTypes.bool.isRequired,
  onCancel: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};
class CreateEvent extends Component {
  handleOk = () => {
    const { onSubmit, form } = this.props;
    form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        // console.log(values);
        // onSubmit(values);
      }
    });
  }

  render() {
    const {
      visible, onCancel, loading, form,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    return (
      <div>
        <Sidebar
          title="创建事件"
          visible={visible}
          onOk={this.handleOk}
          onCancel={onCancel}
          confirmLoading={loading}
        >
          <Content
            style={{
              padding: '0 0 10px 0',
            }}
          >
            <Form>
              <FormItem>
                {getFieldDecorator('statusName', {
                  rules: [{
                    required: true, message: '请输入事件主题',
                  }],
                })(
                  <Input label="主题" style={{ width: 500 }} maxLength={30} placeholder="请输入事件主题" />,
                )}
              </FormItem>
              <FormItem>
                <span className="ant-input-wrapper ant-input-has-value ant-input-has-label">
                  <div className="ant-input-label"><span>持续时间</span></div>
                  {getFieldDecorator('range', {
                    rules: [{
                      required: true, message: '请选择日期!',
                    }],
                  })(
                    <RangePicker
                      format="YYYY-MM-DD"
                      style={{ width: 500 }}
                    />,
                  )}
                </span>
              </FormItem>
              <FormItem>
                {getFieldDecorator('isSendMessage', {
                  initialValue: false,
                })(
                  <RadioGroup>
                    <Radio value={false} style={radioStyle}>不发送通知</Radio>
                    <Radio value style={radioStyle}>发送消息通知/邮件通知</Radio>
                  </RadioGroup>,
                )}
              </FormItem>
              {
                getFieldValue('isSendMessage') && (
                  <Fragment>
                    <FormItem>
                      {getFieldDecorator('messageType', {
                        initialValue: 'message',
                        rules: [{
                          required: true, message: '请选择类型!',
                        }],
                      })(
                        <Select label="消息类型" style={{ width: 500 }}>
                          <Option value="message">消息通知</Option>
                          <Option value="email">邮件</Option>
                        </Select>,
                      )}
                    </FormItem>
                    <FormItem>
                      {getFieldDecorator('receiver', {
                        initialValue: 'owner',
                        rules: [{
                          required: true, message: '请选择接收对象!',
                        }],
                      })(
                        <Select label="接收对象" style={{ width: 500 }}>
                          <Option value="owner">项目所有者</Option>
                          <Option value="member">项目成员</Option>
                        </Select>,
                      )}
                    </FormItem>
                    <FormItem>
                      {getFieldDecorator('sendTime', {
                        rules: [{
                          required: true, message: '请选择发送时间!',
                        }],
                      })(
                        <DatePicker
                          label="消息发送时间"
                          format="YYYY-MM-DD"
                          style={{ width: 500 }}
                        />,
                      )}
                    </FormItem>
                    <FormItem>
                      {getFieldDecorator('isSendNow', {
                        initialValue: false,
                      })(
                        <Checkbox>
                          立即发送
                        </Checkbox>,
                      )}
                    </FormItem>
                    <FormItem style={{ width: 500 }}>
                      {getFieldDecorator('message', {
                      })(
                        <Input.TextArea
                          label="消息内容"

                          placeholder="请输入消息通知的内容"
                        />,
                      )}
                    </FormItem>
                  </Fragment>
                )
              }
            </Form>
          </Content>
        </Sidebar>
      </div>
    );
  }
}

CreateEvent.propTypes = propTypes;

export default Form.create()(CreateEvent);
