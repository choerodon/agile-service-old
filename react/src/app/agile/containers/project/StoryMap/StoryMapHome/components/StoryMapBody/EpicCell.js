import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { Icon } from 'choerodon-ui';
import { observer } from 'mobx-react';
import Column from './Column';
import EpicCard from './EpicCard';
import Cell from './Cell';
import AddCard from './AddCard';
import CreateEpic from './CreateEpic';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class EpicCell extends Component {
  handleCollapse = () => {
    const { epicData, collapse } = this.props;
    StoryMapStore.collapse(epicData.issueId);
  }

  handleAddEpicClick = () => {
    const { epicData } = this.props;
    StoryMapStore.addEpic(epicData);
  }

  render() {
    const { epicData, otherData } = this.props;
    const { collapse } = otherData || {};
    const {
      featureCommonDOList,
      issueId,
      issueNum,
      summary,
      typeCode,
      adding,
    } = epicData;
    return (
      <Cell style={{ paddingLeft: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {!adding && (
            <div style={{ width: 20 }}>
              <Icon type={collapse ? 'navigate_next' : 'navigate_before'} onClick={this.handleCollapse} />
            </div>
          )}
          {collapse ? null : (
            <Fragment>
              <Column style={{ minHeight: 'unset' }}>
                {adding 
                  ? <CreateEpic />
                  : <EpicCard epic={epicData} />}
              </Column>
              {!adding && <AddCard style={{ height: 42 }} onClick={this.handleAddEpicClick} />}
            </Fragment>
          )}
        </div>
      </Cell>
    );
  }
}

EpicCell.propTypes = {

};

export default EpicCell;
