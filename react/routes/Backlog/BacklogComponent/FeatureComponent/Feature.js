import React, { Component } from 'react';
import { observer } from 'mobx-react';
import {
  Icon,
} from 'choerodon-ui';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../stores/project/backlog/BacklogStore';
import { QuickSearchEvent } from '../../../../components/QuickSearch';
import FeatureItem from './FeatureItem';
import { getFeaturesInProject } from '../../../../api/FeatureApi';
import CreateFeature from './CreateFeature';
import './Feature.scss';

@observer
class Feature extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addFeature: false,
    };
  }

  componentDidMount() {    
    this.featureRefresh();
  }

  featureRefresh = () => {
    getFeaturesInProject().then((data) => {
      BacklogStore.setFeatureData(data);
    }).catch((error3) => {
    });
  };

  /**
   *点击featureItem的事件
   *
   * @param {*} type
   * @memberof 
   */
  handleClickFeature = (type) => {
    BacklogStore.setChosenFeature(type);
    BacklogStore.axiosGetSprint().then((res) => {
      BacklogStore.setSprintData(res);
    }).catch(() => {
    });
  };

  render() {
    const { draggableIds, addFeature } = this.state;
    const { refresh, issueRefresh, isInProgram } = this.props;
    return BacklogStore.getCurrentVisible === 'feature' ? (
      <div className="c7n-backlog-epic">
        <div className="c7n-backlog-epicContent">
          <div className="c7n-backlog-epicTitle">
            <p style={{ fontWeight: 'bold' }}>特性</p>
            <div className="c7n-backlog-epicRight">
              {/* {!isInProgram && (
              <p
                style={{ color: '#3F51B5', cursor: 'pointer', whiteSpace: 'nowrap' }}
                role="none"
                onClick={() => {
                  this.setState({
                    addFeature: true,
                  });
                }}
              >
                创建特性
              </p>
              )} */}
              <Icon
                type="first_page"
                role="none"
                onClick={() => {
                  BacklogStore.toggleVisible(null);
                  QuickSearchEvent.emit('unSelectStory');
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
                background: BacklogStore.getChosenFeature === 'all' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickFeature('all');
              }}
            >
              所有问题
            </div>
            <DragDropContext
              onDragEnd={(result) => {
                const { destination, source } = result;
                if (!destination || !source) {
                  return;
                }
                const { index: destinationIndex } = destination;
                const { index: sourceIndex } = source;
                BacklogStore.moveFeature(sourceIndex, destinationIndex);
              }}
            >
              <Droppable droppableId="feature">
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    style={{
                      background: snapshot.isDraggingOver ? '#e9e9e9' : 'white',
                      padding: 'grid',
                    }}
                  >
                    <FeatureItem
                      clickFeature={this.handleClickFeature}
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
                background: BacklogStore.getChosenFeature === 'unset' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickFeature('unset');
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
                  BacklogStore.axiosUpdateIssuesToFeature(
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
              未指定特性的问题
            </div>
          </div>
          <CreateFeature
            store={BacklogStore}
            visible={addFeature}
            onCancel={() => {
              this.setState({
                addFeature: false,
              });
            }}
            refresh={this.featureRefresh}
          />
        </div>
      </div>
    ) : null;
  }
}

export default Feature;
