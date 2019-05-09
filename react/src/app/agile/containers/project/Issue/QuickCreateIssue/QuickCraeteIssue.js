import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Button, Input, Dropdown, Menu, Icon,
} from 'choerodon-ui';
import { axios, stores } from 'choerodon-front-boot';
import IssueStore from '../../../../stores/project/sprint/IssueStore';
import { createIssue, loadPriorities, createIssueField } from '../../../../api/NewIssueApi';
import TypeTag from '../../../../components/TypeTag';
import IssueFilterControler from '../IssueFilterControler';
import { QuickSearchEvent } from '../../../../components/QuickSearch';

const { AppState } = stores;
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
    const { selectIssueType } = this.state;
    const currentType = IssueStore.getIssueTypes.find(t => t.typeCode === selectIssueType);
    if (createIssueValue !== '') {
      const { history } = this.props;
      const {
        type, id, name, organizationId,
      } = AppState.currentMenuType;
      axios.get(`/agile/v1/projects/${id}/project_info`)
        .then((res) => {
          const data = {
            priorityCode: `priority-${IssueStore.getDefaultPriorityId}`,
            priorityId: IssueStore.getDefaultPriorityId,
            projectId: id,
            sprintId: 0,
            summary: createIssueValue,
            issueTypeId: currentType.id,
            typeCode: currentType.typeCode,
            epicId: 0,
            epicName: selectIssueType === 'issue_epic' ? createIssueValue : undefined,
            parentIssueId: 0,
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
              this.filterControler = new IssueFilterControler();
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
              history.push(`/agile/issue?type=${type}&id=${id}&name=${encodeURIComponent(name)}&organizationId=${organizationId}&paramName=${response.issueNum}&paramIssueId=${response.issueId}&paramOpenIssueId=${response.issueId}`);
            })
            .catch((error) => {
            });
        });
    }
  };

  handleChangeType = (type) => {
    this.setState({
      selectIssueType: type.key,
    });
  };


  render() {
    const {
      checkCreateIssue, createLoading, selectIssueType, createIssueValue,
    } = this.state;
    let issueTypes = IssueStore.getIssueTypes;
    issueTypes = AppState.currentMenuType.category === 'PROGRAM' ? issueTypes : issueTypes.filter(item => item.typeCode !== 'feature');
    const currentType = issueTypes.find(t => t.typeCode === selectIssueType);
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
          issueTypes.filter(t => t.typeCode !== 'sub_task').map(type => (
            <Menu.Item key={type.typeCode}>
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
            <div style={{ display: 'flex' }}>
              <Dropdown overlay={typeList} trigger={['click']}>
                <div style={{ display: 'flex', alignItem: 'center' }}>
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
                  ref={(e) => { this.inputvalue = e; }}
                  placeholder="需要做什么？"
                  maxLength={44}
                  onPressEnter={this.handleBlurCreateIssue.bind(this)}
                />
              </div>
            </div>
            <div
              style={{
                marginTop: 10,
                display: 'flex',
                marginLeft: 32,
                justifyContent: !IssueStore.getExpand ? 'flex-start' : 'flex-end',
              }}
            >
              <Button
                type="primary"
                onClick={() => {
                  this.setState({
                    checkCreateIssue: false,
                  });
                }}
              >
                {'取消'}
              </Button>
              <Button
                type="primary"
                loading={createLoading}
                onClick={this.handleBlurCreateIssue.bind(this)}
              >
                {'确定'}
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
