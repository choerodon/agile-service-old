import React, { Component } from 'react';
import PropTypes from 'prop-types';

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
      </div>
    );
  }
}

Search.propTypes = {

};

export default Search;
