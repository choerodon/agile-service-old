// 反馈按钮
import React, { useEffect } from 'react';
import { Upload, Button } from 'choerodon-ui';
import { randomWord } from '../../../../common/utils';
import './FeedbackUpload.scss';

const FeedbackUpload = ({ fileList, onChange }) => {
  console.log(fileList);
  const randomClassName = randomWord(false, 5);
  useEffect(() => {
    const selectEle = document.querySelector(`.${randomClassName} .c7n-upload-select`);
    const fileListEle = document.querySelector(`.${randomClassName} .c7n-upload-list`);
    if (selectEle && fileListEle) {
      fileListEle.appendChild(selectEle);
    }
  });
  return (
    <div className="feedbackUpload">
      <Upload
        className={`upload-content ${randomClassName}`}
        fileList={fileList}
        onChange={onChange}
        multiple={false}
        beforeUpload={() => false}
      >
        <Button
          type="primary"
          funcType="raised"
          className="upload-btn"
          shape="circle"
          icon="file_upload"
        />
      </Upload>
    </div>
  );
};
export default FeedbackUpload;
