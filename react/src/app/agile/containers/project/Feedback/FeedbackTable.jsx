import React, { Component, useEffect, useState } from 'react';
import {
  Button, Modal, Select, Input, Form, Upload, Icon, Tabs, Table,
} from 'choerodon-ui';
import { inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Content, Header, Page } from '@choerodon/boot';
import { axios } from '@choerodon/boot';
import {
  FeedbackNum,
  FeedbackType,
  FeedbackSummary,
  FeedbackStatus,
  LastUpdateTime,
  Reporter,
  Assignee,
} from './FeedbackComponent/FeedbackTableComponent';
import FeedbackButton from './FeedbackComponent/FeedbackButton';
import './FeedbackTable.scss';

const { TabPane } = Tabs;

function FeedbackTable({
  history, AppState, handleTabChange, handleTableChange, openExport, data, loading, fetchUser, page, size, totalNum, type, 
}) {
  const column = [
    {
      title: '问题编号',
      dataIndex: 'feedbackNum',
      key: 'feedbackNum',
      className: 'feedbackId',
      sorterId: 'feedbackId',
      width: 100,
      sorter: true,
      filters: [],
      // fixed: true,
      render: text => <FeedbackNum feedbackNum={text} />,
    },
    {
      title: '问题类型',
      dataIndex: 'type',
      key: 'type',
      className: 'feedbackType',
      sorterId: 'feedbackTypeId',
      width: 100,
      sorter: true,
      // filters: IssueStore.getColumnFilter.get('typeId'),
      // filterMultiple: true,
      // fixed: true,
      render: text => <FeedbackType type={text} />,
    },
    {
      title: '概要',
      dataIndex: 'summary',
      className: 'summary',
      key: 'summary',
      width: 240,
      filters: [],
      // fixed: true,
      render: text => <FeedbackSummary text={text} />,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'statusId',
      className: 'status',
      sorterId: 'statusId',
      width: 100,
      sorter: true,
      // filters: IssueStore.getColumnFilter.get('statusId'),
      // filterMultiple: true,
      render: text => <FeedbackStatus status={text} />,
    },
    {
      title: '经办人',
      dataIndex: 'assignee',
      className: 'assignee',
      width: 135,
      key: 'assignee',
      sorterId: 'assigneeId',
      sorter: true,
      // filteredValue: assigneeFilterValue,
      render: (text, record) => (
        <Assignee user={text} />
      ),
    },
    {
      title: '最后更新时间',
      dataIndex: 'lastUpdateDate',
      className: 'lastUpdateDate',
      key: 'lastUpdateDate',
      sorterId: 'lastUpdateDate',
      width: 134,
      sorter: true,
      render: text => <LastUpdateTime date={text} />,
    },
    {
      title: '报告人',
      dataIndex: 'reporter',
      key: 'reporter',
      className: 'reporter',
      width: 135,
      sorterId: 'reporterId',
      sorter: true,
      // filteredValue: assigneeFilterValue,
      render: text => <Reporter reporter={text} />,
    },
  ];

  return (
    <Page
      className="c7n-Issue"
      service={['agile-service.issue.deleteIssue', 'agile-service.issue.listIssueWithSub']}
    >
      <Header
        title="反馈列表"
      >
        {/* <Button className="leftBtn" funcType="flat" onClick={openExport}>
          <Icon type="get_app icon" />
          <span>导出</span>
        </Button> */}
        <Button
          funcType="flat"
          onClick={() => fetchUser(type)}
        >
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
      </Header>
      <Content>
        <Tabs className="c7n-agile-feedback-table" onChange={handleTabChange}>
          <TabPane tab="全部" key="all" />
          <TabPane tab="建议与意见" key="recommendation_and_opinion" />
          <TabPane tab="问题咨询" key="question_consultation" />
          <TabPane tab="报告缺陷" key="bug_report" />
        </Tabs>
        <Table
          dataSource={data}
          columns={column}
          loading={loading}
          onRowClick={(issue) => {
            const urlParams = AppState.currentMenuType;
            history.push(`feedback/content?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&feedbackId=${issue.id}`);
          }}
          pagination={{
            current: page,
            pageSize: size,
            total: totalNum,
          }}
          onChange={handleTableChange}
        />
      </Content>
    </Page>
  );
}

export default inject('AppState')(withRouter(FeedbackButton(FeedbackTable)));
