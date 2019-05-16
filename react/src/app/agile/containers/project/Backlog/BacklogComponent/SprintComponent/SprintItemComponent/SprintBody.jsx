import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Droppable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../../../stores/project/backlog/BacklogStore';
import QuickCreateIssue from './QuickCreateIssue';
import IssueList from './IssueList';
import { deBounce } from '../Utils';
import { getFeaturesInProject } from '../../../../../../api/FeatureApi';
import { createIssueField } from '../../../../../../api/NewIssueApi';

const debounceCallback = deBounce(500);
/**
   * 加载版本数据
   */
const loadVersion = () => {
  BacklogStore.axiosGetVersion().then((data2) => {
    const newVersion = [...data2];
    for (let index = 0, len = newVersion.length; index < len; index += 1) {
      newVersion[index].expand = false;
    }
    BacklogStore.setVersionData(newVersion);
  }).catch(() => {
  });
};

/**
   * 加载史诗
   */
const loadEpic = () => { 
  BacklogStore.axiosGetEpic().then((data3) => {
    const newEpic = [...data3];
    for (let index = 0, len = newEpic.length; index < len; index += 1) {
      newEpic[index].expand = false;
    }
    BacklogStore.setEpicData(newEpic);
  }).catch(() => {
  });
};

/**
   * 加载特性
   */
const loadFeature = () => {
  getFeaturesInProject().then((data) => {
    BacklogStore.setFeatureData(data);
  }).catch(() => {
  });
};
@inject('AppState')
@observer class SprintBody extends Component {
  handleCreateIssue(currentType, inputValue) {
    const {
      defaultPriority, AppState, sprintId, defaultType,
    } = this.props;
    // 防抖函数
    debounceCallback(() => {
      const req = {
        priorityCode: `priority-${defaultPriority.id}`,
        priorityId: defaultPriority.id,
        projectId: AppState.currentMenuType.id,
        sprintId: sprintId * 1,
        summary: inputValue,
        issueTypeId: currentType ? currentType.id : defaultType.id,
        typeCode: currentType ? currentType.typeCode : defaultType.typeCode,
        /* eslint-disable */
        ...!isNaN(BacklogStore.getChosenEpic) ? {
          epicId: BacklogStore.getChosenEpic,
        } : {},
        ...!isNaN(BacklogStore.getChosenVersion) ? {
          versionIssueRelDTOList: [
            {
              versionId: BacklogStore.getChosenVersion,
            },
          ],
        } : {},
        parentIssueId: 0,
      };
      BacklogStore.axiosEasyCreateIssue(req).then((res) => {
        BacklogStore.clickedOnce(sprintId, res);
        this.setState({
          expand: false,
          loading: false,
        });
        const dto = {
          schemeCode: 'agile_issue',
          context: res.typeCode,
          pageCode: 'agile_issue_create',
        };
        createIssueField(res.issueId, dto);
        // if (BacklogStore.getCurrentVisible === 'version') {
        //   loadVersion();
        // } else if (BacklogStore.getCurrentVisible === 'epic') {
        //   loadEpic();
        // } else if (BacklogStore.getCurrentVisible === 'feature') {
        //   loadFeature();
        // }
        BacklogStore.createIssue({
          ...res,
          imageUrl: res.assigneeImageUrl,
          versionIds: res.versionIssueRelDTOList.length ? [res.versionIssueRelDTOList[0].versionId] : [],
          versionNames: res.versionIssueRelDTOList.length ? [res.versionIssueRelDTOList[0].name] : [],
        }, sprintId);
      }).catch((error) => {
        this.setState({
          loading: false,
        });
      });
    }, this);
    /* eslint-enable */
  }

  render() {
    const {
      expand, versionVisible, epicVisible,
      issueCount, sprintId, EmptyIssueComponent,
      defaultType, issueType, defaultPriority,
    } = this.props;

    return (
      <Droppable
        droppableId={sprintId}
        isDropDisabled={BacklogStore.getIssueCantDrag}
      >
        {(provided, snapshot) => (
          <div
            ref={provided.innerRef}
            style={{
              display: expand ? 'block' : 'none',
              background: snapshot.isDraggingOver ? '#e9e9e9' : 'inherit',
              padding: 'grid',
              borderBottom: '1px solid rgba(0,0,0,0.12)',
            }}
          >
            {issueCount ? (
              <IssueList
                sprintItemRef={this.sprintItemRef}
                versionVisible={versionVisible}
                epicVisible={epicVisible}
                sprintId={sprintId}
              />
            ) : <EmptyIssueComponent />
              }
            {provided.placeholder}
            <QuickCreateIssue
              defaultPriority={defaultPriority}
              sprintId={sprintId}
              issueType={issueType}
              defaultType={defaultType}
              handleCreateIssue={this.handleCreateIssue}
            />
          </div>
        )}
      </Droppable>
    );
  }
}

export default SprintBody;
