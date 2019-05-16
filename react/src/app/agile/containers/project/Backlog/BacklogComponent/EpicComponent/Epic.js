import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Icon,
} from 'choerodon-ui';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
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

  componentWillMount() {
    this.epicRefresh();
  }

  epicRefresh = () => {
    Promise.all([BacklogStore.axiosGetEpic(), BacklogStore.axiosGetColorLookupValue()]).then(([epicList, lookupValues]) => {
      BacklogStore.initEpicList(epicList, lookupValues);
    });
  };

  /**
   *点击epicItem的事件
   *
   * @param {*} type
   * @memberof Epic
   */
  handleClickEpic =(type) => {
    BacklogStore.setChosenEpic(type);
    BacklogStore.axiosGetSprint().then((res) => {
      BacklogStore.setSprintData(res);
    }).catch(() => {
    });
  };

  render() {
    const { draggableIds, addEpic } = this.state;
    const { refresh, issueRefresh } = this.props;
    return BacklogStore.getCurrentVisible === 'epic' ? (
      <div className="c7n-backlog-epic">
        <div className="c7n-backlog-epicContent">
          <div className="c7n-backlog-epicTitle">
            <p style={{ fontWeight: 'bold' }}>史诗</p>
            <div className="c7n-backlog-epicRight">
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
                  BacklogStore.toggleVisible(null);
                }}
                style={{
                  cursor: 'pointer',
                  marginLeft: 6,
                }}
              />
            </div>
          </div>
          <div className="c7n-backlog-epicChoice">
            <div
              className="c7n-backlog-epicItems-first"
              style={{
                color: '#3F51B5',
                background: BacklogStore.getChosenEpic === 'all' ? 'rgba(140, 158, 255, 0.08)' : '',
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
                BacklogStore.moveEpic(sourceIndex, destinationIndex);
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
                      clickEpic={this.handleClickEpic}
                      draggableIds={draggableIds}
                      refresh={refresh}
                      issueRefresh={issueRefresh}
                    />
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </DragDropContext>
            <div
              className="c7n-backlog-epicItems-last"
              style={{
                background: BacklogStore.getChosenEpic === 'unset' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickEpic('unset');
              }}
              onMouseEnter={(e) => {
                if (BacklogStore.isDragging) {
                  BacklogStore.toggleIssueDrag(true);
                  e.currentTarget.style.border = '2px dashed green';
                }
              }}
              onMouseLeave={(e) => {
                if (BacklogStore.isDragging) {
                  BacklogStore.toggleIssueDrag(false);
                  e.currentTarget.style.border = 'none';
                }
              }}
              onMouseUp={(e) => {
                if (BacklogStore.getIsDragging) {
                  BacklogStore.toggleIssueDrag(false);
                  e.currentTarget.style.border = 'none';
                  BacklogStore.axiosUpdateIssuesToEpic(
                    0, BacklogStore.getIssueWithEpicOrVersion,
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
            store={BacklogStore}
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
