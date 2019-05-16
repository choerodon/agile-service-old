import React, { Component } from 'react';
import { Icon } from 'choerodon-ui';
import './TypeTag.scss';

const initTypes = ['agile_epic', 'agile_story', 'agile_fault', 'agile_task', 'agile_subtask', 'agile_feature', 'agile_enabler'];

class TypeTag extends Component {
  render() {
    const {
      data, showName, style,
    } = this.props;
    return (
      <div className="c7n-typeTag" style={style}>
        <Icon
          style={{
            fontSize: '26px',
            color: data ? data.colour : '#fab614',
          }}
          type={data ? data.icon : 'help'}
        />
        {
          showName && (
            <span className="name">{data ? data.name : ''}</span>
          )
        }
      </div>
    );
  }
}
export default TypeTag;
