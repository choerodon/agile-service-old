import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import EpicRow from './EpicRow';
import FeatureRow from './FeatureRow';
import StoryArea from './StoryArea';
import StoryMapStore from '../../../../../stores/project/StoryMap/StoryMapStore';
import IsInProgramStore from '../../../../../stores/common/program/IsInProgramStore';
import './StoryMapBody.scss';

@observer
class StoryMapBody extends Component {
  render() {    
    return (
      <div className="c7nagile-StoryMapBody">        
        <table>
          <tbody>            
            <EpicRow />
            {/* 在项目群下才显示 */}
            {IsInProgramStore.isInProgram && <FeatureRow />}
            <StoryArea />
            {/* <tr style={{ visibility: 'hidden', height: 'auto' }} /> */}
          </tbody>
        </table>        
      </div>
    );
  }
}

StoryMapBody.propTypes = {

};

export default StoryMapBody;
