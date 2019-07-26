import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch,
} from '@choerodon/boot';
import IsInProgramStore from './IsInProgramStore';
import RunWhenProjectChange from '../../common/RunWhenProjectChange';

const SettingsIndex = asyncRouter(() => import('./settings'));

class ProjectIndex extends React.Component {
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

    return (
      <div>
        <Switch>
          <Route path={`${match.url}`} component={SettingsIndex} />

          <Route path="*" component={nomatch} />
        </Switch>
      </div>
    );
  }
}

export default ProjectIndex;
