import React, { useEffect } from 'react';
import { Upload, Button } from 'choerodon-ui';
import { randomWord } from '../../common/utils';
import './UploadButton.scss';

const UploadButton = ({ ...props }) => {
  const className = randomWord(false, 32);
  useEffect(() => {
    const uploadElement = document.querySelector(`.${className} .c7n-upload-select`);
    const uploadListElement = document.querySelector(`.${className} .c7n-upload-list`);
    if (uploadElement && uploadListElement) {
      uploadListElement.appendChild(uploadElement);
    }
  });
  const innerProps = {
    multiple: true,
    beforeUpload: () => false,
  };
  return (
    <Upload
      {...innerProps}
      {...props}
      className={`c7nagile-upload-button ${className}`}
    >
      <Button funcType="raised" type="primary" style={{ color: 'white' }} icon="file_upload" shape="circle" />
    </Upload>
  );
};

export default UploadButton;
