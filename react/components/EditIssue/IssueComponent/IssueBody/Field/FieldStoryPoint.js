import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { Select } from 'choerodon-ui';
import { injectIntl } from 'react-intl';
import TextEditToggle from '../../../../TextEditToggle';
import { updateIssue } from '../../../../../api/NewIssueApi';
import SelectNumber from '../../../../SelectNumber';

const { Option } = Select;
const { Text, Edit } = TextEditToggle;
const defaultList = ['0.5', '1', '2', '3', '4', '5', '8', '13'];

@inject('AppState')
@observer class FieldStoryPoint extends Component {
  updateIssueField = (value) => {  
    const {
      store, onUpdate, reloadIssue, field,
    } = this.props;
    const issue = store.getIssue;
    const { fieldCode } = field;

    const {
      issueId, objectVersionNumber,
    } = issue;
    const obj = {
      issueId,
      objectVersionNumber,
      [fieldCode]: value === '' ? null : value,
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
    const { store, field, disabled } = this.props;
    const issue = store.getIssue;
    const { fieldCode, fieldName } = field;
    const { [fieldCode]: value } = issue;

    return (
      <div className="line-start mt-10" style={{ width: '100%' }}>
        <div>
          <span className="c7n-property">
            {`${fieldName}：`}
          </span>
        </div>
        <div className="c7n-value-wrapper" style={{ width: '80px' }}>
          <TextEditToggle
            formKey={fieldName}
            disabled={disabled}
            onSubmit={this.updateIssueField}
            originData={value ? String(value) : undefined}
          >
            <Text>
              <div style={{ whiteSpace: 'nowrap' }}>
                {value ? `${value} ${fieldCode === 'storyPoints' ? '点' : '小时'}` : '无'}
              </div>
            </Text>
            <Edit>
              <SelectNumber getPopupContainer={() => document.body} />
            </Edit>
          </TextEditToggle>
        </div>
      </div>
    );
  }
}

export default withRouter(injectIntl(FieldStoryPoint));
