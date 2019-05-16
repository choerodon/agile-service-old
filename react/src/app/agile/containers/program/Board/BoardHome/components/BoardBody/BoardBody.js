import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { find } from 'lodash';
import { ColumnWidth, CardHeight, CardMargin } from '../Constants';
import BoardStore from '../../../../../../stores/program/Board/BoardStore';

import Cell from './Cell';
import './BoardBody.scss';


const dataMap = new Map([[1, new Map([[2, new Map([[3, []]])]])]]);
@observer
class BoardBody extends Component {
  render() {
    const { resizing, activePi } = BoardStore;
    const { sprints, projects } = this.props;
    return (
      <div className="c7nagile-BoardBody">
        <table>
          <thead>
            <tr>
              <th style={{ width: 140, minWidth: 140, textAlign: 'center' }}>
                {activePi.piCode}
              </th>
              {
                sprints.map(sprint => <th style={{ width: ColumnWidth * sprint.columnWidth }}>{sprint.sprintName}</th>)
              }
            </tr>
          </thead>
          <tbody>
            {
              projects.map((project, i) => {
                const { teamSprints, projectName } = project;
                return (
                  <tr>
                    <td style={{
                      width: 140, minWidth: 140, textAlign: 'center', 
                    }}
                    >          
                      {projectName}
                    </td>
                    {teamSprints.map((sprint, j) => (
                      <td>
                        <Cell
                          project={project}
                          data={sprint}
                          sprintIndex={j}
                          projectIndex={i}
                          sprintId={sprint.sprintId}
                          teamProjectId={project.projectId}
                        />
                      </td>
                    ))}
                  </tr>
                );
              })
            }
          </tbody>
        </table>
        {resizing && (
          <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            zIndex: 9999,
            cursor: 'col-resize',
          }}
          />
        )}
      </div>
    );
  }
}

BoardBody.propTypes = {

};

export default BoardBody;
