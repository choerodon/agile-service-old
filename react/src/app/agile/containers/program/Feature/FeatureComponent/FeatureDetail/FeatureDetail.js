import React, { Component } from 'react';
import { observer, inject } from 'mobx-react';
import EditIssue from './EditFeature';


@inject('AppState')
@observer
class FeatureDetail extends Component {
  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  /**
   * 刷新issue详情的数据
   */
  refreshIssueDetail() {
    if (this.editIssue) {
      this.editIssue.loadIssueDetail();
    }
  }

  render() {
    const { refresh, store } = this.props;
    const visible = Object.keys(store.getClickIssueDetail).length > 0;
    return visible
      && (
      <EditIssue
        store={store}
        onRef={(ref) => {
          this.editIssue = ref;
        }}
        issueId={store.getClickIssueId}
        onCancel={() => {
          store.setClickIssueDetail({});
          store.clearMultiSelected();
        }}
        onDeleteIssue={() => {
          store.setClickIssueDetail({});
          refresh();
        }}
        onUpdate={refresh}
      />
      );
  }
}

export default FeatureDetail;
