import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Column from '../Column';
import StoryCard from './StoryCard';
import CreateStory from './CreateStory';

@observer
class StoryColumn extends Component {
  render() {
    const { storys, width } = this.props;
    // console.log(storys);
    return (
      <Column width={width}>
        <div>
          {storys && storys.map(story => <StoryCard story={story} />)}
          <CreateStory />
        </div>
      </Column>
    );
  }
}

StoryColumn.propTypes = {

};

export default StoryColumn;
