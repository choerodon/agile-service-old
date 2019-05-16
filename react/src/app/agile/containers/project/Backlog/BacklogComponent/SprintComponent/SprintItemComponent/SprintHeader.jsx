import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Modal } from 'choerodon-ui';
import SprintName from './SprintHeaderComponent/SprintName';
import SprintVisibleIssue from './SprintHeaderComponent/SprintVisibleIssue';
import ClearFilter from './SprintHeaderComponent/ClearAllFilter';
import SprintStatus from './SprintHeaderComponent/SprintStatus';
import AssigneeInfo from './SprintHeaderComponent/AssigneeInfo';
import AssigneeStoryPoint from './SprintHeaderComponent/AssigneeStoryPoint';
import StoryPointContainer from './SprintHeaderComponent/StoryPointContainer';
import SprintDateRange from './SprintHeaderComponent/SprintDateRange';
import SprintGoal from './SprintHeaderComponent/SprintGoal';
import '../Sprint.scss';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';

const shouldContainTypeCode = ['issue_epic', 'sub_task'];
const { confirm } = Modal;

@inject('AppState', 'HeaderStore')
@observer class SprintHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprintName: props.data.sprintName,
      startDate: props.data.startDate,
      endDate: props.data.endDate,
      sprintGoal: props.data.sprintGoal,
      objectVersionNumber: props.data.objectVersionNumber,
    };
  }

  componentWillReceiveProps(nextProps, nextContext) {
    this.setState({
      sprintName: nextProps.data.sprintName,
      startDate: nextProps.data.startDate,
      endDate: nextProps.data.endDate,
      sprintGoal: nextProps.data.sprintGoal,
      objectVersionNumber: nextProps.data.objectVersionNumber,
    });
  }

  handleBlurName = (value) => {
    if (/[^\s]+/.test(value)) {
      const { data, AppState } = this.props;
      const { objectVersionNumber } = this.state;
      const req = {
        objectVersionNumber,
        projectId: AppState.currentMenuType.id,
        sprintId: data.sprintId,
        sprintName: value,
      };
      BacklogStore.axiosUpdateSprint(req).then((res) => {
        this.setState({
          sprintName: value,
          objectVersionNumber: res.objectVersionNumber,
        });
      }).catch((error) => {
      });
    }
  };

  handleChangeDateRange = (type, dateString) => {
    const date = `${dateString} 00:00:00`;
    const { data, AppState } = this.props;
    const { objectVersionNumber } = this.state;
    const req = {
      objectVersionNumber,
      projectId: AppState.currentMenuType.id,
      sprintId: data.sprintId,
      [type]: date,
    };
    BacklogStore.axiosUpdateSprint(req).then((res) => {
      this.setState({
        objectVersionNumber: res.objectVersionNumber,
        startDate: res.startDate,
        endDate: res.endDate,
      });
    }).catch((error) => {
    });
  };

  handleChangeGoal =(value) => {
    const { store, refresh } = this.props;
    const { data, AppState } = this.props;
    const { objectVersionNumber } = this.state;
    const req = {
      objectVersionNumber,
      projectId: AppState.currentMenuType.id,
      sprintId: data.sprintId,
      sprintGoal: value,
    };
    BacklogStore.axiosUpdateSprint(req).then((res) => {
      this.setState({
        objectVersionNumber: res.objectVersionNumber,
        sprintGoal: res.sprintGoal,
      });
      refresh();
    }).catch((error) => {
    });
  };

  handleDeleteSprint = ({ key }) => {
    // const that = this;
    const { data, refresh } = this.props;

    if (key === '0') {
      if (data.issueSearchDTOList && data.issueSearchDTOList.length > 0) {
        confirm({
          width: 560,
          wrapClassName: 'deleteConfirm',
          title: `删除冲刺${data.sprintName}`,
          content: (
            <div>
              <p style={{ marginBottom: 10 }}>请确认您要删除这个冲刺。</p>
              <p style={{ marginBottom: 10 }}>这个冲刺将会被彻底删除，冲刺中的任务将会被移动到待办事项中。</p>
            </div>
          ),
          onOk() {
            return BacklogStore.axiosDeleteSprint(data.sprintId).then((res) => {
              refresh();
            }).catch((error) => {
            });
          },
          onCancel() {},
          okText: '删除',
          okType: 'danger',
        });
      } else {
        BacklogStore.axiosDeleteSprint(data.sprintId).then((res) => {
          refresh();
        }).catch((error) => {
        });
      }
    }
  };

  render() {
    const {
      data, expand, toggleSprint, sprintId, issueCount, refresh,
    } = this.props;
    const { piId } = data;
    const {
      sprintName, startDate, endDate, sprintGoal,
    } = this.state;

    return (
      <div className="c7n-backlog-sprintTop">
        <div className="c7n-backlog-springTitle">
          <div className="c7n-backlog-sprintTitleSide" style={{ flex: 1 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <SprintName
                type="sprint"
                expand={expand}
                sprintName={sprintName}
                toggleSprint={toggleSprint}
                handleBlurName={this.handleBlurName}
                data={data}
              />
              <SprintVisibleIssue
                issueCount={issueCount}
              />
            </div>
          </div>
          <div style={{ flex: 9 }}>
            <SprintStatus
              sprintId={sprintId}
              refresh={refresh}
              store={BacklogStore}
              data={data}
              statusCode={data.statusCode}
              handleDeleteSprint={this.handleDeleteSprint}
            />
          </div>
        </div>
        <div
          className="c7n-backlog-sprintDes"
          style={{
            display: data.assigneeIssues && data.assigneeIssues.length > 0 ? 'flex' : 'none',
          }}
        >
          <AssigneeInfo
            data={data}
            assigneeIssues={data.assigneeIssues}
          />
          <StoryPointContainer
            statusCode={data.statusCode}
            todoStoryPoint={data.todoStoryPoint}
            doingStoryPoint={data.doingStoryPoint}
            doneStoryPoint={data.doneStoryPoint}
          />
        </div>
        <div
          className="c7n-backlog-sprintGoal"
          style={{
            display: data.statusCode === 'started' ? 'flex' : 'none',
          }}
        >
          <SprintDateRange
            disabled={piId}
            statusCode={data.statusCode}
            startDate={startDate}
            endDate={endDate}
            handleChangeDateRange={this.handleChangeDateRange}
          />
          <SprintGoal
            sprintGoal={sprintGoal}
            handleChangeGoal={this.handleChangeGoal}
          />
        </div>
      </div>
    );
  }
}

export default SprintHeader;
