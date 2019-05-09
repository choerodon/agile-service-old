import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import TimeAgo from 'timeago-react';
import { Button, Icon, Popover } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import CreateBranch from '../../../CreateBranch';
import Commits from '../../../Commits';
import MergeRequest from '../../../MergeRequest';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';

const STATUS_SHOW = {
  opened: '开放',
  merged: '已合并',
  closed: '关闭',
};

@inject('AppState')
@observer class IssueBranch extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
    this.state = {
      commitShow: false,
      mergeRequestShow: false,
    };
  }

  componentDidMount() {
  }

  renderBranchs() {
    const {
      store,
    } = this.props;
    const branchs = store.getBranches;
    const {
      totalCommit, commitUpdateTime, totalMergeRequest,
      mergeRequestStatus, mergeRequestUpdateTime,
    } = branchs;
    return (
      <div>
        {
          branchs.branchCount ? (
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
                          this.setState({
                            commitShow: true,
                          });
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
                          this.setState({
                            mergeRequestShow: true,
                          });
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
  }

  render() {
    const { mergeRequestShow, commitShow } = this.state;
    const {
      store, reloadIssue,
    } = this.props;
    const branchs = store.getBranches;
    const { commitUpdateTime, totalMergeRequest } = branchs;
    const { issueId, issueNum, typeCode } = store.getIssue;
    const createBranchShow = VisibleStore.getCreateBranchShow;

    return (
      <div id="branch">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="branch c7n-icon-title" />
            <FormattedMessage id="issue.branch" />
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => VisibleStore.setCreateBranchShow(true)}>
              <Icon type="playlist_add icon" />
              <FormattedMessage id="issue.branch.create" />
            </Button>
          </div>
        </div>
        {this.renderBranchs()}
        {
          createBranchShow ? (
            <CreateBranch
              issueId={issueId}
              typeCode={typeCode}
              issueNum={issueNum}
              onOk={() => {
                VisibleStore.setCreateBranchShow(false);
                if (reloadIssue) {
                  reloadIssue(issueId);
                }
              }}
              onCancel={() => VisibleStore.setCreateBranchShow(false)}
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
                this.setState({ commitShow: false });
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
                this.setState({ mergeRequestShow: false });
              }}
              visible={mergeRequestShow}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueBranch));
