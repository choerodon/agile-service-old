import React, { Component } from 'react';
import { stores, axios, Content } from 'choerodon-front-boot';
import _ from 'lodash';
import { Modal, Form, Select } from 'choerodon-ui';
import { createLink, loadIssuesInLink } from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';

import './ChangeParent.scss';

const { AppState } = stores;
const FormItem = Form.Item;
const { Option } = Select;
let sign = false;

class ChangeParent extends Component {
  debounceFilterIssues = _.debounce((input) => {
    const { issueId } = this.props;
    this.setState({ selectLoading: true });
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
      loading: false,
      selectLoading: true,
      originIssues: [],
    };
  }
  
  handleClickBtn = () => {
    const {
      form, issueId, objectVersionNumber, onOk,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const projectId = AppState.currentMenuType.id;
        const parentIssueId = values.issues;
        const issueUpdateParentIdDTO = {
          issueId,
          parentIssueId,
          objectVersionNumber,
        };
        this.setState({ loading: true });
        axios.post(`/agile/v1/projects/${projectId}/issues/update_parent`, issueUpdateParentIdDTO)
          .then((res) => {
            this.setState({ loading: false });
            onOk();
          });
      }
    });
  };

  handleFilterChange(input) {
    const { issueId } = this.props;
    if (!sign) {
      this.setState({ selectLoading: true });
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

  render() {
    const {
      visible, onCancel, onOk, issueId, issueNum, objectVersionNumber, form: { getFieldDecorator },
    } = this.props;
    const { loading, selectLoading, originIssues } = this.state;

    return (
      <Modal
        title={`修改${issueNum}的父级问题`}
        visible={visible || false}
        onOk={this.handleClickBtn}
        onCancel={onCancel}
        okText="修改"
        cancelText="取消"
        confirmLoading={loading}
      >
        <Form layout="vertical" style={{ marginTop: 20 }}>
          <FormItem>
            {getFieldDecorator('issues', {
              rules: [{ required: true, message: '请选择父任务' }],
            })(
              <Select
                label="父任务"
                loading={selectLoading}
                filter
                filterOption={false}
                onFilterChange={this.handleFilterChange.bind(this)}
              >
                {originIssues.map(issue => (
                  <Option
                    key={issue.issueId}
                    value={issue.issueId}
                  >
                    <div className="c7n-agile-changeParent-listWrap">
                      <div>
                        <TypeTag
                          data={issue.issueTypeDTO}
                        />
                      </div>
                      <a className="issueNum text-overflow-hidden">
                        {issue.issueNum}
                      </a>
                      <div className="issueSummary-wrap">
                        <p className="issueSummary text-overflow-hidden">
                          {issue.summary}
                        </p>
                      </div>
                    </div>
                  </Option>))}
              </Select>,
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
export default Form.create({})(ChangeParent);
