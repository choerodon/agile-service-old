import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch,
} from '@choerodon/boot';
import IsInProgramStore from './src/app/agile/stores/common/program/IsInProgramStore';
import RunWhenProjectChange from './common/RunWhenProjectChange';
import './style/index.less';
import PROJECTIndex from './routes/project/settings';


const RELEASEINDEX = asyncRouter(() => import('./src/app/agile/containers/project/Release'));
const BACKLOGINDEX = asyncRouter(() => import('./src/app/agile/containers/project/Backlog'));
const SCRUMBOARDINDEX = asyncRouter(() => import('./src/app/agile/containers/project/ScrumBoard'));
const ISSUEIndex = asyncRouter(() => import('./src/app/agile/containers/project/Issue'));
const PROJECTSETTINGINDEX = asyncRouter(() => import('./src/app/agile/containers/project/ProjectSetting'));
const REPORTHOSTINDEX = asyncRouter(() => import('./src/app/agile/containers/project/ReportHost'));
const STORYMAPINDEX = asyncRouter(() => import('./src/app/agile/containers/project/StoryMap'));
const INERATIONBOARDINDEX = asyncRouter(() => import('./src/app/agile/containers/project/IterationBoard'));
const REPORTBOARD = asyncRouter(() => import('./src/app/agile/containers/project/ReportBoard'));
// 敏捷设置子路由
const PROJECTINDEX = asyncRouter(() => import('./routes/project/settings'));

const WORKCALENDARINDEX = asyncRouter(() => import('./src/app/agile/containers/organization/WorkCalendar'));

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
    const IntlProviderAsync = asyncLocaleProvider(language, () => import(`./src/app/agile/locale/${language}`));
    return (
      <div className="agile">
        <IntlProviderAsync>
          <Switch>
            {/* 发布版本 */}
            <Route path={`${match.url}/release`} component={RELEASEINDEX} />
            {/* 待办事项 */}
            {/* <Route path={`${match.url}/backlog`} component={BACKLOGINDEX} /> */}
            {/* 在这暂时改成设置项合并后的测试链接 */}
            <Route path={`${match.url}/backlog`} component={PROJECTINDEX} />
            {/* 活跃冲刺 */}
            <Route path={`${match.url}/scrumboard`} component={SCRUMBOARDINDEX} />
            {/* 问题管理 */}
            <Route path={`${match.url}/issue`} component={ISSUEIndex} />
            {/* 报告 */}
            <Route path={`${match.url}/reporthost`} component={REPORTHOSTINDEX} />
            {/* 项目设置 */}
            <Route path={`${match.url}/projectSetting`} component={PROJECTSETTINGINDEX} />
            <Route path={`${match.url}/userMap`} component={STORYMAPINDEX} />
            {/* 迭代工作台 */}
            <Route path={`${match.url}/iterationBoard/:id`} component={INERATIONBOARDINDEX} />
            <Route path={`${match.url}/reportBoard`} component={REPORTBOARD} />

            <Route path={`${match.url}/workCalendar`} component={WORKCALENDARINDEX} />

            {/* 敏捷设置的路由 */}
            {/* <Route path={`${match.url}/project`} component={PROJECTIndex} /> */}

            <Route path="*" component={nomatch} />
          </Switch>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default Index;
