import React, { Component } from 'react';
import { Page, Header, Content } from '@choerodon/boot';
import { find } from 'lodash';
import { Spin, Button } from 'choerodon-ui';
import moment from 'moment';
import 'tui-calendar/dist/tui-calendar.css';
import Calendar from '@toast-ui/react-calendar';
import { getArtCalendar, getActiveArt } from '../../../../api/QueryProgramApi';
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
    window.addEventListener('click', this.onCalendarClick);
  }

  componentWillUnmount() {
    window.removeEventListener('click', this.onCalendarClick);
  }

  onCalendarClick = (e) => {
    const layers = document.getElementsByClassName('tui-full-calendar-floating-layer');
    if (layers.length === 2) {
      if (layers[1].style.left && layers[1].style.left.slice(0, 1) === '-') {
        layers[1].style.left = '0px';
      }
    }
  };

  loadArt = () => {
    const { programId } = this.props;
    this.setState({
      loading: true,
    });
    getActiveArt(programId).then((doingArt) => {
      this.setState({
        doingArt,
        artStartDate: doingArt && doingArt.startDate,
      }, () => {
        if (doingArt && doingArt.id) {
          getArtCalendar(doingArt.id, programId).then((res) => {
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

  getItemStatus = (pi, sprint, todayIsBetweenPI, index, sprintListLength, ip) => {
    if (pi.statusCode === 'doing') {
      const todayIsBetween = moment().isBetween(sprint.startDate, sprint.endDate);
      if (todayIsBetween) {
        return 'doing';
      } else if (moment().isBefore(moment(sprint.startDate))) {
        return !todayIsBetweenPI && index === 0 ? 'doing' : 'todo';
      } else if (moment().isAfter(moment(sprint.endDate))) {
        return !todayIsBetweenPI && index === sprintListLength && !ip ? 'doing' : 'todo';
      }
    } else if (pi.statusCode === 'todo') {
      return 'todo';
    } else if (pi.statusCode === 'done') {
      return 'done';
    }
  };

  getSchedules = (data) => {
    const schedules = [];
    if (data) {
      data.forEach((pi) => {
        const sprintList = pi.sprintCalendarDTOList.sort((a, b) => a.sprintId - b.sprintId);
        const todayIsBetweenPI = moment().isBetween(pi.startDate, pi.endDate);
        const sprintListLength = sprintList.length;
        if (sprintList) {
          const ipDays = sprintList && sprintList.length > 0 ? !moment(lastSprintEndDate).isSame(pi.endDate) : false;
          sprintList.forEach((sprint, index) => {
            schedules.push({
              id: sprint.sprintId,
              calendarId: this.getItemStatus(pi, sprint, todayIsBetweenPI, index, sprintListLength, ipDays),
              title: `${sprint.sprintName}`,
              category: 'allday',
              dueDateClass: '',
              start: moment(sprint.startDate.slice(0, 10)).format('YYYY-MM-DD'),
              end: moment(sprint.endDate.slice(0, 10)).subtract(1, 'days').format('YYYY-MM-DD'),
            });
          });
          const lastSprintEndDate = sprintList[sprintList.length - 1].endDate;
          if (ipDays) {
            schedules.push({
              id: `ip-${pi.id}`,
              calendarId: 'ip',
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
      currentPI, 
      ArtName,
      doingArt,
      loading,
      artStartDate,
    } = this.state;
    return (
      <Page
        className="c7nagile-ArtCalendar"
        service={[
          'agile-service.art.queryArtCalendar',
        ]}
      >
        <Header title="ART日历">
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
                            id: 'done',
                            name: 'Sprint',
                            bgColor: '#D2F7F2',
                            borderColor: '#FFF',
                          },
                          {
                            id: 'doing',
                            name: 'Sprint',
                            bgColor: '#E3EFFF',
                            borderColor: '#FFF',
                          },
                          {
                            id: 'todo',
                            name: 'Sprint',
                            bgColor: '#FFF4D6',
                            borderColor: '#FFF',
                          },
                          {
                            id: 'ip',
                            name: 'IP',
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
        </Content>
      </Page>
    );
  }
}

ArtCalendar.propTypes = {

};

export default ArtCalendar;
