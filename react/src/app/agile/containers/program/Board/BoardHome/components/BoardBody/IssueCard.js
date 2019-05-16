import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { find } from 'lodash';
import { observer } from 'mobx-react';
import { DragSource, DropTarget } from 'react-dnd';
import { CardHeight, CardWidth, CardMargin } from '../Constants';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';
import './IssueCard.scss';


@observer
class IssueCard extends Component {
  render() {
    const {
      issue, isDragging, connectDragSource, connectDropTarget,
    } = this.props;
    const opacity = isDragging ? 0 : 1;
    return (
      connectDragSource(
        connectDropTarget(
          <div
            style={{
              // opacity, 
              height: CardHeight,
              width: CardWidth,
              margin: CardMargin,
            }}
            className="c7nagile-IssueCard"
          >
            <div className="c7nagile-IssueCard-inner">
              {issue.summary}
            </div>
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
      console.log(props, monitor.getDropResult(), component);
      console.log(monitor.canDrop());
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
      console.log(monitor.canDrop());
      if (!monitor.canDrop()) {
        return;
      }
      const source = monitor.getItem();
      const { issue: { id } } = props;
      // console.log(source.boardFeatureId, boardFeatureId);
      if (source.id !== id) {
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
      endDrag(props, monitor) {
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
