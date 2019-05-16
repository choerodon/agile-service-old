import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import SprintName from './PIHeaderComponent/PIName';
import SprintVisibleIssue from './PIHeaderComponent/SprintVisibleIssue';
import PILastDays from './PIHeaderComponent/PILastDays';
import PIStatus from './PIHeaderComponent/PIStatus';
import PIDateRange from './PIHeaderComponent/PIDateRange';
import '../PI.scss';

@inject('AppState', 'HeaderStore')
@observer class SprintHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      name: props.data.name,
      startDate: props.data.startDate,
      endDate: props.data.endDate,
      objectVersionNumber: props.data.objectVersionNumber,
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      name: nextProps.data.name,
      startDate: nextProps.data.startDate,
      endDate: nextProps.data.endDate,
      objectVersionNumber: nextProps.data.objectVersionNumber,
    });
  }

  handleBlurName = (value) => {
    if (/[^\s]+/.test(value)) {
      const { data, AppState, store } = this.props;
      const { objectVersionNumber } = this.state;
      const req = {
        objectVersionNumber,
        programId: AppState.currentMenuType.id,
        id: data.id,
        name: value,
      };
      store.axiosUpdateSprint(req).then((res) => {
        this.setState({
          name: value,
          objectVersionNumber: res.objectVersionNumber,
        });
      }).catch(() => {
      });
    }
  };

  render() {
    const {
      data, expand, toggleSprint, piId, issueCount, refresh, store, index,
    } = this.props;
    const {
      name, startDate, endDate,
    } = this.state;

    return (
      <div className="c7n-feature-sprintTop">
        <div className="c7n-feature-springTitle">
          <div className="c7n-feature-sprintTitleSide" style={{ flex: 1 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <SprintName
                type="sprint"
                expand={expand}
                piName={name}
                toggleSprint={toggleSprint}
                handleBlurName={this.handleBlurName}
              />
              <SprintVisibleIssue
                issueCount={issueCount}
              />
              {data.statusCode === 'doing'
                ? (
                  <PILastDays
                    startDate={startDate}
                    endDate={endDate}
                  />
                ) : null
              }
            </div>
          </div>
          <div style={{ flex: 9 }}>
            <PIStatus
              piId={piId}
              refresh={refresh}
              store={store}
              data={data}
              statusCode={data.statusCode}
              type="pi"
              index={index}
            />
          </div>
        </div>
        <div
          className="c7n-feature-sprintGoal"
          style={{
            display: 'flex',
          }}
        >
          <PIDateRange
            startDate={startDate}
            endDate={endDate}
          />
        </div>
      </div>
    );
  }
}

export default SprintHeader;
