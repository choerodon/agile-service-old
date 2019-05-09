import React from 'react';
import PropTypes from 'prop-types';
import { Modal, Icon } from 'choerodon-ui';

const propTypes = {
  visible: PropTypes.bool.isRequired,
  // eslint-disable-next-line react/no-unused-prop-types
  onOk: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};

let canStop = true;
// eslint-disable-next-line consistent-return
const renderStopArtModalContent = (props) => {
  const { stopArtPIInfo, data } = props;
  // canStop = !(stopArtPIInfo && stopArtPIInfo.activePiDTO && Object.keys(stopArtPIInfo.activePiDTO).length > 0);
  canStop = true;
  if (!canStop) {
    return (
      <p>
        {'你无法停止 '}
        <span style={{ fontWeight: 600 }}>{data.name}</span>
        {' ，你需要手动关闭活跃的PI '}
        <span style={{ color: 'red', fontWeight: 600 }}>{`${stopArtPIInfo.activePiDTO.code}-${stopArtPIInfo.activePiDTO.name} `}</span>
        {'之后，才能停止火车。'}
      </p>
    );
  } else if (stopArtPIInfo) {
    return (
      <div style={{ marginTop: 20 }}>
        <p>
          <Icon type="report" style={{ marginTop: -5, color: 'red' }} />
          {'你将要停止 '}
          <span style={{ fontWeight: 600 }}>{data.name}</span>
          {' ，停止后火车下的PI会进行关闭删除，已关联PI的特性会被放入特性列表的待办事项，你可以重新规划。'}
        </p>
        <p style={{ marginBottom: 5 }}>
          {'当前进行中的PI：'}
          <span style={{ color: 'red', fontWeight: 600 }}>{stopArtPIInfo.activePiDTO ? `${stopArtPIInfo.activePiDTO.code}-${stopArtPIInfo.activePiDTO.name} ` : '无'}</span>
        </p>
        <p style={{ marginBottom: 5 }}>
          {'已完成的PI个数：'}
          <span style={{ color: 'red', fontWeight: 600 }}>{stopArtPIInfo.completedPiCount ? stopArtPIInfo.completedPiCount : '无'}</span>
        </p>
        <p style={{ marginBottom: 5 }}>
          {'未开启的PI个数：'}
          <span style={{ color: 'red', fontWeight: 600 }}>{stopArtPIInfo.todoPiCount ? stopArtPIInfo.todoPiCount : '无'}</span>
        </p>
        <p style={{ marginBottom: 5 }}>
          {'关联PI的特性个数：'}
          <span style={{ color: 'red', fontWeight: 600 }}>{stopArtPIInfo.relatedFeatureCount ? stopArtPIInfo.relatedFeatureCount : '无'}</span>
        </p>
      </div>
    );
  }
};

const handleOnOk = (props) => {
  const { onOk } = props;
  onOk(canStop);
};

const StopArtModal = (props) => {
  const { visible, onCancel } = props;
  return (
    <Modal
      title="停止火车"
      visible={visible}
      onOk={() => { handleOnOk(props); }}
      onCancel={onCancel}
    >
      <div>
        {renderStopArtModalContent(props)}
      </div>
    </Modal>
  );
};

StopArtModal.propTypes = propTypes;

export default StopArtModal;
