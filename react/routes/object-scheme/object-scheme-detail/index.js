import React from 'react';
import { StoreProvider } from '../stores';
import ObjectSchemeDetail from './ObjectSchemeDetail';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <ObjectSchemeDetail />
    </StoreProvider>
  );
}
