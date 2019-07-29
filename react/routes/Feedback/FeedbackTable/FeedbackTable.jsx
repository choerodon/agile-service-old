import React, {
  Component, useEffect, useState, useContext, 
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
import FeedbackButton from '../FeedbackComponent/FeedbackButton';
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
} from '../FeedbackComponent/FeedbackTableComponent';
import FiltersProviderHOC from '../../../components/FiltersProvider/FiltersProvider';

const { TabPane } = Tabs;
const { Column } = Table;

const FeedbackTable = observer(() => {
  const {
    feedbackTableDataSet, prefixCls, 
  } = useContext(FeedbackTableContext);

  function refresh() {
    feedbackTableDataSet.query();
  }

  const [type, setType] = useState(null);

  const handleTabChange = (key) => {
    if (key === 'all') {
      setType(null);
      feedbackTableDataSet.setQueryParameter('searchArgs', { typeList: ['recommendation_and_opinion', 'question_consultation', 'bug_report'] });
      refresh();
    } else {
      setType(key);
      feedbackTableDataSet.setQueryParameter('searchArgs', { typeList: [key] });
      refresh();
    }
  };

  return (
    <Page
      className={`${prefixCls}`}
      service={['agile-service.issue.deleteIssue', 'agile-service.issue.listIssueWithSub']}
    >
      <Header
        title="反馈列表"
      >
        <Button
          funcType="flat"
          onClick={refresh}
        >
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
      </Header>
      <Content>
        <Tabs onChange={handleTabChange}>
          <TabPane tab="全部" key="all" />
          <TabPane tab="建议与意见" key="recommendation_and_opinion" />
          <TabPane tab="问题咨询" key="question_consultation" />
          <TabPane tab="报告缺陷" key="bug_report" />
        </Tabs>
        <Table dataSet={feedbackTableDataSet}>
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
      </Content>
    </Page>
  );
});

// export default inject('AppState')(withRouter(FeedbackButton(FeedbackTable)));
export default FeedbackTable;
