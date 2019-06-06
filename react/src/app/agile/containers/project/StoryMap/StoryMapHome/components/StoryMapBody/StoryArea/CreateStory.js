import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Input } from 'choerodon-ui';
import Card from '../Card';
import './CreateStory.scss';
import { createIssue, createIssueField } from '../../../../../../../api/NewIssueApi';
import { getProjectId } from '../../../../../../../common/utils';
import StoryMapStore from '../../../../../../../stores/project/StoryMap/StoryMapStore';

class CreateStory extends Component {
  state = {
    adding: false,
  }

  handleBlur = (e) => {   
    const { value } = e.target;
    if (value) {
      const { swimLine } = StoryMapStore;
      const {
        onCreate, epic, feature, version, 
      } = this.props;
      const storyType = StoryMapStore.getIssueTypeByCode('story');
      const defaultPriority = StoryMapStore.getDefaultPriority;
      const req = {
        epicId: epic.issueId,
        featureId: feature.issueId === 'none' ? 0 : feature.issueId,
        projectId: getProjectId(),      
        summary: value,
        typeCode: 'story',
        issueTypeId: storyType.id,
        priorityCode: `priority-${defaultPriority.id}`,
        priorityId: defaultPriority.id,
        ...swimLine === 'version' ? {
          versionIssueRelDTOList: [{
            ...version,
            relationType: 'fix',
          }],
        } : {},
      };
      createIssue(req).then((res) => {
        const dto = {
          schemeCode: 'agile_issue',
          context: res.typeCode,
          pageCode: 'agile_issue_create',
        };
        this.setState({
          adding: false,
        });
        const { versionIssueRelDTOList } = res;
        onCreate({ ...res, storyMapVersionDOList: versionIssueRelDTOList });
        createIssueField(res.issueId, dto);
      });
    } else {
      this.setState({
        adding: false,
      });
    }
  }

  handleAddStoryClick=() => {
    this.setState({
      adding: true,
    });
  }

  render() {
    const { adding } = this.state;
    return (
      <Card
        style={{
          boxShadow: adding ? '0 0 4px -2px rgba(0,0,0,0.50), 0 2px 4px 0 rgba(0,0,0,0.13)' : '',
          borderRadius: 2,
          padding: 7,
          display: 'flex',
          justifyContent: 'center',
        }}
        className="c7nagile-StoryMap-CreateStory"
      >
        {
          adding 
            ? <Input autoFocus onBlur={this.handleBlur} placeholder="在此创建新内容" />
            : (
              <div className="c7nagile-StoryMap-CreateStory-btn">
                <span role="none" style={{ cursor: 'pointer', color: '#3F51B5' }} onClick={this.handleAddStoryClick}>新建问题</span>
                {' '}
                或
                {' '}
                <span style={{ cursor: 'pointer', color: '#3F51B5' }}>从需求池引入</span>
              </div>
            )
        }
      </Card>
    );
  }
}

CreateStory.propTypes = {

};

export default CreateStory;
