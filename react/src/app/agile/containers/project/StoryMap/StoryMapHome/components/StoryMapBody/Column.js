import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { ColumnWidth, ColumnMinHeight } from '../../Constants';

class Column extends Component {
  render() {
    const {
      width, children, style, saveRef, 
    } = this.props;
    return (
      <div style={{ width: ColumnWidth * width, minHeight: ColumnMinHeight, ...style }} ref={saveRef}>
        {children}
      </div>
    );
  }
}

Column.propTypes = {

};
Column.defaultProps = {
  width: 1,
};
export default Column;
