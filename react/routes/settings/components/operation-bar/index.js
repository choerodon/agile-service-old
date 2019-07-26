import React from 'react';
import { Button, Icon } from 'choerodon-ui/pro';
import './index.less';

function OperationBarIndex(props) {
  const { type, name } = props.data;
  return (
    <div className="operation-bar-box">
      <Button funcType="flat" color="blue" icon={type}>{name}</Button>
    </div>
  );
}

export default OperationBarIndex;
