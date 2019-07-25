import React, { useEffect, useState } from 'react';
import {
  Button, Modal, Select, Input, Form, Upload, Tooltip, Icon,
} from 'choerodon-ui';
import { inject } from 'mobx-react';
import axios from 'axios';
import { beforeTextUpload } from '../../../../../common/utils';
import WYSIWYGEditor from '../../../../../components/WYSIWYGEditor';
import FeedbackUpload from '../FeedbackUpload';
import './FeedbackContent.scss';

const { Sidebar } = Modal;
const { Item: FormItem } = Form;
const { Option } = Select;

const tokenList = new Map([
  ['1052', { name: '敏捷新测试项目2', token: '428ecb67-71af-4a5d-a430-a66cda58ac75' }],
  ['1051', { name: '敏捷新测试项目1', token: 'f22347cf-161c-4be5-a068-482ce039c2b1' }],
  ['1036', { name: '纯敏捷项目', token: '371d64b1-9400-4be2-8427-b8affaaf8d4c' }],
  ['1034', { name: '敏捷新测试项目', token: '47f9f36a-d327-498d-9d3f-b7afc289614f' }],
]);

function FeedbackContent({
  form, visible, onClose, AppState, loading, delta, fileList, onSetLoading, onCreateFeedback, onFileListChange, onFeedbackTypeChange, onWYSIWYGEditorChange,
}) {
  const handleSubmitValue = async () => {
    form.validateFields((err, values) => {
      if (!err) {
        onSetLoading(true);
        const send = {
          summary: values.summary,
          type: values.type,
          status: 'todo',
          organizationId: AppState.currentMenuType.organizationId * 1,
          projectId: AppState.currentMenuType.id * 1,
          reporter: values.reporter,
          screenSize: `${window.screen.width}x${window.screen.height}`,
          browser: navigator.userAgent,
          email: values.email,
          token: tokenList.get(AppState.currentMenuType.id).token,
        };
        if (delta) {
          beforeTextUpload(delta, send, onCreateFeedback);
        } else {
          send.description = '';
          onCreateFeedback(send);
        }
      }
    });
  };

  const { getFieldDecorator } = form;
  return (
    <Sidebar
      className="c7n-feedback-content"
      title="反馈问题"
      visible={visible}
      onOk={handleSubmitValue}
      onCancel={onClose}
      cancelText="取消"
      okText="提交"
      confirmLoading={loading}
    >
      <Form style={{ width: 512 }}>
        <FormItem>
          {getFieldDecorator('type', {
            rules: [{
              required: true, message: '类型是必填的',
            }],
          })(
            <Select label="问题类型" allowClear onChange={onFeedbackTypeChange}>
              <Option value="question_consultation">问题咨询</Option>
              <Option value="bug_report">报告缺陷</Option>
              <Option value="recommendation_and_opinion">建议与意见</Option>
            </Select>,
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('summary', {
            rules: [{
              required: true, message: '概要是必填的',
            }],
          })(
            <Input placeholder="问题概要" maxLength={50} required label="问题概要" />,
          )}
        </FormItem>
        <div>
          <span style={{ marginBottom: '13px', display: 'block' }}>描述</span>
          <WYSIWYGEditor
            hideFullScreen
            value={delta}
            style={{ width: '512px' }}
            onChange={(value) => {
              onWYSIWYGEditorChange(value);
            }}
          />
        </div>
        <div>
          <span style={{ marginTop: '20px', display: 'block' }}>附件</span>
          <FeedbackUpload fileList={fileList} onChange={e => onFileListChange(e)} />
        </div>
        <FormItem>
          {getFieldDecorator('reporter', {
            rules: [{
              required: true, message: '报告人是必填的',
            }],
            initialValue: AppState.userInfo.realName,
          })(
            <Input maxLength={50} required label="报告人" />,
          )}
        </FormItem>
        <FormItem 
          className="c7n-feedback-content-emailItem"
        >
          {getFieldDecorator('email', {
            rules: [{
              type: 'email', message: '请输入有效的邮箱地址!',
            }],
          })(
            <Input
              maxLength={50}
              label="邮箱"
              suffix={(
                <Tooltip title="此邮箱由于问题反馈的消息接收">
                  <Icon type="help" />
                </Tooltip>
            )}
            />,
          )}
        </FormItem>
      </Form>
    </Sidebar>
  );
}

export default inject('AppState')(Form.create({})(FeedbackContent));
