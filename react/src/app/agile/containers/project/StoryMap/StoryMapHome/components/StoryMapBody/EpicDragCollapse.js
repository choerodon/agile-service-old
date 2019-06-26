import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { DropTarget } from 'react-dnd';
import { getEmptyImage } from 'react-dnd-html5-backend';
import EpicDrag from './EpicDrag';

class EpicDragCollapse extends Component {
  componentDidMount() {
    const { connectDragPreview } = this.props;
    if (connectDragPreview) {
      // Use empty image as a drag preview so browsers don't draw it
      // and we can draw whatever we want on the custom drag layer instead.
      connectDragPreview(getEmptyImage(), {
        // IE fallback: specify that we'd rather screenshot the node
        // when it already knows it's being dragged so we can hide it with CSS.
        captureDraggingState: true,
      });
    }
  }
  
  render() {
    const { connectDragSource } = this.props;
    return connectDragSource(
      <div
        role="none"
        // onMouseDown={this.handleMouseDown}
        style={{
          position: 'absolute',
          height: '100%',
          top: 0,
          left: 0,
          width: '100%',
        }}
      />,
    );
  }
}

EpicDragCollapse.propTypes = {

};

export default EpicDrag(EpicDragCollapse);
