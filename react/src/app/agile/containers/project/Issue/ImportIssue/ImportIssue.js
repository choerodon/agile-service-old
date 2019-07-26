import React, { Component } from 'react';
import { stores, WSHandler } from '@choerodon/boot';
import {
  Modal, Button, Icon, Progress, 
} from 'choerodon-ui';
import FileSaver from 'file-saver';
import {
  exportExcelTmpl,
  importIssue,
  cancelImport,
  queryImportHistory,
} from '../../../../api/NewIssueApi';
import './ImportIssue.scss';

const { AppState } = stores;

class ImportIssue extends Component {
  state = {
    visible: false,
    step: 1,
    wsData: {},
    historyId: false,
    ovn: false,
    latestInfo: false,
    fileName: false,
  };

  loadLatestImport = () => {
    queryImportHistory().then((res) => {
      if (res) {
        this.setState({
          latestInfo: res,
          historyId: res.status === 'doing' ? res.id : false,
          ovn: res.objectVersionNumber,
          step: res.status === 'doing' ? 3 : 1,
        });
      }
    });
  };

  open = () => {
    this.setState({
      visible: true,
    });
    this.loadLatestImport();
  };

  onCancel = () => {
    const { historyId, ovn } = this.state;
    if (historyId) {
      cancelImport(historyId, ovn);
    }
    this.finish();
  };

  exportExcel = () => {
    exportExcelTmpl().then((excel) => {
      const blob = new Blob([excel], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const fileName = '问题导入模板.xlsx';
      FileSaver.saveAs(blob, fileName);
    });
  };

  importExcel = () => {
    this.uploadInput.click();
  };

  beforeUpload = (e) => {
    if (e.target.files[0]) {
      this.upload(e.target.files[0]);
    }
  };

  upload = (file) => {
    if (!file) {
      Choerodon.prompt('请选择文件');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    this.setState({
      uploading: true,
      fileName: file.name,
    });
    importIssue(formData).then((res) => {
      this.changeStep(1);
      // this.uploadInput.value = '';
      this.setState({
        uploading: false,
      });
    }).catch((e) => {
      this.setState({
        uploading: false,
      });
      Choerodon.prompt('网络错误');
    });
  };

  handleMessage = (data) => {
    if (data) {
      this.setState({
        wsData: data,
        historyId: data.id,
        ovn: data.objectVersionNumber,
      });
      if (data.status === 'failed') {
        if (data.fileUrl) {
          window.location.href = data.fileUrl;
        }
      }
    }
  };

  changeStep = (value) => {
    const { step } = this.state;
    this.setState({
      step: step + value,
    });
  };

  finish = () => {
    const { onFinish } = this.props;
    if (onFinish) {
      onFinish();
    }
    this.setState({
      visible: false,
      step: 1,
      wsData: {},
      historyId: false,
    });
  };

  renderFooter = () => {
    const { step, wsData } = this.state;
    if (step === 1) {
      return [
        <Button onClick={this.onCancel}>取消</Button>,
        <Button type="primary" onClick={() => this.changeStep(1)}>下一步</Button>,        
      ];
    } else if (step === 2) {
      return [
        <Button onClick={this.onCancel}>取消</Button>,
        <Button type="primary" onClick={() => this.changeStep(-1)}>上一步</Button>,        
      ];
    } else {
      return [
        <Button type="primary" onClick={this.finish}>完成</Button>,
        <Button disabled={wsData.status && wsData.status !== 'doing'} onClick={this.onCancel}>取消上传</Button>,
      ];
    }
  };

  renderProgress = () => {
    const { wsData, fileName } = this.state;
    const {
      process = 0,
      status,
      failCount,
      fileUrl,
      successCount,
    } = wsData;
    if (status === 'doing') {
      return (
        <div style={{ width: 512 }}>
          {fileName
            ? (
              <span className="c7n-importIssue-fileName">
                <Icon type="folder_open" className="c7n-importIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7n-importIssue-text">正在导入</span>
          <Progress
            className="c7n-importIssue-progress"
            percent={(process * 100).toFixed(0)}
            size="small"
            status="active"
            showInfo={false}
          />
        </div>
      );
    } else if (status === 'failed') {
      return (
        <div>
          {fileName
            ? (
              <span className="c7n-importIssue-fileName">
                <Icon type="folder_open" className="c7n-importIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7n-importIssue-text">
            {'导入失败 '}
            <span style={{ color: '#FF0000' }}>{failCount}</span>
            {' 问题'}
            <a href={fileUrl}>
              {' 点击下载失败详情'}
            </a>
          </span>
        </div>
      );
    } else if (status === 'success') {
      return (
        <div>
          {fileName
            ? (
              <span className="c7n-importIssue-fileName">
                <Icon type="folder_open" className="c7n-importIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7n-importIssue-text">
            {'导入成功 '}
            <span style={{ color: '#0000FF' }}>{successCount}</span>
            {' 问题'}
          </span>
        </div>
      );
    } else if (status === 'template_error') {
      return (
        <div>
          {fileName
            ? (
              <span className="c7n-importIssue-fileName">
                <Icon type="folder_open" className="c7n-importIssue-icon" />
                <span>{fileName}</span>
              </span>
            )
            : ''
          }
          <span className="c7n-importIssue-text">
            {'导入模板错误，或无数据。'}
          </span>
        </div>
      );
    } else {
      return (
        <div>
          {'正在查询导入信息，请稍后'}
        </div>
      );
    }
  };

  renderForm = () => {
    const { step, uploading, latestInfo } = this.state;
    const { failCount, fileUrl } = latestInfo;
    if (step === 1) {
      return (
        <React.Fragment>
          <Button
            type="primary"
            // funcType="flat"
            onClick={() => this.exportExcel()}
          >
            <Icon type="get_app icon" />
            <span>下载模板</span>
          </Button>
          {failCount
            ? (
              <div style={{ marginTop: 10 }}>
                {failCount && failCount !== 0
                  ? (
                    <span>
                      {'导入失败 '}
                      <span style={{ color: '#F44336' }}>
                        {failCount}
                      </span>
                      {' 问题，'}
                    </span>
                  ) : ''
              }
                {fileUrl && (
                <a href={fileUrl}>
                  {' 点击下载失败详情'}
                </a>
                )}
              </div>
            ) : ''
          }
        </React.Fragment>
      );
    } else if (step === 2) {
      return (
        <React.Fragment>
          <Button
            loading={uploading}
            type="primary"
            funcType="flat"
            onClick={() => this.importExcel()}
            style={{ marginBottom: 2 }}
          >
            <Icon type="archive icon" />
            <span>导入问题</span>
          </Button>
          <input
            ref={
              (uploadInput) => { this.uploadInput = uploadInput; }
            }
            type="file"
            onChange={this.beforeUpload}
            style={{ display: 'none' }}
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          />
        </React.Fragment>
      );
    } else {
      return (
        <WSHandler
          messageKey={`choerodon:msg:agile-import-issues:${AppState.userInfo.id}`}
          onMessage={this.handleMessage}
        >
          {this.renderProgress()}
        </WSHandler>
      );
    }
  };

  render() {
    const {
      visible,
    } = this.state;
    return (
      <Modal
        className="c7n-importIssue"
        title="导入问题"
        visible={visible}
        onCancel={this.onCancel}
        footer={this.renderFooter()}
        destroyOnClose
      >  
        <div style={{ height: 70 }}>
          <div style={{ margin: '20px 0' }}>导入问题前请先下载 导入问题模板 ，按照格式要求进行问题数据准备。</div>
          {this.renderForm()}  
        </div>
      </Modal>
    );
  }
}


export default ImportIssue;
