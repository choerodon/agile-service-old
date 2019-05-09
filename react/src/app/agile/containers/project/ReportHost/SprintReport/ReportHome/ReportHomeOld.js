import React, { Component } from 'react';
import { observer } from 'mobx-react';
import ReactEcharts from 'echarts-for-react';
import {
 Page, Header, Content, stores 
} from 'choerodon-front-boot';
import {
 Button, Tabs, Table, Select, Icon, Tooltip, Dropdown, Menu 
} from 'choerodon-ui';
import ReportStore from '../../../../../stores/project/Report';
import './ReleaseDetail.scss';
import StatusTag from '../../../../../components/StatusTag';
import PriorityTag from '../../../../../components/PriorityTag';
import TypeTag from '../../../../../components/TypeTag';
import { formatDate } from '../../../../../common/utils';
import NoDataComponent from '../../Component/noData';
import epicSvg from '../../Home/style/pics/no_sprint.svg';
import SwithChart from '../../Component/switchChart';

const TabPane = Tabs.TabPane;
const Option = Select.Option;
const { AppState } = stores;

@observer
class ReleaseDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentDidMount() {
    ReportStore.init();
  }

  getOption() {
    return {
      tooltip: {
        trigger: 'axis',
      },
      grid: {
        left: '3%',
        right: '3%',
        top: 10,
        bottom: 20,
        containLabel: true,
        show: true,
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ReportStore.chartData.xAxis.slice(),
        axisLabel: {
          formatter(value, index) {
            return `${value.split(' ')[0]}\n${value.split(' ')[1]}`;
          },
        },
      },
      yAxis: {
        type: 'value',
        minInterval: 1,
      },
      series: [
        {
          name: '期望值',
          type: 'line',
          data: [
            [0, ReportStore.chartData.yAxis[0]],
            [ReportStore.chartData.yAxis.length - 1, 0],
          ],
          itemStyle: {
            color: 'grey',
          },
          lineStyle: {
            type: 'dashed',
            color: 'grey',
          },
        },
        {
          name: '剩余值',
          type: 'line',
          itemStyle: {
            color: 'red',
          },
          data: ReportStore.chartData.yAxis,
        },
      ],
    };
  }

  callback(key) {
    ReportStore.setActiveKey(key);
    const ARRAY = {
      done: 'loadDoneIssues',
      todo: 'loadTodoIssues',
      remove: 'loadRemoveIssues',
    };
    if (!ReportStore[key] && ReportStore.currentSprint.sprintId) {
      ReportStore[ARRAY[key]]();
    }
  }
  
  renderDoneIssue(column) {
    return (
      <div>
        <Table
          rowKey={record => record.issueId}
          dataSource={ReportStore.doneIssues}
          columns={column}
          filterBar={false}
          pagination={false}
          scroll={{ x: true }}
          loading={ReportStore.loading}
        />
      </div>
    );
  }

  renderTodoIssue(column) {
    return (
      <div>
        <Table
          rowKey={record => record.issueId}
          dataSource={ReportStore.todoIssues}
          columns={column}
          filterBar={false}
          pagination={false}
          scroll={{ x: true }}
          loading={ReportStore.loading}
        />
      </div>
    );
  }

  renderRemoveIssue(column) {
    return (
      <div>
        <Table
          rowKey={record => record.issueId}
          dataSource={ReportStore.removeIssues}
          columns={column}
          filterBar={false}
          pagination={false}
          scroll={{ x: true }}
          loading={ReportStore.loading}
        />
      </div>
    );
  }

  render() {
    const column = [
      {
        width: '15%',
        title: '编码',
        dataIndex: 'issueNum',
        render: (issueNum, record) => (
          <span
            style={{ 
              color: '#3f51b5',
              cursor: 'pointer',
            }}
            role="none"
            onClick={() => {
              const { history } = this.props;
              const urlParams = AppState.currentMenuType;
              history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${record.issueId}&paramUrl=reporthost/sprintreport`);
            }}
          >
{issueNum} 
{' '}
{record.addIssue ? '*' : ''}

          </span>
        ),
      }, {
        width: '30%',
        title: '概要',
        dataIndex: 'summary',
      }, {
        width: '15%',
        title: '问题类型',
        dataIndex: 'typeCode',
        render: (typeCode, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`任务类型： ${record.typeCode}`}>
              <div>
                <TypeTag
                  data={record.issueTypeDTO}
                  showName
                />
              </div>
            </Tooltip>
          </div>
        ),
      }, {
        width: '15%',
        title: '优先级',
        dataIndex: 'priorityCode',
        render: (priorityCode, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`优先级： ${record.priorityDTO.name}`}>
              <div style={{ marginRight: 12 }}>
                <PriorityTag
                  priority={record.priorityDTO}
                />
              </div>
            </Tooltip>
          </div>
        ),
      }, {
        width: '15%',
        title: '状态',
        dataIndex: 'statusCode',
        render: (statusCode, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${record.statusName}`}>
              <div>
                <StatusTag
                  style={{ display: 'inline-block' }}
                  name={record.statusName}
                  color={record.statusColor}
                />
              </div>
            </Tooltip>
          </div>
        ),
      }, {
        width: '10%',
        title: '故事点',
        dataIndex: 'storyPoints',
        render: (storyPoints, record) => (
          <div>
            {record.typeCode === 'story' ? storyPoints || '0' : ''}
          </div>
        ),
      },
    ];
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    
    return (
      <Page className="c7n-report">
        <Header 
          title="冲刺报告"
          backPath={`/agile/reporthost?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}
        >
          <SwithChart
            history={this.props.history}
            current="sprint"
          />
          <Button 
            funcType="flat" 
            onClick={() => ReportStore.changeCurrentSprint(ReportStore.currentSprint.sprintId)}
          >
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title={ReportStore.currentSprint.sprintName ? `迭代冲刺“${ReportStore.currentSprint.sprintName}”的冲刺报告` : '无冲刺报告'}
          description="了解每个冲刺中完成、进行和退回待办的工作。这有助于您确定您团队的工作量是否超额，更直观的查看冲刺的范围与工作量。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/report/sprint/"
        >
          {ReportStore.sprints.length ? (
<React.Fragment>
            <Select
              value={ReportStore.currentSprint.sprintId}
              onChange={(value) => {
                ReportStore.changeCurrentSprint(value);
              }}
              style={{ width: 520 }}
              label="迭代冲刺"
              loading={this.state.selectLoading}
              filter
              filterOption={(input, option) =>
                option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {ReportStore.sprints.map(sprint =>
                (<Option key={sprint.sprintId} value={sprint.sprintId}>{sprint.sprintName}</Option>),
              )}
            </Select>
            <div className="c7n-sprintMessage">
              <div className="c7n-sprintContent">
                <span>
                  {ReportStore.getCurrentSprintStatus.status}冲刺,
                  共 {ReportStore.currentSprint.issueCount || 0} 个问题
                </span>
                <span>
                  {`${formatDate(ReportStore.currentSprint.startDate)} - ${formatDate(ReportStore.currentSprint.actualEndDate) || '至今'}`}
                </span>
              </div>
              <p
                style={{
                  color: '#3F51B5',
                  cursor: 'pointer',
                }}
                role="none"
                onClick={() => {
                  this.props.history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramType=sprint&paramId=${ReportStore.currentSprint.sprintId}&paramName=${encodeURIComponent(`${ReportStore.currentSprint.sprintName}下的问题`)}&paramUrl=reporthost/sprintreport`);
                }}
              >
                在“问题管理中”查看
                <Icon style={{ fontSize: 13 }} type="open_in_new" />
              </p>
            </div>
            <ReactEcharts className="c7n-chart" option={this.getOption()} />

            <Tabs activeKey={ReportStore.activeKey} onChange={this.callback}>
              <TabPane tab="已完成的问题" key="done">
                {this.renderDoneIssue(column)}
              </TabPane>
              <TabPane tab="未完成的问题" key="todo">
                {this.renderTodoIssue(column)}
              </TabPane>
              <TabPane tab="从Sprint中删除的问题" key="remove">
                {this.renderRemoveIssue(column)}
              </TabPane>
            </Tabs>
          </React.Fragment>
) : <NoDataComponent title="冲刺" links={[{ name: '待办事项', link: '/agile/backlog' }]} img={epicSvg} /> }

        </Content>
      </Page>
    );
  }
}

export default ReleaseDetail;
