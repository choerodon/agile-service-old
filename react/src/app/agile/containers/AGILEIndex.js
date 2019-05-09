import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch, 
} from 'choerodon-front-boot';
import './Agile.scss';

const Home = asyncRouter(() => import('./Home'));
const RELEASEINDEX = asyncRouter(() => import('./project/Release'));
const BACKLOGINDEX = asyncRouter(() => import('./project/Backlog'));
const SCRUMBOARDINDEX = asyncRouter(() => import('./project/ScrumBoard'));
const ISSUEIndex = asyncRouter(() => import('./project/Issue'));
const COMPONENTIndex = asyncRouter(() => import('./project/Component'));
const PROJECTSETTINGINDEX = asyncRouter(() => import('./project/ProjectSetting'));
const FASTSEARCHINDEX = asyncRouter(() => import('./project/FastSearch'));
const REPORTHOSTINDEX = asyncRouter(() => import('./project/ReportHost'));
const ISSUELINKINDEX = asyncRouter(() => import('./project/IssueLink'));
const USERMAPINDEX = asyncRouter(() => import('./project/userMap'));
const INERATIONBOARDINDEX = asyncRouter(() => import('./project/IterationBoard'));
const REPORTBOARD = asyncRouter(() => import('./project/ReportBoard'));
const MESSAGENOTIFICATION = asyncRouter(() => import('./project/MessageNotification'));

const WORKCALENDARINDEX = asyncRouter(() => import('./organization/WorkCalendar'));
const OBJECTSCHEMEINDEX = asyncRouter(() => import('./organization/ObjectScheme'));
const PAGEINDEX = asyncRouter(() => import('./organization/Page'));

const ART = asyncRouter(() => import('./program/Art')); 
const KANBAN = asyncRouter(() => import('./program/Kanban')); 
const FEATURE = asyncRouter(() => import('./program/Feature'));
const PIAIMS = asyncRouter(() => import('./program/PI'));
const PROGRAMSETTING = asyncRouter(() => import('./program/ProgramSetting'));
const ARTCALENDAR = asyncRouter(() => import('./program/Art/ArtCalendar'));
const ROADMAP = asyncRouter(() => import('./program/RoadMap'));

class AGILEIndex extends React.Component {
  render() {
    const { match } = this.props;
    const { AppState } = stores;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <Switch>
          <Route exact path={match.url} component={Home} />
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
          <Route path={`${match.url}/userMap`} component={USERMAPINDEX} />
          {/* 迭代工作台 */}
          <Route path={`${match.url}/iterationBoard/:id`} component={INERATIONBOARDINDEX} />
          <Route path={`${match.url}/reportBoard`} component={REPORTBOARD} />
          <Route path={`${match.url}/messageNotification`} component={MESSAGENOTIFICATION} />

          <Route path={`${match.url}/workCalendar`} component={WORKCALENDARINDEX} />
          <Route path={`${match.url}/objectScheme`} component={OBJECTSCHEMEINDEX} />
          <Route path={`${match.url}/page`} component={PAGEINDEX} />

          <Route path={`${match.url}/art`} component={ART} />
          <Route path={`${match.url}/kanban`} component={KANBAN} />          
          <Route path={`${match.url}/feature`} component={FEATURE} /> 
          <Route path={`${match.url}/pi`} component={PIAIMS} />
          <Route path={`${match.url}/programSetting`} component={PROGRAMSETTING} /> 
          <Route path={`${match.url}/artCalendar`} component={ARTCALENDAR} /> 
          <Route path={`${match.url}/roadMap`} component={ROADMAP} /> 

          <Route path="*" component={nomatch} />
        </Switch>
      </IntlProviderAsync>
    );
  }
}

export default AGILEIndex;
