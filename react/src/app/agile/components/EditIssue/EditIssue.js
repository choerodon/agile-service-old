/* eslint-disable react/sort-comp */
import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { stores, axios } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { Spin } from 'choerodon-ui';
import { throttle } from 'lodash';
import './EditIssue.scss';
import {
  loadBranchs, loadDatalogs, loadLinkIssues,
  loadIssue, loadWorklogs, loadDocs, getFieldAndValue, loadIssueTypes,
} from '../../api/NewIssueApi';
import RelateStory from '../RelateStory';
import CopyIssue from '../CopyIssue';
import ResizeAble from '../ResizeAble';
import TransformSubIssue from '../TransformSubIssue';
import TransformFromSubIssue from '../TransformFromSubIssue';
import Assignee from '../Assignee';
import ChangeParent from '../ChangeParent';
import IssueSidebar from './IssueComponent/IssueSidebar';
import IssueHeader from './IssueComponent/IssueHeader';
import IssueBody from './IssueComponent/IssueBody/IssueBody';
import VisibleStore from '../../stores/common/visible/VisibleStore';
import EditIssueStore from './EditIssueStore';
import IsInProgramStore from '../../stores/common/program/IsInProgramStore';
// 项目加入群之后，不关联自己的史诗和特性，只能关联项目群的，不能改关联的史诗
const { AppState } = stores;

