/* eslint-disable camelcase */
import React from 'react';
import { Select } from 'choerodon-ui';
import { find } from 'lodash';
import UserHead from '../UserHead';
import { getUsers, getUser } from '../../api/CommonApi';
import {
  loadEpics, loadProgramEpics, loadIssueTypes, loadPriorities,
  loadComponents, loadLabels, loadVersions,
  loadStatusList, loadIssuesInLink, loadSprints,

} from '../../api/NewIssueApi';
import { getPISelect } from '../../api/PIApi';
import IssueLinkType from '../../api/IssueLinkType';
import TypeTag from '../TypeTag';
import StatusTag from '../StatusTag';

const filterOption = (input, option) => option.props.children && option.props.children.toLowerCase().indexOf(
  input.toLowerCase(),
) >= 0;

function transform(links) {
  // split active and passive
  const active = links.map(link => ({
    name: link.outWard,
    isIn: false,
    linkTypeId: link.linkTypeId,
  }));
  const passive = [];
  links.forEach((link) => {
    if (link.inWard !== link.outWard) {
      passive.push({
        name: link.inWard,
        isIn: true,
        linkTypeId: link.linkTypeId,
      });
    }
  });

  return active.concat(passive);
}
const { Option } = Select;
const issue_type_program = {
  props: {
    filterOption,
  },
  request: () => new Promise(resolve => loadIssueTypes('program').then((issueTypes) => {
    // const defaultType = find(issueTypes, { typeCode: 'feature' }).id;
    resolve(issueTypes);
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
    request: (...args) => new Promise(resolve => getUsers(...args).then((UserData) => { resolve(UserData.list.filter(user => user.enabled)); })),
    render: user => (
      <Option key={user.id} value={user.id}>
        <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
          <UserHead
            user={user}
          />
        </div>
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
          if (res.list && res.list.length > 0) {
            extraList.push(res.list[0]);
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
      filterOption,
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
      getPopupContainer: triggerNode => triggerNode.parentNode,
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
  issue_link: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,
      filter: false,
      filterOption: false,
      loadWhenMount: true,
    },
    request: () => new Promise(resolve => IssueLinkType.queryAll().then((res) => { resolve(transform(res.list)); })),
    render: link => (
      <Option value={`${link.linkTypeId}+${link.isIn}`}>
        {link.name}
      </Option>
    ),
  },
  issues_in_link: {
    props: {
      mode: 'multiple',
      optionLabelProp: 'showName',
      getPopupContainer: triggerNode => triggerNode.parentNode,
    },
    request: input => new Promise(resolve => loadIssuesInLink(1, 20, undefined, input).then((res) => { resolve(res.list); })),
    render: issue => (
      <Option
        key={issue.issueId}
        value={issue.issueId}
        showName={issue.issueNum}
      >
        <div style={{
          display: 'inline-flex',
          flex: 1,
          width: 'calc(100% - 30px)',
          alignItems: 'center',
          verticalAlign: 'middle',
        }}
        >
          <TypeTag
            data={issue.issueTypeVO}
          />
          <span style={{
            paddingLeft: 12, paddingRight: 12, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
          }}
          >
            {issue.issueNum}
          </span>
          <div style={{ overflow: 'hidden', flex: 1 }}>
            <p style={{
              paddingRight: '25px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', marginBottom: 0, maxWidth: 'unset',
            }}
            >
              {issue.summary}
            </p>
          </div>
        </div>
      </Option>
    ),
  },
  priority: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,
      filter: false,
      filterOption: false,
      loadWhenMount: true,
    },
    request: loadPriorities,
    getDefaultValue: priorities => find(priorities, { default: true }).id,
    render: priority => (
      <Option key={priority.id} value={priority.id}>
        <div style={{ display: 'inline-flex', alignItems: 'center', padding: 2 }}>
          <span>{priority.name}</span>
        </div>
      </Option>
    ),
  },
  component: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,
      filter: false,
      filterOption: false,
      loadWhenMount: true,
    },
    request: input => new Promise(resolve => loadComponents(input).then((res) => { resolve(res.list); })),
    render: component => (
      <Option
        key={component.name}
        value={component.name}
      >
        {component.name}
      </Option>
    ),
  },
  label: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,
      filter: false,
      filterOption: false,
      loadWhenMount: true,
    },
    request: loadLabels,
    render: label => (
      <Option key={label.labelName} value={label.labelName}>
        {label.labelName}
      </Option>
    ),
  },
  version: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,
      filter: false,
      filterOption: false,
      loadWhenMount: true,
    },
    request: () => new Promise(resolve => loadVersions(['version_planning']).then((res) => { resolve(res); })),
    render: version => (
      <Option
        key={version.versionId}
        value={version.versionId}
      >
        {version.name}
      </Option>
    ),
  },
  sprint: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,     
      filterOption,
      loadWhenMount: true,
    },
    request: () => new Promise(resolve => loadSprints(['sprint_planning', 'started']).then((res) => { resolve(res); })),
    render: sprint => (
      <Option key={sprint.sprintId} value={sprint.sprintId}>
        {sprint.sprintName}
      </Option>
    ),
  },
  pi: {
    props: {
      getPopupContainer: triggerNode => triggerNode.parentNode,     
      filterOption,
      loadWhenMount: true,
    },
    request: getPISelect,
    render: pi => (
      <Option key={pi.id} value={pi.id}>
        {`${pi.code}-${pi.name}`}
      </Option>    
    ),
  },
};
