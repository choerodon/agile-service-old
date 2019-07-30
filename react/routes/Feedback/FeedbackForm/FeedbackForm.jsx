import React, {
  useEffect, useState, useContext, useImperativeHandle, useRef, 
} from 'react';
import {
  Button, Select, Form, EmailField, TextField, Tooltip,
} from 'choerodon-ui/pro';
import { Icon } from 'choerodon-ui';
import { inject } from 'mobx-react';
import axios from 'axios';
import FeedbackFormContext from './stores';
import { beforeTextUpload } from '../../../common/utils';
import WYSIWYGEditor from '../../../components/WYSIWYGEditor';
import FeedbackUpload from '../FeedbackContent/components/FeedbackUpload';
import './FeedbackForm.less';

const { Option } = Select;

function FeedbackForm(props) {
  const formRef = useRef();
  const {
    feedbackFormDataSet, prefixCls, feedbackFormRef, AppState,
  } = useContext(FeedbackFormContext);
 

  const [delta, setDelta] = useState(false);
  const [fileList, setFileList] = useState([]);

  const resetFeedbackForm = () => {
    setDelta('');
    setFileList([]);
  };

  const handleFileListChange = (e) => {
    setFileList([...e.fileList]);
  };

  const handleWYSIWYGEditorChange = (value) => {
    setDelta(value);
  };

  const createFeedback = (obj, refresh) => {
    try {
      axios.post('/agile/v1/feedback', obj).then((res) => {
        if (fileList.length) {
          const formData = new FormData();
          fileList.forEach(file => formData.append('file', file));
          axios.post(`/agile/v1/feedback_attachment?feedbackId=${res.id}&token=47f9f36a-d327-498d-9d3f-b7afc289614f`, formData, {
            headers: { 'content-type': 'multipart/form-datal' },
          }).then(() => {
            resetFeedbackForm();
            refresh(res.type);
            Choerodon.prompt('创建成功');
          });
        } else {
          resetFeedbackForm();
          refresh(res.type);
          Choerodon.prompt('创建成功');
        }
      });
    } catch (err) {
      Choerodon.prompt('创建失败，请重新创建');
    }
  };

  const onCreateFeedback = async (refresh) => {
    const values = feedbackFormDataSet.toData();
    if (await feedbackFormDataSet.validate(true, true)) {
      const send = {
        summary: values[0].summary,
        type: values[0].type,
        status: 'todo',
        organizationId: AppState.currentMenuType.organizationId * 1,
        projectId: AppState.currentMenuType.id * 1,
        reporter: values[0].reporter,
        screenSize: `${window.screen.width}x${window.screen.height}`,
        browser: navigator.userAgent,
        email: values[0].email,
        token: '47f9f36a-d327-498d-9d3f-b7afc289614f',
      };
      if (delta) {
        beforeTextUpload(delta, send, () => createFeedback(send, refresh));
      } else {
        send.description = '';
        createFeedback(send, refresh);
      }
    } else {
      Choerodon.prompt('请确保字段输入正确。');
    }
  };

  useImperativeHandle(feedbackFormRef, () => ({
    onCreateFeedback,
  }));


  return (
    <Form ref={formRef} dataSet={feedbackFormDataSet} className={prefixCls}>
      <Select name="type">
        <Option value="question_consultation">问题咨询</Option>
        <Option value="bug_report">报告缺陷</Option>
        <Option value="recommendation_and_opinion">建议与意见</Option>
      </Select>
      <TextField name="summary" placeholder="问题概要" maxLength={50} />
      <div name="description">
        <span style={{ marginBottom: '13px', display: 'block' }}>描述</span>
        <WYSIWYGEditor
          hideFullScreen
          value={delta}
          style={{ width: '512px' }}
          onChange={handleWYSIWYGEditorChange}
        />
      </div>
      <div>
        <span style={{ marginTop: '20px', display: 'block' }}>附件</span>
        <FeedbackUpload fileList={fileList} onChange={handleFileListChange} />
      </div>
      <TextField name="reporter" placeholder="报告人" />
      <EmailField
        name="email"
        placeholder="邮箱"
        suffix={(
          <Tooltip title="此邮箱由于问题反馈的消息接收">
            <Icon type="help" />
          </Tooltip>
            )}
      />
    </Form>
  );
}

export default FeedbackForm;
