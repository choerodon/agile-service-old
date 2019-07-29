import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { stores } from '@choerodon/boot';
import { Icon } from 'choerodon-ui';
import { map } from 'lodash';
import LinkList from '../../Component/LinkList';
import EditIssueContext from '../../stores';

const { AppState } = stores;

const TestLink = observer(({
  history, reloadIssue,
}) => {
  const { store } = useContext(EditIssueContext);
  const { issueId: id } = store.getIssue;
  const linkIssues = store.getLinkIssues.filter(i => i.applyType === 'test');
  const onOpen = (caseId) => {
    const urlParams = AppState.currentMenuType;
    history.push(`/testManager/IssueManage/testCase/${caseId}?type=${urlParams.type}&id=${urlParams.id}&name=${encodeURIComponent(urlParams.name)}&organizationId=${urlParams.organizationId}`);
  };

  const renderLinkList = (link, i) => (
    <LinkList
      issue={{
        ...link,
        typeCode: link.typeCode,
      }}
      i={i}
      onOpen={(issueId) => {
        onOpen(issueId);
      }}
        // canDelete={false}
      onRefresh={() => {
        reloadIssue(id);
      }}
      type="test"
    />
  );

  const renderLinkIssues = () => (
    <div className="c7n-tasks">
      {
          map(linkIssues, (linkIssue, i) => renderLinkList(linkIssue, i))
        }
    </div>
  );

  
  if (linkIssues && linkIssues.length) {
    return (
      <div id="link_test">
        <div className="c7n-title-wrapper">
          <div className="c7n-title-left">           
            <span>测试用例</span>
          </div>         
        </div>
        {renderLinkIssues()}
      </div>
    );
  } else {
    return null;
  }
});


export default withRouter(TestLink);
