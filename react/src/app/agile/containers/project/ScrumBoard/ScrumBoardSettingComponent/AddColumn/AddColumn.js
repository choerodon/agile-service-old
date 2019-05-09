import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { axios, Content, stores } from 'choerodon-front-boot';
import {
  Form, Modal, Input, Select,
} from 'choerodon-ui';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import { STATUS } from '../../../../../common/Constant';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { Option } = Select;
const { confirm } = Modal;
const { AppState } = stores;

@observer
class AddColumn extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      statusType: false,
    };
    this.checkStatusDebounce = false;
  }

  checkStatusName(rule, value, callback) {
    const { store, form } = this.props;
    if (this.checkStatusDebounce) {
      clearTimeout(this.checkStatusDebounce);
      this.checkStatusDebounce = null;
    }
    this.checkStatusDebounce = setTimeout(() => {
      axios.get(`state/v1/projects/${AppState.currentMenuType.id}/status/project_check_name?organization_id=${AppState.currentMenuType.organizationId}&name=${value}`).then((res) => {
        if (res.statusExist) {
          this.setState({
            statusType: res.type,
          }, () => {
            form.setFieldsValue({
              column_categoryCode: res.type,
            });
          });
        } else {
          this.setState({
            statusType: false,
          }, () => {
            form.setFieldsValue({
              column_categoryCode: '',
            });
          });
        }
        callback();
      });
    }, 300);
  }

  renderOptions() {
    const result = [];
    if (JSON.stringify(ScrumBoardStore.getStatusCategory) !== '{}') {
      const data = ScrumBoardStore.getStatusCategory.lookupValues;
      data.sort();
      for (let index = 0, len = data.length; index < len; index += 1) {
        if (data[index].valueCode !== 'prepare') {
          result.push(
            <Option value={data[index].valueCode}>
              <div style={{ display: 'inline-flex', justifyContent: 'flex-start', alignItems: 'center' }}>
                <div style={{
                  width: 15,
                  height: 15,
                  borderRadius: 2,
                  marginRight: 5,
                  background: STATUS[data[index].valueCode] || 'rgb(255, 177, 0)',
                }}
                />
                <span>
                  {` ${data[index].name}`}
                </span>
              </div>
            </Option>,
          );
        }
      }
    }
    return result;
  }

  render() {
    const {
      form,
      visible,
      onChangeVisible,
    } = this.props;
    const {
      loading,
      statusType,
    } = this.state;
    const { getFieldDecorator } = form;
    const { name: kanbanName } = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
    return (
      <Sidebar
        title="添加列"
        visible={visible}
        onCancel={onChangeVisible.bind(this, false)}
        confirmLoading={loading}
        onOk={this.handleAddColumn}
        okText="创建"
        cancelText="取消"
      >
        <Content
          style={{ padding: 0 }}
          title={`添加看板“${kanbanName}”的列`}
          description="请在下面输入列名，选择列的类别。可以添加、删除、重新排序和重命名一个列，同时可以通过设置最大最小值来控制每列中的问题数量。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('column_name', {
                rules: [{
                  required: true, message: '列名称是必填的',
                }, {
                  validator: this.checkStatusName.bind(this),
                }],
              })(
                <Input label="列名称" placeholder="请输入列名称" />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('column_categoryCode', {
                rules: [{
                  required: true, message: '类别是必填的',
                }],
              })(
                <Select
                  label="类别"
                  placeholder="请选择类别"
                  disabled={!!statusType}
                >
                  {this.renderOptions()}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(AddColumn);
