import React, { Component } from 'react';
import './EmptyBlock.scss';

class EmptyBlock extends Component {
  render() {
    return (
      <div
        className="c7n-emptyBlock"
        style={{ ...this.props.style }}
      >
        <div
          className="c7n-wrap"
          style={{
            border: this.props.border ? '1px dashed rgba(0, 0, 0, 0.54)' : '',
          }}
        >
          <div className="c7n-imgWrap">
            <img src={this.props.pic} alt="" className="c7n-img" />
          </div>
          <div
            className="c7n-textWrap"
            style={{ width: this.props.textWidth || 150 }}
          >
            <h1 className="c7n-title">
              {this.props.title || ''}
            </h1>
            <div className="c7n-des">
              {this.props.des || ''}
            </div>
          </div>
        </div>
      </div>
    );
  }
}
export default EmptyBlock;
