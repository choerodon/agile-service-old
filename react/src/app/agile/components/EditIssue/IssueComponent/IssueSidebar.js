import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Dropdown, Icon, Menu } from 'choerodon-ui';
import IssueNav from './IssueNav';
import TypeTag from '../../TypeTag';
import { updateIssueType, updateIssue } from '../../../api/NewIssueApi';
import './IssueComponent.scss';

@inject('AppState', 'HeaderStore')
@observer class IssueSidebar extends Component {
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
      issueId, objectVersionNumber, summary, featureVO = {}, issueTypeVO = {},
    } = issue;
    const { featureType, value } = type.item.props;
    const { typeCode } = issueTypeVO;
    if (typeCode === 'feature') {
      const { id, objectVersionNumber: featureObjNum } = featureVO;
      const issueUpdateVO = {
        issueId,
        objectVersionNumber,
        featureVO: {
          id,
          issueId,
          objectVersionNumber: featureObjNum,
          featureType: type.item.props.value,
        },
      };
      updateIssue(issueUpdateVO)
        .then(() => {
          if (reloadIssue) {
            reloadIssue(issueId);
          }
          if (onUpdate) {
            onUpdate();
          }
        });
    } else {
      const issueUpdateTypeVO = {
        epicName: type.key === 'issue_epic' ? summary : undefined,
        issueId,
        objectVersionNumber,
        typeCode: type.key,
        issueTypeId: value,
        featureType,
      };
      updateIssueType(issueUpdateTypeVO)
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
      store, type = 'narrow', disabled, applyType,
    } = this.props;

    let issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
    const issue = store.getIssue;
    const { issueTypeVO = {}, featureVO = {} } = issue;
    const { typeCode } = issueTypeVO;
    const { featureType } = featureVO || {};
    let currentIssueType = issueTypeVO;
    if (typeCode === 'feature') {
      issueTypeData = [
        {
          ...issueTypeVO,
          colour: '#29B6F6',
          featureType: 'business',
          name: '特性',
          id: 'business',
        }, {
          ...issueTypeVO,
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
            <Menu.Item key={t.typeCode} value={t.id} featureType={t.featureType}>
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
          {disabled ? (
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
            </div>          
          ) : (
            <Dropdown overlay={typeList} trigger={['click']} disabled={typeCode === 'sub_task' || disabled}>
              <div
                className={type === 'narrow' ? 'issue-nav-narrow' : 'issue-nav-wide'}
              >
                <TypeTag
                  data={currentIssueType}
                  featureType={featureVO && featureVO.featureType}
                />
                <Icon
                  type="arrow_drop_down"
                  style={{ fontSize: 16 }}
                />
              </div>
            </Dropdown>
          )}
        </div>
        {/* 锚点 */}
        <IssueNav typeCode={typeCode} applyType={applyType} />
      </div>
    );
  }
}

export default IssueSidebar;
