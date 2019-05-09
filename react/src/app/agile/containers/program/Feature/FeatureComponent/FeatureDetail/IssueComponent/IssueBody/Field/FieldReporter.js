import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import _ from 'lodash';
import TextEditToggle from '../../../../../../../../components/TextEditToggle';
import UserHead from '../../../../../../../../components/UserHead';
import { updateIssue } from '../../../../../../../../api/NewIssueApi';
import { getUsers, getSelf } from '../../../../../../../../api/CommonApi';
import './Field.scss';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldStatus extends Component {
  debounceFilterIssues = _.debounce((input) => {
    this.loadUsers(input);
  }, 500);

  constructor(props) {
    super(props);
    this.state = {
      originUsers: [],
      selectLoading: true,
      newReporterId: undefined,
    };
  }

  componentDidMount() {
    this.init();
  }

  init = () => {
    const { store } = this.props;
    const issue = store.getIssue;
    const { reporterId } = issue;
    this.setState({
      newReporterId: reporterId,
    });
    this.loadUsers();
  };

  loadUsers = (input) => {
    this.setState({
      selectLoading: true,
    });
    getUsers(input).then((res) => {
      this.setState({
        originUsers: res.content,
        selectLoading: false,
      });
    });
  };

  onFilterChange = (input) => {
    this.debounceFilterIssues(input);
  };

  updateIssueReporter = () => {
    const { newReporterId } = this.state;
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { issueId, objectVersionNumber, reporterId } = issue;
    if (reporterId !== newReporterId) {
      const obj = {
        issueId,
        objectVersionNumber,
        reporterId: newReporterId || 0,
      };
      updateIssue(obj)
        .then(() => {
          if (onUpdate) {
            onUpdate();
          }
          if (reloadIssue) {
            reloadIssue(issueId);
          }
        });
    }
  };

  render() {
    const { selectLoading, originUsers } = this.state;
    const { store, loginUserId, hasPermission } = this.props;
    const issue = store.getIssue;
    const {
      reporterId, reporterName, reporterImageUrl,
      reporterLoginName, reporterRealName,
    } = issue;
    const targetUser = _.find(originUsers, { id: reporterId, enabled: true });
    if (!targetUser && reporterId) {
      originUsers.unshift({
        id: reporterId,
        loginName: reporterLoginName,
        realName: reporterRealName,
        imageUrl: reporterImageUrl,
        enabled: true,
      });
    }

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'报告人：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            disabled={reporterId !== loginUserId && !hasPermission}
            formKey="reporter"
            onSubmit={this.updateIssueReporter}
            originData={reporterId || []}
            className="reporter"
          >
            <Text>
              {
                reporterId && reporterName ? (
                  <UserHead
                    user={{
                      id: reporterId,
                      loginName: reporterLoginName,
                      realName: reporterRealName,
                      avatar: reporterImageUrl,
                    }}
                  />
                ) : (
                  <div>
                    {'无'}
                  </div>
                )
              }
            </Text>
            <Edit>
              <Select
                style={{ width: 150 }}
                loading={selectLoading}
                allowClear
                filter
                filterOption={false}
                onFilterChange={this.onFilterChange.bind(this)}
                getPopupContainer={triggerNode => triggerNode.parentNode}
                onChange={(value) => {
                  this.setState({ newReporterId: value });
                }}
              >
                {originUsers.filter(u => u.enabled).map(user => (
                  <Option key={user.id} value={user.id}>
                    <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
                      <UserHead
                        user={{
                          id: user && user.id,
                          loginName: user && user.loginName,
                          realName: user && user.realName,
                          avatar: user && user.imageUrl,
                        }}
                      />
                    </div>
                  </Option>
                ))}
              </Select>
            </Edit>
          </TextEditToggle>
          {reporterId !== loginUserId && hasPermission
            ? (
              <span
                role="none"
                style={{
                  color: '#3f51b5',
                  cursor: 'pointer',
                  marginTop: '-3px',
                  margin: '-3px 0 0 10px',
                  display: 'inline-block',
                }}
                onClick={() => {
                  getSelf().then((res) => {
                    if (res.id !== reporterId) {
                      this.setState({
                        newReporterId: res.id,
                      }, () => {
                        this.updateIssueReporter();
                      });
                    }
                  });
                }}
              >
                {'分配给我'}
              </span>
            ) : ''
          }
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldStatus));
