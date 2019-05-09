import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { stores } from 'choerodon-front-boot';
import { observer } from 'mobx-react';
import {
  Input, Icon, Popover, Checkbox,
} from 'choerodon-ui';
import { toJS } from 'mobx';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import _ from 'lodash';
import './Backlog.scss';
import '../../../../Agile.scss';
import US from '../../../../../stores/project/userMap/UserMapStore';
import TypeTag from '../../../../../components/TypeTag';

const { AppState } = stores;

@observer
class Backlog extends Component {
  constructor(props) {
    super(props);
    this.state = {
      keyword: '',
    };
  }

  componentDidMount() {
    this.loadIssues();
  }

  /**
   * filter issues by keyword
   * @param {*} keyword user input
   * @param {*} issues issues filter after fast search
   */
  getIssuesByKeyword(keyword, issues) {
    if (!keyword) return issues;
    return issues.filter(issue => issue.issueNum.indexOf(keyword) !== -1
      || issue.summary.indexOf(keyword) !== -1);
  }

  handleChangeInput = (e) => {
    this.setState({ keyword: e.target.value });
  };

  onIssueClick = (id) => {
    const { handleClickIssue } = this.props;
    if (handleClickIssue) {
      handleClickIssue(id);
    }
  };

  loadIssues() {
    US.loadBacklogIssues();
  }

  handleClickExpand(id) {
    const expand = US.backlogExpand.slice();
    const index = expand.findIndex(v => v === id);
    if (index === -1) {
      expand.push(id);
    } else {
      expand.splice(index, 1);
    }
    US.setBacklogExpand(expand);
  }

  handleClickFilter(id) {
    const currentBacklogFilters = US.currentBacklogFilters.slice();
    const index = currentBacklogFilters.findIndex(v => v === id);
    if (index === -1) {
      currentBacklogFilters.push(id);
    } else {
      currentBacklogFilters.splice(index, 1);
    }
    US.setCurrentBacklogFilter(currentBacklogFilters);
    US.loadBacklogIssues();
  }

