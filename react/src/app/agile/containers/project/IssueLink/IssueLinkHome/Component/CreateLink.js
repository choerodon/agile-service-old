import React, { Component } from 'react';
import { Modal, Form, Input } from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';

const { Sidebar } = Modal;
const { TextArea } = Input;
const { AppState } = stores;
const FormItem = Form.Item;

class CreateLink extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  checkLinkName(rule, value, callback) {
    const projectId = AppState.currentMenuType.id;
    axios.get(`agile/v1/projects/${projectId}/issue_link_types/check_name?issueLinkTypeName=${value}&issueLinkTypeId=`)
      .then((res) => {
        if (!res) {
          callback('问题链接名称重复');
        } else {
          callback();
        }
      });
  }

  handleOk(e) {
    const { form, onOk } = this.props;
    e.preventDefault();
    form.validateFieldsAndScroll((err, values) => {
      const { name, inWard, outWard } = values;
      if (!err) {
        const obj = {
          inWard,
          outWard,
          linkName: name,
        };
        this.setState({ loading: true });
        axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types`, obj)
          .then(() => {
            this.setState({ loading: false });
            onOk();
          });
      }
    });
  }

  render() {
    const { onCancel, form: { getFieldDecorator } } = this.props;
    const { loading } = this.state;
    return (
      <Sidebar
        className="c7n-component-component"
        title="创建问题链接"
        okText="创建"
        cancelText="取消"
        visible
        confirmLoading={loading}
        onOk={this.handleOk.bind(this)}
        onCancel={onCancel}
      >
        <Content
          style={{ padding: 0 }}
          title={`在项目“${AppState.currentMenuType.name}”中创建问题链接`}
          description="通过自定义问题链接，可以帮助您更好的对多个问题进行关联，不再局限于父子任务。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/issue-link/"
        >
          <Form layout="vertical" style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '名称为必输项',
                }, {
                  validator: this.checkLinkName.bind(this),
                }],
              })(
                <Input label="名称" maxLength={30} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('outWard', {
                rules: [{
                  required: true,
                  message: '链出描述为必输项',
                }],
              })(
                <TextArea label="链出描述" autosize maxLength={30} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('inWard', {
                rules: [{
                  required: true,
                  message: '链入描述为必输项',
                }],
              })(
                <TextArea label="链入描述" autosize maxLength={30} />,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(CreateLink);
