import React, {
  Component, useCallback, useEffect, useState,
} from 'react';
import {
  Icon, Select, Button, Upload, Input, Menu, Dropdown, Collapse, Tooltip,
} from 'choerodon-ui';
import { stores } from '@choerodon/boot';
import classnames from 'classnames';
import TimeAgo from 'timeago-react';
import WYSIWYGEditor from '../../../../components/WYSIWYGEditor';
import WYSIWYGViewer from '../../../../components/WYSIWYGViewer';
import './FeedbackContentComponent.scss';
import UserHead from './UserHead';
import CommentDescription from '../../../../components/CommonComponent/IssueDescription';
import { delta2Html, text2Delta, randomWord } from '../../../../common/utils';

const { AppState } = stores;
// eslint-disable-next-line quote-props
const statusList = new Map([
  ['todo', '待处理'],
  ['doing', '处理中'],
  ['done', '完成'],
  ['cancel', '取消'],
]);

export function ToggleQuestion({
  summary, status, handleChangeStatus, handleClose,
}) {
  let nextStatusName = '';
  let nextStatus = '';
  const statusIndex = Array.from(statusList.keys()).findIndex(listStatus => listStatus === status);
  // 判断是不是最后一个状态
  if (statusIndex !== statusList.length - 1) {
    // 拿到在这个状态后的
    nextStatusName = Array.from(statusList.values())[statusIndex + 1];
    nextStatus = Array.from(statusList.keys())[statusIndex + 1];
  }
  return (
    <section className="feedback-main-toggleQuestion">
      <h1>{summary}</h1>
      {status !== 'cancel' && (
        <aside className="feedback-main-toggleQuestion-btn-group">
          {nextStatus !== 'cancel' && (
            <Button
              type="primary"
              funcType="raised"
              onClick={() => handleChangeStatus(nextStatus)}
            >
              {nextStatusName}
            </Button>
          )}
          <Button
            funcType="raised"
            onClick={handleClose}
          >
            取消
          </Button>
        </aside>
      )}
    </section>
  );
}

export function Summary({ description }) {
  return (
    <section className="feedback-main-summary">
      <h6>描述</h6>
      <WYSIWYGViewer data={description} />
    </section>
  );
}

export function Attachment({ data }) {
  const [fileList, setFileList] = useState([]);
  useEffect(() => {
    setFileList(data.map(({
      id, feedbackId, url, fileName,
    }) => ({
      name: fileName,
      uid: id,
      feedbackId,
      url: `http://minio.staging.saas.hand-china.com/feedback-service/${url}`,
      status: 'done',
    })));
  }, []);
 
  return (
    <section className="feedback-main-attachment" style={{ marginBottom: `${fileList.length ? '20px' : 0}` }}>
      {
        fileList.length ? (
          <React.Fragment>
            <h6>附件</h6>
            <div
              className="feedback-main-attachment-upload-container"
            >
              <Upload
                className="upload-content"
                fileList={fileList}
                multiple
              />
            </div>
          </React.Fragment>
        ) : null
      }
     
    </section>
  );
}

export function Discuss({
  data, replyData, handleAddDiscuss, handleEditDiscuss, handleRemoveDiscuss, hasPermission,
}) {
  const [openAllDiscuss, setOpenAllDiscuss] = useState(true);

  const [asc, setAsc] = useState(false);

  const handleOpenAllDiscuss = () => setOpenAllDiscuss(!openAllDiscuss);

  const handleSort = () => setAsc(!asc);

  const build = (parentComment) => {
    const children = [];
    const findChildren = (parentId) => {
      const comments = replyData[parentId] || [];
      comments.forEach((comment) => {
        children.push(comment);
        findChildren(comment.id);
      });
    };
    const parentId = parentComment.id;
    findChildren(parentId);
    return children;
  };

  // 父节点
  const commentObj = data.map(item => ({
    ...item,
    children: build(item),
  }));

  return (
    <section
      className="feedback-main-discuss"
    >
      <header className="feedback-main-discuss-header">
        <h6 style={{ marginBottom: 0 }}>评论</h6>
        {replyData && replyData[0]
          ? (
            <div>
              <Button
                type="primary"
                onClick={handleOpenAllDiscuss}
              >
                {openAllDiscuss ? '全部收起' : '全部展开'}
              </Button>
              <Tooltip title="排序" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button
                  shape="circle"
                  className={asc ? 'feedback-main-discuss-order-asc' : 'feedback-main-discuss-order-desc'}
                  icon="vertical_align_bottom"
                  onClick={handleSort}
                />
              </Tooltip>
            </div>
          ) : null
        }
      </header>
      <DiscussBoard
        data={commentObj}
        replyData={replyData}
        asc={asc}
        openAllDiscuss={openAllDiscuss}
        handleEditDiscuss={handleEditDiscuss}
        handleRemoveDiscuss={handleRemoveDiscuss}
        handleAddDiscuss={handleAddDiscuss}
        hasPermission={hasPermission}
      />
      <AddDiscuss
        handleAddDiscuss={handleAddDiscuss}
      />
    </section>
  );
}

