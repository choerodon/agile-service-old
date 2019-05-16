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
      newAssigneeId: undefined,
    };
  }

  componentDidMount() {
    this.init();
  }

  init = () => {
    const { store } = this.props;
    const issue = store.getIssue;
    const { assigneeId } = issue;
    this.setState({
      newAssigneeId: assigneeId,
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

  updateIssueAssignee = () => {
    const { newAssigneeId } = this.state;
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { issueId, objectVersionNumber, assigneeId } = issue;
    if (assigneeId !== newAssigneeId) {
      const obj = {
        issueId,
        objectVersionNumber,
        assigneeId: newAssigneeId || 0,
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
    const { store, loginUserId } = this.props;
    const issue = store.getIssue;
    const { assigneeId, assigneeName, assigneeImageUrl } = issue;
    const targetUser = _.find(originUsers, { id: assigneeId, enabled: true });
    if (!targetUser && assigneeId) {
      originUsers.unshift({
        id: assigneeId,
        loginName: '',
        realName: assigneeName,
        imageUrl: assigneeImageUrl,
        enabled: true,
      });
    }

    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'经办人：'}
          </span>
        </div>
        <div className="c7n-value-wrapper">
          <TextEditToggle
            formKey="assignee"
            onSubmit={this.updateIssueAssignee}
            originData={assigneeId || []}
            className="assignee"
          >
            <Text>
              {
                assigneeId && assigneeName ? (
                  <UserHead
                    user={{
                      id: assigneeId,
                      loginName: '',
                      realName: assigneeName,
                      avatar: assigneeImageUrl,
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
                  this.setState({ newAssigneeId: value });
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
          {assigneeId !== loginUserId
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
                    if (res.id !== assigneeId) {
                      this.setState({
                        newAssigneeId: res.id,
                      }, () => {
                        this.updateIssueAssignee();
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
