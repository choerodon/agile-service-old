import React, { Component } from 'react';
import { Icon } from 'choerodon-ui';
import './TypeTag.scss';

const initTypes = ['agile_epic', 'agile_story', 'agile_fault', 'agile_task', 'agile_subtask', 'agile_feature', 'agile_enabler'];

class TypeTag extends Component {
  render() {
    const {
      data, showName, style, featureType,
    } = this.props;
    let { colour, name = '' } = data || {};
    if (featureType === 'business') {
      colour = '#29B6F6';
      name = '特性';
    } else if (featureType === 'enabler') {
      colour = '#FFCA28';
      name = '使能';
    }
    return (
      <div className="c7n-typeTag" style={style}>
        <Icon
          style={{
            fontSize: '26px',
            color: colour || '#fab614',
          }}
          type={data ? data.icon : 'help'}
        />
        {
          showName && (
            <span className="name">{name}</span>
          )
        }
      </div>
    );
  }
}
export default TypeTag;
