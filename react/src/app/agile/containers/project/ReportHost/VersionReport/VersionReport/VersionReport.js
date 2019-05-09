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
// import pic from './no_version.svg';
import pic from '../../../../../assets/image/emptyChart.svg';
import finish from './legend/finish.svg';
import total from './legend/total.svg';
import noEstimated from './legend/noEstimated.svg';
import SwithChart from '../../Component/switchChart';
import StatusTag from '../../../../../components/StatusTag';
import PriorityTag from '../../../../../components/PriorityTag';
import TypeTag from '../../../../../components/TypeTag';
import VS from '../../../../../stores/project/versionReportNew';
import EmptyBlock from '../../../../../components/EmptyBlock';
import './VersionReport.scss';
import { STATUS } from '../../../../../common/Constant';

const { TabPane } = Tabs;
const { AppState } = stores;
const { Option } = Select;
const MONTH = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];
let backUrl;

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

    VS.loadEpicAndChartAndTableData();
  }

  getLabel(record) {
    if (VS.beforeCurrentUnit === 'story_point') {
      if (record.issueTypeDTO.typeCode === 'story') {
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
            if (VS.beforeCurrentUnit === 'issue_count') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>总问题数：${VS.getChartDataYIssueCountAll[item.dataIndex]} ${VS.getChartDataYIssueCountAll[item.dataIndex] ? ' 个' : ''}</div>
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>已完成问题数：${VS.getChartDataYIssueCountCompleted[item.dataIndex]} ${VS.getChartDataYIssueCountCompleted[item.dataIndex] ? ' 个' : ''}</div>
            </div>`;
            }
  
            if (VS.beforeCurrentUnit === 'story_point') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>未预估问题百分比：${VS.getChartDataYIssueCountUnEstimate[item.dataIndex]}</div>
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>已完成故事点：${VS.getChartDataYCompleted[item.dataIndex]}${VS.getChartDataYCompleted[item.dataIndex] ? ' 点' : ''}</div>
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[2].color}"></div>总计故事点：${VS.getChartDataYAll[item.dataIndex]}${VS.getChartDataYAll[item.dataIndex] ? ' 点' : ''}</div>
            </div>`;
            }
  
            if (VS.beforeCurrentUnit === 'remain_time') {
              content = `<div>
              <span>${params[0].axisValue}</span>
              <br />
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[0].color}"></div>未预估问题百分比：${VS.getChartDataYIssueCountUnEstimate[item.dataIndex]}</div>
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[1].color}"></div>已完成剩余时间：${VS.getChartDataYCompleted[item.dataIndex]}${VS.getChartDataYCompleted[item.dataIndex] ? ' 小时' : ''}</div>
              <div style="font-size: 11px"><div style="display:inline-block; width: 10px; height: 10px; margin-right: 3px; border-radius: 50%; background:${params[2].color}"></div>总计剩余时间：${VS.getChartDataYAll[item.dataIndex]}${VS.getChartDataYAll[item.dataIndex] ? ' 小时' : ''}</div>
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
        data: [
          ...[
            VS.beforeCurrentUnit === 'issue_count' ? {} : {
              name: `总计${VS.getChartYAxisName}`,
              icon: `image://${total}`,
            },
          ],
          ...[
            VS.beforeCurrentUnit === 'issue_count' ? {} : {
              name: `已完成${VS.getChartYAxisName}`,
              icon: `image://${finish}`,
            },
          ],
          ...[
            VS.beforeCurrentUnit === 'issue_count' ? {
              name: '总问题数',
              icon: `image://${total}`,
            } : {},
          ],
          ...[
            VS.beforeCurrentUnit === 'issue_count' ? {} : {
              name: '未预估问题百分比',
              icon: `image://${noEstimated}`,
            },
          ],
          ...[
            VS.beforeCurrentUnit === 'issue_count' ? {
              name: '已完成问题数',
              icon: `image://${finish}`,
            } : {},
          ],
        ],
      },
      grid: {
        y2: 50,
        top: '30',
        left: 0,
        right: '20',
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
          interval: VS.getChartDataX.length >= 20 ? 4 : 0,
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
            width: 2,
            type: 'solid',
          },
        },
        data: VS.getChartDataX,
      },
      dataZoom: [{
        startValue: VS.getChartDataX[0],
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
    if (VS.beforeCurrentUnit === 'issue_count') {
      option = {
        yAxis: [
          {
            name: '问题数',
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
                width: 2,
              },
            },
          },
        ],
        series: [
          {
            name: '总问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#78aafe',
            },
            areaStyle: {
              color: 'rgba(77, 144, 254, 0.1)',
            },
            data: VS.getChartDataYIssueCountAll,
          },
          {
            name: '已完成问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#00bfa4',
            },
            areaStyle: {
              color: 'rgba(0, 191, 165, 0.1)',
            },
            data: VS.getChartDataYIssueCountCompleted,
          },
          {
            name: '未预估问题百分比',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#f44336',
            },
            areaStyle: {
              color: 'rgba(244, 67, 54, 0.1)',
            },
            data: VS.getChartDataYIssueCountUnEstimate,
          },
        ],
      };
    } else {
      option = {
        yAxis: [
          {
            name: VS.getChartYAxisName,
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
                if (value && VS.beforeCurrentUnit === 'remain_time') {
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
                width: 2,
              },
            },
          },
          {
            name: '百分比',
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
              // formatter(value, index) {
              //   if (value && VS.beforeCurrentUnit !== 'issue_count') {
              //     return `${value}%`;
              //   }
              //   return value;
              // },
            },
            splitLine: {
              show: false,
            },
          },
        ],
        series: [
          {
            name: '总问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#78aafe',
            },
            areaStyle: {
              color: 'rgba(77, 144, 254, 0.1)',
            },
            yAxisIndex: 1,
            data: VS.getChartDataYIssueCountAll,
          },
          {
            name: '已完成问题数',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#00bfa4',
            },
            areaStyle: {
              color: 'rgba(0, 191, 165, 0.1)',
            },
            yAxisIndex: 1,
            data: VS.getChartDataYIssueCountCompleted,
          },
          {
            name: '未预估问题百分比',
            type: 'line',
            step: true,
            itemStyle: {
              color: '#f44336',
            },
            areaStyle: {
              color: 'rgba(244, 67, 54, 0.1)',
            },
            yAxisIndex: 1,
            data: VS.getChartDataYIssueCountUnEstimate,
          },
          {
            name: `已完成${VS.getChartYAxisName}`,
            type: 'line',
            step: true,
            yAxisIndex: 0,
            data: VS.getChartDataYCompleted,
            itemStyle: {
              color: '#00bfa4',
            },
            areaStyle: {
              color: 'rgba(0, 191, 165, 0.1)',
            },
          },
          {
            name: `总计${VS.getChartYAxisName}`,
            type: 'line',
            step: true,
            yAxisIndex: 0,
            data: VS.getChartDataYAll,
            itemStyle: {
              color: '#78aafe',
            },
            areaStyle: {
              color: 'rgba(77, 144, 254, 0.1)',
            },
          },
        ],
      };
    }
    return Object.assign({}, commonOption, option);
  }

  getTableDta(type) {
    if (type === 'compoleted') {
      return VS.tableData.filter(v => v.completed === 1);
    }
    if (type === 'unFinish') {
      return VS.tableData.filter(v => v.completed === 0);
    }
    if (type === 'unFinishAndunEstimate') {
      return VS.tableData.filter(v => v.completed === 0
        && ((v.storyPoints === null && v.issueTypeDTO && v.issueTypeDTO.typeCode === 'story')
          || (v.remainTime === null && v.issueTypeDTO && v.issueTypeDTO.typeCode !== 'story')));
    }
    return [];
  }

  GetRequest(url) {
    const theRequest = {};
    if (url.indexOf('?') !== -1) {
      const str = url.split('?')[1];
      const strs = str.split('&');
      for (let i = 0; i < strs.length; i += 1) {
        theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
      }
    }
    return theRequest;
  }

  refresh() {
    if (!VS.currentVersionId) {
      VS.loadEpicAndChartAndTableData();
    } else {
      VS.loadChartData();
      VS.loadTableData();
    }
  }

  handleChangeCurrentVersion(versionId) {
    VS.setCurrentVersion(versionId);
    VS.loadChartData();
    VS.loadTableData();
  }

  handleChangeCurrentUnit(unit) {
    VS.setCurrentUnit(unit);
    VS.loadChartData();
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
    return `${w ? `${w}周 ` : ''}${d ? `${d}天 ` : ''}${time ? `${time}小时 ` : ''}`;
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
                history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${record.issueId}&paramUrl=reporthost/versionReport`);
              }}
            >
              {issueNum}
              {' '}
              {record.addIssue ? '*' : ''}
            </span>
          ),
        },
        {
          width: '30%',
          title: '概要',
          dataIndex: 'summary',
          render: summary => (
            <div style={{ width: '100%', overflow: 'hidden' }}>
              <Tooltip placement="topLeft" mouseEnterDelay={0.5} title={`问题概要：${summary}`}>
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
          width: '10%',
          title: '状态',
          dataIndex: 'statusCode',
          render: (statusCode, record) => (
            <div>
              <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${record.statusMapDTO.name}`}>
                <div>
                  <StatusTag
                    style={{ display: 'inline-block' }}
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
        VS.beforeCurrentUnit === 'issue_count' ? {} : {
          width: '15%',
          title: VS.beforeCurrentUnit === 'story_point' ? '故事点(点)' : '剩余时间(小时)',
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
        loading={VS.tableLoading}
      />
    );
  }

  render() {
    const { history } = this.props;
    const { linkFromParamUrl } = this.state;
    const urlParams = AppState.currentMenuType;
    return (
      <Page className="c7n-versionReport">
        <Header
          title="版本报告图"
          backPath={`/agile/${linkFromParamUrl || 'reporthost'}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >
          <SwithChart
            history={history}
            current="versionReport"
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
          title="版本报告图"
          description="跟踪对应的版本发布日期。这样有助于您监控此版本是否按时发布，以便工作滞后时能采取行动。"
          link="https://v0-16.choerodon.io/zh/docs/user-guide/report/agile-report/version-report/"
        >
          {
            !(!VS.versions.length && VS.versionFinishLoading) ? (
              <div>
                <div style={{ display: 'flex' }}>
                  <Select
                    style={{ width: 244 }}
                    label="版本"
                    value={VS.currentVersionId}
                    onChange={this.handleChangeCurrentVersion.bind(this)}
                  >
                    {
                      VS.versions.map(version => (
                        <Option
                          key={version.versionId}
                          value={version.versionId}
                        >
                          {version.name}
                        </Option>
                      ))
                    }
                  </Select>
                  <Select
                    style={{ width: 244, marginLeft: 24 }}
                    label="单位"
                    value={VS.currentUnit}
                    onChange={this.handleChangeCurrentUnit.bind(this)}
                  >
                    <Option key="story_point" value="story_point">故事点</Option>
                    <Option key="issue_count" value="issue_count">问题计数</Option>
                    <Option key="remain_time" value="remain_time">剩余时间</Option>
                  </Select>
                </div>
                <div style={{ marginTop: 10, display: 'flex', justifyContent: 'space-between' }}>
                  <p style={{ marginBottom: 0 }}>{VS.getCurrentVersion.versionId && VS.getCurrentVersion.statusCode === 'released' ? `发布于 ${VS.getCurrentVersion.releaseDate ? VS.getCurrentVersion.releaseDate.split(' ')[0] : '未指定发布日期'}` : '未发布'}</p>
                  <p
                    style={{
                      color: '#3F51B5',
                      cursor: 'pointer',
                      display: 'flex',
                      alignItems: 'center',
                      marginBottom: 0,
                    }}
                    role="none"
                    onClick={() => {
                      // history.push(encodeURI(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${urlParams.name}&organizationId=${urlParams.organizationId}&paramType=version&paramId=${VS.currentVersionId}&paramName=${VS.getCurrentVersion.name}下的问题&paramUrl=reporthost/VersionReport`));
                      history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramType=version&paramId=${VS.currentVersionId}&paramName=${encodeURIComponent(`${VS.getCurrentVersion.name}下的问题`)}&paramUrl=reporthost/VersionReport`);
                    }}
                  >
                    {'在“问题管理中”查看'}
                    <Icon style={{ fontSize: 13 }} type="open_in_new" />
                  </p>
                </div>
                <Spin spinning={VS.chartLoading}>
                  <div className="c7n-report">
                    {
                      VS.chartData.length ? (
                        <div className="c7n-chart">
                          {
                            VS.reload ? null : (
                              <ReactEcharts option={this.getOption()} style={{ height: 400 }} />
                            )
                          }
                        </div>
                      ) : (
                        <div style={{ padding: '20px 0', textAlign: 'center', width: '100%' }}>
                          {VS.tableData.length ? '当前单位下问题均未预估，切换单位或从下方问题列表进行预估。' : '当前版本下没有问题。'}
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
                    VS.beforeCurrentUnit === 'issue_count' ? null : (
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
                title="当前项目无可用版本"
                des={(
                  <div>
                    <span>请在</span>
                    <span
                      style={{ color: '#3f51b5', margin: '0 5px', cursor: 'pointer' }}
                      role="none"
                      onClick={() => {
                        history.push(`/agile/release?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
                      }}
                    >
                      {'发布版本'}
                    </span>
                    <span>中创建一个版本</span>
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
