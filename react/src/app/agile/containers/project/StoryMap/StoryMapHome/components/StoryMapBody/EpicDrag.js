import { DragSource } from 'react-dnd';

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
  
      return { story: props.story, version: props.version };
    },
    endDrag(props, monitor) {
        
    },
  },
  (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    connectDragPreview: connect.dragPreview(),
    isDragging: monitor.isDragging(),
  }),
)(Component);

export default EpicDrag;
