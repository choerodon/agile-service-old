import React, { Component } from 'react';
import {
  stores, axios, Page, Header, Content, Permission,
} from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import _ from 'lodash';
import {
  Form, Input, Button, Icon, Select, Radio,
} from 'choerodon-ui';
import { COLOR } from '../../../../common/Constant';
import { loadPriorities } from '../../../../api/NewIssueApi';
import { getUsers, getUser } from '../../../../api/CommonApi';
import UserHead from '../../../../components/UserHead';

const { AppState } = stores;
const { Option } = Select;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
let sign = false;

class ProjectSetting extends Component {
  constructor(props) {
    super(props);
    this.state = {
      origin: {},
      loading: false,
      couldUpdate: false,
      originUsers: [],

      code: undefined,
      strategy: undefined,
      assignee: undefined,
    };
  }

  componentDidMount() {
    this.getProjectSetting();
  }

  onChangeStrategy = (e) => {
    const strategy = e.target.value;
    if (strategy !== 'assignee') {
      this.props.form.setFieldsValue({
        assignee: undefined,
      });
    }
  }

  getProjectSetting() {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/project_info`)
      .then((res) => {
        this.setState({
          origin: res,
          code: res.projectCode,
          strategy: res.defaultAssigneeType,
        });
        this.props.form.setFieldsValue({
          code: res.projectCode,
          strategy: res.defaultAssigneeType,
        });
        if (res.defaultAssigneeId) {
          this.loadUser(res.defaultAssigneeId);
        } else {
          this.setState({
            assignee: undefined,
          });
          this.props.form.setFieldsValue({
            assignee: undefined,
          });
        }
      });
  }

  loadUser(assigneeId) {
    getUser(assigneeId).then((res) => {
      this.setState({
        assignee: assigneeId,
        originUsers: res.content.length ? [res.content[0]] : [],
      });
      this.props.form.setFieldsValue({
        assignee: assigneeId,
      });
    });
  }

  onFilterChange(input) {
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      getUsers(input).then((res) => {
        this.setState({
          originUsers: res.content.filter(u => u.enabled),
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  debounceFilterIssues = _.debounce((input) => {
    this.setState({
      selectLoading: true,
    });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.content.filter(u => u.enabled),
        selectLoading: false,
      });
    });
  }, 500);

  handleCheckSameName = (rule, value, callback) => {
    if (!value) {
      this.setState({
        couldUpdate: false,
      });
      callback('项目code不能为空');
    } else if (value === this.state.origin.projectCode) {
      this.setState({
        couldUpdate: false,
      });
      callback();
    } else {
      // axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/project_info/check?projectName=${value}`)
      //   .then((res) => {
      //     if (res) {
      //       this.setState({
      //         couldUpdate: false,
      //       });
      //       callback('存在同名code，请选择其他项目code');
      //     } else {
      //       this.setState({
      //         couldUpdate: true,
      //       });
      //       callback();
      //     }
      //   });
      this.setState({
        couldUpdate: true,
      });
      callback();
    }
  }

  handleUpdateProjectSetting = () => {
    this.props.form.validateFields((err, values, modify) => {
      if (!err && modify) {
        const projectInfoDTO = {
          ...this.state.origin,
          projectCode: values.code,
          defaultAssigneeType: values.strategy,
          defaultAssigneeId: values.assignee || 0,
        };
        this.setState({
          loading: true,
        });
        axios.put(`/agile/v1/projects/${AppState.currentMenuType.id}/project_info`, projectInfoDTO)
          .then((res) => {
            if (res.failed) {
              Choerodon.prompt(res.message);
            } else {
              this.setState({
                origin: res,
                loading: false,
                couldUpdate: false,
                code: res.projectCode,
                strategy: res.defaultAssigneeType,
                assignee: res.defaultAssigneeId,
              });
              Choerodon.prompt('修改成功');
            }
          })
          .catch((error) => {
            this.setState({
              loading: false,
            });
            this.getProjectCode();
          });
      }
    });
  };

  render() {
    const { getFieldDecorator, isModifiedFields, getFieldValue } = this.props.form;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const radioStyle = {
      display: 'block',
      height: '30px',
      lineHeight: '30px',
    };
    return (
      <Page
        service={[
          'agile-service.project-info.updateProjectInfo',
        ]}
      >
        <Header title="项目设置">
          <Button funcType="flat" onClick={() => this.getProjectSetting()}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title="项目设置"
          description="可修改项目编码、默认经办人策略、经办人，修改项目设置后，项目中所有的问题编号前缀、经办人策略、经办人将随之改变。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/setup/project-setting/"
        >
          <div style={{ marginTop: 8 }}>
            <Form layout="vertical">
              <FormItem label="项目编码" style={{ width: 512 }}>
                {getFieldDecorator('code', {
                  rules: [{ required: true, message: '项目编码必填' }],
                  initialValue: this.state.code,
                })(
                  <Input
                    label="项目编码"
                    maxLength={5}
                  />,
                )}
              </FormItem>
              <FormItem label="默认经办人策略" style={{ width: 512, marginBottom: 0 }}>
                {getFieldDecorator('strategy', {
                  rules: [{ required: true, message: '默认经办人策略为必选项' }],
                  // initialValue: this.state.strategy || 'undistributed',
                  initialValue: this.state.strategy || undefined,
                })(
                  <RadioGroup label="默认经办人策略" onChange={this.onChangeStrategy}>
                    <Radio style={radioStyle} value="undistributed">无</Radio>
                    <Radio style={radioStyle} value="current_user">默认创建人</Radio>
                    <Radio style={radioStyle} value="default_assignee">指定经办人</Radio>
                  </RadioGroup>,
                )}
              </FormItem>
              <FormItem label="默认经办人" style={{ width: 512 }}>
                {getFieldDecorator('assignee', {
                  rules: [{ required: this.props.form.getFieldValue('strategy') === 'default_assignee', message: '默认经办人必选' }],
                  initialValue: this.state.assignee || undefined,
                })(
                  <Select
                    label="默认经办人"
                    getPopupContainer={triggerNode => triggerNode.parentNode}
                    loading={this.state.selectLoading}
                    disabled={this.props.form.getFieldValue('strategy') !== 'default_assignee'}
                    filter
                    filterOption={false}
                    allowClear
                    onFilterChange={this.onFilterChange.bind(this)}
                  >
                    {this.state.originUsers.map(user => (
                      <Option key={user.id} value={user.id}>
                        <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
                          <UserHead
                            user={{
                              id: user.id,
                              loginName: user.loginName,
                              realName: user.realName,
                              avatar: user.imageUrl,
                            }}
                          />
                        </div>
                      </Option>
                    ))}
                  </Select>,
                )}
              </FormItem>
            </Form>
            <div style={{ padding: '12px 0', borderTop: '1px solid rgba(0, 0, 0, 0.12)' }}>
              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.project-info.updateProjectInfo']}>
                <Button
                  type="primary"
                  funcType="raised"
                  loading={this.state.loading}
                  onClick={() => this.handleUpdateProjectSetting()}
                >
                  {'保存'}
                </Button>
              </Permission>
              <Button
                funcType="raised"
                style={{ marginLeft: 12 }}
                onClick={() => this.getProjectSetting()}
              >
                {'重置'}
              </Button>
            </div>
          </div>
        </Content>
      </Page>
    );
  }
}
export default Form.create({})(withRouter(ProjectSetting));
