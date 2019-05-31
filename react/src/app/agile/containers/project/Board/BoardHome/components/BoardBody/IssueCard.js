/* eslint-disable consistent-return */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { Tooltip, Icon } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { programIssueLink } from '../../../../../../common/utils';
import TypeTag from '../../../../../../components/TypeTag';
import { CardHeight, CardWidth, CardMargin } from '../Constants';
import BoardStore from '../../../../../../stores/project/Board/BoardStore';
import './IssueCard.scss';

@observer
class IssueCard extends Component {
  handleSelect = (e) => {
    e.stopPropagation();
    const { issue } = this.props;
    BoardStore.setClickIssue(issue);
  }


  render() {
    const {
      issue, mode,
    } = this.props;

    const {
      issueTypeDTO, summary, issueNum, featureType, featureId, programId,
    } = issue;
    return (
      <div
        role="none"
        ref={(container) => { this.container = container; }}
        onClick={this.handleSelect}        
        style={{
          // zIndex,
          height: CardHeight,
          width: CardWidth,
          margin: CardMargin,
        }}
        className={`c7nagile-IssueCard ${mode}`}
      >
        <div role="none" className="c7nagile-IssueCard-top" onClick={(e) => { e.stopPropagation(); }}>
          <TypeTag data={issueTypeDTO} featureType={featureType} />
          <span className="c7nagile-IssueCard-top-issueNum">
            <Link to={programIssueLink(featureId, issueNum, programId)} target="_blank">{issueNum}</Link>
          </span>
        </div>
        <Tooltip title={summary}>
          <div className="c7nagile-IssueCard-summary">
            {summary}
          </div>
        </Tooltip>
      </div>
    );
  }
}

IssueCard.propTypes = {

};

export default IssueCard;
