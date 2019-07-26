import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import SelectFocusLoad from '../../../../SelectFocusLoad';
import UserHead from '../../../../UserHead';
import { updateIssue } from '../../../../../api/NewIssueApi';
import { getSelf } from '../../../../../api/CommonApi';
import './Field.scss';


const { Text, Edit } = TextEditToggle;

@inject('AppState')
@observer class FieldStatus extends Component { 
  updateIssueAssignee = (assigneeId) => { 
    const { store, onUpdate, reloadIssue } = this.props;
    const issue = store.getIssue;
    const { issueId, objectVersionNumber } = issue;
 
    const obj = {
      issueId,
      objectVersionNumber,
      assigneeId: assigneeId || 0,
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
  };

  render() {  
    const { store, loginUserId, disabled } = this.props;
    const issue = store.getIssue;
    const {
      assigneeId, assigneeImageUrl,
      assigneeLoginName, assigneeRealName,
    } = issue;
    return (
      <div className="line-start mt-10">
        <div className="c7n-property-wrapper">
          <span className="c7n-property">
            {'经办人：'}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ display: 'flex' }}>
          <TextEditToggle
            disabled={disabled}
            formKey="assignee"
            onSubmit={this.updateIssueAssignee}
            originData={assigneeId || []}
            className="assignee"
            style={{ flex: 1 }}
          >
            <Text>
              {
                assigneeId ? (
                  <UserHead
                    user={{
                      id: assigneeId,
                      loginName: assigneeLoginName,
                      realName: assigneeRealName,
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
              <SelectFocusLoad
                type="user"
                defaultOption={{
                  id: assigneeId,
                  loginName: assigneeLoginName,
                  realName: assigneeRealName,
                  avatar: assigneeImageUrl,
                }}                       
                defaultOpen
                allowClear                
                dropdownStyle={{ width: 250 }}
                dropdownMatchSelectWidth={false}                
                getPopupContainer={() => document.getElementById('detail')}               
              />
            </Edit>
          </TextEditToggle>
          {assigneeId !== loginUserId && !disabled
            ? (
              <span
                role="none"
                style={{
                  color: '#3f51b5',
                  cursor: 'pointer',
                  marginLeft: '10px',
                  marginRight: 10,
                  display: 'inline-block',
                  verticalAlign: 'middle',
                }}
                onClick={() => {
                  getSelf().then((res) => {
                    if (res.id !== assigneeId) {                      
                      this.updateIssueAssignee(res.id);                    
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
