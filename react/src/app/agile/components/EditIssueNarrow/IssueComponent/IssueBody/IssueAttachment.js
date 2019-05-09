import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon } from 'choerodon-ui';
import _ from 'lodash';
import { injectIntl } from 'react-intl';
import { UploadButtonNow } from '../../../CommonComponent';
import { handleFileUpload } from '../../../../common/utils';

@inject('AppState')
@observer class IssueAttachment extends Component {
  constructor(props) {
    super(props);
    this.state = {
      fileList: false,
    };
  }

  componentDidMount() {
  }

  /**
   * 更新fileList
   * @param data
   */
  setFileList = (data) => {
    this.setState({ fileList: data });
  };

  refresh = () => {
    this.setFileList(false);
    const { reloadIssue } = this.props;
    if (reloadIssue) {
      reloadIssue();
    }
  };

  /**
   * 上传附件
   * @param arr
   */
  onChangeFileList = (arr) => {
    const { AppState, store } = this.props;
    const { issueId } = store.getIssue;
    if (arr.length > 0 && arr.some(one => !one.url)) {
      const config = {
        issueId,
        fileName: arr[0].name || 'AG_ATTACHMENT',
        projectId: AppState.currentMenuType.id,
      };
      handleFileUpload(arr, this.refresh, config);
    }
  };

  render() {
    const { fileList } = this.state;
    const { store } = this.props;
    const { issueAttachmentDTOList = [] } = store.getIssue;
    const files = fileList || _.map(issueAttachmentDTOList, issueAttachment => ({
      uid: issueAttachment.attachmentId,
      name: issueAttachment.fileName,
      url: issueAttachment.url,
    }));
    return (
      <div id="attachment">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="attach_file c7n-icon-title" />
            <span>附件</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px', marginRight: '114.67px',
          }}
          />
        </div>
        <div className="c7n-content-wrapper" style={{ marginTop: '-47px', justifyContent: 'flex-end' }}>
          <UploadButtonNow
            onRemove={this.setFileList}
            onBeforeUpload={this.setFileList}
            updateNow={this.onChangeFileList}
            fileList={files}
          />
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueAttachment));
