import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import {
  asyncRouter, nomatch, asyncLocaleProvider, stores, 
} from '@choerodon/boot';

const FeedbackTable = asyncRouter(() => (import('./FeedbackTable')));
const FeedbackContent = asyncRouter(() => (import('./FeedbackContent/FeedbackContent')));

const Feedback = ({ match }) => {
  const { AppState } = stores;
  const langauge = AppState.currentLanguage;
  const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../../locale/${langauge}`));
  return (
    <div>
      <IntlProviderAsync>
        <Switch>
          <Route exact path={`${match.url}`} component={FeedbackTable} />
          <Route path={`${match.url}/content`} component={FeedbackContent} />
          <Route path="*" component={nomatch} />
        </Switch>
      </IntlProviderAsync>
    </div>
  
  );
};

export default Feedback;
