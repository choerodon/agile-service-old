import React, { memo } from 'react';
import PropTypes from 'prop-types';

import ArtForm, { ee } from './component/ArtForm';

const propTypes = {
  initValue: PropTypes.shape({}).isRequired,
  onSave: PropTypes.func.isRequired,
  onFormChange: PropTypes.func.isRequired,
};
const ArtSetting = ({
  initValue,
  data,
  PiList,
  onSave,
  onFormChange,
  onGetPIList,
  onGetArtInfo,
  onDeletePI,
}) => (
  <div>
    <ArtForm 
      onChange={onFormChange}
      data={data}
      initValue={initValue}
      PiList={PiList}
      onSave={onSave}
      onGetPIList={onGetPIList}
      onGetArtInfo={onGetArtInfo}
      onDeletePI={onDeletePI}
    />
  </div>
);

ArtSetting.propTypes = propTypes;

export { ee };
export default memo(ArtSetting);
