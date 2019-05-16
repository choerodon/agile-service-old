import React from 'react';
import PropTypes from 'prop-types';
import { Modal } from 'choerodon-ui';
import _ from 'lodash';
import moment from 'moment';

const propTypes = {
  visible: PropTypes.bool.isRequired,
  // eslint-disable-next-line react/no-unused-prop-types
  onOk: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};

let canStart = true;

const renderPIName = (props) => {
  const { data: { piCodePrefix, piCodeNumber, piCount }, PiList } = props;
  const PiArr = PiList.sort((a, b) => a.id - b.id).map(item => <span key={item.name} style={{ marginRight: 5 }}>{`${item.code}-${item.name}`}</span>);
  if ((PiArr && PiArr.length > 0) || (piCount && piCodePrefix && piCodeNumber)) {
    const piCodeNumArr = [];
    // eslint-disable-next-line no-plusplus
    for (let i = 0; i < piCount; i++) {
      piCodeNumArr.push(piCodeNumber + i);
    }

    const newPi = piCodeNumArr.map(codeNum => (
      <span key={codeNum} style={{ marginRight: 5, fontWeight: 600 }}>{`${piCodePrefix}-${codeNum}`}</span>
    ));
    return [...PiArr, ...newPi];
  } else {
    return (
      <span style={{ fontWeight: 600 }}>-</span>
    );
  }
};

// eslint-disable-next-line consistent-return
const renderStartArtModalContent = (props) => {
  const { data, artList, startArtShowInfo } = props;
  const doingArt = artList.find(item => item.statusCode === 'doing');
  const nonEmpty = Object.keys(_.pick(data, ['startDate', 'piCount', 'piCodePrefix', 'piCodeNumber', 'interationCount', 'interationWeeks', 'ipWeeks'])).every(key => data[key] !== undefined && data[key] !== null);
  canStart = nonEmpty && !doingArt;
  const showInfos = (
    <div>
      {
        Object.keys(startArtShowInfo).map((key) => {
          if (key !== 'piName' && key !== 'startDate') {
            return (
              <p key={key} style={{ marginBottom: 5 }}>
                <span>{`${startArtShowInfo[key].name}：`}</span>
                <span style={{ fontWeight: 600, color: startArtShowInfo[key].empty ? 'red' : '#3f51b5' }}>{data[key] !== undefined && data[key] !== null ? data[key] : '-'}</span>
              </p>
            );
          } else if (key === 'startDate') {
            return (
              <p key={key} style={{ marginBottom: 5 }}>
                <span>{`${startArtShowInfo[key].name}：`}</span>
                <span style={{ fontWeight: 600, color: startArtShowInfo[key].empty ? 'red' : '#3f51b5' }}>{moment(data[key]).format('YYYY-MM-DD') || '-'}</span>
              </p>
            );
          } else {
            return (
              <p key={key} style={{ marginBottom: 5 }}>
                <span>{`${startArtShowInfo[key].name}：`}</span>
                <span style={{ fontWeight: 600, color: data.piCount && data.piCodePrefix && data.piCodeNumber ? '#3f51b5' : 'red' }}>
                  {
                    renderPIName(props)
                  }
                </span>

              </p>
            );
          }
        })
      }
    </div>
  );
  const nonEmptyShow = (
    <div>
      <p style={{ marginBottom: 15 }}>
        <span>
          {'你正在启动 '}
          <span style={{ fontWeight: 600 }}>{data.name}</span>
          {'，当前没有正在进行的火车。'}
        </span>
      </p>

    </div>
  );
  const EmptyShow = (
    <div>
      <p style={{ marginBottom: 15 }}>
        <span>
          {'你无法启动火车 '}
          <span style={{ fontWeight: 600 }}>{data.name}</span>
          {'，请输入带'}
          <span style={{ color: 'red' }}>
            {' * '}
          </span>
          {'符号的必填信息再启动火车。'}
        </span>
      </p>

    </div>
  );
  if (!nonEmpty) {
    return (
      <div>
        {EmptyShow}
        {showInfos}
      </div>
    );
  } else if (doingArt) {
    return (
      <div>
        {'你无法开启新的火车  '}
        <span style={{ fontWeight: 600 }}>{data.name}</span>
        {'  ，火车 '}
        <span style={{ color: 'red', fontWeight: 600 }}>
          {doingArt.name}
        </span>
        {'  正在进行中，可以先停止进行中的火车，再开启新的火车。'}
      </div>
    );
  } else {
    return (
      <div>
        {nonEmptyShow}
        {showInfos}
      </div>
    );
  }
};

const handleStartOk = (props) => {
  const { onOk } = props;
  onOk(canStart);
};

const StartArtModal = (props) => {
  const {
    visible, onCancel,
  } = props;
  return (
    <Modal
      title="启动火车"
      visible={visible}
      onOk={() => { handleStartOk(props); }}
      onCancel={onCancel}
    >
      <div style={{ marginTop: 20 }}>
        {renderStartArtModalContent(props)}
      </div>
    </Modal>
  );
};

StartArtModal.propTypes = propTypes;

export default StartArtModal;
