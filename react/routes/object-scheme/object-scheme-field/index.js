import React from 'react';
import { StoreProvider } from '../stores';
import ObjectSchemeField from './ObjectSchemeField';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <ObjectSchemeField />
    </StoreProvider>
  );
}
