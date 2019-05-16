import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Icon } from 'choerodon-ui';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import EpicItem from './EpicItem';
import './Epic.scss';
import CreateEpic from './CreateEpic';

@observer
class Epic extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addEpic: false,
    };
  }

  componentDidMount() {
    this.epicRefresh();
  }

  epicRefresh = () => {
    const { store } = this.props;
    Promise.all([store.axiosGetEpic(), store.axiosGetColorLookupValue()]).then(([epicList, lookupValues]) => {
      store.initEpicList(epicList, lookupValues);
    });
  };

  refresh=() => {
    this.epicRefresh();
    const { issueRefresh } = this.props;
    issueRefresh();
  }

  /**
   *点击epicItem的事件
   *
   * @param {*} type
   * @memberof Epic
   */
  handleClickEpic =(type) => {
    const { store, onEpicClick } = this.props;
    store.setChosenEpic(type);
    onEpicClick();
  };

  render() {
    const {
      store, refresh, visible, issueRefresh,
    } = this.props;
    const { draggableIds, addEpic } = this.state;
    return visible ? (
      <div className="c7n-feature-epic">
        <div className="c7n-feature-epicContent">
          <div className="c7n-feature-epicTitle">
            <p style={{ fontWeight: 'bold' }}>史诗</p>
            <div className="c7n-feature-epicRight">
              <p
                style={{ color: '#3F51B5', cursor: 'pointer', whiteSpace: 'nowrap' }}
                role="none"
                onClick={() => {
                  this.setState({
                    addEpic: true,
                  });
                }}
              >
                创建史诗
              </p>
              <Icon
                type="first_page"
                role="none"
                onClick={() => {
                  store.toggleVisible(null);
                }}
                style={{
                  cursor: 'pointer',
                  marginLeft: 6,
                }}
              />
            </div>
          </div>
          <div className="c7n-feature-epicChoice">
            <div
              className="c7n-feature-epicItems-first"
              style={{
                color: '#3F51B5',
                background: store.getChosenEpic === 'all' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickEpic('all');
              }}
            >
              所有问题
            </div>
            <DragDropContext
              onDragEnd={(result) => {
                const { destination, source } = result;
                const { index: destinationIndex } = destination;
                const { index: sourceIndex } = source;
                store.moveEpic(sourceIndex, destinationIndex);
              }}
            >
              <Droppable droppableId="epic">
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    style={{
                      background: snapshot.isDraggingOver ? '#e9e9e9' : 'white',
                      padding: 'grid',
                    }}
                  >
                    <EpicItem
                      store={store}
                      clickEpic={this.handleClickEpic}
                      draggableIds={draggableIds}
                      refresh={refresh}
                      issueRefresh={this.refresh}
                    />
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </DragDropContext>
            <div
              className="c7n-feature-epicItems-last"
              style={{
                background: store.getChosenEpic === 'unset' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickEpic('unset');
              }}
              onMouseEnter={(e) => {
                if (store.isDragging) {
                  store.toggleIssueDrag(true);
                  e.currentTarget.style.border = '2px dashed green';
                }
              }}
              onMouseLeave={(e) => {
                if (store.isDragging) {
                  store.toggleIssueDrag(false);
                  e.currentTarget.style.border = 'none';
                }
              }}
              onMouseUp={(e) => {
                if (store.getIsDragging) {
                  store.toggleIssueDrag(false);
                  e.currentTarget.style.border = 'none';
                  store.moveIssuesToEpic(
                    0, store.getIssueWithEpic,
                  ).then(() => {
                    issueRefresh();
                    refresh();
                  }).catch(() => {
                    issueRefresh();
                    refresh();
                  });
                }
              }}
            >
              未指定史诗的问题
            </div>
          </div>
          <CreateEpic
            store={store}
            visible={addEpic}
            onCancel={() => {
              this.setState({
                addEpic: false,
              });
            }}
            refresh={this.epicRefresh}
          />
        </div>
      </div>
    ) : null;
  }
}

export default Epic;
