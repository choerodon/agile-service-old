import React, {
  Component, useCallback, useEffect, useState,
} from 'react';
import { stores } from '@choerodon/boot';
import {
  Icon, Select, Button, Upload, Input, Menu, Dropdown, Collapse, Tooltip,
} from 'choerodon-ui';
import TimeAgo from 'timeago-react';
import UserHead from './UserHead';
import statusList from './status';
import Field from './Field';
import './FeedbackSidebarComponent.scss';
import { text2Delta } from '../../../common/utils';

const { AppState } = stores;
const { Option } = Select;

const typeMap = new Map([
  ['question_consultation', '问题咨询'],
  ['bug_report', '缺陷报告'],
  ['recommendation_and_opinion', '建议与意见'],
]);

const typeArr = [
  {
    id: 'question_consultation',
    value: '问题咨询',
  },
  {
    id: 'bug_report',
    value: '缺陷报告',
  },
  {
    id: 'recommendation_and_opinion',
    value: '建议与意见',
  },
];

const statusArr = [
  {
    id: 'feedback_todo',
    value: '待处理',
  },
  {
    id: 'feedback_doing',
    value: '处理中',
  },
  {
    id: 'feedback_done',
    value: '已完成',
    enabled: true,
  },
  {
    id: 'feedback_cancel',
    value: '取消',
  },
];

export function Detail({
  feedbackNum, assignee, assigneeId, assigneeArr, assigneeToMe, reporter, email, type, status, priority, screenSize, browser, creationDate, lastUpdateDate, activeLog, handleUpdate, handleFilterUser,
}) {
  return (
    <React.Fragment>
      <section>
        <Title
          iconType="error_outline"
          title="详情"
        />
        <DetailInfo
          feedbackNum={feedbackNum}
          reporter={reporter}
          email={email}
          assignee={assignee}
          assigneeId={assigneeId}
          assigneeArr={assigneeArr}
          type={type}
          status={status}
          priority={priority}
          assigneeToMe={assigneeToMe}
          handleUpdate={handleUpdate}
          handleFilterUser={handleFilterUser}
        />
      </section>
      <section>
        <Title
          iconType="web"
          title="问题相关"
        />
        <FeedbackReleated
          screenSize={screenSize}
          browser={browser}
        />
      </section>
      <section>
        <Title
          iconType="av_timer"
          title="时间记录"
        />
        <TimeRecord
          creationDate={creationDate}
          lastUpdateDate={lastUpdateDate}
        />
      </section>
      <section>
        <Title
          iconType="today"
          title="活动日志"
        />
        {activeLog.length > 5 ? (
          <ExpandContainer
            data={activeLog}
          />
        ) : (
          <ActivityLog
            data={activeLog}
          />
        )}

      </section>
    </React.Fragment>
  );
}

const Title = ({ iconType, title }) => (
  <header className="feedback-sidebar-title">
    <Icon type={iconType} />
    <h3>{title}</h3>
    <div className="feedback-sidebar-title-hr" />
  </header>
);

const InfoComponent = ({
  style, type, infoType, info, current, handleUpdate, handleFilterUser, assigneeToMe,
}) => {
  const [assigneeArr, setAssigneeArr] = useState([]);
  const [assigneeLoading, setAssigneeLoading] = useState(false);
  useEffect(() => {
    if (type === '经办人') {
      setAssigneeArr(info);
    }
  }, []);
  return (
    <section className="feedback-sidebar-info" style={{ ...style }}>
      <h6>{`${type}:`}</h6>
      <p>{info}</p>
    </section>
  );
};
const DetailInfo = ({
  feedbackNum, assignee, assigneeId, assigneeArr, assigneeToMe, reporter, email, type, status, priority, handleUpdate, handleFilterUser,
}) => {
  if (assignee) {
  // eslint-disable-next-line no-param-reassign
    assignee.id = assigneeId;
  }
  return (
    <article>
      <InfoComponent
        type="编号"
        infoType="text"
        info={feedbackNum}
      />
  
      <Field
        field={{
          fieldCode: 'status',
          fieldName: '状态',
          value: status,
          fieldType: 'single',
          fieldOptions: statusArr,
          valueStr: statusList.get(status).name,
        }}
        handleUpdate={(selectType, content) => { handleUpdate(selectType, content); }}
      />
  
      <Field
        field={{
          fieldCode: 'type',
          fieldName: '类型',
          value: type,
          fieldType: 'single',
          fieldOptions: typeArr,
          valueStr: typeMap.get(type),
        }}
        handleUpdate={(selectType, content) => { handleUpdate(selectType, content); }}
      />
  
      <div className="feedback-sidebar-info-detailInfo-assignee">
        <Field
          field={{
            fieldCode: 'member',
            fieldName: '经办人',
            value: assignee,
            fieldType: 'member',
            valueStr: assignee,
          }}
          handleUpdate={(selectType, content) => { handleUpdate(selectType, content); }}
        />
        {(!assignee || (assignee && assignee.id !== AppState.userInfo.id)) ? <span role="none" className="feedback-sidebar-info-detailInfo-assignee-assigneeToMe" onClick={() => assigneeToMe()}>分配给我</span> : ''}
      </div>
  
      <InfoComponent
        style={{ marginTop: 10 }}
        type="报告人"
        infoType="text"
        info={`${reporter}`}
      />

      {email && (
      <InfoComponent
        style={{ marginTop: 18 }}
        type="邮箱"
        infoType="text"
        info={`${email}`}
      />
      )}
    </article>
  );
};

