import React, { Component } from 'react';
import ReactEcharts from 'echarts-for-react';
import { withRouter } from 'react-router-dom';
import { Spin } from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
// import pic from './no_epic.svg';
import pic from '../../../../../assets/image/emptyChart.svg';
import './index.scss';

const { AppState } = stores;

class EpicProgress extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      loading: true,
    };
  }

  componentDidMount() {
    this.loadEpics();
  }

  getOption() {
    const { data } = this.state;
    return {
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#fff',
        textStyle: {
          color: '#000',
        },
        formatter(params) {
          const epic = params.find(v => v.value !== undefined);
          const res = `${epic.name}:</br>${epic.value}%`;
          return res;
        },
        extraCssText: 
          'box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2); border: 1px solid #ddd; border-radius: 0;',
      },
      legend: {
        orient: 'vertical',
        x: 'left',
        padding: [0, 10, 0, 30],
        itemWidth: 14,
        itemGap: 20,
        textStyle: {
          width: 100,
        },
        formatter(name) {
          return name.length > 6 ? `${name.slice(0, 6)}...` : name;
        },
        data: [
          ...[
            data[0] ? data[0].summary : undefined,
            data[3] ? data[3].summary : undefined,
          ],
          '',
          ...[
            data[1] ? data[1].summary : undefined,
            data[4] ? data[4].summary : undefined,
          ],
          '',
          ...[
            data[2] ? data[2].summary : undefined,
            data[5] ? data[5].summary : undefined,
          ],
        ],
      },
      grid: {
        y2: 30,
        top: '100',
        left: '30',
        right: '40',
        containLabel: true,
      },
      calculable: true,
      xAxis: {
        type: 'category',
        boundaryGap: true,
        nameGap: -10,
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
          rotate: 40,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 9,
            fontStyle: 'normal',
          },
          formatter(value) {
            if (value.length > 5) {
              return `${value.slice(0, 4)}...`;
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
        data: data.map(v => v.summary),
      },
      yAxis: {
        type: 'value',
        nameTextStyle: {
          color: '#000',
        },
        axisTick: { show: false },
        axisLine: {
          show: false,
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
          formatter(value) {
            return `${value}%`;
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
      series: data.map((v, i) => ({
        name: v.summary,
        type: 'bar',
        barWidth: 24,
        barGap: '-100%',
        itemStyle: {
          normal: {
            barBorderRadius: 2,
            barBorderWidth: 1,
            barBorderColor:
              [
                '#ffb100', '#303f9f', '#ff7043',
                '#f44336', '#f953ba', '#00bfa5',
              ][i],
            color() {
              const colorList = [
                'rgba(255, 177, 0, 0.4)', 'rgba(48, 63, 159, 0.4)',
                'rgba(255, 112, 67, 0.4)', 'rgba(244, 67, 54, 0.4)',
                'rgba(249, 83, 186, 0.4)', 'rgba(0, 191, 165, 0.4)',
              ];
              return colorList[i];
            },
          },
        },
        data: data.map((x, y) => (y === i ? x.progress : undefined)),
      })),
    };
  }

  loadEpics() {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${projectId}/issues/epics?organizationId=${orgId}`)
      .then((res) => {
        this.handleEpics(res.slice(0, 6));
      });
  }

  handleEpics(epics) {
    const epicsProgress = epics.map(epic => ({
      issueId: epic.issueId,
      summary: epic.epicName,
      progress: this.calcProgress(epic.doneIssueCount, epic.issueCount),
    }));
    this.setState({
      data: epicsProgress,
      loading: false,
    });
  }

  calcProgress(doneCount, count) {
    if (!count) return 0;
    return ((doneCount / count).toFixed(2) * 100).toFixed(0);
  }

  renderContent() {
    const { loading, data } = this.state;
    if (loading) {
      return (
        <div className="loading-wrap">
          <Spin />
        </div>
      );
    }
    if (data && !data.length) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic}
            des="当前没有史诗"
          />
        </div>
      );
    }
    return (
      <ReactEcharts
        className="c7n-chart"
        option={this.getOption()}
        style={{ height: 307 }}
      />
    );
  }

  render() {
    return (
      <div className="c7n-agile-reportBoard-epicProgress">
        {this.renderContent()}
      </div>
    );
  }
}

export default withRouter(EpicProgress);
