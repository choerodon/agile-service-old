import React, { Component } from 'react';
import { observer } from 'mobx-react';
import { Draggable } from 'react-beautiful-dnd';
import BacklogStore from '../../../../stores/project/backlog/BacklogStore';
import DraggableVersion from './DraggableVersion';

@observer
class VersionItem extends Component {
  handleClickVersion = (type) => {
    const { handleClickVersion } = this.props;
    handleClickVersion(type);
  };

  render() {
    const {
      issueRefresh, refresh, 
    } = this.props;

    return BacklogStore.getVersionData.map((item, index) => (
      <div
        role="none"
        onMouseEnter={(e) => {
          if (BacklogStore.getIsDragging) {
            BacklogStore.toggleIssueDrag(true);
            e.currentTarget.style.border = '2px dashed green';
          }
        }}
        onMouseLeave={(e) => {
          if (BacklogStore.getIsDragging) {
            BacklogStore.toggleIssueDrag(false);
            e.currentTarget.style.border = 'none';
          }
        }}
        onMouseUp={(e) => {
          BacklogStore.toggleIssueDrag(false);
          if (BacklogStore.getIsDragging) {
            e.currentTarget.style.border = 'none';
            BacklogStore.axiosUpdateIssuesToVersion(
              item.versionId, BacklogStore.getIssueWithEpicOrVersion,
            ).then((res) => {
              issueRefresh();
              refresh();
            }).catch((error) => {
              issueRefresh();
              refresh();
            });
          }
        }}
      >
        <Draggable draggableId={`epicItem-${item.versionId}`} key={item.versionId} index={index}>
          {(draggableProvided, draggableSnapshot) => (
            <DraggableVersion
              item={item}
              refresh={refresh}
              draggableProvided={draggableProvided}
              draggableSnapshot={draggableSnapshot}
              handleClickVersion={this.handleClickVersion}
            />
          )}
        </Draggable>
      </div>
    ));
  }
}

export default VersionItem;
