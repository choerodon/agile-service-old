/* eslint-disable consistent-return */
import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { find, findIndex } from 'lodash';
import { observer } from 'mobx-react';
import BoardStore from '../../../../../../stores/project/Board/BoardStore';
import IssueCard from './IssueCard';
import { ColumnWidth, CardHeight, CardMargin } from '../Constants';
import './Cell.scss';

@observer
class Cell extends Component {
  judgeMode = (issue) => {
    const { heightLightIssueAndConnection: { issues, connections } } = BoardStore;
    let mode = 'normal';
    if (issues.length > 0 || connections.length > 0) {
      mode = 'unlight';
    }
    if (find(issues, { id: issue.id })) {
      mode = 'light';
    }
    return mode;
  }

  render() {
    const {
      data, project, sprintIndex,
    } = this.props;
    const { columnWidth } = BoardStore.sprints[sprintIndex];
    const { boardFeatures: issues, sprintId } = data; 

    return (
      <td>
        <div className="c7nagile-Cell" style={{ width: ColumnWidth * columnWidth, minHeight: CardHeight + CardMargin * 2 }}>
          {issues.map((issue, i) => (
            <IssueCard
              index={i}
              issue={issue}
              issues={issues}
              sprintId={sprintId}
              projectId={project.projectId}
              findCard={this.findCard}
              moveCard={this.moveCard}
              mode={this.judgeMode(issue)}
            />
          ))}
        </div>
      </td>
    );
  }
}

Cell.propTypes = {

};
export default Cell;
