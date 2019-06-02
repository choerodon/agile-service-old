
import { observable, action, computed } from 'mobx';
import {
  find, findIndex, max, remove,
} from 'lodash';
import {
  getBoard,
} from '../../../api/QueryProgramApi';
import { getProjectId } from '../../../common/utils';

class BoardStore {
  @observable resizing = false;

  @observable filter = {
    sprintIds: [],
    teamProjectIds: [getProjectId()],

  };

  @observable allFilters = [{
    id: 'onlyDependFeature',
    name: '仅显示依赖关系',
  }, {
    id: 'onlyOtherTeamDependFeature',
    name: '显示团队相关卡片',
  }]
    ;

  @observable selectedFilter = ['onlyOtherTeamDependFeature'];

  @observable boardData = null;

  @observable loading = false;

  @observable featureListVisible = false;

  @observable featureListLoading = false;

  @observable featureListCollapse = false;

  @observable addingConnection = false;

  @observable activePi = {};

  @observable featureList = [];

  @observable projects = [];

  @observable sprints = [];

  @observable connections = [];

  @observable clickIssue = {};

  @observable clickConnection = {};

  @observable heightLightIssueAndConnection = {
    issues: [],
    connections: [],
  }

  @observable overIssue = {};

  @action
  loadData = (programId) => {
    this.loading = true;
    const Filter = {
      ...this.filter,
    };
    this.allFilters.forEach((filter) => {
      Filter[filter.id] = this.selectedFilter.includes(filter.id);
    });
    getBoard(programId, Filter).then((boardData) => {
      const {
        filterSprintList,
        filterTeamList,
        boardDepends,
        piCode,
        piId,
        sprints,
        teamProjects,
      } = boardData;
      this.setBoardData(boardData);
      this.init({
        boardDepends,
        piCode,
        piId,
        sprints,
        teamProjects,
      });
    });
  }

  @action setSelectedFilter = (selectedFilter) => {
    this.selectedFilter = selectedFilter;
  }

  @action setFilter = (filter) => {
    this.filter = { ...this.filter, ...filter };
  }

  @action init = ({
    boardDepends,
    piCode,
    piId,
    sprints,
    teamProjects: projects,
  }) => {
    this.sprints = sprints;
    this.projects = projects;
    this.activePi = {
      piCode,
      piId,
    };
    this.connections = boardDepends;
    this.loading = false;
  }

  @action setBoardData(boardData) {
    this.boardData = boardData;
  }

  @action setLoading(loading) {
    this.loading = loading;
  }

  @action setFeatureListVisible(featureListVisible) {
    this.featureListVisible = featureListVisible;
  }

  @action setFeatureListCollapse(featureListCollapse) {
    this.featureListCollapse = featureListCollapse;
  }

  @action setFeatureList(featureList) {
    this.featureList = featureList;
  }

  @action test() {
    this.sprints[0].columnWidth = 3 - this.sprints[0].columnWidth;
  }

  @action setResizing(resizing) {
    this.resizing = resizing;
  }

  @action setClickIssue(clickIssue) {
    this.clickIssue = clickIssue;
    this.setHeightLightIssueAndConnection({ issue: clickIssue });
  }

  @action setClickConnection(clickConnection) {
    this.clickConnection = clickConnection;
    this.setHeightLightIssueAndConnection({ connection: clickConnection });
  }

  @action clearSelect() {
    this.clickIssue = {};
    this.clickConnection = {};
    this.setHeightLightIssueAndConnection();
  }

  @action setHeightLightIssueAndConnection({ issue, connection } = {}) {
    if (issue && issue.id) {
      const issues = [issue];
      const connections = [];
      this.connections.forEach((conn) => {
        const { boardFeature, dependBoardFeature } = conn;
        if (!boardFeature || !dependBoardFeature) {
          return;
        }
        if (conn.boardFeature.id === issue.id || conn.dependBoardFeature.id === issue.id) {
          connections.push(conn);
          if (conn.boardFeature.id === issue.id) {
            issues.push(conn.dependBoardFeature);
          } else {
            issues.push(conn.boardFeature);
          }
        }
      });
      this.heightLightIssueAndConnection.issues = issues;
      this.heightLightIssueAndConnection.connections = connections;
    } else if (connection && connection.id) {
      const connections = [connection];
      const { boardFeature, dependBoardFeature } = connection;
      const issues = [boardFeature, dependBoardFeature];
      this.heightLightIssueAndConnection.issues = issues;
      this.heightLightIssueAndConnection.connections = connections;
    } else {
      this.heightLightIssueAndConnection.issues = [];
      this.heightLightIssueAndConnection.connections = [];
    }
  }


  @action setOverIssue(overIssue) {
    this.overIssue = overIssue;
  }

