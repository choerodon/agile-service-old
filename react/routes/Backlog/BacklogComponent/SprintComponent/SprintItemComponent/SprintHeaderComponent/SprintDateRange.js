import React, { Component } from 'react';
import moment from 'moment';
import { observer, inject } from 'mobx-react';
import EasyEdit from '../../../../../../../components/EasyEdit/EasyEdit';
// import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

@inject('AppState', 'HeaderStore')
@observer class SprintDateRange extends Component {
  componentWillUpdate(nextProps, nextState, nextContext) {

  }

  handleUpdateDate = (type, dateString) => {
    const { handleChangeDateRange } = this.props;
    handleChangeDateRange(type, dateString);
  };

  onClick = (e) => {
    e.stopPropagation();
  };

  render() {
    const {
      statusCode, startDate, endDate, disabled,
    } = this.props;
    return statusCode === 'started' ? (
      <div
        onClick={this.onClick}
        className="c7n-backlog-sprintData"
        style={{
          display: 'flex',
          flexWrap: 'wrap',
        }}
        role="none"
      >
        <EasyEdit
          disabled={disabled}
          type="date"
          time
          defaultValue={startDate ? moment(startDate, 'YYYY-MM-DD HH-mm-ss') : ''}
          disabledDate={endDate ? current => current > moment(endDate, 'YYYY-MM-DD HH:mm:ss') : ''}
          onChange={(date, dateString) => {
            this.handleUpdateDate('startDate', dateString);
            // this.updateDate('startDate', dateString, item);
          }}
        >
          <div
            className="c7n-backlog-sprintDataItem"
            role="none"
          >
            {startDate ? moment(startDate, 'YYYY-MM-DD HH:mm:ss').format('YYYY年MM月DD日 HH:mm:ss') : '无'}
          </div>
        </EasyEdit>
        <p>~</p>
        <EasyEdit
          disabled={disabled}
          type="date"
          time
          defaultValue={endDate ? moment(endDate, 'YYYY-MM-DD HH-mm-ss') : ''}
          disabledDate={startDate ? current => current < moment(startDate, 'YYYY-MM-DD HH:mm:ss') : ''}
          onChange={(date, dateString) => {
            this.handleUpdateDate('endDate', dateString);
            // this.updateDate('endDate', dateString, item);
          }}
        >
          <div
            onClick={this.onClick}
            className="c7n-backlog-sprintDataItem"
            role="none"
          >
            {endDate ? moment(endDate, 'YYYY-MM-DD HH:mm:ss').format('YYYY年MM月DD日 HH:mm:ss') : '无'}
          </div>
        </EasyEdit>
      </div>
    ) : null;
  }
}

export default SprintDateRange;
