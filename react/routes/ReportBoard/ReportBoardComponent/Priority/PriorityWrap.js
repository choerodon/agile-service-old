import React, { Component } from 'react';
import Card from '../Card';
import Prio from './Priority';

class Priority extends Component {
  render() {
    const { link } = this.props;

    return (
      <Card
        title="优先级分布"
        link={link}
      >
        <Prio />
      </Card>
    );
  }
}

export default Priority;
