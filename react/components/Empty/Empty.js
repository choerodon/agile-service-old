import React, { Component } from 'react';
import './Empty.scss';

const Empty = ({
  style,
  border,
  pic,
  title,
  description,
}) => (
  <div
    className="c7nagile-Empty"
    style={style}
  >
    <div
      className="c7nagile-Empty-content"
      style={{
        border: border ? '1px dashed rgba(0, 0, 0, 0.54)' : '',
      }}
    >
      <div className="c7nagile-Empty-imgWrap">
        <img src={pic} alt="" className="c7nagile-Empty-imgWrap-img" />
      </div>
      <div
        className="c7nagile-Empty-textWrap"
      >
        <h1 className="c7nagile-Empty-title">
          {title || ''}
        </h1>
        <div className="c7nagile-Empty-description">
          {description || ''}
        </div>
      </div>
    </div>
  </div>
);
export default Empty;
