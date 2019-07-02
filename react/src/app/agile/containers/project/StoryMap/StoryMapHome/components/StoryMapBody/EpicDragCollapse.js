import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import EpicDrag from './EpicDrag';
import EpicCard from './EpicCard';
import dom2canvas from './dom2canvas';

const DragPreviewImage = React.memo(({ connect, src }) => {
  const img = new Image();
  img.src = src;
  img.onload = function () { return connect(img); };
  return null;
});

class EpicDragCollapse extends Component {
  constructor() {
    super();
    this.CardPreview = React.createRef();
    this.state = {
      src: '',
    };
  }
  

  componentDidMount() {
    // eslint-disable-next-line react/no-find-dom-node
    setTimeout(() => {     
      const src = dom2canvas(this.CardPreview.current);
      this.setState({
        src,
      });
    });
  }
  
  render() {
    const { connectDragSource, connectDragPreview, onMouseDown } = this.props;
    const { src } = this.state;
    return connectDragSource(      
      <div
        role="none"      
        style={{
          cursor: 'grab',
          position: 'absolute',
          height: '100%',
          top: 0,
          left: 0,
          width: '100%',
        }}
        onMouseDown={onMouseDown}
      >
        <DragPreviewImage connect={connectDragPreview} src={src} />
        {/* {connectDragPreview(<img src={src} />)} */}
        {/* 隐藏元素,用来生成preview */}
        <div style={{ position: 'fixed', top: -9999, left: -9999 }}>  
          <EpicCard saveRef={this.CardPreview} {...this.props} />
        </div>
      </div>,     
    );
  }
}

EpicDragCollapse.propTypes = {

};

export default EpicDrag(EpicDragCollapse);
