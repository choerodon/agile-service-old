import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react';
import { stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';
import { Spin } from 'choerodon-ui';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
// import pic from './no_issue.png';
import pic from '../../../../../assets/image/emptyChart.svg';
import './IterationType.scss';

const { AppState } = stores;
class IterationType extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      iterationTypeInfo: [],
    };
  }

  componentDidMount() {
    this.loadData();
  }

  getXAxisData = () => {
    const { iterationTypeInfo } = this.state;
    const xAxisData = [];
    if (iterationTypeInfo && iterationTypeInfo.length !== 0) {
      iterationTypeInfo.forEach((item) => {
        if (_.findIndex(xAxisData, o => o === (item.issueTypeDTO
            && item.issueTypeDTO.name)) === -1) {
          xAxisData.push(item.name);
        }
      });
      return xAxisData;
    }
    return '';
  };

  getIterationTypeData() {
    const { iterationTypeInfo } = this.state;
    const xAxisData = this.getXAxisData();
    const iterationTypeData = { todoData: [], doingData: [], doneData: [] };
    const { todoData, doingData, doneData } = iterationTypeData;
    if (iterationTypeInfo && iterationTypeInfo.length !== 0) {
      for (let i = 0; i < xAxisData.length; i += 1) {
        const iterationType = iterationTypeInfo.filter(item => (item.issueTypeDTO
          && item.issueTypeDTO.name) === xAxisData[i]);
        let todoValue = 0;
        let doingValue = 0;
        let doneValue = 0;
        iterationType.forEach((obj) => {
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
      return iterationTypeData;
    }
    return '';
  }

  getOption() {
    const xAxisData = this.getXAxisData();
    const iterationTypeData = this.getIterationTypeData();
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
      },
      legend: {
        orient: 'vertical',
        data: ['待处理', '处理中', '已完成'],
        itemWidth: 14,
        itemHeight: 14,
        itemGap: 48,
        icon: 'rect',
        right: 0,
        top: 25,
      },
      grid: {
        left: '5px',
        top: '26px',
        right: '28%',
        bottom: 30,
        containLabel: true,
      },
      xAxis: [
        {
          type: 'category',
          data: xAxisData,
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
          },
        },
      ],
      yAxis: [
        {
          name: '问题计数',
          nameTextStyle: {
            fontSize: 12,
            color: 'rgba(0,0,0,0.64)',  
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
          stack: '广告',
          // data: [120, 132, 101, 134],
          data: iterationTypeData.todoData,
          barCategoryGap: '28px',
          barWidth: '24px',
          itemStyle: {
            color: '#FFB100',
          },
        },
        {
          name: '处理中',
          type: 'bar',
          stack: '广告',
          // data: [220, 182, 191, 234],
          data: iterationTypeData.doingData,
          itemStyle: {
            color: '#45A3FC',
          },
        },
        {
          name: '已完成',
          type: 'bar',
          stack: '广告',
          // data: [150, 232, 201, 154],
          data: iterationTypeData.doneData,
          itemStyle: {
            color: ' #00BFA5',
          },
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
    axios.get(`/agile/v1/projects/${projectId}/reports/issue_type_distribution_chart`)
      .then((res) => {
        if (res && res.length) {
          this.setState({
            loading: false,
            iterationTypeInfo: res,
          });
        } else {
          this.setState({
            loading: false,
          });
        }
      });
  }

  renderContent() {
    const { loading, iterationTypeInfo } = this.state;
    if (loading) {
      return (
        <div className="c7n-IterationType-loading">
          <Spin />
        </div>
      );
    }
    if (!iterationTypeInfo || !iterationTypeInfo.length) {
      return (
        <div className="c7n-IterationType-emptyBlock">
          <EmptyBlockDashboard
            pic={pic}
            des="当前迭代下没有问题"
          />
        </div>
      );
    }
    return (
      <div className="c7n-iterationType-chart">
        <ReactEcharts 
          style={{ height: 227 }}
          option={this.getOption()}
        />
      </div>
    );
  }

  render() {
    return (
      <div className="c7n-reportBoard-IterationType">
        <div className="c7n-IterationType-content">
          {this.renderContent()}
        </div>
      </div>
    );
  }
}

export default IterationType;
