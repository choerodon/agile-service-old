import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Input } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { updateIssue } from '../../../../../api/NewIssueApi';

const { Text, Edit } = TextEditToggle;
const { TextArea } = Input;

@inject('AppState')
@observer class FieldSummary extends Component {
  constructor(props) {
    super(props);
    this.TextEditToggle = undefined;
    this.state = {
      newValue: undefined,
    };
  }

  updateIssueField = () => {
    const { newValue } = this.state;
    const {
      store, onUpdate, reloadIssue, field, feature,
    } = this.props;
    const { fieldCode } = field;
    const issue = store.getIssue;
    const {
      issueId, objectVersionNumber, [fieldCode]: value, featureVO = {},
    } = issue;
    const { id, objectVersionNumber: featureObjNum } = featureVO || {};
    if (value !== newValue.trim()) {
      let obj = false;
      if (feature) {
        obj = {
          issueId,
          objectVersionNumber,
          featureVO: {
            id,
            issueId,
            objectVersionNumber: featureObjNum,
            [fieldCode]: newValue.trim(),
          },
        };
      } else if (newValue.trim()) {
        obj = {
          issueId,
          objectVersionNumber,
          [fieldCode]: newValue.trim(),
        };
      }
      if (obj) {
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
    }
  };

  render() {
    const {
      store, field, feature, disabled,
    } = this.props;
    const { fieldCode } = field;
    const issue = store.getIssue;
    const { featureVO = {} } = issue;
    const value = feature ? featureVO[fieldCode] : issue[fieldCode];

    return (
      <div className="line-start" style={{ width: '100%', fontSize: 20, fontWeight: 500 }}>       
        <TextEditToggle
          style={{ width: '100%' }}
          disabled={disabled}
          saveRef={(e) => {
            this.TextEditToggle = e;
          }}
          formKey={fieldCode}
          onSubmit={this.updateIssueField}
          originData={value}
        >
          <Text>
            <div style={{ wordBreak: 'break-all' }}>
              {value || '无'}
            </div>
          </Text>
          <Edit>
            <TextArea
              autosize
              autoFocus
              maxLength="44"
              style={{ fontSize: '20px', fontWeight: 500, padding: '0.04rem' }}
              onChange={(e) => {
                this.setState({
                  newValue: e.target.value,
                });
              }}
              onPressEnter={() => {
                if (this.TextEditToggle && this.TextEditToggle.leaveEditing) {
                  this.updateIssueField();
                  this.TextEditToggle.leaveEditing();
                }
              }}
            />
          </Edit>
        </TextEditToggle>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldSummary));
