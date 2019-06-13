import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { stores } from '@choerodon/boot';
import { Icon } from 'choerodon-ui';
import { map } from 'lodash';
import { injectIntl } from 'react-intl';
import LinkList from '../../Component/LinkList';

const { AppState } = stores;

@inject('AppState')
@observer class TestLink extends Component {
  onOpen = (caseId) => {
    const { history } = this.props;
    const urlParams = AppState.currentMenuType;
    history.push(`/testManager/IssueManage/testCase/${caseId}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

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
          this.onOpen(issueId);
        }}
        // canDelete={false}
        onRefresh={() => {
          reloadIssue(id);
        }}
        type="test"
      />
    );
  }

  renderLinkIssues() {
    const { store } = this.props;
    const linkIssues = store.getLinkIssues.filter(i => i.applyType === 'test');
    return (
      <div className="c7n-tasks">
        {
          map(linkIssues, (linkIssue, i) => this.renderLinkList(linkIssue, i))
        }
      </div>
    );
  }

  render() {
    const { store } = this.props;
    const linkIssues = store.getLinkIssues.filter(i => i.applyType === 'test');
    if (linkIssues && linkIssues.length) {
      return (
        <div id="link_test">
          <div className="c7n-title-wrapper">
            <div className="c7n-title-left">
              <Icon type="classname c7n-icon-title" />
              <span>测试用例</span>
            </div>
            <div style={{
              flex: 1, height: 1, borderTop: '1px solid rgba(0, 0, 0, 0.08)', marginLeft: '14px',
            }}
            />
          </div>
          {this.renderLinkIssues()}
        </div>
      );
    } else {
      return '';
    }
  }
}

export default withRouter(injectIntl(TestLink));
