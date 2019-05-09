import React, { Component } from 'react';
import { axios, stores } from 'choerodon-front-boot';
import './UncompleteTaskHome.scss';
import { Spin } from 'choerodon-ui';
import Progress from '../../../components/Progress';

const { AppState } = stores;
class UncompleteTaskHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      completeInfo: {},
      loading: true,
    };
  }

  componentDidMount() {
    this.loadDate();
  }

  loadDate() {
    const projectId = AppState.currentMenuType.id;
    axios.get(`agile/v1/projects/${projectId}/issues/count`)
      .then((res) => {
        this.setState({
          completeInfo: res,
          loading: false,
        });
      });
  }

  render() {
    const { completeInfo, loading } = this.state;
    return (
      <div className="c7n-unCompleteTaskHome">
        {
         loading ? (
           <div className="c7n-loadWrap">
             <Spin />
           </div>
         ) : (
           <Progress
             percent={completeInfo.unresolved / completeInfo.all * 100}
             title={completeInfo.unresolved}
             unit="个"
           />
         )
       }
      </div>
    );
  }
}
export default UncompleteTaskHome;
