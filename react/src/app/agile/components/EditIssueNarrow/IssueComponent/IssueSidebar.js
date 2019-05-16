import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Dropdown, Icon, Menu } from 'choerodon-ui';
import IssueNav from './IssueNav';
import TypeTag from '../../TypeTag';
import { updateIssueType, updateIssue } from '../../../api/NewIssueApi';
import './IssueComponent.scss';

@inject('AppState', 'HeaderStore')
@observer class SprintHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
    };
  }

  handleChangeType = (type) => {
    const {
      store, reloadIssue, onUpdate,
    } = this.props;
    const issue = store.getIssue;
    const {
      issueId, objectVersionNumber, summary,
    } = issue;
    const issueUpdateTypeDTO = {
      epicName: type.key === 'issue_epic' ? summary : undefined,
      issueId,
      objectVersionNumber,
      typeCode: type.key,
      issueTypeId: type.item.props.value,
    };
    updateIssueType(issueUpdateTypeDTO)
      .then(() => {
        if (reloadIssue) {
          reloadIssue(issueId);
        }
        if (onUpdate) {
          onUpdate();
        }
      });
  };

  render() {
    const {
      store, type = 'narrow',
    } = this.props;

    const issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
    const issue = store.getIssue;
    const { issueTypeId, typeCode } = issue;
    let issueTypes = [];
    const currentType = issueTypeData.find(t => t.id === issueTypeId);
    if (currentType) {
      issueTypes = issueTypeData.filter(t => (t.stateMachineId === currentType.stateMachineId
        && t.typeCode !== typeCode && t.typeCode !== 'sub_task' && t.typeCode !== 'feature'
      ));
    }

    const typeList = (
      <Menu
        style={{
          background: '#fff',
          boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
          borderRadius: '2px',
        }}
        className="issue-sidebar-types"
        onClick={this.handleChangeType}
      >
        {
          issueTypes.map(t => (
            <Menu.Item key={t.typeCode} value={t.id}>
              <TypeTag
                style={{ margin: 0 }}
                data={t}
                showName
              />
            </Menu.Item>
          ))
        }
      </Menu>
    );

    return (
      <div className="c7n-nav">
        {/* 转换类型 */}
        <div>
          <Dropdown overlay={typeList} trigger={['click']} disabled={typeCode === 'sub_task'}>
            <div
              className={type === 'narrow' ? 'issue-nav-narrow' : 'issue-nav-wide'}
            >
              <TypeTag
                data={currentType}
              />
              <Icon
                type="arrow_drop_down"
                style={{ fontSize: 16 }}
              />
            </div>
          </Dropdown>
        </div>
        {/* 锚点 */}
        <IssueNav typeCode={typeCode} />
      </div>
    );
  }
}

export default SprintHeader;
