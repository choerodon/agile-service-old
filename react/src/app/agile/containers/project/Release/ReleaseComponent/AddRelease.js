import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Modal, Form, Input, DatePicker,
} from 'choerodon-ui';
import moment from 'moment';
import { Content, stores } from 'choerodon-front-boot';
import ReleaseStore from '../../../../stores/project/release/ReleaseStore';
import BacklogStore from "../../../../stores/project/backlog/BacklogStore";

const { Sidebar } = Modal;
const { TextArea } = Input;
const FormItem = Form.Item;
const { AppState } = stores;

@observer
class AddRelease extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expectReleaseDate: null,
      startDate: moment(),
      loading: false,
    };
  }

  handleOk = (e) => {
    this.setState({
      loading: true,
    });
    e.preventDefault();
    const { form, onCancel, refresh } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const data = {
          description: values.description,
          name: values.name,
          projectId: AppState.currentMenuType.id,
          startDate: values.startDate ? `${moment(values.startDate).format('YYYY-MM-DD')} 00:00:00` : null,
          expectReleaseDate: values.expectReleaseDate ? `${moment(values.expectReleaseDate).format('YYYY-MM-DD')} 00:00:00` : null,
        };
        ReleaseStore.axiosAddRelease(data).then((res) => {
          form.resetFields();
          onCancel();
          refresh();
          this.setState({
            loading: false,
            expectReleaseDate: null,
            startDate: moment(),
          });
        }).catch((error) => {
          this.setState({
            loading: false,
            expectReleaseDate: null,
            startDate: moment(),
          });
        });
      } else {
        this.setState({
          loading: false,
          expectReleaseDate: null,
          startDate: moment(),
        });
      }
    });
  };

  handleCancel = () => {
    const { form, onCancel } = this.props;
    form.resetFields();
    onCancel();
    this.setState({
      expectReleaseDate: null,
      startDate: moment(),
    });
  };

  checkName = (rule, value, callback) => {
    const proId = AppState.currentMenuType.id;
    if (value) {
      ReleaseStore.axiosCheckName(proId, value).then((res) => {
        if (res) {
          callback('版本名称重复');
        } else {
          callback();
        }
      }).catch((error) => {
      });
    } else {
      callback();
    }
  };

  render() {
    const { loading, expectReleaseDate, startDate } = this.state;
    const { form, visible } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Sidebar
        title="创建发布版本"
        visible={visible}
        onCancel={this.handleCancel}
        onOk={this.handleOk.bind(this)}
        okText="创建"
        cancelText="取消"
        confirmLoading={loading}
      >
        <Content
          style={{ padding: 0 }}
          title={`在项目“${AppState.currentMenuType.name}”中创建发布版本`}
          description="请在下面输入版本的名称、描述、开始和预计发布日期，创建新的软件版本。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/release/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '版本名称必填',
                }, {
                  validator: this.checkName,
                }],
              })(
                <Input label="版本名称" maxLength={15} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('startDate', {
                initialValue: moment(),
              })(
                <DatePicker
                  style={{ width: '100%' }}
                  label="开始日期"
                  disabledDate={expectReleaseDate
                    ? current => current > moment(expectReleaseDate) : () => false}
                  onChange={(date) => {
                    this.setState({
                      startDate: date,
                    });
                  }}
                />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('expectReleaseDate', {})(
                <DatePicker
                  style={{ width: '100%' }}
                  label="预计发布日期"
                  onChange={(date) => {
                    this.setState({
                      expectReleaseDate: date,
                    });
                  }}
                  disabledDate={startDate ? current => current < moment(startDate) : () => false}
                />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('description', {})(
                <TextArea label="版本描述" autosize maxLength={30} />,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(AddRelease);
