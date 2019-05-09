import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import ReactEcharts from 'echarts-for-react';
import { Spin } from 'choerodon-ui';
import _ from 'lodash';
import EmptyBlockDashboard from '../../../../../../components/EmptyBlockDashboard';
import pic from '../../EmptyPics/no_sprint.svg';
import pic2 from '../../EmptyPics/no_version.svg';
import './Status.scss';

const { AppState } = stores;

class Status extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprintId: undefined,
      loading: true,
      statusInfo: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    const { sprintId } = this.props;
    if (nextProps.sprintId !== sprintId) {
      const newSprintId = nextProps.sprintId;
      this.setState({
        sprintId: newSprintId,
      });
      this.loadStatus(newSprintId);
    }
  }

  getOption() {
    const { statusInfo } = this.state;
    const allCount = _.reduce(statusInfo, (sum, n) => sum + n, 0);
    const option = {
      legend: {
        orient: 'vertical',
        x: '60%',
        top: '18%',
        data: [
          {
            name: '待处理',
            icon: 'circle',
          }, 
          {
            name: '处理中',
            icon: 'circle',
          }, 
          {
            name: '已完成',
            icon: 'circle',
          },
        ],
        itemWidth: 12,
        itemHeight: 12,
        itemGap: 16,
        textStyle: {
          fontSize: '13',
        },
      },
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
          color: ['#FFB100', '#4D90FE', '#00BFA5'],
          type: 'pie',
          radius: '60px',
          avoidLabelOverlap: false,
          hoverAnimation: false,
          center: ['35%', '42%'],
          label: {
            normal: {
              show: false,
              position: 'center',
              textStyle: {
                fontSize: '13',
              },
            },
            emphasis: {
              show: false,
              
            },
          },
          data: [
            { value: statusInfo[0] || 0, name: '待处理' },
            { value: statusInfo[1] || 0, name: '处理中' },
            { value: statusInfo[2] || 0, name: '已完成' },
          ],
          itemStyle: { 
            normal: { 
              borderColor: '#FFFFFF', borderWidth: 1, 
            },
          },
        },
      ],
    };
    return option;
  }

  loadStatus(sprintId) {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    this.setState({ loading: true });
    axios.get(`/agile/v1/projects/${projectId}/iterative_worktable/status?organizationId=${orgId}&sprintId=${sprintId}`)
      .then((res) => {
        const statusInfo = this.transformStatus(res);
        this.setState({
          loading: false,
          statusInfo,
        });
      });
  }

  transformStatus(statusArr) {
    const todo = statusArr.filter(v => v.categoryCode === 'todo');
    const doing = statusArr.filter(v => v.categoryCode === 'doing');
    const done = statusArr.filter(v => v.categoryCode === 'done');
    const result = [
      todo ? _.reduce(_.map(todo, 'issueNum'), (sum, n) => sum + n, 0) : 0,
      doing ? _.reduce(_.map(doing, 'issueNum'), (sum, n) => sum + n, 0) : 0,
      done ? _.reduce(_.map(done, 'issueNum'), (sum, n) => sum + n, 0) : 0,
    ];
    return result;
  }

  renderContent() {
    const { statusInfo, loading, sprintId } = this.state;
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
    if (statusInfo.every(v => v === 0)) {
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
        style={{ height: 144 }}
      />
    );
  }

  render() {
    return (
      <div className="c7n-agile-srpintDashboard-status">
        {this.renderContent()}
      </div>
    );
  }
}

export default Status;
