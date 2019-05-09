/* eslint-disable react/self-closing-comp,jsx-a11y/accessible-emoji */
import React from 'react';
import './NoneSprint.scss';
import EmptyScrumboard from '../../../../../../assets/image/emptyScrumboard.svg';

const NoneSprint = () => (
  <React.Fragment>
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginTop: '80px',
      }}
    >
      <img style={{ width: 170 }} src={EmptyScrumboard} alt="emptyscrumboard" />
      <div
        style={{
          marginLeft: 40,
        }}
      >
        <p style={{ color: 'rgba(0,0,0,0.65)' }}>没有计划的项目群看板</p>
        <p style={{ fontSize: 20, lineHeight: '34px' }}>
          {'你可以在'}
          <span style={{ color: '#3f51b5' }}>特性列表</span>
          {'中创建特性'}
        </p>
      </div>
    </div>
  </React.Fragment>
);

export default NoneSprint;
