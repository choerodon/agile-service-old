import React, { Component } from 'react';
import { stores, axios, store } from 'choerodon-front-boot';
import { observer, inject } from 'mobx-react';
import classnames from 'classnames';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import {
  Dropdown, Menu, Input, Icon, message,
} from 'choerodon-ui';
import _ from 'lodash';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';

const { AppState } = stores;
// @inject('AppState')
@observer
class DraggableEpic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expand: false,
      editName: false,
    };
  }

  // shouldComponentUpdate(nextProps, nextState, nextContext) {
  //   if (JSON.stringify(nextProps) === JSON.stringify(this.props)) {
  //     return false;
  //   } else {
  //     return true;
  //   }
  // }

  /**
   *menu的点击事件
   *
   * @param {*} e
   * @memberof EpicItem
   */
  clickMenu = (e) => {
    const { item } = this.props;

    e.domEvent.stopPropagation();
    if (e.key === '1') {
      this.setState({
        editName: true,
      });
    }
    if (e.key === '2') {
      BacklogStore.setClickIssueDetail(item);
    }
  }

  /**
   *每个epic 右侧下拉选择项的menu
   *
   * @returns
   * @memberof EpicItem
   */
  getmenu = () => {
    const { item, refresh } = this.props;
    return (
      <Menu onClick={this.clickMenu.bind(this)}>
        <div style={{ padding: '5px 12px' }}>
          {'颜色'}
          <div className="c7n-backlog-epicColor">
            {BacklogStore.getColorLookupValue.map(color => (
              <div
                key={color.name}
                style={{ background: color.name }}
                className="c7n-backlog-epicColorItem"
                role="none"
                onClick={(e) => {
                  e.stopPropagation();
                  const inputData = {
                    colorCode: color.valueCode,
                    issueId: item.issueId,
                    objectVersionNumber: item.objectVersionNumber,
                  };
                  BacklogStore.axiosUpdateIssue(inputData).then((res) => {
                    BacklogStore.updateEpic(res);
                    refresh();
                  }).catch((error) => {
                  });
                }}
              />
            ))}
          </div>
        </div>
        <Menu.Divider />
        <Menu.Item key="1">编辑名称</Menu.Item>
        <Menu.Item key="2">查看史诗详情</Menu.Item>
      </Menu>
    );
  }

  /**
   *epic名称保存事件
   *
   * @param {*} e
   * @memberof EpicItem
   */
  handleSave = (e) => {
    const { item, index, refresh } = this.props;
    e.stopPropagation();
    const { value } = e.target;
    if (item && item.epicName === value) {
      this.setState({
        editName: false,
      });
    } else {
      axios.get(`/agile/v1/projects/${AppState.currentMenuType.id}/issues/check_epic_name?epicName=${value}`)
        .then((checkRes) => {
          if (checkRes) {
            Choerodon.prompt('史诗名称重复');
          } else {
            this.setState({
              editName: false,
            });
            const req = {
              objectVersionNumber: item.objectVersionNumber,
              issueId: item.issueId,
              epicName: value,
            };
            BacklogStore.axiosUpdateIssue(req).then((res) => {
              BacklogStore.updateEpic(res);
              refresh();
            }).catch((error) => {
            });
          }
        });
    }
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
      draggableProvided, item,
    } = this.props;
    const { expand, editName } = this.state;

    return (
      <div
        ref={draggableProvided.innerRef}
        {...draggableProvided.draggableProps}
        {...draggableProvided.dragHandleProps}
        className={classnames('c7n-backlog-epicItems', {
          onClickEpic: BacklogStore.getChosenEpic === item.issueId,
        })}
        role="none"
      >
        <div
          className="c7n-backlog-epicItemTitle"
        >
          <Icon
            type={expand ? 'baseline-arrow_drop_down' : 'baseline-arrow_right'}
            role="none"
            onClick={this.toggleExpand}
          />
          <div style={{ width: '100%' }}>
            <div className="c7n-backlog-epicItemsHead">
              {editName ? (
                <Input
                  className="editEpicName"
                  autoFocus
                  defaultValue={item.epicName}
                  onPressEnter={this.handleSave}
                  onClick={(e) => {
                    e.stopPropagation();
                  }}
                  onBlur={this.handleSave}
                  maxLength={10}
                />
              ) : (
                <p>{item.epicName}</p>
              )}
              <Dropdown onClick={e => e.stopPropagation()} overlay={this.getmenu()} trigger={['click']}>
                <Icon
                  style={{
                    width: 12,
                    height: 12,
                    background: item.color,
                    color: 'white',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    borderRadius: 2,
                  }}
                  type="arrow_drop_down"
                />
              </Dropdown>
            </div>
            <div
              className="c7n-backlog-epicItemProgress"
            >
              <div
                className="c7n-backlog-epicItemDone"
                style={{
                  flex: item.doneIssueCount,
                }}
              />
              <div
                className="c7n-backlog-epicItemTodo"
                style={{
                  flex: item.issueCount ? item.issueCount - item.doneIssueCount : 1,
                }}
              />
            </div>
          </div>
        </div>
        {expand ? (
          <div style={{ paddingLeft: 12 }}>
            <p className="c7n-backlog-epicItemDes">
              {_.isNull(item.summary) ? '没有描述' : item.summary}
            </p>
            <p className="c7n-backlog-epicItemDetail">计数详情</p>
            <div className="c7n-backlog-epicItemParams">
              <div className="c7n-backlog-epicItemParam">
                <p className="c7n-backlog-epicItemParamKey">问题数</p>
                <p className="c7n-backlog-epicItemNotStoryPoint">{item.issueCount}</p>
              </div>
              <div className="c7n-backlog-epicItemParam">
                <p className="c7n-backlog-epicItemParamKey">已完成数</p>
                <p className="c7n-backlog-epicItemNotStoryPoint">{item.doneIssueCount}</p>
              </div>
              <div className="c7n-backlog-epicItemParam">
                <p className="c7n-backlog-epicItemParamKey">未预估数</p>
                <p className="c7n-backlog-epicItemNotStoryPoint">{item.notEstimate}</p>
              </div>
              <div className="c7n-backlog-epicItemParam">
                <p className="c7n-backlog-epicItemParamKey">故事点数</p>
                <p
                  className="c7n-backlog-epicItemParamValue"
                  style={{ minWidth: 31, color: 'rgba(0,0,0,0.65)' }}
                >
                  {item.totalEstimate}
                </p>
              </div>
            </div>
          </div>
        ) : ''}
      </div>
    );
  }
}

export default DraggableEpic;
