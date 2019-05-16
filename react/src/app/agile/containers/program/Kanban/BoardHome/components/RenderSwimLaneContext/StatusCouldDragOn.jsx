import React, { Component } from 'react';
import { observer } from 'mobx-react';

import KanbanStore from '../../../../../../stores/program/Kanban/KanbanStore';

@observer
class StatusCouldDragOn extends Component {
  shouldComponentUpdate(nextProps) {
    const { cantDragOn } = this.props;
    return nextProps.cantDragOn !== cantDragOn;
  }

  render() {
    const { statusId } = this.props;
    const cantDragOn = KanbanStore.getCanDragOn.get(statusId);
    const hasActivePi = KanbanStore.getActivePi;
    const isDragging = KanbanStore.getIsDragging;
    return (
      <div className={cantDragOn && isDragging ? 'statusCantDragOn' : ''} />
    );
  }
}

export default StatusCouldDragOn;
