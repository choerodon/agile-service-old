/* eslint-disable react/sort-comp */
import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { stores, axios } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { Spin } from 'choerodon-ui';
import { throttle } from 'lodash';
import '../EditIssue/EditIssue.scss';
import {
  loadDatalogs, loadIssue, getFieldAndValue,
} from '../../api/QueryProgramApi';
// import { loadIssueTypes } from '../../api/NewIssueApi';
import CopyIssue from '../CopyIssue';
import IssueSidebar from './IssueComponent/IssueSidebar';
import IssueHeader from './IssueComponent/IssueHeader';
import IssueBody from './IssueComponent/IssueBody/IssueBody';
import VisibleStore from '../../stores/common/visible/VisibleStore';
import ResizeAble from '../ResizeAble';
import EditIssueStore from '../EditIssue/EditIssueStore';

const { AppState } = stores;
const store = EditIssueStore;
let loginUserId;
let hasPermission;
const defaultProps = {
  disabled: true,
};

@observer class FeatureDetailShow extends Component {
  constructor(props) {
    super(props);
    this.state = {
      issueLoading: false,
    };
    this.container = React.createRef();
  }

  componentDidMount() {
    const { onRef, issueId } = this.props;
    if (onRef) {
      onRef(this);
    }
    this.loadIssueDetail(issueId);

    axios.all([
      axios.get('/iam/v1/users/self'),
      axios.post('/iam/v1/permissions/checkPermission', [{
        code: 'agile-service.project-info.updateProjectInfo',
        organizationId: AppState.currentMenuType.organizationId,
        projectId: AppState.currentMenuType.id,
        resourceType: 'project',
      }, {
        code: 'agile-service.notice.queryByProjectId',
        organizationId: AppState.currentMenuType.organizationId,
        projectId: AppState.currentMenuType.id,
        resourceType: 'project',
      }]),
      // loadIssueTypes('program'),
    ])
      .then(axios.spread((users, permission) => {
        loginUserId = users.id;
        hasPermission = permission[0].approve || permission[1].approve;
        // store.setIssueTypes(issueTypes);
      }));
    this.setQuery();
  }

  componentWillReceiveProps(nextProps) {
    const { issueId } = this.props;
    if (nextProps.issueId && nextProps.issueId !== issueId) {
      this.loadIssueDetail(nextProps.issueId);
    }
  }

  loadIssueDetail = (paramIssueId) => {
    const { issueId, programId } = this.props;
    const id = paramIssueId || issueId;
    this.setState({
      issueLoading: true,
    }, () => {
      loadIssue(id, programId).then((res) => {
        const param = {
          schemeCode: 'agile_issue',
          context: res.typeCode,
          pageCode: 'agile_issue_edit',
        };
        getFieldAndValue(id, param, programId).then((fields) => {
          this.setState({
            issueLoading: false,
          });
          store.setIssueFields(res, fields);
        });
      });
      axios.all([
        loadDatalogs(id, programId),
      ])
        .then(axios.spread((dataLogs) => {
          store.initIssueAttribute([], [], dataLogs, [], []);
        }));
    });
  };

  handleCopyIssue = () => {
    const { onUpdate } = this.props;
    VisibleStore.setCopyIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    this.loadIssueDetail();
  };

  handleResizeEnd = (size) => {
    const { width } = size;
    localStorage.setItem('agile.EditIssue.width', `${width}px`);
  }

  setQuery = (width = this.container.current.clientWidth) => {
    if (width <= 600) {
      this.container.current.setAttribute('max-width', '600px');
    } else {
      this.container.current.removeAttribute('max-width');
    }
  }

  handleResize = throttle(({ width }) => {
    this.setQuery(width);
    // console.log(width, parseInt(width / 100) * 100);
  }, 150)

  render() {
    const {
      backUrl,
      onCancel,
      style,
      onUpdate,
      onDeleteIssue,
      disabled,
      programId,
    } = this.props;
    const {
      issueLoading,
    } = this.state;

    const issue = store.getIssue;
    const { issueId, issueNum, summary } = issue;
    const linkIssues = store.getLinkIssues;
    const {
      getCopyIssueShow: copyIssueShow,
    } = VisibleStore;

    return (
      <div style={{
        position: 'absolute',
        right: 0,
        top: 0,
        height: '100%',
        zIndex: 101,
        overflow: 'hidden',
      }}
      >
        <ResizeAble
          modes={['left']}
          size={{
            maxWidth: 800,
            minWidth: 440,
          }}
          defaultSize={{
            width: localStorage.getItem('agile.EditIssue.width') || 440,
            height: '100%',
          }}
          onResizeEnd={this.handleResizeEnd}
          onResize={this.handleResize}
        >
          <div className="choerodon-modal-editIssue" style={style} ref={this.container}>
            <div className="choerodon-modal-editIssue-divider" />
            {
              issueLoading ? (
                <div
                  style={{
                    position: 'absolute',
                    top: 0,
                    bottom: 0,
                    left: 0,
                    right: 0,
                    background: 'rgba(255, 255, 255, 0.65)',
                    zIndex: 9999,
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                  }}
                >
                  <Spin />
                </div>
              ) : null
            }
            <IssueSidebar
              disabled={disabled}
              store={store}
              reloadIssue={this.loadIssueDetail}
              onUpdate={onUpdate}
            />
            <div className="c7n-content">
              <IssueHeader
                store={store}
                disabled={disabled}
                reloadIssue={this.loadIssueDetail}
                backUrl={backUrl}
                onCancel={onCancel}
                loginUserId={loginUserId}
                hasPermission={hasPermission}
                onUpdate={onUpdate}
                onDeleteIssue={onDeleteIssue}
              />
              <IssueBody
                disabled={disabled}
                programId={programId}
                store={store}
                reloadIssue={this.loadIssueDetail}
                onUpdate={onUpdate}
                loginUserId={loginUserId}
                hasPermission={hasPermission}
              />
            </div>
            {
              copyIssueShow ? (
                <CopyIssue
                  issueId={issueId}
                  issueNum={issueNum}
                  issue={issue}
                  issueLink={linkIssues}
                  issueSummary={summary}
                  visible={copyIssueShow}
                  onCancel={() => VisibleStore.setCopyIssueShow(false)}
                  onOk={this.handleCopyIssue.bind(this)}
                  applyType="program"
                />
              ) : null
            }
          </div>
        </ResizeAble>
      </div>
    );
  }
}
FeatureDetailShow.defaultProps = defaultProps;
export default withRouter(FeatureDetailShow);
