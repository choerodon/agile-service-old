import React, { Component } from 'react';
import { Page, Header, Content } from '@choerodon/boot';
import { find } from 'lodash';
import { Spin, Button } from 'choerodon-ui';
import moment from 'moment';
import 'tui-calendar/dist/tui-calendar.css';
import Calendar from '@toast-ui/react-calendar';
import { getArtCalendar, getActiveArt } from '../../../../api/ArtApi';
import './ArtCalendar.scss';
import emptyArtCalendar from '../../../../assets/image/emptyArtCalendar.svg';
import Empty from '../../../../components/Empty';

class ArtCalendar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      artStartDate: null,
      doingArt: undefined,
      ArtName: null,
      data: null,
      currentPI: null,
      startDate: null,
      endDate: null,
      loading: true,
      createEventVisible: false,
      createEventLoading: false,
    };
    this.calendar = React.createRef();
  }


  componentDidMount() {
    this.loadArt();
  }

  loadArt = () => {
    this.setState({
      loading: true,
    });
    getActiveArt().then((doingArt) => {
      this.setState({
        doingArt,
        artStartDate: doingArt && doingArt.startDate,
      }, () => {
        if (doingArt && doingArt.id) {
          getArtCalendar(doingArt.id).then((res) => {
            this.setState({
              loading: false,
            });
            const data = res.sort((a, b) => a.id - b.id);
            const { startDate, endDate } = this.getDuring(data);
            this.setState({
              data,
              ArtName: doingArt.name,
              currentPI: find(data, { statusCode: 'doing' }),
              startDate,
              endDate,
            });
          });
        } else {
          this.setState({
            loading: false,
          });
        }
      });
    });
  };

  getDuring = (data) => {
    const startDate = data.length > 0 ? data[0].startDate : moment();
    const endDate = data.length > 0 ? data[data.length - 1].endDate : moment().add(7, 'days');
    return {
      startDate,
      endDate,
    };
  };

  handleCreateEventClick=() => {
    this.setState({
      createEventVisible: true,
    });
  };

  handleCancelCreateEvent=() => {
    this.setState({
      createEventVisible: false,
    });
  };

  getSchedules = (data) => {
    const schedules = [];
    if (data) {
      data.forEach((pi) => {
        const sprintList = pi.sprintCalendarDTOList.sort((a, b) => a.sprintId - b.sprintId);
        if (sprintList) {
          sprintList.forEach((sprint) => {
            schedules.push({
              id: sprint.sprintId,
              calendarId: '0',
              title: `${sprint.sprintName}`,
              category: 'allday',
              dueDateClass: '',
              start: moment(sprint.startDate.slice(0, 10)).format('YYYY-MM-DD'),
              end: moment(sprint.endDate.slice(0, 10)).subtract(1, 'days').format('YYYY-MM-DD'),
            });
          });
          const lastSprintEndDate = sprintList[sprintList.length - 1].endDate;
          const piDays = sprintList && sprintList.length > 0 ? !moment(lastSprintEndDate).isSame(pi.endDate) : false;
          if (piDays) {
            schedules.push({
              id: `ip-${pi.id}`,
              calendarId: '1',
              title: `${pi.code}-${pi.name} IP`,
              category: 'allday',
              dueDateClass: '',
              start: moment(lastSprintEndDate.slice(0, 10)).format('YYYY-MM-DD'),
              end: moment(pi.endDate.slice(0, 10)).subtract(1, 'days').format('YYYY-MM-DD'),
            });
          }
        }
      });
    }
    return schedules;
  };

  onClickBtn = (action) => {
    this.calendar.current.getInstance()[action]();
  };

  render() {
    const {
      data, 
      startDate,
      currentPI, 
      ArtName,
      endDate,
      doingArt,
      loading,
      artStartDate,
      createEventVisible,
      createEventLoading,
    } = this.state;
    return (
      <Page
        className="c7nagile-ArtCalendar"
        service={[
          'agile-service.art.queryArtCalendar',
        ]}
      >
        <Header title="ART日历">
          {/* <Button icon="playlist_add" onClick={this.handleCreateEventClick}>
            创建事件
          </Button> */}
          <Button icon="refresh" onClick={this.loadArt}>
            刷新
          </Button>
        </Header>
        <Content style={{ padding: 0 }}>
          <Spin spinning={loading}>
            {
              doingArt && data ? (
                <div style={{
                  display: 'flex', flexDirection: 'column', padding: 0, height: '100%',
                }}
                >
                  <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                    <div className="c7nagile-ArtCalendar-bar">
                      <span style={{ fontSize: '16px' }}>{ArtName && ArtName}</span>
                      <span style={{ margin: '0 40px' }}>
                        {'开始日期：'}
                        {artStartDate && moment(artStartDate).format('YYYY-MM-DD')}
                      </span>
                      {currentPI && (
                        <span>
                          {'正在进行中的PI：'}
                          {`${currentPI.code}-${currentPI.name}`}
                        </span>
                      )}
                    </div>
                    <div style={{ padding: 15 }}>
                      <div style={{ marginBottom: 10 }}>
                        <Button onClick={() => this.onClickBtn('today')}>
                          今天
                        </Button>
                        <Button style={{ marginLeft: 10 }} funcType="raised" shape="circle" icon="navigate_before" onClick={() => this.onClickBtn('prev')} />
                        <Button style={{ marginLeft: 10 }} funcType="raised" shape="circle" icon="navigate_next" onClick={() => this.onClickBtn('next')} />
                      </div>
                      <Calendar
                        calendars={[
                          {
                            id: '0',
                            name: 'Sprint',
                            bgColor: '#E5F9F6',
                            borderColor: '#FFF',
                          },
                          {
                            id: '1',
                            name: 'PI',
                            bgColor: '#F7F7F7',
                            borderColor: '#FFF',
                          },
                        ]}
                        defaultView="month"
                        disableDblClick
                        height="calc(80vh - 30px)"
                        isReadOnly
                        month={{
                          startDayOfWeek: 0,
                        }}
                        schedules={this.getSchedules(data)}
                        template={{
                          milestone(schedule) {
                            return `<span style="color:#fff;background-color: ${schedule.bgColor};">${schedule.title}</span>`;
                          },
                          milestoneTitle() {
                            return '里程碑';
                          },
                          taskTitle() {
                            return '任务';
                          },
                          allday(schedule) {
                            return `${schedule.title}<i class="fa fa-refresh"></i>`;
                          },
                          alldayTitle() {
                            return 'All Day';
                          },
                        }}
                        theme={{}}
                        useDetailPopup
                        // useCreationPopup
                        view="month"
                        taskView // e.g. true, false, or ['task', 'milestone'])
                        scheduleView={['time']} // e.g. true, false, or ['allday', 'time'])
                        timezones={[{
                          timezoneOffset: 540,
                          displayLabel: 'GMT+08:00',
                          tooltip: 'Seoul',
                        }]}
                        ref={this.calendar}
                      />
                    </div>
                  </div>
                </div>
              ) : (
                <Empty
                  style={{ marginTop: 60 }}
                  pic={emptyArtCalendar}
                  title="计划您的敏捷发布火车"
                  description="这是您的ART日历。如果您想看到具体的计划，请先设置火车的PI节奏，然后开启火车。"
                  border             
                />
              )
            }
          </Spin>
          {/* <CreateEvent 
            visible={createEventVisible}
            loading={createEventLoading}
            onCancel={this.handleCancelCreateEvent}
            onSubmit={this.handleEventSubmit}
          /> */}
        </Content>
      </Page>
    );
  }
}

ArtCalendar.propTypes = {

};

export default ArtCalendar;
