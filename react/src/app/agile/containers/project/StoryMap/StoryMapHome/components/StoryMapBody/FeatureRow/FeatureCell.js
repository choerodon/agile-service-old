import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import FeatureColumn from './FeatureColumn';
import Cell from '../Cell';

@observer
class FeatureCell extends Component {
  render() {
    const { epicData, otherData } = this.props;
    const { featureCommonDOList } = epicData;
    const { collapse } = otherData;
    return (
      <Cell>
        {collapse ? null : (
          <div style={{ display: 'flex' }}>        
            {featureCommonDOList.map(feature => <FeatureColumn feature={feature} />)}
          </div>
        )}
      </Cell>
    );
  }
}

FeatureCell.propTypes = {

};

export default FeatureCell;
