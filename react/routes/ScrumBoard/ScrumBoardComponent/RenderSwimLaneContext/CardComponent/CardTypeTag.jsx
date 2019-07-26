import React, { Component } from 'react';
import { Tooltip, Icon, Rate } from 'choerodon-ui';
import TypeTag from '../../../../../components/TypeTag/TypeTag';

/**
 * 任务编号呈现
 * @returns React 函数式组件
 * @param issueTypeVO
 */

export default class CardTypeTag extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { issueTypeVO } = this.props;
    return nextProps.issueTypeVO.name !== issueTypeVO.name;
  }

  render() {
    const { issueTypeVO } = this.props;
    return (
      <Tooltip title={issueTypeVO ? issueTypeVO.name : ''}>
        <TypeTag
          data={issueTypeVO}
        />
      </Tooltip>
    );
  }
}
