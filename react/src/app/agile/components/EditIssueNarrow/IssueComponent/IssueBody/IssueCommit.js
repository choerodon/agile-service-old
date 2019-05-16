import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import WYSIWYGEditor from '../../../WYSIWYGEditor';
import Comment from '../../Component/Comment';
import { text2Delta, beforeTextUpload } from '../../../../common/utils';
import { createCommit } from '../../../../api/NewIssueApi';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addCommit: false,
      addCommitDes: '',
    };
  }

  componentDidMount() {
  }

  newCommit = (commit) => {
    const { reloadIssue } = this.props;
    createCommit(commit).then(() => {
      if (reloadIssue) {
        reloadIssue();
      }
      this.setState({
        addCommit: false,
        addCommitDes: '',
      });
    });
  };

  handleCreateCommit() {
    const { store } = this.props;
    const issue = store.getIssue;
    const { issueId } = issue;
    const { addCommitDes } = this.state;
    if (addCommitDes) {
      beforeTextUpload(addCommitDes, { issueId, commentText: '' }, this.newCommit, 'commentText');
    } else {
      // this.newCommit({ issueId, commentText: '' });
      this.setState({
        addCommit: false,
        addCommitDes: '',
      });
    }
  }

  renderCommits() {
    const { store } = this.props;
    const issue = store.getIssue;
    const { issueCommentDTOList = [] } = issue;

    const { addCommitDes, addCommit } = this.state;
    const { reloadIssue } = this.props;
    const delta = text2Delta(addCommitDes);
    return (
      <div>
        {
          addCommit && (
            <div className="line-start mt-10" style={{ width: '100%' }}>
              <WYSIWYGEditor
                bottomBar
                value={delta}
                style={{ height: 200, width: '100%' }}
                onChange={(value) => {
                  this.setState({ addCommitDes: value });
                }}
                handleDelete={() => {
                  this.setState({
                    addCommit: false,
                    addCommitDes: '',
                  });
                }}
                handleSave={() => this.handleCreateCommit()}
                handleClickOutSide={() => this.handleCreateCommit()}
              />
            </div>
          )
        }
        {
          issueCommentDTOList.map(comment => (
            <Comment
              key={comment.commentId}
              comment={comment}
              onDeleteComment={reloadIssue}
              onUpdateComment={reloadIssue}
            />
          ))
        }
      </div>
    );
  }

  render() {
    return (
      <div id="commit">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="sms_outline c7n-icon-title" />
            <FormattedMessage id="issue.commit" />
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Button className="leftBtn" funcType="flat" onClick={() => this.setState({ addCommit: true })}>
              <Icon type="playlist_add icon" />
              <FormattedMessage id="issue.commit.create" />
            </Button>
          </div>
        </div>
        {this.renderCommits()}
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
