import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Modal, Form, Input,
} from 'choerodon-ui';
import { Content, stores, axios } from 'choerodon-front-boot';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import { createIssueField } from '../../../../../api/NewIssueApi';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;

@Form.create({})
@observer
class CreateEpic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  /**
   *
   * 创建史诗
   * @param {*} e
   * @memberof CreateEpic
   */
  handleCreateEpic =(e) => {
    const {
      form, onCancel, refresh,
    } = this.props;
    const issueTypes = BacklogStore.getIssueTypes || [];
    const defaultPriorityId = BacklogStore.getDefaultPriority ? BacklogStore.getDefaultPriority.id : '';
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        const epicType = issueTypes.find(t => t.typeCode === 'issue_epic');
        const req = {
          projectId: AppState.currentMenuType.id,
          epicName: value.name,
          summary: value.summary,
          typeCode: 'issue_epic',
          issueTypeId: epicType && epicType.id,
          priorityCode: `priority-${defaultPriorityId}`,
          priorityId: defaultPriorityId,
        };
        this.setState({
          loading: true,
        });
        BacklogStore.axiosEasyCreateIssue(req).then((res) => {
          const dto = {
            schemeCode: 'agile_issue',
            context: res.typeCode,
            pageCode: 'agile_issue_create',
          };
          createIssueField(res.issueId, dto);
          this.setState({
            loading: false,
          });
          form.resetFields();
          refresh();
          onCancel();
        }).catch((error) => {
          this.setState({
            loading: false,
          });
        });
      }
    });
  };

  checkEpicNameRepeat = (rule, value, callback) => {
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/check_epic_name?epicName=${value}`)
      .then((res) => {
        if (res) {
          callback('史诗名称重复');
        } else {
          callback();
        }
      });
  };

  render() {
    const {
      form, onCancel, visible, store,
    } = this.props;
    const issueTypes = BacklogStore.getIssueTypes || [];
    const epicType = issueTypes.find(t => t.typeCode === 'issue_epic');
    const { loading } = this.state;
    const { getFieldDecorator } = form;
    return (
      <Sidebar
        title="创建史诗"
        visible={visible}
        okText="创建"
        cancelText="取消"
        onCancel={() => {
          form.resetFields();
          onCancel();
        }}
        confirmLoading={loading}
        onOk={this.handleCreateEpic}
      >
        <Content
          style={{
            padding: 0,
          }}
          title={`创建项目“${AppState.currentMenuType.name}”的史诗`}
          description="请在下面输入史诗名称、概要，创建新史诗。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/backlog/epic/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('name', {
                rules: [{
                  required: true,
                  message: '史诗名称不能为空',
                  // transform: value => value && value.trim(),
                }, {
                  validator: this.checkEpicNameRepeat,
                }],
              })(
                <Input label="史诗名称" maxLength={10} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('summary', {
                rules: [{
                  required: true,
                  message: '概要不能为空',
                  // transform: value => value && value.trim(),
                }],
              })(
                <TextArea autosize label="概要" maxLength={44} />,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default CreateEpic;
