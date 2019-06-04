import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Card from '../Card';
import './FeatureCard.scss';


@observer
class FeatureCard extends Component {
  render() {
    const { feature } = this.props;
    const { featureType, summary } = feature;
    return (
      <Card className={`c7nagile-StoryMap-FeatureCard ${featureType}`}>
        {summary}
      </Card>
    );
  }
}

FeatureCard.propTypes = {

};

export default FeatureCard;
