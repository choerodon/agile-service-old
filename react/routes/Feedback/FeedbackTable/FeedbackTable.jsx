import React, {
  Component, useEffect, useState, useContext, useImperativeHandle, useRef, 
} from 'react';
import {
  Button, Modal, Select, Input, Form, Upload, Icon, Tabs, 
} from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Content, Header, Page } from '@choerodon/boot';
import { axios } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import FeedbackButton from '../FeedbackButton';
import './FeedbackTable.less';
import FeedbackTableContext from './stores';
import {
  FeedbackNum,
  FeedbackType,
  FeedbackSummary,
  FeedbackStatus,
  LastUpdateTime,
  Reporter,
  Assignee,
} from './components/FeedbackTableComponent';

const { TabPane } = Tabs;
const { Column } = Table;

const FeedbackTable = observer(({ feedbackTableRef, history }) => {
  const tableRef = useRef();

  const {
    feedbackTableDataSet, recommendationDs, questionDs, bugReportDs, prefixCls, AppState,
  } = useContext(FeedbackTableContext);

  const Ds = {
    all: feedbackTableDataSet,
    recommendation_and_opinion: recommendationDs,
    question_consultation: questionDs,
    bug_report: bugReportDs,
  };

  const [type, setType] = useState('all');

  function refresh(feedbackType) {
    if (feedbackType) {
      Ds[feedbackType].query();
      Ds.all.query();
    } else {
      Ds[type].query();
    }
  }

  const handleTabChange = (key) => {
    if (key === 'all') {
      setType('all');
    } else {
      setType(key);
    }
  };
  

  const renderTable = (feedbackType, dataSet) => {
    const handleRowClick = (record) => {
      const urlParams = AppState.currentMenuType;
      history.push(`feedback/content?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&feedbackId=${record.get('id')}`);
    };
    const handleRow = ({ record }) => {
      const rowProps = {
        onClick: handleRowClick.bind(this, record),
      };
      return rowProps;
    };

    if (feedbackType !== 'all') {
      dataSet.setQueryParameter('typeList', [feedbackType]);
    }
    return (
      <Table 
        dataSet={dataSet}
        onRow={handleRow}
      >
        <Column
          name="feedbackNum"
          renderer={({ record }) => (<FeedbackNum feedbackNum={record.get('feedbackNum')} />)}
        />
        <Column 
          name="type"
          renderer={({ record }) => (<FeedbackType type={record.get('type')} />)}
        />
        <Column 
          name="summary"
          renderer={({ record }) => (<FeedbackSummary text={record.get('summary')} />)}
        />
        <Column
          name="status"
          className={`${prefixCls}-statusColumn`}
          renderer={({
            record,
          }) => (<FeedbackStatus status={record.get('status')} />)}
        />
        <Column 
          name="assignee"
          renderer={({ record }) => (<Assignee user={record.get('assignee')} />)}
        />
        <Column 
          name="lastUpdateDate"
          renderer={({ record }) => (<LastUpdateTime date={record.get('lastUpdateDate')} />)}
        />
        <Column 
          name="reporter"
          renderer={({ record }) => (<Reporter reporter={record.get('reporter')} />)}
        />
      </Table>
    );
  };
  
  useImperativeHandle(feedbackTableRef, () => ({
    refresh,
  }));


  return (
    <Page
      ref={tableRef}
      className={`${prefixCls}`}
      service={['agile-service.issue.deleteIssue', 'agile-service.issue.listIssueWithSub']}
    >
      <Header
        title="反馈列表"
      >
        <Button
          funcType="flat"
          onClick={() => { refresh(); }}
        >
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
      </Header>
      <Content>
        <Tabs onChange={handleTabChange}>
          <TabPane tab="全部" key="all">
            {renderTable('all', feedbackTableDataSet)} 
          </TabPane>
          <TabPane tab="建议与意见" key="recommendation_and_opinion">
            {renderTable('recommendation_and_opinion', recommendationDs)}
          </TabPane>
          <TabPane tab="问题咨询" key="question_consultation">
            {renderTable('question_consultation', questionDs)}
          </TabPane>
          <TabPane tab="报告缺陷" key="bug_report">
            {renderTable('bug_report', bugReportDs)}
          </TabPane>
        </Tabs>
       
      </Content>
    </Page>
  );
});

export default withRouter(FeedbackButton(FeedbackTable));
