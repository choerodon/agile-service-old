import React, { Component } from 'react';

function IssueTypeScreenSchemesEditHOC(WrappedComponent, type) {
  return class Cmp extends Component {
    render() {
      return <WrappedComponent {...this.props} type={type} />;
    }
  };
}

export default IssueTypeScreenSchemesEditHOC;
