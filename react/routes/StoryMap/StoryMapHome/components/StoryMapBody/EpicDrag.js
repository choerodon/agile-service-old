import { DragSource } from 'react-dnd';
import StoryMapStore from '../../../../../stores/project/StoryMap/StoryMapStore';

const EpicDrag = Component => DragSource(
  'epic',
  {
    beginDrag: (props, monitor, component) => {
      if (component && component.resetZIndex) {
        component.setZIndex();
        setTimeout(() => {
          component.resetZIndex();
        });
      }
  
      return { epic: props.epic, index: props.index };
    },
    endDrag(props, monitor) {
      const item = monitor.getItem();
      const dropResult = monitor.getDropResult();
      if (!dropResult) {
        return;
      }
      const { epic, index } = item;
      const { epic: targetEpic, index: targetIndex } = dropResult;
      // console.log({
      //   source: epic,
      //   destination: targetEpic,
      // });
      if (index === targetIndex || index === targetIndex - 1) {
        return; 
      }
      StoryMapStore.sortEpic(epic, targetEpic, index, targetIndex);
    },
  },
  (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    connectDragPreview: connect.dragPreview(),
    isDragging: monitor.isDragging(),
  }),
)(Component);

export default EpicDrag;
