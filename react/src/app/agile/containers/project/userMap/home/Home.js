/* eslint-disable array-callback-return */
import React, { Component } from 'react';
import { observer } from 'mobx-react';
import _ from 'lodash';
import { toJS } from 'mobx';
import {
  Page, Header, Content, Permission,
} from 'choerodon-front-boot';
import {
  Button, Popover, Dropdown, Menu, Icon, Checkbox, Spin, message, Tooltip,
} from 'choerodon-ui';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import html2canvas from 'html2canvas';
import './Home.scss';
import QuickSearch from '../../../../components/QuickSearch';
import CreateEpic from '../component/CreateEpic';
import Backlog from '../component/Backlog/Backlog.js';
import EpicCard from '../component/EpicCard/EpicCard.js';
import IssueCard from '../component/IssueCard/IssueCard.js';
import CreateVOS from '../component/CreateVOS';
import CreateIssue from '../component/CreateIssue/CreateIssue.js';
// import epicPic from '../../../../assets/image/用户故事地图－空.svg';
import epicPic from '../../../../assets/image/emptyStory.svg';
import { getProjectsInProgram } from '../../../../api/CommonApi';

const FileSaver = require('file-saver');

const CheckboxGroup = Checkbox.Group;

// let scrollL;
const left = 0;
let flag = false;
let inWhich;

function toFullScreen(dom) {
  if (dom.requestFullscreen) {
    dom.requestFullscreen();
  } else if (dom.webkitRequestFullscreen) {
    dom.webkitRequestFullscreen();
  } else if (dom.mozRequestFullScreen) {
    dom.mozRequestFullScreen();
  } else {
    dom.msRequestFullscreen();
  }
}

function exitFullScreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.msExitFullscreen) {
    document.msExitFullscreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if (document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  }
}

function transformNull2Zero(val) {
  if (val === null) {
    return 0;
  }
  return val;
}

