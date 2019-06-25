import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CellPadding } from '../../Constants';

class Cell extends Component {
  render() {
    const {
      children, style, lastCollapse, collapse, 
    } = this.props;
    return (
      <td
        {...this.props}
        style={{
          padding: CellPadding, 
          boxShadow: 'inset 0 -1px 0 #D8D8D8,inset 1px 0 0 #D8D8D8', 
          border: 'none',     
          // ...isLastEpic ? { borderRight: 'solid 1px #D8D8D8' } : {},
          ...lastCollapse ? { boxShadow: 'inset 0 -1px 0 #D8D8D8' } : {},
          ...collapse ? { boxShadow: 'none' } : {},
          ...style, 
        }}
      >
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
