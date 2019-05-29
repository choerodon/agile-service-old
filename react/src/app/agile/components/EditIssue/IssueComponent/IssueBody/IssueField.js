import React, { Component, Fragment } from 'react';
import { toJS } from 'mobx';
import { observer, inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import { injectIntl } from 'react-intl';
import {
  Field, FieldAssignee, FieldVersion, FieldStatus, FieldSprint, FieldText,
  FieldReporter, FieldPriority, FieldLabel, FieldFixVersion, FieldPI,
  FieldEpic, FieldDateTime, FieldComponent, FieldTimeTrace, FieldStoryPoint,
  FieldSummary, FieldInput,
} from './Field';
import VisibleStore from '../../../../stores/common/visible/VisibleStore';

@inject('AppState')
@observer class IssueField extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  componentDidMount() {

  }

  getFieldComponent = (field) => {
    const { store } = this.props;
    const issue = store.getIssue;
    const { typeCode } = issue;
    switch (field.fieldCode) {
      case 'assignee':
        return (<FieldAssignee {...this.props} />);
      case 'influenceVersion':
        return (<FieldVersion {...this.props} />);
      case 'status':
        return (<FieldStatus {...this.props} />);
      case 'sprint':
        if (typeCode !== 'sub_task') {
          return (<FieldSprint {...this.props} />);
        } else {
          return (<FieldSprint {...this.props} disabled />);
        }
      case 'reporter':
        return (<FieldReporter {...this.props} />);
      case 'priority':
        return (<FieldPriority {...this.props} />);
      case 'label':
        return (<FieldLabel {...this.props} />);
      case 'fixVersion':
        return (<FieldFixVersion {...this.props} />);
      case 'epic':
        // 子任务、史诗不显示史诗
        if (['issue_epic', 'sub_task'].indexOf(typeCode) === -1) {
          return (<FieldEpic {...this.props} />);
        }
        return '';
      case 'creationDate':
      case 'lastUpdateDate':
        return (<FieldDateTime {...this.props} field={field} />);
      case 'component':
        if (typeCode !== 'sub_task') {
          return (<FieldComponent {...this.props} />);
        }
        return '';
      case 'timeTrace':
        return (<FieldTimeTrace {...this.props} />);
      case 'pi':
        return (<FieldPI {...this.props} />);
      case 'benfitHypothesis':
      case 'acceptanceCritera':
        return (<FieldText {...this.props} field={field} feature />);
      case 'summary':
        return (<FieldSummary {...this.props} field={field} />);
      case 'epicName':
        return (<FieldInput {...this.props} field={field} />);
      case 'remainingTime':
      case 'storyPoints':
        return (<FieldStoryPoint {...this.props} field={field} />);
      default:
        return (<Field {...this.props} field={field} />);
    }
  };

  render() {
    const { store, isWide = false } = this.props;
    const issue = store.getIssue;
    let fields = toJS(store.getFields);
    const { issueId } = issue;
    
    if (!VisibleStore.detailShow) {
      fields = fields.slice(0, 3);
    }

    return (
      <div className="c7n-content-wrapper IssueField">
        { issueId ? fields.map(field => this.getFieldComponent(field)) : ''}
      </div>
    );
  }
}

export default withRouter(injectIntl(IssueField));
