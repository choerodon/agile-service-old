import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import _ from 'lodash';
import CreateLinkTask from '../../../CreateLinkTask';
import LinkList from '../../Component/LinkList';


@observer class IssueLink extends Component {
  constructor(props) {
    super(props);
    this.state = {
      createLinkTaskShow: false,
    };
  }

  componentDidMount() {
  }

  handleCreateLinkIssue() {
    const { reloadIssue } = this.props;
    this.setState({
      createLinkTaskShow: false,
    });
    if (reloadIssue) {
      reloadIssue();
    }
  }

  renderLinkList(link, i) {
    const { reloadIssue, store } = this.props;
    const { issueId: id } = store.getIssue;
    return (
      <LinkList
        issue={{
          ...link,
          typeCode: link.typeCode,
        }}
        i={i}
        onOpen={(issueId, linkedIssueId) => {
          reloadIssue(issueId === id ? linkedIssueId : issueId);
        }}
        onRefresh={() => {
          reloadIssue(id);
        }}
      />
    );
  }

  renderLinkIssues() {
    const { store } = this.props;
    const linkIssues = store.getLinkIssues;
    const group = _.groupBy(linkIssues.filter(i => i.applyType === 'agile'), 'ward');
    return (
      <div className="c7n-tasks">
        {
          _.map(group, (v, k) => (
            <div key={k}>
              <div style={{ margin: '7px auto' }}>{k}</div>
              {
                _.map(v, (linkIssue, i) => this.renderLinkList(linkIssue, i))
              }
            </div>
          ))
        }
      </div>
    );
  }

  render() {
    const { createLinkTaskShow } = this.state;
    const { store, disabled } = this.props;
    const issue = store.getIssue;
    const { issueId } = issue;

    return (
      <div id="link_task">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">
            <Icon type="link c7n-icon-title" />
            <span>问题链接</span>
          </div>
          <div style={{
            flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
          }}
          />
          {!disabled && (
          <div className="c7n-title-right" style={{ marginLeft: '14px' }}>
            <Tooltip title="创建链接" getPopupContainer={triggerNode => triggerNode.parentNode}>
              <Button style={{ padding: '0 6px' }} className="leftBtn" funcType="flat" onClick={() => this.setState({ createLinkTaskShow: true })}>
                <Icon type="playlist_add icon" />
              </Button>
            </Tooltip>
          </div>
          )}
        </div>
        {this.renderLinkIssues()}
        {
          createLinkTaskShow ? (
            <CreateLinkTask
              issueId={issueId}
              visible={createLinkTaskShow}
              onCancel={() => this.setState({ createLinkTaskShow: false })}
              onOk={this.handleCreateLinkIssue.bind(this)}
            />
          ) : null
        }
      </div>
    );
  }
}

export default IssueLink;
