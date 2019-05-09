import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Page, Header, Content, stores,
} from 'choerodon-front-boot';
import { Row, Col, Button } from 'choerodon-ui';
import { loadSprint } from '../../../../api/NewIssueApi';
import Assignee from '../IterationBoardComponent/Assignee';
import BurnDown from '../IterationBoardComponent/BurnDown';
import Sprint from '../IterationBoardComponent/Sprint';
import Status from '../IterationBoardComponent/Status';
import Remain from '../IterationBoardComponent/Remain';
import Priority from '../IterationBoardComponent/Priority';
import IssueType from '../IterationBoardComponent/IssueType';
import SprintDetails from '../IterationBoardComponent/SprintDetails';

import './IterationBoardHome.scss';

const { AppState } = stores;

@observer
class IterationBoardHome extends Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: true,
      sprintId: undefined,
      sprintName: undefined,
    };
  }

  componentDidMount() {
    this.loadSprint();
  }

  loadSprint() {
    const { match } = this.props;
    const sprintId = match.params.id;
    if (!sprintId) return;
    this.setState({ loading: true });
    loadSprint(sprintId)
      .then((res) => {
        this.setState({
          loading: false,
          sprintId: res.sprintId,
          sprintName: res.sprintName,
        });
      });
  }

  renderContent() {
    const { loading, sprintId, sprintName } = this.state;
    if (!loading && !sprintId) {
      return (
        <div>
          {'当前项目下无冲刺'}
        </div>
      );
    }
    return (
      <div>
        <Row gutter={20}>
          <Col span={8}>
            <Sprint
              sprintId={sprintId}
              sprintName={sprintName}
              // link="backlog"
            />
          </Col>
          <Col span={8}>
            <Status
              sprintId={sprintId}
              // link="reporthost/pieReport/statusCode"
            />
          </Col>
          <Col span={8}>
            <Remain
              sprintId={sprintId}
              // link="backlog"
            />
          </Col>
        </Row>
        <Row gutter={20}>
          <Col span={24}>
            <BurnDown
              sprintId={sprintId}
              link="reporthost/burndownchart"
            />
          </Col>
        </Row>
        <Row gutter={20}>
          <Col span={8}>
            <IssueType 
              sprintId={sprintId}
              link="reporthost/pieReport/typeCode"
            />
          </Col>

          <Col span={8}>
            <Priority
              sprintId={sprintId}
              link="reporthost/pieReport/priority"
            />
          </Col>
          <Col span={8}>
            <Assignee
              sprintId={sprintId}
              link="reporthost/pieReport/assignee"
            />
          </Col>
        </Row>
        <Row gutter={20}>
          <Col span={24}>
            <SprintDetails
              sprintId={sprintId}
              link="reporthost/sprintReport"
            />
          </Col>
        </Row>
      </div>
    );
  }

  render() {
    const { history } = this.props;
    return (
      <Page className="c7n-agile-iterationBoard">
        <Header title="活跃冲刺">
          <Button
            className="leftBtn2"
            funcType="flat"
            onClick={() => {
              history.push(`/agile/scrumboard?type=project&id=${AppState.currentMenuType.id}&name=${encodeURIComponent(AppState.currentMenuType.name)}&organizationId=${AppState.currentMenuType.organizationId}`);
            }}
          >
            <span>切换至看板</span>
          </Button>
        </Header>
        <Content>
          {this.renderContent()}
        </Content>
      </Page>
    );
  }
}

export default IterationBoardHome;
