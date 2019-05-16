import React, { Component } from 'react';
import { observer } from 'mobx-react';
import classnames from 'classnames';
import PICount from '../PICount';
import PIIssue from '../PIIssue';

@observer
class IssueItem extends Component {
  handleClick = (e) => {
    e.stopPropagation();
    const { onClick, item } = this.props;
    onClick(e, item);
  };

  render() {
    const {
      item, store,
    } = this.props;
    return (
      <div
        className={classnames('c7n-feature-sprintIssueItem', {
          'issue-selected': store.getMultiSelected.get(item.issueId),
        })}
        key={item.issueId}
        label="sprintIssue"
        onClick={this.handleClick}
        role="none"
      >
        <PICount
          key={`${item.issueId}-count`}
          issueId={item.issueId}
          store={store}
        />
        <PIIssue
          key={item.issueId}
          ref={((e) => {
            this.ref = e;
          })}
          item={item}
          epicVisible={null}
          versionVisible={null}
          issueDisplay={0}
        />
      </div>
    );
  }
}

export default IssueItem;
