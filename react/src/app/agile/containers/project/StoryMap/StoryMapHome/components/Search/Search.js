import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Select } from 'choerodon-ui';
import FiltersProvider from '../../../../../../components/FiltersProvider';
import { configTheme } from '../../../../../../common/utils';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

const { Option } = Select;
@FiltersProvider(['issueStatus', 'version'])
class Search extends Component {
  setFilter=(field, values) => {
    StoryMapStore.handleFilterChange(field, values.map(Number));
  }

  render() {
    const { filters: { issueStatus, version: versionList } } = this.props;
    return (
      <div style={{
        height: 48, 
        borderBottom: '1px solid #d3d3d3',
        padding: '8px 24px',
        display: 'flex',
        alignItems: 'center', 
      }}
      >
        Search        
        <div style={{ display: 'flex' }}>
          <Select
            {...configTheme({ list: issueStatus })} 
            allowClear
            mode="multiple"
            style={{ width: 100 }}
            onChange={this.setFilter.bind(this, 'statusList')}
            placeholder="状态"
          >
            {issueStatus.map(({ text, value }) => <Option value={value}>{text}</Option>)}
          </Select>
          <Select
            {...configTheme({ list: versionList })} 
            allowClear
            mode="multiple"
            style={{ width: 100 }}
            onChange={this.setFilter.bind(this, 'versionList')}
            placeholder="版本"
          >
            {versionList.map(({ text, value }) => <Option value={value}>{text}</Option>)}
          </Select>
        </div> 
      </div>
    );
  }
}

Search.propTypes = {

};

export default Search;
