import React from 'react';
import {
  Route,
  Switch,
} from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const StateMachineList = asyncRouter(() => import('./stateMachineList'), () => import('../../../stores/organization/stateMachine'));
const EditStateMachine = asyncRouter(() => import('./editStateMachine'), () => import('../../../stores/organization/stateMachine'));
const EditConfig = asyncRouter(() => import('./editConfig'), () => import('../../../stores/organization/stateMachine'));
const EditConfigSelect = asyncRouter(() => import('./editConfigSelect'), () => import('../../../stores/organization/stateMachine'));

const StateMachineIndex = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={StateMachineList} />
    <Route exact path={`${match.url}/edit/:id/:status`} component={EditStateMachine} />
    <Route exact path={`${match.url}/:machineId/editconfig/:id`} component={EditConfig} />
    <Route exact path={`${match.url}/:machineId/editconfig/:id/state/:stateId`} component={EditConfig} />
    <Route exact path={`${match.url}/:machineId/editconfig/select/:type/:id`} component={EditConfigSelect} />
    <Route path={'*'} component={nomatch} />
  </Switch>
);

export default StateMachineIndex;
