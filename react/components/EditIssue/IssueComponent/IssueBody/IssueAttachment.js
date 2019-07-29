import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { UploadButtonNow } from '../../../CommonComponent';
import { handleFileUpload, getProjectId } from '../../../../common/utils';
import EditIssueContext from '../../stores';
import Divider from './Divider';

const IssueAttachment = observer(({ reloadIssue }) => {
  const { store, disabled } = useContext(EditIssueContext);
  const { issueId, issueAttachmentVOList = [] } = store.getIssue;
  const initialFileList = issueAttachmentVOList.map(issueAttachment => ({
    uid: issueAttachment.attachmentId,
    name: issueAttachment.fileName,
    url: issueAttachment.url,
  }));
  const [fileList, setFileList] = useState(initialFileList);

  const refresh = () => {
    if (reloadIssue) {
      reloadIssue(issueId);
    }
  };

  /**
   * 上传附件
   * @param arr
   */
  const onChangeFileList = (arr) => {
    if (arr.length > 0 && arr.some(one => !one.url)) {
      const config = {
        issueId,
        fileName: arr[0].name || 'AG_ATTACHMENT',
        projectId: getProjectId(),
      };
      handleFileUpload(arr, refresh, config);
    }
  };

  return (
    <div id="attachment">
      <Divider />
      <div className="c7n-title-wrapper">
        <div className="c7n-title-left"> 
          <span>附件</span>
        </div>        
      </div>
      {!disabled && (
        <div className="c7n-content-wrapper" style={{ marginTop: '-47px', justifyContent: 'flex-end' }}>
          <UploadButtonNow
            onRemove={setFileList}
            onBeforeUpload={setFileList}
            updateNow={onChangeFileList}
            fileList={fileList}
          />
        </div>
      )}
    </div>
  );
});

export default IssueAttachment;
