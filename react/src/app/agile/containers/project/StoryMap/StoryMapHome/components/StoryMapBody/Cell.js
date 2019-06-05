import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CellPadding } from '../../Constants';

class Cell extends Component {
  render() {
    const { children, style } = this.props;
    return (
      <td style={{ padding: CellPadding, ...style }}>
        {children}
      </td>
    );
  }
}

Cell.propTypes = {

};
Cell.defaultProps = {

};
export default Cell;
