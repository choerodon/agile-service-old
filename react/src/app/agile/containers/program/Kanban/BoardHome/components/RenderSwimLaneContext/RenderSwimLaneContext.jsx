import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import classnames from 'classnames';
import { Collapse } from 'choerodon-ui';
import './RenderSwimLaneContext.scss';
import SwimLaneHeader from './SwimLaneHeader.jsx';

const { Panel } = Collapse;

@inject('AppState')
@observer
class SwimLaneContext extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: this.getDefaultExpanded(props.mode, [...props.parentIssueArr.values(), props.otherIssueWithoutParent]),
    };
  }

  componentWillReceiveProps(nextProps) {
    this.setState({
      activeKey: this.getDefaultExpanded(nextProps.mode, [...nextProps.parentIssueArr.values(), nextProps.otherIssueWithoutParent]),
    });
  }

  getPanelKey = (mode, issue) => {
    const modeMap = new Map([
      ['swimlane_none', 'swimlaneContext-all'],
      ['assignee', `swimlaneContext-${issue.assigneeId || issue.type}`],
      ['feature', `swimlaneContext-${issue.featureType}`],
      ['swimlane_epic', `swimlaneContext-${issue.epicId || issue.type}`],
      ['parent_child', `swimlaneContext-${issue.issueId || issue.type || 'other'}`],
    ]);
    return modeMap.get(mode);
  };

  getDefaultExpanded = (mode, issueArr, key) => {
    let retArr = issueArr;
    if (mode === 'parent_child') {
      retArr = retArr.filter(issue => !issue.isComplish || key === 'other');
    }
    return retArr.map(issue => this.getPanelKey(mode, issue));
  };

  getPanelItem = (key, parentIssue = null) => {
    const {
      children, mode, fromEpic, parentIssueArr,
    } = this.props;
    return (
      <Panel
        showArrow={mode !== 'swimlane_none'}
        key={this.getPanelKey(mode, parentIssue, key)}
        className={classnames('c7n-swimlaneContext-container', {
          shouldBeIndent: fromEpic,
          noStoryInEpic: fromEpic && Array.from(parentIssueArr).length === 0,
        })}
        header={(
          <SwimLaneHeader
            parentIssue={parentIssue}
            mode={mode}
            keyId={key}
            subIssueDataLength={parentIssue instanceof Array ? parentIssue.length : parentIssue.subIssueData.length}
          />
        )}
      >
        {children(this.keyConverter(key, mode))}
      </Panel>
    );
  };

  panelOnChange = (arr) => {
    this.setState({
      activeKey: arr,
    });
  };

  keyConverter = (key, mode) => {
    const { epicPrefix } = this.props;
    const retMap = new Map([
      ['parent_child', `parent_child-${key}`],
      ['assignee', `assignee-${key}`],
      ['feature', `feature-${key}`],
      ['swimlane_none', 'swimlane_none-other'],
    ]);
    if (epicPrefix) {
      return `${epicPrefix}-${key}`;
    }
    return retMap.get(mode);
  };

  render() {
    const { parentIssueArr, otherIssueWithoutParent } = this.props;
    const { activeKey } = this.state;
    return (
      <Collapse
        activeKey={activeKey}
        onChange={this.panelOnChange}
        bordered={false}
        forceRender
      >
        {Array.from(parentIssueArr).map(([key, value]) => this.getPanelItem(key, value))}
        {otherIssueWithoutParent.length && this.getPanelItem('other', otherIssueWithoutParent, 'fromOther')}
      </Collapse>
    );
  }
}

export default SwimLaneContext;
