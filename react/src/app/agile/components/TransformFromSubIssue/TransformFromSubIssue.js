import React, { Component } from 'react';
import { stores, axios, Content } from 'choerodon-front-boot';
import {
  Modal, Form, Select, Input,
} from 'choerodon-ui';
import { transformedTask } from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';

import './TransformFromSubIssue.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { Option } = Select;
const STATUS_COLOR = {
  todo: 'rgb(255, 177, 0)',
  doing: 'rgb(77, 144, 254)',
  done: 'rgb(0, 191, 165)',
};

class TransformFromSubIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectLoading: true,
      originTypes: [],
      originStatus: [],
      issueTypeId: false,
      isEpicType: false,
    };
  }

  componentDidMount() {
    this.axiosGetIssueTypes();
  }

  getStatus() {
    this.setState({
      selectLoading: true,
    });
    const { issueTypeId } = this.state;
    const proId = AppState.currentMenuType.id;
    if (issueTypeId) {
      axios.get(`/issue/v1/projects/${proId}/schemes/query_status_by_issue_type_id?issue_type_id=${issueTypeId}&apply_type=agile`)
        .then((res) => {
          this.setState({
            selectLoading: false,
            originStatus: res,
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
      issueId, ovn, form, onOk,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const { originTypes, isEpicType } = this.state;
        const { typeCode } = originTypes.find(t => t.id === values.typeId);
        const issueUpdateTypeDTO = {
          epicName: isEpicType ? values.epicName : undefined,
          issueId,
          objectVersionNumber: ovn,
          typeCode,
          issueTypeId: values.typeId,
          statusId: values.statusId,
        };
        this.setState({
          loading: true,
        });
        transformedTask(issueUpdateTypeDTO)
          .then((res) => {
            this.setState({
              loading: false,
            });
            onOk();
          });
      }
    });
  };

  onTypeChange = (typeId) => {
    const { form } = this.props;
    const { originTypes } = this.state;
    form.setFieldsValue({
      statusId: undefined,
    });
    const epicType = originTypes.find(t => t.typeCode === 'issue_epic');
    this.setState({
      issueTypeId: typeId,
      isEpicType: epicType && epicType.id === typeId,
    }, () => {
      this.getStatus();
    });
  };

  axiosGetIssueTypes() {
    const proId = AppState.currentMenuType.id;
    axios.get(`/issue/v1/projects/${proId}/schemes/query_issue_types_with_sm_id?apply_type=agile`)
      .then((data) => {
        this.setState({
          selectLoading: false,
          originTypes: data,
        });
      });
  }

  render() {
    const {
      form,
      visible,
      onCancel,
      issueNum,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      originStatus,
      originTypes,
      selectLoading,
      loading,
      isEpicType,
    } = this.state;

    return (
      <Sidebar
        className="c7n-transformFromSubIssue"
        title="转化为问题"
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
          title={`将问题“${issueNum}”转化为任务`}
          description="请在下面选择问题类型，表示将该子任务转化为该种问题，实现子任务与其他类型问题之间的互转。"
        >
          <Form layout="vertical">
            <FormItem label="问题类型" style={{ width: 520 }}>
              {getFieldDecorator('typeId', {
                rules: [{ required: true }],
              })(
                <Select
                  label="问题类型"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                  onChange={this.onTypeChange}
                >
                  {originTypes.filter(t => t.typeCode !== 'sub_task').map(type => (
                    <Option key={type.id} value={type.id}>
                      <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
                        <TypeTag
                          data={type}
                          showName
                        />
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
            <FormItem label="状态" style={{ width: 520 }}>
              {getFieldDecorator('statusId', {
                rules: [{ required: true, message: '请选择状态' }],
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
            {
              isEpicType && (
                <FormItem label="史诗名称" style={{ width: 520 }}>
                  {getFieldDecorator('epicName', {
                    rules: [{ required: true, message: '史诗名称为必输项' }],
                  })(
                    <Input label="史诗名称" maxLength={44} />,
                  )}
                </FormItem>
              )
            }
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(TransformFromSubIssue);
