import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import DailyLog from '../../../DailyLog';
import Log from '../../Component/Log';
import EditIssueContext from '../../stores';

const IssueCommit = observer(({
  hasPermission,
  reloadIssue,
}) => {
  const { store, disabled } = useContext(EditIssueContext);

  const workLogShow = store.getWorkLogShow;
  const issue = store.getIssue;
  const { issueNum, issueId } = issue;

  const renderLogs = () => {
    const worklogs = store.getWorkLogs || [];
    return (
      <div>
        {
          worklogs.map(worklog => (
            <Log
              key={worklog.logId}
              worklog={worklog}
              onDeleteLog={reloadIssue}
              onUpdateLog={reloadIssue}
              hasPermission={hasPermission}
            />
          ))
        }
      </div>
    );
  };

  return (
    <div id="log">
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">
          <Icon type="work_log c7n-icon-title" />
          <FormattedMessage id="issue.log" />
        </div>
        <div style={{
          flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
        }}
        />
        {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="登记工作" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => store.setWorkLogShow(true)}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
        )}
      </div>
      {renderLogs()}
      {
        workLogShow ? (
          <DailyLog
            issueId={issueId}
            issueNum={issueNum}
            visible={workLogShow}
            onCancel={() => store.setWorkLogShow(false)}
            onOk={() => {
              reloadIssue(issueId);
              store.setWorkLogShow(false);
            }}
          />
        ) : null
      }
    </div>
  );
});

export default IssueCommit;
