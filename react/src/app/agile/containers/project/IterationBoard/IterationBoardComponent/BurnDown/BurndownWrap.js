import React, { Component } from 'react';
import Card from '../Card';
import Burndown from './BurnDown';

class BurndownWrap extends Component {
  render() {
    const { sprintId, link } = this.props;
    return (
      <Card
        title="燃尽图"
        link={link}
        sprintId={sprintId}
      >
        <Burndown
          sprintId={sprintId}
        />
      </Card>
    );
  }
}

export default BurndownWrap;
