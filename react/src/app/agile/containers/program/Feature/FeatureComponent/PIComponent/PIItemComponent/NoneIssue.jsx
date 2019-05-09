import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import emptyPI from '../../../../../../assets/image/feature-list.svg';
import emptySprint from '../../../../../../assets/image/emptyBacklog.svg';

const data = {
  sprint: {
    img: emptySprint,
    title: '计划您的SPRINT',
    description: '这是一个Sprint。将问题拖拽至此来计划一个Sprint。',
    hasFilter: '在Sprint中所有问题已筛选',
  },
  pi: {
    img: emptyPI,
    title: '计划您的PI',
    description: '这是一个PI。将问题拖拽至此来计划一个PI。',
    hasFilter: '在PI中所有问题已筛选',
  },
};

@inject('AppState')
@observer class NoneIssue extends Component {
  getSourceByType = (source) => {
    const { type } = this.props;
    return data[type] && data[type][source];
  };

  render() {
    const { store } = this.props;
    return store.hasFilter ? (
      <div className="c7n-noissue-wapper">
        <div className="c7n-noissue-notzero">{this.getSourceByType('hasFilter')}</div>
      </div>
    ) : (
      <div className="c7n-noissue-wapper">
        <div style={{ display: 'flex', height: 100 }} className="c7n-noissue-notzero">
          <img style={{ width: 80, height: 70 }} alt="空" src={this.getSourceByType('img')} />
          <div style={{ marginLeft: 20 }}>
            <p>{this.getSourceByType('title')}</p>
            <p>{this.getSourceByType('description')}</p>
          </div>
        </div>
      </div>
    );
  }
}

export default NoneIssue;
