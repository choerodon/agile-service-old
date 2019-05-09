import React, { Component } from 'react';
import { stores, axios, Content } from 'choerodon-front-boot';
import _ from 'lodash';
import { Modal, Form, Select } from 'choerodon-ui';
import { createLink, loadIssuesInLink } from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';

import './TransformSubIssue.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { Option } = Select;
const STATUS_COLOR = {
  todo: 'rgb(255, 177, 0)',
  doing: 'rgb(77, 144, 254)',
  done: 'rgb(0, 191, 165)',
};
let sign = false;

class TransformSubIssue extends Component {
  debounceFilterIssues = _.debounce((input) => {
    const { issueId } = this.props;
    this.setState({
      selectLoading: true,
    });
    loadIssuesInLink(0, 20, issueId, input).then((res) => {
      this.setState({
        originIssues: res.content,
        selectLoading: false,
      });
    });
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      selectLoading: true,
      selectDefaultValue: undefined,
      originIssues: [],
      originStatus: [],
    };
  }

  componentDidMount() {
    this.getStatus();
  }

  onFilterChange(input) {
    const { issueId } = this.props;
    if (!sign) {
      this.setState({
        selectLoading: true,
      });
      loadIssuesInLink(0, 20, issueId, input).then((res) => {
        this.setState({
          originIssues: res.content,
          selectLoading: false,
        });
      });
      sign = true;
    } else {
      this.debounceFilterIssues(input);
    }
  }

  getStatus() {
    this.setState({
      selectLoading: true,
    });
    const { store } = this.props;
    const proId = AppState.currentMenuType.id;
    const issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
    const subTask = issueTypeData.find(t => t.typeCode === 'sub_task');
    if (subTask) {
      axios.get(`/issue/v1/projects/${proId}/schemes/query_status_by_issue_type_id?issue_type_id=${subTask.id}&apply_type=agile`)
        .then((res) => {
          this.setState({
            selectLoading: false,
            originStatus: res,
          });
        });

      axios.get(`/issue/v1/projects/${proId}/status/query_first_status?organizationId=${AppState.currentMenuType.organizationId}&applyType=agile&issueTypeId=${subTask.id}`)
        .then((res) => {
          this.setState({
            selectDefaultValue: res,
          });
        });
    } else {
      this.setState({
        selectLoading: false,
        originStatus: [],
      });
    }
  }

  handleTransformSubIssue = () => {
    const {
      form, onOk, store, issueId, ovn,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const projectId = AppState.currentMenuType.id;
        const orgId = AppState.currentMenuType.organizationId;
        const issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
        const subTask = issueTypeData.find(t => t.typeCode === 'sub_task');
        const issueTransformSubTask = {
          issueId,
          parentIssueId: values.issuesId,
          statusId: values.statusId,
          objectVersionNumber: ovn,
          issueTypeId: subTask && subTask.id,
          typeCode: subTask && subTask.typeCode,
        };
        this.setState({
          loading: true,
        });
        axios.post(`/agile/v1/projects/${projectId}/issues/transformed_sub_task?organizationId=${orgId}`, issueTransformSubTask)
          .then((res) => {
            this.setState({
              loading: false,
            });
            onOk();
          });
      }
    });
  };

  render() {
    const {
      form,
      visible,
      onCancel,
      issueNum,
    } = this.props;
    const {
      loading,
      selectLoading,
      originIssues,
      originStatus,
      selectDefaultValue,
    } = this.state;
    const { getFieldDecorator } = form;

    return (
      <Sidebar
        className="c7n-transformSubIssue"
        title="转化为子问题"
        visible={visible || false}
        onOk={this.handleTransformSubIssue}
        onCancel={onCancel}
        okText="转化"
        cancelText="取消"
        confirmLoading={loading}
      >
        <Content
          style={{
            padding: 0,
            width: 520,
          }}
          title={`将问题“${issueNum}”转化为子任务`}
          description="请在下面输入父任务和状态，表示为该子任务选择一个父任务和状态，实现其他类型问题与子任务之间的互转。"
        >
          <Form layout="vertical">
            <FormItem label="父任务" style={{ width: 520 }}>
              {getFieldDecorator('issuesId', {
                rules: [{ required: true, message: '请选择父任务' }],
              })(
                <Select
                  label="父任务"
                  loading={selectLoading}
                  filter
                  filterOption={false}
                  onFilterChange={this.onFilterChange.bind(this)}
                >
                  {originIssues.map(issue => (
                    <Option
                      key={issue.issueId}
                      value={issue.issueId}
                    >
                      <div style={{ display: 'inline-flex', width: 'calc(100% - 30px)', flex: 1 }}>
                        <div>
                          <TypeTag
                            data={issue.issueTypeDTO}
                          />
                        </div>
                        <a style={{
                          paddingLeft: 12,
                          paddingRight: 12,
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                        }}
                        >
                          {issue.issueNum}
                        </a>
                        <div style={{ overflow: 'hidden', flex: 1 }}>
                          <p style={{
                            paddingRight: '25px',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            whiteSpace: 'nowrap',
                            marginBottom: 0,
                            maxWidth: 'unset',
                          }}
                          >
                            {issue.summary}
                          </p>
                        </div>
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            <FormItem label="状态" style={{ width: 520 }}>
              {getFieldDecorator('statusId', {
                rules: [{ required: true, message: '请选择状态' }],
                initialValue: selectDefaultValue,
              })(
                <Select
                  label="状态"
                  loading={selectLoading}
                >
                  {
                    originStatus.map(status => (
                      <Option key={status.id} value={status.id}>
                        <div style={{ display: 'inline-flex', alignItems: 'center' }}>
                          <div
                            style={{
                              width: 15,
                              height: 15,
                              background: STATUS_COLOR[status.type],
                              marginRight: 6,
                              borderRadius: '2px',
                            }}
                          />
                          { status.name }
                        </div>
                      </Option>
                    ))
                  }
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(TransformSubIssue);
