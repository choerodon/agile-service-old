import React, { Component } from 'react';
import Card from '../Card';
import IterationSpeed from './IterationSpeed';

class IterationSpeedWrap extends Component {
  render() {
    const { link } = this.props;
    return (
      <Card
        title="迭代速度图"
        link={link}
      >
        <IterationSpeed />
      </Card>
    );
  }
}

export default IterationSpeedWrap;
