import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Button, Icon, Progress, Input,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';
import { injectIntl } from 'react-intl';
import { createSubIssue, createIssueField } from '../../../../api/NewIssueApi';
import CreateSubTask from '../../../CreateSubTask';
import IssueList from '../../Component/IssueList';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';
import './SubTask.scss';

const { AppState } = stores;

@observer class SubTask extends Component {
  constructor(props) {
    super(props);
    this.sign = false;
    this.state = {
      expand: false,
      summary: false,
    };
  }

  componentDidMount() {
  }

  /**
   * IssueList
   * @param {*} issue
   * @param {*} i
   */
  renderIssueList = (issue, i) => {
    const { reloadIssue, store, onDeleteSubIssue } = this.props;
    const { issueId: id } = store.getIssue;
    return (
      <IssueList
        showAssignee
        key={issue.issueId}
        issue={{
          ...issue,
          typeCode: issue.typeCode || 'sub_task',
        }}
        i={i}
        onOpen={() => {
          if (reloadIssue) {
            reloadIssue(issue.issueId);
          }
        }}
        onRefresh={() => {
          if (reloadIssue) {
            reloadIssue(id);
          }
          if (onDeleteSubIssue) {
            onDeleteSubIssue();
          }
        }}
      />
    );
  };

  renderSubIssues = () => {
    const { store } = this.props;
    const { subIssueDTOList = [] } = store.getIssue;
    return (
      <div className="c7n-tasks">
        {
          subIssueDTOList.map((subIssue, i) => this.renderIssueList(subIssue, i))
        }
      </div>
    );
  };

  handleCreateSubIssue = () => {
    const { onUpdate, reloadIssue } = this.props;
    VisibleStore.setCreateSubTaskShow(false);
    if (onUpdate) {
      onUpdate();
    }
    if (reloadIssue) {
      reloadIssue();
    }
  };

  getPercent = () => {
    const { store } = this.props;
    const { subIssueDTOList = [] } = store.getIssue;
    const completeList = subIssueDTOList.filter(issue => issue.completed);
    const allLength = (subIssueDTOList && subIssueDTOList.length) || 0;
    const completeLength = completeList.length;
    if (allLength === 0) {
      return 100;
    } else {
      return parseInt(completeLength / allLength * 100, 10);
    }
  };

  handleCancel = () => {
    this.setState({
      expand: false,
      summary: false,
    });
  };

  handleSave = () => {
    const { store } = this.props;
    const { summary } = this.state;
    const { issueId, priorityId, sprintId } = store.getIssue;
    const subIssueType = store.getIssueTypes && store.getIssueTypes.find(t => t.typeCode === 'sub_task');
    if (summary) {
      const issue = {
        summary,
        priorityId,
        priorityCode: `priority-${priorityId}`,
        projectId: AppState.currentMenuType.id,
        parentIssueId: issueId,
        sprintId,
        issueTypeId: subIssueType && subIssueType.id,
      };
      createSubIssue(issueId, issue)
        .then((res) => {
          const dto = {
            schemeCode: 'agile_issue',
            context: subIssueType && subIssueType.typeCode,
            pageCode: 'agile_issue_create',
          };
          createIssueField(res.issueId, dto);
          this.handleCancel();
          this.handleCreateSubIssue();
        })
        .catch(() => {
        });
    } else {
      Choerodon.prompt('子任务概要不能为空！');
    }
  };

  onSummaryChange = (e) => {
    this.setState({
      summary: e.target && e.target.value && e.target.value.trim(),
    });
  };

  render() {
    const { expand } = this.state;
    const { store } = this.props;
    const { issueId, summary } = store.getIssue;
    const { getCreateSubTaskShow: createSubTaskShow } = VisibleStore;
    const { subIssueDTOList = [] } = store.getIssue;
    return (
      <div id="sub_task">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="filter_none c7n-icon-title" />
            <span>子任务</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => VisibleStore.setCreateSubTaskShow(true)}>
              <Icon type="playlist_add icon" />
            </Button>
          </div>
        </div>
        {subIssueDTOList && subIssueDTOList.length
          ? (
            <div className="c7n-subTask-progress">
              <Progress percent={this.getPercent()} />
            </div>
          ) : ''
        }
        {this.renderSubIssues()}
        <div className="c7n-subTask-quickCreate">
          {expand
            ? (
              <span style={{ position: 'relative' }}>
                <Input
                  placeholder="在此输入子任务概要"
                  maxLength={44}
                  onChange={this.onSummaryChange}
                />
                <div style={{
                  textAlign: 'right',
                  lineHeight: '20px',
                  position: 'absolute',
                  right: '0px',
                }}
                >
                  <Icon type="done" className="c7n-subTask-icon" onClick={this.handleSave} />
                  <Icon type="close" className="c7n-subTask-icon" onClick={this.handleCancel} />
                </div>
              </span>
            ) : (
              <Button
                className="leftBtn"
                functyp="flat"
                onClick={() => {
                  this.setState({
                    expand: true,
                  });
                }}
              >
                <Icon type="playlist_add" />
                {'创建问题'}
              </Button>
            )
          }
        </div>
        {
          createSubTaskShow ? (
            <CreateSubTask
              issueId={issueId}
              parentSummary={summary}
              visible={createSubTaskShow}
              onCancel={() => VisibleStore.setCreateSubTaskShow(false)}
              onOk={this.handleCreateSubIssue}
              store={store}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(SubTask));
