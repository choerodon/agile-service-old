/* eslint-disable camelcase */
import React from 'react';
import { Select } from 'choerodon-ui';
import { find } from 'lodash';
import User from '../User';
import { getUsers, getUser } from '../../api/CommonApi';
import {
  loadEpics, loadProgramEpics, loadIssueTypes, loadStatusList, 
} from '../../api/NewIssueApi';
import TypeTag from '../TypeTag';
import StatusTag from '../StatusTag';

const { Option } = Select;
const issue_type_program = {
  props: {
    filterOption:
      (input, option) => option.props.children
        && option.props.children.toLowerCase().indexOf(
          input.toLowerCase(),
        ) >= 0,
  },
  request: () => new Promise(resolve => loadIssueTypes('program').then((issueTypes) => {
    const defaultType = find(issueTypes, { typeCode: 'feature' }).id;
    resolve(issueTypes, defaultType);
  })),
  render: issueType => (
    <Option
      key={issueType.id}
      value={issueType.id}
      name={issueType.name}
    >
      <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
        <TypeTag
          data={issueType}
          showName
        />
      </div>
    </Option>
  ),
};
export default {
  user: {
    request: (...args) => new Promise(resolve => getUsers(...args).then((UserData) => { resolve(UserData.content); })),
    render: user => (
      <Option key={user.id} value={user.id}>
        <User user={user} />
      </Option>
    ),
    avoidShowError: (props, List) => new Promise((resolve) => {
      const { value } = props;
      const extraList = [];
      const values = value instanceof Array ? value : [value];
      const requestQue = [];
      values.forEach((a) => {
        if (a && !find(List, { id: a })) {
          requestQue.push(getUser(a));
        }
      }); 
      Promise.all(requestQue).then((users) => {
        users.forEach((res) => {
          if (res.content && res.content.length > 0) {
            extraList.push(res.content[0]);
          }
        });
        resolve(extraList);
      }).catch((err) => {        
        resolve(extraList);
      });
    }),
  },
  status_program: {
    request: () => new Promise(resolve => loadStatusList('program').then((statusList) => {      
      resolve(statusList);
    })),
    render: status => (
      <Option
        key={status.id}
        value={status.id}
        name={status.name}
      >
        <div style={{ display: 'inline-flex', alignItems: 'center', padding: '2px' }}>
          <StatusTag
            data={status}            
          />
        </div>
      </Option>
    ),
  },
  epic: {
    props: {
      filterOption:
        (input, option) => option.props.children
          && option.props.children.toLowerCase().indexOf(
            input.toLowerCase(),
          ) >= 0,
    },
    request: loadEpics,
    render: epic => (
      <Option
        key={epic.issueId}
        value={epic.issueId}
      >
        {epic.epicName}
      </Option>
    ),
  },
  epic_program: {
    props: {
      filterOption:
        (input, option) => option.props.children
          && option.props.children.toLowerCase().indexOf(
            input.toLowerCase(),
          ) >= 0,
    },
    request: loadProgramEpics,
    render: epic => (
      <Option
        key={epic.issueId}
        value={epic.issueId}
      >
        {epic.epicName}
      </Option>
    ),
  },
  issue_type_program,
  issue_type_program_simple: {
    ...issue_type_program,
    render: issueType => (
      <Option
        key={issueType.id}
        value={issueType.id}
        name={issueType.name}
      >
        {issueType.name}
      </Option>
    ),
  },
  issue_type_program_feature_epic: {
    ...issue_type_program,
    request: () => new Promise(resolve => loadIssueTypes('program').then((issueTypes) => {
      const featureTypes = [{
        id: 'business',
        name: '特性',
      }, {
        id: 'enabler',
        name: '使能',
      }];
      const epicType = find(issueTypes, { typeCode: 'issue_epic' });
      resolve([...featureTypes, epicType]);
    })),
    render: issueType => (
      <Option
        key={issueType.id}
        value={issueType.id}
        name={issueType.name}
      >
        {issueType.name}
      </Option>
    ),
  },
};
