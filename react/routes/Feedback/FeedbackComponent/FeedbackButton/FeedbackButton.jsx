// 反馈按钮
import React, {
  useState, useEffect, createRef, 
} from 'react';
import { axios, stores, Content } from '@choerodon/boot';
import { Button, Modal } from 'choerodon-ui/pro';
import './FeedbackButton.scss';
import FeedbackForm from '../../FeedbackForm';

const { AppState } = stores;
const FeedbackButton = WrappedComponent => ((props) => {
  const feedbackForm = createRef();
  
  const handleCreateFeedback = async () => {
    const { onCreateFeedback } = feedbackForm.current;
    onCreateFeedback();
  };


  const openFeedbackModal = () => {
    Modal.open({
      key: 'feedbackModal',
      drawer: true,
      title: '反馈问题',
      children: (
        <FeedbackForm
          forwardedRef={feedbackForm}
          AppState={AppState}
        />
      ),
      okText: '提交',
      onOk: handleCreateFeedback,
      style: { width: '380px' },
    });
  };

  return (
    <React.Fragment>
      <WrappedComponent 
        {...props} 
      />
      <Button
        className="feedback-btn"
        type="primary"
        funcType="raised"
        icon="message_notification"
        onClick={() => openFeedbackModal()}
      />
    </React.Fragment>
  );
});

export default FeedbackButton;
