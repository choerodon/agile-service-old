import React, { Component } from 'react';
import { Draggable } from 'react-beautiful-dnd';
import {
  CardTypeTag, IssueNum, StatusName, Summary,
} from './CardComponent';
import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';
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
    KanbanStore.setClickedIssue(issue, this.ref);
  };

  myOnMouseDown = (issue) => {
    KanbanStore.setWhichCanNotDragOn(issue.statusId, issue.issueTypeDTO);
  };


  editRef = (e) => {
    this.ref = e;
  };

  render() {
    const {
      completed, issue, index, draggableId, statusName, categoryCode, onClick, clicked, ...otherProps
    } = this.props;
    const {
      featureType, issueId, issueNum, issueTypeDTO,
    } = issue;
    return (
      <Draggable draggableId={draggableId} index={index} key={draggableId}>
        {(provided) => {
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
              key={issueId}
              role="none"
              className="c7n-swimlaneContext-itemBodyCard"
              ref={provided.innerRef}
              {...provided.draggableProps}
              {...provided.dragHandleProps}
              onMouseDown={onMouseDown}
            >
              <div
                className="c7n-board-issue"
                role="none"
                onClick={e => this.handleClick(e)}
                ref={this.editRef}
                {...otherProps}
                key={issue.issueNum}
              >
                <div style={{ flexGrow: 1 }}>
                  <div
                    className="c7n-board-issueTop"
                    style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                    }}
                  >
                    <div
                      style={{
                        display: 'flex',                     
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
                        <CardTypeTag issueTypeDTO={{ ...issueTypeDTO, colour: featureType === 'enabler' ? '#FFCA28' : '#29B6F6' }} />
                        <IssueNum issueNum={issueNum} completed={completed} />
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
                      </div>
                    </div>
                  </div>
                  <div className="c7n-board-issueBottom">
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
