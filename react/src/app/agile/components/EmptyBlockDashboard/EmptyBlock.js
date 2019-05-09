import React, { Component } from 'react';
import './EmptyBlock.scss';

class EmptyBlock extends Component {
  render() {
    const { pic, des } = this.props;
    return (
      <div
        className="c7n-emptyBlock"
      >
        <div className="c7n-imgWrap">
          <img src={pic} alt="" className="c7n-img" />
        </div>
        <div className="c7n-des">
          {des || ''}
        </div>
      </div>
    );
  }
}
export default EmptyBlock;
