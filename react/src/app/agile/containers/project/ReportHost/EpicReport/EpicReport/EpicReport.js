import React, { Component } from 'react';
import { observer } from 'mobx-react';
import ReactEcharts from 'echarts-for-react';
import _ from 'lodash';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import {
  Button, Tabs, Table, Select, Icon, Tooltip, Spin,
} from 'choerodon-ui';
// import pic from './no_epic.svg';
import pic from '../../../../../assets/image/emptyChart.svg';
import finish from './legend/finish.svg';
import SwithChart from '../../Component/switchChart';
import StatusTag from '../../../../../components/StatusTag';
import PriorityTag from '../../../../../components/PriorityTag';
import TypeTag from '../../../../../components/TypeTag';
import ES from '../../../../../stores/project/epicReport';
import EmptyBlock from '../../../../../components/EmptyBlock';
import './EpicReport.scss';
import { STATUS } from '../../../../../common/Constant';

const { TabPane } = Tabs;
const { AppState } = stores;
const { Option } = Select;
const MONTH = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];

@observer
class EpicReport extends Component {
  constructor(props) {
    super(props);
    this.state = {
      linkFromParamUrl: undefined,
    };
  }

  componentDidMount() {
    const { location: { search } } = this.props;
    const linkFromParamUrl = _.last(search.split('&')).split('=')[0] === 'paramUrl' ? _.last(search.split('&')).split('=')[1] : undefined;
    this.setState({
      linkFromParamUrl,
    });
    ES.loadEpicAndChartAndTableData();
  }

  getLabel(record) {
    if (ES.beforeCurrentUnit === 'story_point') {
      if (record.issueTypeDTO && record.issueTypeDTO.typeCode === 'story') {
        return record.storyPoints === null ? '未预估' : record.storyPoints;
      } else {
        return '';
      }
    } else {
      return record.remainTime === null ? '未预估' : record.remainTime;
    }
  }

