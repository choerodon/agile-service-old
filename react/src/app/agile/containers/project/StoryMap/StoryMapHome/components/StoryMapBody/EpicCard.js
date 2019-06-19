import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Card from './Card';
import './EpicCard.scss';

class EpicCard extends Component {
  render() {
    const { epic, subIssueNum } = this.props;
    return (
      <Card className={`c7nagile-StoryMap-EpicCard minimapCard ${epic.issueId ? '' : 'none'}`} style={{ height: 42 }}>
        {`${epic.epicName || '无史诗'} (${subIssueNum})`}
      </Card>
    );
  }
}

EpicCard.propTypes = {

};

export default EpicCard;
