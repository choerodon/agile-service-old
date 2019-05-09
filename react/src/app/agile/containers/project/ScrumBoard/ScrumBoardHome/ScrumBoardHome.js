import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import {
  Button, Select, Spin, message, Icon, Modal, Input, Form, Tooltip,
} from 'choerodon-ui';
import ScrumBoardDataController from './ScrumBoardDataController';
import ScrumBoardStore from '../../../../stores/project/scrumBoard/ScrumBoardStore';
import StatusColumn from '../ScrumBoardComponent/StatusColumn/StatusColumn';
import './ScrumBoardHome.scss';
import IssueDetail from '../ScrumBoardComponent/IssueDetail/IssueDetail';
import BacklogStore from '../../../../stores/project/backlog/BacklogStore';
import CloseSprint from '../../Backlog/BacklogComponent/SprintComponent/CloseSprint';
import QuickSearch from '../../../../components/QuickSearch';
import NoneSprint from '../ScrumBoardComponent/NoneSprint/NoneSprint';
import '../ScrumBoardComponent/RenderSwimLaneContext/RenderSwimLaneContext.scss';
import SwimLane from '../ScrumBoardComponent/RenderSwimLaneContext/SwimLane';
import CSSBlackMagic from '../../../../components/CSSBlackMagic/CSSBlackMagic';

const { Option } = Select;
const { Sidebar } = Modal;
const FormItem = Form.Item;
const { AppState } = stores;
const { confirm } = Modal;

const style = swimLaneId => `
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn {
    background-color: rgba(140, 158, 255, 0.12) !important;
  }
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus >  .c7n-swimlaneContext-itemBodyStatus-container {
    border-width: 2px;
    border-style: dashed;
    border-color: #26348b;
  }
  .${swimLaneId}.c7n-swimlaneContext-itemBodyColumn > .c7n-swimlaneContext-itemBodyStatus > .c7n-swimlaneContext-itemBodyStatus-container > .c7n-swimlaneContext-itemBodyStatus-container-statusName {
      visibility: visible !important;
  } 
`;

@CSSBlackMagic
@inject('AppState', 'HeaderStore')
@observer
class ScrumBoardHome extends Component {
  constructor(props) {
    super(props);
    this.dataConverter = new ScrumBoardDataController();
    this.ref = null;
    this.state = {
      quickSearchObj: {
        onlyMe: false,
        onlyStory: false,
        quickSearchArray: [],
        assigneeFilterIds: [],
      },
      addBoard: false,
      closeSprintVisible: false,
      updateParentStatus: null,
      checkResult: false,
    };
  }

  componentDidMount() {
    this.getBoard();
  }

  componentWillUnmount() {
    this.dataConverter = null;
    ScrumBoardStore.resetDataBeforeUnmount();
  }

  async getBoard() {
    const { location } = this.props;
    const url = this.paramConverter(location.search);
    const boardListData = await ScrumBoardStore.axiosGetBoardList();
    const defaultBoard = boardListData.find(item => item.userDefault) || boardListData[0];
    if (defaultBoard.boardId) {
      this.refresh(defaultBoard, url, boardListData);
    }
  }

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

  /**
   *创建面板
   *
   * @param {*} e
   * @memberof ScrumBoardHome
   */
  handleCreateBoard = (e) => {
    e.preventDefault();
    const { form } = this.props;
    const { checkResult } = this.state;
    form.validateFields((err, values) => {
      if (!err && !checkResult) {
        ScrumBoardStore.axiosCreateBoard(values.name).then((res) => {
          form.resetFields();
          Choerodon.prompt('创建成功');
          this.setState({
            addBoard: false,
          });
          this.getBoard();
        }).catch((error) => {
        });
      }
    });
  }

  onQuickSearchChange = (onlyMeChecked = false, onlyStoryChecked = false, moreChecked) => {
    const { quickSearchObj } = this.state;
    ScrumBoardStore.addQuickSearchFilter(onlyMeChecked, onlyStoryChecked, moreChecked);
    this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
  };

  onAssigneeChange = (value) => {
    const { quickSearchObj } = this.state;
    ScrumBoardStore.addAssigneeFilter(value);
    this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
  }

  /**
   *完成冲刺
   *
   * @memberof ScrumBoardHome
   */
  handleFinishSprint = () => {
    BacklogStore.axiosGetSprintCompleteMessage(
      ScrumBoardStore.getSprintId,
    ).then((res) => {
      BacklogStore.setSprintCompleteMessage(res);
      // this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
      this.setState({
        closeSprintVisible: true,
      });
    }).catch((error) => {
    });
  };