function DiscussBoard({
  data, handleEditDiscuss, handleRemoveDiscuss, handleAddDiscuss, openAllDiscuss, asc, hasPermission,
}) {
  return (
    <DiscussBoardContainer
      data={data}
      openAllDiscuss={openAllDiscuss}
      asc={asc}
      handleAddDiscuss={handleAddDiscuss}
      handleEditDiscuss={handleEditDiscuss}
      handleRemoveDiscuss={handleRemoveDiscuss}
      hasPermission={hasPermission}
    />
  );
}

function DiscussBoardContainer({
  data = [], replyDiscuss = false, openAllDiscuss, asc, handleAddDiscuss, handleRemoveDiscuss, handleEditDiscuss, hasPermission,
}) {
  const orderedData = asc ? data.reverse() : data;
  return (
    <div style={{ borderBottom: orderedData && orderedData.length ? '1px solid rgba(0,0,0,0.06)' : '' }}>
      {orderedData.map(({
        content, within, user, lastUpdateDate, id, userId, objectVersionNumber, parentId, beRepliedId, beRepliedUser, feedbackAttachmentDTOList, children,
      }) => (
        <SingleDiscuss
          className={replyDiscuss ? 'feedback-main-discuss-board-reply' : 'feedback-main-discuss-board'}
          replyData={children}
          user={user}
          beRepliedId={beRepliedId}
          beRepliedUser={beRepliedUser}
          userId={userId}
          within={within}
          lastUpdateDate={lastUpdateDate}
          content={content}
          id={id}
          parentId={parentId}
          objectVersionNumber={objectVersionNumber}
          replyDiscuss={replyDiscuss}
          openAllDiscuss={openAllDiscuss}
          handleAddDiscuss={handleAddDiscuss}
          handleEditDiscuss={handleEditDiscuss}
          handleRemoveDiscuss={handleRemoveDiscuss}
          feedbackAttachmentDTOList={feedbackAttachmentDTOList}
          hasPermission={hasPermission}
        />
      ))}
    </div>
  );
}

