import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Content, stores, axios } from 'choerodon-front-boot';
import {
  Form, Modal, Input, Select, message,
} from 'choerodon-ui';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import { STATUS } from '../../../../../common/Constant';

const FormItem = Form.Item;
const { Sidebar, confirm } = Modal;
const { Option } = Select;
const { AppState } = stores;

@observer
class SideBarContent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      statusType: false,
    };
    this.checkStatusDebounce = false;
  }

  handleAddColumn = (e) => {
    const {
      form, store, onChangeVisible, refresh,
    } = this.props;
    e.preventDefault();
    form.validateFields((err, values) => {
      if (!err) {
        const statusDate = store.getStatusList;
        const status = statusDate.find(s => s.name === values.name);
        const { categoryCode } = values;
        const data = {
          boardId: ScrumBoardStore.getSelectedBoard,
          name: values.name,
          projectId: AppState.currentMenuType.id,
          categoryCode: values.categoryCode,
          sequence: ScrumBoardStore.getBoardData.length - 1,
        };
        if (status) {
          confirm({
            title: '警告',
            content: `已存在状态“${values.name}”，如果创建该列，不会创建同名状态`,
            onOk() {
              ScrumBoardStore.axiosAddColumn(categoryCode, data).then((res2) => {
                onChangeVisible(false);
                refresh();
              }).catch((error) => {
              });
            },
            onCancel() {
            },
          });
        } else {
          ScrumBoardStore.axiosAddColumn(categoryCode, data).then((res2) => {
            onChangeVisible(false);
            refresh();
            this.setState({
              loading: false,
            });
          }).catch((error) => {
            this.setState({
              loading: false,
            });
          });
        }
      }
    });
  }

  handleAddStatus = (e) => {
    e.preventDefault();
    const { form, onChangeVisible, refresh } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        this.setState({
          loading: true,
        });
        const params = {
          name: values.name,
          projectId: AppState.currentMenuType.id,
          enable: true,
          categoryCode: values.categoryCode,
        };
        ScrumBoardStore.axiosAddStatus(params).then((data) => {
          if (data && data.failed && data.code === 'error.status.exist') {
            Choerodon.prompt(`状态 ${values.name} 已经存在。`, 'error');
          }
          onChangeVisible(false);
          refresh();
          this.setState({
            loading: false,
          });
        }).catch((error) => {
          this.setState({
            loading: false,
          });
        });
      }
    });
  }

  checkStatusName(rule, value, callback) {
    if (!value) {
      callback();
      return;
    }
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
              categoryCode: res.type,
            });
          });
        } else {
          this.setState({
            statusType: false,
          }, () => {
            // form.setFieldsValue({
            //   categoryCode: '',
            // });
            // form.validateFields(['categoryCode']);
          });
        }
        callback();
      });
    }, 300);
  }

  renderOptions() {
    if (JSON.stringify(ScrumBoardStore.getStatusCategory) !== '{}') {
      return ScrumBoardStore.getStatusCategory.lookupValues.sort().filter(item => item.valueCode !== 'prepare').map(item => (
        <Option value={item.valueCode}>
          <div style={{ display: 'inline-flex', justifyContent: 'flex-start', alignItems: 'center' }}>
            <div style={{
              width: 15,
              height: 15,
              borderRadius: 2,
              marginRight: 5,
              background: STATUS[item.valueCode] || 'rgb(255, 177, 0)',
            }}
            />
            <span>
              {item.name}
            </span>
          </div>
        </Option>
      ));
    }
    return [];
  }

  render() {
    const {
      form,
      visible,
      onChangeVisible,
      fromStatus,
      type,
    } = this.props;
    const {
      loading,
      statusType,
    } = this.state;
    const { getFieldDecorator } = form;
    const { name: kanbanName } = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
    const modifiedMap = new Map([
      ['Status', {
        name: '状态',
        contentTitle: fromStatus ? `在项目“${AppState.currentMenuType.name}”中创建状态` : `添加看板“${kanbanName}”的状态`,
        contentDescription: '配置完成后，您可以通过board对问题拖拽进行状态的流转。',
        onOk: this.handleAddStatus,
      }],
      ['Column', {
        name: '列',
        contentTitle: `添加看板“${kanbanName}”的列`,
        contentDescription: '同时可以通过设置最大最小值来控制每列中的问题数量',
        onOk: this.handleAddColumn,
      }],
    ]);
    const { name: modifiedName, contentDescription: description } = modifiedMap.get(type);
    return (
      <Sidebar
        title={`添加${modifiedName}`}
        visible={visible}
        onCancel={onChangeVisible.bind(this, false)}
        onOk={modifiedMap.get(type).onOk}
        confirmLoading={loading}
        okText="创建"
        cancelText="取消"
      >
        <Content
          style={{ padding: 0 }}
          title={modifiedMap.get(type).contentTitle}
          description={`请在下面输入${modifiedName}名称，选择${modifiedName}的类别。可以添加、删除、重新排序和重命名一个${modifiedName}，${description}`}
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/manage-kanban/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                rules: [{
                  required: true, message: `${modifiedName}名称是必填的`,
                },
                {
                  validator: type === 'Status' && this.checkStatusName.bind(this),
                },
                ],
              })(
                <Input label={`${modifiedName}名称`} placeholder={`请输入${modifiedName}名称`} maxLength={10} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('categoryCode', {
                rules: [{
                  required: true, message: '类别是必填的',
                }],
              })(
                <Select
                  label="类别"
                  placeholder="请选择类别"
                  disabled={type === 'Status' ? !!statusType : false}
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

export default Form.create()(SideBarContent);
