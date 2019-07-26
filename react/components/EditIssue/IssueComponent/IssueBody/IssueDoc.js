import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import { deleteDoc, loadDocs } from '../../../../api/NewIssueApi';
import Doc from '../../../Doc';
import DocItem from '../../Component/DocItem';

@inject('AppState')
@observer class IssueDoc extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addDocShow: false,
    };
  }

  componentDidMount() {
  }

  onDeleteDoc = async (id) => {
    const { store } = this.props;
    const { issueId } = store.getIssue;
    await deleteDoc(id);
    const res = await loadDocs(issueId);
    store.setDoc(res || []);
  };

  onDocCreate = async () => {
    const { store } = this.props;
    const { issueId } = store.getIssue;
    this.setState({ addDocShow: false });
    const res = await loadDocs(issueId);
    store.setDoc(res || []);
  };

  renderDoc = () => {
    const { store } = this.props;
    const docs = store.getDoc;
    return (
      <div>
        {
          docs && docs.knowledgeRelationList
          && docs.knowledgeRelationList.filter(item => item.spaceId).map(doc => (
            <DocItem
              key={doc.id}
              doc={doc}
              onDeleteDoc={this.onDeleteDoc}
              type="narrow"
            />
          ))
        }
      </div>
    );
  };

  render() {
    const { addDocShow } = this.state;
    const { store, disabled } = this.props;
    const { issueId } = store.getIssue;
    const docs = store.getDoc;

    return (
      <div id="doc">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="library_books c7n-icon-title" />
            <span>文档</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="添加文档" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => this.setState({ addDocShow: true })}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
          )}
        </div>
        {this.renderDoc()}
        {
          addDocShow ? (
            <Doc
              issueId={issueId}
              visible={addDocShow}
              onCancel={() => this.setState({ addDocShow: false })}
              onOk={this.onDocCreate}
              checkIds={docs ? docs.knowledgeRelationList.map(doc => doc.spaceId) : []}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueDoc));
