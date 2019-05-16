import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import {
  Modal, Form, Input,
} from 'choerodon-ui';
import { checkArtName } from '../../../../../../api/ArtApi';

const { Sidebar } = Modal;
const FormItem = Form.Item;

class EditArtNameSidebar extends Component {
    handleEditArtNameOk = () => {
      const { form, onOk } = this.props;
      form.validateFields((err, values, modify) => {
        if (!err && modify) {
          onOk(values.ArtName);
        }
      });
    }

    handleEditArtNameCancel = () => {
      const { form, onCancel, name } = this.props;
      form.setFieldsValue({
        ArtName: name,
      });
      onCancel();
    }

    checkArtNameRepeat=(rule, value, callback) => {
      const { name } = this.props;
      if (value === name) {
        callback();
      } else {
        checkArtName(value).then((res) => {
          if (res) {
            callback('Art名称重复');
          } else {
            callback();
          }
        });
      }
    }

    render() {
      const { visible, name, form: { getFieldDecorator } } = this.props;
      return (
        <Sidebar
          className="c7n-wikiDoc"
          title="修改名称"
          visible={visible || false}
          onOk={this.handleEditArtNameOk}
          onCancel={this.handleEditArtNameCancel}
          okText="保存"
          cancelText="取消"
        >
          <Form>
            <FormItem style={{ width: 500 }}>
              {
                getFieldDecorator('ArtName', {
                  rules: [{ required: true, message: '请输入ART名称' }, {
                    validator: this.checkArtNameRepeat,
                  }],
                  initialValue: name,
                })(
                  <Input maxLength={15} label="ART名称" placeholder="请输入ART名称" />,
                )
              }
            </FormItem>
          </Form>
        </Sidebar>
      );
    }
}
export default withRouter(Form.create()(EditArtNameSidebar));
