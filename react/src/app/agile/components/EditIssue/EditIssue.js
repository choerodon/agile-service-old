/* eslint-disable react/sort-comp */
import React, {
  useContext, useState, useEffect, useImperativeHandle, forwardRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import { stores, axios } from '@choerodon/boot';
import { Spin } from 'choerodon-ui';
import { throttle } from 'lodash';
import './EditIssue.scss';
import {
  loadBranchs, loadDatalogs, loadLinkIssues,
  loadIssue, loadWorklogs, loadDocs, getFieldAndValue, loadIssueTypes,
} from '../../api/NewIssueApi';
import RelateStory from '../RelateStory';
import CopyIssue from '../CopyIssue';
import ResizeAble from '../ResizeAble';
import TransformSubIssue from '../TransformSubIssue';
import TransformFromSubIssue from '../TransformFromSubIssue';
import Assignee from '../Assignee';
import ChangeParent from '../ChangeParent';
import IssueHeader from './IssueComponent/IssueHeader';
import IssueBody from './IssueComponent/IssueBody/IssueBody';
import VisibleStore from '../../stores/common/visible/VisibleStore';
import EditIssueContext from './stores';
import IsInProgramStore from '../../stores/common/program/IsInProgramStore';
// 项目加入群之后，不关联自己的史诗和特性，只能关联项目群的，不能改关联的史诗
const { AppState } = stores;

let loginUserId;
let hasPermission;
const defaultProps = {
  applyType: 'agile',
};

const EditIssue = observer(() => {
  const [issueLoading, setIssueLoading] = useState(false);
  const {
    store, 
    forwardedRef,
    issueId: currentIssueId,
    applyType,
    programId,
    onUpdate,
    backUrl,
    onCancel,
    style,
    onDeleteIssue,
    onDeleteSubIssue,
    disabled, 
    prefixCls,
    intlPrefix,   
  } = useContext(EditIssueContext);
  const container = React.createRef();
  const loadIssueDetail = (paramIssueId) => {
    const id = paramIssueId || currentIssueId;
    setIssueLoading(true);
    loadIssue(id, programId || undefined).then((res) => {
      const param = {
        schemeCode: 'agile_issue',
        context: res.typeCode,
        pageCode: 'agile_issue_edit',
      };
      getFieldAndValue(id, param, programId || undefined).then((fields) => {
        setIssueLoading(false);
        store.setIssueFields(res, fields);
      });
    });
    axios.all([
      loadDocs(id),
      loadWorklogs(id),
      loadDatalogs(id, programId || undefined),
      loadLinkIssues(id),
      loadBranchs(id),
    ])
      .then(axios.spread((doc, workLogs, dataLogs, linkIssues, branches) => {
        store.initIssueAttribute(doc, workLogs, dataLogs, linkIssues, branches);
      }));
  };
  useImperativeHandle(forwardedRef, () => ({
    loadIssueDetail,
  }));
  const setQuery = (width = container.current.clientWidth) => {
    if (width <= 600) {
      container.current.setAttribute('max-width', '600px');
    } else {
      container.current.removeAttribute('max-width');
    }
  };
  useEffect(() => {
    loadIssueDetail(currentIssueId);
    axios.all([
      axios.get('/iam/v1/users/self'),
      axios.post('/iam/v1/permissions/checkPermission', [{
        code: 'agile-service.project-info.updateProjectInfo',
        organizationId: AppState.currentMenuType.organizationId,
        projectId: AppState.currentMenuType.id,
        resourceType: 'project',
      }, {
        code: 'agile-service.notice.queryByProjectId',
        organizationId: AppState.currentMenuType.organizationId,
        projectId: AppState.currentMenuType.id,
        resourceType: 'project',
      }]),
      loadIssueTypes(applyType),
    ])
      .then(axios.spread((users, permission, issueTypes) => {
        loginUserId = users.id;
        hasPermission = permission[0].approve || permission[1].approve;
        store.setIssueTypes(issueTypes);
      }));
    setQuery();
  }, [currentIssueId]);

  const handleCopyIssue = () => {
    VisibleStore.setCopyIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    loadIssueDetail();
  };

  const handleRelateStory = () => {
    VisibleStore.setRelateStoryShow(false);
    if (onUpdate) {
      onUpdate();
    }
    loadIssueDetail();
  };

  const handleTransformSubIssue = () => {
    VisibleStore.setTransformSubIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    loadIssueDetail();
  };

  const handleTransformFromSubIssue = () => {
    VisibleStore.setTransformFromSubIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    loadIssueDetail();
  };

  const handleResizeEnd = ({ width }) => {
    localStorage.setItem('agile.EditIssue.width', `${width}px`);
  };


  const handleResize = throttle(({ width }) => {
    setQuery(width);
  }, 150);


  const issue = store.getIssue;
  const {
    issueId, issueNum, summary,
    assigneeId, objectVersionNumber, createdBy, typeCode,
  } = issue;
  const linkIssues = store.getLinkIssues;

  const {
    getChangeParentShow: changeParentShow,
    getAssigneeShow: assigneeShow,
    getCopyIssueShow: copyIssueShow,
    getTransformSubIssueShow: transformSubIssueShow,
    getTransformFromSubIssueShow: transformFromSubIssueShow,
    getRelateStoryShow: relateStoryShow,
  } = VisibleStore;
  const rightDisabled = disabled || (IsInProgramStore.isInProgram && typeCode === 'issue_epic' && !hasPermission && createdBy !== AppState.userInfo.id);
  return (
    <div style={{
      position: 'absolute',
      right: 0,
      top: 0,
      height: '100%',
      zIndex: 101,
      overflow: 'hidden',
    }}
    >
      <ResizeAble
        modes={['left']}
        size={{
          maxWidth: 800,
          minWidth: 440,
        }}
        defaultSize={{
          width: localStorage.getItem('agile.EditIssue.width') || 605,
          height: '100%',
        }}
        onResizeEnd={handleResizeEnd}
        onResize={handleResize}
      >
        <div className={`${prefixCls}`} style={style} ref={container}>
          <div className={`${prefixCls}-divider`} />         
          {
            issueLoading ? (
              <div
                style={{
                  position: 'absolute',
                  top: 0,
                  bottom: 0,
                  left: 0,
                  right: 0,
                  background: 'rgba(255, 255, 255, 0.65)',
                  zIndex: 9999,
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                }}
              >
                <Spin />
              </div>
            ) : null
          }
          <div className="c7n-content">
            <IssueHeader
              disabled={rightDisabled}
              store={store}
              reloadIssue={loadIssueDetail}
              backUrl={backUrl}
              onCancel={onCancel}
              loginUserId={loginUserId}
              hasPermission={hasPermission}
              onDeleteIssue={onDeleteIssue}
              onUpdate={onUpdate}
            />
            <IssueBody
              key={issueId}
              disabled={rightDisabled}
              store={store}
              reloadIssue={loadIssueDetail}
              onUpdate={onUpdate}
              onDeleteSubIssue={onDeleteSubIssue}
              loginUserId={loginUserId}
              hasPermission={hasPermission}
              applyType={applyType}
            // programId={programId}
            />
          </div>
          {
            copyIssueShow ? (
              <CopyIssue
                issueId={issueId}
                issueNum={issueNum}
                issue={issue}
                issueLink={linkIssues}
                issueSummary={summary}
                visible={copyIssueShow}
                onCancel={() => VisibleStore.setCopyIssueShow(false)}
                onOk={handleCopyIssue.bind(this)}
                applyType={applyType}
              />
            ) : null
          }
          {
            relateStoryShow ? (
              <RelateStory
                issue={issue}
                visible={relateStoryShow}
                onCancel={() => VisibleStore.setRelateStoryShow(false)}
                onOk={handleRelateStory.bind(this)}
              />
            ) : null
          }
          {
            transformSubIssueShow ? (
              <TransformSubIssue
                visible={transformSubIssueShow}
                issueId={issueId}
                issueNum={issueNum}
                ovn={objectVersionNumber}
                onCancel={() => VisibleStore.setTransformSubIssueShow(false)}
                onOk={handleTransformSubIssue.bind(this)}
                store={store}
              />
            ) : null
          }
          {
            transformFromSubIssueShow ? (
              <TransformFromSubIssue
                visible={transformFromSubIssueShow}
                issueId={issueId}
                issueNum={issueNum}
                ovn={objectVersionNumber}
                onCancel={() => VisibleStore.setTransformFromSubIssueShow(false)}
                onOk={handleTransformFromSubIssue.bind(this)}
                store={store}
              />
            ) : null
          }

          {
            assigneeShow ? (
              <Assignee
                issueId={issueId}
                issueNum={issueNum}
                visible={assigneeShow}
                assigneeId={assigneeId}
                objectVersionNumber={objectVersionNumber}
                onOk={() => {
                  VisibleStore.setAssigneeShow(false);
                  if (onUpdate) {
                    onUpdate();
                  }
                  loadIssueDetail(issueId);
                }}
                onCancel={() => {
                  VisibleStore.setAssigneeShow(false);
                }}
              />
            ) : null
          }
          {
            changeParentShow ? (
              <ChangeParent
                issueId={issueId}
                issueNum={issueNum}
                visible={changeParentShow}
                objectVersionNumber={objectVersionNumber}
                onOk={() => {
                  VisibleStore.setChangeParentShow(false);
                  loadIssueDetail(issueId);
                }}
                onCancel={() => {
                  VisibleStore.setChangeParentShow(false);
                }}
              />
            ) : null
          }
        </div>
      </ResizeAble>
    </div>
  );
});
EditIssue.defaultProps = defaultProps;
export default EditIssue;
