import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Column from '../Column';
import FeatureCard from './FeatureCard';
import { ColumnWidth } from '../../../Constants';
import AutoScroll from '../../../../../../common/AutoScroll';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import './FeatureColumn.scss';

@observer
class FeatureColumn extends Component {
  state = {
    resizing: false,
  }

  componentDidMount() {
    this.AutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('minimap-container-scroll')[0],
      onMouseMove: this.handleMouseMove,
      onMouseUp: this.handleMouseUp,
    });
  }

  /**
   * item改变大小
   * @parameter mode 模式 left或right
   * @parameter multiple 变几个 => 1
   */
  handleItemResize = (mode, multiple) => {
    const { epic, feature } = this.props;
    let width = this.initWidth;
    switch (mode) {
      case 'right': {
        width += multiple;
        break;
      }
      default: break;
    }
    // 最小为一
    if (width > 0) {
      StoryMapStore.setFeatureWidth({ epicId: epic.issueId, featureId: feature.issueId, width });
    }
  }

  handleMouseDown = (mode, e) => {
    // e.stopPropagation();
    // e.preventDefault();
    this.AutoScroll.prepare(e);
    const { otherData: { width } } = this.props;
    this.initWidth = width;
    this.initScrollPosition = {
      x: e.clientX,     
    };
    this.setState({
      resizing: true,
    });
  }

  handleMouseMove = (e, { left: scrollPos }) => {
    this.fireResize(e, scrollPos);
  }
  
  // 触发item的宽度变化
  fireResize = ({ clientX }, scrollPos) => {
    const { isLast } = this.props;
    const posX = clientX - this.initScrollPosition.x + scrollPos;
    if (isLast && posX > 15) {
      this.handleItemResize('right', 1);
    }
    // 一个所占宽度
    if (Math.abs(posX) > (ColumnWidth / 2)) {
      // 变化的倍数 当达到宽度1/2的倍数的时候触发变化        
      const multiple = Math.round(Math.abs(posX) / (ColumnWidth / 2));
      // console.log(multiple);
      // 奇数和偶数的不同处理 5=>2  4=>2
      if (multiple % 2 === 0) {
        this.handleItemResize('right', multiple * (posX > 0 ? 1 : -1) / 2);
      } else {
        this.handleItemResize('right', (multiple - 1) / 2 * (posX > 0 ? 1 : -1));
      }
    }
  }

  /**
   * 鼠标up将数据初始化
   * 
   * 
   */
  handleMouseUp = (e) => {
    this.setState({
      resizing: false,
    });
    const { epic, feature, otherData: { width } } = this.props;

    // 只在数据变化时才请求
    if (this.initWidth !== width) {    
      const issueId = feature.issueId === 'none' ? epic.issueId : feature.issueId;
      const type = feature.issueId === 'none' ? 'epic' : 'feature';
      StoryMapStore.changeWidth({
        width,       
        issueId,
        type,
      }, {
        epicId: epic.issueId, 
        featureId: feature.issueId,
        initWidth: this.initWidth,
      });      
    }
  }

  render() {
    const { feature, otherData } = this.props;
    const { width } = otherData || {};
    const { resizing } = this.state;
    return (
      <Column width={width} style={{ position: 'relative' }}>
        <FeatureCard feature={feature} />      
        {resizing && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            zIndex: 9999,
            cursor: 'col-resize',
          }}
          />
        )}
        <div   
          className="c7nagile-StoryMap-FeatureColumn-Resize"       
          style={{
            top: 0,
            height: '100%',
            width: 20,
            position: 'absolute',
            zIndex: 2,
            cursor: 'col-resize',
            right: -10,
          }}
          onMouseDown={this.handleMouseDown.bind(this, 'right')}
          role="none"
        >
          <div className={`c7nagile-StoryMap-FeatureColumn-Resize-highlight ${resizing ? 'active' : ''}`} />
        </div>
      </Column>
    );
  }
}

FeatureColumn.propTypes = {

};

export default FeatureColumn;
