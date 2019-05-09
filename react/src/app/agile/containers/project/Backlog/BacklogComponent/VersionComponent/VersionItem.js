/* eslint-disable react/sort-comp */
import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import _ from 'lodash';
import moment from 'moment';
import { stores, Permission, axios } from 'choerodon-front-boot';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import {
  message, DatePicker, Icon, Dropdown, Menu, Input,
} from 'choerodon-ui';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import EasyEdit from '../../../../../components/EasyEdit/EasyEdit';
import DraggableVersion from './DraggableVersion';

const { AppState } = stores;

@observer
class VersionItem extends Component {
  constructor(props) {
    super(props);
    this.state = {
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
   *更新描述
   *
   * @param {*} value
   * @memberof VersionItem
   */
  handleOnBlurDes(value) {
    const { data: { objectVersionNumber, versionId }, index } = this.props;
    const data = {
      objectVersionNumber,
      projectId: parseInt(AppState.currentMenuType.id, 10),
      versionId,
      description: value,
    };
    BacklogStore.axiosUpdateVerison(versionId, data).then((res) => {
      // this.setState({
      //   editDescription: false,
      // });
      const originData = _.clone(BacklogStore.getVersionData);
      originData[index].description = res.description;
      originData[index].objectVersionNumber = res.objectVersionNumber;
      BacklogStore.setVersionData(originData);
    }).catch((error) => {
      // this.setState({
      //   editDescription: false,
      // });
    });
  }

  /**
   *更改名称
   *
   * @param {*} value
   * @memberof VersionItem
   */
  handleBlurName(e) {
    const {
      data: { objectVersionNumber, versionId },
      data,
      refresh,
    } = this.props;
    const { value } = e.target;
    if (data && data.name === value) {
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
  updateDate(type, date2) {
    let date = date2;
    const { data: { objectVersionNumber, versionId }, index } = this.props;
    const data = {
      objectVersionNumber,
      projectId: parseInt(AppState.currentMenuType.id, 10),
      versionId,
      [type]: date ? date += ' 00:00:00' : null,
    };
    BacklogStore.axiosUpdateVerison(versionId, data).then((res) => {
      const originData = _.clone(BacklogStore.getVersionData);
      originData[index][type] = res[type];
      originData[index].objectVersionNumber = res.objectVersionNumber;
      BacklogStore.setVersionData(originData);
    }).catch((error) => {
    });
  }

  handleClickVersion = (type) => {
    const { handleClickVersion } = this.props;
    handleClickVersion(type);
  };

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

  render() {
    // const { data: item } = this.props;
    const {
      handelClickVersion, issueRefresh, refresh, draggableIds,
    } = this.props;
    const { editName } = this.state;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    return BacklogStore.getVersionData.map((item, index) => (
      <div
        role="none"
        onMouseEnter={(e) => {
          if (BacklogStore.getIsDragging) {
            BacklogStore.toggleIssueDrag(true);
            e.currentTarget.style.border = '2px dashed green';
          }
        }}
        onMouseLeave={(e) => {
          if (BacklogStore.getIsDragging) {
            BacklogStore.toggleIssueDrag(false);
            e.currentTarget.style.border = 'none';
          }
        }}
        onMouseUp={(e) => {
          BacklogStore.toggleIssueDrag(false);
          if (BacklogStore.getIsDragging) {
            e.currentTarget.style.border = 'none';
            BacklogStore.axiosUpdateIssuesToVersion(
              item.versionId, BacklogStore.getIssueWithEpicOrVersion,
            ).then((res) => {
              issueRefresh();
              refresh();
            }).catch((error) => {
              issueRefresh();
              refresh();
            });
          }
        }}
      >
        <Draggable draggableId={`epicItem-${item.versionId}`} key={item.versionId} index={index}>
          {(draggableProvided, draggableSnapshot) => (
            <DraggableVersion
              item={item}
              refresh={refresh}
              draggableProvided={draggableProvided}
              draggableSnapshot={draggableSnapshot}
              handleClickVersion={this.handleClickVersion}
            />
          )}
        </Draggable>
      </div>
    ));
  }
}

export default VersionItem;
