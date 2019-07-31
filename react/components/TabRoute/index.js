import React, { Fragment, createContext, useContext } from 'react';
import {
  withRouter, Route, Redirect, Switch, 
} from 'react-router-dom';
import queryString from 'query-string';
import { Tabs } from 'choerodon-ui';
import { Content, nomatch } from '@choerodon/boot';

const { TabPane } = Tabs;
const RouteContext = createContext();
const TabRoute = withRouter(({
  routes, history, location, match,
}) => {
  const parsed = `?${queryString.extract(location.search)}`;
  const callback = (key) => {
    history.push(`${key}${parsed}`);
  };

  const currentPath = location.pathname;
  const renderPanes = () => routes.map(({ title, path }) => <TabPane tab={title} key={path} />);
  const TabComponent = (
    <Tabs activeKey={currentPath} onChange={callback}>
      {renderPanes()}
    </Tabs>
  );
  const renderRoutes = () => routes.map(route => <Route {...route} />);
  return (
    <RouteContext.Provider value={{ TabComponent }}>
      <div className="c7n-Header-Area" style={{ height: 58 }} />
      <Switch>
        {renderRoutes()}
        <Redirect from={`${match.url}`} to={`${routes[0].path}${parsed}`} />
        <Route path="*" component={nomatch} />
      </Switch>
    </RouteContext.Provider>
  );
});

export const ContentWithTab = (props) => {
  const { TabComponent } = useContext(RouteContext);
  return (
    <Fragment>
      {TabComponent}
      <Content {...props} />
    </Fragment>
  );
};

TabRoute.propTypes = {

};
export default TabRoute;
