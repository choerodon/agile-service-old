import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch,
} from '@choerodon/boot';
import IsInProgramStore from './stores/common/program/IsInProgramStore';
import RunWhenProjectChange from './common/RunWhenProjectChange';
import './style/index.less';

const RELEASEINDEX = asyncRouter(() => import('./routes/Release'));
// const BACKLOGINDEX = asyncRouter(() => import('./routes/Backlog'));
const SCRUMBOARDINDEX = asyncRouter(() => import('./routes/ScrumBoard'));
// const ISSUEIndex = asyncRouter(() => import('./routes/Issue'));
const COMPONENTIndex = asyncRouter(() => import('./routes/Component'));
const PROJECTSETTINGINDEX = asyncRouter(() => import('./routes/ProjectSetting'));
const FASTSEARCHINDEX = asyncRouter(() => import('./routes/FastSearch'));
const REPORTHOSTINDEX = asyncRouter(() => import('./routes/ReportHost'));
const ISSUELINKINDEX = asyncRouter(() => import('./routes/IssueLink'));
const STORYMAPINDEX = asyncRouter(() => import('./routes/StoryMap'));
const INERATIONBOARDINDEX = asyncRouter(() => import('./routes/IterationBoard'));
const REPORTBOARD = asyncRouter(() => import('./routes/ReportBoard'));
const MESSAGENOTIFICATION = asyncRouter(() => import('./routes/MessageNotification'));
const FEEDBACK = asyncRouter(() => import('./routes/Feedback'));
const WORKCALENDARINDEX = asyncRouter(() => import('./routes/WorkCalendar'));
const WorkList = asyncRouter(() => import('./routes/WorkList'));
const IssueTypeIndex = asyncRouter(() => import('./routes/issueType'));
const IssueTypeSchemeIndex = asyncRouter(() => import('./routes/issueTypeScheme'));
const StateMachineSchemeIndex = asyncRouter(() => import('./routes/stateMachineScheme'));
const PriorityIndex = asyncRouter(() => import('./routes/priority'));
const IssueTypeScreenSchemes = asyncRouter(() => import('./routes/issueTypeScreenSchemes'));
const StateIndex = asyncRouter(() => import('./routes/state'));
const StateMachineIndex = asyncRouter(() => import('./routes/stateMachine'));
const OBJECTSCHEMEINDEX = asyncRouter(() => import('./routes/object-scheme'));
const PAGEINDEX = asyncRouter(() => import('./routes/Page'));

// 敏捷设置子路由
const settings = asyncRouter(() => import('./routes/settings'));

class Index extends React.Component {
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
    const language = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(language, () => import(`.//locale/${language}`));
    return (
      <div className="agile">
        <IntlProviderAsync>
          <Switch>
            {/* 发布版本 */}
            <Route path={`${match.url}/release`} component={RELEASEINDEX} />
            <Route path={`${match.url}/work-list`} component={WorkList} />
            {/* 待办事项 */}
            {/* <Route path={`${match.url}/backlog`} component={BACKLOGINDEX} /> */}
            {/* 活跃冲刺 */}
            <Route path={`${match.url}/scrumboard`} component={SCRUMBOARDINDEX} />
            {/* 问题管理 */}
            {/* <Route path={`${match.url}/issue`} component={ISSUEIndex} /> */}
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

            <Route path={`${match.url}/state-machine-schemes`} component={StateMachineSchemeIndex} />
            <Route path={`${match.url}/issue-type`} component={IssueTypeIndex} />
            <Route path={`${match.url}/issue-type-schemes`} component={IssueTypeSchemeIndex} />
            <Route path={`${match.url}/priorities`} component={PriorityIndex} />
            <Route path={`${match.url}/issue-type-screen-schemes`} component={IssueTypeScreenSchemes} />
            <Route path={`${match.url}/states`} component={StateIndex} />
            <Route path={`${match.url}/state-machines`} component={StateMachineIndex} />
            <Route path={`${match.url}/objectScheme`} component={OBJECTSCHEMEINDEX} />
            <Route path={`${match.url}/page`} component={PAGEINDEX} />
            <Route path={`${match.url}/settings`} component={settings} />
            <Route path="*" component={nomatch} />
          </Switch>

        </IntlProviderAsync>
      </div>
    );
  }
}

export default Index;
