import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import { Modal, Form, Input } from 'choerodon-ui';

import './CreateVOS.scss';

const { AppState } = stores;
const FormItem = Form.Item;

class CreateVOS extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      nextSprintName: '',
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.visible) {
      const { type } = nextProps;
      if (type === 'sprint') {
        const projectId = AppState.currentMenuType.id;
        // 请求下一个冲刺名，置入state
        axios.get(`/agile/v1/projects/${projectId}/sprint/current_create_name`)
          .then((res) => {
            this.setState({ nextSprintName: res });
          });
      } else {
        this.setState({ nextSprintName: '' });
      }
    }
  }

  handleCreate = () => {
    const { form, onOk } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const projectId = AppState.currentMenuType.id;
        const { type } = this.props;
        const { name } = values;
        const copyConditionDTO = {
        };
        this.setState({
          loading: true,
        });
        if (type === 'sprint') {
          // 创建冲刺
          axios.post(`/agile/v1/projects/${projectId}/sprint/create?sprintName=${name}`)
            .then((res) => {
              this.setState({ loading: false });
              onOk();
            })
            .catch((error) => {
              this.setState({ loading: false });
            });
        } else {
          // 创建版本
          const versionCreateDTO = {
            name,
            projectId,
            releaseDate: null,
            startDate: null,
          };
          axios.post(`/agile/v1/projects/${projectId}/product_version`, versionCreateDTO)
            .then((res) => {
              if (!res.failed) {
                onOk();
              } else {
                Choerodon.promt(res.message);
              }
              this.setState({ loading: false });
            })
            .catch((error) => {
              this.setState({ loading: false });
            });
        }
      }
    });
  };

  /**
   *验证版本名称是否重复
   *
   * @memberof CreateVersion
   */
  checkVersionNameRepeat = (rule, value, callback) => {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/check?name=${value}`)
      .then((res) => {
        if (res) {
          callback('版本名称重复');
        } else {
          callback();
        }
      });
  };

  /**
   *验证冲刺名称是否重复
   */
  checkSprintNameRepeat = (rule, value, callback) => {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/sprint/check_name?sprintName=${value}`)
      .then((res) => {
        if (res) {
          callback('冲刺名称重复');
        } else {
          callback();
        }
      });
  };

  render() {
    const {
      visible, onCancel, type, container, form,
    } = this.props;
    const { getFieldDecorator } = form;
    const { loading, nextSprintName } = this.state;

    return (
      <Modal
        className="c7n-createVOS"
        // getContainer={() => container}
        title={`创建${type === 'sprint' ? '冲刺' : '版本'}`}
        visible={visible || false}
        onOk={this.handleCreate}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        destroyOnClose
        confirmLoading={loading}
      >
        <Form layout="vertical">
          <FormItem>
            {getFieldDecorator('name', {
              rules: [{ required: true, message: '请输入名称' },
                type === 'version' ? {
                  validator: this.checkVersionNameRepeat,
                } : {
                  validator: this.checkSprintNameRepeat,
                }],
              initialValue: nextSprintName,
            })(
              <Input
                label={`${type === 'sprint' ? '冲刺' : '版本'}名称`}
                // defaultValue={this.state.nextSprintName}
                onPressEnter={this.handleCreate}
                maxLength={type === 'sprint' ? 30 : 15}
              />,
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
export default Form.create({})(CreateVOS);
