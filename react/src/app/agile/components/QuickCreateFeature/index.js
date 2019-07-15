import React from 'react';
import QuickCreateFeature from './QuickCreateFeature';
import QuickCreateFeatureProvider from './QuickCreateFeatureProvider';

const QuickCreateFeatureWithProvider = props => (
  <QuickCreateFeatureProvider>
    {({
      featureTypeVO, 
      defaultPriority,      
      ...otherProps
    }) => (
      <QuickCreateFeature
        featureTypeVO={featureTypeVO}  
        defaultPriority={defaultPriority}
        {...otherProps}
        {...props}
      />
    )}
  </QuickCreateFeatureProvider>
);
export {
  QuickCreateFeature,
  QuickCreateFeatureProvider,
  QuickCreateFeatureWithProvider,
};
