import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Modal, Form, Input, Select,
} from 'choerodon-ui';
import { Content, stores, axios } from '@choerodon/boot';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import { createIssueField } from '../../../../../api/NewIssueApi';

const { AppState } = stores;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
@Form.create({})
@observer
class CreateFeature extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  /**
   *
   * 创建特性
   * @param {*} e
   * @memberof CreateFeature
   */
  handleCreateFeature =(e) => {
    const {
      form, onCancel, refresh,
    } = this.props;
    const issueTypes = BacklogStore.getIssueTypes || [];
    const defaultPriorityId = BacklogStore.getDefaultPriority ? BacklogStore.getDefaultPriority.id : '';
    e.preventDefault();
    form.validateFieldsAndScroll((err, value) => {
      if (!err) {
        const FeatureType = issueTypes.find(t => t.typeCode === 'feature');
        const req = {
          projectId: AppState.currentMenuType.id,
          summary: value.summary,
          typeCode: 'feature',
          issueTypeId: FeatureType && FeatureType.id,
          priorityCode: `priority-${defaultPriorityId}`,
          priorityId: defaultPriorityId,
          featureDTO: {
            // benfitHypothesis: values.benfitHypothesis,
            // acceptanceCritera: values.acceptanceCritera,
            featureType: value.featureType,
          },
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
        title="创建特性"
        visible={visible}
        okText="创建"
        cancelText="取消"
        onCancel={() => {
          form.resetFields();
          onCancel();
        }}
        confirmLoading={loading}
        onOk={this.handleCreateFeature}
      >
        <Content
          style={{
            padding: 0,
          }}
          title={`创建项目“${AppState.currentMenuType.name}”的特性`}
          description="请在下面输入特性名称，创建新特性。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/backlog/epic/"
        >
          <Form style={{ width: 512 }}>
            <FormItem>
              {getFieldDecorator('summary', {
                rules: [{
                  required: true,
                  message: '特性名称不能为空',
                  // transform: value => value && value.trim(),
                }, 
                // {
                //   validator: this.checkEpicNameRepeat,
                // }
                ],
              })(
                <Input label="特性名称" maxLength={44} />,
              )}
            </FormItem>
            <FormItem style={{ width: 520 }}>
              {getFieldDecorator('featureType', {
                rules: [{ required: true, message: '特性类型为必输项' }],
                initialValue: 'business',
              })(
                <Select
                  label="特性类型"
                  getPopupContainer={triggerNode => triggerNode.parentNode}
                >
                  <Option key="business" value="business">
                    特性
                  </Option>
                  <Option key="enabler" value="enabler">
                    使能
                  </Option>
                </Select>,
              )}
            </FormItem>            
          </Form>
        </Content>
      </Sidebar>
    );
  }
}

export default CreateFeature;
