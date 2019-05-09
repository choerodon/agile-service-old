import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import { Spin } from 'choerodon-ui';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
// import pic from './no_issue.png';
import pic from '../../../../../assets/image/emptyChart.svg';
import './VersionProgress.scss';


const { AppState } = stores;
class VersionProgress extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      versionProgressInfo: [],
    };
  }

  componentDidMount() {
    this.loadData();
  }

  getXAxisData = () => {
    const { versionProgressInfo } = this.state;
    const xAxisData = [];
    if (versionProgressInfo && versionProgressInfo.length !== 0) {
      versionProgressInfo.forEach((item) => {
        if (_.findIndex(xAxisData, o => o === item.name) === -1) {
          xAxisData.push(item.name);
        }
      });
      return xAxisData;
    }
    return '';
  }

  getVersionProgressData() {
    const { versionProgressInfo } = this.state;
    const xAxisData = this.getXAxisData();
    const versionProgressData = { todoData: [], doingData: [], doneData: [] };
    const { todoData, doingData, doneData } = versionProgressData;
    if (versionProgressInfo && versionProgressInfo.length !== 0) {
      for (let i = 0; i < xAxisData.length; i += 1) {
        const version = versionProgressInfo.filter(item => item.name === xAxisData[i]);
        let todoValue = 0;
        let doingValue = 0;
        let doneValue = 0;
        version.forEach((obj) => {
          if (obj.statusMapDTO && obj.statusMapDTO.type === 'todo') {
            todoValue += obj.count || 0;
          }
          if (obj.statusMapDTO && obj.statusMapDTO.type === 'doing') {
            doingValue += obj.count || 0;
          }
          if (obj.statusMapDTO && obj.statusMapDTO.type === 'done') {
            doneValue += obj.count || 0;
          }
        });
        todoData.push(todoValue);
        doingData.push(doingValue);
        doneData.push(doneValue);
      }
      return versionProgressData;
    }
    return '';
  }

  getOption() {
    const versionProgressData = this.getVersionProgressData();
    const option = {
      grid: {
        left: 10,
        right: 0,
        bottom: 30,
        containLabel: true,
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: { // 坐标轴指示器，坐标轴触发有效
          type: 'shadow', // 默认为直线，可选为：'line' | 'shadow'
        },
        backgroundColor: '#fff',
        textStyle: {
          color: 'rgba(0,0,0,0.64)',
        },
        formatter(params) {
          let content = '';
          params.forEach((item) => {
            content = `<div>
            <span>${params[0].axisValue}</span>
            <br />
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>待处理：${versionProgressData.todoData[item.dataIndex]} ${versionProgressData.todoData[item.dataIndex] ? ' 个' : ''}</div>
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>处理中：${versionProgressData.doingData[item.dataIndex]} ${versionProgressData.doingData[item.dataIndex] ? ' 个' : ''}</div>
            <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[2].color}"></div>已完成：${versionProgressData.doneData[item.dataIndex]} ${versionProgressData.doneData[item.dataIndex] ? ' 个' : ''}</div>
          </div>`;
          });
          return content;
        },
      },
      legend: {
        // top: ,
        // right: 30,
        data: ['待处理', '处理中', '已完成'],
        itemWidth: 14,
        itemHeight: 14,
        itemGap: 48,
        icon: 'rect',
      },
      xAxis: [
        {
          type: 'category',
          data: this.getXAxisData(),
          axisLine: {
            show: false,
                
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            interval: 0,
            fontSize: 12,
            color: 'rgba(0,0,0,0.65)',
            // eslint-disable-next-line no-confusing-arrow
            formatter: (value, index) => value.length > 7 ? `${value.substring(0, 7)}...` : value,
          },
         
        },
      ],
      yAxis: [
        {
          name: '百分比',
          nameTextStyle: {
            fontSize: 12,
          },
          type: 'value',
          axisLine: {
            show: false,
                
          },
          axisTick: {
            show: false,
          },
          axisLabel: {
            fontSize: 12,
            color: 'rgba(0,0,0,0.65)',
          },
          splitLine: {
            lineStyle: {
              color: '#eee',
            },
          },
        },
      ],
      series: [
        {
          name: '待处理',
          type: 'bar',
          // data: [320, 332, 301, 334, 390, 330, 320],
          data: versionProgressData.todoData,
          itemStyle: {
            color: '#FFB100',
          },
          barCategoryGap: '30px',
          barWidth: '14px',
        },
        {
          name: '处理中',
          type: 'bar',
          // data: [120, 132, 101, 134, 90, 230, 210],
          data: versionProgressData.doingData,
          itemStyle: {
            color: '#45A3FC',
          },
          barWidth: '14px',
        },
        {
          name: '已完成',
          type: 'bar',
          // data: [862, 1018, 964, 1026, 1679, 1600, 1570],
          data: versionProgressData.doneData,
          itemStyle: {
            color: ' #00BFA5',
          },
          barWidth: '14px',
        },
      ],
    };

    return option;
  }

  loadData = () => {
    const projectId = AppState.currentMenuType.id;
    this.setState({
      loading: true,
    });
    axios.get(`/agile/v1/projects/${projectId}/reports/version_progress_chart`)
      .then((res) => {
        if (!res.length) {
          this.setState({
            loading: false,
          });
        }
        this.setState({
          loading: false,
          versionProgressInfo: res,
        });
      });
  }

  renderContent() {
    const { loading, versionProgressInfo } = this.state;
    if (loading) {
      return (
        <div className="c7n-versionProgress-loading">
          <Spin />
        </div>
      );
    }
    if (!versionProgressInfo || !versionProgressInfo.length) {
      return (
        <div className="c7n-versionProgress-EmptyBlock">
          <EmptyBlockDashboard
            pic={pic}
            des="当前版本下没有问题"
          />
        </div>
      );
    }
    return (
      <div className="c7n-versionProgress-chart">
        <ReactEcharts 
          style={{ height: 304 }}
          option={this.getOption()}
        />
      </div>
    );
  }
  

  render() {
    return (
      <div className="c7n-reportBoard-versionProgress">
        <div className="c7n-versionProgress-content">
          {this.renderContent()}
        </div>
      </div>
    );
  }
}

export default VersionProgress;
