import React from 'react';
import { Icon } from 'choerodon-ui';

const Toolbar = ({ onFullScreenClick }) => (
  <div id="toolbar">    
    <button type="button" className="ql-bold" />
    <button type="button" className="ql-italic" />
    <button type="button" className="ql-underline" />
    <button type="button" className="ql-strike" />
    <button type="button" className="ql-blockquote" />
    <button type="button" className="ql-list" value="ordered" />
    <button type="button" className="ql-list" value="bullet" />
    <button type="button" className="ql-image" />
    <button type="button" className="ql-link" />

    <button type="button" className="ql-fullScreen" style={{ outline: 'none' }}>
      <Icon type="zoom_out_map" onClick={onFullScreenClick} style={{ marginTop: -2 }} />
    </button>
  </div>
);
export default Toolbar;