@observer
class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expandColumns: [],
      popOverVisible: false,
      showDoneEpicCheckbox: false,
      filterEpicCheckbox: false,
      isInProgram: false,
    };
  }

  componentDidMount() {
    const { UserMapStore } = this.props;
    window.addEventListener('keydown', this.onKeyDown);
    window.addEventListener('keyup', this.onKeyUp);
    flag = false;
    this.initData();
    const timer = setInterval(() => {
      if (document.getElementById('fixHead-body')) {
        document.getElementById('fixHead-head').addEventListener('scroll', this.handleScrollHead, { passive: true });
        document.getElementById('fixHead-body').addEventListener('scroll', this.handleScroll, { passive: true });
        document.getElementById('fixHead-head').addEventListener('mouseover', this.handleMouseOverHead);
        document.getElementById('fixHead-body').addEventListener('mouseover', this.handleMouseOverBody);
        // this.getPrepareOffsetTops();
        clearInterval(timer);
      }
    }, 20);
    document.addEventListener('fullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('webkitfullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('mozfullscreenchange', this.handleChangeFullScreen);
    document.addEventListener('MSFullscreenChange', this.handleChangeFullScreen);
    UserMapStore.setCurrentFilter(false, false, []);
    getProjectsInProgram().then((res) => {
      this.setState({
        isInProgram: Boolean(res),
      });
    });
  }

  componentWillUnmount() {
    const { UserMapStore } = this.props;
    UserMapStore.setCurrentFilter(false, false, []);
    UserMapStore.setMode('none');
    UserMapStore.setIssues([]);
    UserMapStore.setAssigneeFilterIds([]);
    UserMapStore.setEpics([]);
    UserMapStore.setTop(0);
    UserMapStore.setLeft(0);
    UserMapStore.setCurrentIndex(0);
    UserMapStore.setIsFullScreen(false);
    UserMapStore.setCacheIssues([]);
    if (document.getElementById('fixHead-head') && document.getElementById('fixHead-head')) {
      document.getElementById('fixHead-head').removeEventListener('scroll', this.handleScrollHead, { passive: true });
      document.getElementById('fixHead-body').removeEventListener('scroll', this.handleScroll, { passive: true });
      document.getElementById('fixHead-head').removeEventListener('mouseover', this.handleMouseOverHead);
      document.getElementById('fixHead-body').removeEventListener('mouseover', this.handleMouseOverBody);
    }
    document.removeEventListener('fullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('webkitfullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('mozfullscreenchange', this.handleChangeFullScreen);
    document.removeEventListener('MSFullscreenChange', this.handleChangeFullScreen);
    window.removeEventListener('keydown', this.onKeyDown);
    window.removeEventListener('keyup', this.onKeyUp);
    // 清除需求池，退出全屏
    UserMapStore.clearShowBacklog();
    this.exitFullScreen();
  }

  getPrepareOffsetTops = (isExpand = false) => {
    const { UserMapStore } = this.props;
    setTimeout(() => {
      const lines = document.getElementsByClassName('fixHead-line-content');
      const body = document.getElementById('fixHead-body');
      const offsetTops = [];
      for (let i = 0; i < lines.length; i += 1) {
        offsetTops.push(lines[i].offsetTop);
      }
      UserMapStore.setOffsetTops(offsetTops);
      // window.console.log('when change mode, the offsetTops is: ' + offsetTops);
      if (!isExpand) {
        const bodyTop = body.scrollTop;
        if (bodyTop) {
          body.scrollTop = 0;
          UserMapStore.setTop(0);
        }
        const { top, currentIndex } = UserMapStore;
        // window.console.log('when change mode, the top is: ' + top);
        const index = _.findLastIndex(offsetTops, v => v <= 0 + 42);
        if (currentIndex !== index && index !== -1) {
          UserMapStore.setCurrentIndex(index);
        }
      } else {
        const bodyTop = body.scrollTop;
        UserMapStore.setTop(bodyTop);
        const { top, currentIndex } = UserMapStore;
        // window.console.log('when change mode, the top is: ' + bodyTop);
        const index = _.findLastIndex(offsetTops, v => v <= top + 42);
        if (currentIndex !== index && index !== -1) {
          UserMapStore.setCurrentIndex(index);
        }
      }
    }, 1000);
  };

  /**
   *键盘按起事件
   *
   * @param {*} event
   * @memberof Sprint
   */
  onKeyUp=(event) => {
    if (document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA') {
      this.setState({
        keydown: '',
      });
    }
  }

  /**
   *键盘按下事件
   *
   * @param {*} event
   * @memberof Sprint
   */
  onKeyDown=(event) => {
    const { keydown } = this.state;
    const { UserMapStore } = this.props;
    if (document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA') {
      if (event.keyCode !== keydown) {
        this.setState({
          keydown: event.keyCode,
        });
        UserMapStore.setSelectIssueIds([]);
      }
    }
  }

  getSprintIdAndEpicId = (str) => {
    const epicId = parseInt(str.split('_')[0].split('-')[1], 10);
    const modeId = parseInt(str.split('_')[1], 10);// [sprint || version]id;
    return { epicId, modeId };
  }

  changeMode = (options) => {
    const { UserMapStore } = this.props;
    UserMapStore.setMode(options.key);
    const mode = options.key;
    // this.setState({ title: undefined, vosId: null });
    if (mode === 'sprint') {
      UserMapStore.loadSprints();
    } else if (mode === 'version') {
      UserMapStore.loadVersions();
    }
    UserMapStore.loadIssues('usermap');
    if (UserMapStore.showBackLog) {
      UserMapStore.loadBacklogIssues();
    }
  };

  showBackLog =() => {
    const { UserMapStore } = this.props;
    UserMapStore.changeShowBackLog();
  };

  handleCreateVOS=(type) => {
    const { UserMapStore } = this.props;
    UserMapStore.setCreateVOSType(type);
    UserMapStore.setCreateVOS(true);
  };

  handleCreateOk= () => {
    const { UserMapStore } = this.props;
    UserMapStore.setCreateVOS(false);
    const a = UserMapStore.getCreateVOSType === 'version' ? UserMapStore.loadVersions() : UserMapStore.loadSprints();
  };

  handleAddIssue = (epicId, vosId) => {
    const { UserMapStore } = this.props;
    const { mode } = UserMapStore;
    const obj = { epicId, [`${mode}Id`]: vosId };
    this.setState({ showChild: null });
    UserMapStore.setCurrentNewObj(obj);
  };


  handleShowDoneEpic =(e) => {
    const { UserMapStore } = this.props;
    this.setState({
      showDoneEpicCheckbox: e.target.checked,
    });
    UserMapStore.setShowDoneEpic(e.target.checked);
    UserMapStore.loadEpic();
  };

  handleFilterEpic =(e) => {
    const { UserMapStore } = this.props;
    this.setState({
      filterEpicCheckbox: e.target.checked,
    });
    UserMapStore.setIsApplyToEpic(e.target.checked);
    UserMapStore.loadEpic();
  }

  handleExpandColumn =(id) => {
    const { expandColumns } = this.state;
    const index = expandColumns.indexOf(id);
    if (index === -1) {
      expandColumns.push(id);
    } else {
      expandColumns.splice(index, 1);
    }
    this.setState({ expandColumns });
    // this.getPrepareOffsetTops(true);
    // this.handleScroll();
  };

  handleClickIssue = (issueId, epicId) => {
    const { UserMapStore } = this.props;
    const { selectIssueIds } = UserMapStore;
    let arr = _.cloneDeep(toJS(selectIssueIds));
    const index = arr.indexOf(issueId);
    const { keydown } = this.state;
    // command ctrl shift 支持多选
    if (keydown === 91 || keydown === 17 || keydown === 16) {
      if (index === -1) {
        arr.push(issueId);
      } else {
        arr.splice(index, 1);
      }
    } else {
      arr = [issueId];
      // arr.push(issueId);
    }
    if (issueId === 0) {
      arr = [];
    }
    UserMapStore.setSelectIssueIds(arr);
  };

  handleScrollHead = (e) => {
    if (inWhich !== 'header') return;
    const { scrollLeft } = e.target;
    const body = document.getElementById('fixHead-body');
    body.scrollLeft = scrollLeft;
    const ua = window.navigator.userAgent;
    const isSafari = ua.indexOf('Safari') !== -1 && ua.indexOf('Version') !== -1;
    if (isSafari) {
      document.getElementsByClassName('c7n-userMap')[0].style.setProperty('--left', `${scrollLeft}px`);
    }
  };

  handleMouseOverBody = (e) => {
    inWhich = 'body';
  }

  handleMouseOverHead = (e) => {
    inWhich = 'header';
  }

  handleChangeFullScreen = (e) => {
    // const node = e.target;
    const isFullScreen = document.webkitFullscreenElement
      || document.mozFullScreenElement
      || document.msFullscreenElement;
    // this.setState({
    //   isFullScreen: !!isFullScreen,
    // });
    const { UserMapStore } = this.props;
    UserMapStore.setIsFullScreen(!!isFullScreen);
  }

  handleFullScreen = () => {
    const isFullScreen = document.webkitFullscreenElement
      || document.mozFullScreenElement
      || document.msFullscreenElement;
    if (!isFullScreen) {
      this.fullScreen();
    } else {
      this.exitFullScreen();
    }
  }

  /**
   * 拖拽到史诗框内
   */
  handleEpicDrag =(res) => {
    // 不允许将非史诗拖拽到史诗列
    if (res.source && res.source.droppableId !== 'epic') return;
    const { UserMapStore } = this.props;
    const data = UserMapStore.getEpics;
    const result = Array.from(data);
    const sourceIndex = res.source.index;
    const tarIndex = res.destination.index;
    const [removed] = result.splice(sourceIndex, 1);
    result.splice(tarIndex, 0, removed);
    let beforeSequence = null;
    let afterSequence = null;
    if (tarIndex === 0) {
      afterSequence = result[1].epicSequence;
    } else if (tarIndex === data.length - 1) {
      beforeSequence = result[data.length - 2].epicSequence;
    } else {
      afterSequence = result[tarIndex + 1].epicSequence;
      beforeSequence = result[tarIndex - 1].epicSequence;
    }
    const epicId = data[sourceIndex].issueId;
    const { objectVersionNumber } = data[sourceIndex];
    const postData = {
      afterSequence, beforeSequence, epicId, objectVersionNumber,
    };
    UserMapStore.setEpics(result);
    UserMapStore.handleEpicDrag(postData);
  };

  handleScroll = (e) => {
    if (inWhich !== 'body') return;
    const { scrollLeft } = e.target;
    const header = document.getElementById('fixHead-head');
    header.scrollLeft = scrollLeft;
    const ua = window.navigator.userAgent;
    const isSafari = ua.indexOf('Safari') !== -1 && ua.indexOf('Version') !== -1;
    if (isSafari) {
      document.getElementsByClassName('c7n-userMap')[0].style.setProperty('--left', `${scrollLeft}px`);
    }
  };

  initData =() => {
    const { UserMapStore } = this.props;
    UserMapStore.axiosGetIssueTypes();
    UserMapStore.axiosGetDefaultPriority();
    UserMapStore.initData(true);
    UserMapStore.setShowDoneEpic(false);
    UserMapStore.setIsApplyToEpic(false);
    // UserMapStore.setCurrentFilter(false, false[]);
    this.setState({
      showDoneEpicCheckbox: false,
      filterEpicCheckbox: false,
    });
    const timer = setInterval(() => {
      if (document.getElementById('fixHead-body')) {
        document.getElementById('fixHead-head').addEventListener('scroll', this.handleScrollHead, { passive: true });
        document.getElementById('fixHead-body').addEventListener('scroll', this.handleScroll, { passive: true });
        document.getElementById('fixHead-head').addEventListener('mouseover', this.handleMouseOverHead);
        document.getElementById('fixHead-body').addEventListener('mouseover', this.handleMouseOverBody);
        // this.getPrepareOffsetTops();
        clearInterval(timer);
      }
    }, 20);
  };

  handleCreateEpic = () => {
    const { UserMapStore } = this.props;
    UserMapStore.setCreateEpic(true);
  }

  onQuickSearchChange = (onlyMeChecked, onlyStoryChecked, moreChecked) => {
    const { UserMapStore } = this.props;
    UserMapStore.setCurrentFilter(onlyMeChecked, onlyStoryChecked, moreChecked);
    UserMapStore.loadIssues('usermap');
    if (UserMapStore.isApplyToEpic) {
      UserMapStore.loadEpic();
    }
  }

  onAssigneeChange = (filters) => {
    const { UserMapStore } = this.props;
    UserMapStore.setAssigneeFilterIds(filters);
    UserMapStore.loadIssues('usermap');
    if (UserMapStore.isApplyToEpic) {
      UserMapStore.loadEpic();
    }
  }

  fullScreen = () => {
    const target = document.querySelector('.content');
    toFullScreen(target);
  };

  exitFullScreen = () => {
    exitFullScreen();
  }

  transformDateToPostDate = (ids, originIssues, mode, targetEpicId, targetModeId) => {
    const res = {
      epicIssueIds: [],
      sprintIssueIds: [],
      versionIssueIds: [],
    };
    const key = `${mode}Id`;
    ids.forEach((issueId) => {
      const issue = originIssues.find(v => v.issueId === issueId);
      if (transformNull2Zero(targetEpicId) !== transformNull2Zero(issue.epicId)) {
        res.epicIssueIds.push(issueId);
      }
      if (mode !== 'none' && transformNull2Zero(issue[key]) !== transformNull2Zero(targetModeId)) {
        res[`${mode}IssueIds`].push(issueId);
      }
    });
    return res;
  }

  handleDataWhenMove = (ids, before, outsetIssueId, mode, desEpicId, desModeId) => {
    const { UserMapStore } = this.props;
    const { issues, backlogIssues } = UserMapStore;
    const issuesCopy = UserMapStore.getCacheIssues;
    const backlogIssuesCopy = _.cloneDeep(toJS(backlogIssues));
    const issuesDragged = [];
    let resIssues = [];
    let resBacklogIssues = [];
    ids.forEach((issueId) => {
      const issue = issuesCopy.find(v => v.issueId === issueId);
      issue.epicId = desEpicId;
      if (mode !== 'none') {
        issue[`${mode}Id`] = desModeId;
      }
      issuesDragged.push(issue);
      const issuesIssueIndex = issuesCopy.findIndex(v => v.issueId === issueId);
      const backlogIssueIndex = backlogIssuesCopy.findIndex(v => v.issueId === issueId);
      if (issuesIssueIndex !== -1) {
        issuesCopy.splice(issuesIssueIndex, 1);
      }
      if (backlogIssueIndex !== -1) {
        backlogIssuesCopy.splice(backlogIssueIndex, 1);
      }
    });
    if (outsetIssueId === 0 && before) {
      if (desEpicId === 0 || desEpicId === null) {
        resIssues = issuesCopy;
        resBacklogIssues = issuesDragged.concat(backlogIssuesCopy);
      } else {
        resIssues = issuesDragged.concat(issuesCopy);
        resBacklogIssues = backlogIssuesCopy;
      }
    } else if (outsetIssueId === 0 && !before) {
      if (desEpicId === 0 || desEpicId === null) {
        resIssues = issuesCopy;
        resBacklogIssues = backlogIssuesCopy.concat(issuesDragged);
      } else {
        resIssues = issuesCopy.concat(issuesDragged);
        resBacklogIssues = backlogIssuesCopy;
      }
    } else if (before) {
      if (desEpicId === 0 || desEpicId === null) {
        resIssues = issuesCopy;
        const backlogInsertIndex = backlogIssuesCopy.findIndex(v => v.issueId === outsetIssueId);
        if (backlogInsertIndex !== -1) {
          if (backlogInsertIndex === 0) {
            resBacklogIssues = issuesDragged.concat(backlogIssuesCopy);
          } else {
            resBacklogIssues = [
              ...backlogIssuesCopy.slice(0, backlogInsertIndex),
              ...issuesDragged,
              ...backlogIssuesCopy.slice(backlogInsertIndex),
            ];
            // window.console.log(resBacklogIssues);
          }
        } else {
          resBacklogIssues = issuesDragged.concat(backlogIssuesCopy);
        }
      } else {
        resBacklogIssues = backlogIssuesCopy;
        const issuesInsertIndex = issuesCopy.findIndex(v => v.issueId === outsetIssueId);
        if (issuesInsertIndex !== -1) {
          if (issuesInsertIndex === 0) {
            resIssues = issuesDragged.concat(issuesCopy);
          } else {
            resIssues = [
              ...issuesCopy.slice(0, issuesInsertIndex),
              ...issuesDragged,
              ...issuesCopy.slice(issuesInsertIndex),
            ];
          }
        }
      }
    } else if (true) {
      if (desEpicId === 0 || desEpicId === null) {
        resIssues = issuesCopy;
        const backlogInsertIndex = issuesCopy.findIndex(v => v.issueId === outsetIssueId);
        if (backlogInsertIndex !== -1) {
          resBacklogIssues = [
            ...backlogIssuesCopy.slice(0, backlogInsertIndex + 1),
            ...issuesDragged,
            ...backlogIssuesCopy.slice(backlogInsertIndex + 1),
          ];
        } else {
          resBacklogIssues = backlogIssuesCopy.concat(issuesDragged);
        }
      } else {
        resBacklogIssues = backlogIssuesCopy;
        const issuesInsertIndex = issuesCopy.findIndex(v => v.issueId === outsetIssueId);
        if (issuesInsertIndex !== -1) {
          resIssues = [
            ...issuesCopy.slice(0, issuesInsertIndex + 1),
            ...issuesDragged,
            ...issuesCopy.slice(issuesInsertIndex + 1),
          ];
        }
      }
    }
    UserMapStore.setBacklogIssues(resBacklogIssues);
    UserMapStore.setIssues(resIssues);
    // window.console.log(resIssues);
  }

  handleMultipleDragToBoard = (res) => {
    const { UserMapStore } = this.props;
    const {
      mode, backlogIssues, selectIssueIds,
    } = UserMapStore;
    const issues = UserMapStore.getCacheIssues;
    const sourceIndex = res.source.index;
    const tarIndex = res.destination.index;
    const tarEpicId = parseInt(res.destination.droppableId.split('_')[0].split('-')[1], 10);
    const key = `${mode}Id`;
    const desIndex = res.destination.index;
    const dragIssueId = parseInt(res.draggableId.split('-')[1], 10);
    const desEpicId = this.getSprintIdAndEpicId(res.destination.droppableId).epicId;
    const desModeId = this.getSprintIdAndEpicId(res.destination.droppableId).modeId;
    const souModeId = this.getSprintIdAndEpicId(res.source.droppableId).modeId;
    const souEpicId = this.getSprintIdAndEpicId(res.source.droppableId).epicId;
    const issueIds = toJS(selectIssueIds);
    const issueData = _.cloneDeep(toJS(issues));
    const backlogData = _.cloneDeep(toJS(backlogIssues));
    let desEpicAndModeIssues;
    if (desModeId === 0) {
      const desEpicIssues = _.filter(issueData, issue => issue.epicId === desEpicId);
      if (mode === 'none') {
        desEpicAndModeIssues = desEpicIssues.slice();
      } else {
        desEpicAndModeIssues = _.filter(desEpicIssues, issue => issue[key] === 0
          || issue[key] === null);
      }
    } else {
      desEpicAndModeIssues = _.filter(issueData, issue => issue.epicId === desEpicId
        && issue[key] === desModeId);
    }
    let desModeIssues;
    if (desModeId === 0) {
      if (mode === 'none') {
        desModeIssues = issueData;
      } else {
        desModeIssues = _.filter(issueData, issue => issue[key] === 0 || issue[key] === null);
      }
    } else {
      desModeIssues = _.filter(issueData, issue => issue[key] === desModeId);
    }
    let before;
    let outsetIssueId;
    if (desEpicAndModeIssues.length) {
      // 移到有卡的块中
      if (desEpicAndModeIssues.every(v => issueIds.includes(v.issueId))) {
        // 该块中所有卡均被选中
        if (desModeIssues.every(v => issueIds.includes(v.issueId))) {
          // 该行中所有卡均被选中
          before = true;
          outsetIssueId = 0;
        } else {
          // 该行中有未被选中的卡
          before = true;
          outsetIssueId = desModeIssues.find(v => !issueIds.includes(v.issueId)).issueId;
        }
      } else if (true) {
        // 该块中有未被选中的卡
        if (_.map(desEpicAndModeIssues, 'issueId').includes(dragIssueId)) {
          // 移动卡属于该块
          if (desIndex === desEpicAndModeIssues.length - 1) {
            before = false;
            outsetIssueId = _.findLast(
              desEpicAndModeIssues,
              v => !issueIds.includes(v.issueId),
            ).issueId;
          } else if (true) {
            if (sourceIndex <= desIndex) {
              const afterDesIndex = _.find(
                desEpicAndModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex + 1,
              );
              const beforeDesIndex = _.findLast(
                desEpicAndModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex + 1,
              );
              if (afterDesIndex) {
                before = true;
                outsetIssueId = afterDesIndex.issueId;
              } else if (beforeDesIndex) {
                before = false;
                outsetIssueId = beforeDesIndex.issueId;
              } else {
                before = true;
                outsetIssueId = 0;
              }
            } else {
              const afterDesIndex = _.find(
                desEpicAndModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex,
              );
              const beforeDesIndex = _.findLast(
                desEpicAndModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex,
              );
              if (afterDesIndex) {
                before = true;
                outsetIssueId = afterDesIndex.issueId;
              } else if (beforeDesIndex) {
                before = false;
                outsetIssueId = beforeDesIndex.issueId;
              } else {
                before = true;
                outsetIssueId = 0;
              }
            }
          }
        } else if (true) {
          // 移动卡不属于该块
          if (desIndex === desEpicAndModeIssues.length) {
            before = false;
            outsetIssueId = _.findLast(
              desEpicAndModeIssues,
              v => !issueIds.includes(v.issueId),
            ).issueId;
          } else {
            const afterDesIndex = _.find(
              desEpicAndModeIssues,
              v => !issueIds.includes(v.issueId),
              desIndex,
            );
            const beforeDesIndex = _.findLast(
              desEpicAndModeIssues,
              v => !issueIds.includes(v.issueId),
              desIndex,
            );
            if (afterDesIndex) {
              before = true;
              outsetIssueId = afterDesIndex.issueId;
            } else if (beforeDesIndex) {
              before = false;
              outsetIssueId = beforeDesIndex.issueId;
            } else {
              before = true;
              outsetIssueId = 0;
            }
          }
        }
      }
    } else if (true) {
      // 移到无卡的块中
      if (!desModeIssues.length) {
        // 同行长度为0
        outsetIssueId = 0;
        if (souModeId && desModeId) {
          const modeData = UserMapStore[`${mode}s`] || [];
          const souModeIndex = _.findIndex(modeData, v => v[key] === souModeId);
          const desModeIndex = _.findIndex(modeData, v => v[key] === desModeId);
          before = desModeIndex < souModeIndex;
        } else if (!souModeId) {
          before = true;
        } else {
          before = false;
        }
      } else if (true) {
        // 同行长度不为0
        if (desModeIssues.every(v => issueIds.includes(v.issueId))) {
          // 所有选中都在同行中
          before = true;
          outsetIssueId = 0;
        } else {
          // 同行中有未被选中的
          before = true;
          outsetIssueId = desModeIssues.find(v => !issueIds.includes(v.issueId)).issueId;
        }
      }
    }
    const rankIndex = null;
    const transformData = this.transformDateToPostDate(
      issueIds,
      issues,
      mode,
      desEpicId,
      desModeId,
    );
    const postData = {
      before,
      epicId: transformData.epicIssueIds.length ? desEpicId : undefined,
      outsetIssueId,
      rankIndex,
      issueIds,
      versionIssueIds: transformData.versionIssueIds.length
        ? transformData.versionIssueIds : undefined,
      sprintIssueIds: transformData.sprintIssueIds.length
        ? transformData.sprintIssueIds : undefined,
      epicIssueIds: transformData.epicIssueIds.length ? transformData.epicIssueIds : undefined,
    };
    if (mode !== 'none' && transformData[`${mode}IssueIds`].length) {
      postData[key] = desModeId;
    }
    const tarBacklogData = backlogIssues;
    this.handleDataWhenMove(issueIds, before, outsetIssueId, mode, desEpicId, desModeId);
    UserMapStore.handleMoveIssue(postData);
    UserMapStore.setSelectIssueIds([]);
    UserMapStore.setCurrentDraggableId(null);
  }

  handleMultipleDragToBacklog = (res) => {
    const { UserMapStore } = this.props;
    const {
      mode, backlogIssues, selectIssueIds,
    } = UserMapStore;
    const issues = UserMapStore.getCacheIssues;
    const sourceIndex = res.source.index;
    const tarIndex = res.destination.index;
    const tarEpicId = parseInt(res.destination.droppableId.split('_')[0].split('-')[1], 10);
    const key = `${mode}Id`;
    const desIndex = res.destination.index;
    const dragIssueId = parseInt(res.draggableId.split('-')[1], 10);
    const desEpicId = this.getSprintIdAndEpicId(res.destination.droppableId).epicId;
    const desModeId = this.getSprintIdAndEpicId(res.destination.droppableId).modeId;
    const souModeId = this.getSprintIdAndEpicId(res.source.droppableId).modeId;
    const souEpicId = this.getSprintIdAndEpicId(res.source.droppableId).epicId;
    const issueIds = toJS(selectIssueIds);
    const issueData = _.cloneDeep(toJS(issues));
    const backlogData = _.cloneDeep(toJS(backlogIssues));
    let desModeIssues;
    if (desModeId === 0) {
      // const desEpicIssues = _.filter(issueData, issue => issue.epicId === desEpicId);
      if (mode === 'none') {
        desModeIssues = backlogData.slice();
        // desEpicAndModeIssues = desEpicIssues.slice();
      } else {
        desModeIssues = _.filter(backlogData, issue => issue[key] === 0
          || issue[key] === null);
      }
    } else {
      desModeIssues = _.filter(backlogData, issue => issue[key] === desModeId);
    }
    let desModeIssuesAll;
    if (desModeId === 0) {
      if (mode === 'none') {
        desModeIssuesAll = issueData;
      } else {
        desModeIssuesAll = _.filter(issueData, issue => issue[key] === 0 || issue[key] === null);
      }
    } else {
      desModeIssuesAll = _.filter(issueData, issue => issue[key] === desModeId);
    }
    let before;
    let outsetIssueId;
    if (desModeIssues.length) {
      // 移到有卡的块中
      if (desModeIssues.every(v => issueIds.includes(v.issueId))) {
        // 该块中所有卡均被选中
        if (desModeIssuesAll.every(v => issueIds.includes(v.issueId))) {
          // 该行中所有卡均被选中
          before = true;
          outsetIssueId = 0;
        } else {
          // 该行中有未被选中的卡
          before = true;
          outsetIssueId = desModeIssuesAll.find(v => !issueIds.includes(v.issueId)).issueId;
        }
      } else if (true) {
        // 该块中有未被选中的卡
        if (_.map(desModeIssues, 'issueId').includes(dragIssueId)) {
          if (desIndex === desModeIssues.length - 1) {
            before = false;
            outsetIssueId = _.findLast(desModeIssues, v => !issueIds.includes(v.issueId)).issueId;
          } else if (true) {
            if (sourceIndex <= desIndex) {
              const afterDesIndex = _.find(
                desModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex + 1,
              );
              const beforeDesIndex = _.findLast(
                desModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex + 1,
              );
              if (afterDesIndex) {
                before = true;
                outsetIssueId = afterDesIndex.issueId;
              } else if (beforeDesIndex) {
                before = false;
                outsetIssueId = beforeDesIndex.issueId;
              } else {
                before = true;
                outsetIssueId = 0;
              }
            } else {
              const afterDesIndex = _.find(
                desModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex,
              );
              const beforeDesIndex = _.findLast(
                desModeIssues,
                v => !issueIds.includes(v.issueId),
                desIndex,
              );
              if (afterDesIndex) {
                before = true;
                outsetIssueId = afterDesIndex.issueId;
              } else if (beforeDesIndex) {
                before = false;
                outsetIssueId = beforeDesIndex.issueId;
              } else {
                before = true;
                outsetIssueId = 0;
              }
            }
          }
        } else if (true) {
          if (desIndex === desModeIssues.length) {
            before = false;
            outsetIssueId = _.findLast(desModeIssues, v => !issueIds.includes(v.issueId)).issueId;
          } else if (true) {
            const afterDesIndex = _.find(
              desModeIssues,
              v => !issueIds.includes(v.issueId),
              desIndex,
            );
            const beforeDesIndex = _.findLast(
              desModeIssues,
              v => !issueIds.includes(v.issueId),
              desIndex,
            );
            if (afterDesIndex) {
              before = true;
              outsetIssueId = afterDesIndex.issueId;
            } else if (beforeDesIndex) {
              before = false;
              outsetIssueId = beforeDesIndex.issueId;
            } else {
              before = true;
              outsetIssueId = 0;
            }
          }
        }
      }
    } else if (true) {
      // 移到无卡的块中
      if (!desModeIssuesAll.length) {
        // 同行长度为0
        outsetIssueId = 0;
        if (souModeId && desModeId) {
          const modeData = UserMapStore[`${mode}s`] || [];
          const souModeIndex = _.findIndex(modeData, v => v[key] === souModeId);
          const desModeIndex = _.findIndex(modeData, v => v[key] === desModeId);
          before = desModeIndex < souModeIndex;
        } else if (!souModeId) {
          before = true;
        } else {
          before = false;
        }
      } else if (true) {
        // 同行长度不为0
        if (desModeIssuesAll.every(v => issueIds.includes(v.issueId))) {
          // 同行中均被选中
          before = true;
          outsetIssueId = 0;
        } else {
          // 同行中有未被选中的
          before = true;
          outsetIssueId = desModeIssuesAll.find(v => !issueIds.includes(v.issueId)).issueId;
        }
      }
    }
    const rankIndex = null;
    const transformData = this.transformDateToPostDate(issueIds, issues, mode, 0, desModeId);
    const postData = {
      before,
      epicId: transformData.epicIssueIds.length ? 0 : undefined,
      outsetIssueId,
      rankIndex,
      issueIds,
      versionIssueIds: transformData.versionIssueIds.length
        ? transformData.versionIssueIds : undefined,
      sprintIssueIds: transformData.sprintIssueIds.length
        ? transformData.sprintIssueIds : undefined,
      epicIssueIds: transformData.epicIssueIds.length ? transformData.epicIssueIds : undefined,
    };
    if (mode !== 'none' && transformData[`${mode}IssueIds`].length) {
      postData[key] = desModeId;
    }
    const tarBacklogData = backlogIssues;
    this.handleDataWhenMove(issueIds, before, outsetIssueId, mode, 0, desModeId);
    UserMapStore.handleMoveIssue(postData);
    UserMapStore.setSelectIssueIds([]);
    UserMapStore.setCurrentDraggableId(null);
  }

  handleDragToBoard = (res) => {
    const { UserMapStore } = this.props;
    const {
      mode, backlogIssues, selectIssueIds,
    } = UserMapStore;
    const issues = UserMapStore.getCacheIssues;
    // 不允许将史诗拖拽到非史诗列
    if (res.destination.droppableId !== 'epic' && res.source.droppableId === 'epic') {
      message.warning('无法将史诗移动到非史诗区域。');
      return;
    }
    // 只拖拽了一个issue
    if (selectIssueIds.length < 2) {
      // 拖拽到了同一列,不做处理
      if (res.destination.droppableId === res.source.droppableId
        && res.destination.index === res.source.index) return;
      const key = `${mode}Id`;
      const desIndex = res.destination.index;
      const desEpicId = this.getSprintIdAndEpicId(res.destination.droppableId).epicId;
      const desModeId = this.getSprintIdAndEpicId(res.destination.droppableId).modeId;
      const souModeId = this.getSprintIdAndEpicId(res.source.droppableId).modeId;
      const souEpicId = this.getSprintIdAndEpicId(res.source.droppableId).epicId;
      const issueIds = selectIssueIds.length ? toJS(selectIssueIds) : [parseInt(res.draggableId.split('-')[1], 10)];
      // 全部issue数据
      const issueData = _.cloneDeep(toJS(issues));
      // const backlogData = _.cloneDeep(toJS(backlogIssues));
      let desEpicAndModeIssues;
      if (desModeId === 0) {
        const desEpicIssues = _.filter(issueData, issue => issue.epicId === desEpicId);
        if (mode === 'none') {
          desEpicAndModeIssues = desEpicIssues.slice();
        } else {
          desEpicAndModeIssues = _.filter(desEpicIssues, issue => issue[key] === 0
            || issue[key] === null);
        }
      } else {
        desEpicAndModeIssues = _.filter(issueData, issue => issue.epicId === desEpicId
          && issue[key] === desModeId);
      }
      let before;
      let outsetIssueId;
      if (desEpicAndModeIssues.length) {
        // 目的地块中有卡，判断所放位置是否为0
        if (!desIndex) {
          before = true;
          outsetIssueId = desEpicAndModeIssues[0].issueId;
        } else if (desEpicId === souEpicId && desModeId === souModeId) {
          // 同块之间移动，判断是否放在最后
          if (desIndex === desEpicAndModeIssues.length - 1) {
            before = false;
            outsetIssueId = desEpicAndModeIssues[desEpicAndModeIssues.length - 1].issueId;
          } else {
            before = true;
            if (desIndex > res.source.index) {
              outsetIssueId = desEpicAndModeIssues[desIndex + 1].issueId;
            } else {
              outsetIssueId = desEpicAndModeIssues[desIndex].issueId;
            }
          }
        } else if (true) {
          // 不同块之间移动，判断是否放在最后
          if (desIndex === desEpicAndModeIssues.length) {
            before = false;
            outsetIssueId = desEpicAndModeIssues[desEpicAndModeIssues.length - 1].issueId;
          } else {
            before = true;
            outsetIssueId = desEpicAndModeIssues[desIndex].issueId;
          }
        }
      } else {
        // 目的地块中无卡，判断同行是否为空
        let desModeIssues;
        if (desModeId === 0) {
          if (mode === 'none') {
            desModeIssues = issueData;
          } else {
            desModeIssues = _.filter(issueData, issue => issue[key] === 0 || issue[key] === null);
          }
        } else {
          desModeIssues = _.filter(issueData, issue => issue[key] === desModeId);
        }
        if (desModeId === souModeId) {
          // 同行之间移动
          if (desModeIssues.length === 1) {
            // 长度为1，该行只有一张卡，就是移动的卡
            before = true;
            outsetIssueId = 0;
          } else {
            // 该行有除了移动卡的卡，放在之前
            before = true;
            outsetIssueId = desModeIssues[0].issueId === parseInt(res.draggableId.split('-')[1], 10)
              ? desModeIssues[1].issueId : desModeIssues[0].issueId;
          }
        } else if (true) {
          if (desModeIssues.length) {
            before = true;
            outsetIssueId = desModeIssues[0].issueId;
          } else {
            outsetIssueId = 0;
            if (souModeId && desModeId) {
              const modeData = UserMapStore[`${mode}s`] || [];
              const souModeIndex = _.findIndex(modeData, v => v[key] === souModeId);
              const desModeIndex = _.findIndex(modeData, v => v[key] === desModeId);
              before = desModeIndex < souModeIndex;
            } else if (!souModeId) {
              before = true;
            } else {
              before = false;
            }
          }
        }
      }
      const rankIndex = null;
      const transformData = this.transformDateToPostDate(
        issueIds,
        issues,
        mode,
        desEpicId,
        desModeId,
      );
      const postData = {
        before,
        epicId: transformData.epicIssueIds.length ? desEpicId : undefined,
        outsetIssueId,
        rankIndex,
        issueIds,
        versionIssueIds: transformData.versionIssueIds.length
          ? transformData.versionIssueIds : undefined,
        sprintIssueIds: transformData.sprintIssueIds.length
          ? transformData.sprintIssueIds : undefined,
        epicIssueIds: transformData.epicIssueIds.length ? transformData.epicIssueIds : undefined,
      };
      if (mode !== 'none' && transformData[`${mode}IssueIds`].length) {
        postData[key] = desModeId;
      }
      const tarBacklogData = backlogIssues;

      this.handleDataWhenMove(issueIds, before, outsetIssueId, mode, desEpicId, desModeId);
      UserMapStore.handleMoveIssue(postData);
      UserMapStore.setSelectIssueIds([]);
      UserMapStore.setCurrentDraggableId(null);
    } else {
      this.handleMultipleDragToBoard(res);
    }
  };

  handleDragToBacklog = (res) => {
    const { UserMapStore } = this.props;
    const {
      mode, backlogIssues, selectIssueIds,
    } = UserMapStore;
    // 不允许将史诗拖拽到代办
    if (res.source && res.source.droppableId === 'epic') return;
    const issues = UserMapStore.getCacheIssues;
    if (selectIssueIds.length < 2) {
      if (res.destination.droppableId === res.source.droppableId
        && res.destination.index === res.source.index) return;
      const key = `${mode}Id`;
      const desIndex = res.destination.index;
      // const desEpicId = this.getSprintIdAndEpicId(res.destination.droppableId).epicId;
      const desModeId = this.getSprintIdAndEpicId(res.destination.droppableId).modeId;
      const souModeId = this.getSprintIdAndEpicId(res.source.droppableId).modeId;
      const souEpicId = this.getSprintIdAndEpicId(res.source.droppableId).epicId;
      const issueIds = selectIssueIds.length ? toJS(selectIssueIds) : [parseInt(res.draggableId.split('-')[1], 10)];
      const issueData = _.cloneDeep(toJS(issues));
      const backlogData = _.cloneDeep(toJS(backlogIssues));
      // let desEpicAndModeIssues;
      let desModeIssues;
      if (mode === 'none') {
        desModeIssues = backlogData.slice();
      } else if (true) {
        if (desModeId === 0) {
          desModeIssues = _.filter(backlogData, issue => issue[key] === 0 || issue[key] === null);
        } else {
          desModeIssues = _.filter(backlogData, issue => issue[key] === desModeId);
        }
      }
      let before;
      let outsetIssueId;
      if (desModeIssues.length) {
        // 目的地块中有卡，判断所放位置是否为0
        if (!desIndex) {
          before = true;
          outsetIssueId = desModeIssues[0].issueId;
        } else if (desModeId === souModeId) {
          // 同块之间移动，判断是否放在最后
          if (desIndex === desModeIssues.length - 1) {
            before = false;
            outsetIssueId = desModeIssues[desModeIssues.length - 1].issueId;
          } else {
            before = true;
            if (desIndex > res.source.index) {
              outsetIssueId = desModeIssues[desIndex + 1].issueId;
            } else {
              outsetIssueId = desModeIssues[desIndex].issueId;
            }
          }
        } else if (true) {
          // 不同块之间移动，判断是否放在最后
          if (desIndex === desModeIssues.length) {
            before = false;
            outsetIssueId = desModeIssues[desModeIssues.length - 1].issueId;
          } else {
            before = true;
            outsetIssueId = desModeIssues[desIndex].issueId;
          }
        }
      } else {
        // 目的地块中无卡，判断同行是否为空
        let desModeAllIssues;
        if (desModeId === 0) {
          if (mode === 'none') {
            desModeAllIssues = issueData;
          } else {
            desModeAllIssues = _.filter(
              issueData,
              issue => issue[key] === 0 || issue[key] === null,
            );
          }
        } else {
          desModeAllIssues = _.filter(issueData, issue => issue[key] === desModeId);
        }
        if (desModeId === souModeId) {
          // 同行之间移动
          if (desModeAllIssues.length === 1) {
            // 长度为1，该行只有一张卡，就是移动的卡
            before = true;
            outsetIssueId = 0;
          } else {
            // 该行有除了移动卡的卡，放在之前
            before = true;
            outsetIssueId = desModeAllIssues[0].issueId === parseInt(res.draggableId.split('-')[1], 10)
              ? desModeAllIssues[1].issueId : desModeAllIssues[0].issueId;
          }
        } else if (true) {
          // 不同行之间移动，放到空块里
          if (desModeAllIssues.length) {
            before = true;
            outsetIssueId = desModeAllIssues[0].issueId;
          } else {
            outsetIssueId = 0;
            if (souModeId && desModeId) {
              const modeData = UserMapStore[`${mode}s`] || [];
              const souModeIndex = _.findIndex(modeData, v => v[key] === souModeId);
              const desModeIndex = _.findIndex(modeData, v => v[key] === desModeId);
              before = desModeIndex < souModeIndex;
            } else if (!souModeId) {
              before = true;
            } else {
              before = false;
            }
          }
        }
      }
      const rankIndex = null;
      const transformData = this.transformDateToPostDate(issueIds, issues, mode, 0, desModeId);
      const postData = {
        before,
        epicId: transformData.epicIssueIds.length ? 0 : undefined,
        outsetIssueId,
        rankIndex,
        issueIds,
        versionIssueIds: transformData.versionIssueIds.length
          ? transformData.versionIssueIds : undefined,
        sprintIssueIds: transformData.sprintIssueIds.length
          ? transformData.sprintIssueIds : undefined,
        epicIssueIds: transformData.epicIssueIds.length ? transformData.epicIssueIds : undefined,
      };
      if (mode !== 'none' && transformData[`${mode}IssueIds`].length) {
        postData[key] = desModeId;
      }
      const tarBacklogData = backlogIssues;
      this.handleDataWhenMove(issueIds, before, outsetIssueId, mode, 0, desModeId);
      UserMapStore.handleMoveIssue(postData);
      UserMapStore.setSelectIssueIds([]);
      UserMapStore.setCurrentDraggableId(null);
    } else {
      this.handleMultipleDragToBacklog(res);
    }
  };

  handleEpicOrIssueDrag = (res) => {
    const { UserMapStore } = this.props;
    // 拖动到可拖动范围外
    if (!res.destination) {
      UserMapStore.setSelectIssueIds([]);
      UserMapStore.setCurrentDraggableId(null);
      return;
    }
    if (res.destination.droppableId === 'epic') {
      this.handleEpicDrag(res);
    } else if (res.destination.droppableId.includes('backlog')) {
      this.handleDragToBacklog(res);
    } else {
      this.handleDragToBoard(res);
    }
  };

  handleEpicOrIssueDragStart =(res) => {
    const { UserMapStore } = this.props;
    if (!(res.source.droppableId === 'epic') && UserMapStore.selectIssueIds.length) {
      UserMapStore.setCurrentDraggableId(parseInt(res.draggableId.split('-')[1], 10));
    }
  };

  handleSaveAsImage = () => {
    const { UserMapStore } = this.props;
    UserMapStore.saveChangeShowBackLog();
    this.setState({
      popOverVisible: false,
    });

    message.config({
      top: 110,
      duration: 2,
    });
    const shareContent = document.querySelector('.fixHead');// 需要截图的包裹的（原生的）DOM 对象
    const shareContentWidth = shareContent.style.width;
    const shareContentHeight = shareContent.style.height;
    shareContent.style.width = `${Math.max(document.querySelector('.fixHead-head').scrollWidth, document.querySelector('.fixHead-body').scrollWidth)}px`;
    shareContent.style.height = `${document.querySelector('.fixHead-head').scrollHeight + document.querySelector('.fixHead-body').scrollHeight}px`;

    const scaleBy = 2;
    const canvas = document.createElement('canvas');
    canvas.style.width = `${_.parseInt(_.trim(shareContent.style.width, 'px')) * scaleBy}px`;
    canvas.style.height = `${_.parseInt(_.trim(shareContent.style.height, 'px')) * scaleBy}px`;
    const context = canvas.getContext('2d');
    context.scale(scaleBy, scaleBy);

    const opts = {
      useCORS: true, // 【重要】开启跨域配置
      dpi: window.devicePixelRatio,
      canvas,
      scale: scaleBy,
      width: _.parseInt(_.trim(shareContent.style.width, 'px')),
      height: _.parseInt(_.trim(shareContent.style.height, 'px')),
    };

    html2canvas(shareContent, opts)
      .then((pcanvas) => {
        pcanvas.toBlob((blob) => {
          FileSaver.saveAs(blob, '用户故事地图.png');
        });
        shareContent.style.width = shareContentWidth;
        shareContent.style.height = shareContentHeight;

        Choerodon.prompt('导出图片成功');
      })
      .catch((error) => {
        Choerodon.prompt('导出图片失败');
      });
  }

  getHistoryCount = (id) => {
    const { UserMapStore } = this.props;
    const { issues, mode } = UserMapStore;
    const count = {};
    let issuesData = issues;
    if (mode !== 'none') {
      issuesData = _.filter(issues, issue => issue[`${mode}Id`] === id && issue.epicId !== 0);
    } else {
      issuesData = _.filter(issues, issue => issue.epicId !== 0);
    }
    count.todoCount = _.reduce(issuesData, (sum, issue) => {
      if (issue.statusCode === 'todo') {
        return sum + issue.storyPoints;
      } else {
        return sum;
      }
    }, 0);
    count.doneCount = _.reduce(issuesData, (sum, issue) => {
      if (issue.statusCode === 'done') {
        return sum + issue.storyPoints;
      } else {
        return sum;
      }
    }, 0);
    count.doingCount = _.reduce(issuesData, (sum, issue) => {
      if (issue.statusCode === 'doing') {
        return sum + issue.storyPoints;
      } else {
        return sum;
      }
    }, 0);
    return count;
  };

  handleInitScroll = () => {
    if (document.getElementById('fixHead-body')) {
      document.getElementById('fixHead-head').addEventListener('scroll', this.handleScrollHead, { passive: true });
      document.getElementById('fixHead-body').addEventListener('scroll', this.handleScroll, { passive: true });
      document.getElementById('fixHead-head').addEventListener('mouseover', this.handleMouseOverHead);
      document.getElementById('fixHead-body').addEventListener('mouseover', this.handleMouseOverBody);
      flag = true;
    }
  }

  handleCalcStoryPoints = (issues, vos, id, type, mode) => {
    // _.reduce(_.filter(issues, issue => issue.epicId !== 0
    //   && ((mode !== 'none' && issue[id] == null) || mode === 'none')), (sum, issue) => {
    //   if (issue.statusMapDTO && issue.statusMapDTO.type === 'todo') {
    //     return sum + (issue.storyPoints || 0);
    //   } else {
    //     return sum;
    //   }
    // }, 0).toFixed(1)

    const storyPoints = _.reduce(_.filter(
      issues,
      issue => (!mode && vos ? (issue[id] === vos[id] && issue.epicId !== 0) : (issue.epicId !== 0
        && ((mode !== 'none' && issue[id] == null) || mode === 'none'))),
    ),
    (sum, issue) => {
      if (issue.statusMapDTO && issue.statusMapDTO.type === type) {
        return sum + (issue.storyPoints || 0);
      } else {
        return sum;
      }
    }, 0);
    return Math.floor(storyPoints) === storyPoints ? storyPoints : storyPoints.toFixed(1);
  }

  renderHeader = () => {
    const { UserMapStore } = this.props;
    const { showDoneEpicCheckbox, filterEpicCheckbox, popOverVisible } = this.state;
    const {
      mode,
    } = UserMapStore;
    const swimlanMenu = (
      <Menu onClick={this.changeMode} selectable defaultSelectedKeys={[mode]}>
        <Menu.Item key="none">无泳道</Menu.Item>
        <Menu.Item key="version">版本泳道</Menu.Item>
        <Menu.Item key="sprint">冲刺泳道</Menu.Item>
      </Menu>
    );
    return (
      <Header title="故事地图">
        {/* {!this.state.isFullScreen ?
          <Button className="leftBtn" functyp="flat" onClick={this.handleCreateEpic}>
            <Icon type="playlist_add" />
            {'创建史诗'}
          </Button> : ''
        } */}

        {!UserMapStore.isFullScreen
          ? (
            <Button className="leftBtn" functyp="flat" onClick={this.handleCreateEpic}>
              <Icon type="playlist_add" />
              {'创建史诗'}
            </Button>
          ) : ''
        }
        <Dropdown
          overlay={swimlanMenu}
          trigger={['click']}
          overlayClassName="modeMenu"
          placement="bottomCenter"
          getPopupContainer={triggerNode => triggerNode}
        >
          <Button>
            {mode === 'none' && '无泳道'}
            {mode === 'version' && '版本泳道'}
            {mode === 'sprint' && '冲刺泳道'}
            <Icon type="arrow_drop_down" />
          </Button>
        </Dropdown>
        <Popover
          getPopupContainer={triggerNode => triggerNode}
          overlayClassName="moreMenuPopover"
          arrowPointAtCenter={false}
          placement="bottom"
          trigger={['click']}
          visible={popOverVisible}
          onVisibleChange={(visible) => {
            this.setState({
              popOverVisible: visible,
            });
          }}
          content={(
            <div>
              <div className="menu-title">史诗过滤选择器</div>
              <div style={{ height: 30, padding: '5px 12px' }}>
                <Checkbox className="showDoneEpicCheckbox" onChange={this.handleShowDoneEpic} checked={showDoneEpicCheckbox}>显示已完成的史诗</Checkbox>
              </div>
              <div style={{ height: 30, padding: '5px 12px' }}>
                <Checkbox className="filterEpicCheckbox" onChange={this.handleFilterEpic} checked={filterEpicCheckbox}>应用搜索到史诗</Checkbox>
              </div>
              <div className="menu-title">导出</div>
              <div
                onClick={this.handleSaveAsImage}
                role="none"
                style={{
                  height: 30, padding: '5px 12px', marginLeft: 26, cursor: 'pointer',
                }}
              >
                {'导出为png格式'}
              </div>
            </div>
          )}
        >
          <Button>
            {'更多'}
            <Icon type="arrow_drop_down" />
          </Button>
        </Popover>
        <Button className="leftBtn2" funcType="flat" onClick={this.initData.bind(this, true)}>
          <Icon type="refresh icon" />
          <span>刷新</span>
        </Button>
        {/* <Button className="leftBtn2" funcType="flat" onClick={this.handleFullScreen.bind(this)}>
          <Icon type={`${this.state.isFullScreen ? 'exit_full_screen' : 'zoom_out_map'} icon`} />
          <span>{this.state.isFullScreen ? '退出全屏' : '全屏'}</span>
        </Button> */}
        <Button className="leftBtn2" funcType="flat" onClick={this.handleFullScreen.bind(this)}>
          <Icon type={`${UserMapStore.isFullScreen ? 'exit_full_screen' : 'zoom_out_map'} icon`} />
          <span>{UserMapStore.isFullScreen ? '退出全屏' : '全屏'}</span>
        </Button>
        {
          UserMapStore.getEpics.length ? (
            <Button
              style={{
                color: 'white', fontSize: 12, position: 'absolute', right: 24,
              }}
              type="primary"
              funcType="raised"
              onClick={this.showBackLog}
            >
              <Icon type="layers" />
              {'需求池'}
            </Button>
          ) : null
        }
      </Header>
    );
  }

  renderBody = () => {
    const { UserMapStore } = this.props;
    const { expandColumns, showChild, isInProgram } = this.state;
    const dom = [];
    const epicData = UserMapStore.getEpics;
    const {
      issues, sprints, versions, currentNewObj, top, selectIssueIds, currentDraggableId,
    } = UserMapStore;
    const { epicId, versionId, sprintId } = currentNewObj;
    const { mode } = UserMapStore;
    const vosData = UserMapStore[`${mode}s`] || [];
    const id = `${mode}Id`;
    

    if (epicData.length) {
      vosData.map((vos, vosIndex) => {
        const name = mode === 'sprint' ? `${mode}Name` : 'name';
        const todoStoryPoints = this.handleCalcStoryPoints(issues, vos, id, 'todo');
        const doingStoryPoints = this.handleCalcStoryPoints(issues, vos, id, 'doing');
        const doneStoryPoints = this.handleCalcStoryPoints(issues, vos, id, 'done');
        dom.push(
          <React.Fragment key={vos[id]}>
            <div
              className={`fixHead-line-title title-transform ${vosIndex === 0 ? 'firstLine-titles' : ''}`}
            >
              <div>{vos[name]}</div>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <Tooltip title={`待处理故事点: ${todoStoryPoints}`}>
                  <p className="point-span" style={{ background: '#4D90FE' }}>
                    {todoStoryPoints}
                  </p>
                </Tooltip>
                <Tooltip title={`处理中故事点：${doingStoryPoints}`}>
                  <p className="point-span" style={{ background: '#FFB100' }}>
                    {doingStoryPoints}
                  </p>
                </Tooltip>
                <Tooltip title={`已完成故事点: ${doneStoryPoints}`}>
                  <p className="point-span" style={{ background: '#00BFA5' }}>
                    {doneStoryPoints}
                  </p>
                </Tooltip>
                <Button shape="circle" className="expand-btn" onClick={this.handleExpandColumn.bind(this, vos[id])} role="none">
                  <Icon type={`${expandColumns.includes(vos[id]) ? 'baseline-arrow_drop_up' : 'baseline-arrow_drop_down'}`} />
                </Button>
              </div>
            </div>
            <div
              className="fixHead-line-content"
              style={{ display: 'flex', height: expandColumns.includes(vos[id]) ? 1 : '', overflow: expandColumns.includes(vos[id]) ? 'hidden' : 'visible' }}
              data-title={vos[name]}
              data-id={vos[id]}
            >
              {epicData.map((epic, index) => (
                <Droppable droppableId={`epic-${epic.issueId}_${vos[id]}`} key={`epic-${epic.issueId}_${vos[id]}`}>
                  {(provided, snapshot) => (
                    <div
                      ref={provided.innerRef}
                      className="swimlane-column fixHead-block"
                      style={{
                        background: snapshot.isDraggingOver ? '#f0f0f0' : '',
                        padding: 'grid',
                      }}
                    >
                      <React.Fragment>
                        {_.filter(
                          issues,
                          issue => issue.epicId === epic.issueId && issue[id] === vos[id],
                        ).map((item, indexs) => (
                          <IssueCard
                            draggableId={`${mode}-${item.issueId}`}
                            index={indexs}
                            selected={selectIssueIds.includes(item.issueId)}
                            dragged={currentDraggableId === item.issueId}
                            handleClickIssue={this.handleClickIssue}
                            key={item.issueId}
                            issue={item}
                            borderTop={indexs === 0}
                            showDelete={!UserMapStore.isFullScreen}
                          />
                        ))}
                        {
                        epicId === epic.issueId && currentNewObj[id] === vos[id] ? (
                          <CreateIssue
                            store={UserMapStore}
                            data={{ epicId: epic.issueId, [id]: vos[id] }}
                            onOk={() => {
                              UserMapStore.initData(false);
                              this.setState({ showChild: null });
                            }}
                            onCancel={() => {
                              this.handleAddIssue(0, 0);
                            }}
                          />
                        ) : null
                      }
                        <div
                          role="none"
                          onClick={this.handleClickIssue.bind(this, 0)}
                          className="maskIssue"
                          onMouseLeave={() => { this.setState({ showChild: null }); }}
                          onMouseEnter={() => {
                            if (snapshot.isDraggingOver) return;
                            this.setState({ showChild: `${epic.issueId}-${vos[id]}` });
                          }}
                        >
                          <div style={{ fontWeight: '500', display: !snapshot.isDraggingOver && showChild === `${epic.issueId}-${vos[id]}` ? 'block' : 'none' }}>
                            {/* {'Add'} */}
                            <a role="none" onClick={this.handleAddIssue.bind(this, epic.issueId, vos[id])}>新建问题</a>
                            {' '}
                            {'或 '}
                            <a role="none" onClick={this.showBackLog}>从需求池引入</a>
                          </div>
                        </div>
                      </React.Fragment>
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>

              ))}
            </div>
          </React.Fragment>,
        );
      });
      const unPlanTodoStoryPoints = this.handleCalcStoryPoints(issues, undefined, id, 'todo', mode); 
      const unPlanDoingStoryPoints = this.handleCalcStoryPoints(issues, undefined, id, 'doing', mode);
      dom.push(
        <React.Fragment key="no-sprint">
          <div
            className={`fixHead-line-title column-title title-transform ${vosData.length ? '' : 'firstLine-titles'}`}
            title={mode === 'none' ? '问题' : '未计划部分'}
            data-id={-1}
            // style={{ transform: `translateX(${`${left}px`}) translateZ(0)` }}
          >
            <div>
              {mode === 'none' ? '问题' : '未计划部分' }
              {mode === 'none' || UserMapStore.isFullScreen ? null
                : (
                  <React.Fragment>
                    {mode === 'version'
                      ? (
                        <Permission service={['agile-service.product-version.createVersion']}>
                          <Button className="createSpringBtn" functyp="flat" onClick={this.handleCreateVOS.bind(this, mode)}>
                            <Icon type="playlist_add" />
                            {'创建版本'}
                          </Button>
                        </Permission>
                      ) : ''
                    }
                    {mode === 'sprint' && !isInProgram
                      ? (
                        <Button className="createSpringBtn" functyp="flat" onClick={this.handleCreateVOS.bind(this, mode)}>
                          <Icon type="playlist_add" />
                          {'冲刺'}
                        </Button>
                      ) : ''
                    }
                  </React.Fragment>
                ) }
            </div>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Tooltip title={`待处理故事点：${unPlanTodoStoryPoints}`}>
                <p className="point-span" style={{ background: '#4D90FE' }}>
                  {
                    unPlanTodoStoryPoints
                  }
                </p>
              </Tooltip>
              <Tooltip title={`处理中故事点：${unPlanDoingStoryPoints}`}>
                <p className="point-span" style={{ background: '#FFB100' }}>
                  {unPlanDoingStoryPoints}
                </p>
              </Tooltip>
            </div>
          </div>
          <div
            className="fixHead-line-content"
            style={{
              display: 'flex',
              height: expandColumns.includes(`-1-${mode}`) ? 1 : '',
              overflow: expandColumns.includes(`-1-${mode}`) ? 'hidden' : 'visible',
            }}
            data-title={mode === 'none' ? 'issue' : '未计划部分'}
            data-id={-1}
          >
            {epicData.map((epic, index) => (
              <Droppable droppableId={`epic-${epic.issueId}_0`} key={epic.issueId}>
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    className="fixHead-block swimlane-column"
                    style={{
                      background: snapshot.isDraggingOver ? '#f0f0f0' : '',
                      padding: 'grid',
                      // borderBottom: '1px solid rgba(0,0,0,0.12)'
                    }}
                  >
                    <React.Fragment>
                      {_.filter(issues, issue => issue.epicId === epic.issueId
                        && (mode === 'none' || ((issue[id] && issue[id] === 0) || !issue[id]))).map((item, indexs) => (
                          <IssueCard
                            draggableId={`${mode}-${item.issueId}`}
                            index={indexs}
                            selected={selectIssueIds.includes(item.issueId)}
                            dragged={currentDraggableId === item.issueId}
                            handleClickIssue={this.handleClickIssue}
                            key={item.issueId}
                            issue={item}
                            borderTop={indexs === 0}
                            showDelete={!UserMapStore.isFullScreen}
                          />
                      ))}
                      {
                        epicId === epic.issueId && currentNewObj[id] === 0 ? (
                          <CreateIssue
                            store={UserMapStore}
                            data={{ epicId: epic.issueId, [`${mode}Id`]: 0 }}
                            onOk={() => {
                              UserMapStore.initData(false);
                              this.setState({ showChild: null });
                            }}
                            onCancel={() => {
                              this.handleAddIssue(0, 0);
                            }}
                          />
                        ) : null
                      }
                      <div
                        role="none"
                        className="maskIssue"
                        onClick={this.handleClickIssue.bind(this, 0)}
                        onMouseLeave={() => { this.setState({ showChild: null }); }}
                        onMouseEnter={() => {
                          if (snapshot.isDraggingOver) return;
                          this.setState({ showChild: epic.issueId });
                        }}
                      >
                        <div style={{ fontWeight: '500', display: !snapshot.isDraggingOver && showChild === epic.issueId ? 'block' : 'none' }}>
                          <a role="none" onClick={this.handleAddIssue.bind(this, epic.issueId, 0)}>新建问题</a>
                          {' '}
                          {'或 '}
                          <a role="none" onClick={this.showBackLog}>从需求池引入</a>
                        </div>
                      </div>
                    </React.Fragment>
                    {provided.placeholder}
                  </div>

                )}
              </Droppable>
            ))}
          </div>
        </React.Fragment>,
      );
    }
    return dom;
  };

  render() {
    const { UserMapStore } = this.props;
    const epicData = UserMapStore.getEpics;
    if (!flag) {
      this.handleInitScroll();
    }
    const timer = setInterval(() => {
      if (document.getElementById('fixHead-body')) {
        document.getElementById('fixHead-head').addEventListener('scroll', this.handleScrollHead, { passive: true });
        document.getElementById('fixHead-body').addEventListener('scroll', this.handleScroll, { passive: true });
        document.getElementById('fixHead-head').addEventListener('mouseover', this.handleMouseOverHead);
        document.getElementById('fixHead-body').addEventListener('mouseover', this.handleMouseOverBody);
        // this.getPrepareOffsetTops();
        clearInterval(timer);
      }
    }, 20);
    const {
      filters, mode, createEpic, currentFilters, showBackLog, isLoading,
    } = UserMapStore;
    const firstTitle = '';
    const count = this.getHistoryCount(UserMapStore.getVosId);
    const vosId = UserMapStore.getVosId === 0 ? `-1-${mode}` : UserMapStore.getVosId;
    let showDone = true;
    if (UserMapStore.getVosId === 0) {
      showDone = false;
    }

    return (
      <Page
        className="c7n-userMap"
        service={['agile-service.issue.deleteIssue', 'agile-service.issue.listEpic']}
      >
        {this.renderHeader()}
        <div style={{ padding: 0, paddingLeft: 24, overflow: 'unset' }}>
          <QuickSearch
            moreSelection={UserMapStore.getFilters}
            onQuickSearchChange={this.onQuickSearchChange}
            onAssigneeChange={this.onAssigneeChange}
          />
        </div>

        <Content style={{ padding: 0, height: '100%', paddingLeft: 24 }}>
          {/* eslint-disable */
            isLoading
           /* eslint-enable */
              ? (
                <div style={{
                  display: 'flex', height: '100%', justifyContent: 'center', alignItems: 'center',
                }}
                >
                  <Spin spinning={isLoading} />
                </div>
              )
              : epicData.length
                ? (
                  <div style={{ padding: 0, height: '100%' }}>
                    <DragDropContext
                      onDragEnd={this.handleEpicOrIssueDrag}
                      onDragStart={this.handleEpicOrIssueDragStart}
                    >
                      <div style={{ width: showBackLog ? `calc(100% - ${350}px)` : '100%', height: '100%' }}>

                        { showBackLog ? (
                          <div style={{ display: showBackLog ? 'block' : 'none', width: 350 }}>
                            <Backlog handleClickIssue={this.handleClickIssue} />
                          </div>
                        ) : null }
                        <div className="fixHead" style={{ height: `calc(100% - ${11}px)` }}>
                          <div className="fixHead-head" id="fixHead-head">
                            <div className="fixHead-line">
                              <Droppable droppableId="epic" direction="horizontal">
                                {(provided, snapshot) => (
                                  <div
                                    className="fixHead-line-epic"
                                    ref={provided.innerRef}
                                    style={{
                                      background: snapshot.isDraggingOver ? '#f0f0f0' : 'white',
                                      padding: 'grid',
                                    // borderBottom: '1px solid rgba(0,0,0,0.12)'
                                    }}
                                  >
                                    {UserMapStore.epics.map((epic, index) => (
                                      <div className="fixHead-block" key={epic.issueId}>
                                        <EpicCard
                                          index={index}
                                          // key={epic.issueId}
                                          epic={epic}
                                        />
                                      </div>
                                    ))}
                                    {provided.placeholder}
                                  </div>
                                )}
                              </Droppable>
                            </div>
                          </div>
                          <div id="fixHead-body" className="fixHead-body" style={{ flex: 1, position: 'relative' }}>
                            {this.renderBody()}
                          </div>
                        </div>
                      </div>
                    </DragDropContext>
                    <CreateEpic
                      store={UserMapStore}
                      // container={document.querySelector('.c7n-userMap')}
                      visible={createEpic}
                      onOk={() => {
                        UserMapStore.setCreateEpic(false);
                        UserMapStore.loadEpic();
                      }}
                      onCancel={() => UserMapStore.setCreateEpic(false)}
                    />
                    <CreateVOS
                      // container={document.querySelector('.c7n-userMap')}
                      visible={UserMapStore.createVOS}
                      // onOk={() => {UserMapStore.setCreateVOS(false)}}
                      onOk={this.handleCreateOk}
                      onCancel={() => { UserMapStore.setCreateVOS(false); }}
                      type={UserMapStore.getCreateVOSType}
                    />
                  </div>
                )
                : (
                  <div style={{ padding: 0, height: '100%', paddingLeft: 24 }}>
                    <div style={{
                      display: 'flex', justifyContent: 'center', alignItems: 'center', marginTop: '10%',
                    }}
                    >
                      <CreateEpic
                        store={UserMapStore}
                // container={document.querySelector('.c7n-userMap')}
                        visible={createEpic}
                        onOk={() => {
                          UserMapStore.setCreateEpic(false);
                          UserMapStore.loadEpic();
                        }}
                        onCancel={() => UserMapStore.setCreateEpic(false)}
                      />
                      <img src={epicPic} alt="" width="200" />
                      <div style={{ marginLeft: 50, width: 390 }}>
                        <span style={{ color: 'rgba(0,0,0,0.65)', fontSize: 14 }}>欢迎使用敏捷用户故事地图</span>
                        <p style={{ fontSize: 20, marginTop: 10 }}>
                          {'用户故事地图是以史诗为基础，根据版本控制，迭代冲刺多维度对问题进行管理规划，点击'}
                          <a role="none" onClick={this.handleCreateEpic}>创建史诗</a>
                          {'进入用户故事地图。'}
                        </p>
                      </div>
                    </div>
                  </div>
                )}
        </Content>
      </Page>
    );
  }
}
export default Home;
