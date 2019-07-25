import React, {
  Component, useEffect, useState, Fragment, 
} from 'react';
import {
  Button, Modal, Select, Input, Form, Upload, Icon, Tabs, Table, Spin,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';
import { inject } from 'mobx-react';
import {
  axios, Content, Header, Page,
} from '@choerodon/boot';
import FeedbackMain from './FeedbackMain';
import FeedbackSidebar from './FeedbackSidebar';
import './FeedbackContent.scss';

const { AppState } = stores;
let hasPermission = false;
const paramConverter = (url) => {
  const reg = /[^?&]([^=&#]+)=([^&#]*)/g;
  const retObj = {};
  url.match(reg).forEach((item) => {
    const [tempKey, paramValue] = item.split('=');
    const paramKey = tempKey[0] !== '&' ? tempKey : tempKey.substring(1);
    Object.assign(retObj, {
      [paramKey]: paramValue,
    });
  });
  return retObj;
};

const useFetch = (dataUrl, commentUrl, logUrl, assigneeUrl, currentAssigneeUrl) => {
  const [dataArr, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchUser = async () => {
    const retArr = [];
    setLoading(true);
    const data = await axios.get(dataUrl);
    retArr.push(data);
    // const comment = await axios.get(commentUrl);
    retArr.push(await axios.get(commentUrl));
    retArr.push(await axios.get(logUrl));
    retArr.push(await axios.get(assigneeUrl));
    setData(retArr);
    setLoading(false);
  }; 

  const getEditOrDeleteCommentPermission = () => {
    const { currentMenuType: { id, organizationId } } = AppState;
    axios.post('/iam/v1/permissions/checkPermission', [{
      code: 'agile-service.project-info.updateProjectInfo',
      organizationId,
      projectId: id,
      resourceType: 'project',
    }, {
      code: 'agile-service.notice.queryByProjectId',
      organizationId,
      projectId: id,
      resourceType: 'project',
    }]).then((res) => {
      hasPermission = res[0].approve || res[1].approve;
    });
  }; 

  useEffect(() => {
    fetchUser();
    getEditOrDeleteCommentPermission();
  }, []);
  return { dataArr, loading, fetchUser };
};

function FeedbackContent({ location }) {
  const url = paramConverter(location.search);

  const {
    dataArr: [data, comment, log, assigneeArr], loading, fetchUser,
  } = useFetch(`/agile/v1/projects/${AppState.currentMenuType.id}/feedback/${url.feedbackId}?organizationId=${AppState.currentMenuType.organizationId * 1}`, `/agile/v1/projects/${AppState.currentMenuType.id}/feedback_comment?feedbackId=${url.feedbackId}`, `/agile/v1/projects/${AppState.currentMenuType.id}/feedback_data_log?feedbackId=${url.feedbackId}`, `iam/v1/projects/${AppState.currentMenuType.id}/users`, `iam/v1/organizations/${AppState.currentMenuType.id}/users`);
  return (
    <Page
      className="c7n-feedback"
      service={['agile-service.issue.deleteIssue', 'agile-service.issue.listIssueWithSub']}
    >
      <Header
        title="问题详情"
        backPath={`/agile/feedback?type=${AppState.currentMenuType.type}&id=${AppState.currentMenuType.id * 1}&name=${encodeURIComponent(AppState.currentMenuType.name)}&organizationId=${AppState.currentMenuType.organizationId * 1}`}
      >
        <Button
          funcType="flat"
          onClick={fetchUser}
        >
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
      </Header>
      <Content
        style={{
          padding: 0,
          overflow: 'hidden',
        }}
      >
        <Spin spinning={loading}>
          <section style={{ display: 'flex', flexGrow: '1', height: '100%' }}>
            {
            data && (
              <Fragment>
                <FeedbackMain
                  fetchUser={fetchUser}
                  id={url.feedbackId * 1}
                  className="feedback-main"
                  feedbackData={data}
                  feedbackComment={comment}
                  hasPermission={hasPermission}
                />
                <FeedbackSidebar
                  fetchUser={fetchUser}
                  id={url.feedbackId * 1}
                  assigneeArr={assigneeArr && assigneeArr.list}
                  className="feedback-sidebar"
                  feedbackData={data}
                  activeLog={log}
                />
              </Fragment>
            )
          }
          </section>
        </Spin>
      </Content>

    </Page>
  );
}

export default FeedbackContent;
