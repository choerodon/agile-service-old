/* eslint-disable consistent-return */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Tooltip, Icon, Popconfirm } from 'choerodon-ui';
import { find } from 'lodash';
import { observer } from 'mobx-react';
import { DragSource, DropTarget } from 'react-dnd';
import TypeTag from '../../../../../../components/TypeTag';
import { CardHeight, CardWidth, CardMargin } from '../Constants';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';
import './IssueCard.scss';

@observer
class IssueCard extends Component {
  handleSelect = (e) => {
    e.stopPropagation();
    const { issue } = this.props;
    BoardStore.setClickIssue(issue);
  }

  handleMouseUp = () => {
    if (BoardStore.addingConnection) {
      const { issue } = this.props;
      BoardStore.createConnection(issue);
      // console.log(this.props.issue, BoardStore.clickIssue);
    } 
    this.resetZIndex();
  }

  handleMouseDown = () => {
    this.container.style.zIndex = 9999;    
  }

  resetZIndex=() => {
    this.container.style.zIndex = 'unset';   
  }

  handleDelete = (e) => {
    e.stopPropagation();
    const { issue } = this.props;
    BoardStore.deleteFeatureFromBoard(issue);
  }

  render() {
    const {
      issue, isDragging, connectDragSource, connectDropTarget, mode,
    } = this.props;    

    const {
      issueTypeDTO, summary, issueNum, featureType,
    } = issue;
    return (
      connectDragSource(
        connectDropTarget(
          <div
            role="none"
            ref={(container) => { this.container = container; }}
            onClick={this.handleSelect}
            onMouseUp={this.handleMouseUp}
            onMouseDown={this.handleMouseDown}         
            style={{
              // zIndex,
              height: CardHeight,
              width: CardWidth,
              margin: CardMargin,
            }}
            className={`c7nagile-IssueCard ${mode}`}
          >
            <div role="none" className="c7nagile-IssueCard-top" onClick={(e) => { e.stopPropagation(); }}>
              <TypeTag data={issueTypeDTO} featureType={featureType} />
              <span className="c7nagile-IssueCard-top-issueNum">
                {issueNum}
              </span>
              <Popconfirm
                title="确认要移除此特性吗?"
                onConfirm={this.handleDelete}
                okText="确定"
                cancelText="取消"
              >
                <Icon className="c7nagile-IssueCard-top-delete" type="delete" />
              </Popconfirm>

            </div>
            {isDragging ? (
              <div className="c7nagile-IssueCard-summary">
                {summary}
              </div>
            ) : (
              <Tooltip title={summary}>
                <div className="c7nagile-IssueCard-summary">
                  {summary}
                </div>
              </Tooltip>
            )}
          </div>,
        ),
      )
    );
  }
}

IssueCard.propTypes = {

};

export default DropTarget(
  'card',
  {
    canDrop: (props, monitor) => {
      const source = monitor.getItem();
      const same = find(props.issues, { featureId: source.issue.featureId || source.issue.issueId });
      if (same && source.issue.id !== same.id) {
        return false;
      }
      return true;
    },
    drop(props, monitor, component) {
      // console.log(monitor.canDrop());
      if (!monitor.canDrop()) {
        return;
      }
      return {
        dropType: 'inner',
        index: props.index,
        teamProjectId: props.projectId,
        sprintId: props.sprintId,
      };
    },
    hover(props, monitor) {
      // console.log(monitor.canDrop());
      if (!monitor.canDrop() || !monitor.isOver({ shallow: true })) {
        return;
      }
      const source = monitor.getItem();
      const { issue: { id, featureId } } = props;
      // console.log(source.boardFeatureId, boardFeatureId);
      if (source.id !== id && source.issue.issueId !== featureId && source.issue.featureId !== featureId) {
        props.moveCard(source, {
          index: props.index,
          sprintId: props.sprintId,
          projectId: props.projectId,
        });
      }
      return props;
    },
  },
  connect => ({
    connectDropTarget: connect.dropTarget(),
  }),
)(
  DragSource(
    'card',
    {
      beginDrag: props => ({
        id: props.issue.id,
        issue: props.issue,
        index: props.index,
        sprintId: props.sprintId,
        projectId: props.projectId,
      }),
      endDrag(props, monitor, component) {
        component.resetZIndex();
        const source = monitor.getItem();
        const didDrop = monitor.didDrop();
        const result = monitor.getDropResult();
        if (result) {
          const {
            dropType, teamProjectId, sprintId, index,
          } = result;

          BoardStore.featureBoardMove({
            dropType,
            index,
            teamProjectId,
            sprintId,
            issue: source.issue,
          });
        } else {
          BoardStore.resetMovingIssue(source);
        }
      },
    },
    (connect, monitor) => ({
      connectDragSource: connect.dragSource(),
      isDragging: monitor.isDragging(),
    }),
  )(IssueCard),
);
