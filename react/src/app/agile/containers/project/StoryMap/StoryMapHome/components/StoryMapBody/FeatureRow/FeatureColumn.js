import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Column from '../Column';
import FeatureCard from './FeatureCard';

@observer
class FeatureColumn extends Component {
  render() {
    const { feature, otherData } = this.props;
    const { width } = otherData || {};
    return (
      <Column width={width}>
        <FeatureCard feature={feature} />      
      </Column>
    );
  }
}

FeatureColumn.propTypes = {

};

export default FeatureColumn;
