import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import { Card, Icon } from 'choerodon-ui';
import { injectIntl, FormattedMessage } from 'react-intl';
import Tips from '../Tips';
import './TransferDrag.scss';
import drag from '../../assets/image/drag.png';
import TypeTag from '../TypeTag/TypeTag';

class TransferDrag extends Component {
  constructor(props) {
    super(props);
    this.state = {
      validator: true,
    };
  }

  componentWillReceiveProps(nextProps) {
    const { validator = true } = this.props;
    if (nextProps.validator !== validator) {
      this.setState({
        validator: nextProps.validator,
      });
    }
  }

  /**
   * 拖动完成时触发
   * @param result
   */
  onDragEnd = (result) => {
    const { origin, target, onDragChange } = this.props;
    const { source, destination } = result;
    // 拖拽到边框外
    if (!destination) {
      return;
    }

    if (source.droppableId === destination.droppableId) {
      // 在同一个框内拖动，排序
      const items = this.reorder(
        source.droppableId === 'left' ? target : origin,
        source.index,
        destination.index,
      );
      if (source.droppableId === 'left') {
        if (onDragChange) {
          onDragChange(items, origin);
        }
      } else if (onDragChange) {
        onDragChange(target, items);
      }
    } else {
      // 同不框内拖动，移动
      const res = this.move(
        source.droppableId === 'left' ? target : origin,
        source.droppableId === 'left' ? origin : target,
        source,
        destination,
      );
      if (res.left.length) {
        this.setState({
          validator: true,
        });
      }
      if (onDragChange) {
        onDragChange(res.left, res.right);
      }
    }
  };

  // 开始拖动回调
  onDragStart = (data) => {
    window.console.log(data);
  };

  // 获取元素样式，根据是否拖动变化
  getItemStyle = (isDragging, draggableStyle) => ({
    userSelect: 'none',
    padding: '5px',
    margin: '0 0 5px 0',
    background: isDragging ? '#DDE7F2' : '#F7F7F7',
    height: 30,
    ...draggableStyle,
  });

  /**
   * 排序
   * @param list
   * @param startIndex
   * @param endIndex
   * @returns {Array}
   */
  reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);
    return result;
  };

  /**
   * 移动
   * @param source
   * @param destination
   * @param droppableSource
   * @param droppableDestination
   * @returns {{}}
   */
  move = (source, destination, droppableSource, droppableDestination) => {
    const sourceClone = Array.from(source);
    const destClone = Array.from(destination);
    const [removed] = sourceClone.splice(droppableSource.index, 1);
    destClone.splice(droppableDestination.index, 0, removed);
    const result = {};
    result[droppableSource.droppableId] = sourceClone;
    result[droppableDestination.droppableId] = destClone;
    return result;
  };

  render() {
    const {
      origin,
      target,
      textField,
      renderChildren,
      originTitle = 'issueTypeScheme.target',
      targetTitle = 'issueTypeScheme.origin',
      errorMessage = '',
      intl,
    } = this.props;
    const { validator } = this.state;
    const tips = intl.formatMessage({ id: 'issueTypeScheme.label.tips' });

    return (
      <div>
        <div className="issue-issueTypeDrag-des">
          <Tips tips={[tips]} />
        </div>
        <DragDropContext onDragEnd={this.onDragEnd} onDragStart={data => this.onDragStart(data)}>
          <div className={`issue-issueTypeDrag-content ${validator !== true ? 'issue-transferDrag-error' : ''}`}>
            <Card
              title={<FormattedMessage id={originTitle} />}
              bordered={false}
              className="issue-issueTypeDrag-card"
            >
              <Droppable droppableId="left">
                {(provided, snapshot) => (
                  <div
                    className="issue-issueTypeDrag-drop"
                    ref={provided.innerRef}
                  >
                    {target && target.map((item, index) => (
                      <Draggable
                        key={item.id}
                        draggableId={item.id}
                        index={index}
                      >
                        {(subProvided, subSnapshot) => (
                          <div
                            ref={subProvided.innerRef}
                            {...subProvided.draggableProps}
                            {...subProvided.dragHandleProps}
                            style={this.getItemStyle(
                              snapshot.isDragging,
                              subProvided.draggableProps.style,
                            )}
                          >
                            {renderChildren ? renderChildren(item) : <React.Fragment>
                              <img src={drag} className="issue-issueTypeDrag-drag" alt="" />
                               {
                                item.icon && (
                                  <TypeTag
                                    data={item}
                                    showName
                                  />
                                )
                              }
                            </React.Fragment>
                            }
                          </div>
                        )}
                      </Draggable>
                    ))}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </Card>
            <Card
              title={<FormattedMessage id={targetTitle} />}
              bordered={false}
              className="issue-issueTypeDrag-card"
            >
              <Droppable droppableId="right">
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    className="issue-issueTypeDrag-drop"
                  >
                    {origin && origin.map((item, index) => (
                      <Draggable
                        key={item.id}
                        draggableId={item.id}
                        index={index}
                      >
                        {(subProvided, subSnapshot) => (
                          <div
                            ref={subProvided.innerRef}
                            {...subProvided.draggableProps}
                            {...subProvided.dragHandleProps}
                            style={this.getItemStyle(
                              subSnapshot.isDragging,
                              subProvided.draggableProps.style,
                            )}
                          >
                            {renderChildren ? renderChildren(item) : <React.Fragment>
                              <img src={drag} className="issue-issueTypeDrag-drag" alt="" />
                              {
                                item.icon && (
                                  <TypeTag
                                    data={item}
                                    showName
                                  />
                                )
                              }
                            </React.Fragment>
                            }

                          </div>
                        )}
                      </Draggable>
                    ))}
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </Card>
          </div>
        </DragDropContext>
        {!validator && <p className="issue-transferDrag-error-tip"><FormattedMessage id={errorMessage || 'required'} /></p>}
      </div>
    );
  }
}

TransferDrag.propTypes = {
  validator: PropTypes.bool,
};

TransferDrag.defaultProps = {
  validator: true,
};

export default injectIntl(TransferDrag);
