import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Select } from 'choerodon-ui';
import FiltersProvider from '../../../../../../components/FiltersProvider';

const { Option } = Select;
class Search extends Component {
  render() {
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
        {/* <FiltersProvider fields={['issueStatus', 'version']}>
          {([issueStatus, versionList]) => (
            <div>
              <Select placeholder="状态">
                {issueStatus.map(({ text, value }) => <Option value={value}>{text}</Option>)}
              </Select>
              <Select placeholder="版本">
                {versionList.map(({ text, value }) => <Option value={value}>{text}</Option>)}
              </Select>
            </div>
          )}
        </FiltersProvider> */}
      </div>
    );
  }
}

Search.propTypes = {

};

export default Search;
