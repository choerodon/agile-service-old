import { Table } from 'choerodon-ui';
import React, { Component } from 'react';
import { DragDropContext, DragSource, DropTarget } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import './SortTable.scss';


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

  let { className } = restProps;
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
    props.moveRow(dragIndex, hoverIndex, monitor);
    const item = monitor.getItem();
    item.index = hoverIndex;
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
class SortTable extends Component {
  components = {
    body: {
      row: BodyRow,
    },
  };

  constructor(props) {
    super(props);
    this.state = {
      data: props.dataSource,
    };
  }

  componentWillReceiveProps(nextProps) {
    const { dataSource } = this.props;
    const { data } = this.state;
    if (JSON.stringify(data) !== JSON.stringify(dataSource)) {
      this.setState({ data: dataSource });
    }
  }


  moveRow = (dragIndex, hoverIndex, e) => {
    const { dataSource, handleDrag } = this.props;

    // 调整顺序
    const result = Array.from(dataSource);
    const [removed] = result.splice(dragIndex, 1);
    result.splice(hoverIndex, 0, removed);

    const currentFieldId = dataSource[dragIndex].fieldId;
    let outsetFieldId = null;
    let before = false;
    if (hoverIndex === 0) {
      before = true;
      outsetFieldId = result[1].fieldId;
    } else {
      outsetFieldId = result[hoverIndex - 1].fieldId;
    }
    const postData = {
      before,
      currentFieldId,
      outsetFieldId,
    };

    handleDrag(result, postData);
  };

  render() {
    const {
      columns, dataSource = [],
      pagination = false, filterBar = false,
    } = this.props;

    return (
      <Table
        rowClassName="table-row"
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        components={this.components}
        filterBar={filterBar}
        filterBarPlaceholder="过滤表"
        onRow={(record, index) => ({
          index,
          moveRow: this.moveRow,
          onMouseEnter: (e) => {
            e.target.parentElement.classList.add('hover-row');
          },
        })}
        rowKey={record => record.id}
      />
    );
  }
}

export default SortTable;
