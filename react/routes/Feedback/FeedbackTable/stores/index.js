import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import FeedbackTableDataSet from './FeedbackTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { id } }, children } = props;
    const feedbackTableDataSet = useMemo(() => new DataSet(FeedbackTableDataSet({ projectId: id })), []);
    const recommendationDs = useMemo(() => new DataSet(FeedbackTableDataSet({ projectId: id })), []);
    const questionDs = useMemo(() => new DataSet(FeedbackTableDataSet({ projectId: id })), []);
    const bugReportDs = useMemo(() => new DataSet(FeedbackTableDataSet({ projectId: id })), []);

    const value = {
      ...props,
      prefixCls: 'c7n-agile-feedback-table',
      feedbackTableDataSet,
      recommendationDs,
      questionDs,
      bugReportDs,
      AppState: props.AppState,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
