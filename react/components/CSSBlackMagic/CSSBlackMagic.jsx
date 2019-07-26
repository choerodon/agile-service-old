// CSS 黑魔法，请勿滥用
import React, { Component } from 'react';

export default WrappedComponent => class CSSBlackMagic extends Component {
  constructor(props) {
    super(props);
    this.id = Choerodon.randomString();
  }

  changeStyle = (style) => {
    if (document.getElementById(this.id)) {
      document.getElementById(this.id).innerHTML = style;
    } else {
      const el = document.createElement('style');
      el.type = 'text/css';
      el.id = this.id;
      // Add it to the head of the document
      const head = document.querySelector('head');
      head.appendChild(el);
      // At some future point we can totally redefine the entire content of the style element
      el.innerHTML = style;
    }
  };

  unMountStyle = () => {
    document.getElementById(this.id).innerHTML = '';
  };

  render() {
    return (
      <WrappedComponent {...this.props} headerStyle={{ changeStyle: this.changeStyle, unMountStyle: this.unMountStyle }} />
    );
  }
};
