import React from 'react';
import PageDetail from './PageDetail';
import { StoreProvider } from '../stores';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <PageDetail />
    </StoreProvider>
  );
}

export default Index;