  changeState = (name, value) => {
    if (name === 'judgeUpdateParent') {
      ScrumBoardStore.loadTransforms(value.statusId, value.id, value.typeId).then((types) => {
        this.matchStatus(types);
        this.setState({
          [name]: value,
        });
      }).catch((e) => {
        Choerodon.prompt('查询状态失败，请重试！');
      });
    }
  };

  checkBoardNameRepeat = (rule, value, callback) => {
    const proId = AppState.currentMenuType.id;
    ScrumBoardStore.checkBoardNameRepeat(proId, value)
      .then((res) => {
        this.setState({
          checkResult: res,
        });
        if (res) {
          callback('看板名称重复');
        } else {
          callback();
        }
      });
  };

  onDragStart = (result) => {
    const { headerStyle } = this.props;
    const { draggableId } = result;
    const [SwimLaneId, issueId] = draggableId.split(['/']);
    headerStyle.changeStyle(style(SwimLaneId));
    ScrumBoardStore.setIsDragging(true);
  };

  onDragEnd = (result) => {
    const { headerStyle } = this.props;
    const { destination, source, draggableId } = result;
    const [SwimLaneId, issueId] = draggableId.split(['/']);
    const allDataMap = ScrumBoardStore.getAllDataMap;
    ScrumBoardStore.resetCanDragOn();
    ScrumBoardStore.setIsDragging(true);
    headerStyle.unMountStyle();
    if (!destination) {
      return;
    }

    if (destination.droppableId === source.droppableId && destination.index === source.index) {
      return;
    }

    const [startStatus, startColumn] = source.droppableId.split(['/']).map(id => parseInt(id, 10));
    const startStatusIndex = source.index;

    const [destinationStatus, destinationColumn] = destination.droppableId.split(['/']).map(id => parseInt(id, 10));
    const destinationStatusIndex = destination.index;

    const issue = {
      ...allDataMap.get(+issueId),
      stayDay: 0,
    };

    const [type, parentId] = SwimLaneId.split('-');

    ScrumBoardStore.updateIssue(issue, startStatus, startStatusIndex, destinationStatus, destinationStatusIndex, SwimLaneId).then((data) => {
      if (data.failed) {
        Choerodon.prompt(data.message);
        ScrumBoardStore.setSwimLaneData(SwimLaneId, startStatus, startStatusIndex, SwimLaneId, destinationStatus, destinationStatusIndex, issue, true);
      } else {
        if (ScrumBoardStore.getSwimLaneCode === 'parent_child' && parentId !== 'other') {
          ScrumBoardStore.judgeMoveParentToDone(destinationStatus, SwimLaneId, +parentId, ScrumBoardStore.getStatusMap.get(destinationStatus).categoryCode === 'done');
        }
        if (data.issueId === ScrumBoardStore.getCurrentClickId) {
          ScrumBoardStore.getEditRef.reloadIssue();
        }
        if (startColumn !== destinationColumn) {
          ScrumBoardStore.resetHeaderData(startColumn, destinationColumn, issue.issueTypeDTO.typeCode);
        }
        ScrumBoardStore.rewriteObjNumber(data, issueId, issue);
      }
    });
    ScrumBoardStore.setSwimLaneData(SwimLaneId, startStatus, startStatusIndex, SwimLaneId, destinationStatus, destinationStatusIndex, issue, false);
  };

  refresh(defaultBoard, url, boardListData) {
    ScrumBoardStore.setSpinIf(true);
    Promise.all([ScrumBoardStore.axiosGetIssueTypes(), ScrumBoardStore.axiosGetStateMachine(), ScrumBoardStore.axiosGetBoardData(defaultBoard.boardId), ScrumBoardStore.axiosGetAllEpicData()]).then(([issueTypes, stateMachineMap, defaultBoardData, epicData]) => {
      this.dataConverter.setSourceData(epicData, defaultBoardData);
      const renderDataMap = new Map([
        ['parent_child', this.dataConverter.getParentWithSubData],
        ['swimlane_epic', this.dataConverter.getEpicData],
        ['assignee', this.dataConverter.getAssigneeData],
        ['swimlane_none', this.dataConverter.getAllData],
      ]);
      const renderData = renderDataMap.get(defaultBoard.userDefaultBoard)();
      const canDragOn = this.dataConverter.getCanDragOn();
      const statusColumnMap = this.dataConverter.getStatusColumnMap();
      const statusMap = this.dataConverter.getStatusMap();
      const mapStructure = this.dataConverter.getMapStructure();
      const allDataMap = this.dataConverter.getAllDataMap(defaultBoard.userDefaultBoard);
      const headerData = this.dataConverter.getHeaderData();
      ScrumBoardStore.scrumBoardInit(AppState, url, boardListData, defaultBoard, defaultBoardData, null, issueTypes, stateMachineMap, canDragOn, statusColumnMap, allDataMap, mapStructure, statusMap, renderData, headerData);
    });
  }

