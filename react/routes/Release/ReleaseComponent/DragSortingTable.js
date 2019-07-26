import { Table } from 'choerodon-ui';
import React, { Component } from 'react';
import { DragDropContext, DragSource, DropTarget } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import update from 'immutability-helper';
import isEqual from 'lodash/isEqual';
import './DrapSortingTable.scss';

const dragDirection = (
  dragIndex,
  hoverIndex,
  initialClientOffset,
  clientOffset,
  sourceClientOffset,
) => {
  const hoverMiddleY = (initialClientOffset.y - sourceClientOffset.y) / 2;
  const hoverClientY = clientOffset.y - sourceClientOffset.y;
  let type = '';
  if (dragIndex < hoverIndex && hoverClientY > hoverMiddleY) {
    type = 'downward';
  }
  if (dragIndex > hoverIndex && hoverClientY < hoverMiddleY) {
    type = 'upward';
  }
  return type;
};


let BodyRow = (props) => {
  const {
    isOver,
    connectDragSource,
    connectDropTarget,
    moveRow,
    dragRow,
    clientOffset,
    sourceClientOffset,
    initialClientOffset,
    ...restProps
  } = props;
  const style = { ...restProps.style, cursor: 'move' };

  let className = restProps.className;
  if (isOver && initialClientOffset) {
    const direction = dragDirection(
      dragRow.index,
      restProps.index,
      initialClientOffset,
      clientOffset,
      sourceClientOffset,
    );
    if (direction === 'downward') {
      className += ' drop-over-downward';
    }
    if (direction === 'upward') {
      className += ' drop-over-upward';
    }
  }

  return connectDragSource(
    connectDropTarget(
      <tr
        {...restProps}
        className={className}
        style={style}
      />,
    ),
  );
};
// 开始拖
const rowSource = {
  beginDrag(props) {
    return {
      index: props.index,
    };
  },
};
// 拖结束
const rowTarget = {
  drop(props, monitor) {
    const dragIndex = monitor.getItem().index;
    const hoverIndex = props.index;

    // Don't replace items with themselves
    if (dragIndex === hoverIndex) {
      return;
    }
    props.moveRow(dragIndex, hoverIndex);
    monitor.getItem().index = hoverIndex;
  },
};

BodyRow = DropTarget('row', rowTarget, (connect, monitor) => ({
  connectDropTarget: connect.dropTarget(),
  isOver: monitor.isOver(),
  sourceClientOffset: monitor.getSourceClientOffset(),
  className: 'drop',
}))(
  DragSource('row', rowSource, (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    dragRow: monitor.getItem(),
    clientOffset: monitor.getClientOffset(),
    initialClientOffset: monitor.getInitialClientOffset(),
    className: 'drab',
  }))(BodyRow),
);


@DragDropContext(HTML5Backend)
class DragSortingTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      data: props.dataSource,
      sourceData: props.dataSource,
    };
  }

  components = {
    body: {
      row: BodyRow,
    },
  };

  moveRow = (dragIndex, hoverIndex) => {
    const data = this.props.dataSource;
    const result = Array.from(data);
    const [removed] = result.splice(dragIndex, 1);
    result.splice(hoverIndex, 0, removed);
    const dragRow = data[dragIndex];
    let beforeSequence = null;
    let afterSequence = null;
    // 拖的方向
    if (hoverIndex === 0) {
      afterSequence = result[1].sequence;
    } else if (hoverIndex === data.length - 1) {
      beforeSequence = result[data.length - 2].sequence;
    } else {
      afterSequence = result[hoverIndex + 1].sequence;
      beforeSequence = result[hoverIndex - 1].sequence;
    }
    const versionId = data[dragIndex].versionId;
    const { objectVersionNumber } = data[dragIndex];
    const postData = {
      afterSequence, beforeSequence, versionId, objectVersionNumber, 
    };
    this.setState(
      update(this.state, {
        data: {
          $splice: [[dragIndex, 1], [hoverIndex, 0, dragRow]],
        },
      }),
    );
    this.props.handleDrag(result, postData);
  };

  render() {
    return (
      <Table
        rowClassName="table-row"
        columns={this.props.columns}
        dataSource={this.props.dataSource}
        pagination={this.props.pagination}
        // pagination={this.props.dataSource.length <= 10 ? false : this.props.pagination}
        onChange={this.props.onChange}
        filterBarPlaceholder="过滤表"
        components={this.components}
        onRow={(record, index) => ({
          index,
          moveRow: this.moveRow,
          onMouseEnter: (e) => {
            e.target.parentElement.classList.add('hover-row');
          },
        })}
        rowKey={record => record.versionId}
      />
    );
  }
}

export default DragSortingTable;
