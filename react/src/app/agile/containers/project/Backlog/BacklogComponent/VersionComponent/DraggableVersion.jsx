import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import _ from 'lodash';
import moment from 'moment';
import classnames from 'classnames';
import { stores, Permission, axios } from 'choerodon-front-boot';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import {
  message, DatePicker, Icon, Dropdown, Menu, Input,
} from 'choerodon-ui';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import EasyEdit from '../../../../../components/EasyEdit/EasyEdit';
import DraggableEpic from '../EpicComponent/EpicItem';

const { AppState } = stores;

@observer
class VersionItem extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
      editName: false,
    };
  }

  /**
   *下拉菜单的menu
   *
   * @returns
   * @memberof VersionItem
   */
  getmenu() {
    return (
      <Menu onClick={this.clickMenu.bind(this)}>
        <Menu.Item key="0">编辑名称</Menu.Item>
      </Menu>
    );
  }

  /**
   *点击单个menu的事件
   *
   * @param {*} e
   * @memberof VersionItem
   */
  clickMenu(e) {
    e.domEvent.stopPropagation();
    if (e.key === '0') {
      this.setState({
        editName: true,
      });
    }
  }

  /**
   *更新描述
   *
   * @param {*} value
   * @memberof VersionItem
   */
  handleOnBlurDes(value) {
    const { item: { objectVersionNumber, versionId }, index } = this.props;
    const req = {
      objectVersionNumber,
      projectId: parseInt(AppState.currentMenuType.id, 10),
      versionId,
      description: value,
    };
    BacklogStore.axiosUpdateVerison(versionId, req).then((res) => {
      BacklogStore.updateVersion(res, 'description');
    }).catch((error) => {
    });
  }

  /**
   *更改名称
   *
   * @param {*} value
   * @memberof VersionItem
   */
  handleBlurName = (e) => {
    const { item, refresh } = this.props;
    const { objectVersionNumber, versionId } = item;
    const { value } = e.target;
    if (item && item.name === value) {
      this.setState({
        editName: false,
      });
    } else {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/product_version/check?name=${value}`)
        .then((checkRes) => {
          if (checkRes) {
            Choerodon.prompt('版本名称重复');
          } else {
            const verisonData = {
              objectVersionNumber,
              projectId: parseInt(AppState.currentMenuType.id, 10),
              versionId,
              name: value,
            };
            BacklogStore.axiosUpdateVerison(versionId, verisonData).then((res) => {
              if (res && res.failed) {
                this.setState({
                  editName: false,
                });
                Choerodon.prompt(res.message);
              } else {
                this.setState({
                  editName: false,
                });
                BacklogStore.updateVersion(res, 'name');
                refresh();
                // const originData = _.clone(BacklogStore.getVersionData);
                // originData[index].name = res.name;
                // originData[index].objectVersionNumber = res.objectVersionNumber;
                // BacklogStore.setVersionData(originData);
              }
            }).catch((error) => {
              this.setState({
                editName: false,
              });
            });
          }
        });
    }
  }


  /**
   *更新日期
   *
   * @param {*} type
   * @param {*} date2
   * @memberof VersionItem
   */
  updateDate(type, date) {
    const { item: { objectVersionNumber, versionId } } = this.props;
    const req = {
      objectVersionNumber,
      projectId: parseInt(AppState.currentMenuType.id, 10),
      versionId,
      [type]: date ? `${date} 00:00:00` : null,
    };
    BacklogStore.axiosUpdateVerison(versionId, req).then((res) => {
      BacklogStore.updateVersion(res, 'date');
      // const originData = _.clone(BacklogStore.getVersionData);
      // originData[index][type] = res[type];
      // originData[index].objectVersionNumber = res.objectVersionNumber;
      // BacklogStore.setVersionData(originData);
    }).catch((error) => {
    });
  }

  toggleExpand = (e) => {
    e.stopPropagation();
    const { expand } = this.state;
    this.setState({
      expand: !expand,
    });
  }

  render() {
    const {
      item, index, handleClickVersion, draggableSnapshot, draggableProvided,
    } = this.props;
    const { editName, expand } = this.state;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    return (
      <div
        ref={draggableProvided.innerRef}
        {...draggableProvided.draggableProps}
        {...draggableProvided.dragHandleProps}
        className={classnames('c7n-backlog-versionItems', {
          onClickVersion: BacklogStore.getChosenVersion === item.versionId,
        })}
        style={{
          paddingLeft: 0,
          cursor: 'move',
          ...draggableProvided.draggableProps.style,
        }}
        role="none"
        onClick={() => {
          handleClickVersion(item.versionId);
        }}
      >

        <div className="c7n-backlog-versionItemTitle">
          <Icon
            type={expand ? 'baseline-arrow_drop_down' : 'baseline-arrow_right'}
            role="none"
            onClick={this.toggleExpand}
          />
          <div style={{ width: '100%' }}>
            <div
              className="c7n-backlog-ItemsHead"
            >
              {editName ? (
                <Input
                  className="editVersionName"
                  autoFocus
                  defaultValue={item.name}
                  onPressEnter={this.handleBlurName}
                  onBlur={this.handleBlurName}
                  onClick={e => e.stopPropagation()}
                  maxLength={15}
                />
              ) : (
                <p>{item.name}</p>
              )}

              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.product-version.createVersion']}>
                <Dropdown onClick={e => e.stopPropagation()} overlay={this.getmenu()} trigger={['click']}>
                  <Icon
                    style={{
                      width: 12,
                      height: 12,
                      background: '#f5f5f5',
                      display: 'flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      border: '1px solid #ccc',
                      borderRadius: 2,
                    }}
                    type="arrow_drop_down"
                  />
                </Dropdown>
              </Permission>
            </div>
            {/* </div> */}
            <div className="c7n-backlog-versionItemProgress">
              <div
                className="c7n-backlog-versionItemDone"
                style={{
                  flex: item.doneIssueCount,
                }}
              />
              <div
                className="c7n-backlog-versionItemTodo"
                style={{
                  flex: item.issueCount ? item.issueCount - item.doneIssueCount : 1,
                }}
              />
            </div>
          </div>
        </div>

        {expand ? (
          <div style={{ paddingLeft: 12 }}>
            <div style={{ marginTop: 12 }}>
              <Permission
                type={type}
                projectId={projectId}
                organizationId={orgId}
                service={['agile-service.product-version.updateVersion']}
                noAccessChildren={(
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <p className="c7n-backlog-versionItemDes c7n-backlog-versionItemNotStoryPoint" ref={(versionId) => { this[item.versionId] = versionId; }}>
                      {!item.description ? '没有描述' : item.description}
                    </p>
                  </div>
                      )}
              >
                <EasyEdit
                  type="input"
                  defaultValue={item.description}
                  enterOrBlur={this.handleOnBlurDes.bind(this)}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <p className="c7n-backlog-versionItemDes" ref={(versionId) => { this[item.versionId] = versionId; }}>
                      {!item.description ? '没有描述' : item.description}
                    </p>
                  </div>
                </EasyEdit>
              </Permission>
            </div>
            <p className="c7n-backlog-versionItemDetail">详情</p>
            <div className="c7n-backlog-versionItemParams">
              <div className="c7n-backlog-versionItemParam">
                <p style={{ color: 'rgba(0,0,0,0.65)' }}>开始日期</p>
                <Permission
                  type={type}
                  projectId={projectId}
                  organizationId={orgId}
                  service={['agile-service.product-version.updateVersion']}
                  noAccessChildren={<p className="c7n-backlog-versionItemNotStoryPoint">{!_.isNull(item.startDate) ? `${item && item.startDate.split('-')[0]}/${item.startDate.split('-')[1]}/${item.startDate.split('-')[2].substring(0, 2)}` : '无'}</p>}
                >
                  <EasyEdit
                    type="date"
                    defaultValue={item.startDate ? moment(item.startDate.split(' ')[0], 'YYYY-MM-DD') : ''}
                    disabledDate={item.expectReleaseDate ? current => current > moment(item.expectReleaseDate, 'YYYY-MM-DD HH:mm:ss') : ''}
                    onChange={(date, dateString) => {
                      this.updateDate('startDate', dateString);
                    }}
                  >
                    <p className="c7n-backlog-versionItemNotStoryPoint">{!_.isNull(item.startDate) ? `${item && item.startDate.split('-')[0]}/${item.startDate.split('-')[1]}/${item.startDate.split('-')[2].substring(0, 2)}` : '无'}</p>
                  </EasyEdit>
                </Permission>
              </div>
              <div className="c7n-backlog-versionItemParam">
                <p style={{ color: 'rgba(0,0,0,0.65)' }}>预计发布日期</p>
                <Permission
                  type={type}
                  projectId={projectId}
                  organizationId={orgId}
                  service={['agile-service.product-version.updateVersion']}
                  noAccessChildren={<p className="c7n-backlog-versionItemNotStoryPoint">{!_.isNull(item.expectReleaseDate) ? `${item && item.expectReleaseDate.split('-')[0]}/${item.expectReleaseDate.split('-')[1]}/${item.expectReleaseDate.split('-')[2].substring(0, 2)}` : '无'}</p>}
                >
                  <EasyEdit
                    type="date"
                    defaultValue={item.expectReleaseDate ? moment(item.expectReleaseDate.split(' ')[0], 'YYYY-MM-DD') : ''}
                    disabledDate={item.startDate ? current => current < moment(item.startDate, 'YYYY-MM-DD HH:mm:ss') : ''}
                    onChange={(date, dateString) => {
                      this.updateDate('expectReleaseDate', dateString);
                    }}
                  >
                    <p className="c7n-backlog-versionItemNotStoryPoint">{!_.isNull(item.expectReleaseDate) ? `${item && item.expectReleaseDate.split('-')[0]}/${item.expectReleaseDate.split('-')[1]}/${item.expectReleaseDate.split('-')[2].substring(0, 2)}` : '无'}</p>
                  </EasyEdit>
                </Permission>
              </div>
              <div className="c7n-backlog-versionItemParam">
                <p className="c7n-backlog-versionItemParamKey">问题数</p>
                <p className="c7n-backlog-versionItemNotStoryPoint">{item.issueCount}</p>
              </div>
              <div className="c7n-backlog-versionItemParam">
                <p className="c7n-backlog-versionItemParamKey">已完成数</p>
                <p className="c7n-backlog-versionItemNotStoryPoint">{item.doneIssueCount}</p>
              </div>
              <div className="c7n-backlog-versionItemParam">
                <p className="c7n-backlog-versionItemParamKey">未预估数</p>
                <p className="c7n-backlog-versionItemNotStoryPoint">{item.notEstimate}</p>
              </div>
              <div className="c7n-backlog-versionItemParam">
                <p className="c7n-backlog-versionItemParamKey">故事点数</p>
                <p className="c7n-backlog-versionItemParamValue" style={{ minWidth: 31 }}>{`${item.totalEstimate}`}</p>
              </div>
            </div>
          </div>
        ) : ''}
      </div>
    );
  }
}

export default VersionItem;
