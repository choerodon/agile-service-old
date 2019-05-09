import React from 'react';
import QuickCreateFeature from './QuickCreateFeature';
import QuickCreateFeatureProvider from './QuickCreateFeatureProvider';

const QuickCreateFeatureWithProvider = props => (
  <QuickCreateFeatureProvider>
    {({
      featureTypeDTO, 
      defaultPriority,      
      ...otherProps
    }) => (
      <QuickCreateFeature
        featureTypeDTO={featureTypeDTO}  
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
