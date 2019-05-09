/* eslint-disable prefer-destructuring */
/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Content, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import {
  Form, Modal, Input, Select, 
} from 'choerodon-ui';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';

const FormItem = Form.Item;
const { Sidebar } = Modal;
const { Option } = Select;
const { AppState } = stores;

@observer
class EditStatus extends Component {
  constructor(props) {
    super(props);
    this.state = {
      changeName: false,
    };
  }

  handleEditStatus(e) {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        const params = {
          id: this.props.data.id,
          objectVersionNumber: this.props.data.objectVersionNumber,
          name: values.name,
          projectId: AppState.currentMenuType.id,
          categoryCode: values.categoryCode,
        };
        KanbanStore.axiosUpdateIssueStatus(this.props.data.id, params).then((data) => {
          this.props.onChangeVisible(false);
          this.props.refresh();
        }).catch((error) => {
        });
      }
    });
  }

  checkStatusName(rule, value, callback) {
    KanbanStore.axiosCheckRepeatName(value).then((res) => {
      if (res) {
        if (this.state.changeName) {
          callback('状态名称重复');
        } else {
          callback();
        }
      } else {
        callback();
      }
    }).catch((error) => {
    });
  }

  renderOptions() {
    const result = [];
    if (JSON.stringify(KanbanStore.getStatusCategory) !== '{}') {
      const data = KanbanStore.getStatusCategory.lookupValues;
      data.sort((x, y) => {
        if (x.valueCode === 'todo') {
          return -1;
        } else if (x.valueCode === 'done') {
          return 1;
        } else if (y.valueCode === 'todo') {
          return 1;
        } else {
          return -1;
        }
      });
      for (let index = 0, len = data.length; index < len; index += 1) {
        let color = '';
        if (data[index].valueCode === 'doing') {
          color = 'rgb(77, 144, 254)';
        } else if (data[index].valueCode === 'done') {
          color = 'rgb(0, 191, 165)';
        } else {
          color = 'rgb(255, 177, 0)';
        }
        result.push(
          <Option value={data[index].valueCode}>
            <div style={{ display: 'inline-flex', justifyContent: 'flex-start', alignItems: 'center' }}>
              <div style={{
                width: 15, height: 15, borderRadius: 2, marginRight: 5, background: color,
              }}
              />
              <span>
                {' '}
                {data[index].name}
              </span>
            </div>

          </Option>,
        );
      }
    }
    return result;
  }

  render() {
    const { getFieldDecorator } = this.props.form;
    let name;
    for (let index = 0, len = KanbanStore.getBoardList.length; index < len; index += 1) {
      if (KanbanStore.getBoardList[index].boardId === KanbanStore.getSelectedBoard) {
        name = KanbanStore.getBoardList[index].name;
      }
    }
    return (
      <Sidebar
        title="修改状态"
        visible={this.props.visible}
        onCancel={this.props.onChangeVisible.bind(this, false)}
        onOk={this.handleEditStatus.bind(this)}
        okText="修改"
        cancelText="取消"
      >
        <Content
          style={{ padding: 0 }}
          title={`修改看板“${name}”的状态`}
          description="请在下面输入状态名称，选择状态的类别。可以添加、删除、重新排序和重命名一个状态，配置完成后，您可以通过board对问题拖拽进行状态的流转。"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                initialValue: this.props.data.name ? this.props.data.name : undefined,
                rules: [{
                  required: true, message: '状态名称是必填的',
                }, {
                  validator: this.checkStatusName.bind(this),
                }],
              })(
                <Input
                  label="状态名称"
                  placeholder="请输入状态名称"
                  onChange={() => {
                    if (!this.state.changeName) {
                      this.setState({
                        changeName: true,
                      });
                    }
                  }}
                />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('categoryCode', {
                initialValue: this.props.data.categoryCode
                  ? this.props.data.categoryCode : undefined,
                rules: [{
                  required: true, message: '类别是必填的',
                }],
              })(
                <Select
                  label="类别"
                  placeholder="请选择类别"
                >
                  {this.renderOptions()}
                  {/* <Option value="todo">todo</Option> */}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(EditStatus);
