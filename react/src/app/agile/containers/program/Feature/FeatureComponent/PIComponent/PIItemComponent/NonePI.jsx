import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import emptyImg from '../../../../../../assets/image/feature-list.svg';

const data = {
  sprint: {
    img: emptyImg,
    title: '用问题填充您的待办事项',
    description1: '这是您的团队待办事项。创建并预估新的问题，并通',
    description2: '过上下拖动来对待办事项排优先级。',
  },
  pi: {
    img: emptyImg,
    title: '用问题填充您的特性列表',
    description1: '这是您的项目群特性列表。您可以先添加一些问题，再通过',
    description2: '上下拖动来排列问题的优先级。',
  },
};

@inject('AppState')
@observer class NonePI extends Component {
  getSourceByType = (source) => {
    const { type } = this.props;
    return data[type] && data[type][source];
  };

  render() {
    return (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          padding: '42px 0 45px 0',
        }}
      >
        <img style={{ width: 172 }} alt="emptybacklog" src={this.getSourceByType('img')} />
        <div style={{ marginLeft: 40 }}>
          <p style={{ color: 'rgba(0,0,0,0.65)' }}>{this.getSourceByType('title')}</p>
          <p style={{ fontSize: 16, lineHeight: '28px', marginTop: 8 }}>
            {this.getSourceByType('description1')}
            <br />
            {this.getSourceByType('description2')}
          </p>
        </div>
      </div>
    );
  }
}

export default NonePI;
