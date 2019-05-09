import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Form, Modal, Select, Input, DatePicker, Icon,
} from 'choerodon-ui';
import { Content, stores } from 'choerodon-front-boot';
import _ from 'lodash';
import moment from 'moment';
import WorkCalendar from '../../../../../components/WorkCalendar';
// import this.props.store from '../../../../../stores/project/backlog/this.props.store';

const { Sidebar } = Modal;
const FormItem = Form.Item;
const { TextArea } = Input;
const { Option } = Select;
const { AppState } = stores;
const format = 'YYYY-MM-DD';

@observer
class StartSprint extends Component {
  constructor(props) {
    super(props);
    this.state = {
      startDate: null,
      endDate: null,
      showCalendar: false,
      workDates: [], // 冲刺自定义设置
    };
  }

  /**
   *开启冲刺事件
   *
   * @param {*} e
   * @memberof StartSprint
   */
  handleStartSprint =(e) => {
    e.preventDefault();
    const { workDates } = this.state;
    const {
      form, data, store, onCancel, refresh,
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const newData = {
          endDate: values.endDate ? `${moment(values.endDate).format('YYYY-MM-DD HH:mm:ss')}` : null,
          startDate: values.startDate ? `${moment(values.startDate).format('YYYY-MM-DD HH:mm:ss')}` : null,
          projectId: AppState.currentMenuType.id,
          sprintGoal: values.goal,
          sprintId: data.sprintId,
          sprintName: values.name,
          objectVersionNumber: data.objectVersionNumber,
          workDates,
        };
        store.axiosStartSprint(newData).then((res) => {
          onCancel();
          refresh();
        }).catch((error) => {
        });
      }
    });
  };

  showWorkCalendar = () => {
    const { showCalendar } = this.state;
    this.setState({ showCalendar: !showCalendar });
  };

  getWorkDays = (startDate, endDate) => {
    // 是否显示非工作日
    const { store } = this.props;
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      timeZoneWorkCalendarDTOS: selectDays,
      workHolidayCalendarDTOS: holidayRefs,
    } = store.getWorkSetting;
    const { workDates } = this.state;
    const weekdays = [
      saturdayWork ? null : '六',
      sundayWork ? null : '日',
    ];
    const result = [];
    const beginDay = moment(startDate).format(format).split('-');
    const endDay = moment(endDate).format(format).split('-');
    const diffDay = new Date();
    const dateList = [];
    let i = 0;
    diffDay.setDate(beginDay[2]);
    diffDay.setMonth(beginDay[1] - 1);
    diffDay.setFullYear(beginDay[0]);
    while (i === 0) {
      const localData = moment.localeData();
      // 周六日
      const isWeekDay = weekdays.includes(localData.weekdaysMin(moment(diffDay)));
      // 冲刺自定义设置
      const workDate = workDates.filter(date => date.workDay === moment(diffDay).format('YYYY-MM-DD'));
      // 工作日历自定义设置
      const selectDay = selectDays.filter(date => date.workDay === moment(diffDay).format('YYYY-MM-DD'));
      // 法定假期
      let holiday = false;
      if (useHoliday && holidayRefs.length) {
        holiday = holidayRefs.filter(date => date.holiday === moment(diffDay).format('YYYY-MM-DD'));
      }
      if (workDate.length) {
        if (workDate[0].status === 1) {
          result.push(workDate.workDay);
        }
      } else if (selectDay.length) {
        if (selectDay[0].status === 1) {
          result.push(selectDay.workDay);
        }
      } else if (holiday && holiday.length) {
        if (holiday[0].status === 1) {
          result.push(holiday.holiday);
        }
      } else if (!isWeekDay) {
        result.push(moment(diffDay).format('YYYY-MM-DD'));
      }
      dateList[2] = diffDay.getDate();
      dateList[1] = diffDay.getMonth() + 1;
      dateList[0] = diffDay.getFullYear();
      if (String(dateList[1]).length === 1) { dateList[1] = `0${dateList[1]}`; }
      if (String(dateList[2]).length === 1) { dateList[2] = `0${dateList[2]}`; }
      if (String(dateList[0]) === endDay[0]
          && String(dateList[1]) === endDay[1]
          && String(dateList[2]) === endDay[2]) {
        i = 1;
      }
      const countDay = diffDay.getTime() + 24 * 60 * 60 * 1000;
      diffDay.setTime(countDay);
    }
    return result.length;
  };

  onWorkDateChange = (workDates) => {
    this.setState({
      workDates,
    });
  };

  render() {
    const {
      data,
      store,
      visible,
      onCancel,
      form: { getFieldDecorator, getFieldValue, setFieldsValue },
    } = this.props;
    const {
      showCalendar,
      startDate,
      endDate,
    } = this.state;
    const { piId, startDate: start, endDate: end } = data;
    const {
      saturdayWork,
      sundayWork,
      useHoliday,
      timeZoneWorkCalendarDTOS: selectDays,
      workHolidayCalendarDTOS: holidayRefs,
    } = store.getWorkSetting;
    const completeMessage = JSON.stringify(store.getOpenSprintDetail) === '{}' ? null : store.getOpenSprintDetail;
    return (
      <Sidebar
        title="开启冲刺"
        visible={visible}
        okText="开启"
        cancelText="取消"
        onCancel={() => {
          this.setState({ showCalendar: false });
          onCancel();
        }}
        onOk={this.handleStartSprint}
      >
        <Content
          style={{
            padding: 0,
            paddingBottom: 20,
          }}
          title={`开启冲刺“${data.sprintName}”`}
          description="请在下面输入冲刺名称、目标，选择冲刺的时间周期范围，开启新的冲刺。每个项目中仅能有一个活跃的冲刺，同时尽量避免在开启的冲刺中添加新的故事和任务，尽可能在开启冲刺前的迭代会议上完成冲刺的任务范围。"

        >
          <p className="c7n-closeSprint-message">
            <span>{!_.isNull(completeMessage) ? completeMessage.issueCount : ''}</span>
            {' 个问题 将包含在此Sprint中'}
          </p>
          <Form style={{ width: 512, marginTop: 24 }}>
            <FormItem>
              {getFieldDecorator('name', {
                initialValue: !_.isNull(completeMessage) ? completeMessage.sprintName : null,
                rules: [{
                  required: true,
                  message: '冲刺名称是必填的',
                }],
              })(
                <Input label="Sprint名称" maxLength={30} disabled={!!data.piId} />,
              )}
            </FormItem>
            <FormItem>
              {getFieldDecorator('goal', {
                initialValue: !_.isNull(completeMessage) ? completeMessage.sprintGoal : null,
              })(
                <TextArea label="目标" autoSize maxLength={30} />,
              )}
            </FormItem>
            {!piId
              ? (
                <FormItem>
                  {getFieldDecorator('duration', {
                    initialValue: '0',
                  })(
                    <Select
                      label="周期"
                      onChange={(value) => {
                        if (parseInt(value, 10) > 0) {
                          if (!getFieldValue('startDate')) {
                            setFieldsValue({
                              startDate: moment(),
                            });
                            this.setState({
                              startDate: moment(),
                            });
                          }
                          setFieldsValue({
                            endDate: moment(getFieldValue('startDate')).add(parseInt(value, 10), 'w'),
                          });
                          this.setState({
                            endDate: moment(getFieldValue('startDate')).add(parseInt(value, 10), 'w'),
                          });
                        }
                      }}
                    >
                      <Option value="0">自定义</Option>
                      <Option value="1">1周</Option>
                      <Option value="2">2周</Option>
                      <Option value="4">4周</Option>
                    </Select>,
                  )}
                </FormItem>
              ) : ''
            }
            {!piId
              ? (
                <FormItem>
                  {getFieldDecorator('startDate', {
                    rules: [{
                      required: true,
                      message: '开始日期是必填的',
                    }],
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      label="开始日期"
                      showTime
                      format="YYYY-MM-DD HH:mm:ss"
                      disabledDate={endDate
                        ? current => current > moment(endDate) || current < moment().subtract(1, 'days') : current => current < moment().subtract(1, 'days')}
                      onChange={(date, dateString) => {
                        setFieldsValue({
                          startDate: date,
                        });
                        this.setState({
                          startDate: date,
                        });
                        if (parseInt(getFieldValue('duration'), 10) > 0) {
                          setFieldsValue({
                            endDate: moment(getFieldValue('startDate')).add(parseInt(getFieldValue('duration'), 10), 'w'),
                          });
                          this.setState({
                            endDate: moment(getFieldValue('startDate')).add(parseInt(getFieldValue('duration'), 10), 'w'),
                          });
                        }
                      }}
                    />,
                  )}
                </FormItem>
              ) : (
                <FormItem>
                  {getFieldDecorator('startDate', {
                    rules: [{
                      required: true,
                      message: '开始日期是必填的',
                    }],
                    initialValue: moment(start),
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      label="开始日期"
                      showTime
                      format="YYYY-MM-DD HH:mm:ss"
                      disabled
                    />,
                  )}
                </FormItem>
              )
            }
            {!piId
              ? (
                <FormItem>
                  {getFieldDecorator('endDate', {
                    rules: [{
                      required: true,
                      message: '结束日期是必填的',
                    }],
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      label="结束日期"
                      format="YYYY-MM-DD HH:mm:ss"
                      disabled={parseInt(getFieldValue('duration'), 10) > 0}
                      showTime
                      onChange={(date) => {
                        this.setState({
                          endDate: date,
                        });
                      }}
                      disabledDate={startDate
                        ? current => current < moment(startDate) || current < moment().subtract(1, 'days')
                        : current => current < moment().subtract(1, 'days')}
                    />,
                  )}
                </FormItem>
              ) : (
                <FormItem>
                  {getFieldDecorator('endDate', {
                    rules: [{
                      required: true,
                      message: '结束日期是必填的',
                    }],
                    initialValue: moment(end),
                  })(
                    <DatePicker
                      style={{ width: '100%' }}
                      label="结束日期"
                      format="YYYY-MM-DD HH:mm:ss"
                      disabled
                      showTime
                    />,
                  )}
                </FormItem>
              )
            }
          </Form>
          {!piId && startDate && endDate
            ? (
              <div>
                <div style={{ marginBottom: 20 }}>
                  <span style={{ marginRight: 20 }}>
                    {`此Sprint中有${this.getWorkDays(startDate, endDate)}个工作日`}
                  </span>
                  <Icon type="settings" style={{ verticalAlign: 'top' }} />
                  <a onClick={this.showWorkCalendar} role="none">
                  设置当前冲刺工作日
                  </a>
                </div>
                {showCalendar
                  ? (
                    <WorkCalendar
                      startDate={startDate.format(format)}
                      endDate={endDate.format(format)}
                      mode="BacklogComponent"
                      saturdayWork={saturdayWork}
                      sundayWork={sundayWork}
                      useHoliday={useHoliday}
                      selectDays={selectDays}
                      holidayRefs={holidayRefs}
                      onWorkDateChange={this.onWorkDateChange}
                    />
                  ) : null
              }
              </div>
            ) : ''
          }
          {piId
            ? (
              <div>
                <div style={{ marginBottom: 20 }}>
                  <span style={{ marginRight: 20 }}>
                    {`此Sprint中有${this.getWorkDays(start, end)}个工作日`}
                  </span>
                  <Icon type="settings" style={{ verticalAlign: 'top' }} />
                  <a onClick={this.showWorkCalendar} role="none">
                    设置当前冲刺工作日
                  </a>
                </div>
                {showCalendar
                  ? (
                    <WorkCalendar
                      startDate={moment(start).format(format)}
                      endDate={moment(end).format(format)}
                      mode="BacklogComponent"
                      saturdayWork={saturdayWork}
                      sundayWork={sundayWork}
                      useHoliday={useHoliday}
                      selectDays={selectDays}
                      holidayRefs={holidayRefs}
                      onWorkDateChange={this.onWorkDateChange}
                    />
                  ) : null
                }
              </div>
            ) : ''
          }
        </Content>
      </Sidebar>
    );
  }
}

export default Form.create()(StartSprint);
