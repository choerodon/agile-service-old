import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch,
} from '@choerodon/boot';
import IsInProgramStore from '../stores/common/program/IsInProgramStore';
import RunWhenProjectChange from '../common/RunWhenProjectChange';
import './Agile.scss';


const RELEASEINDEX = asyncRouter(() => import('./project/Release'));
const BACKLOGINDEX = asyncRouter(() => import('./project/Backlog'));
const SCRUMBOARDINDEX = asyncRouter(() => import('./project/ScrumBoard'));
const ISSUEIndex = asyncRouter(() => import('./project/Issue'));
const COMPONENTIndex = asyncRouter(() => import('./project/Component'));
const PROJECTSETTINGINDEX = asyncRouter(() => import('./project/ProjectSetting'));
const FASTSEARCHINDEX = asyncRouter(() => import('./project/FastSearch'));
const REPORTHOSTINDEX = asyncRouter(() => import('./project/ReportHost'));
const ISSUELINKINDEX = asyncRouter(() => import('./project/IssueLink'));
const STORYMAPINDEX = asyncRouter(() => import('./project/StoryMap'));
const INERATIONBOARDINDEX = asyncRouter(() => import('./project/IterationBoard'));
const REPORTBOARD = asyncRouter(() => import('./project/ReportBoard'));
const MESSAGENOTIFICATION = asyncRouter(() => import('./project/MessageNotification'));
const FEEDBACK = asyncRouter(() => import('./project/Feedback'));

const WORKCALENDARINDEX = asyncRouter(() => import('./organization/WorkCalendar'));

const IssueTypeIndex = asyncRouter(() => import('./organization/issueType'));
const IssueTypeSchemeIndex = asyncRouter(() => import('./organization/issueTypeScheme'));
const StateMachineSchemeIndex = asyncRouter(() => import('./organization/stateMachineScheme'));
const PriorityIndex = asyncRouter(() => import('./organization/priority'));
const IssueTypeScreenSchemes = asyncRouter(() => import('./organization/issueTypeScreenSchemes'));
const StateIndex = asyncRouter(() => import('./organization/state'));
const StateMachineIndex = asyncRouter(() => import('./organization/stateMachine'));
const OBJECTSCHEMEINDEX = asyncRouter(() => import('./organization/ObjectScheme'));
const PAGEINDEX = asyncRouter(() => import('./organization/Page'));

class AGILEIndex extends React.Component {
  componentDidCatch(error, info) {
    // Choerodon.prompt(error.message);
  }

  componentDidMount() {
    // 切换项目查是否在项目群中
    RunWhenProjectChange(IsInProgramStore.refresh);
    IsInProgramStore.refresh();
  }

  render() {
    const { match } = this.props;
    const { AppState } = stores;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <div className="agile">
        <IntlProviderAsync>
          <Switch>
            {/* 发布版本 */}
            <Route path={`${match.url}/release`} component={RELEASEINDEX} />
            {/* 待办事项 */}
            <Route path={`${match.url}/backlog`} component={BACKLOGINDEX} />
            {/* 活跃冲刺 */}
            <Route path={`${match.url}/scrumboard`} component={SCRUMBOARDINDEX} />
            {/* 问题管理 */}
            <Route path={`${match.url}/issue`} component={ISSUEIndex} />
            {/* 模块管理 */}
            <Route path={`${match.url}/component`} component={COMPONENTIndex} />
            {/* 报告 */}
            <Route path={`${match.url}/reporthost`} component={REPORTHOSTINDEX} />
            {/* 项目设置 */}
            <Route path={`${match.url}/projectSetting`} component={PROJECTSETTINGINDEX} />
            {/* 快速搜索 */}
            <Route path={`${match.url}/fastSearch`} component={FASTSEARCHINDEX} />
            {/* 问题链接 */}
            <Route path={`${match.url}/issueLink`} component={ISSUELINKINDEX} />
            <Route path={`${match.url}/userMap`} component={STORYMAPINDEX} />
            {/* 迭代工作台 */}
            <Route path={`${match.url}/iterationBoard/:id`} component={INERATIONBOARDINDEX} />
            <Route path={`${match.url}/reportBoard`} component={REPORTBOARD} />
            <Route path={`${match.url}/messageNotification`} component={MESSAGENOTIFICATION} />
            {/* 反馈中心 */}
            <Route path={`${match.url}/feedback`} component={FEEDBACK} />

            <Route path={`${match.url}/workCalendar`} component={WORKCALENDARINDEX} />

            <Route
              path={`${match.url}/state-machine-schemes`}
              component={StateMachineSchemeIndex}
            />
            <Route
              path={`${match.url}/issue-type`}
              component={IssueTypeIndex}
            />
            <Route
              path={`${match.url}/issue-type-schemes`}
              component={IssueTypeSchemeIndex}
            />
            <Route
              path={`${match.url}/priorities`}
              component={PriorityIndex}
            />
            <Route
              path={`${match.url}/issue-type-screen-schemes`}
              component={IssueTypeScreenSchemes}
            />
            <Route
              path={`${match.url}/states`}
              component={StateIndex}
            />
            <Route
              path={`${match.url}/state-machines`}
              component={StateMachineIndex}
            />
            <Route
              path={`${match.url}/objectScheme`}
              component={OBJECTSCHEMEINDEX}
            />
            <Route
              path={`${match.url}/page`}
              component={PAGEINDEX}
            />

            <Route path="*" component={nomatch} />
          </Switch>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default AGILEIndex;
