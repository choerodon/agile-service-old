import React, { Component } from 'react';
import { loadIssueTypes, getDefaultPriority } from '../../api/NewIssueApi';

class QuickCreateFeatureProvider extends Component {
  state={
    issueTypes: [],
    defaultPriority: null,
  }

  componentDidMount() {
    this.loadData();
  }

  loadData=() => {
    Promise.all([
      loadIssueTypes('program'),
      getDefaultPriority(),
    ]).then(([issueTypes, defaultPriority]) => {
      this.setState({
        issueTypes,
        defaultPriority,
      });
    });
  }

  render() {
    const { children, ...otherProps } = this.props;
    const { issueTypes, defaultPriority } = this.state;  
    const featureTypeDTO = issueTypes.find(type => type.typeCode === 'feature');
    return (children({
      featureTypeDTO,
      defaultPriority,
      ...otherProps,
    }));    
  }
}

export default QuickCreateFeatureProvider;
