import React, { Component } from 'react';
import Card from '../Card';
import EpicProgress from './EpicProgress';

class EpicProgressWrap extends Component {
  render() {
    const { link } = this.props;
    return (
      <Card
        title="史诗进度"
        // link={link}
      >
        <EpicProgress />
      </Card>
    );
  }
}

export default EpicProgressWrap;
