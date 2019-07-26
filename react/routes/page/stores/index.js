import React, { createContext, useMemo } from 'react';
// import { store, stores } from '@choerodon/boot';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import PageStore from './PageStore';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    // const { AppState } = props;
    // const { children } = props;
    const pageStore = useMemo(() => new PageStore(), []);
    const value = {
      ...props,
      prefixCls: 'issue-page',
      intlPrefix: 'issue-page',
      pageStore,
    };
    return (
      <Store.Provider value={value}>
        {props.children}
      </Store.Provider>
    );
  }
));
