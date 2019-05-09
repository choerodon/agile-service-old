import React, { Component } from 'react';
import { stores } from 'choerodon-front-boot';
import {
  Table, Tabs, Spin, Tooltip, Pagination,
} from 'choerodon-ui';
import _ from 'lodash';
import { withRouter } from 'react-router-dom';
import TypeTag from '../../../../../components/TypeTag';
import PriorityTag from '../../../../../components/PriorityTag';
import StatusTag from '../../../../../components/StatusTag';
import { loadSprintIssues } from '../../../../../api/NewIssueApi';
import './SprintDetails.scss';

const { TabPane } = Tabs;
const { AppState } = stores;
class SprintDetails extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      pagination: undefined,
      sprintId: undefined,
      doneIssues: [],
      undoIssues: [],
      undoAndNotEstimatedIssues: [],
      activeKey: 'done',
      done: false,
      undo: false,
      undoAndNotEstimated: false, // 用来判断是否已经加载过数据，如果为true，说明已经load，不再重新load
    };
  }
  
  componentWillReceiveProps(nextProps) {
    if (nextProps.sprintId !== this.props.sprintId) {
      const { sprintId } = nextProps;
      this.setState({
        sprintId,
        done: false,
        undo: false,
        undoAndNotEstimated: false,
      });
      this.loadDoneIssues(sprintId);
    }
  }

  getPagination = (pagination, res) => { // 注意：pagination
    if (pagination == undefined) {
      return res.content.length > 10 ? { current: 1, pageSize: 10 } : false;
    } else {
      return { current: pagination.page + 1, pageSize: pagination.size };
    }
  }

    handleTableChange = (pagination) => {
      const { activeKey, sprintId } = this.state;
      const ARRAY = {
        done: 'loadDoneIssues',
        undo: 'loadUndoIssues',
        undoAndNotEstimated: 'loadUndoAndNotEstimatedIssues',
      };
      this.setState({
        pagination,
      });
      // this[ARRAY[activeKey]](sprintId, {
      //   page: pagination.current - 1,
      //   size: pagination.pageSize,
      // });
    }

    handleTabChange = (key) => {
      const { sprintId } = this.state;
      this.setState({
        activeKey: key,
        // pagination: { current: 1, pageSize: 10 }, // 注意每次tab切换要重设pagination,否则current可能是上一个tab的值
      });
      const ARRAY = {
        done: 'loadDoneIssues',
        undo: 'loadUndoIssues',
        undoAndNotEstimated: 'loadUndoAndNotEstimatedIssues',
      };
      if (!this.state[key]) {
        this[ARRAY[key]](sprintId);
      } else {
        this.setState({
          pagination: this.state[_.lowerFirst(_.trim(ARRAY[key], 'load'))].length > 10 ? { current: 1, pageSize: 10 } : false,
        });
      }
    }

    loadDoneIssues(sprintId, pagination) {
      this.setState({
        loading: true,
        done: true,
      });
      loadSprintIssues(sprintId, 'done')
        .then((res) => {
          this.setState({
            doneIssues: res.content,
            loading: false,
            pagination: this.getPagination(pagination, res),
          });
        })
        .catch((e) => {
          this.setState({
            loading: false,
          });
          Choerodon.handleResponseError(e);
        });
    }
  

    loadUndoIssues(sprintId, pagination) {
      this.setState({
        loading: true,
        undo: true,
      });
      loadSprintIssues(sprintId, 'unfinished')
        .then((res) => {
          this.setState({
            undoIssues: res.content,
            loading: false,
            pagination: this.getPagination(pagination, res),
          });
        })
        .catch((e) => {
          this.setState({
            loading: false,
          });
          Choerodon.handleResponseError(e);
        });
    }

    loadUndoAndNotEstimatedIssues(sprintId, pagination) {
      this.setState({
        loading: true,
        undoAndNotEstimated: true,
      });
      loadSprintIssues(sprintId, 'unfinished')
        .then((res) => {
          this.setState({
            undoAndNotEstimatedIssues: res.content.filter(item => (item.storyPoints === 0 && item.typeCode === 'story') || (item.remainTime === null && item.typeCode === 'task')),
            loading: false,
            pagination: this.getPagination(pagination, res),
          });
        })
        .catch((e) => {
          this.setState({
            loading: false,
          });
          Choerodon.handleResponseError(e);
        });
    }

    renderDoneIssues(column) {
      const { loading, doneIssues, pagination } = this.state;
      return (
        <div>
          <Table
            rowKey={record => record.issueId}
            dataSource={doneIssues}
            columns={column}
            filterBar={false}
            pagination={doneIssues.length > 10 ? pagination : false}
            scroll={{ x: false }}
            loading={loading}
            onChange={this.handleTableChange}
          />
        </div>
      );
    }

    renderUndoIssues(column) {
      const { loading, undoIssues, pagination } = this.state;
      return (
        <div>
          <Table
            rowKey={record => record.issueId}
            dataSource={undoIssues}
            columns={column}
            filterBar={false}
            pagination={undoIssues.length > 10 ? pagination : false}
            scroll={{ x: false }}
            loading={loading}
            onChange={this.handleTableChange}
          />
        </div>
      );
    }

    renderUndoAndNotEstimatedIssues(column) {
      const { loading, undoAndNotEstimatedIssues, pagination } = this.state;
      return (
        <div>
          <Table
            rowKey={record => record.issueId}
            dataSource={undoAndNotEstimatedIssues}
            columns={column}
            filterBar={false}
            pagination={undoAndNotEstimatedIssues.length > 10 ? pagination : false}
            scroll={{ x: false }}
            loading={loading}
            onChange={this.handleTableChange}
          />
        </div>
      );
    }
    

    render() {
      const { activeKey, pagination, sprintId } = this.state;
      const column = [{
        title: '问题编号',
        dataIndex: 'issueNum',
        key: 'keyword',
        width: 70,
        render: (issueNum, record) => (
          <Tooltip mouseEnterDelay={0.5} title={`问题编号：${issueNum}`}>
            <span
              style={{
                display: 'block',
                minWidth: 70,
                color: '#3f51b5',
                cursor: 'pointer',
              }}
              role="none"
              onClick={() => {
                const { history } = this.props;
                const urlParams = AppState.currentMenuType;
                history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${record.issueId}&paramUrl=iterationBoard/${sprintId}`);
              }}
            >
              {/* <span> */}
              {issueNum} 
              {' '}
              {record.addIssue ? '*' : ''}
              {/* </span> */}
             
            </span>
          </Tooltip>
        ),
      }, {
        title: '概要',
        dataIndex: 'summary',
        key: 'summary',
        width: 160,
        render: summary => (
          <Tooltip title={`概要：${summary}`}>
            <div
              role="none"
              style={{ 
                maxWidth: '250px', minWidth: '50px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
              }}
            >
              {summary}
            </div>
          </Tooltip>
        ),
      }, {
        title: '问题类型',
        dataIndex: 'typeCode',
        key: 'typeCode',
        width: 100,
        render: (typeCode, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`任务类型： ${record.issueTypeDTO.name}`}>
              <div>
                <TypeTag
                  style={{ minWidth: 100 }}
                  data={record.issueTypeDTO}
                  showName
                />
              </div>
            </Tooltip>
          </div>
        ),
      }, {
        title: '优先级',
        dataIndex: 'priority',
        key: 'priority',
        width: 40,
        render: (text, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`优先级： ${record.priorityDTO.name}`}>
              <div style={{ marginRight: 12 }}>
                <PriorityTag
                  style={{ minWidth: 40 }}
                  priority={record.priorityDTO}
                />
              </div>
            </Tooltip>
          </div>
        ),
      }, {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 40,
        render: (text, record) => (
          <div>
            <Tooltip mouseEnterDelay={0.5} title={`任务状态： ${record.statusMapDTO.name}`}>
              <div>
                <StatusTag
                  style={{ minWidth: 40 }}
                  data={record.statusMapDTO}
                />
              </div>
            </Tooltip>
          </div>
        ), 
      }, {
        title: '剩余时间(小时)',
        dataIndex: 'remainingTime',
        key: 'remainingTime',
        width: 90,
        render: (remainingTime, record) => (
          <span style={{ display: 'inline-block', minWidth: 15 }}>{`${remainingTime === null ? '' : (`${remainingTime}`)}`}</span>
        ),
      }, {
        title: '故事点(点)',
        dataIndex: 'storyPoints',
        key: 'storyPoints',
        width: 70,
        render: (storyPoints, record) => (
          <div style={{ minWidth: 15 }}>
            {record.typeCode === 'story' ? storyPoints || '0' : ''}
          </div>
        ),
      }];
      return (
        <div className="c7n-SprintDetails">
          <div className="c7n-SprintDetails-tabs">
            <Tabs activeKey={activeKey} onChange={this.handleTabChange}>
              <TabPane tab="已完成的问题" key="done">
                {this.renderDoneIssues(column)}
              </TabPane>
              <TabPane tab="未完成的问题" key="undo">
                {this.renderUndoIssues(column)}
              </TabPane>
              <TabPane tab="未完成的未预估问题" key="undoAndNotEstimated">
                {this.renderUndoAndNotEstimatedIssues(column)}
              </TabPane>
            </Tabs>
          </div>
        </div>
      );
    }
}

export default withRouter(SprintDetails);
