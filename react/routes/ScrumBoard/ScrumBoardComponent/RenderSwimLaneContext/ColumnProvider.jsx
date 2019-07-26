/* eslint-disable camelcase */
import React from 'react';
import classnames from 'classnames';
import { inject, observer } from 'mobx-react';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';
import ColumnCouldDragOn from './ColumnCouldDragOn';
import CSSBlackMagic from '../../../../../components/CSSBlackMagic';

@CSSBlackMagic
@observer
export default class ColumnProvider extends React.Component {
  getColumn(columnObj) {
    const {
      children, column_status_RelationMap, className, keyId,
    } = this.props;
    const columnConstraintsIsOn = ScrumBoardStore.getAllColumnCount.size > 0;
    const subStatusArr = column_status_RelationMap.get(columnObj.columnId);
    return (
      <React.Fragment key={columnObj.columnId}>
        <ColumnCouldDragOn keyId={keyId} dragOn={ScrumBoardStore.getCurrentDrag === keyId} />
        <div
          className={classnames('c7n-swimlaneContext-itemBodyColumn', `${className} ${keyId}`, {
            greaterThanMax: columnConstraintsIsOn && columnObj.maxNum !== null && ScrumBoardStore.getAllColumnCount.get(columnObj.columnId) > columnObj.maxNum,
            lessThanMin: columnConstraintsIsOn && columnObj.minNum !== null && ScrumBoardStore.getAllColumnCount.get(columnObj.columnId) < columnObj.minNum,
          })}
        >
          {children(subStatusArr, columnObj.columnId)}
        </div>
      </React.Fragment>
    );
  }

  render() {
    const { columnStructure, column_status_RelationMap } = this.props;
    return columnStructure.filter(column => column_status_RelationMap.get(column.columnId).length > 0).map(column => this.getColumn(column));
  }
}
