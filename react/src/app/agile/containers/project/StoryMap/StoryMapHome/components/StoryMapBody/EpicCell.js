/* eslint-disable no-nested-ternary */
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
import IsInProgramStore from '../../../../../../stores/common/program/IsInProgramStore';

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

  handleCreateEpic = (newEpic) => {
    const { index } = this.props;
    StoryMapStore.afterCreateEpic(index, newEpic);
  }

  render() {
    const {
      epicData, otherData, lastCollapse, index, 
    } = this.props;
    const { collapse, storys, feature } = otherData || {};
    const { isInProgram } = IsInProgramStore;
    const {
      // featureCommonDOList,
      issueId,
      // issueNum,
      // summary,
      // typeCode,
      adding,
    } = epicData;
    let subIssueNum = 0;
    if (storys && feature) {
      subIssueNum = Math.max(storys.length + Object.keys(feature).length - 1, 0);// 减去none
    }
    return (
      <Cell 
        epicIndex={index}
        lastCollapse={lastCollapse}
        collapse={collapse}
        rowspan={collapse ? '9999' : '1'}
        style={{
          paddingLeft: 0,
          position: 'sticky',
          top: 0,
          zIndex: 6,
          background: 'white',   
          ...collapse ? {
            borderLeft: lastCollapse ? 'none' : 'solid 1px #D8D8D8',
            borderRight: 'solid 1px #D8D8D8',
            boxShadow: 'rgb(216, 216, 216) 0px -1px 0px inset',
          } : {},
        }}
      >
        <div style={{
          display: 'flex',
          alignItems: 'center', 
          position: 'sticky',
          top: 5, 
        }}
        >
          {!adding && (
            <Fragment>
              <div style={{
                width: 10,
                height: 50,
                display: 'flex',
                alignItems: 'center',
                ...collapse ? { marginRight: 35 } : {},
              }}
              >
                <Icon style={{ position: 'relative', zIndex: 10 }} type={collapse ? 'navigate_next' : 'navigate_before'} onClick={this.handleCollapse} />
              </div>
            </Fragment>
          )}
          {collapse
            ? (
              <Fragment>                
                <div style={{
                  width: 26,
                  overflow: 'hidden',
                  wordBreak: 'break-all',
                  whiteSpace: 'pre-wrap',
                  position: 'absolute',
                  top: 20,
                  marginLeft: 20,                 
                }}
                >
                  {`${epicData.epicName || '无史诗'} (${subIssueNum})`}
                </div>                  
                {/* </div> */}
              </Fragment>
            ) : (
              <Fragment>
                <Column style={{ minHeight: 'unset' }}>
                  {adding
                    ? <CreateEpic onCreate={this.handleCreateEpic} />
                    : <EpicCard epic={epicData} subIssueNum={subIssueNum} />}
                </Column>
                {issueId ? (!adding && !isInProgram && <AddCard style={{ height: 42 }} onClick={this.handleAddEpicClick} />) : null}
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
