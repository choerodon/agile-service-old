import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import { Spin, Select, Modal } from 'choerodon-ui';
import {
  Header, Page,
} from 'choerodon-front-boot';
import { DragDropContext } from 'react-beautiful-dnd';
import { loadStatusList } from '../../../../../api/NewIssueApi';
import FeatureStore from '../../../../../stores/program/Feature/FeatureStore';
import Epic from '../EpicComponent/Epic';
import SprintItem from '../PIComponent/PIItem';
// import './FeatureList.scss';
const { confirm } = Modal;
const { Option } = Select;
@observer
class PlanMode extends Component {
  componentDidMount() {
    this.refresh();
  }

  componentWillUnmount() {
    FeatureStore.setEpicVisible(false);
    FeatureStore.setClickIssueDetail({});
    FeatureStore.clearMultiSelected();
  }

  refresh = () => {
    this.Epic.epicRefresh();
    Promise.all([
      FeatureStore.axiosGetIssueTypes(),
      FeatureStore.axiosGetDefaultPriority(),
      FeatureStore.getCurrentEpicList(),
      FeatureStore.getFeatureListData(),
      loadStatusList('program'),
    ]).then(([issueTypes, defaultPriority, epics, featureList, statusList]) => {
      FeatureStore.initData(issueTypes, defaultPriority, epics, featureList, statusList);
    });
  };

  onEpicClick = () => {
    FeatureStore.getFeatureListData().then((res) => {
      FeatureStore.setFeatureData(res);
    }).catch(() => {
    });
  };

  handleMove = (statusType, statusList, ...otherArgs) => {
    const { issueRefresh } = this.props;
    let statusId = statusList[0] && statusList[0].id;
    if (statusList.length === 1) {
      FeatureStore.moveSingleIssue(...otherArgs, statusId, statusType).then(() => {
        if (issueRefresh) {
          issueRefresh();
        }
      });
    } else {
      const options = statusList.map(status => <Option value={status.id}>{status.name}</Option>);
      const content = (
        <Select
          style={{ width: '100%' }}
          defaultValue={statusId}
          onChange={(value) => {
            statusId = value;
          }}
        >
          {options}
        </Select>
      );
      confirm({
        title: '将特性状态置为',
        content,
        onOk: () => {
          FeatureStore.moveSingleIssue(...otherArgs, statusId, statusType).then(() => {
            if (issueRefresh) {
              issueRefresh();
            }
          });
        },
      });
    }
  };


  handleDragEnd = (result) => {
    const { issueRefresh } = this.props;
    FeatureStore.setIsDragging(null);
    const { destination, source, draggableId } = result;
    if (destination) {
      const { droppableId: destinationId, index: destinationIndex } = destination;
      const { droppableId: sourceId, index: sourceIndex } = source;
      if (destinationId === sourceId && destinationIndex === sourceIndex) {
        return;
      }
      if (result.reason !== 'CANCEL') {
        const item = FeatureStore.getIssueMap.get(sourceId)[sourceIndex];
        const destinationArr = FeatureStore.getIssueMap.get(destinationId);
        let destinationItem;
        if (destinationIndex === 0) {
          destinationItem = null;
        } else if (destinationIndex === FeatureStore.getIssueMap.get(destinationId).length) {
          destinationItem = destinationArr[destinationIndex - 1];
        } else {
          destinationItem = destinationArr[destinationIndex];
        }
        const type = FeatureStore.getMultiSelected.size > 1 && !FeatureStore.getMultiSelected.has(destinationItem) ? 'multi' : 'single';
        // 相同pi不做状态转换判断
        if (sourceId === destinationId) {
          FeatureStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type).then(() => {
            if (issueRefresh) {
              issueRefresh();
            }
          });
        } else {
          // 判断目标pi和当前pi的状态
          const sourcePI = FeatureStore.getPIById(sourceId);
          const destinationPI = FeatureStore.getPIById(destinationId);
          const isSourceDoing = sourcePI && sourcePI.statusCode === 'doing';
          const isDestinationDoing = destinationPI && destinationPI.statusCode === 'doing';
          // 判断拖动的用例状态
          const moveFeatures = FeatureStore.getMoveFeatures(item, type);
          const moveFeaturesStatus = moveFeatures.map(feature => feature.statusMapDTO.type);

          // 从活跃pi拖动到非活跃pi
          if (isSourceDoing && !isDestinationDoing) {
            const prepareStatusList = FeatureStore.getPrepareStatusList;
            this.handleMove('prepare', prepareStatusList, destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type);
          } else if (!isSourceDoing && isDestinationDoing) {
            // 从非活跃pi拖动到活跃pi
            // 如果有prepare，那么就要改状态       
            if (moveFeaturesStatus.includes('prepare')) {
              const todoStatusList = FeatureStore.getTodoStatusList;
              this.handleMove('todo', todoStatusList, destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type);
            } else {
              FeatureStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type).then(() => {
                if (issueRefresh) {
                  issueRefresh();
                }
              });
            }
          } else if (!isSourceDoing && !isDestinationDoing) {
            // 从pi拖到准备
            if (sourcePI && !destinationPI) {
              // 如果有非准备的
              if (moveFeaturesStatus.some(code => code !== 'prepare')) {
                const prepareStatusList = FeatureStore.getPrepareStatusList;
                this.handleMove('prepare', prepareStatusList, destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type);
              } else {
                FeatureStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type).then(() => {
                  if (issueRefresh) {
                    issueRefresh();
                  }
                });
              }
            } else {
              FeatureStore.moveSingleIssue(destinationId, destinationIndex, sourceId, sourceIndex, draggableId, item, type).then(() => {
                if (issueRefresh) {
                  issueRefresh();
                }
              });
            }
          }          
        }
      }
    }
  }

  render() {
    const { issueRefresh, display } = this.props;
    return (
      <Fragment>
        <div className="c7n-feature-side">
          <p
            style={{
              marginTop: 12,
            }}
            role="none"
            onClick={() => {
              FeatureStore.toggleVisible();
            }}
          >
            {'史诗'}
          </p>
        </div>
        <Epic
          ref={(epic) => { this.Epic = epic; }}
          refresh={this.refresh}
          visible={FeatureStore.getEpicVisible}
          store={FeatureStore}
          issueRefresh={issueRefresh}
          onEpicClick={this.onEpicClick}
        />
        <Spin spinning={FeatureStore.getSpinIf}>
          <div className="c7n-feature-content">
            <DragDropContext
              onDragEnd={this.handleDragEnd}
              onDragStart={(result) => {
                const { source } = result;
                const { droppableId: sourceId, index: sourceIndex } = source;
                const item = FeatureStore.getIssueMap.get(sourceId)[sourceIndex];
                FeatureStore.setIsDragging(item.issueId);
                FeatureStore.setIssueWithEpic(item);
              }}
            >
              <SprintItem
                display={display}
                epicVisible={FeatureStore.getEpicVisible}
                onRef={(ref) => {
                  this.sprintItemRef = ref;
                }}
                refresh={this.refresh}
                store={FeatureStore}
                type="pi"
              />
            </DragDropContext>
          </div>
        </Spin>
      </Fragment>
    );
  }
}

export default PlanMode;
