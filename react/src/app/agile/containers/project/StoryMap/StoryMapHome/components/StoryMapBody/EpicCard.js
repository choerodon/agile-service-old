import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { Tooltip } from 'choerodon-ui';
import { programIssueLink, issueLink } from '../../../../../../common/utils';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import Card from './Card';
import './EpicCard.scss';

class EpicCard extends Component {
  handleClick = () => {
    const { epic } = this.props;
    if (epic.issueId) {
      StoryMapStore.setClickIssue(epic);
    }
  }

  render() {
    const { epic, subIssueNum } = this.props;
    const {
      issueId, epicName, issueNum, programId, 
    } = epic;
    return (
      <Card
        className={`c7nagile-StoryMap-EpicCard minimapCard ${issueId ? '' : 'none'}`}
        style={{
          height: 42, display: 'flex', alignItems: 'center', padding: '0 12px', 
        }}
        onClick={this.handleClick}
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

export default EpicCard;
