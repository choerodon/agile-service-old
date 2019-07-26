import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import emptyPng from '../../../../../../assets/image/emptySprint.svg';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';

@inject('AppState')
@observer class NoneIssue extends Component {
  render() {
    return BacklogStore.hasFilter ? (
      <div className="c7n-noissue-wapper">
        <div className="c7n-noissue-notzero">在sprint中所有问题已筛选</div>
      </div>
    ) : (
      <div className="c7n-noissue-wapper">
        <div style={{ display: 'flex', height: 100 }} className="c7n-noissue-notzero">
          <img style={{ width: 80, height: 70 }} alt="空sprint" src={emptyPng} />
          <div style={{ marginLeft: 20 }}>
            <p>计划您的SPRINT</p>
            <p>这是一个Sprint。将问题拖拽至此来计划一个Sprint。</p>
          </div>
        </div>
      </div>
    );
  }
}

export default NoneIssue;
