import React, { Component, Fragment } from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import {
  Page, Header, Content, stores,
} from '@choerodon/boot';
import { Spin } from 'choerodon-ui';
import moment from 'moment';
import { observer, inject } from 'mobx-react';
import EditFeature from '../../Feature/FeatureComponent/FeatureDetail/EditFeature';
import RoadMapContent from './components/RoadMapContent';
import RoadMapHeader from './components/RoadMapHeader';
import { getRoadMap } from '../../../../api/RoadMapApi';
import FeatureStore from '../../../../stores/program/Feature/FeatureStore';
import Empty from '../../../../components/Empty';
import { artListLink } from '../../../../common/utils';
import noPI from '../../../../assets/noPI.svg';

@inject('HeaderStore')
@observer
class RoadMap extends Component {
  state = {
    piList: [],
    loading: false,
    editFeatureVisible: false,
    currentFeature: null,
  }

  componentDidMount() {
    this.loadRoadMap();
  }

  loadRoadMap = () => {
    this.setState({
      loading: true,
    });
    getRoadMap().then((piList) => {
      this.setState({
        piList,
        loading: false,
      });
    });
  }

  getRange = piList => ({
    startDate: piList[0] && piList[0].startDate,
    endDate: piList[piList.length - 1] && piList[piList.length - 1].endDate,
  })

  handleFeatureClick = (feature) => {
    this.setState({
      currentFeature: feature.issueId,
      editFeatureVisible: true,
    });
  }

  handleCancel = () => {
    this.setState({
      currentFeature: null,
      editFeatureVisible: false,
    });
  }

  handleDelete = () => {
    this.setState({
      currentFeature: null,
      editFeatureVisible: false,
    });
    this.loadRoadMap();
  }

  render() {
    const {
      piList, editFeatureVisible, currentFeature, loading,
    } = this.state;
    const { HeaderStore } = this.props;
    const { startDate, endDate } = this.getRange(piList);
    return (
      <Page
        className="c7ntest-Issue c7ntest-region"
        service={[
          'agile-service.pi.queryRoadMapOfProgram',
        ]}
      >
        <Header
          title="路线图"
        />
        <Content style={{ paddingTop: 0 }}>
          <Spin spinning={loading}>
            {
            piList.length > 0 ? (
              <Fragment>
                <RoadMapHeader startDate={startDate} endDate={endDate} />
                <RoadMapContent piList={piList} onFeatureClick={this.handleFeatureClick} currentFeature={currentFeature} />
              </Fragment>
            ) : (
              <Empty
                style={{ marginTop: 60 }}
                pic={noPI}
                title="没有进行中的敏捷发布火车"
                description={(
                  <Fragment>
                      这是您的ART线路图。如果您想看到具体的PI计划，可以先到
                    <Link to={artListLink()}>ART设置</Link>
                      创建开启火车。
                  </Fragment>
                  )}
              />
            )
          }
          </Spin>
          {
            editFeatureVisible && ( 
            <div style={{
              position: 'fixed', bottom: 0, right: 0, top: 155, 
            }}
            >             
              <EditFeature
                store={FeatureStore}
                issueId={currentFeature}
                onCancel={this.handleCancel}
                onUpdate={this.loadRoadMap}
                onDeleteIssue={this.handleDelete}
              />         
            </div>    
            )}
        </Content>
      </Page>
    );
  }
}

RoadMap.propTypes = {

};

export default RoadMap;
