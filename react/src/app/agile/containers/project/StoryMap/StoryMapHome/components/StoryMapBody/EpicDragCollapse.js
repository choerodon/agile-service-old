import React, { Component, Fragment } from 'react';
import { findDOMNode } from 'react-dom';
import PropTypes from 'prop-types';
import { DropTarget, DragPreviewImage } from 'react-dnd';
import { getEmptyImage } from 'react-dnd-html5-backend';
import EpicDrag from './EpicDrag';
import EpicCard from './EpicCard';
import dom2canvas from './dom2canvas';
// import img from '../../../../../../assets/noPI.svg';


class EpicDragCollapse extends Component {
  state={
    src: '',
  }

  componentDidMount() {
    // eslint-disable-next-line react/no-find-dom-node
    const src = dom2canvas(findDOMNode(this.CardPreview));
    this.setState({
      src,
    });
  }
  
  render() {
    const { connectDragSource, connectDragPreview } = this.props;
    const { src } = this.state;
    return connectDragSource(      
      <div
        role="none"      
        style={{
          position: 'absolute',
          height: '100%',
          top: 0,
          left: 0,
          width: '100%',
        }}
      >
        <DragPreviewImage connect={connectDragPreview} src={src} />
        {/* 隐藏元素,用来生成preview */}
        <div style={{ position: 'fixed', top: -9999, left: -9999 }}>
          <EpicCard ref={(CardPreview) => { this.CardPreview = CardPreview; }} {...this.props} />
        </div>
      </div>,     
    );
  }
}

EpicDragCollapse.propTypes = {

};

export default EpicDrag(EpicDragCollapse);
