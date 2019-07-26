import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon, Spin } from 'choerodon-ui';
import FilterItem from './FilterItem';
import './FilterManage.scss';

class FilterManage extends Component {
  render() {
    const {
      myFilters, onClose, onDelete, onUpdate, loading,
    } = this.props;
    return (

      <div className="c7nagile-FilterManage">
        <Spin spinning={loading}>
          <div className="c7nagile-FilterManage-header">
            <span>筛选管理</span>
            <Icon type="close" onClick={onClose} />
          </div>
          <div className="c7nagile-FilterManage-content">
            {
              myFilters.map(filter => (
                <FilterItem
                  key={filter.filterId}
                  filter={filter}
                  onDelete={onDelete}
                  onUpdate={onUpdate}
                />
              ))
            }
          </div>

        </Spin>
      </div>
    );
  }
}

FilterManage.propTypes = {

};

export default FilterManage;