  /**
   * load issues,
   * 1. mode none then render issue list 
   * 2. mode not none render issue group, sprint||version
   */
  renderIssues() {
    const {
      mode, backlogExpand,
    } = US;
    const { keyword } = this.state;
    let group = [];
    if (mode === 'none') {
      group = this.getIssuesByKeyword(keyword, toJS(US.backlogIssues).filter(v => v.statusCode !== 'done'));
      return (
        <div className="issues">
          <div className="title">
            <h4 className="word">问题</h4>
            <Icon
              type={backlogExpand.includes(0) ? 'expand_less' : 'expand_more'}
              onClick={this.handleClickExpand.bind(this, 0)}
            />
          </div>
          <Droppable droppableId="backlog-0_0">
            {(provided, snapshot) => (
              <div
                ref={provided.innerRef}
                className="epic"
                style={{
                  background: snapshot.isDraggingOver ? '#f0f0f0' : 'white',
                  padding: 'grid',
                  // borderBottom: '1px solid rgba(0,0,0,0.12)'
                }}
              >
                {
                  backlogExpand.includes(0) ? null : (
                    <ul className="issue-block">
                      {
                        _.map(group, (issue, index) => this.renderIssue(issue, index))
                      }
                    </ul>
                  )
                }
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </div>
      );
    } else {
      group = US[`${mode}s`];
      return (
        <div className="issues">
          {_.map(group, (v, i) => this.renderGroupIssue(v, i))}
          {this.renderUnscheduledIssue()}
        </div>
      );
    }
  }

  /**
   * render issue group, container title and filter by sprint||version issues, and unschedule issues
   * @param {*} group sprint or version array
   * @param {*} i index 
   */
  renderGroupIssue(group, i) {
    const { mode, backlogExpand } = US;
    const { keyword } = this.state;
    const issues = this.getIssuesByKeyword(keyword, toJS(US.backlogIssues).filter(v => v[`${mode}Id`] === group[`${mode}Id`]));
    return (
      <React.Fragment>
        <div className="title">
          <h4 className="word text-overflow-hidden">
            {group.name || group.sprintName}
          </h4>
          {
            issues.length ? (
              <Icon
                type={backlogExpand.includes(group[`${mode}Id`]) ? 'expand_less' : 'expand_more'}
                onClick={this.handleClickExpand.bind(this, group[`${mode}Id`])}
              />
            ) : null
          }
        </div>
        <Droppable droppableId={`backlog-0_${group[`${mode}Id`]}`}>
          {(provided, snapshot) => (
            <div
              ref={provided.innerRef}
              className="epic"
              style={{
                background: snapshot.isDraggingOver ? '#f0f0f0' : 'white',
                padding: 'grid',
                // borderBottom: '1px solid rgba(0,0,0,0.12)'
              }}
            >
              <div key={i}>
                {backlogExpand.includes(group[`${mode}Id`]) ? null : (
                  <ul className="issue-block">
                    {
                      _.map(issues, (issue, index) => this.renderIssue(issue, index))
                    }
                  </ul>
                )}
              </div>
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </React.Fragment>

    );
  }

  renderUnscheduledIssue() {
    const { mode, backlogExpand } = US;
    const { keyword } = this.state;
    const issues = this.getIssuesByKeyword(keyword, toJS(US.backlogIssues).filter(v => (v[`${mode}Id`] === null || v[`${mode}Id`] === 0) && v.statusCode !== 'done'));
    return (
      <React.Fragment>
        <div className="title">
          <h4 className="word">
            {'未规划'}
          </h4>
          <Icon
            type={backlogExpand.includes('Unscheduled') ? 'expand_less' : 'expand_more'}
            onClick={this.handleClickExpand.bind(this, 'Unscheduled')}
          />
        </div>
        <Droppable droppableId="backlog-0_0">
          {(provided, snapshot) => (
            <div
              ref={provided.innerRef}
              className="epic"
              style={{
                background: snapshot.isDraggingOver ? '#f0f0f0' : 'white',
                padding: 'grid',
                // borderBottom: '1px solid rgba(0,0,0,0.12)'
              }}
            >
              {backlogExpand.includes('Unscheduled') ? null : (
                <ul className="issue-block">
                  {
                    _.map(issues, (issue, index) => this.renderIssue(issue, index))
                  }
                </ul>
              )}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </React.Fragment>
    );
  }

  renderIssue(issue, index) {
    const { mode, selectIssueIds, currentDraggableId } = US;
    return (
      <Draggable draggableId={`${mode}-${issue.issueId}`} index={index} key={issue.issueId}>
        {(provided1, snapshot1) => (
          <div
            ref={provided1.innerRef}
            {...provided1.draggableProps}
            {...provided1.dragHandleProps}
            onClick={this.onIssueClick.bind(this, issue.issueId)}
            style={{
              cursor: 'move',
              ...provided1.draggableProps.style,
              background: selectIssueIds.includes(issue.issueId) ? 'rgb(235, 242, 249)' : 'white',
            }}
            role="none"
          >
            <li
              role="none"
              key={issue.issueId}
              className="issue"
            >
              <div style={{
                display: selectIssueIds.length > 1 && currentDraggableId === issue.issueId ? 'block' : 'none', width: 20, height: 20, color: 'white', background: '#F44336', borderRadius: '50%', textAlign: 'center', float: 'right', 
              }}
              >
                {selectIssueIds.length > 1 ? selectIssueIds.length : null}
              </div>
              <span className="type">
                <TypeTag
                  data={issue.issueTypeDTO}
                />
              </span>
              <span
                className="summary text-overflow-hidden"
                style={{
                  color: issue.statusCode === 'done' ? 'rgba(0, 0, 0, 0.6)' : '#000',
                }}
              >
                {issue.summary}
              </span>
              <span
                className="issueNum"
                style={{
                  textDecoration: issue.statusCode === 'done' ? 'line-through' : 'unset',
                  color: issue.statusCode === 'done' ? 'rgba(63, 81, 181, 0.6)' : '#3f51b5',
                }}
                role="none"
                onClick={() => {
                  const { history } = this.props;
                  const urlParams = AppState.currentMenuType;
                  history.push(
                    `/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${
                      encodeURIComponent(urlParams.name)
                    }&organizationId=${urlParams.organizationId}&paramName=${
                      issue.issueNum
                    }&paramIssueId=${issue.issueId}&paramUrl=usermap`,
                  );
                }}
              >
                {issue.issueNum}
              </span>
            </li>
          </div>

        )}
      </Draggable>

    );
  }

  render() { 
    return (
      <div className="c7n-userMap-backlog agile">
        <div className="header">
          <div className="input">
            <Input
              placeholder="按照名称搜索"
              prefix={<Icon type="search" />}
              label=""
              onChange={this.handleChangeInput}
            />
          </div>
          <Popover
            trigger="click"
            placement="bottomRight"
            overlayClassName="c7n-backlog-popover"
            content={(
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                }}
              >
                {
                  [
                    {
                      name: '仅我的问题',
                      id: 'mine',
                    },
                    {
                      name: '仅用户故事',
                      id: 'story',
                    },
                  ].map(items => (
                    <Checkbox
                      onChange={this.handleClickFilter.bind(this, items.id)}
                      checked={US.currentBacklogFilters.includes(items.id)}
                    >
                      {items.name}
                    </Checkbox>
                  ))
                }
                {
                  US.getFilters.map(filter => (
                    <Checkbox
                      onChange={this.handleClickFilter.bind(this, filter.filterId)}
                      checked={US.currentBacklogFilters.includes(filter.filterId)}
                    >
                      {filter.name}
                    </Checkbox>
                  ))
                }
              </div>
            )}
          >
            <div className="btn">
              <span>快速搜索</span>
              <Icon type="baseline-arrow_drop_down" className="icon" />
            </div>
          </Popover>
        </div>
        <div className="body">
          {this.renderIssues()}
        </div>
      </div>
    );
  }
}

export default withRouter(Backlog);
