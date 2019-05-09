import React, { Component } from 'react';
import {
  Input, Icon, Menu, Dropdown, Spin,
} from 'choerodon-ui';
import './CreateIssue.scss';
import TypeTag from '../../../../../components/TypeTag';
import clickOutSide from '../../../../../components/CommonComponent/ClickOutSide';
import { createIssue, createIssueField } from '../../../../../api/NewIssueApi';

const { TextArea } = Input;
const filterIssueTypeCode = ['issue_epic', 'sub_task', 'feature'];

class CreateIssue extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectIssueType: 'task',
      summary: '',
      loading: false,
    };
    this.couldCreate = true;
  }

  handleClickOutside = (e) => {
    this.handleCreateIssue();
  };

  handlePresEnter = (e) => {
    e.preventDefault();
    this.handleCreateIssue();
  };

  handleCreateIssue = () => {
    const { summary, selectIssueType } = this.state;
    const {
      onCancel, onOk, data, store,
    } = this.props;
    const defaultPriorityId = store.getDefaultPriority ? store.getDefaultPriority.id : '';
    const issueTypes = store.getIssueTypes || [];
    const currentType = issueTypes.find(t => t.typeCode === selectIssueType);
    if (!summary && onCancel) {
      onCancel();
    } else if (this.couldCreate) {
      this.couldCreate = false;
      const issue = {
        epicId: data.epicId,
        parentIssueId: 0,
        sprintId: data.sprintId,
        summary,
        typeCode: selectIssueType,
        issueTypeId: currentType && currentType.id,
        priorityCode: `priority-${defaultPriorityId}`,
        priorityId: defaultPriorityId,
        versionIssueRelDTOList: data.versionId ? [{
          relationType: 'fix',
          versionId: data.versionId,
        }] : undefined,
      };
      this.setState({ loading: true });
      createIssue(issue)
        .then((res) => {
          const dto = {
            schemeCode: 'agile_issue',
            context: res.typeCode,
            pageCode: 'agile_issue_create',
          };
          createIssueField(res.issueId, dto);
          onOk(res);
        })
        .catch(() => {
          this.setState({ loading: false });
          this.couldCreate = true;
        });
    } else {
      // waiting
    }
  }

  handleChangeSummary = (e) => {
    this.setState({ summary: e.target.value });
  };

  handleChangeType({ key }) {
    this.setState({ selectIssueType: key });
  }

  render() {
    const { style, store } = this.props;
    const { selectIssueType, summary, loading } = this.state;
    const issueTypes = store.getIssueTypes
      .filter(t => filterIssueTypeCode.indexOf(t.typeCode) === -1);
    const currentType = issueTypes.find(t => t.typeCode === selectIssueType);
    const typeList = (
      <Menu
        className="ignore-react-onclickoutside"
        style={{
          background: '#fff',
          boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
          borderRadius: '2px',
        }}
        onClick={this.handleChangeType.bind(this)}
      >
        {
          issueTypes.map(type => (
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
      <div className="c7n-userMap-createIssue" style={{ ...style }}>
        <Spin spinning={loading}>
          <div className="c7n-content">
            <TextArea
              autoFocus
              value={summary}
              onChange={this.handleChangeSummary}
              onPressEnter={this.handlePresEnter}
              className="c7n-textArea"
              autosize={{ minRows: 3, maxRows: 3 }}
              placeholder="在此创建新内容"
              maxLength={44}
            />
          </div>
          <div className="c7n-footer">
            <Dropdown
              overlay={typeList}
              trigger={['click']}
              getPopupContainer={triggerNode => triggerNode}
            >
              <div style={{ display: 'flex', alignItem: 'center' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <TypeTag
                    data={currentType}
                  />
                </div>
                <Icon
                  type="arrow_drop_down"
                  style={{ fontSize: 16, lineHeight: '20px' }}
                />
              </div>
            </Dropdown>
          </div>
        </Spin>
      </div>
    );
  }
}
export default clickOutSide(CreateIssue);