let loginUserId;
let hasPermission;
const store = EditIssueStore;
const defaultProps = {
  applyType: 'agile',
};
@observer
class EditIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      issueLoading: false,     
    };
    this.container = React.createRef();
    this.line = React.createRef();
  }

  componentDidMount() {
    const { onRef, issueId, applyType } = this.props;
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
      loadIssueTypes(applyType),
    ])
      .then(axios.spread((users, permission, issueTypes) => {
        loginUserId = users.id;
        hasPermission = permission[0].approve || permission[1].approve;
        store.setIssueTypes(issueTypes);
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
        loadDocs(id),
        loadWorklogs(id),
        loadDatalogs(id, programId),
        loadLinkIssues(id),
        // loadBranchs(id),
      ])
        .then(axios.spread((doc, workLogs, dataLogs, linkIssues, branches) => {
          store.initIssueAttribute(doc, workLogs, dataLogs, linkIssues, branches);
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

  handleRelateStory = () => {
    const { onUpdate } = this.props;
    VisibleStore.setRelateStoryShow(false);
    if (onUpdate) {
      onUpdate();
    }
    this.loadIssueDetail();
  };

  handleTransformSubIssue() {
    const { onUpdate } = this.props;
    VisibleStore.setTransformSubIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    this.loadIssueDetail();
  }

  handleTransformFromSubIssue() {
    const { onUpdate } = this.props;
    VisibleStore.setTransformFromSubIssueShow(false);
    if (onUpdate) {
      onUpdate();
    }
    this.loadIssueDetail();
  }

  handleResizeEnd = ({ width }) => { 
    localStorage.setItem('agile.EditIssue.width', `${width}px`);
  }

  setQuery=(width = this.container.current.clientWidth) => {
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
      onDeleteSubIssue,
      applyType,
      disabled,
    } = this.props;
    const {
      issueLoading, 
    } = this.state;
    const issue = store.getIssue;
    const {
      issueId, issueNum, summary,
      assigneeId, objectVersionNumber, createdBy, typeCode,
    } = issue;
    const linkIssues = store.getLinkIssues;

    const {
      getChangeParentShow: changeParentShow,
      getAssigneeShow: assigneeShow,
      getCopyIssueShow: copyIssueShow,
      getTransformSubIssueShow: transformSubIssueShow,
      getTransformFromSubIssueShow: transformFromSubIssueShow,
      getRelateStoryShow: relateStoryShow,
    } = VisibleStore;
    const rightDisabled = disabled || (IsInProgramStore.isInProgram && typeCode === 'issue_epic' && !hasPermission && createdBy !== AppState.userInfo.id);
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
            // maxHeight: 500,
            // minWidth: 100,
            maxWidth: 800,
            minWidth: 440,
          }}
          defaultSize={{
            width: localStorage.getItem('agile.EditIssue.width') || 605,
            height: '100%',
          }}
          onResizeEnd={this.handleResizeEnd}
          onResize={this.handleResize}
        >
          <div className="choerodon-modal-editIssue" style={style} ref={this.container}>
            <div className="choerodon-modal-editIssue-divider" />
            {/* <div className="choerodon-modal-editIssue-divider-line" ref={this.line} /> */}
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
              disabled={rightDisabled}
              store={store}
              reloadIssue={this.loadIssueDetail}
              onUpdate={onUpdate}
            />
            <div className="c7n-content">
              <IssueHeader
                disabled={rightDisabled}
                store={store}
                reloadIssue={this.loadIssueDetail}
                backUrl={backUrl}
                onCancel={onCancel}
                loginUserId={loginUserId}
                hasPermission={hasPermission}
                onDeleteIssue={onDeleteIssue}
                onUpdate={onUpdate}
              />
              <IssueBody
                disabled={rightDisabled}
                store={store}
                reloadIssue={this.loadIssueDetail}
                onUpdate={onUpdate}
                onDeleteSubIssue={onDeleteSubIssue}
                loginUserId={loginUserId}
                hasPermission={hasPermission}
                applyType={applyType}
                // programId={programId}
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
                  applyType={applyType}
                />
              ) : null
            }
            {
              relateStoryShow ? (
                <RelateStory
                  issue={issue}
                  visible={relateStoryShow}
                  onCancel={() => VisibleStore.setRelateStoryShow(false)}
                  onOk={this.handleRelateStory.bind(this)}
                />
              ) : null
            }
            {
              transformSubIssueShow ? (
                <TransformSubIssue
                  visible={transformSubIssueShow}
                  issueId={issueId}
                  issueNum={issueNum}
                  ovn={objectVersionNumber}
                  onCancel={() => VisibleStore.setTransformSubIssueShow(false)}
                  onOk={this.handleTransformSubIssue.bind(this)}
                  store={store}
                />
              ) : null
            }
            {
              transformFromSubIssueShow ? (
                <TransformFromSubIssue
                  visible={transformFromSubIssueShow}
                  issueId={issueId}
                  issueNum={issueNum}
                  ovn={objectVersionNumber}
                  onCancel={() => VisibleStore.setTransformFromSubIssueShow(false)}
                  onOk={this.handleTransformFromSubIssue.bind(this)}
                  store={store}
                />
              ) : null
            }

            {
              assigneeShow ? (
                <Assignee
                  issueId={issueId}
                  issueNum={issueNum}
                  visible={assigneeShow}
                  assigneeId={assigneeId}
                  objectVersionNumber={objectVersionNumber}
                  onOk={() => {
                    VisibleStore.setAssigneeShow(false);
                    if (onUpdate) {
                      onUpdate();
                    }
                    this.loadIssueDetail(issueId);
                  }}
                  onCancel={() => {
                    VisibleStore.setAssigneeShow(false);
                  }}
                />
              ) : null
            }
            {
              changeParentShow ? (
                <ChangeParent
                  issueId={issueId}
                  issueNum={issueNum}
                  visible={changeParentShow}
                  objectVersionNumber={objectVersionNumber}
                  onOk={() => {
                    VisibleStore.setChangeParentShow(false);
                    this.loadIssueDetail(issueId);
                  }}
                  onCancel={() => {
                    VisibleStore.setChangeParentShow(false);
                  }}
                />
              ) : null
            }
          </div>
        </ResizeAble>
      </div>
    );
  }
}
EditIssue.defaultProps = defaultProps;
export default withRouter(EditIssue);
