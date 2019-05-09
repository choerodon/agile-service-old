import React, { Component } from 'react';
import { Tooltip, Icon, Rate } from 'choerodon-ui';
import TypeTag from '../../../../../../components/TypeTag/TypeTag';

/**
 * 任务编号呈现
 * @returns React 函数式组件
 * @param issueTypeDTO
 */

export default class CardTypeTag extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { issueTypeDTO } = this.props;
    return nextProps.issueTypeDTO.name !== issueTypeDTO.name;
  }

  render() {
    const { issueTypeDTO } = this.props;
    return (
      <Tooltip title={issueTypeDTO ? issueTypeDTO.name : ''}>
        <TypeTag
          data={issueTypeDTO}
        />
      </Tooltip>
    );
  }
}
