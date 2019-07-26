import React from 'react';
import { StoreProvider } from '../stores';
import PageHome from './PageHome';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <PageHome />
    </StoreProvider>
  );
}
