import React, { Component, useContext } from 'react';
import { toJS } from 'mobx';
import { observer } from 'mobx-react-lite';
import {
  Field, FieldAssignee, FieldVersion, FieldStatus, FieldSprint, FieldText,
  FieldReporter, FieldPriority, FieldLabel, FieldFixVersion, FieldPI,
  FieldEpic, FieldDateTime, FieldComponent, FieldTimeTrace, FieldStoryPoint,
  FieldSummary, FieldInput,
} from './Field';
import EditIssueContext from '../../stores';

const hideFields = ['priority', 'component', 'label', 'fixVersion', 'sprint', 'timeTrace', 'assignee'];
const IssueField = (props) => {
  const { store, applyType } = useContext(EditIssueContext);
  const getFieldComponent = (field) => {  
    const issue = store.getIssue;
    const { typeCode } = issue;
    switch (field.fieldCode) {
      case 'assignee':
        return (<FieldAssignee {...props} />);
      case 'influenceVersion':
        return (<FieldVersion {...props} />);
      case 'status':
        return (<FieldStatus {...props} />);
      case 'sprint':
        if (typeCode !== 'sub_task') {
          return (<FieldSprint {...props} />);
        } else {
          return (<FieldSprint {...props} disabled />);
        }
      case 'reporter':
        return (<FieldReporter {...props} />);
      case 'priority':
        return (<FieldPriority {...props} />);
      case 'label':
        return (<FieldLabel {...props} />);
      case 'fixVersion':
        return (<FieldFixVersion {...props} />);
      case 'epic':
        // 子任务、史诗不显示史诗
        if (['issue_epic', 'sub_task'].indexOf(typeCode) === -1) {
          return (<FieldEpic {...props} />);
        }
        return '';
      case 'creationDate':
      case 'lastUpdateDate':
        return (<FieldDateTime {...props} field={field} />);
      case 'component':
        if (typeCode !== 'sub_task') {
          return (<FieldComponent {...props} />);
        }
        return '';
      case 'timeTrace':
        return (<FieldTimeTrace {...props} />);
      case 'pi':
        return (<FieldPI {...props} />);
      case 'benfitHypothesis':
      case 'acceptanceCritera':
        return (<FieldText {...props} field={field} feature />);
      case 'summary':
        return (<FieldSummary {...props} field={field} />);
      case 'epicName':
        return (<FieldInput {...props} field={field} />);
      case 'remainingTime':
      case 'storyPoints':
        return (<FieldStoryPoint {...props} field={field} />);
      default:
        return (<Field {...props} field={field} />);
    }
  };
  const issue = store.getIssue;    
  const { issueId, typeCode } = issue;
  let fields = applyType === 'program' ? toJS(store.getFields).filter(item => hideFields.indexOf(item.fieldCode) === -1) : toJS(store.getFields);
  // 系统字段单独控制是否显示
  if (typeCode === 'sub_task') {
    fields = fields.filter(field => ['component', 'epic'].indexOf(field.fieldCode) === -1);
  } else if (typeCode === 'issue_epic') {
    fields = fields.filter(field => field.fieldCode !== 'epic');
  }
  if (!store.detailShow) {
    fields = fields.slice(0, 4);
  }

  return (
    <div className="c7n-content-wrapper IssueField">
      { issueId ? fields.map(field => getFieldComponent(field)) : ''}
    </div>
  );
};

export default IssueField;
