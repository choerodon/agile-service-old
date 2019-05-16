import React, { Component } from 'react';
import { Icon, Popconfirm } from 'choerodon-ui';
import UserHead from '../../UserHead';
import WYSIWYGEditor from '../../WYSIWYGEditor';
import { IssueDescription, DatetimeAgo } from '../../CommonComponent';
import {
  delta2Html, text2Delta, beforeTextUpload,
} from '../../../common/utils';
import { deleteCommit, updateCommit } from '../../../api/NewIssueApi';
import './Comment.scss';


class Comment extends Component {
  constructor(props, context) {
    super(props, context);
    this.state = {
      editCommentId: undefined,
      editComment: undefined,
      expand: false,
    };
  }

  componentDidMount() {
  }

  handleDeleteCommit = (commentId) => {
    const { onDeleteComment } = this.props;
    deleteCommit(commentId)
      .then(() => {
        if (onDeleteComment) {
          onDeleteComment();
        }
      });
  };

  handleUpdateComment = (comment) => {
    const { editComment } = this.state;
    const { commentId, objectVersionNumber } = comment;
    const extra = {
      commentId,
      objectVersionNumber,
    };
    const updateCommentDes = editComment;
    if (updateCommentDes) {
      beforeTextUpload(updateCommentDes, extra, this.updateComment, 'commentText');
    } else {
      extra.commentText = '';
      this.updateComment(extra);
    }
  };

  updateComment = (comment) => {
    const { onUpdateComment } = this.props;
    updateCommit(comment).then(() => {
      this.setState({
        editCommentId: undefined,
        editComment: undefined,
      });
      if (onUpdateComment) {
        onUpdateComment();
      }
    });
  };

  render() {
    const { comment } = this.props;
    const { editComment, editCommentId, expand } = this.state;


    const deltaEdit = text2Delta(editComment);
    return (
      <div
        className={`c7n-comment ${comment.commentId === editCommentId ? 'c7n-comment-focus' : ''}`}
      >
        <div className="line-justify">
          {
            expand ? (
              <Icon
                role="none"
                style={{ 
                  position: 'absolute',
                  left: 5,
                  top: 15,
                }}
                type="baseline-arrow_drop_down pointer"
                onClick={() => {
                  this.setState({
                    expand: false,
                  });
                }}
              />
            ) : null
          }
          {
            !expand ? (
              <Icon
                role="none"
                style={{ 
                  position: 'absolute',
                  left: 5,
                  top: 15,
                }}
                type="baseline-arrow_right pointer"
                onClick={() => {
                  this.setState({
                    expand: true,
                  });
                }}
              />
            ) : null
          }
          <div className="c7n-title-commit" style={{ flex: 1 }}>
            <UserHead
              user={{
                id: comment.userId,
                loginName: '',
                realName: comment.userName,
                avatar: comment.userImageUrl,
              }}
              color="#3f51b5"
            />
            <div style={{ color: 'rgba(0, 0, 0, 0.65)', marginLeft: 15 }}>
              <DatetimeAgo
                date={comment.lastUpdateDate}
              />
            </div>
          </div>
          <div className="c7n-action">
            <Icon
              role="none"
              type="mode_edit mlr-3 pointer"
              onClick={() => {
                this.setState({
                  editCommentId: comment.commentId,
                  editComment: comment.commentText,
                  expand: true,
                });
              }}
            />
            <Popconfirm
              title="确认要删除该评论吗?"
              placement="left"
              onConfirm={() => this.handleDeleteCommit(comment.commentId)}
              onCancel={this.cancel}
              okText="删除"
              cancelText="取消"
              okType="danger"
            >
              <Icon
                role="none"
                type="delete_forever mlr-3 pointer"
              />
            </Popconfirm>
          </div>
        </div>
        {
          expand && (
            <div className="c7n-conent-commit" style={{ marginTop: 10 }}>
              {
                comment.commentId === editCommentId ? (
                  <WYSIWYGEditor
                    bottomBar
                    value={deltaEdit}
                    style={{ height: 200, width: '100%' }}
                    onChange={(value) => {
                      this.setState({ editComment: value });
                    }}
                    handleDelete={() => {
                      this.setState({
                        editCommentId: undefined,
                        editComment: undefined,
                      });
                    }}
                    handleSave={this.handleUpdateComment.bind(this, comment)}
                    // toolbarHeight={isWide ? null : 66}
                    // toolbarHeight={66}
                  />
                ) : (
                  <IssueDescription data={delta2Html(comment.commentText)} />
                )
              }
            </div>
          )
        }
        
      </div>
    );
  }
}

export default Comment;
