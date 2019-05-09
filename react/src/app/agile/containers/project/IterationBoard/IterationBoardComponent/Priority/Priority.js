import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import { Spin } from 'choerodon-ui';
import PriorityTag from '../../../../../components/PriorityTag';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
import pic from '../EmptyPics/no_sprint.svg';
import pic2 from '../EmptyPics/no_version.svg';
import './Priority.scss';

const { AppState } = stores;

class Priority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprintId: undefined,
      loading: true,
      priorityInfo: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    const { sprintId } = this.props;
    if (nextProps.sprintId !== sprintId) {
      const newSprintId = nextProps.sprintId;
      this.setState({
        sprintId: newSprintId,
      });
      this.loadPriorityInfo(newSprintId);
    }
  }

  loadPriorityInfo(sprintId) {
    if (!sprintId) {
      this.setState({
        loading: false,
        priorityInfo: [],
      });
    } else {
      this.setState({ loading: true });
      const projectId = AppState.currentMenuType.id;
      const orgId = AppState.currentMenuType.organizationId;
      axios.get(`/agile/v1/projects/${projectId}/iterative_worktable/priority?organizationId=${orgId}&sprintId=${sprintId}`)
        .then((res) => {
          this.setState({
            priorityInfo: res,
            loading: false,
          });
        });
    }
  }

  renderContent() {
    const { loading, priorityInfo, sprintId } = this.state;
    if (loading) {
      return (
        <div className="loading-wrap">
          <Spin />
        </div>
      );
    }
    if (!sprintId) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic}
            des="当前项目下无活跃或结束冲刺"
          />
        </div>
      );
    }
    if (priorityInfo.length === 0) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic2}
            des="当前冲刺下无问题"
          />
        </div>
      );
    }
    return (
      <div className="lists">
        <h3 className="title">已完成/总计数</h3>
        <div className="wrapper">
          {priorityInfo.map(priority => this.renderList(priority))}
        </div>
      </div>
    );
  }

  renderList(priority) {
    return (
      <div className="list" key={priority.priorityDTO.id}>
        <div className="tip">
          {`${priority.completedNum}/${priority.totalNum}`}
        </div>
        <div className="body">
          <div>
            <PriorityTag
              priority={priority.priorityDTO}
            />
          </div>
          <div className="progress">
            <div
              className="progress-bg"
              style={{ background: `${priority.priorityDTO.colour}1F` }}
            />
            <div
              className="progress-inner"
              style={{
                background: priority.priorityDTO.colour,
                width: `${priority.completedNum / priority.totalNum * 100}%`,
              }}
            />
          </div>
        </div>
      </div>
    );
  }

  render() {
    return (
      <div className="c7n-agile-sprintDashboard-priority">
        {this.renderContent()}
      </div>
    );
  }
}

export default Priority;
