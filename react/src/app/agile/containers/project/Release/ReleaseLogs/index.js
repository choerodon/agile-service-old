import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import _ from 'lodash';
import {
  Page, Header, Content, stores, axios,
} from 'choerodon-front-boot';
import {
  Button, DatePicker, Tabs, Table, Popover, Modal, Radio, Form, Select, Icon, Spin,
} from 'choerodon-ui';
import moment from 'moment';
import TypeTag from '../../../../components/TypeTag';

const FileSaver = require('file-saver');

const { TabPane } = Tabs;
const { Sidebar } = Modal;
const RadioGroup = Radio.Group;
const FormItem = Form.Item;
const { Option } = Select;
const { AppState } = stores;
let str;

@observer
class ReleaseLogs extends Component {
  constructor(props) {
    super(props);
    this.state = {
      version: {},
      issues: [],
      issue_epic: [],
      story: [],
      task: [],
      bug: [],
      sub_task: [],
      issueTypeData: [],
    };
  }

  componentDidMount() {
    this.loadIssueTypes();
    this.loadVersion();
  }

  loadIssues = () => {
    const { match } = this.props;
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    const versionId = match.params.id;
    axios.post(`/agile/v1/projects/${projectId}/product_version/${versionId}/issues?organizationId=${orgId}`, {})
      .then((res) => {
        this.setState({ issues: res });
        this.splitIssues(res);
      });
  };

  loadVersion = () => {
    const { match } = this.props;
    const projectId = AppState.currentMenuType.id;
    const versionId = match.params.id;
    axios.get(`/agile/v1/projects/${projectId}/product_version/${versionId}`)
      .then((res) => {
        this.setState({ version: res });
      });
  };

  loadIssueTypes = () => {
    const projectId = AppState.currentMenuType.id;
    axios.get(`/issue/v1/projects/${projectId}/schemes/query_issue_types?apply_type=agile`)
      .then((res) => {
        if (res && !res.failed) {
          this.setState({ issueTypeData: res });
          this.loadIssues();
        } else {
          this.setState({ issueTypeData: [] });
        }
      }).catch(() => {
        this.setState({ issueTypeData: [] });
      });
  };

  splitIssues = (issues) => {
    const { issueTypeData } = this.state;
    issueTypeData.forEach((e) => {
      const subset = _.filter(issues, issue => issue.typeCode === e.typeCode);
      this.setState({ [e.typeCode]: subset });
    });
  };

  exportLogs = () => {
    const { issueTypeData, version } = this.state;
    str = '';

    str += '# 发布日志\n\n';
    str += `## [${version.name}]`;
    if (version.statusCode === 'released') {
      str += ` - ${version.releaseDate}\n`;
    } else {
      str += '\n';
    }
    issueTypeData.forEach((v, i) => this.combine(v.typeCode, v.name));
    const blob = new Blob([str], { type: 'text/plain;charset=utf-8' });
    FileSaver.saveAs(blob, `版本${version.name}的发布日志.md`);
  };

  combine(typeCode, name) {
    const { state } = this;
    if (state[typeCode].length) {
      str += `\n### ${name}\n`;
      state[typeCode].forEach((v) => {
        str += `- [${v.issueNum}]-${v.summary}\n`;
      });
    }
  }

  renderSubsetIssues(issueType) {
    const { history, match } = this.props;
    const { state } = this;
    const menu = AppState.currentMenuType;
    const urlParams = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    return (
      <div>
        <div style={{ margin: '17px 0' }}>
          <TypeTag
            data={issueType}
            showName
          />
        </div>
        <ul style={{ marginBottom: 0, paddingLeft: 45 }}>
          {
            state[issueType.typeCode].map(issue => (
              <li style={{ marginBottom: 16 }}>
                <a
                  role="none"
                  onClick={() => {
                    history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issue.issueNum}&paramIssueId=${issue.issueId}&paramUrl=release/logs/${match.params.id}`);
                    return false;
                  }}
                >
                  [
                  {issue.issueNum}
                  ]
                </a>
                {` - ${issue.summary}`}
              </li>
            ))
          }
        </ul>
      </div>
    );
  }

  render() {
    const { match } = this.props;
    const urlParams = AppState.currentMenuType;
    const versionId = match.params.id;
    const { issueTypeData, version } = this.state;
    return (
      <Page>
        <Header
          title="版本日志"
          backPath={`/agile/release/detail/${versionId}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >
          <Button
            funcType="flat"
            onClick={() => {
              this.exportLogs();
            }}
          >
            <Icon type="library_books" />
            <span>导出</span>
          </Button>

        </Header>
        <Content
          title={`版本“${version.name}” 的版本日志`}
          description="您可以在此查看版本的版本日志，按照问题类型来分类显示问题列表，并且可以点击到具体问题进行修改。"
        >
          {
            issueTypeData.filter(type => type.typeCode !== 'feature').map(e => (
              <div>
                {
                  this.renderSubsetIssues(e)
                }
              </div>
            ))
          }
        </Content>
      </Page>
    );
  }
}

export default ReleaseLogs;
