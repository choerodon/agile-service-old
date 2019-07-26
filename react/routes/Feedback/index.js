import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const FeedbackTable = asyncRouter(() => (import('./FeedbackTable')));
const FeedbackContent = asyncRouter(() => (import('./FeedbackContent')));

const Feedback = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={FeedbackTable} />
    <Route path={`${match.url}/content`} component={FeedbackContent} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Feedback;
