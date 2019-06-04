import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import Column from '../Column';
import StoryCard from './StoryCard';

@observer
class StoryColumn extends Component {
  render() {
    const { storys } = this.props;
    // console.log(storys);
    return (
      <Column>
        <div>
          {storys.map(story => <StoryCard story={story} />)}
        </div>       
      </Column>
    );
  }
}

StoryColumn.propTypes = {

};

export default StoryColumn;
