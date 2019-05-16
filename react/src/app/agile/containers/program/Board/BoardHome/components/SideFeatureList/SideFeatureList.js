import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';
import FeatureItem from './FeatureItem';
import './SideFeatureList.scss';

@observer
class SideFeatureList extends Component {
  componentDidMount() {
    BoardStore.loadFeatureList();
  }

  render() {
    const { featureList } = BoardStore;
    return (
      <div className="c7nagile-SideFeatureList">
        <div className="c7nagile-SideFeatureList-header">
          <Input />
        </div>
        <div className="c7nagile-SideFeatureList-content">
          {
            featureList.map(feature => <FeatureItem feature={feature} />)
          }
        </div>
      </div>
    );
  }
}

SideFeatureList.propTypes = {

};

export default SideFeatureList;
