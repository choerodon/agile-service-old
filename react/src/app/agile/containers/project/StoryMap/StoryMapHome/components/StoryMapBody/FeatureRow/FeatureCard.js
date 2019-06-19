import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import { programIssueLink, issueLink, getProjectId } from '../../../../../../../common/utils';
import Card from '../Card';
import './FeatureCard.scss';


@observer
class FeatureCard extends Component {
  render() {
    const { feature } = this.props;
    const {
      featureType, summary, issueId, issueNum, programId, 
    } = feature;
    
    return (
      <Card className={`c7nagile-StoryMap-FeatureCard minimapCard ${featureType || 'none'}`}>
        <div className="summary">
          {issueId && issueNum ? (
            <Link to={programId ? programIssueLink(issueId, issueNum, programId) : issueLink(issueId, 'story', issueNum)} style={{ marginRight: 5 }} target="_blank">
          #
              {issueNum}
            </Link>
          ) : null}
          {summary || '无特性'}
        </div>        
      </Card>
    );
  }
}

FeatureCard.propTypes = {

};

export default FeatureCard;