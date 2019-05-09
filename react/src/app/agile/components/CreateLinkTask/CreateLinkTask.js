import React, { Component } from 'react';
import { stores, axios, Content } from 'choerodon-front-boot';
import _ from 'lodash';
import { Select, Form, Modal } from 'choerodon-ui';
import { createLink, loadIssuesInLink } from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';
import './CreateLinkTask.scss';

const { AppState } = stores;
const { Sidebar } = Modal;
const { Option } = Select;
const FormItem = Form.Item;
let sign = false;

class CreateLinkTask extends Component {
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
      createLoading: false,
      selectLoading: true,
      originIssues: [],
      originLinks: [],
      show: [],
      selected: [],
    };
  }

  componentDidMount() {
    this.getLinks();
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

  getLinks() {
    this.setState({
      selectLoading: true,
    });
    axios.post(`/agile/v1/projects/${AppState.currentMenuType.id}/issue_link_types/query_all`, {
      contents: [],
      linkName: '',
    })
      .then((res) => {
        this.setState({
          selectLoading: false,
          originLinks: res.content,
        });
        this.transform(res.content);
      });
  }

  transform = (links) => {
    // split active and passive
    const active = links.map(link => ({
      name: link.outWard,
      linkTypeId: link.linkTypeId,
    }));
    const passive = [];
    links.forEach((link) => {
      if (link.inWard !== link.outWard) {
        passive.push({
          name: link.inWard,
          linkTypeId: link.linkTypeId,
        });
      }
    });
    this.setState({
      show: active.concat(passive),
    });
  };

  handleSelect = (value, option) => {
    const selected = _.map(option.slice(), v => v.key);
    this.setState({ selected });
  };

  handleCreateIssue = () => {
    const { form, issueId, onOk } = this.props;
    const { selected, originLinks } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const { linkTypeId, issues } = values;
        const labelIssueRelDTOList = _.map(selected, (issue) => {
          const currentLinkType = _.find(originLinks, { linkTypeId: linkTypeId.split('+')[0] * 1 });
          if (currentLinkType.outWard === linkTypeId.split('+')[1]) {
            return ({
              linkTypeId: linkTypeId.split('+')[0] * 1,
              linkedIssueId: issue * 1,
              issueId,
            });
          } else {
            return ({
              linkTypeId: linkTypeId.split('+')[0] * 1,
              issueId: issue * 1,
              linkedIssueId: issueId,
            });
          }
        });
        this.setState({ createLoading: true });
        createLink(issueId, labelIssueRelDTOList)
          .then((res) => {
            this.setState({ createLoading: false });
            onOk();
          });
      }
    });
  };

  render() {
    const {
      form, visible, onCancel,
    } = this.props;
    const { getFieldDecorator } = form;
    const {
      createLoading, selectLoading, show, originIssues,
    } = this.state;

    return (
      <Sidebar
        className="c7n-newLink"
        title="创建链接"
        visible={visible || false}
        onOk={this.handleCreateIssue}
        onCancel={onCancel}
        okText="创建"
        cancelText="取消"
        confirmLoading={createLoading}
      >
        <Content
          style={{ padding: 0 }}
          title="对问题创建链接"
          description="请在下面输入相关任务的基本信息，包括所要创建的关系（复制、阻塞、关联、破坏、被复制、被阻塞、被破坏等）以及所要关联的问题（支持多选）。"
        >
          <Form layout="vertical">
            <FormItem label="关系" style={{ width: 520 }}>
              {getFieldDecorator('linkTypeId', {
                rules: [
                  { required: true, message: '请选择所要创建的关系' },
                ],
              })(
                <Select
                  label="关系"
                  loading={selectLoading}
                >
                  {show.map(link => (
                    <Option key={`${link.linkTypeId}+${link.name}`} value={`${link.linkTypeId}+${link.name}`}>
                      {link.name}
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>

            <FormItem label="问题" style={{ width: 520 }}>
              {getFieldDecorator('issues', {
                rules: [
                  { required: true, message: '请选择所要关联的问题' },
                ],
              })(
                <Select
                  label="问题"
                  mode="multiple"
                  dropdownClassName="issueSelectDropDown"
                  loading={selectLoading}
                  optionLabelProp="value"
                  filter
                  filterOption={false}
                  onFilterChange={this.onFilterChange.bind(this)}
                  onChange={this.handleSelect.bind(this)}
                >
                  {originIssues.map(issue => (
                    <Option
                      key={issue.issueId}
                      value={issue.issueNum}
                    >
                      <div style={{ display: 'inline-block', width: '100%' }}>
                        <div style={{
                          display: 'flex',
                          width: '100%',
                          flex: 1,
                        }}
                        >
                          <div>
                            <TypeTag
                              data={issue.issueTypeDTO}
                            />
                          </div>
                          <div style={{
                            paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', 
                          }}
                          >
                            {issue.issueNum}
                          </div>
                          <div style={{ overflow: 'hidden', flex: 1 }}>
                            <p style={{
                              paddingRight: '25px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset', 
                            }}
                            >
                              {issue.summary}
                            </p>
                          </div>
                        </div>
                      </div>
                    </Option>
                  ))}
                </Select>,
              )}
            </FormItem>
          </Form>
        </Content>
      </Sidebar>
    );
  }
}
export default Form.create({})(CreateLinkTask);