  getOption() {
    const commonOption = {
      tooltip: {
        trigger: 'axis',
        formatter: (params, ticket, callback) => {
          let content = '';
          params.forEach((item) => {
            if (ES.beforeCurrentUnit === 'issue_count') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[0].color}"></div>总问题数：${ES.getChartDataYIssueCountAll[item.dataIndex]} ${ES.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[1].color}"></div>已完成问题数：${ES.getChartDataYIssueCountCompleted[item.dataIndex]} ${ES.getChartDataYIssueCountCompleted[item.dataIndex] ? ' 个' : ''}</div>
            </div>`;
            }
  
            if (ES.beforeCurrentUnit === 'story_point') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[0].color}"></div>总问题数：${ES.getChartDataYIssueCountAll[item.dataIndex]} ${ES.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[1].color}"></div>未预估问题数：${ES.getChartDataYIssueCountUnEstimate[item.dataIndex]} ${ES.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[2].color}"></div>已完成故事点：${ES.getChartDataYCompleted[item.dataIndex]}${ES.getChartDataYCompleted[item.dataIndex] ? ' 点' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[3].color}"></div>总计故事点：${ES.getChartDataYAll[item.dataIndex]}${ES.getChartDataYAll[item.dataIndex] ? ' 点' : ''}</div>
            </div>`;
            }
  
            if (ES.beforeCurrentUnit === 'remain_time') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[0].color}"></div>总问题数：${ES.getChartDataYIssueCountAll[item.dataIndex]} ${ES.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[1].color}"></div>未预估问题数：${ES.getChartDataYIssueCountUnEstimate[item.dataIndex]} ${ES.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[2].color}"></div>已完成剩余时间：${ES.getChartDataYCompleted[item.dataIndex]}${ES.getChartDataYCompleted[item.dataIndex] ? ' 小时' : ''}</div>
              <div style="font-size: 11px"><div class="c7n-tooltip-icon" style="background:${params[3].color}"></div>总计剩余时间：${ES.getChartDataYAll[item.dataIndex]}${ES.getChartDataYAll[item.dataIndex] ? ' 小时' : ''}</div>
            </div>`;
            }
          });
          return content;
        },
      },
      legend: {
        orient: 'horizontal',
        x: 'center',
        y: 0,
        padding: [0, 50, 0, 0],
        itemWidth: 14,
        itemGap: 30,
        data: [
          ...[
            ES.beforeCurrentUnit === 'issue_count' ? {} : {
              name: `已完成${ES.getChartYAxisName}`,
              icon: `image://${finish}`,
            },
          ],
          ...[
            ES.beforeCurrentUnit === 'issue_count' ? {} : {
              name: `总计${ES.getChartYAxisName}`,
              icon: 'rectangle',
            },
          ],
          ...[
            {
              name: '总问题数',
              icon: 'line',
            },
          ],
          ...[
            ES.beforeCurrentUnit === 'issue_count' ? {} : {
              name: '未预估问题数',
              icon: 'line',
            },
          ],
          ...[
            ES.beforeCurrentUnit === 'issue_count' ? {
              name: '已完成问题数',
              icon: 'line',
            } : {},
          ],
        ],
      },
      grid: {
        y2: 50,
        top: '30',
        left: 0,
        right: '50',
        containLabel: true,
      },
      calculable: true,
      xAxis: {
        // name: '日期',
        type: 'category',
        boundaryGap: false,
        nameLocation: 'end',
        nameGap: -10,
        nameTextStyle: {
          color: '#000',
          // verticalAlign: 'bottom',
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
          interval: ES.getChartDataX.length >= 20 ? 4 : 0,
          margin: 13,
          textStyle: {
            color: 'rgba(0, 0, 0, 0.65)',
            fontSize: 12,
            fontStyle: 'normal',
          },
          formatter(value, index) {
            // return `${value.split('-')[2]}/${MONTH[value.split('-')[1] * 1]}月`;
            return value.slice(5);
          },
        },
        splitArea: {
          show: false,
          interval: 0,
          color: 'rgba(0, 0, 0, 0.16)',
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
        data: ES.getChartDataX,
      },
      dataZoom: [{
        startValue: ES.getChartDataX[0],
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
    };
    let option;
    if (ES.beforeCurrentUnit === 'issue_count') {
      option = {
        yAxis: [{
          name: '问题计数',
          nameTextStyle: {
            color: '#000',
          },
          type: 'value',
          minInterval: 1,
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
        }],
        series: [
          {
            name: '总问题数',
            type: 'line',
            step: true,
            // symbol: ES.getChartDataYIssueCountAll.length === 1 ? 'auto' : 'none',
            itemStyle: {
              color: 'rgba(48, 63, 159, 1)',
            },
            // yAxisIndex: ES.beforeCurrentUnit === 'issue_count' ? 0 : 1,
            data: ES.getChartDataYIssueCountAll,
          },
          {
            name: '已完成问题数',
            type: 'line',
            step: true,
            // symbol: ES.getChartDataYIssueCountCompleted.length === 1 ? 'auto' : 'none',
            itemStyle: {
              color: '#00bfa4',
            },
            // yAxisIndex: ES.beforeCurrentUnit === 'issue_count' ? 0 : 1,
            data: ES.getChartDataYIssueCountCompleted,
          },
          {
            name: '未预估问题数',
            type: 'line',
            step: true,
            // symbol: ES.getChartDataYIssueCountUnEstimate.length === 1 ? 'auto' : 'none',
            itemStyle: {
              color: '#ff9915',
            },
            // yAxisIndex: ES.beforeCurrentUnit === 'issue_count' ? 0 : 1,
            data: ES.getChartDataYIssueCountUnEstimate,
          },
        ],
      };
    } else {
      option = {
        yAxis: [{
          name: ES.getChartYAxisName,
          nameTextStyle: {
            color: '#000',
          },
          type: 'value',
          minInterval: 1,
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
            formatter(value, index) {
              if (value && ES.beforeCurrentUnit === 'remain_time') {
                return `${value}h`;
              }
              return value;
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
        {
          name: '问题计数',
          nameTextStyle: {
            color: '#000',
          },
          type: 'value',
          minInterval: 1,
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
            show: false,
          },
        }],
        series: [
          {
            name: '总问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: 'rgba(48, 63, 159, 1)',
            },
            yAxisIndex: 1,
            data: ES.getChartDataYIssueCountAll,
          },
          {
            name: '已完成问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#00bfa4',
            },
            yAxisIndex: 1,
            data: ES.getChartDataYIssueCountCompleted,
          },
          {
            name: '未预估问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#ff9915',
            },
            yAxisIndex: 1,
            data: ES.getChartDataYIssueCountUnEstimate,
          },
          {
            name: `已完成${ES.getChartYAxisName}`,
            type: 'line',
            step: true,
            yAxisIndex: 0,
            data: ES.getChartDataYCompleted,
            itemStyle: {
              color: '#4e90fe',
            },
            areaStyle: {
              color: 'rgba(77, 144, 254, 0.1)',
            },
          },
          {
            name: `总计${ES.getChartYAxisName}`,
            type: 'line',
            step: true,
            yAxisIndex: 0,
            data: ES.getChartDataYAll,
            itemStyle: {
              color: 'rgba(0, 0, 0, 0.16)',
            },
            areaStyle: {
              color: 'rgba(245, 245, 245, 0.5)',
            },
          },
        ],
      };
    }
    return Object.assign({}, commonOption, option);
  }

  getTableDta(type) {
    if (type === 'compoleted') {
      return ES.tableData.filter(v => v.completed === 1);
    }
    if (type === 'unFinish') {
      return ES.tableData.filter(v => v.completed === 0);
    }
    if (type === 'unFinishAndunEstimate') {
      if (ES.currentUnit === 'story_point') {
        return ES.tableData.filter(v => v.completed === 0
          && (v.storyPoints === null && v.issueTypeDTO && v.issueTypeDTO.typeCode === 'story'));
      } else {
        return ES.tableData.filter(v => v.completed === 0 && v.remainTime === null);
      }
    }
    return [];
  }

  refresh() {
    if (!ES.currentEpicId) {
      ES.loadEpicAndChartAndTableData();
    } else {
      ES.loadChartData();
      ES.loadTableData();
    }
  }

  handleChangeCurrentEpic(epicId) {
    ES.setCurrentEpic(epicId);
    ES.loadChartData();
    ES.loadTableData();
  }

  handleChangeCurrentUnit(unit) {
    ES.setCurrentUnit(unit);
    ES.loadChartData();
    // ES.loadTableData();
    // const instance = this.echarts_react.getEchartsInstance();
    // instance.dispose();
    // instance.init();
    // instance.setOption(this.getOption());
  }

  transformRemainTime(remainTime) {
    if (!remainTime) {
      return '0';
    }
    let time = remainTime * 1;
    const w = Math.floor(time / 40);
    time -= 40 * w;
    const d = Math.floor(time / 8);
    time -= 8 * d;
    if (time % 1 > 0) {
      time = time.toFixed(1);
    }
    return `${w ? `${w} 周 ` : ''}${d ? `${d} 天 ` : ''}${time ? `${time} 小时 ` : ''}`;
  }

  transformStoryPoints(storyPoints) {
    return storyPoints && storyPoints > 0
      ? `${storyPoints % 1 > 0 ? storyPoints.toFixed(1) : storyPoints} 点` : storyPoints;
  }

  renderTable(type) {
    const column = [
      ...[
        {
          width: '15%',
          title: '问题编号',
          dataIndex: 'issueNum',
          render: (issueNum, record) => (
            <span
              style={{
                color: '#3f51b5',
                cursor: 'pointer',
                display: 'block',
                minWidth: 85,
              }}
              role="none"
              onClick={() => {
                const { history } = this.props;
                const urlParams = AppState.currentMenuType;
                history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${record.issueId}&paramUrl=reporthost/EpicReport`);
              }}
            >
              {issueNum}
              {' '}
              {record.addIssue ? '*' : ''}

            </span>
          ),
        },
        {
          width: '25%',
          title: '概要',
          dataIndex: 'summary',
          render: summary => (
            <div style={{ width: '100%', overflow: 'hidden' }}>
              <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={summary}>
                <p style={{
                  overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0,
                }}
                >
                  {summary}
                </p>
              </Tooltip>
            </div>
          ),
        },
        {
          width: '15%',
          title: '问题类型',
          dataIndex: 'typeCode',
          render: (typeCode, record) => (
            <div>
              <TypeTag
                style={{ minWidth: 90 }}
                data={record.issueTypeDTO}
                showName
              />
            </div>
          ),
        },
        {
          width: '15%',
          title: '优先级',
          dataIndex: 'priorityId',
          render: (priorityId, record) => (
            <div>
              <PriorityTag
                style={{ minWidth: 55 }}
                priority={record.priorityDTO}
              />
            </div>
          ),
        },
        {
          width: '15%',
          title: '状态',
          dataIndex: 'statusCode',
          render: (statusCode, record) => (
            <div>
              <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${record.statusMapDTO.name}`}>
                <div>
                  <StatusTag
                    style={{ display: 'inline-block', minWidth: 55 }}
                    name={record.statusMapDTO.name}
                    color={STATUS[record.statusMapDTO.type]}
                  />
                </div>
              </Tooltip>
            </div>
          ),
        },
      ],
      ...[
        ES.beforeCurrentUnit === 'issue_count' ? {} : {
          width: '15%',
          title: ES.beforeCurrentUnit === 'story_point' ? '故事点(点)' : '剩余时间(小时)',
          dataIndex: 'storyPoints',
          render: (storyPoints, record) => (
            <div style={{ minWidth: 15 }}>
              {this.getLabel(record)}
            </div>
          ),
        },
      ],
    ];
    return (
      <Table
        pagination={this.getTableDta(type).length > 10}
        rowKey={record => record.issueId}
        dataSource={this.getTableDta(type)}
        filterBar={false}
        columns={column}
        scroll={{ x: true }}
        loading={ES.tableLoading}
      />
    );
  }

  render() {
    const { history } = this.props;
    const { linkFromParamUrl } = this.state;
    const urlParams = AppState.currentMenuType;
    return (
      <Page className="c7n-epicReport">
        <Header
          title="史诗报告图"
          backPath={`/agile/${linkFromParamUrl || 'reporthost'}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >
          <SwithChart
            history={history}
            current="epicReport"
          />
          <Button
            funcType="flat"
            onClick={this.refresh.bind(this)}
          >
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title="史诗报告图"
          description="随时了解一个史诗的完成进度。这有助于您跟踪未完成或未分配问题来管理团队的开发进度。"
          link="https://v0-16.choerodon.io/zh/docs/user-guide/report/agile-report/epicburndown/"
        >
          {
            !(!ES.epics.length && ES.epicFinishLoading) ? (
              <div>
                <div style={{ display: 'flex' }}>
                  <Select
                    style={{ width: 244 }}
                    label="史诗选择"
                    value={ES.currentEpicId}
                    onChange={this.handleChangeCurrentEpic.bind(this)}
                  >
                    {
                      ES.epics.map(epic => (
                        <Option key={epic.issueId} value={epic.issueId}>{epic.epicName}</Option>
                      ))
                    }
                  </Select>
                  <Select
                    style={{ width: 244, marginLeft: 24 }}
                    label="单位选择"
                    value={ES.currentUnit}
                    onChange={this.handleChangeCurrentUnit.bind(this)}
                  >
                    <Option key="story_point" value="story_point">故事点</Option>
                    <Option key="issue_count" value="issue_count">问题计数</Option>
                    <Option key="remain_time" value="remain_time">剩余时间</Option>
                  </Select>
                </div>
                <Spin spinning={ES.chartLoading}>
                  <div>
                    {
                      ES.chartData.length ? (
                        <div className="c7n-report">
                          <div className="c7n-chart">
                            {
                              ES.reload ? null : (
                                <ReactEcharts
                                  ref={(e) => { this.echarts_react = e; }}
                                  option={this.getOption()}
                                  style={{ height: 400 }}
                                />
                              )
                            }
                          </div>
                          <div className="c7n-toolbar">
                            <h2>汇总</h2>
                            <h4>问题汇总</h4>
                            <ul>
                              <li>
                                <span className="c7n-tip">合计：</span>
                                <span>
                                  {`${ES.getLatest.issueCount}${ES.getLatest.issueCount > 0 ? ' 个' : ''}`}
                                </span>
                              </li>
                              {
                                ES.beforeCurrentUnit === 'issue_count' ? (
                                  <li>
                                    <span className="c7n-tip">已完成：</span>
                                    <span>{`${ES.getLatest.issueCompletedCount}${ES.getLatest.issueCompletedCount > 0 ? ' 个' : ''}`}</span>
                                  </li>
                                ) : null
                              }
                              {
                                ES.beforeCurrentUnit === 'issue_count' ? null : (
                                  <li>
                                    <span className="c7n-tip">未预估：</span>
                                    <span>{`${ES.getLatest.unEstimateIssueCount}${ES.getLatest.unEstimateIssueCount > 0 ? ' 个' : ''}`}</span>
                                  </li>
                                )
                              }
                            </ul>
                            {
                              ES.beforeCurrentUnit !== 'issue_count' ? (
                                <div>
                                  <h4>
                                    {`${ES.getChartYAxisName}`}
                                    {'汇总'}
                                  </h4>
                                  <ul>
                                    <li>
                                      <span className="c7n-tip">合计：</span>
                                      <span>
                                        {ES.beforeCurrentUnit === 'story_point' ? this.transformStoryPoints(ES.getLatest.allStoryPoints) : this.transformRemainTime(ES.getLatest.allRemainTimes)}
                                      </span>
                                    </li>
                                    <li>
                                      <span className="c7n-tip">已完成：</span>
                                      <span>
                                        {ES.beforeCurrentUnit === 'story_point' ? this.transformStoryPoints(ES.getLatest.completedStoryPoints) : this.transformRemainTime(ES.getLatest.completedRemainTimes)}
                                      </span>
                                    </li>
                                  </ul>
                                </div>
                              ) : null
                            }
                            <p
                              style={{
                                color: '#3F51B5',
                                cursor: 'pointer',
                              }}
                              role="none"
                              onClick={() => {
                                history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramType=epic&paramId=${ES.currentEpicId}&paramName=${encodeURIComponent(`${ES.epics.find(x => x.issueId === ES.currentEpicId).epicName}下的问题`)}&paramUrl=reporthost/EpicReport`);
                              }}
                            >
                              {'在“问题管理”中查看'}
                              <Icon style={{ fontSize: 13 }} type="open_in_new" />
                            </p>
                          </div>
                        </div>
                      ) : (
                        <div style={{ padding: '30px 0 20px', textAlign: 'center' }}>
                          {ES.tableData.length ? '当前单位下问题均未预估，切换单位或从下方问题列表进行预估。' : '当前史诗下没有问题。'}
                        </div>
                      )
                    }
                  </div>
                </Spin>
                <Tabs>
                  <TabPane tab="已完成的问题" key="done">
                    {this.renderTable('compoleted')}
                  </TabPane>
                  <TabPane tab="未完成的问题" key="todo">
                    {this.renderTable('unFinish')}
                  </TabPane>
                  {
                    ES.beforeCurrentUnit === 'issue_count' ? null : (
                      <TabPane tab="未完成的未预估问题" key="undo">
                        {this.renderTable('unFinishAndunEstimate')}
                      </TabPane>
                    )
                  }
                </Tabs>
              </div>
            ) : (
              <EmptyBlock
                style={{ marginTop: 40 }}
                textWidth="auto"
                pic={pic}
                title="当前项目无可用史诗"
                des={(
                  <div>
                    <span>请在</span>
                    <span
                      style={{ color: '#3f51b5', margin: '0 5px', cursor: 'pointer' }}
                      role="none"
                      onClick={() => {
                        history.push(`/agile/backlog?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
                      }}
                    >
                      {'待办事项'}
                    </span>
                    <span>或</span>
                    <span
                      style={{ color: '#3f51b5', margin: '0 5px', cursor: 'pointer' }}
                      role="none"
                      onClick={() => {
                        history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
                      }}
                    >
                      {'问题管理'}
                    </span>
                    <span>中创建一个史诗</span>
                  </div>
                )}
              />
            )
          }
        </Content>
      </Page>
    );
  }
}

export default EpicReport;
