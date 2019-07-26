import React, { Fragment } from 'react';
import { withRouter, Route } from 'react-router-dom';
import queryString from 'query-string';
import { Tabs } from 'choerodon-ui';
import { find } from 'lodash';
import { nomatch } from '@choerodon/boot';

const { TabPane } = Tabs;
const TabRoute = withRouter(({
  routes, history, location,
}) => {
  const callback = (key) => {
    const parsed = queryString.extract(location.search);
    history.push(`${key}?${parsed}`);
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
    <Fragment>
      {isMatch ? (
        <Fragment>
          <div className="c7n-Header-Area" style={{ height: 58 }} />
          {/* <Tabs activeKey={currentPath} onChange={callback}>
            {renderPanes()}
          </Tabs> */}
          {renderRoutes()}
        </Fragment>
      ) : <Route path="*" component={nomatch} /> }      
    </Fragment>
  );
});

TabRoute.propTypes = {

};
export default TabRoute;
