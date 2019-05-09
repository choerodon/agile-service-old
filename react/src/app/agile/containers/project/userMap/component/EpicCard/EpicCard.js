import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { Input, message } from 'choerodon-ui';
import { axios, stores } from 'choerodon-front-boot';
import { Draggable } from 'react-beautiful-dnd';
import './EpicCard.scss';
import StatusTag from '../../../../../components/StatusTag';
import TypeTag from '../../../../../components/TypeTag';
import { updateIssue } from '../../../../../api/NewIssueApi';
import US from '../../../../../stores/project/userMap/UserMapStore';

const { AppState } = stores;
const { TextArea } = Input;

class EpicCard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      epicName: '',
      originEpicName: '',
      isEdit: false,
    };
  }

  componentDidMount() {
    this.setEpicNameInState();
  }

  shouldComponentUpdate(nextProps, nextState) {
    const { epic, epic: { issueId, objectVersionNumber }, index } = this.props;
    const { epicName, isEdit } = this.state;
    if (nextProps.epic.issueId === issueId
      && nextProps.epic.epicName === epic.epicName
      && nextProps.epic.objectVersionNumber === objectVersionNumber
      && nextProps.index === index
      && nextState.epicName === epicName
      && nextState.isEdit === isEdit
    ) {
      return false;
    }
    return true;
  }

  setEpicNameInState(ep) {
    let e = ep;
    if (!e) {
      const { epic } = this.props;
      e = epic;
    }
    const { epicName } = e;
    this.setState({
      epicName,
      originEpicName: epicName,
    });
  }

  handleClickTextArea = (e) => {
    if (e.defaultPrevented) return;
    
    const { isEdit } = this.state;
    if (!isEdit) {
      const { target } = e;
      target.focus();
      target.select();
      this.setState({ isEdit: true });
    }
  }

  handleEpicNameChange = (e) => {
    this.setState({ epicName: e.target.value });
  }

  handlePressEnter = (e) => {
    e.preventDefault();
    const { target } = e;
    target.blur();
  };

  updateEpicName = (e) => {
    const { epicName, originEpicName } = this.state;
    const { handleUpdateEpicName } = this.props;
    if (!epicName) {
      this.setState({
        epicName: originEpicName,
        isEdit: false,
      });
      return;
    }
    if (epicName === originEpicName) {
      this.setState({
        isEdit: false,
      });
      return;
    }

    e.preventDefault();
    axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/check_epic_name?epicName=${e.target.value}`)
      .then((checkRes) => {
        if (checkRes) {
          Choerodon.prompt('史诗名称重复');
          setTimeout(() => {
            this.textArea.focus();
          }, 0);
        } else {
          const { epic } = this.props;
          const { issueId, objectVersionNumber } = epic;
          if (!epicName) return;
          const obj = {
            issueId,
            epicName,
            objectVersionNumber,
          };
          updateIssue(obj).then((res) => {
            this.setState({
              isEdit: false,
              originEpicName: epicName,
            });
            US.modifyEpic(issueId, res.epicName, res.objectVersionNumber);
            if (handleUpdateEpicName) {
              handleUpdateEpicName();
            }
          });
        }
      });
  };

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

  getEpicIssueCountBackground() {
    const { epic } = this.props;
    const epicType = epic.statusMapDTO && epic.statusMapDTO.type;
    switch (epicType) {
      case 'todo': {
        return 'rgb(77, 144, 254)';
      }
      case 'doing': {
        return 'rgb(255, 177, 0)';
      }
      case 'done': {
        return 'rgb(0, 191, 165)';
      }
      default: {
        return 'rgba(0, 0, 0, 0.26)';
      }
    }
  }

  render() {
    const { epic, index } = this.props;
    const { isEdit, epicName } = this.state;
    const progress = !epic.issueCount ? 0 : epic.doneIssueCount / epic.issueCount;
    return (
      <Draggable
        draggableId={String(index)}
        index={index}
        disableInteractiveElementBlocking={!isEdit}
      >
        {provided1 => (
          <div
            ref={provided1.innerRef}
            {...provided1.draggableProps}
            {...provided1.dragHandleProps}
            style={{
              // marginRight: 10,
              paddingLeft: 0,
              cursor: 'move',
              background: 'white',
              ...provided1.draggableProps.style,
            }}
            role="none"
          >
            <div className="c7n-userMap-epicCard">
              <div
                className="c7n-progress"
                style={{
                  background: epic.color,
                }}
              />
              <div
                className="c7n-bar"
                style={{
                  background: epic.color,
                  width: `${progress * 100}%`,
                }}
              />
              <div
                className="c7n-content"
              >
                <TextArea
                  className="c7n-textArea"
                  autosize={{ minRows: 1, maxRows: 1 }}
                  value={epicName}
                  ref={(textArea) => { this.textArea = textArea; }}
                  onChange={this.handleEpicNameChange.bind(this)}
                  onPressEnter={this.handlePressEnter}
                  onDoubleClick={this.handleClickTextArea}
                  role="none"
                  onBlur={this.updateEpicName}
                  spellCheck="false"
                  maxLength={10}
                />
              </div>
              <div className="c7n-footer">
                <div className="c7n-footer-left">
                  <TypeTag
                    style={{ marginRight: 8 }}
                    data={epic.issueTypeDTO}
                  />
                  <StatusTag
                    data={epic.statusMapDTO}
                    name={epic.statusName}
                    color={epic.statusColor}
                  />
                  <span className="c7n-issueCount" style={{ background: this.getEpicIssueCountBackground() }}>{epic.totalEstimate}</span>
                </div>
                <span
                  className="c7n-issueNum"
                  role="none"
                  onClick={() => {
                    const { history } = this.props;
                    const urlParams = AppState.currentMenuType;
                    const isFullScreen = document.webkitFullscreenElement
                      || document.mozFullScreenElement
                      || document.msFullscreenElement;
                    if (isFullScreen) {
                      this.exitFullScreen();
                    }
                    history.push(`/agile/issue?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&paramName=${epic.issueNum}&paramIssueId=${epic.issueId}&paramUrl=usermap`);
                  }}
                >
                  {epic.issueNum}
                </span>
              </div>
            </div>
          </div>
        )}
      </Draggable>
    );
  }
}
export default withRouter(EpicCard);