  @action setAddingConnection(addingConnection) {
    this.addingConnection = addingConnection;
  }

  @action setSprintWidth(index, width) {
    if (width !== this.sprints[index].columnWidth) {
      // console.log(index, width);
      this.sprints[index].columnWidth = width;
    }
  }

  @action sortIssues = ({
    projectIndex, sprintIndex, index, issue: insertIssue, atIndex, id,
  }) => {
    if (this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures[atIndex]
      && this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures[atIndex].id === id) {
      return;
    }
    this.projects.forEach((project) => {
      project.teamSprints.forEach((sprint) => {
        sprint.boardFeatures.forEach((issue, i) => {
          if (issue.id === id) {
            sprint.boardFeatures.splice(i, 1);
            this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures.splice(atIndex, 0, insertIssue);
            // console.log(toJS(this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures))
            this.setConnectionsWhenDrag({
              ...insertIssue,
              sprintId: this.sprints[sprintIndex].sprintId,
              teamProjectId: this.projects[projectIndex].projectId,
            });
          }
        });
      });
    });
  }

  @action sortProjects = (index, atIndex) => {
    // console.log(index, atIndex);
    if (index === atIndex) {
      return;
    }
    const [project] = this.projects.splice(index, 1);
    this.projects.splice(atIndex, 0, project);
    // console.log(toJS(this.projects));
  }


  @action
  resetProject = (source) => {
    const { index, id } = source;
    const currentIndex = findIndex(this.projects, { boardTeamId: id });
    const [project] = this.projects.splice(currentIndex, 1);
    this.projects.splice(index, 0, project);
  }

  @action setConnectionsWhenDrag(issue) {
    this.connections.forEach((connection) => {
      const { boardFeature, dependBoardFeature } = connection;
      if (!boardFeature || !dependBoardFeature) {
        return;
      }
      if (boardFeature.id === issue.id) {
        boardFeature.sprintId = issue.sprintId;
        boardFeature.teamProjectId = issue.teamProjectId;
      }
      if (dependBoardFeature.id === issue.id) {
        dependBoardFeature.sprintId = issue.sprintId;
        dependBoardFeature.teamProjectId = issue.teamProjectId;
      }
    });
  }

  @action clearMovingIssue = (id) => {
    this.projects.forEach((project) => {
      project.teamSprints.forEach((sprint) => {
        sprint.boardFeatures.forEach((issue, i) => {
          if (issue.id === id) {
            sprint.boardFeatures.splice(i, 1);
            this.setConnectionsWhenDrag(issue);
          }
        });
      });
    });
  }

  @action resetMovingIssue = ({
    issue, sprintId, projectId, index,
  }) => {
    this.clearMovingIssue(issue.id);
    const dropIssues = this.findIssuesByProjectAndSprint(projectId, sprintId);
    if (!find(dropIssues, { id: issue.id })) {
      dropIssues.splice(index, 0, issue);
    }
  }

  @action addIssueToBoard = ({
    issue: insertIssue, atIndex, projectIndex, sprintIndex,
  }) => {
    if (this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures[atIndex]
      && this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures[atIndex].issueId === insertIssue.issueId) {
      return;
    }
    this.projects.forEach((project) => {
      project.teamSprints.forEach((sprint) => {
        sprint.boardFeatures.forEach((issue, i) => {
          // 找到之前不在板上的，删掉
          if (!issue.id && issue.issueId === insertIssue.issueId) {
            sprint.boardFeatures.splice(i, 1);
          }
        });
      });
    });
    this.projects[projectIndex].teamSprints[sprintIndex].boardFeatures.splice(atIndex, 0, insertIssue);
  }


  findIssuesByProjectAndSprint(projectId, sprintId) {
    const targetProject = find(this.projects, { projectId });
    const targetSprint = find(targetProject.teamSprints, { sprintId });
    return targetSprint.boardFeatures;
  }

  @computed get getProjectsHeight() {
    return this.projects.map((project) => {
      const { teamSprints } = project;
      const maxHeight = max(teamSprints.map((sprint, i) => Math.ceil(sprint.boardFeatures.length / this.sprints[i].columnWidth) || 1));
      return maxHeight;
    });
  }


  @action
  addConnection = (connection) => {
    this.connections.push(connection);
  }


  @action
  removeConnection(id) {
    remove(this.connections, { id });
  }


  @action
  removeFeatureAndConnection(issue) {
    const { id, sprintId, teamProjectId } = issue;
    const issues = this.findIssuesByProjectAndSprint(teamProjectId, sprintId);
    remove(issues, { id });
    remove(this.connections, connection => connection.boardFeature.id === id || connection.dependBoardFeature.id === id);
  }
}


export default new BoardStore();
