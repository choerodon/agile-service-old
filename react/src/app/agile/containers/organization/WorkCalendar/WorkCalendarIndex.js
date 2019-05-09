import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const WorkCalendarHome = asyncRouter(() => import('./WorkCalendarHome'), () => import('../../../stores/organization/workCalendar/WorkCalendarStore'));

const WorkCalendarIndex = ({ match }) => (
  <Switch>
    <Route exact path={`${match.url}`} component={WorkCalendarHome} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default WorkCalendarIndex;
