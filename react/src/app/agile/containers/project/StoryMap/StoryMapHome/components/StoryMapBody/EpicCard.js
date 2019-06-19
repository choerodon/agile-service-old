import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { programIssueLink, issueLink, getProjectId } from '../../../../../../common/utils';
import Card from './Card';
import './EpicCard.scss';

class EpicCard extends Component {
  render() {
    const { epic, subIssueNum } = this.props;
    const {
      issueId, epicName, issueNum, programId, 
    } = epic;
    return (
      <Card className={`c7nagile-StoryMap-EpicCard minimapCard ${issueId ? '' : 'none'}`} style={{ height: 42 }}>
        {issueId && issueNum ? (
          <Link to={programId ? programIssueLink(issueId, issueNum, programId) : issueLink(issueId, 'story', issueNum)} style={{ marginRight: 5 }} target="_blank">
          #
            {issueNum}
          </Link>
        ) : null}
        {`${epicName || '无史诗'} (${subIssueNum})`}
      </Card>
    );
  }
}

EpicCard.propTypes = {

};

export default EpicCard;
