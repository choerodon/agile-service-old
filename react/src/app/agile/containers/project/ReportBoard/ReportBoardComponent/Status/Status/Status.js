import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import ReactEcharts from 'echarts-for-react';
import { Spin } from 'choerodon-ui';
import _ from 'lodash';
import EmptyBlockDashboard from '../../../../../../components/EmptyBlockDashboard';
// import pic2 from '../../EmptyPics/no_version.svg';
import pic2 from '../../../../../../assets/image/emptyChart.svg';
import './Status.scss';

const { AppState } = stores;

class Status extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      statusInfo: [],
    };
  }

  componentDidMount() {
    this.loadStatus();
  }

  getOption() {
    const { statusInfo } = this.state;
    const allCount = _.reduce(statusInfo, (sum, n) => sum + n, 0);
    const option = {
      legend: {
        orient: 'vertical',
        x: '70%',
        // y: 'center',
        top: '52px',
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
        itemGap: 30,
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
          radius: '80px',
          avoidLabelOverlap: false,
          hoverAnimation: false,
          center: ['35%', '45%'],
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
            { value: statusInfo[0], name: '待处理' },
            { value: statusInfo[1], name: '处理中' },
            { value: statusInfo[2], name: '已完成' },
          ],
          itemStyle: { 
            normal: { 
              borderColor: '#FFFFFF',
              borderWidth: 1, 
            },
          },
        },
      ],
    };
    return option;
  }

  loadStatus = () => {
    const projectId = AppState.currentMenuType.id;
    this.setState({ loading: true });
    axios.get(`agile/v1/projects/${projectId}/reports/issue_type_distribution_chart`)
      .then((res) => {
        const statusInfo = this.transformStatus(res);
        this.setState({
          loading: false,
          statusInfo,
        });
      })
      .catch((e) => {
        this.setState({
          loading: false,
        });
      });
  };

  transformStatus = (statusArr) => {
    const todo = _.reduce(statusArr,
      (sum, n) => sum + (n.statusMapDTO && n.statusMapDTO.type === 'todo' ? n.count : 0), 0);
    const doing = _.reduce(statusArr,
      (sum, n) => sum + (n.statusMapDTO && n.statusMapDTO.type === 'doing' ? n.count : 0), 0);
    const done = _.reduce(statusArr,
      (sum, n) => sum + (n.statusMapDTO && n.statusMapDTO.type === 'done' ? n.count : 0), 0);
    const result = [
      todo || 0,
      doing || 0,
      done || 0,
    ];
    return result;
  };

  renderContent() {
    const { statusInfo, loading } = this.state;
    if (loading) {
      return (
        <div className="c7n-loadWrap">
          <Spin />
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
        style={{ height: 233 }}
      />
    );
  }

  render() {
    return (
      <div className="c7n-agile-reportBoard-status">
        {this.renderContent()}
      </div>
    );
  }
}

export default Status;
