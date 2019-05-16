import React, { Component } from 'react';
import Moment from 'moment';
import { Popover } from 'choerodon-ui';
import { extendMoment } from 'moment-range';
import StatusTag from '../../../../../../components/StatusTag';

import './PiItem.scss';

const moment = extendMoment(Moment);
const STATUSCODES = {
  sprint_planning: 'todo',
  started: 'doing',
  closed: 'done',
};
const STATUSNAMES = {
  todo: '未开启',
  doing: '进行中',
  done: '已完成',
};
const STATUS = {
  cancel: {
    borderColor: '#393E46',
    PIColor: 'black',
    backgroundColor: '#EBEBEB',
    sprintBorder: '#C0C0C0',
  },
  todo: {
    borderColor: '#FFB100',
    PIColor: ' #FFB100',
    backgroundColor: '#FFF8E7',
    sprintBorder: '#F9D88E',
  },
  doing: {
    borderColor: '#4D90FE',
    PIColor: 'black',
    backgroundColor: '#E9F1FF',
    sprintBorder: '#A2C1F6',
  },
  done: {
    borderColor: '#00BFA5',
    PIColor: 'black',
    backgroundColor: '#E5F9F6',
    sprintBorder: '#98D8CF',
  },
};
const SprintItem = ({
  borderColor, sprint, todayIsBetween,
}) => {
  const isDoing = sprint.statusCode === 'started';
  return (
    <div
      className="PiItem-pi-sprint"
      style={{
        borderColor, background: isDoing && '#4D90FE', color: isDoing && 'white', flex: moment.range(sprint.startDate, sprint.endDate).diff('days'),
      }}
    >
      <Popover
        className={`PiItem-pi-sprintPopover ${STATUSCODES[sprint.statusCode]}`}
        getPopupContainer={triggerNode => document.getElementsByClassName('c7nagile-ArtCalendar-scroller')[0]}
        content={<CardTitle data={sprint} type="sprint" />}
        title={null}
        placement="bottomLeft"
      >
        <div style={{ padding: '0 10px', background: `${todayIsBetween ? '#4D90FE' : '#fff'}`, color: `${todayIsBetween ? '#fff' : '#000'}` }}>
          {sprint.sprintName}
        </div>
      </Popover>
    </div>
  );
};
const CardTitle = ({
  data,
  type,
}) => {
  const {
    name, code, sprintName, statusCode, startDate, endDate,
  } = data;
  const status = type === 'sprint' ? STATUSCODES[statusCode] : statusCode;
  return (
    <div>
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <div>{type === 'sprint' ? sprintName : `${code}-${name}`}</div>
        <div style={{ flex: 1, visibility: 'hidden' }} />
        {
          type === 'pi' && (
            <StatusTag categoryCode={status} name={STATUSNAMES[status]} />
          )
        }
      </div>
      <div style={{ margin: '10px 0 0', color: '#9B9B9B', whiteSpace: 'nowrap' }}>
        {`${moment(startDate).format('YYYY-MM-DD')} ~ ${moment(endDate).format('YYYY-MM-DD')}`}
      </div>
    </div>
  );
};
class PiItem extends Component {
  render() {
    const { pi, diff } = this.props;
    const {
      startDate, endDate, code, name, statusCode, sprintCalendarDTOList, isLast,
    } = pi;
    const flex = moment.range(startDate, endDate).diff('days');
    const SprintList = sprintCalendarDTOList.sort((a, b) => a.sprintId - b.sprintId);
    const ipStartDate = SprintList && SprintList.length > 0 ? SprintList[SprintList.length - 1].endDate : moment();
    const ipEndDate = endDate;
    const ipWeeks = SprintList && SprintList.length > 0 ? moment.range(SprintList[SprintList.length - 1].endDate, endDate).diff('days') : 0;

    const style = STATUS[statusCode];    
    return (
      <div
        className="PiItem"
        style={{
          flex: isLast ? flex + 1 : flex,
        }}
      >
        <div className="PiItem-pi">
          <div className={`PiItem-pi-title BorderLeft ${diff ? 'BorderRight' : ''} ${statusCode}`} style={{ borderColor: style.borderColor, background: style.backgroundColor, color: style.PIColor }}>
            <Popover
              // autoAdjustOverflow={false}
              className={`PiItem-pi-piPopover ${statusCode}`}
              getPopupContainer={triggerNode => document.getElementsByClassName('c7nagile-ArtCalendar-scroller')[0]}
              content={<CardTitle data={pi} type="pi" />}
              title={null}
              placement="bottomLeft"
            >
              <div>
                {`${code}-${name}`}
              </div>
            </Popover>
          </div>
          <div className="PiItem-pi-sprints">
            {SprintList.map((sprint) => {
              const todayIsBetween = moment().isBetween(sprint.startDate, sprint.endDate);
              return <SprintItem borderColor={style.sprintBorder} sprint={sprint} todayIsBetween={todayIsBetween} />;
            })}
            {ipWeeks ? (
              <div className="PiItem-pi-sprint" style={{ flex: ipWeeks, borderColor: style.sprintBorder }}>
                <Popover
                  className="PiItem-pi-sprintPopover"
                  getPopupContainer={triggerNode => document.getElementsByClassName('c7nagile-ArtCalendar-scroller')[0]}
                  content={(
                    <CardTitle
                      data={{
                        type: 'sprint',
                        sprintName: 'IP',
                        startDate: ipStartDate,
                        endDate: ipEndDate,
                      }}
                      type="sprint"
                    />
                )}
                  title={null}
                  placement="bottomLeft"
                >
                  <div style={{ padding: '0 10px', backgroundColor: '#E5E5E5' }}>
                    {'IP'}
                  </div>
                </Popover>
              </div>
            ) : null}
          </div>
        </div>
      </div>
    );
  }
}
    
PiItem.propTypes = {

};
        
export default PiItem;
