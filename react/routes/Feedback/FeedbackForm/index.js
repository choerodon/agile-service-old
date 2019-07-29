import React from 'react';
import { StoreProvider } from './stores';
import FeedbackForm from './FeedbackForm';

export default function Index(props) {
  return (
    <StoreProvider {...props}>
      <FeedbackForm />
    </StoreProvider>
  );
}