  render() {
    const { form: { getFieldDecorator }, history, HeaderStore } = this.props;
    const {
      closeSprintVisible,
      addBoard,
      updateParentStatus,
    } = this.state;
    return (
      <Page
        className="c7n-scrumboard-page"
        service={[
          'agile-service.board.deleteScrumBoard',
          'agile-service.issue-status.createStatus',
          'agile-service.board-column.createBoardColumn',
          'agile-service.issue-status.deleteStatus',
          'agile-service.issue-status.updateStatus',
          'agile-service.issue.deleteIssue',
          'agile-service.board.queryByProjectId',
          'agile-service.board.queryByOptions',
        ]}
      >
        <Header title="活跃冲刺">
          <Button
            funcType="flat"
            onClick={() => {
              this.setState({
                addBoard: true,
              });
            }}
          >
            <Icon type="playlist_add icon" />
            <span>创建看板</span>
          </Button>
          {
           ScrumBoardStore.getBoardList.size === 1 ? (
             <Tooltip key={ScrumBoardStore.getBoardList.values().next().value.boardId} title={ScrumBoardStore.getBoardList.values().next().value.name}>
               <div
                 funcType="flat"
                 className="boardBtn ant-btn ant-btn-flat"
               >
                 <span>{ScrumBoardStore.getBoardList.values().next().value.name}</span>
               </div>
             </Tooltip>
           ) : (
             <Select
               className="select-without-underline"
               value={ScrumBoardStore.getSelectedBoard}
               style={{
                 maxWidth: 100, color: '#3F51B5', margin: '0 30px', fontWeight: 500, lineHeight: '28px',
               }}
               dropdownStyle={{
                 color: '#3F51B5',
                 width: 200,
               }}
               onChange={(value) => {
                 const selectedBoard = ScrumBoardStore.getBoardList.get(value);
                 ScrumBoardStore.setSelectedBoard(value);
                 ScrumBoardStore.setSwimLaneCode(selectedBoard.userDefaultBoard);
                 this.refresh(selectedBoard);
               }}
             >
               {
                 [...ScrumBoardStore.getBoardList.values()].map(item => (
                   <Option key={item.boardId} value={item.boardId}>
                     <Tooltip title={item.name}>
                       {item.name}
                     </Tooltip>
                   </Option>
                 ))
              }
             </Select>
           )}
          {
            (
              <Button
                style={{
                  marginTop: 2,
                }}
                disabled={!ScrumBoardStore.didCurrentSprintExist}
                className="leftBtn2"
                funcType="flat"
                onClick={() => {
                  if (!ScrumBoardStore.getSpinIf) {
                    history.push(`/agile/iterationBoard/${ScrumBoardStore.getSprintId}?type=project&id=${AppState.currentMenuType.id}&name=${AppState.currentMenuType.name}&organizationId=${AppState.currentMenuType.organizationId}`);
                  } else {
                    Choerodon.prompt('等待加载当前迭代');
                  }
                }}
              >
                <span>切换至工作台</span>
              </Button>
            )
          }
          <Button
            className="leftBtn2"
            funcType="flat"
            onClick={() => {
              this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
            }}
          >
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <div style={{ padding: 0, display: 'flex', flexDirection: 'column' }}>
          <div className="c7n-scrumTools">
            <QuickSearch
              onQuickSearchChange={this.onQuickSearchChange}
              onAssigneeChange={this.onAssigneeChange}
              style={{ height: 32 }}
            />
            <div
              className="c7n-scrumTools-right"
              style={{ display: 'flex', alignItems: 'center', color: 'rgba(0,0,0,0.54)' }}
            >
              <Icon type="av_timer" />
              <span style={{
                paddingLeft: 5,
                marginLeft: 0,
                marginRight: 15,
              }}
              >
                {`${ScrumBoardStore.getDayRemain >= 0 ? `${ScrumBoardStore.getDayRemain} days剩余` : '无剩余时间'}`}
              </span>
              <Button
                funcType="flat"
                onClick={this.handleFinishSprint.bind(this)}
              >
                <Icon type="power_settings_new icon" />
                <span style={{ marginLeft: 0 }}>完成Sprint</span>
              </Button>
              <Button
                funcType="flat"
                onClick={() => {
                  const urlParams = AppState.currentMenuType;
                  history.push(`/agile/scrumboard/setting?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}&boardId=${ScrumBoardStore.getSelectedBoard}`);
                }}
              >
                <Icon type="settings icon" />
                <span style={{ marginLeft: 0 }}>配置</span>
              </Button>
            </div>
          </div>
          <Spin spinning={ScrumBoardStore.getSpinIf}>
            <div style={{ display: 'flex', width: '100%' }}>
              <div className="c7n-scrumboard" style={HeaderStore.announcementClosed ? {} : { height: 'calc(100vh - 208px)' }}>
                <div className="c7n-scrumboard-header">
                  <StatusColumn />
                </div>
                {!ScrumBoardStore.didCurrentSprintExist ? (
                  <NoneSprint />
                ) : (
                  <div
                    className="c7n-scrumboard-content"
                    style={HeaderStore.announcementClosed ? {} : { height: 'calc(100vh - 256px)' }}
                  >
                    <div className="c7n-scrumboard-container">
                      <SwimLane
                        mode={ScrumBoardStore.getSwimLaneCode}
                        allDataMap={this.dataConverter.getAllDataMap()}
                        mapStructure={ScrumBoardStore.getMapStructure}
                        onDragEnd={this.onDragEnd}
                        onDragStart={this.onDragStart}
                      />
                    </div>
                  </div>
                )}
              </div>
              <IssueDetail
                refresh={this.refresh.bind(this)}
              />
            </div>
          </Spin>
        </div>
        <CloseSprint
          store={BacklogStore}
          visible={closeSprintVisible}
          onCancel={() => {
            this.setState({
              closeSprintVisible: false,
            });
          }}
          refresh={() => {
            this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
          }}
          data={{
            sprintId: ScrumBoardStore.getSprintId,
            sprintName: ScrumBoardStore.getSprintName,
          }}
        />
        {
          ScrumBoardStore.getUpdateParent ? (
            <Modal
              closable={false}
              maskClosable={false}
              title="更新父问题"
              visible={ScrumBoardStore.getUpdateParent}
              onCancel={() => {
                ScrumBoardStore.setUpdateParent(false);
              }}
              onOk={() => {
                // 后端要在后续增加的 parentIssues 上加 objVersionNumber
                const data = {
                  issueId: ScrumBoardStore.getUpdatedParentIssue.issueId,
                  objectVersionNumber: ScrumBoardStore.getUpdatedParentIssue.objectVersionNumber,
                  transformId: updateParentStatus || ScrumBoardStore.getTransformToCompleted[0].id,
                };
                ScrumBoardStore.axiosUpdateIssue(data).then((res) => {
                  ScrumBoardStore.setUpdateParent(false);
                  this.refresh(ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard));
                }).catch((error) => {
                });
              }}
              disableOk={!ScrumBoardStore.getTransformToCompleted.length}
            >
              <p>
                {'任务'}
                {ScrumBoardStore.getUpdatedParentIssue.issueNum}
                {'的全部子任务为done'}
              </p>
              <div style={{ display: 'flex', alignItems: 'center' }}>
                <p style={{ marginRight: 20, marginBottom: 0 }}>您是否要更新父问题进行匹配</p>
                <Select
                  style={{
                    width: 250,
                  }}
                  onChange={(value) => {
                    this.setState({
                      updateParentStatus: value,
                    });
                  }}
                  defaultValue={ScrumBoardStore.getTransformToCompleted.length ? ScrumBoardStore.getTransformToCompleted[0].id : '无'}
                >
                  {
                    ScrumBoardStore.getTransformToCompleted.map(item => (
                      <Option
                        key={item.id}
                        value={item.id}
                      >
                        {item.statusDTO.name}
                      </Option>
                    ))
                  }
                </Select>
              </div>
            </Modal>
          ) : null
        }
        <Sidebar
          title="创建看板"
          visible={addBoard}
          onCancel={() => {
            this.setState({
              addBoard: false,
            });
          }}
          okText="创建"
          cancelText="取消"
          onOk={this.handleCreateBoard.bind(this)}
        >
          <Content
            style={{ padding: 0 }}
            title={`创建项目“${AppState.currentMenuType.name}”的看板`}
            description="请在下面输入看板名称，创建一个新的board。新的board会默认为您创建'待处理'、'处理中'、'已完成'三个列，同时将todo、doing、done三个类别的状态自动关联入三个列中。"
            link="http://v0-16.choerodon.io/zh/docs/user-guide/agile/sprint/create-kanban/"
          >
            <Form>
              <FormItem>
                {getFieldDecorator('name', {
                  rules: [{
                    required: true, message: '看板名是必填的',
                  }, {
                    validator: this.checkBoardNameRepeat,
                  }],
                })(
                  <Input
                    style={{
                      width: 512,
                    }}
                    label="看板名称"
                    maxLength={30}
                  />,
                )}
              </FormItem>
            </Form>
          </Content>
        </Sidebar>
      </Page>
    );
  }
}

export default Form.create()(ScrumBoardHome);
