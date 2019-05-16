import React, { Component } from 'react';
import Card from '../Card';
import VersionProgress from './VersionProgress';

class VersionProgressWrap extends Component {
  render() {
    const { link } = this.props;
    return (
      <Card 
        title="版本进度"
        // link={link}
      >
        <VersionProgress />
      </Card>
    );
  }
}

export default VersionProgressWrap;
