/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { DashBoardNavBar, stores, axios } from 'choerodon-front-boot';
import { Spin, Tooltip, Pagination } from 'choerodon-ui';
import TypeTag from '../../components/TypeTag';
import PriorityTag from '../../components/PriorityTag';
import EmptyBlockDashboard from '../../components/EmptyBlockDashboard';
// import pic from './empty.png';
import pic from '../../assets/image/emptyChart.svg';
import './index.scss';

const { AppState } = stores;

class Undistributed extends Component {
  constructor(props) {
    super(props);
    this.state = {
      issues: [],
      pagination: {
        total: 0,
        current: 1,
        pageSize: 5,
      },
      loading: false,
    };
  }

  componentDidMount() {
    this.loadData();
  }

  handlePaginationChange = (page, pageSize) => {
    this.loadData({
      current: page,
      pageSize,
    });
  }

  loadData(pagination = this.state.pagination) {
    const projectId = AppState.currentMenuType.id;
    this.setState({ loading: true });
    const { current, pageSize } = pagination;
    axios.get(`/agile/v1/projects/${projectId}/issues/undistributed?size=${pageSize}&page=${current - 1}`)
      .then((res) => {
        const {
          content, totalElements, number, size,
        } = res;
        this.setState({
          issues: content,
          pagination: {
            current: number + 1,
            total: totalElements,
            pageSize: size,
          },
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
        <span
          className="issueNum text-overflow-hidden"
          style={{
            color: '#3f51b5',
            cursor: 'pointer',
            display: 'block',
            minWidth: 85,
          }}
          role="none"
          onClick={() => {
            const { history } = this.props;
            const urlParams = AppState.currentMenuType;
            history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issue.issueNum}&paramIssueId=${issue.issueId}`);
          }}
        >
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
            des="当前没有未分配的任务"
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
    const { issues, pagination } = this.state;
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    const { current, total, pageSize } = pagination;
    return (
      <div className="c7n-agile-dashboard-undistributed">
        {this.renderContent()}
   
        <div style={{ textAlign: 'right', paddingRight: 15 }}>
          <Pagination
            hideOnSinglePage 
            showSizeChanger={false}
            total={total}
            current={current}
            pageSize={pageSize}
            onChange={this.handlePaginationChange}
          />
        </div>
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

export default withRouter(Undistributed);
