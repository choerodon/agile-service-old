import React, { Component, useEffect, useState } from 'react';
import {
  Button, Modal, Select, Input, Form, Upload, Icon, Tabs, Table, Spin,
} from 'choerodon-ui';
import { inject } from 'mobx-react';
import {
  axios, Content, Header, Page,
} from '@choerodon/boot';
import {
  ActivityLog, Detail, FeedbackReleated, TimeRecord,
} from './FeedbackComponent/FeedbackSidebarComponent';

function FeedbackSideBar({
  className, feedbackData, activeLog, assigneeArr, AppState, fetchUser, id,
}) {
  const {
    feedbackNum, assignee, assigneeId, reporter, email, type, status, screenSize, browser, creationDate, lastUpdateDate, objectVersionNumber,
  } = feedbackData;

  const handleUpdate = (selectType, content) => {
    const obj = {
      id,
      objectVersionNumber,
      projectId: AppState.currentMenuType.id,
    };
    if (selectType === '状态') {
      obj.status = content;
    } else if (selectType === '经办人') {
      obj.assigneeId = content;
    } else if (selectType === '类型') {
      obj.type = content;
    }
    axios.put(`agile/v1/projects/${AppState.currentMenuType.id}/feedback`, obj).then(res => fetchUser());
  };

  const assigneeToMe = () => {
    axios.get('iam/v1/users/self').then(res => handleUpdate('经办人', res.id));
  };

  const handleFilterUser = (value, setUser, setLoading) => {
    setLoading(true);
    axios.get(`iam/v1/projects/${AppState.currentMenuType.id}/users?param=${value}`).then((res) => {
      setUser(res.list);
      setLoading(false);
    });
  };

  return (
    <aside
      className={className}
    >
      <Detail
        feedbackNum={feedbackNum}
        reporter={reporter}
        email={email}
        type={type}
        assignee={assignee}
        assigneeId={assigneeId}
        assigneeArr={assigneeArr}
        status={status}
        screenSize={screenSize}
        browser={browser}
        creationDate={creationDate}
        lastUpdateDate={lastUpdateDate}
        activeLog={activeLog}
        assigneeToMe={assigneeToMe}
        handleUpdate={handleUpdate}
        handleFilterUser={handleFilterUser}
      />
    </aside>
  );
}

export default inject('AppState')(FeedbackSideBar);
