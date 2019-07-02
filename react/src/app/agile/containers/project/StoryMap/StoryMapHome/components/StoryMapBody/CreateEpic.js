import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import Card from './Card';
import { createIssue, createIssueField } from '../../../../../../api/NewIssueApi';
import { getProjectId } from '../../../../../../common/utils';
import StoryMapStore from '../../../../../../stores/project/StoryMap/StoryMapStore';
import clickOutSide from '../../../../../../components/CommonComponent/ClickOutSide';

class CreateEpic extends Component {
  state = {
    value: '',
  }

  handleClickOutside = () => {
    this.handleCreateIssue();
  };

  handleCreateIssue = () => {
    // console.log(e.target.value);
    const { value } = this.state;
    if (value !== '') {
      const { onCreate, index } = this.props;
      const epicType = StoryMapStore.getEpicType;
      const defaultPriority = StoryMapStore.getDefaultPriority;
      const preEpic = StoryMapStore.getEpicList[index - 1];
      const req = {
        projectId: getProjectId(),
        epicName: value,
        summary: value,
        typeCode: 'issue_epic',
        issueTypeId: epicType.id,
        priorityCode: `priority-${defaultPriority.id}`,
        priorityId: defaultPriority.id,
        rankDTO: {
          projectId: getProjectId(),
          // objectVersionNumber: source.epicRankObjectVersionNumber, // 乐观锁     
          // issueId: source.issueId,
          type: 'epic',
          before: false, // 是否拖动到第一个  
          referenceIssueId: preEpic ? preEpic.issueId : 0,
        },
      };
      createIssue(req).then((res) => {
        if (res.failed) {
          if (res.code === 'error.epicName.exist') {
            Choerodon.prompt('史诗名称已存在');
          } else {
            Choerodon.prompt('创建失败');
          }
          return;
        }
        const dto = {
          schemeCode: 'agile_issue',
          context: res.typeCode,
          pageCode: 'agile_issue_create',
        };
        onCreate({ ...res, epicName: value });
        createIssueField(res.issueId, dto);
      });
    } else {
      StoryMapStore.removeAddingEpic();
    }
  }

  handleChange=(e) => {
    this.setState({
      value: e.target.value,
    });
  }

  render() {
    const { value } = this.state;
    return (
      <Card style={{
        boxShadow: '0 0 4px -2px rgba(0,0,0,0.50), 0 2px 4px 0 rgba(0,0,0,0.13)',
        borderRadius: 2,
        height: 64,
        margin: '4px 4px 4px 9px',
        padding: 7,
        display: 'flex',
        justifyContent: 'center',
      }}
      >
        <Input autoFocus onPressEnter={this.handleCreateIssue} placeholder="在此创建史诗" maxLength="22" value={value} onChange={this.handleChange} />
      </Card>
    );
  }
}

CreateEpic.propTypes = {

};

export default clickOutSide(CreateEpic);
