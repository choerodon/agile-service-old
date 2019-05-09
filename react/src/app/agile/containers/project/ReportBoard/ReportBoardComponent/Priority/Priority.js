import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import _ from 'lodash';
import { Spin } from 'choerodon-ui';
import PriorityTag from '../../../../../components/PriorityTag';
import EmptyBlockDashboard from '../../../../../components/EmptyBlockDashboard';
// import pic2 from '../EmptyPics/no_version.svg';
import pic2 from '../../../../../assets/image/emptyChart.svg';
import './Priority.scss';
import _default from 'choerodon-ui/lib/card/Grid';

const { AppState } = stores;
const PRIORITY_MAP = {
  medium: {
    color: '#3575df',
    bgColor: 'rgba(77, 144, 254, 0.2)',
    name: '中',
  },
  high: {
    color: '#f44336',
    bgColor: 'rgba(244, 67, 54, 0.2)',
    name: '高',
  },
  low: {
    color: 'rgba(0, 0, 0, 0.36)',
    bgColor: 'rgba(0, 0, 0, 0.08)',
    name: '低',
  },
  default: {
    color: 'transparent',
    bgColor: 'transparent',
    name: '',
  },
};

class Priority extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      priorityInfo: [],
    };
  }

  componentDidMount() {
    this.loadPriorityInfo();
  }

  loadPriorityInfo() {
    this.setState({ loading: true });
    const projectId = AppState.currentMenuType.id;
    const orgId = AppState.currentMenuType.organizationId;
    axios.get(`agile/v1/projects/${projectId}/reports/issue_priority_distribution_chart?organizationId=${orgId}`)
      .then((res) => {
        this.setState({
          priorityInfo: res,
          loading: false,
        });
      });
  }

  renderContent() {
    const { loading, priorityInfo } = this.state;
    if (loading) {
      return (
        <div className="loading-wrap">
          <Spin />
        </div>
      );
    }
    if (priorityInfo.length === 0) {
      return (
        <div className="loading-wrap">
          <EmptyBlockDashboard
            pic={pic2}
            des="当前项目下无问题"
          />
        </div>
      );
    }
    return (
      <div className="lists">
        <h3 className="title">已完成/总计数</h3>
        <div className="wrapper">
          {this.renderPriority()}
        </div>
      </div>
    );
  }

  renderPriority() {
    const { priorityInfo } = this.state;
    return priorityInfo.map(priority => (
      <div className="list" key={priority.priorityDTO.id}>
        <div className="tip">
          {`${priority.doneCount}/${priority.totalCount}`}
        </div>
        <div className="body">
          <div>
            <PriorityTag
              priority={priority.priorityDTO}
            />
          </div>
          <div className="progress">
            <div
              className="progress-bg"
              style={{ background: `${priority.priorityDTO.colour}1F` }}
            />
            <div
              className="progress-inner"
              style={{
                background: priority.priorityDTO.colour,
                width: `${priority.doneCount / priority.totalCount * 100}%`,
              }}
            />
          </div>
        </div>
      </div>
    ));
  }

  //   const arrPriorityName = _.map(priorityInfo, 'name');
  //   if (arrPriorityName.findIndex(item => item === priorityName) === -1) {
  //     return (
  //       <div className="list">
  //         <div className="tip">
  //           {'0/0'}
  //         </div>
  //         <div className="body">
  //           <div>
  //             <PriorityTag
  //               priority={
  //                 {
  //                   name: priorityName,
  //                   colour: color,
  //                 }
  //               }
  //             />
  //           </div>
  //           <div className="progress">
  //             <div
  //               className="progress-bg"
  //               style={{ background: `${color}1F` }}
  //             />
  //             <div
  //               className="progress-inner"
  //               style={{
  //                 background: color,
  //                 width: `${0 / 1 * 100}%`,
  //               }}
  //             />
  //           </div>
  //         </div>
  //       </div>
  //     );
  //   } else {
  //     const priority = _.find(priorityInfo, { name: priorityName });
  //
  //   }
  // }
  
  render() {
    return (
      <div className="c7n-agile-reportBoard-priority">
        {this.renderContent()}
      </div>
    );
  }
}

export default Priority;
