import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import StoryCell from './StoryCell';
import TitleCell from './TitleCell';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

@observer
class StoryRow extends Component {
  getFirstNotCollapseIndex = () => {
    const { storyData } = StoryMapStore;
    const epicList = StoryMapStore.getEpicList;
    for (let i = 0; i < epicList.length; i += 1) {
      if (!storyData[epicList[i].issueId].collapse) {
        return i;
      }
    }
    return 0;
  }

  render() {
    const { storyMapData, storyData, swimLine } = StoryMapStore;
    const { epicWithFeature } = storyMapData || {};
    const epicList = StoryMapStore.getEpicList;
    const firstNotCollapseIndex = this.getFirstNotCollapseIndex();
    const { storyCollapse, rowIndex } = this.props;
    return (
      <Fragment>
        {/* 标题行 */}
        {['version'].includes(swimLine) && (
          <tr>
            {epicList.map((epic, index) => (
              <TitleCell
                epicIndex={index}
                isLastColumn={index === epicWithFeature.length - 1}
                lastCollapse={index > 0 ? storyData[epicList[index - 1].issueId] && storyData[epicList[index - 1].issueId].collapse : false}
                showTitle={firstNotCollapseIndex === index}
                otherData={storyData[epic.issueId]}                
                {...this.props}
              />
            ))}
          </tr>
        )}
        <tr style={{ ...storyCollapse ? { height: 0 } : {} }}>
          {epicList.map((epic, index) => (
            <StoryCell 
              rowIndex={rowIndex}
              epicIndex={index}
              lastCollapse={index > 0 ? storyData[epicList[index - 1].issueId] && storyData[epicList[index - 1].issueId].collapse : false}
              isLastColumn={index === epicWithFeature.length - 1}
              showTitle={firstNotCollapseIndex === index}
              epic={epic}
              otherData={storyData[epic.issueId]}            
              {...this.props}
            />
          ))}
        </tr>        
      </Fragment>
    );
  }
}

StoryRow.propTypes = {

};

export default StoryRow;
