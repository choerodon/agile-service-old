import React from 'react';
import ComponentHome from './ComponentHome';
import { StoreProvider } from './stores';

export default props => (
  <StoreProvider {...props}>
    <ComponentHome />
  </StoreProvider>
);
