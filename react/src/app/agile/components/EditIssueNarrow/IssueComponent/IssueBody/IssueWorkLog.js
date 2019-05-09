import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import DailyLog from '../../../DailyLog';
import Log from '../../Component/Log';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
  }

  renderLogs() {
    const { store, reloadIssue, hasPermission } = this.props;
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
  }

  render() {
    const { store, reloadIssue } = this.props;
    const workLogShow = VisibleStore.getWorkLogShow;
    const issue = store.getIssue;
    const { issueNum, issueId } = issue;

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
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => VisibleStore.setWorkLogShow(true)}>
              <Icon type="playlist_add icon" />
              <FormattedMessage id="issue.log.create" />
            </Button>
          </div>
        </div>
        {this.renderLogs()}
        {
          workLogShow ? (
            <DailyLog
              issueId={issueId}
              issueNum={issueNum}
              visible={workLogShow}
              onCancel={() => VisibleStore.setWorkLogShow(false)}
              onOk={() => {
                reloadIssue(issueId);
                VisibleStore.setWorkLogShow(false);
              }}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
