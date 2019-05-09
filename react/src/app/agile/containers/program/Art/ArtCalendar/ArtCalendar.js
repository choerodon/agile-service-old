import React, { Component } from 'react';
import { Page, Header, Content } from 'choerodon-front-boot';
import { find } from 'lodash';
import { Spin } from 'choerodon-ui';
import moment from 'moment';
import { getArtCalendar, getActiveArt } from '../../../../api/ArtApi';
import { CalendarHeader, CalendarBody } from './components';
import './ArtCalendar.scss';
import emptyArtCalendar from '../../../../assets/image/emptyArtCalendar.svg';
import Empty from '../../../../components/Empty';

class ArtCalendar extends Component {
  state = {
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
  }

  getDuring = (data) => {
    const startDate = data.length > 0 ? data[0].startDate : moment();
    const endDate = data.length > 0 ? data[data.length - 1].endDate : moment().add(7, 'days');
    return {
      startDate,
      endDate,
    };
  }

  handleCreateEventClick=() => {
    this.setState({
      createEventVisible: true,
    });
  }

  handleCancelCreateEvent=() => {
    this.setState({
      createEventVisible: false,
    });
  }

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
      <Page className="c7nagile-ArtCalendar">
        <Header title="ART日历">
          {/* <Button icon="playlist_add" onClick={this.handleCreateEventClick}>
            创建事件
          </Button> */}
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
                    <div className="c7nagile-ArtCalendar-scroller">
                      <div className="c7nagile-ArtCalendar-calendar">
                        <CalendarHeader
                          startDate={startDate}
                          endDate={endDate}
                        />
                        <CalendarBody
                          data={data}
                          startDate={startDate}
                          endDate={endDate}
                        />
                      </div>
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
