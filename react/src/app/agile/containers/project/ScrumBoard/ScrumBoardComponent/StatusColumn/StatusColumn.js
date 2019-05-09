/**
 * 列状态
 */
import React, { Component } from 'react';
import './StatusColumn.scss';
import classnames from 'classnames';
import { observer } from 'mobx-react';
import ScrumBoardStore from '../../../../../stores/project/scrumBoard/ScrumBoardStore';

@observer
class StatusColumn extends Component {
  render() {
    const columnConstraintsIsOn = ScrumBoardStore.getAllColumnCount.size > 0;
    return [...ScrumBoardStore.getHeaderData.values()].filter(column => column.hasStatus).map(column => (
      <div
        className={classnames('c7n-scrumboard-statusHeader', {
          greaterThanMax: ScrumBoardStore.didCurrentSprintExist && columnConstraintsIsOn && column.maxNum !== null && ScrumBoardStore.getAllColumnCount.get(column.columnId) > column.maxNum,
          lessThanMin: ScrumBoardStore.didCurrentSprintExist && columnConstraintsIsOn && column.minNum !== null && ScrumBoardStore.getAllColumnCount.get(column.columnId) < column.minNum,
        })}
        key={column.columnId}
      >
        <div className={classnames('c7n-scrumboard-statusHeader-columnMsg', {
          alignToCenter: !columnConstraintsIsOn || (column.minNum === null && column.maxNum === null),
        })}
        >
          <p className="c7n-scrumboard-statusHeader-columnMsg-name">
            {column.columnName}
          </p>
          <p className="c7n-scrumboard-statusHeader-columnMsg-count">
            {`(${column.columnIssueCount})`}
          </p>
        </div>
        {
          columnConstraintsIsOn ? (
            <div className="c7n-scrumboard-statusHeader-columnConstraint">
              {column.minNum !== null ? (
                <p className="c7n-scrumboard-statusHeader-columnConstraint-min display-in-oneline">
                  {`最小：${column.minNum}`}
                </p>
              ) : null}
              {column.maxNum !== null ? (
                <p className="c7n-scrumboard-statusHeader-columnConstraint-max display-in-oneline">
                  {`最大：${column.maxNum}`}
                </p>
              ) : null}
            </div>
          ) : null
        }
      </div>
    ));
  }
}

export default StatusColumn;
