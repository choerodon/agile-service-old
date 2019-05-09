import React, { Component } from 'react';
import { Spin } from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import EmptyBlockDashboard from '../../../../../../components/EmptyBlockDashboard';
import pic from '../../EmptyPics/no_sprint.svg';
import pic2 from '../../EmptyPics/no_version.svg';
import './Assignee.scss';

const { AppState } = stores;

class Assignee extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprintId: undefined,
      loading: true,
      assigneeInfo: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    const { sprintId } = this.props;
    if (nextProps.sprintId !== sprintId) {
      const newSprintId = nextProps.sprintId;
      this.setState({
        sprintId: newSprintId,
      });
      this.loadAssignee(newSprintId);
    }
  }

  getOption() {
    const { assigneeInfo } = this.state;
    const data = assigneeInfo.map(v => ({
      name: v.assigneeName,
      value: v.issueNum,
    }));
    const allCount = _.reduce(assigneeInfo, (sum, n) => sum + n.issueNum, 0);
    const option = {
      tooltip: {
        trigger: 'item',
        backgroundColor: '#fff',
        textStyle: {
          color: '#000',
        },
        formatter(params) {
          const res = `${params.name}：${params.value} 个<br/>占比：
            ${((params.value / allCount).toFixed(2) * 100).toFixed(0)}%`;
          return res;
        },
        extraCssText: 
          'box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2); border: 1px solid #ddd; border-radius: 0;',
      },
      series: [
        {
          color: ['#9665e2', '#f7667f', '#fad352', '#45a3fc', '#56ca77'],
          type: 'pie',
          radius: '60px',
          hoverAnimation: false,
          center: ['50%', '50%'],
          data,
          itemStyle: {
            normal: {
              borderWidth: 2,
              borderColor: '#fff',
            },
          },
        },
      ],
    };
    return option;
  }

  loadAssignee(sprintId) {
    const projectId = AppState.currentMenuType.id;
    this.setState({ loading: true });
    axios.get(`/agile/v1/projects/${projectId}/iterative_worktable/assignee_id?sprintId=${sprintId}`)
      .then((res) => {
        const assigneeInfo = this.transformAssigneeInfo(res);
        this.setState({
          assigneeInfo,
          loading: false,
        });
      });
  }

  transformAssigneeInfo(assigneeInfo) {
    const res = [];
    let other = {
      assigneeName: '其它',
      issueNum: 0,
      percent: 0,
    };
    assigneeInfo.forEach((v) => {
      if (v.percent >= 3) {
        res.push(v);
      } else {
        other = {
          assigneeName: '其它',
          issueNum: other.issueNum + v.issueNum,
          percent: other.percent + v.percent,
        };
      }
    });
    if (other.issueNum && other.percent) {
      res.push(other);
    }
    return res;
  }

  renderContent() {
    const { loading, sprintId, assigneeInfo } = this.state;
    if (loading) {
      return (
        <div className="c7n-loadWrap">
          <Spin />
        </div>
      );
    }
    if (!sprintId) {
      return (
        <div className="c7n-loadWrap">
          <EmptyBlockDashboard
            pic={pic}
            des="当前项目下无活跃或结束冲刺"
          />
        </div>
      );
    }
    if (assigneeInfo.every(v => v.issueNum === 0)) {
      return (
        <div className="c7n-loadWrap">
          <EmptyBlockDashboard
            pic={pic2}
            des="当前冲刺下无问题"
          />
        </div>
      );
    }
    return (
      <ReactEcharts
        option={this.getOption()}
        style={{ height: 232 }}
      />
    );
  }


  render() {
    return (
      <div className="c7n-sprintDashboard-assignee">
        {this.renderContent()}
      </div>
    );
  }
}

export default Assignee;
