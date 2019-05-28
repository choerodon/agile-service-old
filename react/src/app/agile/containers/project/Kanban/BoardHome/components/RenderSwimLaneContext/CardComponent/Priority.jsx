import React, { Component } from 'react';
import { Tooltip } from 'choerodon-ui';

export default class Priority extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { priorityDTO } = this.props;
    return nextProps.priorityDTO.name !== priorityDTO.name || nextProps.priorityDTO.colour !== priorityDTO.colour;
  }

  render() {
    const { priorityDTO } = this.props;
    return (
      <Tooltip>
        <p
          style={{
            background: `${priorityDTO ? priorityDTO.colour : '#FFFFFF'}1F`,
            color: priorityDTO ? priorityDTO.colour : '#FFFFFF',
            textAlign: 'center',
            marginLeft: '8px',
            minWidth: 16,
            maxWidth: 46,
            paddingLeft: 2,
            paddingRight: 2,
            height: 20,
            borderRadius: 2,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          {priorityDTO && priorityDTO.name}
        </p>
      </Tooltip>
    );
  }
}
