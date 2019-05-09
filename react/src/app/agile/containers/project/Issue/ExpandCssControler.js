import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import IssueStore from '../../../stores/project/sprint/IssueStore';

@observer
class ExpandCssControler extends Component {
  // 当 Table 为 expand 时，添加 ClassName，从而实现不渲染 Table 改变 css 样式的效果
  render() {
    const expand = IssueStore.getExpand;
    const expandClassName = expand ? 'c7n-Issue-expand' : '';
    return (
      <div className={expandClassName} />
    );
  }
}

export default ExpandCssControler;
