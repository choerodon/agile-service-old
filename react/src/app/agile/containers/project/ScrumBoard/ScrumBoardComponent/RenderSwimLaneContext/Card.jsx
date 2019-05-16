import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import {
  CardTypeTag, IssueNum, StayDay, StatusName, Priority, Assignee, Summary,
} from './CardComponent/index';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import './StatusIssue.scss';

export default class CardProvider extends Component {
  constructor(props) {
    super(props);
    this.ref = {};
  }

  handleClick = (e) => {
    const { issue } = this.props;
    e.stopPropagation();
    this.ref.style.backgroundColor = '#edeff6';
    ScrumBoardStore.setClickedIssue(issue, this.ref);
  };

  myOnMouseDown = (issue) => {
    ScrumBoardStore.setWhichCanNotDragOn(issue.statusId, issue.issueTypeDTO);
  };


  editRef = (e) => {
    this.ref = e;
  };

  render() {
    const {
      completed, issue, index, draggableId, statusName, categoryCode, onClick, clicked, ...otherProps
    } = this.props;
    return (
      <Draggable draggableId={draggableId} index={index} key={draggableId}>
        {(provided, snapshot) => {
          const onMouseDown = (() => {
            if (!provided.dragHandleProps) {
              return onMouseDown;
            }
            // creating a new onMouseDown function that calls myOnMouseDown as well as the drag handle one.
            return (event) => {
              provided.dragHandleProps.onMouseDown(event);
              this.myOnMouseDown(issue);
            };
          })();
          return (
            <div
              key={issue.issueId}
              role="none"
              className="c7n-swimlaneContext-itemBodyCard"
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              onMouseDown={onMouseDown}
            >
              <div
                className="c7n-scrumboard-issue"
                role="none"
                onClick={e => this.handleClick(e)}
                ref={this.editRef}
                {...otherProps}
                key={issue.issueNum}
              >
                <div style={{ flexGrow: 1 }}>
                  <div
                    className="c7n-scrumboard-issueTop"
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                    }}
                  >
                    <div
                      style={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        justifyContent: 'space-between',
                        flex: 1,
                        alignItems: 'center',
                      }}
                    >
                      <div
                        style={{
                          display: 'flex',
                          flexGrow: '7',
                          marginBottom: 4,
                        }}
                      >
                        <CardTypeTag issueTypeDTO={issue.issueTypeDTO} />
                        <IssueNum issueNum={issue.issueNum} completed={completed} />
                        <StayDay stayDay={issue.stayDay} completed={completed} />
                      </div>
                      <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        flexGrow: '1',
                        marginBottom: 4,
                      }}
                      >
                        <StatusName
                          categoryCode={categoryCode}
                          statusName={statusName}
                        />
                        <Priority
                          priorityDTO={issue.priorityDTO}
                        />
                      </div>
                    </div>
                    <Assignee
                      assigneeLoginName={issue.assigneeLoginName}
                      assigneeRealName={issue.assigneeRealName}
                      assigneeId={issue.assigneeId}
                      imageUrl={issue.imageUrl}
                    />
                  </div>
                  <div className="c7n-scrumboard-issueBottom">
                    <Summary summary={issue.summary} />
                  </div>
                </div>
              </div>
              {provided.placeholder}
            </div>
          );
        }}
      </Draggable>
    );
  }
}