export function FeedbackReleated({ screenSize, browser }) {
  return (
    <article>
      <InfoComponent
        type="所属应用"
        infoType="text"
        info="敏捷管理/活跃冲刺"
      />
      <InfoComponent
        type="屏幕尺寸"
        infoType="text"
        info={screenSize}
      />
      <InfoComponent
        type="浏览器"
        infoType="text"
        info={browser && browser.match(/(Chrome|Firefox|MSIE|Opera|Safari)[/ ][\d.]+/).shift()}
      />
    </article>
  );
}

export function TimeRecord({ creationDate, lastUpdateDate }) {
  return (
    <article>
      <InfoComponent
        type="创建日期"
        infoType="text"
        info={(
          <TimeAgo
            datetime={creationDate}
            locale="zh_CN"
          />
        )}
      />
      <InfoComponent
        type="更新日期"
        infoType="text"
        info={(
          <TimeAgo
            datetime={lastUpdateDate}
            locale="zh_CN"
          />
        )}
      />
    </article>
  );
}

export function ActivityLog({ data }) {
  const verbMap = {
    Assignee: {
      action1: '变更',
      action2: '变更',
      action3: '变更',
    },
    Status: {
      action1: '变更',
      action2: '变更',
      action3: '变更',
    },
    Type: {
      action1: '变更',
      action2: '',
      action3: '',
    },
    Comment: {
      action1: '变更',
      action2: '添加',
      action3: '删除',
    },
    Attachment: {
      action1: '',
      action2: '上传',
      action3: '删除',
    },
  };
  const nounMap = new Map([
    ['Comment', '评论'],
    ['Status', '状态'],
    ['Type', '类型'],
    ['Attachment', '附件'],
    ['Assignee', '经办人'],
  ]);
  const transformComment = (delta) => {
    let newCommentString = '';
    delta.forEach((item) => {
      if (item.insert && item.insert.image) {
        newCommentString += '[图片]';
      } else {
        newCommentString += item.insert;
      }
    });
    return newCommentString;
  };

  const convertFunc = (
    field, newString, newValue, oldString, oldValue, creationDate,
  ) => {
    if (oldValue && newValue) {
      return (
        <React.Fragment>
          <span>{verbMap[field].action1}</span>
          <span className="blue">{nounMap.get(field)}</span>
          <span>为</span>
          <span className="blue">{field === 'Comment' ? transformComment(text2Delta(newString)) : newString}</span>
        </React.Fragment>
      );
    } else if (newValue && !oldValue) {
      return (
        <React.Fragment>
          <span>{verbMap[field].action2}</span>
          <span className="blue">{nounMap.get(field)}</span>
          {field === 'Assignee' && (
          <React.Fragment>
            <span>为</span>
            <span className="blue">{newString}</span>
          </React.Fragment>
          )}
        </React.Fragment>
      );
    } else if (!newValue && oldValue) {
      return (
        <React.Fragment>
          <span>{verbMap[field].action3}</span>
          <span>{field === 'Attachment' ? oldString : nounMap.get(field)}</span>
        </React.Fragment>
      );
    }
    return null;
  };
  return data.map(({
    field, newString, newValue, oldString, oldValue, creationDate, created,
  }) => created && (
    <article className="feedback-sidebar-activitylog">
      <LogHead
        loginName={created.loginName}
        realName={created.name}
        avatar={created.imageUrl}
      />
      <div className="feedback-sidebar-activitylog-header">
        <div>
          <span className="blue">{`${created.loginName} ${created.name}`}</span>
          {convertFunc(field, newString, newValue, oldString, oldValue, creationDate)}
        </div>
        <div style={{ marginTop: '4px' }}>
          <TimeAgo locale="zh_CN" datetime={creationDate} />
        </div>
      </div>
    </article>
  ));
}

const ExpandContainer = ({ data }) => {
  const [expand, toggleExpand] = useState(true);
  const ExpandButton = () => (<Button onClick={() => toggleExpand(!expand)}>{expand ? '展开' : '收起'}</Button>);
  return expand ? (
    <React.Fragment>
      <ActivityLog
        data={data.slice(0, 5)}
      />
      <ExpandButton />
    </React.Fragment>
  ) : (
    <React.Fragment>
      <ActivityLog
        data={data}
      />
      <ExpandButton />
    </React.Fragment>
  );
};

const LogHead = ({ loginName = '', realName = '', avatar }) => (
  <Tooltip
    title={`${loginName}${realName}`}
    mouseEnterDelay={0.5}
    className="feedback-sidebar-activitylog-user"
  >
    <UserHead
      size={30}
      hiddenText
      user={{
        loginName,
        realName,
        imageUrl: avatar,
      }}
    />
  </Tooltip>
);
