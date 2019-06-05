import React, { Component } from 'react';
import PropTypes from 'prop-types';
import Card from './Card';
import './EpicCard.scss';

class EpicCard extends Component {
  render() {
    const { epic, subIssueNum } = this.props;
    return (
      <Card className="c7nagile-StoryMap-EpicCard" style={{ height: 42 }}>
        {`${epic.epicName} (${subIssueNum})`}
      </Card>
    );
  }
}

EpicCard.propTypes = {

};

export default EpicCard;
