/* eslint-disable react/destructuring-assignment */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { DragSource, DropTarget } from 'react-dnd';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';

@observer
class ProjectCard extends Component {
  render() {
    const {
      project, connectDragSource, connectDropTarget, 
    } = this.props;    
    const { projectName } = project;
    return (
      connectDragSource(
        connectDropTarget(
          <td style={{
            width: 140, minWidth: 140, textAlign: 'center', fontWeight: 500, 
          }}
          >          
            {projectName}
          </td>,
        ),
      )
    );
  }
}

ProjectCard.propTypes = {

};
export default DropTarget(
  'project',
  {
    // canDrop: (props, monitor) => {
    //   const source = monitor.getItem();
    //   const same = find(props.issues, { featureId: source.issue.featureId || source.issue.issueId });
    //   if (same && source.issue.id !== same.id) {
    //     return false;
    //   }
    //   return true;
    // },
    drop(props, monitor, component) {      
      const source = monitor.getItem();
      // if (source.id !== props.project.projectId) {
      return {
        sourceId: source.id,
        sourceIndex: source.index,
        atIndex: props.index,
      };
      // };
      // return null;
    },
    // hover(props, monitor) {
    //   const source = monitor.getItem();
    //   console.log(monitor.didDrop());
    //   console.log(source.id, props.project.boardTeamId);
    //   if (source.id !== props.project.boardTeamId) {
    //     props.moveProject(source.id, props.index);
    //   }
    // },
  },
  connect => ({
    connectDropTarget: connect.dropTarget(),
  }),
)(
  DragSource(
    'project',
    {
      beginDrag: props => ({
        id: props.project.boardTeamId,
        index: props.index,
      }),
      endDrag(props, monitor, component) {
        const source = monitor.getItem();
        // const didDrop = monitor.didDrop();
        const result = monitor.getDropResult();
        if (result) {
          const {
            sourceId, atIndex,
          } = result;

          BoardStore.projectMove(sourceId, atIndex);
        } else {
          BoardStore.resetProject(source);
        }
      },
    },
    (connect, monitor) => ({
      connectDragSource: connect.dragSource(),
      isDragging: monitor.isDragging(),
    }),
  )(ProjectCard),
);
