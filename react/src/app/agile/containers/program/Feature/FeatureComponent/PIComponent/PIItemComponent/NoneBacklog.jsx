import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';

@inject('AppState')
@observer class NoneBacklog extends Component {
  render() {
    const { store } = this.props;
    return store.hasFilter ? (
      <div className="c7n-noissue-wapper">
        <div className="c7n-noissue-notzero">在 Backlog 中所有问题已筛选</div>
      </div>
    ) : (
      <div />
    );
  }
}

export default NoneBacklog;
