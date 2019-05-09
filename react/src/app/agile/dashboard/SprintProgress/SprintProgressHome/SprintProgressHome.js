import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import { Progress, Spin } from 'choerodon-ui';
import EmptyBlockDashboard from '../../../components/EmptyBlockDashboard';
import './SprintProgressHome.scss';
// import pic from './no_sprint.svg';
import pic from '../../../assets/image/emptyChart.svg';

const { AppState } = stores;

class SprintProgressHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      sprint: {},
      loading: true,
    };
  }

  componentDidMount() {
    this.loadData();
  }

  getTotalDay(startDate, endDate) {
    if (!startDate) return '';
    if (!endDate) return '';
    let sd = startDate.substr(0, 10);
    let ed = endDate.substr(0, 10);
    const aStartDate = sd.split('-');
    const aEndDate = ed.split('-');
    sd = new Date(`${aStartDate[1]}-${aStartDate[2]}-${aStartDate[0]}`);
    ed = new Date(`${aEndDate[1]}-${aEndDate[2]}-${aEndDate[0]}`);
    return (parseInt(Math.abs(sd - ed) / 1000 / 60 / 60 / 24, 10) + 1);
  }

  renderContent = () => {
    const { sprint, loading } = this.state;
    const totalDay = this.getTotalDay(sprint.startDate, sprint.endDate);
    if (loading) {
      return (
        <div className="c7n-loadWrap">
          <Spin />
        </div>
      );
    }

    if (!sprint.sprintId) {
      return (
        <div className="c7n-emptySprint">
          <EmptyBlockDashboard pic={pic} des="当前没有冲刺" />
        </div>
      );
    }

    return (
      <div className="c7n-SprintProgressHome">
        <div className="c7n-SprintContainer">
          <p className="c7n-SprintStage">
            {`${this.transformDateStr(sprint.startDate)}-${this.transformDateStr(sprint.endDate)} ${sprint.sprintName}`}
          </p>
          <p className="c7n-SprintRemainDay">
            {'剩余'}
            <span className="c7n-remainDay">
              {sprint.dayRemain > 0 ? sprint.dayRemain : 0}
            </span>
            {'天'}
          </p>
          <div className="c7n-progress">
            <Progress
              percent={(sprint.dayRemain > 0 ? totalDay - sprint.dayRemain : totalDay)
              / totalDay * 100}
              showInfo={false}
            />
            <span className="c7n-sprintStart">
              {`${this.transformDateStr(sprint.startDate)}`}
            </span>
            <span className="c7n-sprintEnd">
              {`${this.transformDateStr(sprint.endDate)}`}
            </span>
          </div>
        </div>
      </div>
    );
  }


  /**
   * 'MM/DD' format
   * @param {*} date
   */
  transformDateStr(date) {
    if (!date) return '';
    return `${date.substr(5, 2).replace(/\b(0+)/gi, '')}/${date.substr(8, 2)}`;
  }

  loadData() {
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`agile/v1/projects/${projectId}/sprint/active/${orgId}`)
      .then((res) => {
        this.setState({
          sprint: res,
          loading: false,
        });
      });
  }

  render() {
    const { sprint, loading } = this.state;
    const totalDay = this.getTotalDay(sprint.startDate, sprint.endDate);
    return (
      <React.Fragment>
        { this.renderContent() }
      </React.Fragment>
    );
  }
}
export default SprintProgressHome;
