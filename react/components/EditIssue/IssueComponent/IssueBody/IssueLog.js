import React, { useContext } from 'react';
import { Icon } from 'choerodon-ui';
import { filter } from 'lodash';
import { FormattedMessage } from 'react-intl';
import DataLogs from '../../Component/DataLogs';
import EditIssueContext from '../../stores';

const IssueLog = () => {
  const { store } = useContext(EditIssueContext);
  const renderDataLogs = () => {
    const stateDatalogs = store.getDataLogs;
    // 过滤掉影响的版本(bug)
    const datalogs = filter(stateDatalogs, v => v.field !== 'Version');
    const issue = store.getIssue;
    const {
      typeCode, creationDate, createdBy,
      createrImageUrl, createrEmail,
      createrName, issueTypeVO = {},
    } = issue;
    // 创建Issue日志
    const createLog = {
      email: createrEmail,
      field: issueTypeVO && issueTypeVO.typeCode,
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
  };

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
      {renderDataLogs()}
    </div>
  );
};

export default IssueLog;
