import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import moment from 'moment';
import {
  Dropdown, Icon, Menu, Spin, Checkbox,
} from 'choerodon-ui';
import { stores, axios } from 'choerodon-front-boot';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
import { loadSprints } from '../../../../../api/NewIssueApi';
import pic from '../../../../../assets/image/emptyChart.svg';
import lineLegend from './Line.svg';
import './BurnDown.scss';

/* eslint-disable */
const { AppState } = stores;

class BurnDown extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprintId: undefined,
      sprint: {},
      unit: 'remainingEstimatedTime',
      loading: true,
      restDayShow: true,
      restDays: [],
      exportAxis: [],
      markAreaData: [],
    };
  }

  componentDidMount() {
    this.loadSprints()
  }


  getUnitFromLocalStorage() {
    if (!window.localStorage) {
      return 'remainingEstimatedTime';
    } else {
      const storage = window.localStorage;
      return storage['c7n-agile-iterationBoard-burndown'] || 'remainingEstimatedTime';
    }
  }

  setUnitFromLocalStorage(unit) {
    if (!window.localStorage) {
      return false;
    } else {
      const storage = window.localStorage;
      storage['c7n-agile-iterationBoard-burndown'] = unit;
      return true;
    }
  }

  getyAxisName(unit) {
    const UNIT_NAME = {
      remainingEstimatedTime: '剩余时间',
      storyPoints: '故事点',
      issueCount: '问题计数',
    };
    return UNIT_NAME[unit] || '';
  }

  getOption() {
    const {
      unit, xAxis, yAxis, expectCount, sprint: { startDate, endDate },
    } = this.state;
    return {
      tooltip: {
        trigger: 'axis',
        backgroundColor: '#fff',
        textStyle: {
          color: '#000',
        },
        extraCssText:
          'box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.2); border: 1px solid #ddd; border-radius: 0;',
        // formatter: function (params) {
        //   let content = '';
        //   params.forEach((item) => {
        //     if (item.seriesName === '剩余值') {
        //       content = `${item.axisValue || '冲刺开启'}<br />${item.marker}${item.seriesName} : ${(item.value || item.value === 0) ? item.value  : '-'}${item.value && ' 个'}`;
        //     }
        //   });
        //   return content;
        // }
        formatter(params) {
          let content = '';
          let curUnit = '';
          params.forEach((item) => {
            if (item.seriesName === '剩余值') {
              if (item.value && unit === 'remainingEstimatedTime') {
                curUnit = ' 小时';
              }
              if (item.value && unit === 'storyPoints') {
                curUnit = ' 点';
              }
              if (item.value && unit === 'issueCount') {
                curUnit = ' 个';
              }
              content = `${item.axisValue || '冲刺开启'}<br />${item.marker}${item.seriesName} : ${(item.value || item.value === 0) ? item.value : '-'}${curUnit && curUnit}`;
            }
          });
          return content;
        },
      },
      legend: {
        top: '0',
        right: '40',
        itemHeight: 2,
        data: [{
          name: '期望值',
          icon: `image://${lineLegend}`,
        }, {
          name: '剩余值',
          icon: 'line',
        }],
      },
      grid: {
        y2: 30,
        top: '40',
        left: '10',
        right: '40',
        containLabel: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: xAxis,
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
          interval: parseInt(xAxis.length / 7) ? parseInt(xAxis.length / 7) - 1 : 0,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 9,
            fontStyle: 'normal',
          },
        },
        splitLine: {
          show: true,
          onGap: false,
          interval: 0,
          lineStyle: {
            color: ['#eee'],
            width: 1,
            type: 'solid',
          },
        },
      },
      yAxis: {
        name: this.getyAxisName(unit),
        nameTextStyle: {
          color: '#000',
        },
        nameGap: 22,
        type: 'value',
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
          margin: 8,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 12,
            fontStyle: 'normal',
          },
          formatter(value, index) {
            if (unit === 'remainingEstimatedTime' && value) {
              return `${value}h`;
            } else {
              return value;
            }
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
      series: [
        {
          symbol: 'none',
          name: '期望值',
          type: 'line',
          data: this.state.exportAxis,
          itemStyle: {
            color: 'rgba(0,0,0,0.65)',
          },
          lineStyle: {
            type: 'dotted',
            color: 'rgba(0,0,0,0.65)',
          },
          markArea: {
            itemStyle: {
              color: 'rgba(235,235,235,0.65)',
            },
            emphasis: {
              itemStyle: {
                color: 'rgba(220,220,220,0.65)',
              },
            },
            data: this.state.markAreaData,
          },
        },
        {
          symbol: 'none',
          name: '剩余值',
          type: 'line',
          itemStyle: {
            color: '#4f9bff',
          },
          data: yAxis,
        },
      ],
    };
  }

  getBetweenDateStr(start, end) {
    const { restDayShow, restDays } = this.state;
    const result = [];
    const rest = [];
    const beginDay = start.split('-');
    const endDay = end.split('-');
    const diffDay = new Date();
    const dateList = [];
    let i = 0;
    diffDay.setDate(beginDay[2]);
    diffDay.setMonth(beginDay[1] - 1);
    diffDay.setFullYear(beginDay[0]);
    while (i == 0) {
      const countDay = diffDay.getTime();
      if (restDays.includes(moment(diffDay).format('YYYY-MM-DD'))) {
        rest.push(moment(diffDay).format('YYYY-MM-DD'));
      }
      dateList[2] = diffDay.getDate();
      dateList[1] = diffDay.getMonth() + 1;
      dateList[0] = diffDay.getFullYear();
      if (String(dateList[1]).length == 1) { dateList[1] = `0${dateList[1]}`; }
      if (String(dateList[2]).length === 1) { dateList[2] = `0${dateList[2]}`; }
      if (restDayShow || !restDays.includes(moment(diffDay).format('YYYY-MM-DD'))) {
        result.push(`${dateList[0]}-${dateList[1]}-${dateList[2]}`);
      }
      diffDay.setTime(countDay + 24 * 60 * 60 * 1000);
      if (dateList[0] == endDay[0] && dateList[1] == endDay[1] && dateList[2] == endDay[2]) {
        i = 1;
      }
    }
    return { result, rest };
  }

  loadSprints(unit = this.state.unit) {
    const projectId = AppState.currentMenuType.id;
    this.setState({ loading: true });
    axios.post(`/agile/v1/projects/${projectId}/sprint/names`, ['started', 'closed'])
      .then((res) => {
        const sprint = res.find(v => v.statusCode === 'started');
        if (res && res.length && sprint) {
          const sprintId = sprint.sprintId;
          this.setState({
            sprint,
            sprintId
          });
          this.getRestDays(sprintId, unit);
        } else {
          this.setState({ loading: false });
        }
      });
  }

  getRestDays = (sprintId, unit) => {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`/agile/v1/projects/${projectId}/sprint/query_non_workdays/${sprintId}/${orgId}`).then((res) => {
      if (res) {
        this.setState({
          restDays: res.map((date) => moment(date).format('YYYY-MM-DD')),
        }, () => {
          this.loadChartData(sprintId, unit);
        });
      } else {
        this.loadChartData(sprintId, unit);
      }
    }).catch(() => {
      this.loadChartData(sprintId, unit);
    });
  };

  loadChartData(sprintId, unit = 'remainingEstimatedTime') {
    if(sprintId) {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/reports/${sprintId}/burn_down_report/coordinate?type=${unit}&ordinalType=asc`)
      .then((res) => {
        const dataDates = Object.keys(res.coordinate);
        const [dataMinDate, dataMaxDate] = [dataDates[0], dataDates[dataDates.length - 1]];
        const { sprint: { endDate } } = this.state;
        const sprintMaxDate = endDate.split(' ')[0];
        const maxDate = moment(dataMaxDate).isBefore(moment(sprintMaxDate))
          ? sprintMaxDate : dataMaxDate;
        let allDate;
        let rest = [];
        if (moment(maxDate).isBefore(sprintMaxDate)) {
          const result = this.getBetweenDateStr(dataMinDate, sprintMaxDate);
          allDate = result.result;
          rest = result.rest;
        } else if (moment(dataMinDate).isSame(maxDate)) {
          allDate = [dataMinDate];
        } else {
          const result = this.getBetweenDateStr(dataMinDate, maxDate);
          allDate = result.result;
          rest = result.rest;
        }
        const xData = allDate;
        let markAreaData = [];
        let exportAxisData = [res.expectCount];
        const { restDayShow } = this.state;
        // 如果展示非工作日，期望为一条连续斜线
        if (!restDayShow) {
          if (allDate.length) {
            exportAxisData = [
              ['', res.expectCount],
              [allDate[allDate.length - 1].split(' ')[0].slice(5).replace('-', '/'), 0],
            ];
          }
        }

        // fix on 916, the crash of endless loop, when dataMinDate and maxDate is same.

        // const xData = this.getBetweenDateStr(dataMinDate, maxDate);
        const xDataFormat = _.map(xData, item => item.slice(5).replace('-', '/'));
        const yAxis = xData.map((data, index) => {
          // 显示非工作日，则非工作日期望为水平线
          if (restDayShow) {
            // 工作日天数
            const countWorkDay = (allDate.length - rest.length) || 1;
            // 日工作量
            const dayAmount = res.expectCount / countWorkDay;
            if (rest.includes(allDate[index])) {
              // 非工作日
              markAreaData.push([
                {
                  xAxis: index === 0 ? '' : allDate[index - 1].split(' ')[0].slice(5).replace('-', '/'),
                },
                {
                  xAxis: allDate[index].split(' ')[0].slice(5).replace('-', '/'),
                }
              ]);
              exportAxisData[index + 1] = exportAxisData[index];
            } else {
              // 工作量取整
              exportAxisData[index + 1] = (exportAxisData[index] - dayAmount) < 0 ? 0 : exportAxisData[index] - dayAmount;
            }
          }
          if (dataDates.includes(data)) return res.coordinate[data];
          if (moment(data).isAfter(moment())) return null;
          res.coordinate[data] = res.coordinate[xData[index - 1]];
          return res.coordinate[xData[index - 1]];
        });
        yAxis.unshift(res.expectCount);
        this.setState({
          expectCount: res.expectCount,
          xAxis: ['', ...xDataFormat],
          yAxis,
          loading: false,
          exportAxis: exportAxisData,
          markAreaData,
        });
      });
    } else {
      this.setState({
        loading: false,
      })
    }
    
  }

  handleChangeUnit({ key }) {
    this.setState({ loading: true });
    const { sprintId } = this.state;
    this.setState({ unit: key });
    this.setUnitFromLocalStorage(key);
    this.loadChartData(sprintId, key);
  }

  onCheckChange = (e) => {
    this.setState({
      restDayShow: e.target.checked,
    }, () => {
      const { sprintId, unit } = this.state;
      this.loadChartData(sprintId, unit);
    });
  };

  renderContent() {
    const { loading, sprintId } = this.state;
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
    return (
      <ReactEcharts
        style={{ height: 400 }}
        option={this.getOption()}
      />
    );
  }

  render() {
    const { loading, sprintId } = this.state;
    const menu = (
      <Menu onClick={this.handleChangeUnit.bind(this)}>
        <Menu.Item key="remainingEstimatedTime">剩余时间</Menu.Item>
        <Menu.Item key="storyPoints">故事点</Menu.Item>
        <Menu.Item key="issueCount">问题计数</Menu.Item>
      </Menu>
    );
    return (
      <div className="c7n-agile-iterationboard-burndown">
        <div className="switch" style={{ display: !loading && !sprintId ? 'none' : 'block' }}>
          <Dropdown overlay={menu} trigger={['click']} getPopupContainer={triggerNode => triggerNode.parentNode}>
            <div className="ant-dropdown-link c7n-agile-dashboard-burndown-select">
              {'单位选择'}
              <Icon type="arrow_drop_down" />
            </div>
          </Dropdown>
          <Checkbox
            style={{ marginLeft: 24 }}
            checked={this.state.restDayShow}
            onChange={this.onCheckChange}
          >
            显示非工作日
          </Checkbox>
        </div>
        {this.renderContent()}
      </div>

    );
  }
}

export default withRouter(BurnDown);
