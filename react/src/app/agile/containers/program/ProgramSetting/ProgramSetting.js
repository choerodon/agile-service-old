import React, { Component } from 'react';
import {
  stores, Page, Header, Content, Permission,
} from 'choerodon-front-boot';
import { observer } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import {
  Form, Input, Button, Icon, Tabs, Table,
} from 'choerodon-ui';
import moment from 'moment';
import WorkCalendar from './Component/WorkCalendar';
import SettingStore from '../../../stores/program/Setting/SettingStore';

const { AppState } = stores;
const FormItem = Form.Item;
const { TabPane } = Tabs;

@observer
class ProgramSetting extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      currentTab: '1',
    };
  }

  componentDidMount() {
    this.getProgramSetting();
  }

  getProgramSetting = () => {
    const { form } = this.props;
    SettingStore.getProgramInfo().then(() => {
      const { projectCode } = SettingStore.getProgram;
      form.setFieldsValue({ projectCode });
    });
    SettingStore.getProgramTeams();
    const year = moment().year();
    SettingStore.axiosOrgSetting(year);
    SettingStore.axiosGetProSetting(year);
  };

  handleUpdateProgramSetting = () => {
    const { form } = this.props;
    const program = SettingStore.getProgram;
    form.validateFields((err, values, modify) => {
      if (!err && modify) {
        const info = {
          ...program,
          projectCode: values.projectCode,
        };
        this.setState({
          loading: true,
        });
        SettingStore.updateProgramInfo(info).then(() => {
          const { projectCode } = SettingStore.getProgram;
          form.setFieldsValue({ projectCode });
          this.setState({
            loading: false,
          });
        });
      }
    });
  };

  handleTabChange = (currentTab) => {
    this.setState({
      currentTab,
    });
  };

  getColumns = () => [{
    title: '团队项目名称',
    dataIndex: 'projName',
    key: 'projName',
  }, {
    title: '人数',
    dataIndex: 'userCount',
    key: 'userCount',
  }, {
    title: '加入时间',
    dataIndex: 'startDate',
    key: 'startDate',
    render: startDate => <span>{moment(startDate).format('YYYY-MM-DD')}</span>,
  }];

  updateSelete = (data) => {
    const year = moment().year();
    if (data.calendarId) {
      SettingStore.axiosDeleteCalendarData(data.calendarId).then(() => {
        SettingStore.axiosGetProSetting(year);
      });
    } else {
      SettingStore.axiosCreateCalendarData(data).then(() => {
        SettingStore.axiosGetProSetting(year);
      });
    }
  };

  render() {
    const { form: { getFieldDecorator } } = this.props;
    const {
      loading, currentTab,
    } = this.state;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      timeZoneWorkCalendarDTOS: selectDays,
      workHolidayCalendarDTOS: holidayRefs,
    } = SettingStore.getOrgSetting;
    const proSetting = SettingStore.getProSetting;
    const { projectCode } = SettingStore.getProgram;
    const teams = SettingStore.getTeams;

    return (
      <Page
        service={[
          'agile-service.project-info.updateProjectInfo',
        ]}
      >
        <Header title="项目设置">
          <Button funcType="flat" onClick={() => this.getProgramSetting()}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content>
          <Tabs defaultActiveKey="1" activeKey={currentTab} onChange={this.handleTabChange}>
            <TabPane tab="项目编码" key="1">
              根据项目需求，你可以修改项目编码。
              <div style={{ marginTop: 20 }}>
                <Form layout="vertical">
                  {projectCode
                    ? (
                      <FormItem label="项目编码" style={{ width: 512 }}>
                        {getFieldDecorator('projectCode', {
                          rules: [{ required: true, message: '项目编码必填' }],
                          initialValue: projectCode,
                        })(
                          <Input
                            label="项目编码"
                            maxLength={5}
                          />,
                        )}
                      </FormItem>
                    ) : ''
                  }
                </Form>
                <div style={{ padding: '12px 0', borderTop: '1px solid rgba(0, 0, 0, 0.12)' }}>
                  <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.project-info.updateProjectInfo']}>
                    <Button
                      type="primary"
                      funcType="raised"
                      loading={loading}
                      onClick={() => this.handleUpdateProgramSetting()}
                    >
                      {'保存'}
                    </Button>
                  </Permission>
                  <Button
                    funcType="raised"
                    style={{ marginLeft: 12 }}
                    onClick={() => this.getProgramSetting()}
                  >
                    {'取消'}
                  </Button>
                </div>
              </div>
            </TabPane>
            <TabPane tab="项目信息" key="2">
              列表显示项目群关联的团队项目信息。
              <div style={{ width: 520, marginTop: 20 }}>
                <Table
                  columns={this.getColumns()}
                  dataSource={teams}
                  filterBar={false}
                  pagination={false}
                />
              </div>
            </TabPane>
            <TabPane tab="工作日历" key="3">
              <div style={{ width: 520, marginBottom: 15 }}>
                工作日历是用于配置当前PI的实际工作时间，比如：在开启ART后，原定的法定假日需要加班，这是可针对改该进行日历修改。
              </div>
              <WorkCalendar
                startDate={moment(new Date())}
                saturdayWork={saturdayWork}
                sundayWork={sundayWork}
                useHoliday={useHoliday}
                selectDays={selectDays}
                holidayRefs={holidayRefs}
                workDates={proSetting || []}
                onWorkDateChange={this.updateSelete}
              />
            </TabPane>
          </Tabs>
        </Content>
      </Page>
    );
  }
}
export default Form.create({})(withRouter(ProgramSetting));
