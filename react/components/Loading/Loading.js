import React, { Component } from 'react';
import { Spin } from 'choerodon-ui';
import PropTypes from 'prop-types';
import './Loading.scss';

const Loading = ({
  loading,
}) => (     
  <Spin spinning={loading} wrapperClassName="c7ntest-Loading">
    <div style={{ width: '100%', height: '100%' }} />
  </Spin>   
);

Loading.propTypes = {

};

export default Loading;
