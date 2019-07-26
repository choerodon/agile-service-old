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

const ReleaseIndex = asyncRouter(() => import('./src/app/agile/containers/project/Release'));
const BacklogIndex = asyncRouter(() => import('./src/app/agile/containers/project/Backlog'));
const ScrumBoardIndex = asyncRouter(() => import('./src/app/agile/containers/project/ScrumBoard'));
const IssueIndex = asyncRouter(() => import('./src/app/agile/containers/project/Issue'));
const ProjectSettingIndex = asyncRouter(() => import('./src/app/agile/containers/project/ProjectSetting'));
const ReportHostIndex = asyncRouter(() => import('./src/app/agile/containers/project/ReportHost'));
const StoryMapIndex = asyncRouter(() => import('./src/app/agile/containers/project/StoryMap'));
const IterationBoardIndex = asyncRouter(() => import('./src/app/agile/containers/project/IterationBoard'));
const ReportBoardIndex = asyncRouter(() => import('./src/app/agile/containers/project/ReportBoard'));
// 敏捷设置子路由
const ProjectIndex = asyncRouter(() => import('./routes/project'));

const WorkCalendarIndex = asyncRouter(() => import('./src/app/agile/containers/organization/WorkCalendar'));

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
            <Route path={`${match.url}/release`} component={ReleaseIndex} />

            {/* 待办事项 */}
            {/* <Route path={`${match.url}/backlog`} component={BacklogIndex} /> */}
            {/* 在这暂时改成设置项合并后的测试链接 */}
            <Route path={`${match.url}/backlog`} component={ProjectIndex} />

            {/* 活跃冲刺 */}
            <Route path={`${match.url}/scrumboard`} component={ScrumBoardIndex} />
            {/* 问题管理 */}
            <Route path={`${match.url}/issue`} component={IssueIndex} />
            {/* 报告 */}
            <Route path={`${match.url}/reporthost`} component={ReportHostIndex} />
            {/* 项目设置 */}
            <Route path={`${match.url}/projectSetting`} component={ProjectSettingIndex} />
            <Route path={`${match.url}/userMap`} component={StoryMapIndex} />
            {/* 迭代工作台 */}
            <Route path={`${match.url}/iterationBoard/:id`} component={IterationBoardIndex} />
            <Route path={`${match.url}/reportBoard`} component={ReportBoardIndex} />

            <Route path={`${match.url}/workCalendar`} component={WorkCalendarIndex} />

            <Route path="*" component={nomatch} />
          </Switch>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default Index;
