import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CardHeight, CardWidth, CardMargin } from '../Constants';
import IssueCard from './IssueCard';

class Column extends Component {
  render() {
    const { column } = this.props;
    const { issues } = column;
    return (
      <div style={{ width: CardWidth + CardMargin * 2, minHeight: CardHeight + CardMargin * 2 }}>
        {issues.map(issue => <IssueCard issue={issue} />)}
      </div>
    );
  }
}

Column.propTypes = {

};

export default Column;
