import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  stores, axios, Page, Header, Content, Permission,
} from 'choerodon-front-boot';
import { withRouter } from 'react-router-dom';
import moment from 'moment';
import {
  Form, Button, Icon, Select, Checkbox,
} from 'choerodon-ui';
import WorkCalendar from './Component/WorkCalendar';

const { AppState } = stores;
const { Option } = Select;
const FormItem = Form.Item;

@observer
class WorkCalendarHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentDidMount() {
    this.getWorkCalendar();
  }

  getWorkCalendar = () => {
    const year = moment().year();
    const { WorkCalendarStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    WorkCalendarStore.axiosGetWorkDaySetting(orgId).then(() => {
      const { timeZoneId } = WorkCalendarStore.getWorkDaySetting;
      if (timeZoneId) {
        WorkCalendarStore.axiosGetCalendarData(orgId, timeZoneId, year);
      }
    });
    WorkCalendarStore.axiosGetHolidayData(orgId, year);
  };

  onCheckChange = (e, type) => {
    const { WorkCalendarStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const { timeZoneId } = WorkCalendarStore.getWorkDaySetting;
    const data = {
      ...WorkCalendarStore.getWorkDaySetting,
      [type]: !!e.target.checked,
    };
    WorkCalendarStore.axiosUpdateSetting(orgId, timeZoneId, data).then(() => {
      this.getWorkCalendar();
    });
  };

  updateSelete = (data) => {
    const year = moment().year();
    const { WorkCalendarStore } = this.props;
    const orgId = AppState.currentMenuType.organizationId;
    const { timeZoneId } = WorkCalendarStore.getWorkDaySetting;
    if (data.calendarId) {
      WorkCalendarStore.axiosDeleteCalendarData(orgId, data.calendarId).then(() => {
        WorkCalendarStore.axiosGetCalendarData(orgId, timeZoneId, year);
      });
    } else {
      WorkCalendarStore.axiosCreateCalendarData(orgId, timeZoneId, data).then(() => {
        WorkCalendarStore.axiosGetCalendarData(orgId, timeZoneId, year);
      });
    }
  };

  render() {
    const { WorkCalendarStore, form } = this.props;
    const { getFieldDecorator } = form;
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      timeZoneCode,
      areaCode,
    } = WorkCalendarStore.getWorkDaySetting;
    const holidayRefs = WorkCalendarStore.getHolidayRefs;
    const selectDays = WorkCalendarStore.getSelectDays;
    return (
      <Page>
        <Header title="工作日历">
          <Button funcType="flat" onClick={this.getWorkCalendar}>
            <Icon type="refresh icon" />
            <span>刷新</span>
          </Button>
        </Header>
        <Content
          title="工作日历"
          description="这里可以为您的组织设置全年的工作日历，该设置将应用到组织下所有项目中，每个项目也可根据实际的工作情况进行修改。勾选自动更新法定节假日后，系统会自动更新并设定法定节假日到工作日历中。"
          link="http://v0-16.choerodon.io/zh/docs/user-guide/system-configuration/tenant/working_calendar/"
        >
          <Form layout="vertical">
            <FormItem style={{ width: 512 }}>
              {getFieldDecorator('region', {
                initialValue: areaCode,
              })(
                <Select label="地区" placeholder="Please Select" style={{ width: 512 }}>
                  <Option value="Asia">亚洲</Option>
                </Select>,
              )}
            </FormItem>
            <FormItem style={{ width: 512 }}>
              {getFieldDecorator('timezone', {
                initialValue: timeZoneCode,
              })(
                <Select label="时区" placeholder="Please Select" style={{ width: 512 }}>
                  <Option value="Asia/Shanghai">(GMT+08:00) Shanghai</Option>
                </Select>,
              )}
            </FormItem>
            <FormItem style={{ width: 512, marginBottom: 5 }}>
              {getFieldDecorator('useHoliday', {
                valuePropName: 'checked',
                initialValue: useHoliday,
              })(
                <Checkbox onChange={e => this.onCheckChange(e, 'useHoliday')}>自动更新每年的法定节假日</Checkbox>,
              )}
            </FormItem>
            <FormItem style={{ width: 512, marginBottom: 5 }}>
              {getFieldDecorator('saturdayWork', {
                valuePropName: 'checked',
                initialValue: saturdayWork,
              })(
                <Checkbox onChange={e => this.onCheckChange(e, 'saturdayWork')}>选定周六为工作日</Checkbox>,
              )}
            </FormItem>
            <FormItem style={{ width: 512, marginBottom: 5 }}>
              {getFieldDecorator('sundayWork', {
                valuePropName: 'checked',
                initialValue: sundayWork,
              })(
                <Checkbox onChange={e => this.onCheckChange(e, 'sundayWork')}>选定周日为工作日</Checkbox>,
              )}
            </FormItem>
          </Form>
          <WorkCalendar
            saturdayWork={saturdayWork}
            sundayWork={sundayWork}
            useHoliday={useHoliday}
            selectDays={selectDays}
            holidayRefs={holidayRefs}
            updateSelete={this.updateSelete}
          />
        </Content>
      </Page>
    );
  }
}
export default Form.create({})(withRouter(WorkCalendarHome));
