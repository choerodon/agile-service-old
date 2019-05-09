import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import ReactEcharts from 'echarts-for-react';
import { Icon, Spin } from 'choerodon-ui';
import pic from './no_issue.png';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
import './IssueType.scss';

const { AppState } = stores;

class IssueType extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      sprintId: undefined,
      issueTypeInfo: [],
    };
  }

  componentWillReceiveProps(nextProps) {
    if (nextProps.sprintId !== this.state.sprintId) {
      this.setState({
        sprintId: nextProps.sprintId,
      });
    }
    if (nextProps.sprintId != undefined) {
      this.loadIssueTypeData(nextProps.sprintId); 
    }
  }

  getCategoryCount(code) {
    const { issueTypeInfo } = this.state;
    const datas = [];
    const typeCodes = ['story', 'bug', 'task', 'sub_task'];
    for (let i = 0; i < typeCodes.length; i += 1) {
      const typeIndex = issueTypeInfo.findIndex(item => item.typeCode === typeCodes[i]);
      if (typeIndex === -1) {
        datas[i] = 0;
      } else {
        const statusData = issueTypeInfo[typeIndex].issueStatus.filter(status => (
          status.categoryCode === code
        ));
        if (statusData.length === 0) {
          datas[i] = 0;
        } else {
          datas[i] = statusData.reduce((sum, data) => sum + data.issueNum, 0);
        }
      }
    }
    return datas;
  }
  

  getOption = () => {
    const { issueTypeInfo } = this.state;
    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
        },
        backgroundColor: '#fff',
        textStyle: {
          color: 'rgba(0,0,0,0.64)',
        },
        formatter: (params) => {
          let content = '';
          params.forEach((item) => {
            content = `<div>
            <span>${params[0].axisValue}</span>
            <br />
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>处理中：${this.getCategoryCount('doing')[item.dataIndex]} ${this.getCategoryCount('doing')[item.dataIndex] ? ' 个' : ''}</div>
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>待处理：${this.getCategoryCount('todo')[item.dataIndex]} ${this.getCategoryCount('todo')[item.dataIndex] ? ' 个' : ''}</div>
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[2].color}"></div>已完成：${this.getCategoryCount('done')[item.dataIndex]} ${this.getCategoryCount('done')[item.dataIndex] ? ' 个' : ''}</div>
          </div>`;
          });
          return content;
        },
      },
      legend: {
        orient: 'vertical',
        data: ['待处理', '处理中', '已完成'],
        itemWidth: 14,
        itemHeight: 14,
        itemGap: 48,
        icon: 'rect',
        right: 0,
        top: 35,
      },
      grid: {
        left: '10',
        top: '28px',
        right: '28%',
        bottom: '8%',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        data: ['故事', '故障', '任务', '子任务'],
      },
      yAxis: {
        name: '问题计数',
        nameTextStyle: {
          color: 'rgba(0,0,0,0.64)',      
        },
        type: 'value',
        itemStyle: {
          color: 'rgba(0,0,0,0.64)',
        },
        splitLine: {
          // show: true, 
          //  改变轴线颜色
          lineStyle: {
            // 使用深浅的间隔色
            color: 'rgba(0,0,0,0.12)',
          },                            
        },
      },
      axisLine: {
        lineStyle: {
          opacity: 0,
        },
      },
      axisTick: {
        lineStyle: {
          color: 'transparent',
        },
      },
      axisLabel: {
        color: 'rgba(0,0,0,0.64)',
      },
      series: [
        {
          name: '处理中',
          type: 'bar',
          stack: '计数',
          barCategoryGap: '28px',
          data: this.getCategoryCount('doing'),
          itemStyle: {
            color: '#45A3FC',
          }, 
        },
        {
          name: '待处理',
          type: 'bar',
          stack: '计数',
          data: this.getCategoryCount('todo'),
          itemStyle: {
            color: ' #FFB100',
          },
        },
        {
          name: '已完成',
          type: 'bar',
          stack: '计数',
          data: this.getCategoryCount('done'),
          itemStyle: {
            color: '#00BFA5',
          },
        },
      ],
    };
    return option;
  }

  loadIssueTypeData(sprintId) {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    this.setState({
      loading: true,
    });
    axios.get(`/agile/v1/projects/${projectId}/iterative_worktable/issue_type?organizationId=${orgId}&sprintId=${sprintId}`)
      .then((res) => {
        if (res && res.length) {
          this.setState({
            loading: false,
            issueTypeInfo: res,
          });
        } else {
          this.setState({
            loading: false,
          });
        }
      });
  }

  
  renderContent() {
    const { loading, sprintId } = this.state;
    if (loading) {
      return (
        <div className="c7n-IssueType-loading">
          <Spin />
        </div>
      );
    }
    if (!sprintId) {
      this.setState({
        loading: false,    
      });
      return (
        <div className="c7n-IssueType-empty">
          <EmptyBlockDashboard
            pic={pic}
            des="当前冲刺下没有问题"
          />
        </div>
      );
    }
    return (
      <div className="c7n-IssueType-chart">
        <ReactEcharts
          style={{ height: 230 }}
          option={this.getOption()}
        />
      </div>
    );
  }

  render() {
    return (
      <div className="c7n-IssueType">
        {this.renderContent()}
      </div>
    );
  }
}

export default IssueType;
