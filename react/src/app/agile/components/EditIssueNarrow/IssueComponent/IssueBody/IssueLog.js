import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button } from 'choerodon-ui';
import _ from 'lodash';
import { injectIntl, FormattedMessage } from 'react-intl';
import DataLogs from '../../Component/DataLogs';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
  }

  renderDataLogs() {
    const { store } = this.props;
    const stateDatalogs = store.getDataLogs;
    // 过滤掉影响的版本(bug)
    const datalogs = _.filter(stateDatalogs, v => v.field !== 'Version');
    const issue = store.getIssue;
    const {
      typeCode, creationDate, createdBy,
      createrImageUrl, createrEmail,
      createrName, issueTypeDTO = {},
    } = issue;
    // 创建Issue日志
    const createLog = {
      email: createrEmail,
      field: issueTypeDTO && issueTypeDTO.typeCode,
      imageUrl: createrImageUrl,
      name: createrName,
      lastUpdateDate: creationDate,
      lastUpdatedBy: createdBy,
      newString: 'issueNum',
      newValue: 'issueNum',
      logId: 'create',
    };
    return (
      <DataLogs
        datalogs={[...datalogs, createLog]}
        typeCode={typeCode}
        createdById={createdBy}
        creationDate={creationDate}
      />
    );
  }

  render() {
    return (
      <div id="data_log">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="insert_invitation c7n-icon-title" />
            <FormattedMessage id="issue.data_log" />
          </div>
          <div
            style={{
              flex: 1,
              height: 1,
              borderTop: '1px solid rgba(0, 0, 0, 0.08)',
              marginLeft: '14px',
            }}
          />
        </div>
        {this.renderDataLogs()}
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
