import React, { memo } from 'react';
import { Icon } from 'choerodon-ui';
import './TypeTag.scss';

const TypeTag = ({
  data, showName, style, featureType,
}) => {
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
};
export default memo(TypeTag);