const SingleDiscuss = ({
  user, within, replyData, lastUpdateDate, content, id, replyDiscuss, openAllDiscuss, handleAddDiscuss,
  handleEditDiscuss, handleRemoveDiscuss, userId, parentId, objectVersionNumber, beRepliedId,
  beRepliedUser, className, feedbackAttachmentDTOList, hasPermission,
}) => {
  const [openDiscussContent, toggleDiscussContent] = useState(true);
  const [reply, toggleReply] = useState(false);
  const [edit, toggleEdit] = useState(false);
  const [inputText, setInputText] = useState(content);
  const [fileList, setFileList] = useState();
  useEffect(() => {
    toggleDiscussContent(openAllDiscuss);
  }, [openAllDiscuss]);
  return (
    <ReplyContainer
      isReply={reply}
      handleAddDiscuss={handleAddDiscuss}
      toggleDiscussContent={toggleDiscussContent}
      toggleReply={toggleReply}
      userId={userId}
      id={id}
      parentId={parentId}
    >
      {() => (
        <div className={className}>
          <header className={classnames(`${className}-header`, {
            white: openDiscussContent,
          })}
          >
            <div className="feedback-main-discuss-board-header-left">
              {!replyDiscuss && <Icon className="feedback-main-discuss-board-header-icon" type={openDiscussContent ? 'expand_more' : 'navigate_next'} onClick={() => toggleDiscussContent(!openDiscussContent)} />}
              <UserHead
                size={30}
                hiddenText
                user={user}
              />
              <span style={{ marginLeft: 5 }}>{user.name}</span>
              {beRepliedId
              && (
                <div className="feedback-main-discuss-board-header-left-comment-reply">
                  <span className="feedback-main-discuss-board-header-left-comment-reply-title">回复</span>
                  <span>{beRepliedUser.name}</span>
                </div>
              )}
              {!within && <span className={`${className}-header-left-comment-type`}>回复提问</span>}
              <Tooltip title={lastUpdateDate}>
                <TimeAgo
                  className="feedback-main-discuss-board-header-left-time"
                  datetime={lastUpdateDate}
                  locale="zh_CN"
                />
              </Tooltip>
              {!replyDiscuss && !openDiscussContent && (
                <div className="feedback-main-discuss-board-header-left-comment-content">
                  <CommentDescription data={delta2Html(content).replace(/<img(.*?)>/, '【图片】')} />
                </div>
              )}
            </div>
            <div className="feedback-main-discuss-board-header-right hover">
              <Tooltip title="回复" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button
                  icon="comment"
                  shape="circle"
                  onClick={() => {
                    toggleReply(!reply);
                  }}
                />
              </Tooltip>
              <Tooltip title="编辑" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button
                  icon="mode_edit"
                  shape="circle"
                  disabled={AppState.userInfo.id !== userId && !hasPermission}
                  onClick={() => {
                    toggleDiscussContent(true);
                    toggleEdit(true);
                    setInputText(content);
                  }}
                />
              </Tooltip>
              <Tooltip title="删除" getPopupContainer={triggerNode => triggerNode.parentNode}>
                <Button
                  icon="delete_forever"
                  shape="circle"
                  disabled={AppState.userInfo.id !== userId && !hasPermission}
                  onClick={() => handleRemoveDiscuss(id)}
                />
              </Tooltip>
            </div>
          </header>
          {openDiscussContent && (
            <div style={{ paddingLeft: '20px' }}>
              {/* eslint-disable-next-line no-nested-ternary */}
              {!replyDiscuss ? (edit ? (
                <React.Fragment>
                  <WYSIWYGEditor hideFullScreen style={{ width: '100%', marginBottom: '20px' }} value={text2Delta(inputText)} onChange={value => setInputText(value)} />
                  <Button onClick={() => handleEditDiscuss(inputText, objectVersionNumber, id, () => toggleEdit(false))}>保存</Button>
                  <Button onClick={() => toggleEdit(false)}>取消</Button>
                </React.Fragment>
              ) : (
                <div>
                  <CommentDescription data={delta2Html(content)} />
                  <div style={{ marginBottom: 9 }}>
                    {feedbackAttachmentDTOList && feedbackAttachmentDTOList.map(item => (
                      <a style={{ marginRight: 6 }} href={`http://minio.staging.saas.hand-china.com/feedback-service/${item.url}`}>{item.fileName}</a>
                    ))}
                  </div>
                 
                </div>
              )) : null}
              <DiscussBoardContainer
                data={replyData}
                replyDiscuss
                handleAddDiscuss={handleAddDiscuss}
                handleEditDiscuss={handleEditDiscuss}
                handleRemoveDiscuss={handleRemoveDiscuss}
              />
            </div>
          )}
          {/* eslint-disable-next-line no-nested-ternary */}
          {replyDiscuss ? (edit
            ? (
              <React.Fragment>
                <WYSIWYGEditor hideFullScreen value={text2Delta(inputText)} onChange={value => setInputText(value)} onPressEnter={e => handleEditDiscuss(e.currentTarget.value, objectVersionNumber, id, () => toggleEdit(false))} />
                <Button onClick={() => handleEditDiscuss(inputText, objectVersionNumber, id, () => toggleEdit(false))}>保存</Button>
                <Button onClick={() => toggleEdit(false)}>取消</Button>
              </React.Fragment>
            )
            : (
              <div>
                <CommentDescription data={delta2Html(content)} />
                <div style={{ marginBottom: 9 }}>
                  {feedbackAttachmentDTOList && feedbackAttachmentDTOList.map(item => (
                    <a style={{ marginRight: 6 }} href={`http://minio.staging.saas.hand-china.com/feedback-service/${item.url}`}>{item.fileName}</a>
                  ))}
                </div>
              </div>
            )) : null}
        </div>
      )}
    </ReplyContainer>
  );
};

function ReplyContainer({
  children, isReply, handleAddDiscuss, userId, parentId = 0, id, toggleDiscussContent, toggleReply,
}) {
  return (
    <React.Fragment>
      {children()}
      {isReply && <AddDiscuss handleAddDiscuss={handleAddDiscuss} inputType="reply" userId={userId} parentId={parentId} toggleDiscussContent={toggleDiscussContent} toggleReply={toggleReply} id={id} />}
    </React.Fragment>
  );
}

