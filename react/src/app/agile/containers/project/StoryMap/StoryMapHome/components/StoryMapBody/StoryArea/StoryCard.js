import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { CardWidth, CardHeight, CardMargin } from '../../../Constants';
import Card from '../Card';
import './StoryCard.scss';

class StoryCard extends Component {
  render() {
    const { story } = this.props;
    return (
      <Card className="c7nagile-StoryMap-StoryCard">
        {story.summary}
      </Card>
    );
  }
}

StoryCard.propTypes = {

};

export default StoryCard;
