import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Page, Header, Content, stores, axios, Permission,
} from 'choerodon-front-boot';
import moment from 'moment';
import {
  Button, Spin, Modal, Form, Input, Select, Tabs, message, Icon,
} from 'choerodon-ui';
import { withRouter } from 'react-router-dom';
import './ScrumBoardSetting.scss';
import ScrumBoardStore from '../../../../stores/project/scrumBoard/ScrumBoardStore';
import ColumnPage from '../ScrumBoardSettingComponent/ColumnPage/ColumnPage';
import SwimLanePage from '../ScrumBoardSettingComponent/SwimLanePage/SwimLanePage';
import WorkcalendarPage from '../ScrumBoardSettingComponent/WorkCalendarPage/WorkCalendarPage';
import EditBoardName from '../ScrumBoardSettingComponent/EditBoardName/EditBoardName';

const { TabPane } = Tabs;
const { confirm } = Modal;
const { AppState } = stores;

@observer
class ScrumBoardSetting extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      hasPermission: false,
    };
  }

  componentDidMount() {
    this.refresh();
    axios.post('/iam/v1/permissions/checkPermission', [{
      code: 'agile-service.project-info.updateProjectInfo',
      organizationId: AppState.currentMenuType.organizationId,
      projectId: AppState.currentMenuType.id,
      resourceType: 'project',
    }]).then((permission) => {
      this.setState({
        hasPermission: !permission[0].approve,
      });
    });
  }

  refresh() {
    this.setState({
      loading: true,
    });
    const boardId = ScrumBoardStore.getSelectedBoard;
    if (!boardId) {
      const { history } = this.props;
      const urlParams = AppState.currentMenuType;
      history.push(`/agile/scrumboard?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
    } else {
      ScrumBoardStore.loadStatus();
      ScrumBoardStore.axiosGetBoardDataBySetting(boardId).then((data) => {
        ScrumBoardStore.axiosGetUnsetData(boardId).then((data2) => {
          const unsetColumn = {
            columnId: 'unset',
            name: '未对应的状态',
            subStatuses: data2,
          };
          data.columnsData.columns.push(unsetColumn);
          ScrumBoardStore.setBoardData(data.columnsData.columns);
          this.setState({
            loading: false,
          });
        }).catch((error2) => {
        });
      }).catch((error) => {
      });
      ScrumBoardStore.axiosGetLookupValue('constraint').then((res) => {
        const oldLookup = ScrumBoardStore.getLookupValue;
        oldLookup.constraint = res.lookupValues;
        ScrumBoardStore.setLookupValue(oldLookup);
      }).catch((error) => {
      });
      const year = moment().year();
      ScrumBoardStore.axiosGetWorkSetting(year).then(() => {
        ScrumBoardStore.axiosGetCalendarData(year);
      }).catch(() => {
        ScrumBoardStore.axiosGetCalendarData(year);
      });
      ScrumBoardStore.axiosCanAddStatus();
    }
  }

  handleDeleteBoard() {
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    const { name } = ScrumBoardStore.getBoardList.get(ScrumBoardStore.getSelectedBoard);
    confirm({
      title: `删除看板"${name}"`,
      content: '确定要删除该看板吗?',
      okText: '删除',
      cancelText: '取消',
      className: 'scrumBoardMask',
      width: 520,
      onOk() {
        ScrumBoardStore.axiosDeleteBoard().then((res) => {
          history.push(`/agile/scrumboard?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
        }).catch((error) => {
        });
      },
      onCancel() {
      },
    });
  }

  render() {
    const { form: { getFieldDecorator } } = this.props;
    const { loading } = this.state;
    const urlParams = AppState.currentMenuType;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const { hasPermission } = this.state;
    return (
      <Page>
        <Header title="配置看板" backPath={`/agile/scrumboard?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`}>
          <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.board.deleteScrumBoard']}>
            <Button funcType="flat" onClick={this.handleDeleteBoard.bind(this)} disabled={ScrumBoardStore.getBoardList.length === 1}>
              <Icon type="delete_forever icon" />
              <span>删除看板</span>
            </Button>
          </Permission>
          <Button funcType="flat" onClick={this.refresh.bind(this)}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content className="c7n-scrumboard" style={{ height: '100%', paddingTop: 0 }}>
          <Tabs
            style={{
              display: 'flex', flexDirection: 'column', height: '100%', overflow: 'auto',
            }}
            defaultActiveKey="1"
          >
            <TabPane tab="列配置" key="1">
              <Spin spinning={loading}>
                <ColumnPage
                  refresh={this.refresh.bind(this)}
                />
              </Spin>
            </TabPane>
            <TabPane tab="泳道" key="2">
              <SwimLanePage />
            </TabPane>
            {ScrumBoardStore.getCalanderCouldUse
              ? (
                <TabPane tab="工作日历" key="3">
                  <WorkcalendarPage selectedDateDisabled={hasPermission} />
                </TabPane>
              ) : null
            }
            <TabPane tab="看板名称" key="4">
              <EditBoardName editBoardNameDisabled={hasPermission} />
            </TabPane>
          </Tabs>
        </Content>
      </Page>
    );
  }
}

export default Form.create()(withRouter(ScrumBoardSetting));
