import React, { useReducer, createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import EditIssueStore from './EditIssueStore';

const EditIssueContext = createContext();

export default EditIssueContext;
// const augmentDispatch = (dispatch, state) => input => (input instanceof Function ? input(dispatch, state) : dispatch(input));
// export const setSync = value => (dispatch, state) => {
//   setTimeout(() => {
//     dispatch({
//       type: 'set',
//       payload: value,
//     });
//   }, 5000);
// };

// function reducer(state, action) {
//   switch (action.type) {
//     case 'increment':
//       return { test: state.test + 1 };  
//     case 'set':
//       return { test: action.payload }; 
//     default:
//       throw new Error();
//   }
// }

// export const EditIssueContextProvider = (props) => {
//   const initialState = {
//     test: 1,
//   };
//   const [state, dispatch] = useReducer(reducer, initialState);
//   return (
//     <EditIssueContext.Provider value={{ state, dispatch: augmentDispatch(dispatch, state) }}>
//       {props.children}
//     </EditIssueContext.Provider>
//   );
// };
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
