import React from 'react';
import { StoreProvider } from '../stores';
import ObjectSchemeHome from './ObjectSchemeHome';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <ObjectSchemeHome />
    </StoreProvider>
  );
}
