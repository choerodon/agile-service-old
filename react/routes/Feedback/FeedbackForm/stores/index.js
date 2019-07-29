import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import FeedbackFormDataSet from './FeedbackFormDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  // const { AppState: { currentMenuType: { id } }, children } = props;
  // 使用缓存钩子，以便将来做路由缓存
  const feedbackFormDataSet = useMemo(() => new DataSet(FeedbackFormDataSet()));
  const value = {
    ...props,
    prefixCls: 'c7n-agile-feedback-feedbackForm',
    feedbackFormDataSet,
  };
  return (
    <Store.Provider value={value}>
      {props.children}
    </Store.Provider>
  );
};
