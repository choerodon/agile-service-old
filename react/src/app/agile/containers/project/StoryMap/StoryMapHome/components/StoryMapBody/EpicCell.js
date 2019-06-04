import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { observer } from 'mobx-react';
import Column from './Column';
import EpicCard from './EpicCard';
import Cell from './Cell';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class EpicCell extends Component {
  handleCollapse=() => {
    const { epicData, collapse } = this.props;
    StoryMapStore.collapse(epicData.issueId);
  }

  render() {
    const { epicData, otherData } = this.props;
    const { collapse } = otherData;
    const { 
      featureCommonDOList,
      issueId,
      issueNum,
      summary,
      typeCode, 
    } = epicData;
    return (
      <Cell style={{ paddingLeft: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <div style={{ width: 20 }}>
            <Icon type={collapse ? 'navigate_next' : 'navigate_before'} onClick={this.handleCollapse} />
          </div>
          {collapse ? null : (
            <Column style={{ minHeight: 'unset' }}>              
              <EpicCard epic={epicData} />                      
            </Column>
          )}
        </div>  
      </Cell>
    );
  }
}

EpicCell.propTypes = {

};

export default EpicCell;
