import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import EditIssueStore from './EditIssueStore';

const EditIssueContext = createContext();

export default EditIssueContext;
export const EditIssueContextProvider = injectIntl(inject('AppState')((props) => {
  const value = {
    ...props,
    prefixCls: 'c7n-agile-EditIssue',
    intlPrefix: 'agile.EditIssue',
    store: useMemo(() => new EditIssueStore(), []), // 防止update时创建多次store
  };

  return (
    <EditIssueContext.Provider value={value}>
      {props.children}
    </EditIssueContext.Provider>
  );
}));
