import React from 'react';
import { Link } from 'react-router-dom';
import { ProgramFeatureListLink } from '../../../../../../common/utils';
import noFeature from './noFeature.svg';

const NoFeature = () => (
  <div style={{
    display: 'flex',
    alignItems: 'center',   
    margin: '10px 15px',
    padding: 15,
    border: '1px dashed rgba(0,0,0,0.54)',
  }}
  >
    <img src={noFeature} alt="" />
    <div style={{ marginLeft: 15 }}>
      <div style={{ fontSize: '12px', color: 'rgba(0,0,0,0.65)' }}>计划PI中的特性</div>
      <div style={{ fontSize: '14px', marginTop: 10 }}>
        你可以到
        <Link style={{ marginLeft: 5 }} to={ProgramFeatureListLink()}>特性列表</Link>
        添加一些特性，再进行PI计划
      </div>
    </div>
  </div>
);
export default NoFeature;
