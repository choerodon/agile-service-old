import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from 'choerodon-front-boot';

const EDITFIELDCONFIGURATION = asyncRouter(() => (import('../MessageNotificationComponent/EditFieldConfiguration')));
const EDITNOTIFICATIONTYPE = asyncRouter(() => (import('../MessageNotificationComponent/EditNotificationType')));


const MessageNotificationHome = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={EDITFIELDCONFIGURATION} />
    <Route path={`${match.url}/editNotificationType`} component={EDITNOTIFICATIONTYPE} />
    <Route path="*" component={nomatch} />
  </Switch>
);
export default MessageNotificationHome;
