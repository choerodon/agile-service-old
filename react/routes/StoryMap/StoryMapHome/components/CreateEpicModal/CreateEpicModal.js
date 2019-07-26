import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Modal, Form, Input } from 'choerodon-ui';
import { Content, stores, axios } from '@choerodon/boot';
import { createIssueField } from '../../../../../../api/NewIssueApi';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;


class CreateEpicModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  componentDidUpdate(prevProps) {
    // eslint-disable-next-line react/destructuring-assignment
    if (this.props.visible && !prevProps.visible) {
      setTimeout(() => {
        this.input.focus();
      });   
    }
  }

  handleCreateEpic =(e) => {
    const {
      form, onOk, epicType, defaultPriority, 
    } = this.props;   
    const defaultPriorityId = defaultPriority ? defaultPriority.id : '';
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {        
        const data = {
          projectId: AppState.currentMenuType.id,
          epicName: value.name.trim(),
          summary: value.summary.trim(),
          typeCode: 'issue_epic',
          issueTypeId: epicType && epicType.id,
          priorityCode: `priority-${defaultPriorityId}`,
          priorityId: defaultPriorityId,
        };
        this.setState({
          loading: true,
        });
        axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issues?applyType=agile`, data)
          .then((res) => {
            const dto = {
              schemeCode: 'agile_issue',
              context: res.typeCode,
              pageCode: 'agile_issue_create',
            };
            createIssueField(res.issueId, dto);
            this.setState({
              loading: false,
            });
            onOk(res);
          })
          .catch((error) => {
            this.setState({
              loading: false,
            });
          });
      }
    });
  }

  checkEpicNameRepeat = (rule, value, callback) => {
    if (value && value.trim()) {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/check_epic_name?epicName=${value.trim()}`)
        .then((res) => {
          if (res) {
            callback('史诗名称重复');
          } else {
            callback();
          }
        });
    } else {
      callback();
    }
  };

  render() {
    const { loading } = this.state;
    const {
      form, visible, onCancel,
    } = this.props;
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
        destroyOnClose
        confirmLoading={loading}
        onOk={this.handleCreateEpic}
      >
        <Content
          style={{ padding: 0 }}
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
                  whitespace: true,
                }, {
                  validator: this.checkEpicNameRepeat,
                }],
              })(
                <Input ref={(input) => { this.input = input; }} label="史诗名称" maxLength={20} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('summary', {
                rules: [{
                  required: true,
                  message: '概要不能为空',
                  whitespace: true,
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
const CreateEpicModalContainer = observer(({ ...props }) => {
  const { createEpicModalVisible } = StoryMapStore;
  return (
    <CreateEpicModal
      visible={createEpicModalVisible}
      epicType={StoryMapStore.getEpicType}
      defaultPriority={StoryMapStore.getDefaultPriority}
      onCancel={() => {
        StoryMapStore.setCreateEpicModalVisible(false);
      }}
      {...props}
    />
  );
});
export default Form.create()(CreateEpicModalContainer);
