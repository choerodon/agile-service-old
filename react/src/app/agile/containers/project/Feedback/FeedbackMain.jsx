import React, { Component, useEffect, useState } from 'react';
import { inject } from 'mobx-react';
import { axios } from '@choerodon/boot';
import { Spin } from 'choerodon-ui';
import {
  ToggleQuestion, Summary, Attachment, Discuss,
} from './FeedbackComponent/FeedbackContentComponent';
import { beforeTextUpload } from '../../../common/utils';

function FeedbackContent({
  className, feedbackData, feedbackComment, AppState, id, fetchUser, hasPermission,
}) {
  const {
    status, summary, description, feedbackAttachmentDTOList, objectVersionNumber,
  } = feedbackData;

  const handleChangeStatus = (changeStatus) => {
    axios.put(`agile/v1/projects/${AppState.currentMenuType.id}/feedback`, {
      status: changeStatus,
      id,
      objectVersionNumber,
    }).then(res => fetchUser());
  };

  const handleClose = () => {
    axios.put(`agile/v1/projects/${AppState.currentMenuType.id}/feedback`, {
      status: 'cancel',
      id,
      objectVersionNumber,
    }).then(res => fetchUser());
  };

  const handleAddSave = (data, fileList) => new Promise((resolve) => {
    axios.post(`agile/v1/projects/${AppState.currentMenuType.id}/feedback_comment`, data).then((res) => {
      if (fileList.length) {
        const formData = new FormData();
        fileList.forEach(file => formData.append('file', file));
        axios.post(`agile/v1/projects/${AppState.currentMenuType.id}/feedback_attachment?feedbackId=${id}&commentId=${res.id}`, formData, {
          headers: { 'content-type': 'multipart/form-datal' },
        }).then(() => {
          resolve();
          fetchUser();
        });
      } else {
        resolve();
        fetchUser();
      }
    });
  });

  const handleAddDiscuss = (value, within, fileList, replyUserId = null, parentId) => new Promise(async (resolve) => {
    const extra = {
      feedbackId: id,
      userId: AppState.userInfo.id,
      projectId: AppState.currentMenuType.id,
      within,
      objectVersionNumber: 0,
      beRepliedId: replyUserId,
      parentId,
    };
    const deltaOps = value;
    if (deltaOps) {
      beforeTextUpload(deltaOps, extra, async (text) => {
        await handleAddSave(text, fileList);
        resolve();
      }, 'content');
    } else {
      extra.content = '';
      await handleAddSave(extra, fileList);
      resolve();
    }
  });

  const handleEditSave = (data, callback) => {
    axios.put(`agile/v1/projects/${AppState.currentMenuType.id}/feedback_comment`, data).then((res) => {
      fetchUser();
      if (callback) {
        callback();
      }
    });
  };

  const handleEditDiscuss = (value, commentObjectVersionNumber, commentId, callback) => {
    const extra = {
      feedbackId: id,
      objectVersionNumber: commentObjectVersionNumber,
      projectId: AppState.currentMenuType.id,
      id: commentId,
    };
    const deltaOps = value;
    if (deltaOps) {
      const inputTextWithImage = deltaOps && deltaOps.filter(item => item.insert.image);
      const inputTextWithNoImage = deltaOps && deltaOps.filter(item => !item.insert.image);
      const inputTextWithNoSpace = inputTextWithNoImage && inputTextWithNoImage.filter(item => item.insert.trim() !== '');
      if ((inputTextWithImage && inputTextWithImage.length) || (inputTextWithNoSpace && inputTextWithNoSpace.length)) {
        beforeTextUpload(deltaOps, extra, (text) => {
          handleEditSave(text, callback);
        }, 'content');
      } else {
        Choerodon.prompt('文本框没有任何内容');
      }
    } else {
      // extra.content = '';
      // handleEditSave(extra, callback);
      Choerodon.prompt('文本框没有任何内容');
    }
  };

  const handleRemoveDiscuss = (commentId) => {
    axios.delete(`agile/v1/projects/${AppState.currentMenuType.id}/feedback_comment/${commentId}`).then(res => fetchUser());
  };

  return (
    <main className={className}>
      <ToggleQuestion
        status={status}
        summary={summary}
        handleChangeStatus={handleChangeStatus}
        handleClose={handleClose}
      />
      <Summary
        description={description}
      />
      <Attachment
        data={feedbackAttachmentDTOList}
      />
      <Discuss
        replyData={feedbackComment}
        data={Object.keys(feedbackComment).length ? feedbackComment[0] : []}
        handleAddDiscuss={handleAddDiscuss}
        handleEditDiscuss={handleEditDiscuss}
        handleRemoveDiscuss={handleRemoveDiscuss}
        hasPermission={hasPermission}
      />
    </main>
  );
}

export default inject('AppState')(FeedbackContent);
