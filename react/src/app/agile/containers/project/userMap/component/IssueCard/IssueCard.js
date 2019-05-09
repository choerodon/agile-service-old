import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { Input, Icon, Modal } from 'choerodon-ui';
import { stores } from 'choerodon-front-boot';
import { Draggable } from 'react-beautiful-dnd';
import _ from 'lodash';
import './IssueCard.scss';
import US from '../../../../../stores/project/userMap/UserMapStore';
import PriorityTag from '../../../../../components/PriorityTag';
import StatusTag from '../../../../../components/StatusTag';
import TypeTag from '../../../../../components/TypeTag';
import UserHead from '../../../../../components/UserHead';
import { updateIssue } from '../../../../../api/NewIssueApi';

const { TextArea } = Input;
const { AppState } = stores;
const { confirm } = Modal;

class IssueCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      issue: {},
      summary: '',
      originSummary: '',
      isFocus: false,
    };
  }

  componentDidMount() {
    this.setIssueInState();
  }

  shouldComponentUpdate(nextProps, nextState) {
    const {
      selected, dragged, index, issue: { issueId, objectVersionNumber }, showDelete,
    } = this.props;
    const { summary, isFocus } = this.state;
    if (nextProps.issue.issueId === issueId
      && nextProps.issue.objectVersionNumber === objectVersionNumber
      && nextProps.selected === selected
      && nextProps.dragged === dragged
      && nextProps.index === index
      && nextState.summary === summary
      && nextState.isFocus === isFocus
      && nextState.showDelete === showDelete
    ) {
      return false;
    }
    return true;
  }

  setIssueInState() {
    const { issue } = this.props;
    this.setState({
      issue,
      summary: issue.summary,
      originSummary: issue.summary,
    });
  }

  handleClickTextArea = (e) => {
    if (e.defaultPrevented) return;

    e.stopPropagation();

    const { isFocus } = this.state;
    if (!isFocus) {
      const { target } = e;
      target.focus();
      target.select();
      this.setState({ isFocus: true });
    }
  }

  handleIssueNameChange = (e) => {
    this.setState({ summary: e.target.value });
  }

  handlePressEnter = (e) => {
    e.preventDefault();

    const { summary } = this.state;
    if (!summary) {
      return;
    }
    e.target.blur();
  }

  updateIssueName = (e) => {
    const { issue, handleUpdateIssueName } = this.props;
    const { issueId, objectVersionNumber } = issue;
    const { summary, originSummary } = this.state;

    e.preventDefault();
    this.setState({ isFocus: false });

    if (!summary) {
      this.setState({ summary: originSummary });
      return;
    }
    if (summary === originSummary) {
      return;
    }

    const obj = {
      issueId,
      objectVersionNumber,
      summary,
    };
    updateIssue(obj)
      .then((res) => {
        if (handleUpdateIssueName) {
          handleUpdateIssueName();
        }
        US.freshIssue(issueId, res.objectVersionNumber, res.summary);
      });
  }

  onIssueClick = (id) => {
    const { handleClickIssue } = this.props;
    handleClickIssue(id);
  };

  handleClickDelete() {
    const { issue: { issueId } } = this.state;
    confirm({
      width: 560,
      wrapClassName: 'deleteConfirm',
      title: '移除问题',
      content: (
        <div>
          <p style={{ marginBottom: 10 }}>请确认您要取消问题与史诗的关联。</p>
          <p style={{ marginBottom: 10 }}>这个操作将取消当前问题与史诗的关联，并从用户故事地图中移除，移除的问题将置于需求池的未规划部分。</p>
        </div>
      ),
      onOk() {
        US.deleteIssue(issueId);
      },
      onCancel() {},
      okText: '移除',
      okType: 'danger',
    });
  }

  exitFullScreen() {
    if (document.exitFullscreen) {
      document.exitFullscreen();
    } else if (document.msExitFullscreen) {
      document.msExitFullscreen();
    } else if (document.mozCancelFullScreen) {
      document.mozCancelFullScreen();
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen();
    }
  }

  getIssueCountBackground() {
    const { issue } = this.state;
    const issueType =  issue.statusMapDTO.type;
    switch(issueType) {
      case 'todo': {
        return 'rgb(77, 144, 254)'
      };
      case 'doing': {
        return 'rgb(255, 177, 0)';
      };
      case 'done': {
        return 'rgb(0, 191, 165)';
      };
      default: {
        return 'rgba(0, 0, 0, 0.26)';
      }
    }
  }

  render() {
    const {
      issue, borderTop, history, selected, dragged, draggableId, index, showDelete,
    } = this.props;
    const {
      isFocus,
      summary,
      issue: {
        issueId,
        statusCode,
        assigneeId,
        assigneeName,
        imageUrl,
        priorityDTO,
        typeCode,
        issueTypeDTO,
        storyPoints,
        statusName,
        statusColor,
        statusMapDTO,
        issueNum,
      },
    } = this.state;
    const selectIssueIds = US.getSelectIssueIds;

    return (
      <Draggable
        draggableId={draggableId}
        index={index}
        key={draggableId}
        disableInteractiveElementBlocking={!isFocus}
      >
        {provided1 => (
          <div
            ref={provided1.innerRef}
            {...provided1.draggableProps}
            {...provided1.dragHandleProps}
            style={{
              cursor: 'move',
              ...provided1.draggableProps.style,
            }}
            role="none"
          >
            {/* {issue.issueId} */}
            <div
              role="none"
              style={{
                background: selected ? 'rgb(235, 242, 249)' : '',
                borderTop: borderTop ? '1px solid rgba(0, 0, 0, 0.2)' : 'unset',
              }}
              className="c7n-userMap-issueCard"
              onClick={this.onIssueClick.bind(this, issue.issueId, issue.epicId)}
            >
              <div
                style={{
                  display: selectIssueIds.length > 1 && dragged ? 'block' : 'none',
                  width: 20,
                  height: 20,
                  color: 'white',
                  background: '#F44336',
                  borderRadius: '50%',
                  textAlign: 'center',
                  float: 'right',
                }}
              >
                {selectIssueIds.length}
              </div>
              <div
                className="c7n-mask"
                style={{
                  display: statusMapDTO && statusMapDTO.type === 'done' && !isFocus ? 'block' : 'none',
                }}
              />
              <div className="c7n-header">
                <div className="c7n-headerLeft">
                  <UserHead
                    user={{
                      id: assigneeId,
                      loginName: '',
                      realName: assigneeName,
                      avatar: imageUrl,
                    }}
                    hiddenText
                    size={30}
                  />
                  <span
                    className="c7n-issueNum"
                    role="none"
                    onClick={() => {
                      const urlParams = AppState.currentMenuType;
                      const isFullScreen = document.webkitFullscreenElement
                      || document.mozFullScreenElement
                      || document.msFullscreenElement;
                      if (isFullScreen) {
                        this.exitFullScreen();
                      }
                      history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${issueNum}&paramIssueId=${issueId}&paramOpenIssueId=${issueId}&paramUrl=usermap`);
                    }}
                  >
                    {issueNum}
                  </span>
                  <PriorityTag
                    priority={priorityDTO}
                  />
                </div>
                {showDelete
                  ? (
                    <Icon
                      className="c7n-delete"
                      type="delete"
                      onClick={this.handleClickDelete.bind(this)}
                    />
                  ) : ''
                }
              </div>

              <div className="c7n-content">
                <TextArea
                  className="c7n-textArea"
                  autosize={{ minRows: 1, maxRows: 10 }}
                  value={summary}
                  onChange={this.handleIssueNameChange.bind(this)}
                  onPressEnter={this.handlePressEnter}
                  onDoubleClick={this.handleClickTextArea}
                  onBlur={this.updateIssueName}
                  spellCheck="false"
                  maxLength={44}
                />
              </div>
              <div className="c7n-footer">
                <TypeTag
                  data={issueTypeDTO}
                  style={{ margin: 4, marginLeft: 0 }}
                />
                <StatusTag
                  name={statusName}
                  color={statusColor}
                  data={statusMapDTO}
                  style={{ margin: 4 }}
                />
                {
                  issueTypeDTO && issueTypeDTO.typeCode === 'story' && storyPoints
                    ? (
                      <span className="c7n-issueCard-storyPoints" style={{ background: this.getIssueCountBackground()}}>
                        {storyPoints}
                      </span>
                    )
                    : null
                }
              </div>
            </div>
          </div>
        )}
      </Draggable>
    );
  }
}
export default withRouter(IssueCard);
