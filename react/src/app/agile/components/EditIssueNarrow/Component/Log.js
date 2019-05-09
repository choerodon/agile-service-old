import React, { Component } from 'react';
import { Icon, Popconfirm } from 'choerodon-ui';
import { AppState } from 'choerodon-front-boot';
import TimeAgo from 'timeago-react';
import UserHead from '../../UserHead';
import WYSIWYGEditor from '../../WYSIWYGEditor';
import { IssueDescription, DatetimeAgo } from '../../CommonComponent';
import {
  delta2Html, text2Delta, beforeTextUpload, formatDate,
} from '../../../common/utils';
import { deleteWorklog, updateWorklog } from '../../../api/NewIssueApi';
import './Log.scss';


class Log extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      editLogId: undefined,
      editLog: undefined,
      expand: false,
    };
  }

  componentDidMount() {
  }

  updateLog = (log) => {
    const { onUpdateLog } = this.props;
    updateWorklog(log.logId, log).then((res) => {
      this.setState({
        editLogId: undefined,
        editLog: undefined,
      });
      onUpdateLog();
    });
  };

  handleDeleteLog(logId) {
    const { onDeleteLog } = this.props;
    deleteWorklog(logId)
      .then((res) => {
        onDeleteLog();
      });
  }

  handleUpdateLog(log) {
    const { logId, objectVersionNumber } = log;
    const { editLog } = this.state;
    const extra = {
      logId,
      objectVersionNumber,
    };
    const updateLogDes = editLog;
    if (updateLogDes) {
      beforeTextUpload(updateLogDes, extra, this.updateLog, 'description');
    } else {
      extra.description = '';
      this.updateLog(extra);
    }
  }

  cancel(e) {
  }

  confirm(logId, e) {
    this.handleDeleteLog(logId);
  }

  render() {
    const { worklog, isWide, hasPermission } = this.props;
    const { editLog, editLogId, expand } = this.state;
    const deltaEdit = text2Delta(editLog);
    return (
      <div
        className={`c7n-log ${worklog.logId === editLogId ? 'c7n-log-focus' : ''}`}
      >
        <div className="line-justify">
          {
            expand ? (
              <Icon
                role="none"
                style={{
                  position: 'absolute',
                  left: 5,
                  top: 15,
                }}
                type="baseline-arrow_drop_down pointer"
                onClick={() => {
                  this.setState({
                    expand: false,
                  });
                }}
              />
            ) : null
          }
          {
            !expand ? (
              <Icon
                role="none"
                style={{
                  position: 'absolute',
                  left: 5,
                  top: 15,
                }}
                type="baseline-arrow_right pointer"
                onClick={() => {
                  this.setState({
                    expand: true,
                  });
                }}
              />
            ) : null
          }
          <div className="c7n-title-log">
            <div style={{ marginRight: 19 }}>
              <UserHead
                user={{
                  id: worklog.userId,
                  loginName: '',
                  realName: worklog.userName,
                  avatar: worklog.imageUrl,
                }}
                color="#3f51b5"
              />
            </div>
            <span style={{ color: 'rgba(0, 0, 0, 0.65)', marginLeft: 15 }}>
              <DatetimeAgo
                date={worklog.lastUpdateDate}
              />
            </span>
          </div>
          <div className="c7n-action">
            <Icon
              role="none"
              type="mode_edit mlr-3 pointer"
              onClick={() => {
                this.setState({
                  editLogId: worklog.logId,
                  editLog: worklog.description,
                  expand: true,
                });
              }}
            />
            {hasPermission
              ? (
                <Popconfirm
                  title="确认要删除该工作日志吗?"
                  placement="left"
                  onConfirm={this.confirm.bind(this, worklog.logId)}
                  onCancel={this.cancel}
                  okText="删除"
                  cancelText="取消"
                  okType="danger"
                >
                  <Icon
                    type="delete_forever mlr-3 pointer"
                  />
                </Popconfirm>
              ) : ''
            }
          </div>
        </div>
        <div className="line-start" style={{ color: 'rgba(0, 0, 0, 0.65)', marginTop: '10px' }}>
          <span style={{ width: 70 }}>耗费时间:</span>
          <span style={{ color: '#000', fontWeight: '500' }}>{`${worklog.workTime}小时` || '无'}</span>
        </div>
        <div className="line-start" style={{ color: 'rgba(0, 0, 0, 0.65)', marginTop: '10px' }}>
          <span style={{ width: 70 }}>工作日期:</span>
          <span style={{ color: '#000', fontWeight: '500' }}>{worklog.startDate || '无'}</span>
        </div>
        {
          expand && (
            <div>
              <div className="c7n-conent-log" style={{ marginTop: 10, display: 'flex' }}>
                <span style={{ width: 70, flexShrink: 0, color: 'rgba(0, 0, 0, 0.65)' }}>备注:</span>
                <span style={{ flex: 1 }}>
                  {
                    worklog.logId !== editLogId ? (
                      <IssueDescription data={delta2Html(worklog.description)} />
                    ) : null
                  }
                </span>
              </div>
              {
                worklog.logId === editLogId ? (
                  <WYSIWYGEditor
                    bottomBar
                    value={deltaEdit}
                    style={{ height: 200, width: '100%' }}
                    onChange={(value) => {
                      this.setState({ editLog: value });
                    }}
                    handleDelete={() => {
                      this.setState({
                        editLogId: undefined,
                        editLog: undefined,
                      });
                    }}
                    handleSave={this.handleUpdateLog.bind(this, worklog)}
                    toolbarHeight={isWide ? null : 66}
                  />
                ) : null
              }
            </div>
          )
        }

      </div>
    );
  }
}

export default Log;
