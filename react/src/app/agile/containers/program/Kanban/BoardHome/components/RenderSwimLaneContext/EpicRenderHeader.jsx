import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import { Collapse } from 'choerodon-ui';
import './RenderSwimLaneContext.scss';
import SwimLaneHeader from './SwimLaneHeader.jsx';

const { Panel } = Collapse;

@inject('AppState')
@observer
class EpicRenderHeader extends Component {
  constructor(props) {
    super(props);
    this.state = {
      activeKey: this.getDefaultExpanded([...props.parentIssueArr.values(), props.otherIssueWithoutParent]),
    };
  }

  getPanelKey = (key) => {
    if (key === 'other') {
      return 'swimlane_epic-other';
    } else {
      return `swimlane_epic-${key}`;
    }
  };

  getDefaultExpanded = issueArr => [...issueArr.map(issue => `swimlane_epic-${issue.epicId}`), 'swimlane_epic-other'];

  getPanelItem = (key, parentIssue) => {
    const { children, mode } = this.props;
    return (
      <Panel
        key={this.getPanelKey(key)}
        className="c7n-swimlaneContext-container"
        header={(
          <SwimLaneHeader
            parentIssue={parentIssue}
            mode={mode}
            keyId={key}
            subIssueDataLength={parentIssue.issueArrLength}
          />
        )}
      >
        {children(key === 'other' ? parentIssue : parentIssue.subIssueData, key === 'other' ? 'swimlane_epic-unInterconnected' : `swimlane_epic-${parentIssue.epicId}`)}
      </Panel>
    );
  };

  panelOnChange = (arr) => {
    this.setState({
      activeKey: arr,
    });
  };

  render() {
    const { parentIssueArr, otherIssueWithoutParent } = this.props;
    const { activeKey } = this.state;
    return (
      <Collapse
        activeKey={activeKey}
        onChange={this.panelOnChange}
        forceRender
        bordered={false}
      >
        {Array.from(parentIssueArr).map(([key, value]) => this.getPanelItem(key, value))}
        {this.getPanelItem('other', otherIssueWithoutParent)}
      </Collapse>
    );
  }
}

export default EpicRenderHeader;
