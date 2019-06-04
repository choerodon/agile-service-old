import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryRow from './StoryRow';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryArea extends Component {
  render() {
    return (
      <StoryRow />    
    );
  }
}

StoryArea.propTypes = {

};

export default StoryArea;
