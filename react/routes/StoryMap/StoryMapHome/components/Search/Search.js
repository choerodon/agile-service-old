import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Select } from 'choerodon-ui';
import { observer } from 'mobx-react';
import FiltersProvider from '../../../../../components/FiltersProvider';
import { configTheme } from '../../../../../common/utils';
import StoryMapStore from '../../../../../stores/project/StoryMap/StoryMapStore';

const { Option } = Select;
@FiltersProvider(['issueStatus', 'version'])
@observer
class Search extends Component {
  setFilter=(field, values) => {
    StoryMapStore.handleFilterChange(field, values.map(Number));
  }

  render() {
    const { filters: { issueStatus } } = this.props;
    const { versionList } = StoryMapStore;
    return (
      <div style={{
        height: 48, 
        borderBottom: '1px solid #d3d3d3',
        padding: '8px 24px',
        display: 'flex',
        alignItems: 'center', 
      }}
      >
        <div style={{ fontWeight: 600, fontSize: '14px', marginRight: 20 }}>
        搜索:
        </div>                
        <div style={{ display: 'flex' }}>
          <Select
            {...configTheme({ list: issueStatus, primary: true })} 
            allowClear
            mode="multiple"
            style={{ width: 100 }}
            onChange={this.setFilter.bind(this, 'statusList')}
            placeholder="状态"
          >
            {issueStatus.map(({ text, value }) => <Option value={value}>{text}</Option>)}
          </Select>
          <Select
            {...configTheme({
              list: versionList, primary: true, textField: 'name', valueFiled: 'versionId', 
            })} 
            allowClear
            mode="multiple"
            style={{ width: 100 }}
            onChange={this.setFilter.bind(this, 'versionList')}
            placeholder="版本"
          >
            {versionList.filter(version => version.versionId !== 'none').map(({ name, versionId }) => <Option value={versionId}>{name}</Option>)}
          </Select>
        </div> 
      </div>
    );
  }
}

Search.propTypes = {

};

export default Search;
