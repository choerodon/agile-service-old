import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import FeedbackTableDataSet from './FeedbackTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    console.log(props);
    const { AppState: { currentMenuType: { id } }, children } = props;
    // 使用缓存钩子，以便将来做路由缓存
    const feedbackTableDataSet = useMemo(() => new DataSet(FeedbackTableDataSet({ projectId: id, AppState: props.AppState })), []);
    const value = {
      ...props,
      prefixCls: 'c7n-agile-feedback-table',
      feedbackTableDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
