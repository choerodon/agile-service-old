import React, { Component } from 'react';
import { withRouter } from 'react-router-dom';
import { observer, inject } from 'mobx-react';
import {
  Button, Input, Dropdown, Menu, Icon,
} from 'choerodon-ui';
import { axios, stores } from '@choerodon/boot';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import { createIssue, loadPriorities, createIssueField } from '../../../../api/NewIssueApi';
import TypeTag from '../../../../components/TypeTag';
import IssueFilterControler from '../IssueFilterControler';
import { QuickSearchEvent } from '../../../../components/QuickSearch';
import IsInProgramStore from '../../../../stores/common/program/IsInProgramStore';

const { AppState } = stores;
@withRouter
@observer
class QuickCreateIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      checkCreateIssue: false,
      createLoading: false,
      createIssueValue: '',
      selectIssueType: 'task',
    };
  }

  componentWillMount() {
    loadPriorities().then((res) => {
      if (res && res.length) {
        const defaultPriorityId = !!res.find(p => p.default) && res.find(p => p.default).id;
        IssueStore.setDefaultPriorityId(defaultPriorityId);
      } else {
        IssueStore.setDefaultPriorityId('');
      }
    });
  }

  handleBlurCreateIssue = () => {
    const createIssueValue = this.inputvalue.input.value;
    const { selectIssueType, featureType } = this.state;
    const currentType = IssueStore.getIssueTypes.find(t => t.typeCode === selectIssueType);
    if (createIssueValue && createIssueValue.trim() && createIssueValue !== '') {
      const { history } = this.props;
      const {
        type, id, name, organizationId,
      } = AppState.currentMenuType;
      const data = {
        priorityCode: `priority-${IssueStore.getDefaultPriorityId}`,
        priorityId: IssueStore.getDefaultPriorityId,
        projectId: id,
        sprintId: 0,
        summary: createIssueValue.trim(),
        issueTypeId: currentType.id,
        typeCode: currentType.typeCode,
        epicId: 0,
        epicName: selectIssueType === 'issue_epic' ? createIssueValue.trim() : undefined,
        parentIssueId: 0,
        featureVO: {
          // benfitHypothesis: values.benfitHypothesis,
          // acceptanceCritera: values.acceptanceCritera,
          featureType,
        },
      };
      this.setState({
        createLoading: true,
      });
      createIssue(data)
        .then((response) => {
          const dto = {
            schemeCode: 'agile_issue',
            context: response.typeCode,
            pageCode: 'agile_issue_create',
          };
          createIssueField(response.issueId, dto);
          this.filterControler = IssueFilterControler;
          this.filterControler.resetCacheMap();
          IssueStore.setLoading(true);
          IssueStore.resetFilterSelect(this.filterControler, true);
          // quickSearch.clearQuickSearch();
          QuickSearchEvent.emitEvent('clearQuickSearchSelect');
          this.filterControler.refresh('refresh').then((resRefresh) => {
            IssueStore.refreshTrigger(resRefresh);
          });
          this.inputvalue.input.value = '';
          this.setState({
            checkCreateIssue: false,
            createLoading: false,
          });
          // history.push(`/agile/issue?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}&paramName=${response.issueNum}&paramIssueId=${response.issueId}&paramOpenIssueId=${response.issueId}`);
        })
        .catch((error) => {
        });
    }
  };

  handleChangeType = (type) => {
    this.setState({
      selectIssueType: type.key,
      featureType: type.item.props.featureType,
    });
  };

  getIssueTypes=() => {
    const createTypes = [];
    const issueTypes = IssueStore.getIssueTypes;
    issueTypes.forEach((type) => {
      const { typeCode } = type;
      if ((IsInProgramStore.isInProgram && ['issue_epic', 'feature'].includes(typeCode)) || ['sub_task'].includes(typeCode)) {
        return;
      }
      if (typeCode === 'feature') {
        createTypes.push({
          ...type,
          colour: '#29B6F6',
          featureType: 'business',
          name: '特性',
          id: 'business',
        });
        createTypes.push({
          ...type,
          colour: '#FFCA28',
          featureType: 'enabler',
          name: '使能',
          id: 'enabler',
        });
      } else {
        createTypes.push(type);
      } 
    });
    return createTypes;
  }

  render() {
    const {
      checkCreateIssue, createLoading, selectIssueType, createIssueValue, featureType,
    } = this.state;    
    const issueTypes = this.getIssueTypes();   
    const currentType = issueTypes.find(t => t.typeCode === selectIssueType && t.featureType === featureType);
    const typeList = (
      <Menu
        style={{
          background: '#fff',
          boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
          borderRadius: '2px',
        }}
        onClick={this.handleChangeType.bind(this)}
      >
        {
          issueTypes.map(type => (
            <Menu.Item key={type.typeCode} featureType={type.featureType}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <TypeTag
                  data={type}
                  showName
                />
              </div>
            </Menu.Item>
          ))
        }
      </Menu>
    );
    return (
      <div
        className="c7n-backlog-sprintIssue"
        style={{
          userSelect: 'none',
          background: 'white',
          fontSize: 13,
          display: 'flex',
          alignItems: 'center',
          borderBottom: '1px solid #e8e8e8',
        }}
      >
        {checkCreateIssue ? (
          <div className="c7n-add" style={{ display: 'block', width: '100%', marginTop: 8 }}>
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Dropdown overlay={typeList} trigger={['click']}>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <TypeTag
                    data={currentType}
                  />
                  <Icon
                    type="arrow_drop_down"
                    style={{ fontSize: 16 }}
                  />
                </div>
              </Dropdown>
              <div style={{ marginLeft: 8, flexGrow: 1 }}>
                <Input
                  autoFocus
                  className="hidden-label"
                  ref={(e) => { this.inputvalue = e; }}
                  placeholder="问题概要"
                  maxLength={44}
                  onPressEnter={this.handleBlurCreateIssue.bind(this)}
                />
              </div>         
              <Button
                type="primary"
                funcType="raised"
                style={{ margin: '0 10px' }}
                loading={createLoading}
                onClick={this.handleBlurCreateIssue.bind(this)}
              >
                {'确定'}
              </Button>                  
              <Button           
                funcType="raised"                
                onClick={() => {
                  this.setState({
                    checkCreateIssue: false,
                  });
                }}
              >
                {'取消'}
              </Button>              
            </div>
          </div>
        ) : (
          <Button
            className="leftBtn"
            style={{ color: '#3f51b5' }}
            funcType="flat"
            onClick={() => {
              this.setState({
                checkCreateIssue: true,
                createIssueValue: '',
              });
            }}
          >
            <Icon type="playlist_add icon" />
            <span>创建问题</span>
          </Button>
        )}
      </div>
    );
  }
}

export default QuickCreateIssue;
