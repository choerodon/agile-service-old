import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import classnames from 'classnames';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

@observer
class ColumnCouldDragOn extends Component {
  shouldComponentUpdate(nextProps, nextState, nextContext) {
    const { keyId, dragOn } = this.props;
    if (nextProps.dragOn !== dragOn) {
      return true;
    }
    return false;
  }

  // 当 Table 为 expand 时，添加 ClassName，从而实现不渲染 Table 改变 css 样式的效果
  render() {
    // const { keyId } = this.props;
    const { keyId } = this.props;
    const dragOn = ScrumBoardStore.getCurrentDrag === keyId;
    // cpmst dragOn={ScrumBoardStore.getCurrentDrag === keyId}
    return (
      <div className={classnames({
        onColumnDragOn: dragOn,
      })}
      />
    );
  }
}

export default ColumnCouldDragOn;
