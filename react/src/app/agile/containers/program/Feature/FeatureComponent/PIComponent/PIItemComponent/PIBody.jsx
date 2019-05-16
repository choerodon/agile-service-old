import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Droppable } from 'react-beautiful-dnd';
import IssueList from './IssueList';
import deBounce from '../Utils';
import { QuickCreateFeature } from '../../../../../../components/QuickCreateFeature';

const debounceCallback = deBounce(500);

@inject('AppState')
@observer class PIBody extends Component {
  render() {
    const {
      expand, versionVisible, epicVisible,
      issueCount, piId, emptyIssueComponent,
      defaultPriority, featureTypeDTO,
      store, refresh,
    } = this.props;
    return (
      <Droppable
        droppableId={piId}
        isDropDisabled={store.getIssueCantDrag}
      >
        {(provided, snapshot) => (
          <div
            ref={provided.innerRef}
            style={{
              display: expand ? 'block' : 'none',
              background: snapshot.isDraggingOver ? '#e9e9e9' : 'inherit',
              padding: 'grid',
              borderBottom: '1px solid rgba(0,0,0,0.12)',
            }}
          >
            {issueCount ? (
              <IssueList
                sprintItemRef={this.sprintItemRef}
                versionVisible={versionVisible}
                epicVisible={epicVisible}
                piId={piId}
                store={store}
              />
            ) : (emptyIssueComponent)
              }
            {provided.placeholder}
            <div style={{ padding: '10px 0px 10px 33px', background: 'white' }}>
              <QuickCreateFeature
                defaultPriority={defaultPriority}
                store={store}
                piId={piId * 1}     
                {
                  // eslint-disable-next-line no-restricted-globals
                  ...!isNaN(store.getChosenEpic) ? {
                    epicId: store.getChosenEpic,
                  } : {}
                }
                featureTypeDTO={featureTypeDTO}
                onCreate={refresh}
              />
            </div>
          </div>
        )}
      </Droppable>
    );
  }
}

export default PIBody;
