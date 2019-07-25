import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { inject } from 'mobx-react';
import {
  asyncRouter, asyncLocaleProvider, stores, nomatch,
} from '@choerodon/boot';
import IsInProgramStore from '../IsInProgramStore';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';


const SETTINGSINDEX = asyncRouter(() => import('./settings'));

class PROJECTIndex extends React.Component {
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
    const IntlProviderAsync = asyncLocaleProvider(language, () => import(`../../locale/${language}`));
    return (
      <div className="agile">
        <IntlProviderAsync>
          <Switch>
            {/* 通知设置 */}
            {/* <Route path={`${match.url}/messageNotification`} component={MESSAGENOTIFICATION} /> */}
            <Route path={`${match.url}`} component={SETTINGSINDEX} />

            <Route path="*" component={nomatch} />
          </Switch>
        </IntlProviderAsync>
      </div>
    );
  }
}

export default PROJECTIndex;
