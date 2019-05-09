/* eslint-disable react/no-unused-state */
import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Page, Header, stores } from 'choerodon-front-boot';
import { DragDropContext } from 'react-beautiful-dnd';
import {
  Button, Spin, Checkbox, Icon,
} from 'choerodon-ui';
import Version from '../BacklogComponent/VersionComponent/Version';
import Epic from '../BacklogComponent/EpicComponent/Epic';
import Feature from '../BacklogComponent/FeatureComponent/Feature';
import IssueDetail from '../BacklogComponent/IssueDetailComponent/IssueDetail';
import CreateIssue from '../../../../components/CreateIssueNew';
import './BacklogHome.scss';
import SprintItem from '../BacklogComponent/SprintComponent/SprintItem';
import QuickSearch, { QuickSearchEvent } from '../../../../components/QuickSearch';
import Injecter from '../../../../components/Injecter';
import ClearFilter from '../BacklogComponent/SprintComponent/SprintItemComponent/SprintHeaderComponent/ClearAllFilter';
import { getFeaturesInProject } from '../../../../api/FeatureApi';
import { getProjectsInProgram } from '../../../../api/CommonApi';

const { AppState } = stores;

@inject('HeaderStore')
@observer
class BacklogHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      spinIf: false,
      versionVisible: false,
      epicVisible: false,
      isInProgram: false,
      display: false,
    };
  }

  componentDidMount() {
    this.refresh();
    getProjectsInProgram().then((res) => {
      this.setState({
        isInProgram: Boolean(res),
      });
    });
  }

  componentWillUnmount() {
    const { BacklogStore } = this.props;
    BacklogStore.resetData();
    BacklogStore.clearMultiSelected();
  }

  /**
   * 加载选择快速搜索的冲刺数据
   * isCreate: 是否创建冲刺，如果是则自动滚动到新建的冲刺
   * issue: 新建的issue，如果新建issue则自动滚动到新建的issue
   */
  getSprint = (isCreate = false, issue) => {
    const { BacklogStore } = this.props;
    BacklogStore.axiosGetIssueTypes();
    BacklogStore.axiosGetDefaultPriority();
    Promise.all([BacklogStore.axiosGetQuickSearchList(), BacklogStore.axiosGetIssueTypes(), BacklogStore.axiosGetDefaultPriority(), BacklogStore.axiosGetSprint()]).then(([quickSearch, issueTypes, priorityArr, backlogData]) => {
      BacklogStore.initBacklogData(quickSearch, issueTypes, priorityArr, backlogData);
    });
  };

  /**
   * 加载版本数据
   */
  loadVersion = () => {
    const { BacklogStore } = this.props;
    BacklogStore.axiosGetVersion().then((data2) => {
      const newVersion = [...data2];
      for (let index = 0, len = newVersion.length; index < len; index += 1) {
        newVersion[index].expand = false;
      }
      BacklogStore.setVersionData(newVersion);
    }).catch((error) => {
    });
  };

  /**
   * 加载史诗
   */
  loadEpic = () => {
    const { BacklogStore } = this.props;
    BacklogStore.axiosGetEpic().then((data3) => {
      const newEpic = [...data3];
      for (let index = 0, len = newEpic.length; index < len; index += 1) {
        newEpic[index].expand = false;
      }
      BacklogStore.setEpicData(newEpic);
    }).catch((error3) => {
    });
  };

  /**
   * 加载特性
   */
  loadFeature = () => {
    const { BacklogStore } = this.props;

    getFeaturesInProject().then((data) => {
      BacklogStore.setFeatureData(data);
    }).catch(() => {
    });
  };

  paramConverter = (url) => {
    const reg = /[^?&]([^=&#]+)=([^&#]*)/g;
    const retObj = {};
    url.match(reg).forEach((item) => {
      const [tempKey, paramValue] = item.split('=');
      const paramKey = tempKey[0] !== '&' ? tempKey : tempKey.substring(1);
      Object.assign(retObj, {
        [paramKey]: paramValue,
      });
    });
    return retObj;
  };

  refresh = (isCreate, issue) => {
    const { location } = this.props;
    const url = this.paramConverter(location.search);
    const { BacklogStore } = this.props;
    BacklogStore.setSpinIf(true);
    this.getSprint(isCreate, issue);
    if (BacklogStore.getCurrentVisible === 'version') {
      this.loadVersion();
    } else if (BacklogStore.getCurrentVisible === 'epic') {
      this.loadEpic();
    } else if (BacklogStore.getCurrentVisible === 'feature') {
      this.loadFeature();
    }
  };

  /**
   * 加载快速搜索
   */
  loadQuickFilter = () => {

  };

  /**
   * 创建冲刺
   */
  handleCreateSprint = () => {
    const { BacklogStore } = this.props;
    this.setState({
      loading: true,
    });
    const data = {
      projectId: AppState.currentMenuType.id,
    };
    BacklogStore.axiosCreateSprint(data).then((res) => {
      BacklogStore.setCreatedSprint(res.sprintId);
      this.setState({
        loading: false,
      });
      this.refresh();
      Choerodon.prompt('创建成功');
    }).catch((error) => {
      this.setState({
        loading: false,
      });
      Choerodon.prompt('创建失败');
    });
  };

  resetSprintChose = () => {
    this.resetMuilterChose();
  };

  /**
   * issue详情回退关闭详情侧边栏
   */
  resetMuilterChose = () => {
    this.setState({
      selected: {
        droppableId: '',
        issueIds: [],
      },
    });
  };

  onQuickSearchChange = (onlyMeChecked, onlyStoryChecked, moreChecked) => {
    const { BacklogStore } = this.props;
    BacklogStore.setQuickFilters(onlyMeChecked, onlyStoryChecked, moreChecked);
    BacklogStore.axiosGetSprint()
      .then((res) => {
        BacklogStore.setSprintData(res);
      }).catch((error) => {
      });
  }

  onAssigneeChange = (data) => {
    const { BacklogStore } = this.props;
    BacklogStore.setAssigneeFilterIds(data);
    BacklogStore.axiosGetSprint()
      .then((res) => {
        BacklogStore.setSprintData(res);
        this.setState({
          spinIf: false,
        });
      }).catch((error) => {
      });
  };

  handleClickCBtn = () => {
    const { BacklogStore } = this.props;
    BacklogStore.setNewIssueVisible(true);
  }

  handleCreateIssue = (res) => {
    const { BacklogStore } = this.props;
    BacklogStore.setNewIssueVisible(false);
    // 创建issue后刷新
    if (res) {
      this.refresh(false, res);
    }
  };

  toggleCurrentVisible = (type) => {
    const { BacklogStore } = this.props;
    if (BacklogStore.getCurrentVisible === type) {
      BacklogStore.toggleVisible(null);
    } else {
      if (type === 'feature') {
        QuickSearchEvent.emit('setSelectQuickSearch', [{ key: -2, label: '仅故事' }]);
      }
      BacklogStore.toggleVisible(type);
    }
  };

  onCheckChange = (e) => {
    this.setState({
      display: e.target.checked,
    });
  };

  render() {
    const { BacklogStore, HeaderStore } = this.props;
    const arr = BacklogStore.getSprintData;
    const { isInProgram, display } = this.state;
    return (
      <Page
        service={[
          // 'agile-service.product-version.createVersion',
          'agile-service.issue.deleteIssue',
          'agile-service.sprint.queryByProjectId',
        ]}
      >
        <Header title="待办事项">
          <Button
            className="leftBtn"
            funcType="flat"
            onClick={this.handleClickCBtn}
          >
            <Icon type="playlist_add icon" />
            <span>创建问题</span>
          </Button>
          {!isInProgram && (
            <Button className="leftBtn" functyp="flat" onClick={this.handleCreateSprint}>
              <Icon type="queue" />
              {'创建冲刺'}
            </Button>
          )}
          <Button
            className="leftBtn2"
            functyp="flat"
            onClick={() => {
              this.refresh();
              this.loadQuickFilter();
            }}
          >
            <Icon type="refresh" />
            {'刷新'}
          </Button>
          {isInProgram
            ? (
              <Checkbox
                disabled={!arr.length}
                style={{ marginLeft: 20, color: '#3f51b5' }}
                onChange={this.onCheckChange}
              >
                显示未开始冲刺
              </Checkbox>
            ) : ''
          }
        </Header>
        <div style={{ padding: 0, display: 'flex', flexDirection: 'column' }}>
          <div
            className="backlogTools"
            style={{
              paddingLeft: 24, display: 'flex', alignItems: 'center',
            }}
          >
            <QuickSearch
              onQuickSearchChange={this.onQuickSearchChange}
              resetFilter={BacklogStore.getQuickSearchClean}
              onAssigneeChange={this.onAssigneeChange}
            />
            <ClearFilter />
          </div>
          <div
            className="c7n-backlog"
            style={{
              height: HeaderStore.announcementClosed ? 'calc(100vh - 156px)' : 'calc(100vh - 208px)',
            }}
          >
            <div className="c7n-backlog-side">
              <p
                role="none"
                onClick={() => {
                  this.toggleCurrentVisible('version');
                }}
              >
                {'版本'}
              </p>
              <p
                style={{
                  marginTop: 12,
                }}
                role="none"
                onClick={() => {
                  this.toggleCurrentVisible('epic');
                }}
              >
                {'史诗'}
              </p>
              {isInProgram && (
                <p
                  style={{
                    marginTop: 12,
                  }}
                  role="none"
                  onClick={() => {
                    this.toggleCurrentVisible('feature');
                  }}
                >
                  {'特性'}
                </p>
              )}
            </div>
            <Version
              store={BacklogStore}
              refresh={this.refresh}
              visible={BacklogStore.getCurrentVisible}
              issueRefresh={() => {
                this.IssueDetail.refreshIssueDetail();
              }}
            />
            <Epic
              refresh={this.refresh}
              visible={BacklogStore.getCurrentVisible}
              issueRefresh={() => {
                this.IssueDetail.refreshIssueDetail();
              }}
            />
            {isInProgram && (
              <Feature
                refresh={this.refresh}
                visible={BacklogStore.getCurrentVisible}
                issueRefresh={() => {
                  this.IssueDetail.refreshIssueDetail();
                }}
              />
            )}
            <Spin spinning={BacklogStore.getSpinIf}>
              <div className="c7n-backlog-content">
                <DragDropContext
                  onDragEnd={(result) => {
                    BacklogStore.setIsDragging(null);
                    const { destination, source, draggableId } = result;
                    if (destination) {
                      const { droppableId: destinationId, index: destinationIndex } = destination;
                      const { droppableId: sourceId, index: sourceIndex } = source;
                      if (destinationId === sourceId && destinationIndex === sourceIndex) {
                        return;
                      }
                      if (result.reason !== 'CANCEL') {
                        const item = BacklogStore.getIssueMap.get(sourceId)[sourceIndex];
                        const destinationArr = BacklogStore.getIssueMap.get(destinationId);
                        let destinationItem;
                        if (destinationIndex === 0) {
                          destinationItem = null;
                        } else if (destinationIndex === BacklogStore.getIssueMap.get(destinationId).length) {
                          destinationItem = destinationArr[destinationIndex - 1];
                        } else {
                          destinationItem = destinationArr[destinationIndex];
                        }
                        if (BacklogStore.getMultiSelected.size > 1 && !BacklogStore.getMultiSelected.has(destinationItem)) {
                          BacklogStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, 'multi').then(() => {
                            if (this.IssueDetail) {
                              this.IssueDetail.refreshIssueDetail();
                            }
                          });
                        } else {
                          BacklogStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, 'single').then(() => {
                            if (this.IssueDetail) {
                              this.IssueDetail.refreshIssueDetail();
                            }
                          });
                        }
                      }
                    }
                  }}
                  onDragStart={(result) => {
                    const { source, draggableId } = result;
                    const { droppableId: sourceId, index: sourceIndex } = source;
                    const item = BacklogStore.getIssueMap.get(sourceId)[sourceIndex];
                    BacklogStore.setIsDragging(item.issueId);
                    BacklogStore.setIssueWithEpicOrVersion(item);
                  }}
                >
                  <SprintItem
                    display={display}
                    isInProgram={isInProgram}
                    epicVisible={BacklogStore.getEpicVisible}
                    versionVisible={BacklogStore.getVersionVisible}
                    onRef={(ref) => {
                      this.sprintItemRef = ref;
                    }}
                    refresh={this.refresh}
                  />
                </DragDropContext>
                <Injecter store={BacklogStore} item="newIssueVisible">
                  {visible => (
                    visible
                      ? (
                        <CreateIssue
                          visible={visible}
                          onCancel={() => {
                            BacklogStore.setNewIssueVisible(false);
                          }}
                          onOk={this.handleCreateIssue}
                        />
                      ) : ''
                  )}
                </Injecter>
              </div>
            </Spin>
            <IssueDetail
              refresh={this.refresh}
              onRef={(ref) => {
                this.IssueDetail = ref;
              }}
              cancelCallback={this.resetSprintChose}
            />
          </div>
        </div>
      </Page>
    );
  }
}

export default BacklogHome;
