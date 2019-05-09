import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { DashBoardNavBar, stores, axios } from 'choerodon-front-boot';
import { Spin, Tooltip } from 'choerodon-ui';
import TypeTag from '../../components/TypeTag';
import PriorityTag from '../../components/PriorityTag';
import EmptyBlockDashboard from '../../components/EmptyBlockDashboard';
// import pic from './empty.png';
import pic from '../../assets/image/emptyChart.svg';
import './index.scss';

const { AppState } = stores;

class MineUnDone extends Component {
  constructor(props) {
    super(props);
    this.state = {
      issues: [],
      loading: false,
    };
  }

  componentDidMount() {
    this.loadData();
  }

  loadData() {
    const projectId = AppState.currentMenuType.id;
    const userId = AppState.getUserId;
    this.setState({ loading: true });
    axios.get(`/agile/v1/projects/${projectId}/issues/unfinished/${userId}`)
      .then((res) => {
        this.setState({
          issues: res,
          loading: false,
        });
      });
  }

  renderIssue(issue) {
    return (
      <div className="list" key={issue.issueNum}>
        <div>
          <TypeTag
            data={issue.issueTypeDTO}
          />
        </div>
        <span className="issueNum text-overflow-hidden">
          {issue.issueNum}
        </span>
        <div className="issueSummary-wrap">
          <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={issue.summary}>
            <p className="issueSummary text-overflow-hidden">
              {issue.summary}
            </p>
          </Tooltip>
        </div>
        <div className="flex-shrink">
          <div className="priority">
            <PriorityTag
              priority={issue.priorityDTO}
            />
          </div>
        </div>
      </div>
    );
  }

  renderContent() {
    const { loading, issues } = this.state;
    if (loading) {
      return (
        <div className="loading-wrap">
          <Spin />
        </div>
      );
    }
    if (issues && !issues.length) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic}
            des="当前没有我的未完成的任务"
          />
        </div>
      );
    }
    return (
      <div className="lists">
        {
          issues.map(issue => this.renderIssue(issue))
        }
      </div>
    );
  }

  render() {
    const { issues } = this.state;
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    return (
      <div className="c7n-agile-dashboard-mineUndone">
        {this.renderContent()}
        <DashBoardNavBar>
          <a
            role="none"
            onClick={() => {
              history.push(`/agile/backlog?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
              return false;
            }}
          >
            {'转至待办事项'}
          </a>
        </DashBoardNavBar>
      </div>
    );
  }
}

export default withRouter(MineUnDone);
