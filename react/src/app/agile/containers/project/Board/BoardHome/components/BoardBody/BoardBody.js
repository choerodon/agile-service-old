import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { observer } from 'mobx-react';
import { Link } from 'react-router-dom';
import { findIndex } from 'lodash';
import { ColumnWidth } from '../Constants';
import BoardStore from '../../../../../../stores/project/Board/BoardStore';
import Connectors from '../Connectors';
import ProjectCard from './ProjectCard';
import noProject from '../../../../../../assets/noProject.svg';
import Empty from '../../../../../../components/Empty';
import Cell from './Cell';
import './BoardBody.scss';

@observer
class BoardBody extends Component {
  handleClick = (e) => {
    BoardStore.clearSelect();
  }

  moveProject = (sourceId, targetIndex) => {
    const sourceIndex = findIndex(BoardStore.projects, { boardTeamId: sourceId });
    BoardStore.sortProjects(sourceIndex, targetIndex);
  }

  render() {
    const { resizing, activePi, featureListVisible } = BoardStore;
    const { sprints, projects } = this.props;
    return (
      projects.length > 0 ? (
        <div role="none" className="c7nagile-BoardBody_project" onClick={this.handleClick} style={{ paddingRight: featureListVisible ? 400 : 30 }}>
          <table>
            <thead>
              <tr>
                <th style={{
                  width: 140, minWidth: 140, textAlign: 'center', fontWeight: 500,
                }}
                >
                  {activePi.piCode}
                </th>
                {
                  sprints.map(sprint => <th style={{ width: ColumnWidth * sprint.columnWidth, fontWeight: 500 }}>{sprint.sprintName}</th>)
                }
              </tr>
            </thead>
            <tbody>
              {
                projects.map((project, i) => {
                  const { teamSprints, projectName } = project;
                  return (
                    <tr>
                      <ProjectCard project={project} index={i} moveProject={this.moveProject} />
                      {teamSprints.map((sprint, j) => (
                        <Cell
                          isLast={j === teamSprints.length - 1}
                          project={project}
                          data={sprint}
                          sprintIndex={j}
                          projectIndex={i}
                          sprintId={sprint.sprintId}
                          teamProjectId={project.projectId}
                        />
                      ))}
                    </tr>
                  );
                })
              }
            </tbody>
          </table>
          <Connectors />
          <div />
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
      ) : (
        <Empty
          style={{ background: 'white', height: 'calc(100% + 120px)', marginTop: -120 }}
          pic={noProject}
          title="没有项目群团队"
          description={(
            <Fragment>
                这是您的项目公告板。您已经创建了活跃PI，但是没有项目群团队，您可以到组织层
              <span style={{ color: '#3f51b5' }}>项目管理</span>
                进行团队维护。
            </Fragment>
            )}
        />
      )
    );
  }
}

BoardBody.propTypes = {

};

export default BoardBody;
