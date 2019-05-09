import React, { Component } from 'react';
import { stores, axios } from 'choerodon-front-boot';
import { observer } from 'mobx-react';
import classnames from 'classnames';
import { Droppable, Draggable } from 'react-beautiful-dnd';
import {
  Dropdown, Menu, Input, Icon, message,
} from 'choerodon-ui';
import _ from 'lodash';

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

  /**
   *menu的点击事件
   *
   * @param {*} e
   * @memberof EpicItem
   */
  clickMenu = (e) => {
    const { item, store } = this.props;
    if (e && e.stopPropagation) {
      e.stopPropagation();
    } else if (e && e.domEvent && e.domEvent.stopPropagation) {
      e.domEvent.stopPropagation();
    }
    if (e.key === '1') {
      this.setState({
        editName: true,
      });
    }
    if (e.key === '2') {
      store.setClickIssueDetail(item);
    }
  };

  /**
   *每个epic 右侧下拉选择项的menu
   *
   * @returns
   * @memberof EpicItem
   */
  getMenu = () => {
    const { item, refresh, store } = this.props;
    return (
      <Menu onClick={this.clickMenu.bind(this)}>
        <div style={{ padding: '5px 12px' }}>
          {'颜色'}
          <div className="c7n-feature-epicColor">
            {store.getColorLookupValue.map(color => (
              <div
                key={color.name}
                style={{ background: color.name }}
                className="c7n-feature-epicColorItem"
                role="none"
                onClick={(e) => {
                  e.stopPropagation();
                  const inputData = {
                    colorCode: color.valueCode,
                    issueId: item.issueId,
                    objectVersionNumber: item.objectVersionNumber,
                  };
                  store.axiosUpdateIssue(inputData).then((res) => {
                    store.updateEpic(res);
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
  };

  /**
   *epic名称保存事件
   *
   * @param {*} e
   * @memberof EpicItem
   */
  handleSave = (e) => {
    const {
      item, refresh, store,
    } = this.props;
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
            store.axiosUpdateIssue(req).then((res) => {
              store.updateEpic(res);
              refresh();
            }).catch((error) => {
            });
          }
        });
    }
  };

  toggleExpand = (e) => {
    e.stopPropagation();
    const { expand } = this.state;
    this.setState({
      expand: !expand,
    });
  };

  render() {
    const {
      draggableProvided, item, store,
    } = this.props;
    const { expand, editName } = this.state;

    return (
      <div
        ref={draggableProvided.innerRef}
        {...draggableProvided.draggableProps}
        {...draggableProvided.dragHandleProps}
        className={classnames('c7n-feature-epicItems', {
          onClickEpic: store.getChosenEpic === item.issueId,
        })}
        role="none"
      >
        <div
          className="c7n-feature-epicItemTitle"
        >
          <Icon
            type={expand ? 'baseline-arrow_drop_down' : 'baseline-arrow_right'}
            role="none"
            onClick={this.toggleExpand}
          />
          <div style={{ width: '100%' }}>
            <div className="c7n-feature-epicItemsHead">
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
              <Dropdown onClick={e => e.stopPropagation()} overlay={this.getMenu()} trigger={['click']}>
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
              className="c7n-feature-epicItemProgress"
            >
              <div
                className="c7n-feature-epicItemDone"
                style={{
                  flex: item.doneIssueCount,
                }}
              />
              <div
                className="c7n-feature-epicItemTodo"
                style={{
                  flex: item.issueCount ? item.issueCount - item.doneIssueCount : 1,
                }}
              />
            </div>
          </div>
        </div>
        {expand ? (
          <div style={{ paddingLeft: 12 }}>
            <p className="c7n-feature-epicItemDes">
              {_.isNull(item.summary) ? '没有描述' : item.summary}
            </p>
            <p className="c7n-feature-epicItemDetail">计数详情</p>
            <div className="c7n-feature-epicItemParams">
              <div className="c7n-feature-epicItemParam">
                <p className="c7n-feature-epicItemParamKey">问题数</p>
                <p className="c7n-feature-epicItemNotStoryPoint">{item.issueCount}</p>
              </div>
              <div className="c7n-feature-epicItemParam">
                <p className="c7n-feature-epicItemParamKey">已完成数</p>
                <p className="c7n-feature-epicItemNotStoryPoint">{item.doneIssueCount}</p>
              </div>
              <div className="c7n-feature-epicItemParam">
                <p className="c7n-feature-epicItemParamKey">未预估数</p>
                <p className="c7n-feature-epicItemNotStoryPoint">{item.notEstimate}</p>
              </div>
              <div className="c7n-feature-epicItemParam">
                <p className="c7n-feature-epicItemParamKey">故事点数</p>
                <p
                  className="c7n-feature-epicItemParamValue"
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
