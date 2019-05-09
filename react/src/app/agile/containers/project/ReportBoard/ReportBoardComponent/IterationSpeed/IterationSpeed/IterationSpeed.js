import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import {
  Dropdown, Icon, Menu, Spin,
} from 'choerodon-ui';
import {
  stores, axios,
} from 'choerodon-front-boot';
import EmptyBlockDashboard from '../../../../../../components/EmptyBlockDashboard';
// import pic from './no_sprint.svg';
import pic from '../../../../../../assets/image/emptyChart.svg';
import './IterationSpeed.scss';

const { AppState } = stores;
const UNIT_STATUS = {
  issue_count: {
    committed: 'committedIssueCount',
    completed: 'completedIssueCount',
  },
  story_point: {
    committed: 'committedStoryPoints',
    completed: 'completedStoryPoints',
  },
  remain_time: {
    committed: 'committedRemainTime',
    completed: 'completedRemainTime',
  },
};

class IterationSpeed extends Component {
  constructor(props) {
    super(props);
    this.state = {
      unit: 'issue_count',
      loading: true,
      chartDataX: [],
      chartDataYCommitted: [],
      chartDataYCompleted: [],
    };
  }

  componentDidMount() {
    this.loadChartData();
  }

  getyAxisName(unit) {
    const UNIT_NAME = {
      remain_time: '剩余时间',
      story_point: '故事点',
      issue_count: '问题计数',
    };
    return UNIT_NAME[unit] || '';
  }

  getOption() {
    const {
      unit, chartDataX, chartDataYCommitted, chartDataYCompleted,
    } = this.state;
    let curUnit = '';
    if (unit === 'story_point') {
      curUnit = '点';
    }

    if (unit === 'issue_count') {
      curUnit = '个';
    }

    if (unit === 'remain_time') {
      curUnit = '小时';
    }
    return {
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#fff',
        textStyle: {
          color: '#000',
        },
        extraCssText: 
          'box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2); border: 1px solid #ddd; border-radius: 0;',
        axisPointer: {
          type: 'shadow',
        },
        formatter: (params, ticket, callback) => {
          let content = '';
          params.forEach((item, index) => {
            content = `<div>
            <span>${params[0].axisValue}</span>
            <br />
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>预估：${chartDataYCommitted[item.dataIndex]} ${chartDataYCommitted[item.dataIndex] && curUnit ? curUnit : ''}</div>
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>已完成：${chartDataYCompleted[item.dataIndex]} ${chartDataYCompleted[item.dataIndex] && curUnit ? curUnit : ''}</div>
          </div>`;
          });
          return content;
        },
      },
      legend: {
        orient: 'horizontal',
        x: 'right',
        y: 0,
        padding: [0, 50, 0, 0],
        itemWidth: 14,
        data: [
          {
            name: '预估',
            icon: 'rectangle',
          },
          {
            name: '已完成',
            icon: 'rectangle',
          },
        ],
      },
      grid: {
        top: '30',
        left: '20',
        right: '50',
        bottom: '50',
        containLabel: true,
      },
      calculable: true,
      xAxis: {
        name: '冲刺',
        type: 'category',
        boundaryGap: true,
        nameGap: -10,
        nameLocation: 'end',
        nameTextStyle: {
          color: '#000',
          padding: [35, 0, 0, 0],
        },
        axisTick: { show: false },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#eee',
            type: 'solid',
            width: 2,
          },
        },
        axisLabel: {
          show: true,
          interval: 0,
          margin: 13,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 12,
            fontStyle: 'normal',
          },
          formatter(value) {
            if (value.length > 10) {
              return `${value.slice(0, 11)}...`;
            } else {
              return value;
            }
          },
        },
        splitLine: {
          show: false,
          onGap: false,
          interval: 0,
          lineStyle: {
            color: ['#eee'],
            width: 1,
            type: 'solid',
          },
        },
        data: chartDataX,
      },
      yAxis: {
        name: this.getyAxisName(unit),
        type: 'value',
        minInterval: 1,
        nameTextStyle: {
          color: '#000',
        },
        axisTick: { show: false },
        axisLine: {
          show: true,
          lineStyle: {
            color: '#eee',
            type: 'solid',
            width: 2,
          },
        },

        axisLabel: {
          show: true,
          interval: 'auto',
          margin: 18,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 12,
            fontStyle: 'normal',
          },
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: '#eee',
            type: 'solid',
            width: 1,
          },
        },
      },
      dataZoom: [{
        startValue: chartDataX[0],
        endValue: chartDataX[8],
        zoomLock: true,
        type: 'slider',
        handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
        handleSize: '100%',
        handleStyle: {
          color: '#fff',
          shadowBlur: 3,
          shadowColor: 'rgba(0, 0, 0, 0.6)',
          shadowOffsetX: 2,
          shadowOffsetY: 2,
        },
      }],
      series: [
        {
          name: '预估',
          type: 'bar',
          barWidth: 34,
          barGap: '12%',
          itemStyle: {
            color: '#d3d3d3',
          },
          data: chartDataYCommitted,
          emphasis: {
            itemStyle: {
              color: '#e0e0e0',
            },
          },
        },
        {
          name: '已完成',
          type: 'bar',
          barWidth: 34,
          data: chartDataYCompleted,
          itemStyle: {
            color: '#00bfa5',
          },
          lineStyle: {
            type: 'dashed',
            color: 'grey',
          },
          emphasis: {
            itemStyle: {
              color: '#35e6ce',
            },
          },
        },
      ],
    };
  }

  loadChartData(unit = 'issue_count') {
    this.setState({ loading: true });
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/velocity_chart?type=${unit}`)
      .then((res) => {
        this.setState({
          chartDataX: _.map(res, 'sprintName'),
          chartDataYCommitted: _.map(res, UNIT_STATUS[unit].committed),
          chartDataYCompleted: _.map(res, UNIT_STATUS[unit].completed),
          loading: false,
        });
      });
  }

  handleChangeUnit({ key }) {
    this.setState({ unit: key });
    this.loadChartData(key);
  }

  renderContent() {
    const { loading, chartDataX } = this.state;
    if (loading) {
      return (
        <div className="loading-wrap">
          <Spin />
        </div>
      );
    }
    if (!chartDataX.length) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic}
            des="当前项目下无活跃或结束冲刺"
          />
        </div>
      );
    }
    return (
      <ReactEcharts
        style={{ height: 434 }}
        option={this.getOption()}
      />
    );
  }

  render() {
    const { loading } = this.state;
    const menu = (
      <Menu onClick={this.handleChangeUnit.bind(this)}>
        <Menu.Item key="remain_time">剩余时间</Menu.Item>
        <Menu.Item key="story_point">故事点</Menu.Item>
        <Menu.Item key="issue_count">问题计数</Menu.Item>
      </Menu>
    );
    return (
      <div className="c7n-agile-reportBoard-iterationSpeed">
        <div className="switch" style={{ display: loading ? 'none' : 'block' }}>
          <Dropdown overlay={menu} trigger={['click']}>
            <div className="ant-dropdown-link c7n-agile-dashboard-burndown-select">
              {'单位选择'}
              <Icon type="arrow_drop_down" />
            </div>
          </Dropdown>
        </div>
        {this.renderContent()}
      </div>

    );
  }
}

export default withRouter(IterationSpeed);
