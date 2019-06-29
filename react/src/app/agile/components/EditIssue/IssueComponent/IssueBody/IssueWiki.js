import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import { deleteWiki, loadWikies } from '../../../../api/NewIssueApi';
import Wiki from '../../../Wiki';
import WikiItem from '../../Component/WikiItem';

@inject('AppState')
@observer class IssueCommit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addWikiShow: false,
    };
  }

  componentDidMount() {
  }

  onDeleteWiki = async (id) => {
    const { store } = this.props;
    const { issueId } = store.getIssue;
    await deleteWiki(id);
    const res = await loadWikies(issueId);
    store.setWiki(res || []);
  };

  onWikiCreate = async () => {
    const { store } = this.props;
    const { issueId } = store.getIssue;
    this.setState({ addWikiShow: false });
    const res = await loadWikies(issueId);
    store.setWiki(res || []);
  };

  renderWiki = () => {
    const { store } = this.props;
    const wikies = store.getWiki;
    return (
      <div>
        {
          wikies && wikies.wikiRelationList
          && wikies.wikiRelationList.filter(item => item.spaceId).map(wiki => (
            <WikiItem
              key={wiki.id}
              wiki={wiki}
              onDeleteWiki={this.onDeleteWiki}
              wikiHost={wikies.wikiHost}
              type="narrow"
            />
          ))
        }
      </div>
    );
  };

  render() {
    const { addWikiShow } = this.state;
    const { store, disabled } = this.props;
    const { issueId } = store.getIssue;
    const wikies = store.getWiki;

    return (
      <div id="wiki">
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
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => this.setState({ addWikiShow: true })}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
          )}
        </div>
        {this.renderWiki()}
        {
          addWikiShow ? (
            <Wiki
              issueId={issueId}
              visible={addWikiShow}
              onCancel={() => this.setState({ addWikiShow: false })}
              onOk={this.onWikiCreate}
              checkIds={wikies ? wikies.wikiRelationList.map(wiki => wiki.spaceId) : []}
            />
          ) : null
        }
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueCommit));
