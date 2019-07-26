import React, { Component, Fragment } from 'react';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import {
  Card, Tooltip, Button, Input, Tree,
} from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import './TreeList.scss';

const { TreeNode } = Tree;

class TreeList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addItemVisible: false,
      selecteId: false,
      tempKey: false,
      value: '',
    };
    this.editting = false;
  }

  onDragEnter = (info) => {
    window.console.log(info);
    this.editting = false;
    this.setState({
      addItemVisible: false,
      tempKey: false,
    });
  };

  onDrop = (info) => {
    const loop = (data, key, callback) => {
      data.forEach((item, index, arr) => {
        if (String(item.tempKey) === key || String(item.id) === key) {
          return callback(item, index, arr);
        }
        if (item.children) {
          return loop(item.children, key, callback);
        }
        return [];
      });
    };

    const { data, onChange } = this.props;
    const dropKey = info.node.props.eventKey;
    const dragKey = info.dragNode.props.eventKey;
    const dropPos = info.node.props.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
    let dragObj;
    loop(data, dragKey, (item, index, arr) => {
      arr.splice(index, 1);
      dragObj = item;
    });
    if (info.dropToGap) {
      // 拖拽到节点间，与节点同级
      let ar;
      let i;
      loop(data, dropKey, (item, index, arr) => {
        ar = arr;
        i = index;
      });
      // 可以移动到一个节点前或后，i为该节点的索引。当移动到前，直接插入即可;如果移动到节点后，则需index+1。
      if (dropPosition === -1) {
        ar.splice(i, 0, dragObj);
      } else {
        ar.splice(i + 1, 0, dragObj);
      }
    } else {
      // 拖拽到节点上，成为该节点的子集
      loop(data, dropKey, (item) => {
        item.children = item.children || [];
        // where to insert 示例添加到尾部，可以是随意位置
        item.children.push(dragObj);
      });
    }
    onChange(data);
  };

  addItem = () => {
    this.setState({
      addItemVisible: true,
      selecteId: false,
      tempKey: false,
    }, () => {
      const input = document.getElementById('treeList-input');
      if (input) {
        input.focus();
      }
    });
  };

  editItem = (tempKey) => {
    this.editting = true;
    this.setState({
      tempKey,
    }, () => {
      const input = document.getElementById('treeList-input');
      if (input) {
        input.focus();
      }
    });
  };

  create = () => {
    const { onCreate } = this.props;
    const { value } = this.state;
    if (onCreate) {
      onCreate(value);
    }
    this.cancel();
  };

  edit = (tempKey) => {
    const loopEdit = (data, key, changeKey, changeValue) => {
      if (data) {
        return data.map((item) => {
          if (item.tempKey === key || item.id === key) {
            return { ...item, [changeKey]: changeValue };
          } else {
            return { ...item, children: loopEdit(item.children, key, changeKey, changeValue) };
          }
        });
      }
      return [];
    };
    const { data, onEdit, onChange } = this.props;
    const { value } = this.state;
    if (onEdit) {
      onEdit(tempKey, value);
    }
    if (onChange) {
      const updatedData = loopEdit(data, tempKey, 'value', value);
      onChange(updatedData, 'edit');
    }
    this.cancel();
  };

  invalid = (tempKey) => {
    const loopInvalid = (data, key, all) => {
      if (data) {
        if (all) {
          return data.map(item => ({ ...item, isEnable: '0', children: loopInvalid(item.children, key, all) }));
        } else {
          return data.map((item) => {
            if (item.tempKey === key || item.id === key) {
              return { ...item, isEnable: '0', children: loopInvalid(item.children, key, true) };
            } else {
              return { ...item, children: loopInvalid(item.children, key, all) };
            }
          });
        }
      }
      return [];
    };
    const { data, onInvalid, onChange } = this.props;
    if (onInvalid) {
      onInvalid(tempKey);
    }
    if (onChange) {
      const updatedData = loopInvalid(data, tempKey, false);
      onChange(updatedData, 'invalid');
    }
    this.cancel();
  };

  active = (tempKey) => {
    const loopActive = (data, key, all) => {
      if (data) {
        if (all) {
          return data.map(item => ({ ...item, isEnable: '1', children: loopActive(item.children, key, all) }));
        } else {
          return data.map((item) => {
            if (item.tempKey === key || item.id === key) {
              return { ...item, isEnable: '1', children: loopActive(item.children, key, true) };
            } else {
              return { ...item, children: loopActive(item.children, key, all) };
            }
          });
        }
      }
      return [];
    };
    const { data, onActive, onChange } = this.props;
    if (onActive) {
      onActive(tempKey);
    }
    if (onChange) {
      const updatedData = loopActive(data, tempKey, false);
      onChange(updatedData, 'active');
    }
    this.cancel();
  };

  remove = (tempKey) => {
    const loopRemove = (data, key) => {
      if (data) {
        const newDate = data.filter(item => item.tempKey !== key && item.id !== key);
        if (data.length !== newDate.length) {
          return newDate;
        } else {
          return data.map(item => ({ ...item, children: loopRemove(item.children, key) }));
        }
      }
      return [];
    };
    const { data, onDelete, onChange } = this.props;
    if (onDelete) {
      onDelete(tempKey);
    }
    if (onChange) {
      const updatedData = loopRemove(data, tempKey);
      onChange(updatedData, 'delete');
    }
    this.cancel();
  };


  cancel = () => {
    this.editting = false;
    this.setState({
      addItemVisible: false,
      saveDisabled: false,
      tempKey: false,
      value: '',
    });
  };

  onInputChange = (e) => {
    this.setState({
      saveDisabled: !e.target.value,
      value: e.target.value || '',
    });
  };

  onSelect = (selectedKeys, e) => {
    const { tempKey } = this.state;
    if (!this.editting) {
      if (selectedKeys && selectedKeys.length) {
        this.setState({
          addItemVisible: false,
          selectedKeys,
          tempKey: selectedKeys[0] === String(tempKey) ? tempKey : false,
          selecteId: selectedKeys[0],
        });
      } else {
        this.setState({
          selectedKeys: [],
          tempKey: false,
          selecteId: false,
        });
      }
    }
  };

  renderCreateTreeNode = () => {
    const { saveDisabled, addItemVisible } = this.state;
    const { intl } = this.props;
    if (addItemVisible) {
      return (
        <TreeNode
          key="create"
          title={(
            <Fragment>
              <span className="issue-dragList-input">
                <Input
                  id="treeList-input"
                  onChange={this.onInputChange}
                  underline={false}
                  placeholder={intl.formatMessage({ id: 'dragList.placeholder' })}
                />
              </span>
              <Button
                disabled={saveDisabled}
                type="primary"
                size="small"
                onClick={this.create}
                funcType="raised"
                className="issue-dragList-add"
              >
                <FormattedMessage id="save" />
              </Button>
              <Button
                size="small"
                onClick={this.cancel}
                funcType="raised"
              >
                <FormattedMessage id="cancel" />
              </Button>
            </Fragment>
          )}
        />
      );
    } else {
      return [];
    }
  };

  renderTreeNode = (item) => {
    const { saveDisabled, selecteId, tempKey } = this.state;
    const { intl } = this.props;
    if (String(tempKey) === String(item.id) || String(tempKey) === item.tempKey) {
      return (
        <Fragment>
          <span className="issue-dragList-input">
            <Input
              id="treeList-input"
              defaultValue={item.value}
              onChange={this.onInputChange}
              underline={false}
              placeholder={intl.formatMessage({ id: 'dragList.placeholder' })}
            />
          </span>
          <Button
            disabled={saveDisabled}
            type="primary"
            size="small"
            onClick={() => this.edit(tempKey)}
            funcType="raised"
            className="issue-dragList-add"
          >
            <FormattedMessage id="save" />
          </Button>
          <Button
            size="small"
            onClick={this.cancel}
            funcType="raised"
          >
            <FormattedMessage id="cancel" />
          </Button>
        </Fragment>
      );
    } else if (item.isEnable === '0' || String(selecteId) === String(item.id) || String(selecteId) === item.tempKey) {
      return (
        <Fragment>
          <span className="issue-dragList-text">{item.value}</span>
          <div className="issue-dragList-operate">
            <Tooltip
              placement="bottom"
              title={<FormattedMessage id="edit" />}
            >
              <Button
                size="small"
                shape="circle"
                onClick={() => this.editItem(item.id || item.tempKey)}
              >
                <i className="icon icon-mode_edit" />
              </Button>
            </Tooltip>
            {
              item.isEnable === '1'
                ? (
                  <Tooltip
                    placement="bottom"
                    title={<FormattedMessage id="dragList.invalid" />}
                  >
                    <Button size="small" shape="circle" onClick={() => this.invalid(item.tempKey || item.id)}>
                      <i className="icon icon-block" />
                    </Button>
                  </Tooltip>
                )
                : (
                  <Tooltip
                    placement="bottom"
                    title={<FormattedMessage id="dragList.active" />}
                  >
                    <Button size="small" shape="circle" onClick={() => this.active(item.tempKey || item.id)}>
                      <i className="icon icon-playlist_add_check" />
                    </Button>
                  </Tooltip>
                )
            }
            <Tooltip
              placement="bottom"
              title={<FormattedMessage id="delete" />}
            >
              <Button size="small" shape="circle" onClick={() => this.remove(item.id || item.tempKey)}>
                <i className="icon icon-delete" />
              </Button>
            </Tooltip>
          </div>
        </Fragment>
      );
    } else {
      return (<span className="issue-dragList-text">{item.value}</span>);
    }
  };

  loop = data => data.map((item) => {
    if (item.children && item.children.length) {
      return (
        <TreeNode
          disabled={item.isEnable === '0'}
          key={item.tempKey || item.id}
          title={this.renderTreeNode(item)}
        >
          {this.loop(item.children)}
        </TreeNode>
      );
    }
    return (
      <TreeNode
        disabled={item.isEnable === '0'}
        key={item.tempKey || item.id}
        title={this.renderTreeNode(item)}
      />
    );
  });


  render() {
    const {
      data, title, tips,
    } = this.props;
    const {
      addItemVisible, selectedKeys,
    } = this.state;

    return (
      <div>
        <div className="issue-treeList-des">
          {tips}
        </div>
        <div className="issue-treeList-content">
          <Card
            title={title}
            extra={(
              <Button
                onClick={this.addItem}
                funcType="flat"
                className="issue-treeList-addBtn"
              >
                <i className="icon-add icon" />
                <FormattedMessage id="add" />
              </Button>
            )}
            bordered={false}
            className="issue-dragList-card"
          >
            <Tree
              selectedKeys={addItemVisible ? ['create'] : selectedKeys}
              className="draggable-tree"
              draggable
              onDragEnter={this.onDragEnter}
              onDrop={this.onDrop}
              onSelect={this.onSelect}
            >
              {data && this.loop(data)}
              {this.renderCreateTreeNode()}
            </Tree>
          </Card>
        </div>
      </div>
    );
  }
}

export default injectIntl(TreeList);
