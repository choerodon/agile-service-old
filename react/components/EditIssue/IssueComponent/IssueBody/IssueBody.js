import React, { useContext } from 'react';
import { Tabs } from 'choerodon-ui';
import IssueDetail from './IssueDetail';
import IssueDes from './IssueDes';
import IssueAttachment from './IssueAttachment';
import IssueDoc from './IssueDoc';
import IssueCommit from './IssueCommit';
import IssueWorkLog from './IssueWorkLog';
import IssueLog from './IssueLog';
import SubTask from './SubTask';
import SubBug from './SubBug';
import IssueLink from './IssueLink';
import IssueBranch from './IssueBranch';
import TestLink from './TestLink';
import IssueDropDown from '../IssueDropDown';
import { FieldStoryPoint, FieldSummary } from './Field';
import EditIssueContext from '../../stores';
import './IssueBody.less';

const { TabPane } = Tabs;

const IssueBody = (props) => {
  const { prefixCls, disabled, store } = useContext(EditIssueContext);
  const issue = store.getIssue;
  const { issueId, typeCode, issueTypeVO = {} } = issue;

  return (
    <section className={`${prefixCls}-body`} id="scroll-area" style={{ position: 'relative' }}>
      <div className="line-justify" style={{ margin: '10px 0', alignItems: 'flex-start' }}>
        <FieldSummary
          {...props}
          showTitle={false}
          field={{ fieldCode: 'summary', fieldName: '概要' }}
        />
        <div style={{ flexShrink: 0, color: 'rgba(0, 0, 0, 0.65)' }}>
          {!disabled && (
            <IssueDropDown {...props} />
          )}
        </div>
      </div>
      {/* 故事点 */}
      <div className="line-start">
        {
          issueId && ['story', 'feature'].indexOf(typeCode) !== -1 ? (
            <div style={{ display: 'flex', marginRight: 25 }}>
              <FieldStoryPoint {...props} field={{ fieldCode: 'storyPoints', fieldName: '故事点' }} />
            </div>
          ) : null
        }
        {
          issueId && ['issue_epic', 'feature'].indexOf(typeCode) === -1 ? (
            <div style={{ display: 'flex' }}>
              <FieldStoryPoint {...props} field={{ fieldCode: 'remainingTime', fieldName: '预估时间' }} />
            </div>
          ) : null
        }
      </div>
      <Tabs defaultActiveKey="1">
        <TabPane tab="详情" key="1">
          <IssueDetail {...props} />
          <IssueDes {...props} />
          <IssueAttachment {...props} />
          {issueTypeVO.typeCode && ['sub_task', 'feature'].indexOf(issueTypeVO.typeCode) === -1
            ? <IssueDoc /> : ''
          }
          {issueTypeVO.typeCode && ['issue_epic', 'sub_task', 'feature'].indexOf(issueTypeVO.typeCode) === -1
            ? <SubTask {...props} /> : ''
          }

          {issueTypeVO.typeCode && ['story', 'task'].indexOf(issueTypeVO.typeCode) !== -1
            ? <SubBug {...props} /> : ''
          }
          {issueTypeVO.typeCode && ['feature', 'sub_task'].indexOf(issueTypeVO.typeCode) === -1
            ? <TestLink {...props} /> : ''
          }
          {issueTypeVO.typeCode && ['feature', 'sub_task'].indexOf(issueTypeVO.typeCode) === -1
            ? <IssueLink {...props} /> : ''
          }
        </TabPane>
        <TabPane tab="评论" key="2">
          <IssueCommit {...props} />
        </TabPane>
        <TabPane tab="记录" key="3">
          {issueTypeVO.typeCode && ['feature'].indexOf(issueTypeVO.typeCode) === -1
            ? <IssueWorkLog {...props} /> : ''
          }
          <IssueLog {...props} />
        </TabPane>
        <TabPane tab="开发" key="4">
          {issueTypeVO.typeCode && ['feature'].indexOf(issueTypeVO.typeCode) === -1
            ? <IssueBranch {...props} /> : ''
          }
        </TabPane>
      </Tabs>
    </section>
  );
};

export default IssueBody;
