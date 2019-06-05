import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import EpicRow from './EpicRow';
import FeatureRow from './FeatureRow';
import StoryArea from './StoryArea';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import './StoryMapBody.scss';

@observer
class StoryMapBody extends Component {
  render() {    
    return (
      <div className="c7nagile-StoryMapBody">        
        <table>
          <tbody>            
            <EpicRow />
            <FeatureRow />  
            <StoryArea />
            <tr style={{ visibility: 'hidden' }} />
          </tbody>          
        </table>        
      </div>
    );
  }
}

StoryMapBody.propTypes = {

};

export default StoryMapBody;
