import React, { Component, useCallback, useState } from 'react';
import {
  Icon, Select, Button, Upload, Input, Menu, Dropdown, Collapse, Tooltip,
} from 'choerodon-ui';
import './FeedbackTableComponent.scss';
import TimeAgo from 'timeago-react';
import UserHead from './UserHead';

export function FeedbackNum({ feedbackNum }) {
  return (
    <span>{feedbackNum}</span>
  );
}

export function FeedbackType({ type }) {
  const typeName = {
    question_consultation: '问题咨询',
    bug_report: '报告缺陷',
    recommendation_and_opinion: '建议与意见',
  };
  return (
    <div>
      <span>{typeName[type]}</span>
    </div>
  );
}

export function FeedbackSummary({ text }) {
  return (
    <Tooltip mouseEnterDelay={0.5} placement="topLeft" title={`问题概要： ${text}`}>
      <span className="c7n-Issue-summary">
        {text}
      </span>
    </Tooltip>
  );
}

export function FeedbackStatus({ status }) {
  const statusObj = new Map([
    [
      'feedback_cancel', {
        color: '#393E46',
        name: '取消',
      },
    ],
    [
      'feedback_done', {
        color: '#4CAF50',
        name: '完成',
      },
    ],
    [
      'feedback_todo', {
        color: '#FFB100',
        name: '待处理',
      },
    ],
    [
      'feedback_doing', {
        color: '#4D90FE',
        name: '处理中',
      },
    ],
  ]);
  return (
    <div
      className="c7n-statusTag"
      style={{
        background: statusObj.get(status).color || 'transparent',
      }}
    >
      {statusObj.get(status).name}
    </div>
  );
}

export function LastUpdateTime({ date }) {
  return (
    <Tooltip mouseEnterDelay={0.5} title={`日期： ${date}`}>
      <div
        style={{ minWidth: 50 }}
      >
        <TimeAgo
          datetime={date}
          locale="zh_CN"
        />
      </div>
    </Tooltip>
  );
}


export function Reporter({ reporter }) {
  return (
    <span>{reporter}</span>
  );
}

export function Assignee({ user }) {
  return user ? (
    <UserHead
      user={{
        id: user && user.id,
        loginName: user && user.loginName,
        realName: user && user.realName,
        imageUrl: user && user.imageUrl,
      }}
    />
  ) : '';
}

// export default {
//   ToggleQuestion,
//   Summary,
// };
