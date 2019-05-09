import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import {
  Modal, Form, Input, DatePicker, Icon,
} from 'choerodon-ui';
import { Content, stores, Permission } from 'choerodon-front-boot';
import { fromJS, is } from 'immutable';
import { DragDropContext, Droppable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../../stores/project/backlog/BacklogStore';
import VersionItem from './VersionItem';
import './Version.scss';
import AddRelease from '../../../Release/ReleaseComponent/AddRelease';
import EpicItem from '../EpicComponent/Epic';

const { AppState } = stores;

@observer
class Version extends Component {
  constructor(props) {
    super(props);
    this.state = {
      draggableIds: [],
      hoverBlockButton: false,
      addRelease: false,
    };
  }

  componentWillMount() {
    this.versionRefresh();
  }

  versionRefresh = () => {
    BacklogStore.axiosGetVersion().then((res) => {
      BacklogStore.setVersionData(res);
    });
  };

  /**
   *点击versionItem事件
   *
   * @param {*} type
   * @memberof Version
   */
  handleClickVersion(type) {
    BacklogStore.setChosenVersion(type);
    BacklogStore.axiosGetSprint().then((res) => {
      BacklogStore.setSprintData(res);
    }).catch((error) => {
    });
  }

  render() {
    const {
      issueRefresh,
      refresh,
    } = this.props;
    const { hoverBlockButton, draggableIds, addRelease } = this.state;
    const menu = AppState.currentMenuType;
    const { type, id: projectId, organizationId: orgId } = menu;
    return BacklogStore.getCurrentVisible === 'version' ? (
      <div className="c7n-backlog-version">
        <div className="c7n-backlog-versionContent">
          <div className="c7n-backlog-versionTitle">
            <p style={{ fontWeight: 'bold' }}>版本</p>
            <div className="c7n-backlog-versionRight">
              <Permission type={type} projectId={projectId} organizationId={orgId} service={['agile-service.product-version.createVersion']}>
                <p
                  style={{ color: '#3F51B5', cursor: 'pointer', whiteSpace: 'nowrap' }}
                  role="none"
                  onClick={() => {
                    this.setState({
                      addRelease: true,
                    });
                  }}
                >
                  {'创建版本'}
                </p>
              </Permission>
              <Icon
                type="first_page"
                role="none"
                style={{
                  cursor: 'pointer',
                  marginLeft: 6,
                }}
                onClick={() => {
                  BacklogStore.toggleVisible(null);
                  BacklogStore.setIsLeaveSprint(false);
                }}
              />
            </div>
          </div>
          <div className="c7n-backlog-versionChoice">
            <div
              className="c7n-backlog-versionItems"
              style={{
                color: '#3F51B5',
                background: BacklogStore.getChosenVersion === 'all' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickVersion('all');
              }}
            >
              {'所有问题'}
            </div>
            <DragDropContext
              onDragEnd={(result) => {
                const { destination, source, draggableId } = result;
                const { droppableId: destinationId, index: destinationIndex } = destination;
                const { droppableId: sourceId, index: sourceIndex } = source;
                BacklogStore.moveVersion(sourceIndex, destinationIndex);
              }}
            >
              <Droppable droppableId="version" type="VERSION">
                {(provided, snapshot) => (
                  <div
                    ref={provided.innerRef}
                    style={{
                      background: snapshot.isDraggingOver ? '#e9e9e9' : 'white',
                    }}
                  >
                    <VersionItem
                      handleClickVersion={this.handleClickVersion}
                      refresh={refresh}
                      issueRefresh={issueRefresh}
                    />
                    {provided.placeholder}
                  </div>
                )}
              </Droppable>
            </DragDropContext>
            <div
              className="c7n-backlog-versionItems"
              style={{
                background: BacklogStore.getChosenVersion === 'unset' ? 'rgba(140, 158, 255, 0.08)' : '',
              }}
              role="none"
              onClick={() => {
                this.handleClickVersion('unset');
              }}
              onMouseUp={() => {
                if (BacklogStore.getIsDragging) {
                  BacklogStore.axiosUpdateIssuesToVersion(
                    0, draggableIds,
                  ).then((res) => {
                    issueRefresh();
                    refresh();
                  }).catch((error) => {
                    refresh();
                  });
                }
              }}
            >
              {'未指定版本的问题'}
            </div>
          </div>
          <AddRelease
            store={BacklogStore}
            visible={addRelease}
            onCancel={() => {
              this.setState({
                addRelease: false,
              });
            }}
            refresh={this.versionRefresh}
          />
        </div>
      </div>
    ) : null;
  }
}

export default Form.create()(Version);
