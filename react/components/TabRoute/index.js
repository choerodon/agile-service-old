import React, { Fragment, createContext, useContext } from 'react';
import { withRouter, Route, Redirect } from 'react-router-dom';
import queryString from 'query-string';
import { Tabs } from 'choerodon-ui';
import { find } from 'lodash';
import { Content } from '@choerodon/boot';

const { TabPane } = Tabs;
const RouteContext = createContext();
const TabRoute = withRouter(({
  routes, history, location,
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
  const renderRoutes = () => routes.map((route) => {
    const { component: Component, path } = route;   
    return <Route path={path} render={props => <Component {...props} TabComponent={TabComponent} />} />;
  });
  
  const isMatch = find(routes, { path: currentPath });
  return (
    <RouteContext.Provider value={{ TabComponent }}>
      {isMatch ? (
        <Fragment>
          <div className="c7n-Header-Area" style={{ height: 58 }} />
          {/* <Tabs activeKey={currentPath} onChange={callback}>
            {renderPanes()}
          </Tabs> */}
          {renderRoutes()}
        </Fragment>
      ) : <Redirect from="*" to={`${routes[0].path}${parsed}`} /> }      
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
