import React, { useState } from 'react';
import TimeAgo from 'timeago-react';
import {
  Button, Icon, Popover, Tooltip,
} from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import CreateBranch from '../../../CreateBranch';
import Commits from '../../../Commits';
import MergeRequest from '../../../MergeRequest';

const STATUS_SHOW = {
  opened: '开放',
  merged: '已合并',
  closed: '关闭',
};
const IssueBranch = observer(({ store, reloadIssue, disabled }) => {
  const [commitShow, setCommitShow] = useState(false);
  const [mergeRequestShow, setMergeRequestShow] = useState(false);

  const branch = store.getBranch;
  const {
    totalCommit, commitUpdateTime, totalMergeRequest,
    mergeRequestStatus, mergeRequestUpdateTime,
  } = branch;
  const { issueId, issueNum, typeCode } = store.getIssue;
  const createBranchShow = store.getCreateBranchShow;

  const renderBranchs = () => (
    <div>
      {
        branch.branchCount ? (
          <div>
            {
              [].length === 0 ? (
                <div style={{
                  borderBottom: '1px solid rgba(0, 0, 0, 0.08)', display: 'flex', padding: '8px 26px', alignItems: 'center', justifyContent: 'space-between', fontSize: '13px',
                }}
                >
                  <div style={{ display: 'inline-flex', justifyContent: 'space-between', flex: 1 }}>
                    <span
                      style={{ color: '#3f51b5', cursor: 'pointer' }}
                      role="none"
                      onClick={() => {
                        setCommitShow(true);
                      }}
                    >
                      {totalCommit || '0'}
                      {'提交'}
                    </span>
                  </div>
                  <div style={{ display: 'inline-flex', justifyContent: 'space-between' }}>
                    <span style={{ marginRight: 12, marginLeft: 63 }}>已更新</span>
                    <span style={{ width: 60, display: 'inline-block' }}>
                      {
                        commitUpdateTime ? (
                          <Popover
                            title="提交修改时间"
                            content={commitUpdateTime}
                            placement="left"
                          >
                            <TimeAgo
                              datetime={commitUpdateTime}
                              locale={Choerodon.getMessage('zh_CN', 'en')}
                            />
                          </Popover>
                        ) : ''
                      }
                    </span>
                  </div>
                </div>
              ) : null
            }
            {
              totalMergeRequest ? (
                <div style={{
                  borderBottom: '1px solid rgba(0, 0, 0, 0.08)', display: 'flex', padding: '8px 26px', alignItems: 'center', justifyContent: 'space-between', fontSize: '13px',
                }}
                >
                  <div style={{ display: 'inline-flex', justifyContent: 'space-between', flex: 1 }}>
                    <span
                      style={{ color: '#3f51b5', cursor: 'pointer' }}
                      role="none"
                      onClick={() => {
                        setMergeRequestShow(true);
                      }}
                    >
                      {totalMergeRequest}
                      {'合并请求'}
                    </span>
                    <span style={{
                      width: 36, height: 20, borderRadius: '2px', color: '#fff', background: '#4d90fe', textAlign: 'center',
                    }}
                    >
                      {['opened', 'merged', 'closed'].includes(mergeRequestStatus) ? STATUS_SHOW[mergeRequestStatus] : ''}
                    </span>
                  </div>
                  <div style={{ display: 'inline-flex', justifyContent: 'space-between' }}>
                    <span style={{ marginRight: 12, marginLeft: 63 }}>已更新</span>
                    <span style={{ width: 60, display: 'inline-block' }}>
                      {
                        mergeRequestUpdateTime ? (
                          <Popover
                            title="合并请求修改时间"
                            content={mergeRequestUpdateTime}
                            placement="left"
                          >
                            <TimeAgo
                              datetime={mergeRequestUpdateTime}
                              locale={Choerodon.getMessage('zh_CN', 'en')}
                            />
                          </Popover>
                        ) : ''
                      }
                    </span>
                  </div>
                </div>
              ) : null
            }
          </div>
        ) : (
          <div style={{
            borderBottom: '1px solid rgba(0, 0, 0, 0.08)', display: 'flex', padding: '8px 26px', alignItems: 'center', justifyContent: 'space-between', fontSize: '13px',
          }}
          >
            <span style={{ marginRight: 12 }}>暂无</span>
          </div>
        )
      }
    </div>
  );

  return (
    <div id="branch">
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">   
          <FormattedMessage id="issue.branch" />
        </div>        
        {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="创建分支" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => store.setCreateBranchShow(true)}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
        )}
      </div>
      {renderBranchs()}
      {
        createBranchShow ? (
          <CreateBranch
            issueId={issueId}
            typeCode={typeCode}
            issueNum={issueNum}
            onOk={() => {
              store.setCreateBranchShow(false);
              if (reloadIssue) {
                reloadIssue(issueId);
              }
            }}
            onCancel={() => store.setCreateBranchShow(false)}
            visible={createBranchShow}
          />
        ) : null
      }
      {
        commitShow ? (
          <Commits
            issueId={issueId}
            issueNum={issueNum}
            time={commitUpdateTime}
            onCancel={() => {
              setCommitShow(false);
            }}
            visible={commitShow}
          />
        ) : null
      }
      {
        mergeRequestShow ? (
          <MergeRequest
            issueId={issueId}
            issueNum={issueNum}
            num={totalMergeRequest}
            onCancel={() => {
              setMergeRequestShow(false);
            }}
            visible={mergeRequestShow}
          />
        ) : null
      }
    </div>
  );
});

export default IssueBranch;
