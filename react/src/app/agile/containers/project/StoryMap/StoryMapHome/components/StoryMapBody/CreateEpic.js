import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import Card from './Card';
import { createIssue, createIssueField } from '../../../../../../api/NewIssueApi';
import { getProjectId } from '../../../../../../common/utils';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';

class CreateEpic extends Component {
  handleBlur = (e) => {
    // console.log(e.target.value);
    const { value } = e.target;
    if (value !== '') {
      const { onCreate } = this.props;
      const epicType = StoryMapStore.getEpicType;
      const defaultPriority = StoryMapStore.getDefaultPriority;
      const req = {
        projectId: getProjectId(),
        epicName: value,
        summary: '',
        typeCode: 'issue_epic',
        issueTypeId: epicType.id,
        priorityCode: `priority-${defaultPriority.id}`,
        priorityId: defaultPriority.id,
      };
      createIssue(req).then((res) => {
        const dto = {
          schemeCode: 'agile_issue',
          context: res.typeCode,
          pageCode: 'agile_issue_create',
        };
        onCreate(res);
        createIssueField(res.issueId, dto);
      });
    }
  }

  render() {
    return (
      <Card style={{
        boxShadow: '0 0 4px -2px rgba(0,0,0,0.50), 0 2px 4px 0 rgba(0,0,0,0.13)',
        borderRadius: 2,
        height: 42,
        margin: '4px 4px 4px 9px',
        padding: 7,
        display: 'flex',
        justifyContent: 'center',
      }}
      >
        <Input autoFocus onBlur={this.handleBlur} placeholder="在此创建史诗" maxLength="22" />
      </Card>
    );
  }
}

CreateEpic.propTypes = {

};

export default CreateEpic;
