import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Content, stores } from '@choerodon/boot';
import _ from 'lodash';
import {
  Form, Modal, Input, Select,
} from 'choerodon-ui';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

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
    const {
      form, data, onChangeVisible, refresh,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          id: data.id,
          objectVersionNumber: data.objectVersionNumber,
          name: values.name,
          projectId: AppState.currentMenuType.id,
          categoryCode: values.categoryCode,
        };
        ScrumBoardStore.axiosUpdateIssueStatus(data.id, params).then((res) => {
          onChangeVisible(false, this.statusInput);
          refresh();
        }).catch((error) => {
        });
      }
    });
  }

  checkStatusName(rule, value, callback) {
    const { changeName } = this.state;
    ScrumBoardStore.axiosCheckRepeatName(value).then((res) => {
      if (res) {
        if (changeName) {
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
    if (JSON.stringify(ScrumBoardStore.getStatusCategory) !== '{}') {
      const data = ScrumBoardStore.getStatusCategory.lookupValues;
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
    const {
      form, visible, onChangeVisible, data,
    } = this.props;
    const { changeName } = this.state;
    const { getFieldDecorator } = form;
    let name;
    for (let index = 0, len = ScrumBoardStore.getBoardList.length; index < len; index += 1) {
      if (ScrumBoardStore.getBoardList[index].boardId === ScrumBoardStore.getSelectedBoard) {
        // eslint-disable-next-line prefer-destructuring
        name = ScrumBoardStore.getBoardList[index].name;
      }
    }
    return (
      <Sidebar
        title="修改状态"
        visible={visible}
        onCancel={onChangeVisible.bind(this, false, this.statusInput)}
        onOk={this.handleEditStatus.bind(this)}
        okText="修改"
        cancelText="取消"
      >
        <Content
          style={{ padding: 0 }}
          title={`修改看板“${name}”的状态`}
          description="请在下面输入状态名称，选择状态的类别。可以添加、删除、重新排序和重命名一个状态，配置完成后，您可以通过board对问题拖拽进行状态的流转。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                initialValue: data.name ? data.name : undefined,
                rules: [{
                  required: true, message: '状态名称是必填的',
                }, {
                  validator: this.checkStatusName.bind(this),
                }],
              })(
                <Input
                  label="状态名称"
                  placeholder="状态名称"
                  ref={(ref) => { this.statusInput = ref; }}
                  onChange={() => {
                    if (!changeName) {
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
                initialValue: data.categoryCode
                  ? data.categoryCode : undefined,
                rules: [{
                  required: true, message: '类别是必填的',
                }],
              })(
                <Select
                  label="类别"
                  placeholder="类别"
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
