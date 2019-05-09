import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Dropdown, Icon, Menu } from 'choerodon-ui';
import IssueNav from './IssueNav';
import TypeTag from '../../../../../../components/TypeTag';
import { updateIssueType, updateIssue } from '../../../../../../api/NewIssueApi';
import '../../../../../../components/EditIssueNarrow/IssueComponent/IssueComponent.scss';

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
      issueTypeDTO = {}, issueId, objectVersionNumber, summary, featureDTO = {},
    } = issue;
    const { id, objectVersionNumber: featureObjNum } = featureDTO;
    const { typeCode } = issueTypeDTO;
    if (typeCode === 'feature') {
      const issueUpdateDTO = {
        issueId,
        objectVersionNumber,
        featureDTO: {
          id,
          issueId,
          objectVersionNumber: featureObjNum,
          featureType: type.item.props.value,
        },
      };
      updateIssue(issueUpdateDTO)
        .then(() => {
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          if (onUpdate) {
            onUpdate();
          }
        });
    } else {
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
    }
  };

  render() {
    const {
      store,
    } = this.props;

    let issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
    const issue = store.getIssue;
    const { issueTypeDTO = {}, featureDTO = {} } = issue;
    const { typeCode } = issueTypeDTO;
    const { featureType } = featureDTO || {};
    let currentIssueType = issueTypeDTO;
    if (typeCode === 'feature') {
      issueTypeData = [
        {
          ...issueTypeDTO,
          colour: '#29B6F6',
          featureType: 'business',
          name: '特性',
          id: 'business',
        }, {
          ...issueTypeDTO,
          colour: '#FFCA28',
          featureType: 'enabler',
          name: '使能',
          id: 'enabler',
        },
      ];
      currentIssueType = featureType === 'business' ? issueTypeData[0] : issueTypeData[1];
    } else {
      issueTypeData = issueTypeData.filter(item => item.typeCode !== 'feature');
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
          issueTypeData.map(t => (
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
          <Dropdown overlay={typeList} trigger={['click']} disabled={issueTypeDTO.typeCode === 'issue_epic'}>
            <div
              style={{
                height: 50,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
              }}
            >
              <TypeTag
                data={currentIssueType}
              />
              <Icon
                type="arrow_drop_down"
                style={{ fontSize: 16 }}
              />
            </div>
          </Dropdown>
        </div>
        {/* 锚点 */}
        <IssueNav typeCode={issueTypeDTO.typeCode} />
      </div>
    );
  }
}

export default SprintHeader;
