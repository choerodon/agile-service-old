import React from 'react';
import { StoreProvider } from './stores';
import FeedbackTable from './FeedbackTable';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <FeedbackTable />
    </StoreProvider>
  );
}
