import React, { useContext } from 'react';
import { Icon } from 'choerodon-ui';
import IssueNumber from './IssueNumber';
import IssueType from './IssueType';

import './IssueComponent.scss';
import EditIssueContext from '../stores';
import './IssueHeader.less';

const IssueHeader = (props) => {
  const { AppState, store, prefixCls } = useContext(EditIssueContext);
  const {
    resetIssue, backUrl, onCancel, reloadIssue, 
  } = props;
  const urlParams = AppState.currentMenuType;
  const issue = store.getIssue;
  const {
    parentIssueId, relateIssueId, typeCode, parentIssueNum, relateIssueNum,
  } = issue;

  return (
    <div className={`${prefixCls}-IssueHeader`}>
      <div className={`${prefixCls}-IssueHeader-top`}>
        <IssueType {...props} />
        {/* 问题编号 */}
        <span style={{ marginLeft: 15 }}>
          <IssueNumber
            parentIssueId={parentIssueId || relateIssueId}
            resetIssue={resetIssue}
            reloadIssue={reloadIssue}
            urlParams={urlParams}
            backUrl={backUrl}
            typeCode={typeCode}
            parentIssueNum={parentIssueNum || relateIssueNum}
            issue={issue}
          />
        </span>
        {/* 隐藏 */}
        <div
          className={`${prefixCls}-IssueHeader-btn`}          
          role="none"
          onClick={() => {
            onCancel();
          }}
        >
          <Icon type="last_page" style={{ fontSize: '18px', fontWeight: '500' }} />
          <span>隐藏详情</span>
        </div>
      </div>
    </div>
  );
};

export default IssueHeader;
