import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Tooltip } from 'choerodon-ui';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import Card from '../Card';
import './FeatureCard.scss';

@observer
class FeatureCard extends Component {
  handleClick = () => {
    const { feature } = this.props;
    if (feature.issueId !== 'none') {
      StoryMapStore.setClickIssue(feature);
    }
  }

  render() {
    const { feature } = this.props;
    const {
      featureType, summary, issueId, issueNum, programId, 
    } = feature;
    const { selectedIssueMap } = StoryMapStore;
    return (
      <Card className={`c7nagile-StoryMap-FeatureCard minimapCard ${featureType || 'none'} ${selectedIssueMap.has(issueId) ? 'selected' : ''}`} onClick={this.handleClick}>
        <div className="summary">
          <Tooltip title={`${summary || '无特性'}`} getPopupContainer={trigger => trigger.parentNode}>
            {summary || '无特性'}
          </Tooltip>
        </div>        
      </Card>
    );
  }
}

FeatureCard.propTypes = {

};

export default FeatureCard;
