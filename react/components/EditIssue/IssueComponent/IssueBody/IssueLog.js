import React, { useContext } from 'react';
import { filter } from 'lodash';
import { FormattedMessage } from 'react-intl';
import DataLogs from '../../Component/DataLogs';
import EditIssueContext from '../../stores';
import Divider from './Divider';

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
      <Divider />
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left">         
          <FormattedMessage id="issue.data_log" />
        </div>        
      </div>
      {renderDataLogs()}
    </div>
  );
};

export default IssueLog;
