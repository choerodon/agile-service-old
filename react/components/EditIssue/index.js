import React from 'react';
import EditIssue from './EditIssue';

// export default EditIssue;
import { EditIssueContextProvider } from './stores';

// const EditIssue = () => {
//   const { state, dispatch } = useContext(EditIssueContext);
//   const handleClick = () => {
//     dispatch({
//       type: 'increment',
//     });
//   };
//   const handleClickSync = () => {
//     dispatch(setSync(10));
//   };
//   return (
//     <div>
//       {state.test}
//       <button onClick={handleClick}>click</button>
//       <button onClick={handleClickSync}>clickSync</button>
//     </div>
//   );
// };
export default function Index(props) {
  return (
    <EditIssueContextProvider {...props}>
      <EditIssue />
    </EditIssueContextProvider>
  );
}
