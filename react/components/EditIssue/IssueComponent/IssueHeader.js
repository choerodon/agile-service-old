import React, { useContext } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Dropdown, Icon, Menu, Button, Modal,
} from 'choerodon-ui';
import IssueNumber from './IssueNumber';
import { FieldStoryPoint, FieldSummary } from './IssueBody/Field';
import IssueType from './IssueType';
import { deleteIssue } from '../../../api/NewIssueApi';
import './IssueComponent.scss';
import VisibleStore from '../../../stores/common/visible/VisibleStore';
import EditIssueContext from '../stores';
import './IssueHeader.less';

const { confirm } = Modal;
const IssueHeader = (props) => {
  const { AppState, store, prefixCls } = useContext(EditIssueContext);
  const {
    resetIssue, backUrl, onCancel, loginUserId, hasPermission, reloadIssue, disabled, onDeleteIssue,
  } = props;
  const handleDeleteIssue = (issueId) => {
    const issue = store.getIssue;
    const { issueNum, subIssueVOList = [] } = issue;
    confirm({
      width: 560,
      title: `删除问题${issueNum}`,
      content:
        (
          <div>
            <p style={{ marginBottom: 10 }}>请确认您要删除这个问题。</p>
            <p style={{ marginBottom: 10 }}>这个问题将会被彻底删除。包括所有附件和评论。</p>
            <p style={{ marginBottom: 10 }}>如果您完成了这个问题，通常是已解决或者已关闭，而不是删除。</p>
            {
              subIssueVOList.length ? <p style={{ color: '#d50000' }}>{`注意：问题的${subIssueVOList.length}子任务将被删除。`}</p> : null
            }
          </div>
        ),
      onOk() {
        return deleteIssue(issueId)
          .then((res) => {
            if (onDeleteIssue) {
              onDeleteIssue();
            }
          });
      },
      onCancel() { },
      okText: '删除',
      okType: 'danger',
    });
  };

  const handleClickMenu = (e) => {
    const issue = store.getIssue;
    if (e.key === '0') {
      VisibleStore.setWorkLogShow(true);
    } else if (e.key === '1') {
      handleDeleteIssue(issue.issueId);
    } else if (e.key === '2') {
      VisibleStore.setCreateSubTaskShow(true);
    } else if (e.key === '3') {
      VisibleStore.setCopyIssueShow(true);
    } else if (e.key === '4') {
      VisibleStore.setTransformSubIssueShow(true);
    } else if (e.key === '5') {
      VisibleStore.setTransformFromSubIssueShow(true);
    } else if (e.key === '6') {
      VisibleStore.setCreateBranchShow(true);
    } else if (e.key === '7') {
      VisibleStore.setAssigneeShow(true);
    } else if (e.key === '8') {
      VisibleStore.setChangeParentShow(true);
    } else if (e.key === '9') {
      VisibleStore.setCreateSubBugShow(true);
    } else if (e.key === '10') {
      VisibleStore.setRelateStoryShow(true);
    }
  };


  const urlParams = AppState.currentMenuType;
  const issue = store.getIssue;
  const {
    parentIssueId, relateIssueId, typeCode, parentIssueNum, relateIssueNum,
    issueId, createdBy, subIssueVOList = [],
  } = issue;

  const getMenu = () => (
    <Menu onClick={handleClickMenu}>
      {!['feature'].includes(typeCode) && (
        <Menu.Item key="0">
          {'登记工作日志'}
        </Menu.Item>
      )}
      {
        <Menu.Item
          key="1"
          disabled={loginUserId !== createdBy && !hasPermission}
        >
          {'删除'}
        </Menu.Item>
      }
      {
        ['sub_task', 'feature'].indexOf(typeCode) === -1 && (
          <Menu.Item key="2">
            {'创建子任务'}
          </Menu.Item>
        )
      }
      {
        ['story', 'task'].indexOf(typeCode) !== -1 && (
          <Menu.Item key="9">
            {'创建缺陷'}
          </Menu.Item>
        )
      }
      <Menu.Item key="3">
        {'复制问题'}
      </Menu.Item>
      {
        ['sub_task', 'feature'].indexOf(typeCode) === -1 && subIssueVOList.length === 0 && (
          <Menu.Item key="4">
            {'转化为子任务'}
          </Menu.Item>
        )
      }
      {
        typeCode === 'sub_task' && (
          <Menu.Item key="5">
            {'转化为任务'}
          </Menu.Item>
        )
      }
      {
        typeCode !== 'feature' && (
          <Menu.Item key="6">
            {'创建分支'}
          </Menu.Item>
        )
      }
      {
        typeCode !== 'feature' && (
          <Menu.Item key="7">
            {'分配问题'}
          </Menu.Item>
        )
      }
      {
        typeCode === 'sub_task' && (
          <Menu.Item key="8">
            {'修改父级'}
          </Menu.Item>
        )
      }
      {
        typeCode === 'bug' && (
          <Menu.Item key="10">
            {'关联问题'}
          </Menu.Item>
        )
      }
    </Menu>
  );

  return (
    <div className={`${prefixCls}-IssueHeader`}>
      <div className={`${prefixCls}-IssueHeader-top`}>
        <IssueType {...props} />
        {/* 问题编号 */}
        <span style={{ marginLeft: 15 }}>
          <IssueNumber
            parentIssueId={parentIssueId || relateIssueId}
            resetIssue={resetIssue}
            reloadIssue={reloadIssue}
            urlParams={urlParams}
            backUrl={backUrl}
            typeCode={typeCode}
            parentIssueNum={parentIssueNum || relateIssueNum}
            issue={issue}
          />
        </span>
        {/* 隐藏 */}
        <div
          className={`${prefixCls}-IssueHeader-btn`}          
          role="none"
          onClick={() => {
            onCancel();
          }}
        >
          <Icon type="last_page" style={{ fontSize: '18px', fontWeight: '500' }} />
          <span>隐藏详情</span>
        </div>
      </div>
      <div className="line-justify" style={{ margin: '10px 0', alignItems: 'flex-start' }}>
        <FieldSummary
          {...props}
          showTitle={false}
          field={{ fieldCode: 'summary', fieldName: '概要' }}
        />
        <div style={{ flexShrink: 0, color: 'rgba(0, 0, 0, 0.65)' }}>
          {!disabled && (
            <Dropdown overlay={getMenu()} trigger={['click']}>
              <Button icon="more_vert" />
            </Dropdown>
          )}
        </div>
      </div>
      {/* 故事点 */}
      <div className="line-start">
        {
          issueId && ['story', 'feature'].indexOf(typeCode) !== -1 ? (
            <div style={{ display: 'flex', marginRight: 25 }}>
              <FieldStoryPoint {...props} field={{ fieldCode: 'storyPoints', fieldName: '故事点' }} />
            </div>
          ) : null
        }
        {
          issueId && ['issue_epic', 'feature'].indexOf(typeCode) === -1 ? (
            <div style={{ display: 'flex' }}>
              <FieldStoryPoint {...props} field={{ fieldCode: 'remainingTime', fieldName: '预估时间' }} />
            </div>
          ) : null
        }
      </div>
    </div>
  );
};

export default IssueHeader;
