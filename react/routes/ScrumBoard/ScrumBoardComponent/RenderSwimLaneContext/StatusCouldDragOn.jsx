import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import classnames from 'classnames';
import ScrumBoardStore from '../../../../stores/project/scrumBoard/ScrumBoardStore';

@observer
class StatusCouldDragOn extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { cantDragOn } = this.props;
    return nextProps.cantDragOn !== cantDragOn;
  }

  render() {
    const { statusId, swimlaneId } = this.props;
    const cantDragOn = ScrumBoardStore.getCanDragOn.get(statusId);
    const { draggingSwimlane, draggingStart } = ScrumBoardStore.getIsDragging;
    return (
      <div className={cantDragOn && draggingStart && (swimlaneId === draggingSwimlane) ? 'statusCantDragOn' : ''} />
    );
  }
}

export default StatusCouldDragOn;
