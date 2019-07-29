import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Dropdown, Menu, Icon } from 'choerodon-ui';
import { find } from 'lodash';
import TypeTag from '../../TypeTag';
import { updateIssueType, updateIssue } from '../../../api/NewIssueApi';
import EditIssueContext from '../stores';
import './IssueComponent.scss';

const IssueType = observer(({
  reloadIssue, onUpdate, 
}) => {
  const { store, disabled } = useContext(EditIssueContext);
  const handleChangeType = (type) => {
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


  let issueTypeData = store.getIssueTypes ? store.getIssueTypes : [];
  const issue = store.getIssue;
  const { issueTypeVO = {}, featureVO = {}, subIssueVOList = [] } = issue;
  const { typeCode } = issueTypeVO;
  const { stateMachineId } = find(issueTypeData, { typeCode }) || {};
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
    issueTypeData = issueTypeData.filter(item => item.stateMachineId === stateMachineId).filter(item => ![typeCode, 'feature', 'sub_task'].includes(item.typeCode));
  }
  if (subIssueVOList.length > 0) {
    issueTypeData = issueTypeData.filter(item => ['task', 'story'].includes(item.typeCode));
  }
  const typeList = (
    <Menu
      style={{
        background: '#fff',
        boxShadow: '0 5px 5px -3px rgba(0, 0, 0, 0.20), 0 8px 10px 1px rgba(0, 0, 0, 0.14), 0 3px 14px 2px rgba(0, 0, 0, 0.12)',
        borderRadius: '2px',
      }}
      className="issue-sidebar-types"
      onClick={handleChangeType}
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
            className="issue-nav-narrow"
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
  );
});
export default IssueType;