function AddDiscuss({
  handleAddDiscuss, inputType = null, userId = '', parentId, id, toggleReply = null,
}) {
  const [clicked, setClicked] = useState(inputType === 'reply');
  const handleOpenAddDiscuss = (e) => {
    e.stopPropagation();
    setClicked(true);
  };
  const handleCloseAddDiscuss = (e) => {
    e.stopPropagation();
    setClicked(false);
    // eslint-disable-next-line no-unused-expressions
    toggleReply && toggleReply(false);
  };

  return (
    <aside
      onClick={handleOpenAddDiscuss}
      role="none"
      className={classnames('feedback-main-discuss-add', {
        'feedback-main-discuss-add-unclicked': !clicked,
        'feedback-main-discuss-add-clicked': clicked,
      })}
    >
      {/* eslint-disable-next-line no-nested-ternary */}
      {inputType === 'reply' ? (
        <UploadAndText
          inputType={inputType}
          userId={userId}
          parentId={parentId}
          id={id}
          handleAddDiscuss={handleAddDiscuss}
          handleCloseAddDiscuss={handleCloseAddDiscuss}
        />
      ) : (
        !clicked ? (
          <span style={{ height: '36px', display: 'flex', alignItems: 'center' }}>点击添加评论...</span>
        ) : (
          <UploadAndText
            inputType="add"
            userId={userId}
            parentId={parentId}
            id={id}
            handleAddDiscuss={handleAddDiscuss}
            handleCloseAddDiscuss={handleCloseAddDiscuss}
          />
        )
      )}
    </aside>
  );
}

const UploadAndText = ({
  content = '', inputType, userId = 0, parentId, id, handleAddDiscuss, handleCloseAddDiscuss,
}) => {
  const [inputText, setInputText] = useState(content);
  const [fileList, setFileList] = useState([]);
  const [type, setType] = useState('内部评论');
  const [within, toggleWithIn] = useState(true);
  const handleChangeDiscussType = (e) => {
    if (e.key === '0') {
      toggleWithIn(true);
      setType('内部评论');
    } else if (e.key === '1') {
      toggleWithIn(false);
      setType('回复问题');
    }
  };
  const getMenu = () => (
    <Menu onClick={handleChangeDiscussType}>
      <Menu.Item
        key="0"
      >
        内部评论
      </Menu.Item>
      <Menu.Item
        key="1"
      >
        回复问题
      </Menu.Item>
    </Menu>
  );
  const uploadFunc = async (e) => {
    const inputTextWithNoSpace = inputText && inputText.filter(item => item.insert.trim() !== '');
    if ((inputText && inputTextWithNoSpace && inputTextWithNoSpace.length) || (fileList && fileList.length)) {
      if (inputType === 'reply') {
        await handleAddDiscuss(inputText, within, fileList, userId, id);
        handleCloseAddDiscuss(e);
      } else {
        await handleAddDiscuss(inputText, within, fileList, null, 0);
        handleCloseAddDiscuss(e);
      }
    } else {
      Choerodon.prompt('文本框没有任何内容');
    }
  };
  return (
    <React.Fragment>
      <WYSIWYGEditor
        hideFullScreen
        style={{
          width: '100%',
        }}
        value={inputText}
        onChange={value => setInputText(value)}
      />
      <div className="feedback-main-discuss-add-clicked-control">
        <DiscussUpload fileList={fileList} setFileList={setFileList} />
        <div className="feedback-main-discuss-add-clicked-type">
          {inputType === 'reply' ? (
            <Button funcType="flat" onClick={uploadFunc} style={{ marginRight: '10px' }}>回复</Button>
          ) : (
            <React.Fragment>
              <Button
                type="primary"
                funcType="flat"
                style={{ border: 'none', borderRadius: '2px 0 0 2px' }}
                onClick={uploadFunc}
              >
                {type}
              </Button>
              <Dropdown overlay={getMenu()} trigger={['click']}>
                <Button
                  icon="more_vert"
                  style={{
                    borderRadius: '0 2px 2px 0',
                    borderLeft: 'none',
                    marginRight: '10px',
                  }}
                />
              </Dropdown>
            </React.Fragment>
          )}
          <Button
            className="feedback-main-discuss-add-clicked-cancel"
            onClick={handleCloseAddDiscuss}
          >
            取消
          </Button>
        </div>
      </div>
    </React.Fragment>
  );
};

const DiscussUpload = ({ fileList, setFileList }) => {
  const randomClassName = randomWord(false, 5);
  useEffect(() => {
    const selectEle = document.querySelector(`.${randomClassName} .c7n-upload-select`);
    const fileListEle = document.querySelector(`.${randomClassName} .c7n-upload-list`);
    if (selectEle && fileListEle) {
      fileListEle.appendChild(selectEle);
    }
  });
  const handleChange = (props) => {
    setFileList(props.fileList);
  };
  return (
    <Upload
      className={`upload-content ${randomClassName}`}
      fileList={fileList}
      onChange={handleChange}
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
  );
};

// export default {
//   ToggleQuestion,
//   Summary,
// };
