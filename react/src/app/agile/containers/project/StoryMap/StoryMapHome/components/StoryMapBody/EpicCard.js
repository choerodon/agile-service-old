import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { Tooltip } from 'choerodon-ui';
import { DragSource } from 'react-dnd';
import { observer } from 'mobx-react';
import EpicDrag from './EpicDrag';
import { programIssueLink, issueLink } from '../../../../../../common/utils';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import AutoScroll from '../../../../../../common/AutoScroll';
import Card from './Card';
import './EpicCard.scss';

@observer
class EpicCard extends Component {
  componentDidMount() {
    this.AutoScroll = new AutoScroll({
      scrollElement: document.getElementsByClassName('minimap-container-scroll')[0],      
      pos: {
        left: 200,
        top: 150,
        bottom: 150,
        right: 150,
      },
      type: 'drag',
    });
  }
  
  handleClick = () => {
    const { epic } = this.props;
    if (epic.issueId) {
      StoryMapStore.setClickIssue(epic);
    }
  }

  setZIndex = () => {
    this.container.style.zIndex = 9999;
  }

  resetZIndex = () => {
    this.container.style.zIndex = 'unset';
  }

  saveRef = (ref) => {
    const { connectDragSource } = this.props;
    connectDragSource(ref);
    this.container = ref;
  }

  handleMouseDown = (e) => {
    this.AutoScroll.prepare(e);
  }

  render() {
    const { epic, subIssueNum } = this.props;
    const {
      issueId, epicName, issueNum, programId, 
    } = epic;
    const { selectedIssueMap } = StoryMapStore;
    return (
      <Card
        className={`c7nagile-StoryMap-EpicCard minimapCard ${issueId ? '' : 'none'} ${selectedIssueMap.has(issueId) ? 'selected' : ''}`}
        style={{
          height: 42, display: 'flex', alignItems: 'center', padding: '0 12px', 
        }}
        onClick={this.handleClick}
        saveRef={this.saveRef}
        onMouseDown={this.handleMouseDown}
      >
        <div style={{ overflow: 'hidden', textOverflow: 'ellipsis' }}>
          <Tooltip title={`${epicName || '无史诗'}`} getPopupContainer={trigger => trigger.parentNode}>
            {/* {issueId && issueNum ? (
              <Link disabled={programId} to={programId ? programIssueLink(issueId, issueNum, programId) : issueLink(issueId, 'story', issueNum)} style={{ marginRight: 5 }} target="_blank">
          #
                {issueNum}
              </Link>
            ) : null} */}
          
            {`${epicName || '无史诗'}`}
          </Tooltip>
        </div>
        <div style={{ marginLeft: 5 }}>{`(${subIssueNum})`}</div>        
      </Card>
    );
  }
}

EpicCard.propTypes = {

};

export default EpicDrag(EpicCard);
