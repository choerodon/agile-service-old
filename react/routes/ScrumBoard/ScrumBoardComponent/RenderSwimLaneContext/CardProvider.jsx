import React from 'react';
import { observer } from 'mobx-react';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import Card from './Card';

@observer
export default class CardProvider extends React.Component {
  render() {
    const {
      keyId, id, completed, statusName, categoryCode,
    } = this.props;
    return ScrumBoardStore.getSwimLaneData[keyId][id].map(
      (issueObj, index) => issueObj && (
        <Card
          key={issueObj.issueId}
          draggableId={`${keyId}/${issueObj.issueId}`}
          index={index}
          issue={issueObj}
          completed={completed}
          statusName={statusName}
          categoryCode={categoryCode}
        />
      ),
    );
  }
}
