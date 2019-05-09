import React, { Component } from 'react';
import { Row, Col } from 'choerodon-ui';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import VersionProgress from '../ReportBoardComponent/VersionProgress';
import IssueType from '../ReportBoardComponent/IssueType';
import IterationType from '../ReportBoardComponent/IterationType';
import IterationSpeed from '../ReportBoardComponent/IterationSpeed';
import EpicProgress from '../ReportBoardComponent/EpicProgress';
import Assignee from '../ReportBoardComponent/Assignee';
import Status from '../ReportBoardComponent/Status';
import Priority from '../ReportBoardComponent/Priority';
import BurnDown from '../ReportBoardComponent/BurnDown';
import './ReportBoardHome.scss';

class ReportBoardHome extends Component {
  render() {
    return (
      <Page className="c7n-agile-reportBoard">
        <Header title="报告工作台" />
        <Content>
          <div className="c7n-reportBoard">
            <Row>
              <Col span={24}>
                <BurnDown
                  link="reporthost/burndownchart"
                />
              </Col>
            </Row>
            <Row gutter={20}>
              <Col span={12}>
                <Assignee
                  link="reporthost/pieReport"
                />
              </Col>
              <Col span={12}>
                <VersionProgress 
                  link="reporthost/versionReport"
                />
              </Col>
            </Row>
            <Row gutter={20}>
              <Col span={10}>
                <EpicProgress
                  link="reporthost/pieReport/epic"
                />
              </Col>
              <Col span={14}>
                <IssueType 
                  link="reporthost/pieReport/typeCode"
                />
              </Col>
            </Row>
            <Row gutter={20}>
              <Col span={8}>
                <Status
                  link="reporthost/pieReport/status"
                />
              </Col>
              <Col span={8}>
                <Priority
                  link="reporthost/pieReport/priority"
                />
              </Col>
              <Col span={8}>
                <IterationType />
              </Col>
            </Row>
            <Row>
              <Col span={24}>
                <IterationSpeed
                  link="reporthost/velocityChart"
                />
              </Col>
            </Row>
          </div>
        </Content>
      </Page>
     
    );
  }
}
export default ReportBoardHome;
